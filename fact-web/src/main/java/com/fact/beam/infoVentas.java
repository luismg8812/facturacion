package com.fact.beam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.api.Calculos;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;
import com.fact.model.ProductoEmpresa;
import com.fact.model.TipoDocumento;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.ProductoEmpresaService;
import com.fact.service.ProductoService;
import com.fact.service.UsuarioService;
import com.fact.vo.ReduccionVo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean
@SessionScoped
public class infoVentas implements Serializable {

	/**
	 * luis Miguel gonzalez
	 */
	private static Logger log = Logger.getLogger(ExportarDelta.class);
	private static final long serialVersionUID = -6253474504097142406L;

	@EJB
	private DocumentoDetalleService documentoDetalleService;
	@EJB
	private ProductoService productoService;
	@EJB
	private DocumentoService documentoService;
	@EJB
	private UsuarioService usuarioService;
	@EJB
	private ProductoEmpresaService productoEmpresaService;

	private Date fechaInicio;
	private Date fechafin;
	private Double totalVentas = 0.0;

	private List<ReduccionVo> documentos;

	// cardex
	private Date fechaIni;
	private Date fechaFin;
	private Long productoId;
	List<ProductoEmpresa> productoList;
	List<DocumentoDetalle> cardexList;
	private ProductoEmpresa actual;
	private Double totalSalidas;
	private Double totalEntradas;

