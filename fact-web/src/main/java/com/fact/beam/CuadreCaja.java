package com.fact.beam;

import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.primefaces.context.RequestContext;

import com.fact.api.Calculos;
import com.fact.api.Impresion;
import com.fact.model.Abono;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empleado;
import com.fact.model.Empresa;
import com.fact.model.EntregaMercancia;
import com.fact.model.Grupo;
import com.fact.model.OpcionUsuario;
import com.fact.model.Producto;
import com.fact.model.SubMenu;
import com.fact.model.Usuario;
import com.fact.service.AbonoService;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.GrupoService;
import com.fact.service.OpcionUsuarioService;
import com.fact.service.ProductoService;
import com.fact.service.UsuarioService;
import com.fact.vo.BodegueroVo;
import com.fact.vo.ProductoVo;
import com.fact.vo.ReduccionVo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean
@SessionScoped
public class CuadreCaja implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private DocumentoService documentoService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;
	
	@EJB
	private AbonoService abonosService;
	
	@EJB
	private UsuarioService usuarioService;
	@EJB
	private ProductoService productoService;
	@EJB
	private OpcionUsuarioService opcionUsuarioService;
	
	@EJB
	private GrupoService grupoService;
	
	
	public Empresa empresa;
	public String primeraFact;
	public String ultimaFact;
	public Double abonosDia;
	public Double base;
	public Double recargas;
	public Double nomina;
	public Double vales;
	public Double cartera;
	public Double avanceEfectivo;
	public Double chequesRecogidos;
	public Double otros;
	public Double valorFajos;
	public Double monedas;
	public Double efectivo;
	public Double cheques;
	public Double documentosEspeciales;
	public Double totalTargetas;
	public Double varios;
	public Double devoluciones;
	public Double gastado;
	public Double descuentos;
	public Double propinas;
	public Double totalEnCaja;
	public Double totalFacturasToDay;
	public Double totalIngresos;
	
	List<Producto> productosAll;
	List<BodegueroVo> cajerosList;
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	public Configuracion configuracion(){		
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}
	
	public Double getTotalFaturasToDay() throws ParseException {
		List<Long> tipoDocumentoId = new ArrayList<>(); 
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		//tipoDocumentoId.add(8l);// tipo documento factura de salida
		Long server=1l;
		Boolean conCierre = Boolean.TRUE;
		//Date hoy = Calculos.fechaInicial(new Date());
		//Date hoyfin = Calculos.fechaFinal(new Date());
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		totalFacturasToDay = 0.0;
		OpcionUsuario liberarCuadre= opcionUsuarioService.getbySubMenuAndUsuario(usuario, 15l);
		List<Documento> factDia=new ArrayList<>();
		if(liberarCuadre.getLiberarCuadre()!=null && liberarCuadre.getLiberarCuadre()==1l){
			 factDia = documentoService.getByfacturasReales(tipoDocumentoId, usuario.getUsuarioId(),conCierre,server);
		}
		
	
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				totalFacturasToDay = totalFacturasToDay + d.getTotal().doubleValue();
			}
		}
		return totalFacturasToDay;
	}
	
	public void setTotalFaturasToDay(Double totalFacturasToDay) {
		this.totalFacturasToDay = totalFacturasToDay;
	}
	
	

	public Long getTotalFaturasNoImp() {
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		List<Long> tipoDocumentoId= new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		//tipoDocumentoId.add(4l); // tipo documento cotizacion
		//tipoDocumentoId.add(9l); // numero de guia
		List<Documento> factDia = documentoService.getDocNoImp(usuario.getUsuarioId(),tipoDocumentoId,1l);
		return (long) (factDia.size());
	}

	public Double getTotalIngresos() throws ParseException {
		totalIngresos = getTotalFaturasToDay()+
				       getAbonosDia()+
				       getAvanceEfectivo()+
				       getChequesRecogidos()+
				       getOtros()+
				       getBase()+
				       getRecargas();
		return totalIngresos;
	}
	
public void limpiar(){
	setChequesRecogidos(null);
	setOtros(null);
	setBase(null);
	setRecargas(null);
	setValorFajos(null);
	setMonedas(null);
	setEfectivo(null);
	setCheques(null);
	setDocumentosEspeciales(null);
	setTotalTargetas(null);
	setVarios(null);
	setGastado(null);
	setNomina(null);
	setDescuentos(null);
	}
	
	public void setTotalIngresos(Double totalIngresos) {
		this.totalIngresos = totalIngresos;
	}
	
	public Double getDiferencias() throws ParseException {
		Double total = getTotalIngresos()-getTotalEnCaja();
		//if(total<0){
			//total=total*-1;
		//}
		return total;
	}
	
	
	
	public String imprimirCuadre() throws IOException, ParseException, DocumentException, PrinterException {
		// falta agregar el pgrow en la pantalla de opcion, para que salga el
		// mensaje
		Configuracion con=configuracion();
		Empresa e = Login.getEmpresaLogin();
		String imp = e.getImpresion().toUpperCase();
		String pdf = "";
		switch (imp) {
		case "TXT":
			pdf =  imprimirCuadreTxt();
			break;
		case "BIG":
			pdf =  imprimirCuadreTxt();
			//pdf =imprimirCuadreTemporal();
			//pdf = imprimirBig(tituloFactura);
			break;
		case "PDF":
			pdf = imprimirCuadrePDF();
			if(con.getImpresionContinua()==1l){
				acumuladoventas(null);
				ventasIndividualesXcajero(null);
			}
			break;
		case "BIG_PDF":
			pdf =  imprimirCuadrePDF();
			break;
		case "SMALL_PDF":
			pdf = imprimirCuadrePDFSmall();
			break;
		default:
			break;
		}
		return pdf;
	}

	private String imprimirCuadrePDF() throws ParseException, DocumentException, IOException, PrinterException {
		Empresa e = Login.getEmpresaLogin();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Long noImpresas = getTotalFaturasNoImp();
		if (noImpresas > 0l) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Warning! Hay " + noImpresas + " Facturas no impresas", ""));
		} else {	
			System.out.println("imprimir cuadre pdf");
			Usuario usuario = (Usuario) sessionMap.get("userLogin");
			String pdf = "C:\\facturas\\cuadre_" + usuario.getNombre()+"_"+ Calendar.HOUR_OF_DAY+Calendar.MINUTE+Calendar.SECOND + ".pdf";
			FileOutputStream archivo = new FileOutputStream(pdf);
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			Document documento = new Document();
			float fntSize, lineSpacing;
			fntSize = 9f;
			lineSpacing = 10f;
			PdfWriter.getInstance(documento, archivo);
			documento.setMargins(10, 1, 1, 1);
			documento.open();
			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing,   e.getNombre() ,
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NOMBRE
			// EMPRESA
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getSlogan(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getRepresentante(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
								// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getDireccion(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getBarrio(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // barrio
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
			documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getNombre(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // te			
			documento.add(new Paragraph(new Phrase(lineSpacing,
			"CAJERO: " + usuario.getUsuarioId() + " "+usuario.getNombre() + " " + usuario.getApellido(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + getFechaHoy(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
			documento.add(new Paragraph(new Phrase(lineSpacing, "ENTREGO:______________________________" , FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Factura inicial:.......: "+getPrimeraFact(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Factura final:.........: "+getUltimaFact(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Descripción.................Valor", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Total Facturas:........: "+ formatea.format(getTotalFaturasToDay()) , FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Abonos:................: "+ formatea.format(getAbonosDia())+"\n", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Base:..................: "+ formatea.format(getBase()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Abance Efectivo:.......: "+ formatea.format(getAvanceEfectivo()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Cheques Recogidos:.....: "+ formatea.format(getChequesRecogidos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Otros :................: "+ formatea.format(getOtros()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Recargas:..............: "+ formatea.format(getRecargas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL MOV. DEL DIA:....: "+formatea.format(getTotalIngresos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			if( getValorFajos()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "VR. EN FAJOS:..........: "+formatea.format(getValorFajos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getMonedas() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "MONEDA: ...............: "+formatea.format(getMonedas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getEfectivo()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "EFECTIVO:..............: "+formatea.format(getEfectivo()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getCheques() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "CHEQUES:...............: "+formatea.format(getCheques()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getDocumentosEspeciales()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "DOC. ESPECIALES:.......: "+formatea.format(getDocumentosEspeciales()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getTotalTargetas()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "TARJETA DÉBITO Y CRÉDIT: "+formatea.format(getTotalTargetas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getVarios()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "VARIOS:................: "+formatea.format(getVarios()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}				
			if(getNomina()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "NOMINA:................: "+formatea.format(getNomina()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getGastado() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "GASTOS:................: "+formatea.format(getGastado()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getDescuentos()!=null){
			}
			if(getPropinas()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "PROPINAS:..............: "+formatea.format(getPropinas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
				documento.add(new Paragraph(new Phrase(lineSpacing, "VENTAS A CRÉDITO.......: "+formatea.format(getCartera()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "BONOS..................: "+"0", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL INGRESOS:........: "+formatea.format(getTotalEnCaja()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "VALES:.................: "+formatea.format(getVales()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, (getDiferencias()<0.0?"SOBRANTE":"FALTANTE")+"..$............: "+formatea.format(getDiferencias()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			//logica de cuadre de caja por grupos
			Map<String, Double> grupos = ventasPorGrupos(usuario);
			if(!grupos.isEmpty()){
				documento.add(new Paragraph(new Phrase(lineSpacing, "    VENTAS POR GRUPOS"+formatea.format(getCartera()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			for (Entry<String, Double> g : grupos.entrySet()){
				documento.add(new Paragraph(new Phrase(lineSpacing, ""+Calculos.cortarDescripcion(g.getKey(), 23)+": "+formatea.format(g.getValue()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			//logica de propinas
			if(getPropinas()!=null && getPropinas()!=0.0){
				documento.add(new Paragraph(new Phrase(lineSpacing, "Detalle Propinas:", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				List<Empleado> temp= usuarioService.getByEmpleadosAll();
				for(Empleado em: temp){
					String nombreEmpleado= Calculos.cortarDescripcion(em.getNombre(), 15);
					Date hoy = Calculos.fechaInicial(new Date());
					Date hoyfin = Calculos.fechaFinal(new Date());
					List<DocumentoDetalle> propinasEmpleado = documentoDetalleService.getbyEmpleado(em.getEmpleadoId(), hoy,  hoyfin);
					Double totalPropina=0.0;
					System.out.println("imprimir cuadre pdf");
					for(DocumentoDetalle dd: propinasEmpleado){
						totalPropina+=dd.getParcial();
					}
					String propinasEmp=formatea.format(totalPropina);
					documento.add(new Paragraph(new Phrase(lineSpacing,nombreEmpleado+": "+propinasEmp, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
					documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				}
			}
			documento.close();
			
			String impresara =  usuario.getImpresora();
			Impresion.printer(impresara, pdf,configuracion());		
		}
		return "";
	}
	
	/**
	 * Medoto que retorna las ventas totales por grupo, si existen grupos creados
	 * @param usuario
	 * @return
	 */
	public Map<String, Double> ventasPorGrupos(Usuario usuario){
		Map<String, Double> totalGrupos = new HashMap<String, Double>();
		List<Grupo> grupos = grupoService.getByAll();
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		Date hoy = Calculos.fechaInicial(new Date());
		Date hoyfin = Calculos.fechaFinal(new Date());
		Boolean conCierre = Boolean.TRUE;
		Long server=1l;
		for(Grupo g: grupos){
			List<DocumentoDetalle> documentos = documentoService.getByGrupo(tipoDocumentoId, hoy, hoyfin,usuario.getUsuarioId(),conCierre,server,g.getGrupoId());
			Double total = 0.0;
			for(DocumentoDetalle d: documentos){
				if(d.getParcial()!=null){
					total+=d.getParcial();
				}
			}
			totalGrupos.put(g.getNombre(), total);
		}
		return totalGrupos;
	}
	
	private String imprimirCuadrePDFSmall() throws ParseException, DocumentException, IOException, PrinterException {
		Empresa e = Login.getEmpresaLogin();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Long noImpresas = getTotalFaturasNoImp();
		if (noImpresas > 0l) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Warning! Hay " + noImpresas + " Facturas no impresas", ""));
		} else {	
			System.out.println("imprimir cuadre pdf");
			Usuario usuario = (Usuario) sessionMap.get("userLogin");
			String pdf = "C:\\facturas\\cuadre_" + usuario.getNombre()+"_"+ Calendar.HOUR_OF_DAY+Calendar.MINUTE+Calendar.SECOND + ".pdf";
			FileOutputStream archivo = new FileOutputStream(pdf);
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			Document documento = new Document();
			float fntSize, lineSpacing;
			fntSize = 8f;
			lineSpacing = 9f;
			PdfWriter.getInstance(documento, archivo);
			documento.setMargins(1, 1, 1, 1);
			documento.open();
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, ">>" + e.getNombre() + "<<",
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NOMBRE
			// EMPRESA
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getSlogan(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getRepresentante(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
								// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getDireccion(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(
			new Phrase(lineSpacing, "" + e.getBarrio(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // barrio
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
			documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getNombre(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // te			
			documento.add(new Paragraph(new Phrase(lineSpacing,
			"CAJERO: " + usuario.getUsuarioId() + " "+usuario.getNombre() + " " + usuario.getApellido(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + getFechaHoy(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
			documento.add(new Paragraph(new Phrase(lineSpacing, "ENTREGO:________________" , FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Factura inicial:...: "+getPrimeraFact(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Factura final:.....: "+getUltimaFact(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Descripción.........Valor", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Total Facturas.....: "+ formatea.format(getTotalFaturasToDay()) , FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Abonos:............: "+ formatea.format(getAbonosDia())+"\n", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Base:..............: "+ formatea.format(getBase()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Abance Efectivo:...: "+ formatea.format(getAvanceEfectivo()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Cheques Recogidos..: "+ formatea.format(getChequesRecogidos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Otros..............: "+ formatea.format(getOtros()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "Recargas:..........: "+ formatea.format(getRecargas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL MOV. DEL DIA.: "+formatea.format(getTotalIngresos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			if( getValorFajos()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "VR. EN FAJOS:..: "+formatea.format(getValorFajos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getMonedas() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "MONEDA: .......: "+formatea.format(getMonedas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getEfectivo()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "EFECTIVO:......: "+formatea.format(getEfectivo()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getCheques() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "CHEQUES:.......: "+formatea.format(getCheques()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getDocumentosEspeciales()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "DOC. ESPECIALES: "+formatea.format(getDocumentosEspeciales()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getTotalTargetas()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "TARJ DÉB Y CRÉD: "+formatea.format(getTotalTargetas()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getVarios()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "VARIOS:........: "+formatea.format(getVarios()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}				
			if(getNomina()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "NOMINA:........: "+formatea.format(getNomina()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getGastado() !=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "GASTOS:........: "+formatea.format(getGastado()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
			if(getDescuentos()!=null){
				documento.add(new Paragraph(new Phrase(lineSpacing, "DESCUENTOS.....: "+formatea.format(getDescuentos()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			}
				documento.add(new Paragraph(new Phrase(lineSpacing, "VENTAS CRÉDITO.: "+formatea.format(getCartera()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "BONOS..........: "+"0", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL INGRESOS.: "+formatea.format(getTotalEnCaja()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "VALES:.........: "+formatea.format(getVales()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, (getDiferencias()<0.0?"SOBRANTE":"FALTANTE")+".:$ "+formatea.format(getDiferencias()), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.add(new Paragraph(new Phrase(lineSpacing, "------------------------", FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
				documento.close();
			String impresara =  usuario.getImpresora();
			Impresion.printer(impresara, pdf,configuracion());		
		}
		return "";
	}

	

	private String imprimirCuadreTxt() throws IOException, ParseException {
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		int maxTamaño=14;
		System.out.println("entro a imprimir cuadre");
		Long noImpresas = getTotalFaturasNoImp();
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		if (noImpresas > 0l) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Warning! Hay " + noImpresas + " Facturas no impresas", ""));
		} else {
			// todo lo de imprimir cuadre
			//Date f= new Date();
			String pdf= Calendar.HOUR_OF_DAY+Calendar.MINUTE+Calendar.SECOND+".txt";
			pdf = pdf.replace(" ", "_");
			File archivo = new File("C:\\facturacion\\"+pdf);
			BufferedWriter bw;
		    bw = new BufferedWriter(new FileWriter(archivo));
		    bw.write("---------------------------------------\n");
		    bw.write("         >>"+getEmpresa().getNombre()+"<<\n");
		    bw.write("           "+getEmpresa().getRepresentante()+"         \n");
		    bw.write("      NIT. "+getEmpresa().getNit()+"   "+getEmpresa().getRegimen()+"   \n");
		    bw.write("          "+getEmpresa().getDireccion()+"           \n");
		    bw.write("           "+getEmpresa().getBarrio()+"            \n");
		    bw.write("  	 	   "+getEmpresa().getCiudad()+"-"+getEmpresa().getDepartamento()+"	        \n");
		    bw.write("             TEL: "+getEmpresa().getCel()+"             \n");
		    bw.write(usuario.getNombre()+" "+usuario.getApellido()+ "\n");
		    bw.write(getFechaHoy()+ "\n");
		    bw.write("ENTREGO:______________________________\n");
		    bw.write("Factura inicial:....: "+getPrimeraFact()+"\n");
		    bw.write("Factura final:......: "+getUltimaFact()+"\n");
		    bw.write("---------------------------------------\n");
		    bw.write("Cantidad.......Descripción........Valor\n");
		    bw.write("---------------------------------------\n");
		    bw.write("Total Facturas:.....: "+ Calculos.cortarCantidades(formatea.format(getTotalFaturasToDay()), maxTamaño)+"\n");
		    bw.write("Abonos:.............: "+ Calculos.cortarCantidades(formatea.format(getAbonosDia()),maxTamaño)+"\n");
		    bw.write("Base:...............: "+ Calculos.cortarCantidades(formatea.format(getBase()),maxTamaño)+"\n");
		    bw.write("Abance Efectivo:....: "+ Calculos.cortarCantidades(formatea.format(getAvanceEfectivo()),maxTamaño)+"\n");
		    bw.write("Cheques Recogidos:..: "+ Calculos.cortarCantidades(formatea.format(getChequesRecogidos()),maxTamaño)+"\n");
		    bw.write("Otros :.............: "+ Calculos.cortarCantidades(formatea.format(getOtros()),maxTamaño)+"\n");
		    bw.write("Recargas:...........: "+ Calculos.cortarCantidades(formatea.format(getRecargas()),maxTamaño)+"\n");
		    bw.write("TOTAL MOV. DEL DIA:.: "+ Calculos.cortarCantidades(formatea.format(getTotalIngresos()),maxTamaño) +"\n");
		    bw.write("---------------------------------------\n");
		    bw.write("VR. EN FAJOS:.......: "+Calculos.cortarCantidades(formatea.format(getValorFajos()==null?0.0:getValorFajos()),maxTamaño)+"\n");
		    bw.write("MONEDA: ............: "+Calculos.cortarCantidades(formatea.format(getMonedas()==null?0.0:getMonedas()),maxTamaño)+"\n");
		    bw.write("EFECTIVO:...........: "+Calculos.cortarCantidades(formatea.format(getEfectivo()==null?0.0:getEfectivo()),maxTamaño)+"\n");
		    bw.write("CHEQUES:............: "+Calculos.cortarCantidades(formatea.format(getCheques()==null?0.0:getCheques()),maxTamaño)+"\n");
		    bw.write("DOC. ESPECIALES:....: "+Calculos.cortarCantidades(formatea.format(getDocumentosEspeciales()==null?0.0:getDocumentosEspeciales()),maxTamaño)+"\n");
		    bw.write("TARJETA:............: "+Calculos.cortarCantidades(formatea.format(getTotalTargetas()==null?0.0:getTotalTargetas()),maxTamaño)+"\n");
		    bw.write("VARIOS:.............: "+Calculos.cortarCantidades(formatea.format(getVarios()==null?0.0:getVarios()),maxTamaño)+"\n");
		    bw.write("VALES:..............: "+Calculos.cortarCantidades(formatea.format(getVales()),maxTamaño)+"\n");
		    if(getNomina()!=null){
		    	bw.write("NOMINA:.........: "+Calculos.cortarCantidades(formatea.format(getNomina()==null?0.0:getNomina()),maxTamaño) +"\n");
		    }	
		    if(getGastado()!=null){
		    	bw.write("GASTOS:.........: "+Calculos.cortarCantidades(formatea.format(getGastado()==null?0.0:getGastado()),maxTamaño)+"\n");
		    }
		    if(getDescuentos()!=null){
		    	bw.write("DESCUENTOS:.....: "+Calculos.cortarCantidades(formatea.format(getDescuentos()==null?0.0:getDescuentos()),maxTamaño)+"\n");
		    }
		    
		    bw.write("TOTAL INGRESOS:.....: "+Calculos.cortarCantidades(formatea.format(getTotalEnCaja()),maxTamaño)+"\n");
		    bw.write("---------------------------------------\n");
		    bw.write("FALTANTE..$.........: "+Calculos.cortarCantidades(formatea.format(getDiferencias()),maxTamaño)+"\n");
		    bw.write("---------------------------------------\n");
		    bw.close();
		    FileInputStream inputStream = null;
	          try {
	              inputStream = new FileInputStream("C:\\facturacion\\"+pdf);
	              System.out.println(pdf);
	          } catch (FileNotFoundException ex) {
	              ex.printStackTrace();
	          }
	          if (inputStream == null) {
	              //return;
	          }
	          DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
	          Doc document = new SimpleDoc(inputStream, docFormat, null);
	          PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();			          
	          PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();			   
	          if (defaultPrintService != null) {
	              DocPrintJob printJob = defaultPrintService.createPrintJob();
	              try {
	                  printJob.print(document, attributeSet);
	   
	              } catch (Exception ex) {
	                  ex.printStackTrace();
	              }
	          } else {
	              System.err.println("No existen impresoras instaladas");
	          }
		}
		return "";
	}

	

	public String pantalla() {
		RequestContext.getCurrentInstance().execute("PF('cuadre_caja').hide();");
		RequestContext.getCurrentInstance().execute("PF('pantallaOpc').show();");
		return "";
	}

	public String getFechaHoy(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechaHoy = df.format(new Date());
		return fechaHoy;
	}
	
public void cierreTotal() throws ParseException{
		
		List<Long> tipoDocumentoId= new ArrayList<>();
		long server =1;
		tipoDocumentoId.add( 10l); // tipo documento factura de salida
		tipoDocumentoId.add( 5l); // tipo documento cotizacion
		tipoDocumentoId.add( 4l); // tipo documento remision
		tipoDocumentoId.add( 8l); // tipo documento remision
		Date hoy = Calculos.fechaInicial(new Date());
		Date hoyfin = Calculos.fechaFinal(new Date());
		List<Documento> factDia = documentoService.getByFacturaSinCierre(tipoDocumentoId, hoy, hoyfin);
		for (Documento d : factDia) {
			d.setCierreDiario(1l);
			d.setImpreso(1l);
			documentoService.update(d,server);
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cierre Diario Realizado Exitosamente"));	
		RequestContext.getCurrentInstance().execute("PF('confirmarCierreDiario').hide();");
		RequestContext.getCurrentInstance().execute("document.getElementById('menuPrincipal:m_1_menuButton').focus();");
	}

public Empresa getEmpresa() {
	if(empresa==null){
		empresa =Login.getEmpresaLogin();
	}
	return empresa;
}

public void setEmpresa(Empresa empresa) {
	this.empresa = empresa;
}

public String getPrimeraFact() throws ParseException {
	Long tipoDocumentoId = 10l; // tipo documento factura de salida
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	List<Documento> factDia = documentoService.getByFacturaByDia(tipoDocumentoId, hoy, hoyfin);
	if(factDia!=null && !factDia.isEmpty()){
		primeraFact = factDia.get(0).getConsecutivoDian();
	}else{
		primeraFact="0";
	}
	return primeraFact;
}

public void setPrimeraFact(String primeraFact) {
	this.primeraFact = primeraFact;
}

public String getUltimaFact() throws ParseException {
	Long tipoDocumentoId = 10l; // tipo documento factura de salida
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	List<Documento> factDia = documentoService.getByFacturaByDia(tipoDocumentoId, hoy, hoyfin);
	if(factDia!=null && !factDia.isEmpty()){
		ultimaFact = factDia.get(factDia.size()-1).getConsecutivoDian();
	}else{
		ultimaFact="0";
	}

	return ultimaFact;
}

public void setUltimaFact(String ultimaFact) {
	this.ultimaFact = ultimaFact;
}

public Double getAbonosDia() throws ParseException {
	List<Abono> abonos = new ArrayList<>();
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	List<Long> tipoDocumentoId= new ArrayList<>();
	tipoDocumentoId.add(10l); //tipo documento igual a factura de venta
	tipoDocumentoId.add(8l);//tipo documetno igual a vale
	abonos=abonosService.abonosDia(hoy,hoyfin,usuario,tipoDocumentoId);
	Double total=0.0;
	for (Abono a : abonos) {
		if (a.getCantidadAbono() != null) {
			total = total + a.getCantidadAbono();
		}
	}
	return total;
}

public void setAbonosDia(Double abonosDia) {
	this.abonosDia = abonosDia;
}

public Double getDevolucionesDia() throws ParseException {
	List<Abono> abonos = new ArrayList<>();
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	List<Long> tipoDocumentoId= new ArrayList<>();
	tipoDocumentoId.add(10l); //tipo documento igual a factura de venta
	tipoDocumentoId.add(8l);//tipo documetno igual a vale
	abonos=abonosService.abonosDia(hoy,hoyfin,usuario,tipoDocumentoId);
	Double total=0.0;
	for (Abono a : abonos) {
		if (a.getCantidadAbono() != null) {
			total = total + a.getCantidadAbono();
		}
	}
	return total;
}

public void setDevolucionesDia(Double abonosDia) {
	this.abonosDia = abonosDia;
}


public Double getBase() {
	if( base==null){
		base=0.0;
	}
	return base;
}

public void setBase(Double base) {
	this.base = base;
}

public Double getRecargas() {
	if(recargas==null){
		recargas=0.0;
	}
	return recargas;
}

public void setRecargas(Double recargas) {
	this.recargas = recargas;
}

public Double getNomina() {	
	return nomina;
}

public void setNomina(Double nomina) {
	this.nomina = nomina;
}

public Double getVales() throws ParseException {
	List<Documento> documentos = new ArrayList<>();
	
	Long tipoDocumentoId= 8l;//bisqieda qie vales
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	Boolean conCierre=Boolean.TRUE;
	Long server=1l;
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	documentos= documentoService.getRemisionesByUsuarioConFecha(tipoDocumentoId, hoy, hoyfin,usuario.getUsuarioId(),conCierre,server);
	Double total=0.0;
	for (Documento a : documentos) {
		if (a.getTotal() != null) {
			total = total + a.getTotal();
		}
	}
	return total;
}


public void setVales(Double vales) {
	this.vales = vales;
}

public Double getCartera() throws ParseException {
	List<Documento> documentos = new ArrayList<>();
	List<Long> tipoPago = new ArrayList<>();
	tipoPago.add(2l);//busqueda de documentos a credito
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	Boolean conCierre=Boolean.TRUE;
	Long server=1l;
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	documentos= documentoService.getByTipoPago(tipoPago, hoy, hoyfin,usuario.getUsuarioId(),conCierre,server);
	Double total=0.0;
	for (Documento a : documentos) {
		if (a.getTotal() != null) {
			total = total + a.getTotal();
		}
	}
	return total;
}

public void setCartera(Double cartera) {
	this.cartera = cartera;
}

public Double getAvanceEfectivo() throws ParseException {
	List<Documento> documentos = new ArrayList<>();
	
	List<Long> tipoDocumentoId =new ArrayList<>();
	tipoDocumentoId.add(5l); //bisqieda qie avance efectivo
	Long server=1l;
	Boolean conCierre=Boolean.TRUE;
	//Date hoy = Calculos.fechaInicial(new Date());
	//Date hoyfin = Calculos.fechaFinal(new Date());
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	documentos= documentoService.getByfacturasReales(tipoDocumentoId,usuario.getUsuarioId(),conCierre,server);
	Double total=0.0;
	for (Documento a : documentos) {
		if (a.getTotal() != null) {
			total = total + a.getTotal();
		}
	}
	return total;
}

public void setAvanceEfectivo(Double avanceEfectivo) {
	this.avanceEfectivo = avanceEfectivo;
}

public Double getChequesRecogidos() {
	if(chequesRecogidos==null){
		chequesRecogidos=0.0;
	}
	return chequesRecogidos;
}

public void setChequesRecogidos(Double chequesRecogidos) {
	this.chequesRecogidos = chequesRecogidos;
}

public Double getOtros() {
	if(otros==null){
		otros=0.0;
	}
	return otros;
}

public void setOtros(Double otros) {
	this.otros = otros;
}

public Double getValorFajos() {
	return valorFajos;
}

public void setValorFajos(Double valorFajos) {
	this.valorFajos = valorFajos;
}

public Double getMonedas() {
	return monedas;
}

public void setMonedas(Double monedas) {
	this.monedas = monedas;
}

public Double getEfectivo() {	
	return efectivo;
}

public void setEfectivo(Double efectivo) {
	this.efectivo = efectivo;
}

public Double getCheques() {	
	return cheques;
}

public void setCheques(Double cheques) {
	this.cheques = cheques;
}

public Double getDocumentosEspeciales() {
		return documentosEspeciales;
}

public void setDocumentosEspeciales(Double documentosEspeciales) {
	this.documentosEspeciales = documentosEspeciales;
}

public Double getTotalTargetas() {		
	return totalTargetas;
}

public void setTotalTargetas(Double totalTargetas) {
	this.totalTargetas = totalTargetas;
}

public Double getVarios() {	
	return varios;
}

public void setVarios(Double varios) {
	this.varios = varios;
}

public Double getDevoluciones() {
	return devoluciones;
}

public void setDevoluciones(Double devoluciones) {
	this.devoluciones = devoluciones;
}

public Double getGastado() {
	return gastado;
}

public void setGastado(Double gastado) {
	this.gastado = gastado;
}

public Double getDescuentos() {
List<Documento> documentos = new ArrayList<>();
	
	List<Long> tipoDocumentoId= new ArrayList<>(); 
	tipoDocumentoId.add(10l);//bisqieda qie vales
	Boolean conCierre=Boolean.TRUE;
	Long server=1l;
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	documentos= documentoService.getByfacturasReales(tipoDocumentoId, usuario.getUsuarioId() , conCierre,server);
	Double total=0.0;
	for (Documento a : documentos) {
		if (a.getDescuento() != null) {
			total = total + (a.getDescuento()*-1);
		}
	}
	descuentos=total;
	return descuentos;
}

public void setDescuentos(Double descuentos) {
	this.descuentos = descuentos;
}

public Double getTotalEnCaja() throws ParseException {
	totalEnCaja=0.0;
	totalEnCaja=(getValorFajos()==null?0.0:getValorFajos())+
				(getMonedas()==null?0.0:getMonedas())+
				(getEfectivo()==null?0.0:getEfectivo())+
				(getCheques()==null?0.0:getCheques())+
				(getDocumentosEspeciales()==null?0.0:getDocumentosEspeciales())+
				(getTotalTargetas()==null?0.0:getTotalTargetas())+
				(getVarios()==null?0.0:getVarios())+
				(getVales()==null?0.0:getVales())+
				(getGastado()==null?0.0:getGastado())+
				(getCartera()==null?0.0:getCartera())+
				(getNomina()==null?0.0:getNomina())+
				(getDescuentos()==null?0.0:getDescuentos())+
				(getPropinas()==null?0.0:getPropinas());
	return totalEnCaja;
}

public void setTotalEnCaja(Double totalEnCaja) {
	this.totalEnCaja = totalEnCaja;
}

public void acumuladoventas(ReduccionVo redu) throws DocumentException, IOException, PrinterException{
	List<Long> tipoDocumentoSalidasId = new ArrayList<>();
	List<Long> tipoDocumentoEntrasdasId = new ArrayList<>();
	List<Long> productosEntradasList = new ArrayList<>();
	List<Long> productosSalidasList = new ArrayList<>();
	
	tipoDocumentoSalidasId.add(10l);
	tipoDocumentoEntrasdasId.add(2l);
    List<Documento> documentosSalidas = new ArrayList<>();
    List<Documento> documentosEntradas = new ArrayList<>();
    List<DocumentoDetalle> docuDetalleEntrada = new ArrayList<>();
    List<DocumentoDetalle> docuDetalleSalida = new ArrayList<>();
    List<ProductoVo> cantidadesEntradas = new ArrayList<>();
    List<ProductoVo> cantidadesSalidas = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    if(redu!=null){
    	calendar.setTime(redu.getFecha());
    }else{
    	calendar.setTime(new Date());
    }
	calendar.set(Calendar.HOUR_OF_DAY, 17);
	calendar.set(Calendar.MINUTE, 59);
	calendar.set(Calendar.SECOND, 59);
	Date hoyfin= calendar.getTime();
	
	System.out.println(calendar.get(Calendar.DAY_OF_MONTH)-1);       
	calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
	calendar.set(Calendar.HOUR_OF_DAY, 18);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	Date hoy = calendar.getTime();
	documentosEntradas=documentoService.getByFacturaByDia(2l, hoy, hoyfin );
	documentosSalidas=documentoService.getByFacturaByDia(10l, hoy, hoyfin);
	//System.out.println("documentos:"+documentos.size());		
	if(!documentosEntradas.isEmpty()){
		docuDetalleEntrada=documentoDetalleService.getByDocumento(documentosEntradas);
	}
	if(!documentosSalidas.isEmpty()){
		docuDetalleSalida=documentoDetalleService.getByDocumento(documentosSalidas);
	}	    
    for(DocumentoDetalle dd: docuDetalleSalida){
		if(!productosSalidasList.contains(dd.getProductoId().getProductoId())){
			ProductoVo producto= new ProductoVo();
			producto.setProducto(dd.getProductoId());
			producto.setCantidad(dd.getCantidad());
			cantidadesSalidas.add(producto);
			productosSalidasList.add(dd.getProductoId().getProductoId());
		}else{
			for(ProductoVo ps: cantidadesSalidas){
				if(ps.getProducto().getProductoId()==(dd.getProductoId().getProductoId())){
					ProductoVo producto=ps;
					Double canti = dd.getCantidad()+ps.getCantidad();
					producto.setCantidad(canti);
					ps.setCantidad(canti);
					break;
										
				}
			}
		}
    }
    for(DocumentoDetalle dd: docuDetalleEntrada){
		if(!productosEntradasList.contains(dd.getProductoId().getProductoId())){
			ProductoVo producto= new ProductoVo();
			producto.setProducto(dd.getProductoId());
			producto.setCantidad(dd.getCantidad());
			cantidadesEntradas.add(producto);
			productosEntradasList.add(dd.getProductoId().getProductoId());
		}else{
			for(ProductoVo ps: cantidadesEntradas){
				if(ps.getProducto().getProductoId()==(dd.getProductoId().getProductoId())){
					ProductoVo producto=ps;
					Double canti = dd.getCantidad()+ps.getCantidad();
					producto.setCantidad(canti);
					ps.setCantidad(canti);
					break;											
				}
			}
		}
    }
    Empresa e = Login.getEmpresaLogin();
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
	String fhoyname = df.format(hoyfin);
	String carpeta = "C:\\facturas\\AcumuladoVentas";
	
	String pdf = "";
	if(redu!=null){
		pdf = "\\AcumuladoVentas" + fhoyname + "_"+redu.getUsuarioId().getUsuarioId()+".pdf";
	}else{
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		pdf = "\\AcumuladoVentas" + fhoyname + "_"+usuario.getUsuarioId()+".pdf";
	}
	FileOutputStream archivo = new FileOutputStream(carpeta+pdf);
	Document documento = new Document();
	float fntSize, lineSpacing;
	fntSize = 9f;
	lineSpacing = 10f;
	PdfWriter.getInstance(documento, archivo);
	documento.setMargins(1, 1, 1, 1);
	documento.open();
	Configuracion con=configuracion();
	documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
	if(redu!=null){	
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, ">>" + e.getNombre() + "<<",
		FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NOMBRE
		// EMPRESA
		documento.add(new Paragraph(
		new Phrase(lineSpacing, "" + e.getSlogan(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
		documento.add(new Paragraph(
		new Phrase(lineSpacing, "" + e.getRepresentante(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
							// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
		FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(
		new Phrase(lineSpacing, "" + e.getDireccion(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
		documento.add(new Paragraph(
		new Phrase(lineSpacing, "" + e.getBarrio(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // barrio
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
		FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getNombre(),
		FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	}
	
	documento.add(new Paragraph(new Phrase(lineSpacing, "Acumulado de ventas diarias: ",
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	documento.add(new Paragraph(new Phrase(lineSpacing,  df2.format(hoyfin),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	documento.add(new Paragraph(new Phrase(lineSpacing, "",FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
	// LEGAL
	documento.add(new Paragraph(new Phrase(lineSpacing, "Producto                  Inic Sal  Fin" ,
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize))));
	documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
	// LEGAL
    for(Producto p: getProductosAll()){
    	
		String inicial = "";
		int tamañoInicial =0, maxTamañoInicial=4;
		String entradas = "0   ";
		int tamañoEntradas =0, maxTamañoEntradas=3;
		String salidas = "0   ";
		int tamañoSalidas =0, maxTamañoSalidas=3;
		Double entrdaSum = 0.0,salidaSum=0.0;
		//descripcion
		String nombre = "";
		int  maxTamañoNombre=25;
		nombre=Calculos.cortarDescripcion(p.getNombre(), maxTamañoNombre);
		
		//salidas
		if(cantidadesSalidas.isEmpty()){
			salidas="0   ";
			salidaSum=0.0;
		}
		for(ProductoVo ps: cantidadesSalidas){
			if(ps.getProducto().getProductoId().toString().equals(p.getProductoId().toString())){
				salidas = ps.getCantidad().toString();
				salidas= salidas.replace(".0", "");
				try {					
					salidas=salidas.substring(0, maxTamañoSalidas);				
				} catch (Exception e2) {
					tamañoSalidas = salidas.length();
				}
				if(tamañoSalidas!=0){
					for(int j= tamañoSalidas; j<maxTamañoSalidas; j++){
						salidas+=" ";
					}
				}	
				salidaSum=ps.getCantidad();
				break;
			}
		}
		
		//Entradas
		if(cantidadesEntradas.isEmpty()){
			entradas="0   ";
			entrdaSum=0.0;
		}
		for(ProductoVo ps: cantidadesEntradas){
			if(ps.getProducto().getProductoId().toString().equals(p.getProductoId().toString())){
				try {
					entradas = ps.getCantidad().toString();
					entradas= entradas.replace(".0", "");
					entradas=entradas.substring(0, maxTamañoEntradas);				
				} catch (Exception e2) {
					entradas = ps.getCantidad().toString();
					entradas= entradas.replace(".0", "");
					tamañoEntradas = entradas.length();
				}
				if(tamañoEntradas!=0){
					for(int j= tamañoEntradas; j<maxTamañoEntradas; j++){
						entradas+=" ";
					}
				}	
				entrdaSum=ps.getCantidad();
				break;
			}				
		}
		
		//inicial
		inicial =  String.valueOf((p.getCantidad()==null?0.0:p.getCantidad())+(salidaSum==null?0.0:salidaSum)-(entrdaSum==null?0.0:entrdaSum));
		inicial= inicial.replace(".0", "");
		try {				
			inicial=inicial.substring(0, maxTamañoInicial);				
		} catch (Exception e2) {			
			tamañoInicial = inicial.length();
		}
		if(tamañoInicial!=0){
			for(int j= tamañoInicial; j<maxTamañoInicial; j++){
				inicial+=" ";
			}
		}	
			
		
		//final
		String final1 = String.valueOf (p.getCantidad()).replace(".0", "");
		documento.add(new Paragraph(new Phrase(lineSpacing,""+ nombre + " " +  inicial+" "+salidas+" "+final1,
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
    }
		
    documento.close();
	
	String impresara =""; 
	if(redu!=null){
		impresara=redu.getUsuarioId().getImpresora();
	}else{
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		impresara=usuario.getImpresora();
	}
	Impresion.printer(impresara, carpeta+pdf,configuracion());   
}

public void ventasIndividualesXcajero(ReduccionVo redu) throws DocumentException, IOException, PrinterException{
	Boolean sinCierre = Boolean.TRUE;
	List<Long> tipoDocumentoId = new ArrayList<>();
	List<Long> productosList = new ArrayList<>();
	
	tipoDocumentoId.add(10l);
    List<Documento> documentos = new ArrayList<>();
    List<DocumentoDetalle> docuDetalle = new ArrayList<>();
    List<ProductoVo> cantidades = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    if(redu!=null){
    	calendar.setTime(redu.getFecha());
    }else{
    	calendar.setTime(new Date());
    }	
	calendar.set(Calendar.HOUR_OF_DAY, 17);
	calendar.set(Calendar.MINUTE, 59);
	calendar.set(Calendar.SECOND, 59);
	Date hoyfin= calendar.getTime();
	
	System.out.println(calendar.get(Calendar.DAY_OF_MONTH)-1);       
	calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
	calendar.set(Calendar.HOUR_OF_DAY, 18);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	Date hoy = calendar.getTime();
	Long server=1l;
	ReduccionVo redu3 = new ReduccionVo();
	if(redu==null){
		ReduccionVo redu2 = new ReduccionVo();
		redu=redu2;
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		redu.setUsuarioId(usuario);
		redu3 = null;
	}else{
		ReduccionVo redu2 = new ReduccionVo();
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		redu3.setUsuarioId(usuario);
		redu3=redu2;
	}
	
	documentos=documentoService.getByfacturasReales(tipoDocumentoId, redu.getUsuarioId().getUsuarioId() , sinCierre,server);
	if(documentos!=null && !documentos.isEmpty()){
		 docuDetalle=documentoDetalleService.getByDocumento(documentos);
		    //hacerlo con dos haspmap, 
			for(DocumentoDetalle dd: docuDetalle){
				if(!productosList.contains(dd.getProductoId().getProductoId())){
					ProductoVo producto= new ProductoVo();
					producto.setProducto(dd.getProductoId());
					producto.setCantidad(dd.getCantidad());
					cantidades.add(producto);
					productosList.add(dd.getProductoId().getProductoId());
				}else{
					for(ProductoVo ps: cantidades){
						if(ps.getProducto().getProductoId()==(dd.getProductoId().getProductoId())){
							ProductoVo producto=ps;
							Double canti = dd.getCantidad()+ps.getCantidad();
							producto.setCantidad(canti);
							ps.setCantidad(canti);
							break;
												
						}
					}
				}
			}
	}
	System.out.println("documentos:"+documentos.size());			
   
	Empresa e = Login.getEmpresaLogin();
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	String fhoyname = df.format(hoyfin);
	String carpeta = "C:\\facturas\\productosXcajero";
	String pdf = "\\productosXcajero_" + fhoyname + "_"+redu.getUsuarioId().getUsuarioId()+".pdf";
	FileOutputStream archivo = new FileOutputStream(carpeta+pdf);
	Document documento = new Document();
	float fntSize, lineSpacing;
	fntSize = 9f;
	lineSpacing = 10f;
	PdfWriter.getInstance(documento, archivo);
	documento.setMargins(1, 1, 1, 1);
	documento.open();
	documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
	if(redu3!=null){
		documento.add(new Paragraph(new Phrase(lineSpacing, ">>" + e.getNombre() + "<<",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NOMBRE
																		// EMPRESA
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getSlogan(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getRepresentante(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																															// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getDireccion(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getBarrio(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // barrio
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getNombre(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	}
	
	documento.add(new Paragraph(new Phrase(lineSpacing, "Cajero: " + redu.getUsuarioId().getNombre() + " - " +  redu.getUsuarioId().getApellido(),
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	documento.add(new Paragraph(new Phrase(lineSpacing, "",FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
	documento.add(new Paragraph(new Phrase(lineSpacing, "Producto                Cantidad " ,
			FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize))));
	for(ProductoVo vo: cantidades){
		String nombre="";	
		int tamañoNombre = 0;	
		try {
			nombre = "" + vo.getProducto().getNombre().substring(0, 23);
		} catch (Exception e2) {
			nombre = "" + vo.getProducto().getNombre();
			tamañoNombre=nombre.length();
		}
		if(tamañoNombre!=0){
			for(int j= tamañoNombre; j<23; j++){
				nombre+=" ";
			}
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + nombre + "..: " +  vo.getCantidad(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		//System.out.println("\nproducto:"+vo.getProducto().getNombre());
		//System.out.println("cantidad:"+vo.getCantidad());
	}
	documento.close();
	
	String impresara = redu.getUsuarioId().getImpresora();
	Impresion.printer(impresara, carpeta+pdf,configuracion());
	//return pdf;
}

public List<Producto> getProductosAll() {
	if (productosAll == null || productosAll.isEmpty()) {
		productosAll = productoService.getByAll();
	}
	return productosAll;
}

public String guardarLiberacion(){
	for(BodegueroVo v: cajerosList){
		
		OpcionUsuario cuadre = v.getOpcionCuadre();
		if(v.getLiberarCuadre()!=null){
			System.out.println("liberar: "+v.getLiberarCuadre());
			//cuadre.setEstado(v.getLiberarCuadre()==Boolean.TRUE?1l:0l);
			cuadre.setLiberarCuadre(v.getLiberarCuadre()==Boolean.TRUE?1l:0l);
		}else{
			cuadre.setEstado(0l);
		}
		if(cuadre.getOpcionUsuarioId()==null){
			opcionUsuarioService.save(cuadre);
		}else{
			opcionUsuarioService.update(cuadre);
		}
		
	}
	return "";
}

public List<BodegueroVo> getCajerosList() {
	cajerosList=new ArrayList<>();
	List<Usuario> usuarios=usuarioService.getByRol(2l);//se trae todos los cajeros
	EntregaMercancia entregaMercancia = new EntregaMercancia();
	for(Usuario u: usuarios){
		OpcionUsuario cuadre = opcionUsuarioService.getbySubMenuAndUsuario(u,15l);//se pregunta si tiene activo el cuadre de caja
		BodegueroVo usuario = new BodegueroVo();
		usuario.setNombreBodeguero(u.getNombre()+u.getApellido());
		entregaMercancia.setUsuarioId(u);
		usuario.setEntregaMercancia(entregaMercancia);
		if(cuadre!=null && cuadre.getLiberarCuadre()!=null){
				usuario.setLiberarCuadre((cuadre.getLiberarCuadre()==0l?Boolean.FALSE:Boolean.TRUE));	
		}else{
			usuario.setLiberarCuadre(Boolean.FALSE);
			if(cuadre==null){
				cuadre= new OpcionUsuario();
				cuadre.setEstado(1l);
				cuadre.setFechaActualiza(new Date());
				cuadre.setFechaRegistro(new Date());
				cuadre.setLiberarCuadre(0l);
				cuadre.setRuta("OPCION");
				SubMenu sub=new SubMenu();
				sub.setSubMenuId(15l);
				cuadre.setSubMenuId(sub);
				cuadre.setUsuarioId(u);
			}
			cuadre.setLiberarCuadre(0l);
		}
		
		usuario.setOpcionCuadre(cuadre);
		cajerosList.add(usuario);
		//System.out.println("cuadre activo:"+(cuadre==null?Boolean.FALSE:Boolean.TRUE));
	}
	return cajerosList;
}



public void setCajerosList(List<BodegueroVo> cajerosList) {
	this.cajerosList = cajerosList;
}

public Double getPropinas() {
List<DocumentoDetalle> documentos = new ArrayList<>();
	
	Long tipoDocumentoId= 10l;//bisqieda qie vales
	Date hoy = Calculos.fechaInicial(new Date());
	Date hoyfin = Calculos.fechaFinal(new Date());
	Boolean conCierre=Boolean.TRUE;
	Long server=1l;
	Usuario usuario = (Usuario) sessionMap.get("userLogin");
	documentos= documentoDetalleService.getPropinasByUsuario(tipoDocumentoId, hoy, hoyfin,usuario.getUsuarioId(),conCierre,server);
	Double total=0.0;
	for (DocumentoDetalle a : documentos) {
		if (a.getParcial() != null) {
			total = total + a.getParcial();
		}
	}
	propinas=total;
	return propinas;
	
	
}

public void setPropinas(Double propinas) {
	this.propinas = propinas;
}




}