	// movimiento documento
	private Date fechaIniMovimiento;
	private Date fechaFinMovimiento;
	private Long tipoDocumento;
	private List<DocumentoDetalle> documentosMovimiento;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}

	public void buscarMovimiento() {
		setDocumentosMovimiento(documentoDetalleService.getByTipoAndFecha(getTipoDocumento(),
				Calculos.fechaInicial(getFechaIniMovimiento()), Calculos.fechaInicial(getFechaFinMovimiento())));
	}

	public void buscarCardex() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (getProductoId() == 0) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, EL producto es Obligatorio", ""));
			return;
		}
		if (getFechaIni() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria", ""));
			return;
		}
		if (getFechaFin() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de fin es obligatoria", ""));
			return;
		}
		ProductoEmpresa productoEmpresa = productoEmpresaService.getById(getProductoId());
		setCardexList(documentoDetalleService.getCardex(getEmpresa(), productoEmpresa.getProductoId().getProductoId(),
				Calculos.fechaInicial(getFechaIni()), Calculos.fechaFinal(getFechaFin())));
		totalEntradas = 0.0;
		totalSalidas = 0.0;
		for (DocumentoDetalle d : getCardexList()) {

			if (d.getCantidad() != null && d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId() == 2) {
				totalEntradas += d.getCantidad();
			}
			if (d.getCantidad() != null && d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId() == 10
					|| d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId() == 6) {
				totalSalidas += d.getCantidad();
			}
		}
		setTotalEntradas(totalEntradas);
		setTotalSalidas(totalSalidas);
		setActual(productoEmpresa);
	}

	private boolean validarfiltros() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valido = true;
		if (getFechaInicio() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria", ""));
			valido = false;
		}
		if (getFechafin() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria", ""));
			valido = false;
		}

		if (getFechafin() != null && getFechaInicio() != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			if (hoyfin < hoy) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error!, Fecha fin es mayor a la fecha de inicio", ""));
				valido = false;
			}
		}
		// hacer la parte de que busca usaurio por login
		return valido;
	}

	public void buscar() throws ParseException {
		if (validarfiltros()) {
			setDocumentos(null);
			Double total = 0.0;
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			for (Long i = hoy; i <= hoyfin; i++) {
				List<Usuario> usu = usuarioService.getByRol(2l); // se bucan cajeros(rol=2)
				for (Usuario u : usu) {
					System.out.println(df.parse(i.toString()));
					Double cantidadOriginal = getTotalFaturasToDay(df.parse(i.toString()), u);
					ReduccionVo rvo = new ReduccionVo();
					rvo.setCantidadOriginal(cantidadOriginal);
					rvo.setUsuarioId(u);
					rvo.setFecha(df.parse(i.toString()));
					getDocumentos().add(rvo);
				}
			}
			for (ReduccionVo r : getDocumentos()) {
				total = r.getCantidadOriginal() + total;
			}
			setTotalVentas(total);
		}
	}

	public Double getTotalFaturasToDay(Date dia, Usuario usuario) throws ParseException {
		Long tipoDocumentoId = 10l; // tipo documento factura de salida
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		Boolean conCierre = Boolean.FALSE;
		List<Usuario> usus = new ArrayList<>();
		usus.add(usuario);
		List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin, usus, conCierre);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
	}

	public StreamedContent getFileListadoArticulos() throws FileNotFoundException, DocumentException {
		StreamedContent file = null;
		String ruta = listadoDocumentos();
		File f = new File(ruta);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", ruta);
		}
		return file;
	}

	public StreamedContent getFileResumenMovimientoArticulos() throws FileNotFoundException, DocumentException {
		if(getFechafin()==null) {
			setFechafin(new Date());
			setFechaInicio(new Date());
		}
		
		StreamedContent file = null;
		String ruta = resumenMovimiento();
		File f = new File(ruta);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", ruta);
		}
		return file;
	}

	public String resumenMovimiento() throws FileNotFoundException, DocumentException {
		log.info("entra a exportar resumin movimiento");
		String carpeta = "C:\\facturas\\resumenMovimientos\\";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(new Date());
		String pdf = "resumenMovimiento_" + fhoyIni + ".pdf";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		FileOutputStream archivo = new FileOutputStream(carpeta + pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		
		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		PdfWriter.getInstance(documento, archivo);
		documento.open();
		documento.setMargins(10, 1, 1, 1);
		
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + getEmpresa().getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "NIT " + getEmpresa().getNit(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "RESUMEN DE MOVIMIENTO DE ARTICULOS CON CANTIDAD ACTUAL Y COSTO KARDEX" , FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Fecha inicial: " +df2.format( getFechaInicio()), FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Fecha final: " + df2.format(getFechafin()), FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " " , FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
		
		List<ProductoEmpresa> productos = productoEmpresaService.getByEmpresa( getEmpresa().getEmpresaId());
		double costoTotalAcum=0.0;
		double titalIvaAcum=0.0;
		for(ProductoEmpresa pe: productos) {
			if(pe.getCantidad()>0) {
				double iva=pe.getProductoId().getIva()/100;
				double costou=(pe.getProductoId().getCosto() / (1 + iva));			 
				double costot=pe.getCantidad()*costou;
				double totaliva = ((pe.getProductoId().getCosto()*pe.getCantidad() / (1 + iva)) * iva);
				costoTotalAcum+=costot;
				titalIvaAcum+=totaliva;
			}
			
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Inventario Inicial: ", 40) + Calculos.cortarCantidades(formatea.format(costoTotalAcum),16 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Iva Inventario Inicial: ", 40) + Calculos.cortarCantidades(formatea.format(titalIvaAcum),16 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		
		double totalAcum= 0.0;
		double totalIva=0.0;
		List<Long> tipo = new ArrayList<>();
		tipo.add(2l);
		List<Documento> list= documentoService.getByFacturaSinCierre(tipo, getFechaInicio(), getFechafin());
		for(Documento d: list) {
			double iva19= d.getBase19()==null?0.0:d.getBase19();
			double iva5= d.getBase5()==null?0.0:d.getBase5();
			double exento= d.getExcento()==null?0.0:d.getExcento();
			totalAcum+=iva19+iva5+exento;
			totalIva+=d.getIva()==null?0.0:d.getIva();
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Compras: ", 40) + Calculos.cortarCantidades(formatea.format(totalAcum),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Iva Compras: ", 40) + Calculos.cortarCantidades(formatea.format(totalIva),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		double totalAcumventas=0.0;
		totalIva=0;
		List<Long> tipo2 = new ArrayList<>();
		tipo2.add(10l);
		List<Documento> list2 = documentoService.getByFacturaSinCierre(tipo2, getFechaInicio(), getFechafin());
		for(Documento d: list2) {
			double iva19= (d.getBase19()==null?0.0:d.getBase19());
			double iva5= (d.getBase5()==null?0.0:d.getBase5());
			double exento= (d.getExcento()==null?0.0:d.getExcento());
			totalAcumventas=totalAcumventas+iva19+iva5+exento;
			totalIva=totalIva+(d.getIva()==null?0.0:d.getIva());
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Ventas: ", 40) + Calculos.cortarCantidades(formatea.format(totalAcumventas),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Iva Ventas: ", 40) + Calculos.cortarCantidades(formatea.format(totalIva),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Entradas: ", 40) + Calculos.cortarCantidades(formatea.format(0),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Kardex Entradas: ", 40) + Calculos.cortarCantidades(formatea.format(0),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		
		totalAcum=0.0;
		totalIva=0;
		List<Long> tipo3 = new ArrayList<>();
		tipo3.add(6l);
		List<Documento> list3= documentoService.getByFacturaSinCierre(tipo, getFechaInicio(), getFechafin());
		for(Documento d: list3) {
			double iva19= (d.getTotalCosto()==null?0.0:d.getTotalCosto());
			//double iva5= d.getBase5()==null?0.0:d.getBase5();
			double exento= (d.getExcento()==null?0.0:d.getExcento());
			totalAcum=totalAcum+iva19+exento;
			totalIva+=(d.getIva()==null?0.0:d.getIva());
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Salidas: ", 40) + Calculos.cortarCantidades(formatea.format(totalAcum),20) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo kardex salidas: ", 40) + Calculos.cortarCantidades(formatea.format(totalIva),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE

		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Devoluciones en compra: ", 40) + Calculos.cortarCantidades(formatea.format(0),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Kardex Devoluciones en compra: ", 40) + Calculos.cortarCantidades(formatea.format(0),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Devoluciones Ventas: ", 40) + Calculos.cortarCantidades(formatea.format(0),20) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Kardex Devoluciones Ventas: ", 40) + Calculos.cortarCantidades(formatea.format(0),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Utilidad Bruta del mes: ", 40) + Calculos.cortarCantidades(formatea.format(totalAcumventas-totalAcum),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Inventario Final (UPC): ", 40) + Calculos.cortarCantidades(formatea.format(costoTotalAcum),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Inventario Final (Costo Kardex): ", 40) + Calculos.cortarCantidades(formatea.format(costoTotalAcum),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, Calculos.cortarDescripcion("Costo Kardex FInal con Ajuste Inf.: ", 40) + Calculos.cortarCantidades(formatea.format(costoTotalAcum),20 ) , FontFactory.getFont(FontFactory.COURIER, 12f)))); // NOMBRE
		documento.close();
		
		
		return carpeta+pdf;
	}

	public String listadoDocumentos() throws FileNotFoundException, DocumentException{
		log.info("entra a exportar listadoDocumentos");
		String carpeta = "C:\\facturas\\listadoArticulos\\";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(new Date());
		String pdf = "listadoArticulos_" + fhoyIni + ".pdf";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		FileOutputStream archivo = new FileOutputStream(carpeta+pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		
		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		PdfWriter.getInstance(documento, archivo);
		documento.open();
		documento.setMargins(10, 1, 1, 1);
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + getEmpresa().getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "NIT " + getEmpresa().getNit(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "LISTADO DE ARTICULOS CON CANTIDAD ACTUAL Y COSTO KARDEX" , FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Fecha inicial: " +df2.format( getFechaInicio()), FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Fecha final: " + df2.format(getFechafin()), FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Códi. Descripción Artículo              Cant.  Costo_U    Costo_total Total_Iva        Total", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // NOMBRE
		
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NOMBRE
		Long tipoDocumento = 2l;
		List<ProductoEmpresa> productos = productoEmpresaService.getByEmpresa( getEmpresa().getEmpresaId());
		double cantidadAcum=0.0;
		double costoUAcum=0.0;
		double costoTotalAcum=0.0;
		double titalIvaAcum=0.0;
		double totalAcum=0.0;
		for(ProductoEmpresa pe: productos) {
			if(pe.getCantidad()>0) {
				double iva=pe.getProductoId().getIva()/100;
				String nombre= Calculos.cortarDescripcion(pe.getProductoId().getNombre(),36);
				String codigo = Calculos.cortarDescripcion(""+pe.getProductoId().getProductoId(), 5);
				String cantidad = Calculos.cortarCantidades(pe.getCantidad(), 6);
				double costou=(pe.getProductoId().getCosto() / (1 + iva));
				String costoU = Calculos.cortarCantidades(formatea.format(costou),10); 
				double costot=pe.getCantidad()*costou;
				String costoTotal = Calculos.cortarCantidades(formatea.format(costot),15);
				double totaliva = ((pe.getProductoId().getCosto()*pe.getCantidad() / (1 + iva)) * iva);
				String totalIva= Calculos.cortarCantidades(formatea.format(totaliva),11);		 
				String total = Calculos.cortarCantidades(formatea.format(costot+totaliva),13);
				documento.add(new Paragraph(
						new Phrase(lineSpacing,  codigo+"  "+ nombre+" "+cantidad+" "+Calculos.cortarCantidades(costoU,10)+" "+ 
								costoTotal+" "+totalIva
								+" "+ total, FontFactory.getFont(FontFactory.COURIER, fntSize-1)))); // NOMBRE
				
			cantidadAcum= cantidadAcum+pe.getCantidad();
			costoUAcum=costoUAcum+costou;
			costoTotalAcum=costoTotalAcum+costot;
			titalIvaAcum+=totaliva;
			totalAcum+=(costoTotalAcum+titalIvaAcum);
			}
			
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Códi. Descripción Artículo              _____  _______   ____________ ___________  _________", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // NOMBRE
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "                                        "+Calculos.cortarCantidades(formatea.format(cantidadAcum),10)+
										" "+Calculos.cortarCantidades(formatea.format(costoUAcum),10) +
										" "+Calculos.cortarCantidades(formatea.format(costoTotalAcum),15) +
										" "+Calculos.cortarCantidades(formatea.format(titalIvaAcum),11) +
										" "+Calculos.cortarCantidades(formatea.format(costoTotalAcum+titalIvaAcum),13) , FontFactory.getFont(FontFactory.COURIER, fntSize-1)))); // NOMBRE
		documento.close();																											// EMPRESA
		return carpeta+pdf;
	}

	public Date getFechaInicio() {
		if(fechaInicio==null) {
			fechaInicio=new Date();
		}
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechafin() {
		return fechafin;
	}

	public void setFechafin(Date fechafin) {
		this.fechafin = fechafin;
	}

	public List<ReduccionVo> getDocumentos() {
		if (documentos == null) {
			documentos = new ArrayList<>();
		}
		return documentos;
	}

	public void setDocumentos(List<ReduccionVo> documentos) {
		this.documentos = documentos;
	}

	public Double getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(Double totalVentas) {
		this.totalVentas = totalVentas;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public List<ProductoEmpresa> getProductoList() {
		productoList = productoEmpresaService.getByEmpresa(getEmpresa().getEmpresaId());
		return productoList;
	}

	public void setProductoList(List<ProductoEmpresa> productoList) {
		this.productoList = productoList;
	}

	public List<DocumentoDetalle> getCardexList() {
		return cardexList;
	}

	public void setCardexList(List<DocumentoDetalle> cardexList) {
		this.cardexList = cardexList;
	}

	public ProductoEmpresa getActual() {
		return actual;
	}

	public void setActual(ProductoEmpresa actual) {
		this.actual = actual;
	}

	public Double getTotalSalidas() {
		return totalSalidas;
	}

	public void setTotalSalidas(Double totalSalidas) {
		this.totalSalidas = totalSalidas;
	}

	public Double getTotalEntradas() {
		return totalEntradas;
	}

	public void setTotalEntradas(Double totalEntradas) {
		this.totalEntradas = totalEntradas;
	}

	public Date getFechaIniMovimiento() {
		return fechaIniMovimiento;
	}

	public void setFechaIniMovimiento(Date fechaIniMovimiento) {
		this.fechaIniMovimiento = fechaIniMovimiento;
	}

	public Date getFechaFinMovimiento() {
		return fechaFinMovimiento;
	}

	public void setFechaFinMovimiento(Date fechaFinMovimiento) {
		this.fechaFinMovimiento = fechaFinMovimiento;
	}

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<DocumentoDetalle> getDocumentosMovimiento() {
		return documentosMovimiento;
	}

	public void setDocumentosMovimiento(List<DocumentoDetalle> documentosMovimiento) {
		this.documentosMovimiento = documentosMovimiento;
	}

}
