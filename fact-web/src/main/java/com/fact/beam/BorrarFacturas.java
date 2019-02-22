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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import com.fact.api.Calculos;
import com.fact.api.FactException;
import com.fact.api.Impresion;
import com.fact.model.Cliente;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.InfoDiario;
import com.fact.model.OpcionUsuario;
import com.fact.model.ProductoEmpresa;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoEvento;
import com.fact.model.Usuario;
import com.fact.service.ClienteService;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.ProductoEmpresaService;
import com.fact.service.TipoDocumentoService;
import com.fact.service.UsuarioService;
import com.fact.vo.DocumentoDetalleVo;
import com.fact.vo.DocumentoVo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean
@SessionScoped
public class BorrarFacturas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2141454443696640336L;
	private static Logger log = Logger.getLogger(BorrarFacturas.class);

	@EJB
	private DocumentoService documentoService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;

	@EJB
	private UsuarioService usuarioService;

	@EJB
	private ClienteService clienteService;

	@EJB
	private EventoService eventoService;
	
	@EJB
	private ProductoEmpresaService productoEmpresaService;
	
	@EJB
	private TipoDocumentoService tipoDocumentoService;

	List<DocumentoVo> documentosVo = new ArrayList<>();
	List<DocumentoVo> documentosVoSelect = new ArrayList<>();

	// busqueda de facturas
	Long usuarioSelect;
	List<Usuario> usuarioList;
	List<Documento> documentosCliente;
	List<DocumentoDetalle> documentoDetallesList;
	List<Cliente> clienteList;
	String documentoId;
	Date fechaIni;
	Date fechaFin;
	String ConDian;
	Documento docuConsulta;
	Long cliente;
	Long tipoDocumento;
	List<TipoDocumento> tipoDocumentoList;

	private DocumentoVo documentoSelect;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}

	public void imprimirPantalla(DocumentoVo rvo) throws DocumentException, IOException, PrinterException {
		documentoSelect = rvo;
		imprimirFactura();

	}
	


	public String imprimirFactura() throws DocumentException, IOException, PrinterException {
		log.info("entra a imprimir");
		documentoSelect.getDocumentoId().setImpreso(1l);
		documentoService.update(documentoSelect.getDocumentoId(), 1l);
		Empresa e =getEmpresa();
		String pdf = "";
		String imp = e.getImpresion().toUpperCase();
		if (imp.equals("TXT")) {
			pdf = imprimirTxt();
		} else {
			pdf = "factura_" + documentoSelect.getDocumentoId() + ".pdf";
			FileOutputStream archivo = new FileOutputStream("C:\\facturacion\\" + pdf);
			Document documento = new Document();
			float fntSize, lineSpacing;
			fntSize = 9f;
			lineSpacing = 10f;
			PdfWriter.getInstance(documento, archivo);
			documento.setMargins(1, 1, 1, 1);
			documento.open();
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "        >>" + e.getNombre() + "<<",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // NOMBRE
																			// EMPRESA
			documento.add(new Paragraph(new Phrase(lineSpacing, "           " + e.getRepresentante() + "         ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(
					new Paragraph(new Phrase(lineSpacing, "      NIT. " + e.getNit() + "   " + e.getRegimen() + "   ",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // NIT
			documento.add(new Paragraph(new Phrase(lineSpacing, "          " + e.getDireccion() + "           ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "           " + e.getBarrio() + "            ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // barrio
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "  	 	        " + e.getCiudad() + "- " + e.getDepartamento() + "		 ",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // ciudad
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "             TEL: " + e.getTelefonoFijo() + " - " + e.getNombre() + "    ",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // tel
			documento.add(new Paragraph(new Phrase(lineSpacing,
					"  FACTURA DE VANTA:    " + documentoSelect.getDocumentoId().getConsecutivoDian(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // numer
																			// de
																			// factura
			documento.add(
					new Paragraph(new Phrase(lineSpacing, "   " + documentoSelect.getDocumentoId().getFechaRegistro(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // fecha
			documento.add(new Paragraph(new Phrase(lineSpacing,
					"CAJERO: " + documentoSelect.getDocumentoId().getUsuarioId().getUsuarioId() + " "
							+ documentoSelect.getDocumentoId().getUsuarioId().getNombre() + " "
							+ documentoSelect.getDocumentoId().getUsuarioId().getApellido(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Descripción        CANT  UNIDAD  TOTAL",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			List<DocumentoDetalle> detalles = documentoDetalleService
					.getByDocumento(documentoSelect.getDocumentoId().getDocumentoId(), 1l);
			for (DocumentoDetalle ddV : detalles) {
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"" + ddV.getProductoId().getNombre() + "     " + ddV.getCantidad() + " "
								+ ddV.getProductoId().getCostoPublico() + " "
								+ (ddV.getProductoId().getCostoPublico() * ddV.getCantidad()),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																				// LEGAL
			}
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Valor Exento:          " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Valor Gravado:         " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Iva:                   " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "TOTAL A PAGAR:         " + documentoSelect.getDocumentoId().getTotal(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																					// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "---------------------------------------------"))); // REPRESENTANTE
																													// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "         **** FORMA DE MAGO****        ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			// Long pago= getValorTargeta()==null?0l:getValorTargeta();
			documento.add(new Paragraph(new Phrase(lineSpacing, "Vr. Pago con Targeta:  " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Vr. Comisión Targeta:  " + 0l,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "Vr. Total Factura:     " + documentoSelect.getDocumentoId().getTotal(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																					// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Efectivo:			     " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Cambio:			     " + 0,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "  *****GRACIAS POR SU COMPRA*****      ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "             Software                  ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "   NICESOFT Cel 3185222474-3112864974  ",
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // REPRESENTANTE
																			// LEGAL
			documento.close();
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Factura Marcada como impresa exitosamente"));

		// falta auto incrementable dian
		return "";
	}

	public String imprimirTxt() throws IOException {
		log.info("entra a imprimir");
		Empresa e = getEmpresa();
		String pdf = "factura_" + documentoSelect.getDocumentoId() + ".txt";
		File archivo = new File("C:\\facturacion\\" + pdf);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("---------------------------------------\n");
		bw.write("         >>" + e.getNombre() + "<<\n");
		bw.write("           " + e.getRepresentante() + "         \n");
		bw.write("      NIT. " + e.getNit() + "   " + e.getRegimen() + "   \n");
		bw.write("          " + e.getDireccion() + "           \n");
		bw.write("           " + e.getBarrio() + "            \n");
		bw.write("  	 	   " + e.getCiudad() + "-" + e.getDepartamento() + "	        \n");
		bw.write("             TEL: " + e.getTelefonoFijo() + "             \n");
		bw.write("FACTURA DE VANTA:    " + documentoSelect.getDocumentoId().getConsecutivoDian());
		bw.write("\n" + documentoSelect.getDocumentoId().getFechaRegistro());
		bw.write("\nCAJERO: " + documentoSelect.getDocumentoId().getUsuarioId().getUsuarioId() + " "
				+ documentoSelect.getDocumentoId().getUsuarioId().getNombre() + " "
				+ documentoSelect.getDocumentoId().getUsuarioId().getApellido());
		bw.write("\n-----------------------------------\n");
		bw.write("Descripción        CANT  UNIDAD  TOTAL\n");
		bw.write("----------------------------------------");
		List<DocumentoDetalle> detalles = documentoDetalleService
				.getByDocumento(documentoSelect.getDocumentoId().getDocumentoId(), 1l);
		for (DocumentoDetalle ddV : detalles) {
			bw.write("\n" + ddV.getProductoId().getNombre().trim() + "     " + ddV.getCantidad() + " "
					+ ddV.getProductoId().getCostoPublico() + " "
					+ (ddV.getProductoId().getCostoPublico() * ddV.getCantidad()));
		}
		bw.write("\n---------------------------------------");
		bw.write("\nValor Exento:          " + documentoSelect.getDocumentoId().getExcento());
		bw.write("\nValor Gravado:         " + documentoSelect.getDocumentoId().getGravado());
		bw.write("\nIva:                   " + documentoSelect.getDocumentoId().getIva());
		bw.write("\n---------------------------------------");
		bw.write("\nTOTAL A PAGAR:         " + documentoSelect.getDocumentoId().getTotal());
		bw.write("\n---------------------------------------\n");
		bw.write("         **** FORMA DE PAGO****        ");
		// Long pago= getValorTargeta()==null?0l:getValorTargeta();
		bw.write("\nVr. Pago con Targeta:  " + 0);
		bw.write("\nVr. Comisión Targeta:  " + 0l);
		bw.write("\nVr. Total Factura:     " + documentoSelect.getDocumentoId().getTotal());
		bw.write("\nEfectivo:			" + 0);
		bw.write("\nCambio:			     " + 0);
		bw.write("\n  *****GRACIAS POR SU COMPRA*****   \n");
		bw.write("             Software               \n");
		bw.write("   NICESOFT Cel 3185222474-3112864974  ");
		bw.write("\n");
		bw.write("\n");
		bw.write("\n");
		bw.write(" \n");
		bw.close();
		return pdf;
	}

	private Usuario usuario() {
		Usuario yourVariable = (Usuario) sessionMap.get("userLogin");
		return yourVariable;
	}

	private Configuracion configuracion() {
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}

	private String impresora(String impresora) {
		return (String) sessionMap.get("impresora"+impresora);
	}

	public void barrarTodas() {
		log.info("borrar_todas");
		long server = 1;
		for (DocumentoVo dvoss : getDocumentosVoSelect()) {
			List<DocumentoDetalle> dd = documentoDetalleService.getByDocumento(dvoss.getDocumentoId().getDocumentoId(),
					1l);
			for (DocumentoDetalle dob : dd) {
				dob.setEstado(0l);
				documentoDetalleService.update(dob, server);
			}
			dvoss.getDocumentoId().setCierreDiario(1l);
			documentoService.update(dvoss.getDocumentoId(), server);
		}

		RequestContext.getCurrentInstance().execute("PF('borrar_todas').hide();");
		RequestContext.getCurrentInstance().execute("document.getElementById('opciones:Sig_movi_mes1').focus();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Facturas Borradas exitosamente"));
	}

	public List<DocumentoVo> getDocumentosVo() throws ParseException {
		List<DocumentoVo> doVos = new ArrayList<>();
		List<Documento> d = new ArrayList<>();
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		tipoDocumentoId.add(4l); // tipo documento cotizacion
		tipoDocumentoId.add(9l); // numero de guia
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		d = documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId, 1l);
		for (Documento docu : d) {
			DocumentoVo dvo = new DocumentoVo();
			dvo.setDocumentoId(docu);
			dvo.setDocumentoId2(docu.getDocumentoId());
			dvo.setBorrar(Boolean.FALSE);
			doVos.add(dvo);
		}
		documentosVo = doVos;
		return documentosVo;
	}

	public void buscarFacturas() {		
		Date hoy; 
		Date hoyfin;
		try {
			hoy = Calculos.fechaInicial(getFechaIni());
			hoyfin = Calculos.fechaFinal(getFechaFin());
		} catch (Exception e) {
			hoy = null;
			hoyfin = null;
		}
		setDocumentosCliente(new ArrayList<>());
		setDocumentosCliente(documentoService.buscarPorFechaAndCajero(getUsuarioSelect(), getDocumentoId(), hoy, hoyfin,
				getConDian(), getCliente(),getTipoDocumento()));
	}

	public void consultarDetalle(Documento docu) {
		setDocumentoDetallesList(documentoDetalleService.getByDocumento(docu.getDocumentoId(), 1l));
		setDocuConsulta(docu);
		RequestContext.getCurrentInstance().execute("PF('consultarDetalle').show();");

	}
	
	public String anularFactura(Documento docu) {
		if(docu.getAnulado()!=null && docu.getAnulado()==1) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Factura "+docu.getConsecutivoDian()+"ya ha sido anulada anteriormente"));
			return "";
		}
		log.info("entra a anular factura");
		log.info("Documento:" + docu.getDocumentoId());
		List<DocumentoDetalle> productos = documentoDetalleService.getByDocumento(docu.getDocumentoId(), 1l); 
		anularDetalles(productos);
		calcularInfoDiario(docu);
		docu.setAnulado(1l);
		documentoService.update(docu, 1l);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Factura "+docu.getConsecutivoDian()+" anulada exitosamente"));
		return "";
	}
	
	private void anularDetalles(List<DocumentoDetalle> productos) {	
			for(DocumentoDetalle d:productos) {
				try {				
					ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(),
							d.getProductoId().getProductoId());
					Double cantidad1 = productoEmpresa.getCantidad() + d.getCantidad();
					productoEmpresa.setCantidad(cantidad1);
					productoEmpresaService.update(productoEmpresa);

				} catch (Exception e) {
					log.error("!!error anulando el producto:" + d.getProductoId().getProductoId());
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error anulando Productos"));
				}				
				log.info("documentoDetalle anulado:" + d.getDocumentoDetalleId());
			}
	}
	
	private void calcularInfoDiario(Documento documento) {
		Date fechaDocumento = documento.getFechaRegistro();
		Date fechaInicio = Calculos.fechaInicial(fechaDocumento);
		Date fechaFinal = Calculos.fechaFinal(fechaDocumento);
		List<InfoDiario> infoList = documentoService.buscarInfodiarioByFecha(fechaInicio, fechaFinal);
		boolean anulado = true;
		try {
			InfoDiario infoDiario = Calculos.calcularInfoDiario(documento, infoList, getEmpresa(), anulado);

			if (infoDiario.getInfoDiarioId() == null) {
				documentoService.save(infoDiario);
			} else {
				documentoService.update(infoDiario);
			}

		} catch (FactException e1) {
			log.error("Error calculando registro de informe diario" + e1.getMessage());
		}
	}

	public String imprimirFactura(Documento docu)
			throws DocumentException, IOException, PrinterException, PrintException {
		// colocar la ruta de la imagen principal de la empresa
		// 77 acabar de cambiar los campos en la empresion de recibos txt y pdf
		log.info("entra a imprimir");
		log.info("Documento:" + docu.getDocumentoId());
		Configuracion configuracion = configuracion();
		String impresora = impresora("1");
		String enPantalla = "false"; 
		Empresa e = getEmpresa();
		String tituloFactura = "";
		docu.setImpreso(1l);
		// se agrega evento copia factura
		Evento evento = new Evento();
		TipoEvento tipoEvento = new TipoEvento();
		tipoEvento.setTipoEventoId(4l); // se asigna tipo evento igual a copia
										// factura
		evento.setTipoEventoId(tipoEvento);
		evento.setUsuarioId(usuario());
		evento.setFechaRegistro(new Date());
		evento.setCampo("" + docu.getConsecutivoDian());
		eventoService.save(evento);
		String tipoDocu=docu.getTipoDocumentoId().getTipoDocumentoId().toString();
		switch (tipoDocu) {
		case "9":
			log.info("consecutivo documentoId: " + docu.getDocumentoId());
			tituloFactura = "No. DE GUIA";
			break;
		case "10":			
			tituloFactura = "FACTURA DE VENTA";
			break;
		case "4":
			tituloFactura = "No. DE COTIZACIÓN";
			break;
		default:
			break;
		}
		List<DocumentoDetalle> detalles = documentoDetalleService.getByDocumento(docu.getDocumentoId(), 1l);
		
		String pdf = "";
		String imp = e.getImpresion().toUpperCase();
		switch (imp) {
		case "TXT":
			//si el tipo documento es igual a entrada o salida de almacen
			if("1".equals(tipoDocu) ||"2".equals(tipoDocu) ||"6".equals(tipoDocu)) {
				 Impresion.imprimirEntadaAlmacenTXT(docu, Calculos.llenarDocumentoDetalleVoList(detalles), configuracion, impresora, e);
			}else {
				pdf = Impresion.imprimirTxt(docu, Calculos.llenarDocumentoDetalleVoList(detalles), docu.getUsuarioId(),
						configuracion, impresora,enPantalla,e);
			}
			
			break;
		case "BIG":
			pdf = imprimirTemporal(tituloFactura, docu);
			// pdf = imprimirBig(tituloFactura);
			break;
		case "PDF":
			pdf = Impresion.imprimirPDF(docu, Calculos.llenarDocumentoDetalleVoList(detalles), docu.getUsuarioId(),
					configuracion, impresora,enPantalla,e);
			break;
		case "SMALL_PDF":
			Impresion.imprimirPDFSmall(docu, Calculos.llenarDocumentoDetalleVoList(detalles), usuario(), configuracion,
					impresora, enPantalla,e);
			break;
		case "BIG_PDF":
			List<DocumentoDetalle> detalles1 = documentoDetalleService.getByDocumento(docu.getDocumentoId(), 1l);
			OpcionUsuario descuentoEnFactura = (OpcionUsuario) sessionMap.get("descuentoEnFactura");
			Impresion.imprimirBig(docu, Calculos.llenarDocumentoDetalleVoList(detalles1), docu.getUsuarioId(),
					configuracion, descuentoEnFactura, impresora,e);
			break;
		case "PDF_PAGE":
			pdf = Impresion.imprimirPDFPage(docu, Calculos.llenarDocumentoDetalleVoList(detalles), docu.getUsuarioId(),
					configuracion, impresora,enPantalla,e);
			break;
			
		default:
			log.info("imprime por defecto en pdf");
			pdf = Impresion.imprimirPDF(docu, Calculos.llenarDocumentoDetalleVoList(detalles), docu.getUsuarioId(),
					configuracion, impresora,enPantalla,e);
			break;
		}

		log.info("todo el codigo de imprimir");
		return pdf;
	}

	private String imprimirTemporal(String tituloFactura, Documento docu) throws IOException {
		log.info("entra a imprimir");
		Empresa e = getEmpresa();
		String pdf = "C:\\facturas\\factura_" + docu.getDocumentoId() + "Copia.txt";
		File archivo = new File(pdf);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyIni = df.format(docu.getFechaRegistro());
		BufferedWriter bw;
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("                                        \n");
		bw.write("                                        \n");
		bw.write("                                                                    " + docu.getConsecutivoDian()
				+ "\n");
		bw.write("                                        \n");
		bw.write("         " + docu.getClienteId().getNombre() + "                       " + e.getResolucionDian()
				+ "   " + fhoyIni + "\n");
		bw.write("         " + docu.getClienteId().getDocumento() + "                            "
				+ e.getAutorizacionDesde() + "  " + e.getAutorizacionHasta() + e.getFechaResolucion() + "\n");
		bw.write("         " + docu.getUsuarioId().getUsuarioId() + " " + docu.getUsuarioId().getNombre() + " "
				+ docu.getUsuarioId().getApellido());
		bw.write("\n");
		bw.write("\n");
		int tope = 16;
		int pagina = 0;
		int numProductos = tope;
		List<DocumentoDetalle> productos = documentoDetalleService.getByDocumento(docu.getDocumentoId(), 1l);
		productos = ordemar(productos);
		for (DocumentoDetalle ddV : productos) {
			String nombreProducto = "";
			String cantidadProducto = "";
			String unidadProducto = "";
			Double total = 0.0;
			String totalProducto = "";
			int tamaño = 0;
			Double des = (docu.getDescuento() == null ? 0 : docu.getDescuento()) / 100;
			Double ivaTem = ddV.getProductoId().getIva() == null ? 0.0 : ddV.getProductoId().getIva();
			try {
				nombreProducto = ddV.getProductoId().getNombre().trim().substring(0, 34);
			} catch (Exception e2) {
				nombreProducto = ddV.getProductoId().getNombre().trim();
				tamaño = nombreProducto.length();
			}
			try {
				cantidadProducto = "" + ddV.getCantidad().toString().substring(0, 5);
			} catch (Exception e2) {
				cantidadProducto = "" + ddV.getCantidad();
			}
			cantidadProducto = cantidadProducto.replace(".0", "  ");
			Double uniProductoTemp = ddV.getProductoId().getCostoPublico()
					+ (ddV.getProductoId().getCostoPublico() * des);
			try {
				unidadProducto = "" + formatea.format(uniProductoTemp).substring(0, 7);
			} catch (Exception e2) {
				unidadProducto = formatea.format(uniProductoTemp);
			}
			total = (uniProductoTemp * ddV.getCantidad());
			try {
				total = (uniProductoTemp * ddV.getCantidad());
				totalProducto = "" + formatea.format(total).substring(0, 7);
			} catch (Exception e2) {
				total = (uniProductoTemp * ddV.getCantidad());
				totalProducto = formatea.format(total);
				;
			}
			if (numProductos > 0) {
				bw.write("\n");
				String varios = ddV.getProductoId().getVarios() == 1l ? "V" : " ";
				bw.write("   (" + varios + ")  " + cantidadProducto + "   " + nombreProducto);
				if (tamaño != 0) {
					for (int j = tamaño; j < 34; j++) {
						bw.write(" ");
					}
				}
				bw.write(" " + unidadProducto + "         " + totalProducto + "    " + (ivaTem));
			} else {
				pagina++;
				String totalpuntos = formatea.format(totalXpagina(pagina, tope, productos));
				bw.write("\n          " + formatea.format(docu.getExcento()) + "               "
						+ formatea.format(docu.getGravado()) + "             " + formatea.format(docu.getIva())
						+ "           " + totalpuntos);
				bw.write("\n");
				bw.write(" \n      " + formatea.format(docu.getPesoTotal()) + " Kgs");
				bw.write("\nCONTINUA....\n ");
				bw.write("\n ");
				bw.write("\n ");
				bw.write("\n ");
				bw.write("\n .");
				bw.write("                                        \n");
				bw.write("                                        \n");
				bw.write("                                                                    "
						+ docu.getConsecutivoDian() + "\n");
				bw.write("                                        \n");
				bw.write("         " + docu.getClienteId().getNombre() + "                       "
						+ e.getResolucionDian() + "    " + e.getFechaResolucion() + "\n");
				bw.write("         " + docu.getClienteId().getDocumento() + "                            "
						+ e.getAutorizacionDesde() + "  " + e.getAutorizacionHasta() + "\n");
				bw.write("         " + docu.getUsuarioId().getUsuarioId() + " " + docu.getUsuarioId().getNombre() + " "
						+ docu.getUsuarioId().getApellido());
				bw.write("\n");
				bw.write("\n");
				bw.write("\n");
				String varios = ddV.getProductoId().getVarios() == 1l ? "V" : " ";
				bw.write("  (" + varios + ")  " + cantidadProducto + "    " + nombreProducto);
				if (tamaño != 0) {
					for (int j = tamaño; j < 34; j++) {
						bw.write(" ");
					}
				}
				bw.write(" " + unidadProducto + "         " + totalProducto + "    " + (ivaTem));
				numProductos = tope;
			}

			numProductos--;
		}
		for (int i = 1; i < numProductos; i++) {
			bw.write("\n");
		}
		String totalpuntos = formatea.format(docu.getTotal());
		bw.write("\n");
		bw.write("\n          " + formatea.format(docu.getExcento()) + "               "
				+ formatea.format(docu.getGravado()) + "             " + formatea.format(docu.getIva()) + "           "
				+ totalpuntos);
		bw.write("\n");
		bw.write(" \n      " + formatea.format(docu.getPesoTotal()) + " Kgs");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n .");
		bw.close();

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(pdf);
			log.info(pdf);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		if (inputStream == null) {
			// return;
		}
		DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc document = new SimpleDoc(inputStream, docFormat, null);
		PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
		PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

		String impresara = impresora("1");
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		log.info("Number of printers configured: " + printServices.length);
		for (PrintService printer : printServices) {
			log.info("Printer:" + printer.getName());
			if (printer.getName().equals(impresara)) {
				log.info("Comparacion:" + printer.getName() + ":" + impresara);
				defaultPrintService = printer;
			}
		}
		if (defaultPrintService != null) {
			DocPrintJob printJob = defaultPrintService.createPrintJob();
			try {
				printJob.print(document, attributeSet);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			log.error("No existen impresoras instaladas");
		}
		return pdf;
	}

	private Double totalXpagina(int pagina, int tope, List<DocumentoDetalle> productos) {
		Double totalPagina = 0.0;
		for (int i = tope * (pagina - 1); i < tope * (pagina - 1) + tope; i++) {
			totalPagina = productos.get(i).getParcial() + totalPagina;
		}
		return totalPagina;
	}

	public List<DocumentoDetalle> ordemar(List<DocumentoDetalle> productos) {
		List<DocumentoDetalle> temp = new ArrayList<>();
		for (DocumentoDetalle ddV : productos) {
			if (ddV.getProductoId().getVarios() != 1) {
				temp.add(ddV);
			}
		}
		for (DocumentoDetalle ddV : productos) {
			if (!temp.contains(ddV)) {
				temp.add(ddV);
			}
		}

		return temp;
	}

	public List<DocumentoDetalle> getDocumentoDetallesList() {
		return documentoDetallesList;
	}

	public void setDocumentoDetallesList(List<DocumentoDetalle> documentoDetallesList) {
		this.documentoDetallesList = documentoDetallesList;
	}

	public void setDocumentosVo(List<DocumentoVo> documentosVo) {
		this.documentosVo = documentosVo;
	}

	public List<DocumentoVo> getDocumentosVoSelect() {
		return documentosVoSelect;
	}

	public void setDocumentosVoSelect(List<DocumentoVo> documentosVoSelect) {
		this.documentosVoSelect = documentosVoSelect;
	}

	public Long getUsuarioSelect() {
		return usuarioSelect;
	}

	public void setUsuarioSelect(Long usuarioSelect) {
		this.usuarioSelect = usuarioSelect;
	}

	public List<Usuario> getUsuarioList() {
		if (usuarioList == null) {
			usuarioList = usuarioService.getByAll();
		}
		return usuarioList;
	}

	public void setUsuarioList(List<Usuario> usuarioList) {
		this.usuarioList = usuarioList;
	}

	public String getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(String documentoId) {
		this.documentoId = documentoId;
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

	public String getConDian() {
		return ConDian;
	}

	public void setConDian(String conDian) {
		ConDian = conDian;
	}

	public List<Documento> getDocumentosCliente() {
		return documentosCliente;
	}

	public void setDocumentosCliente(List<Documento> documentosCliente) {
		this.documentosCliente = documentosCliente;
	}

	public Documento getDocuConsulta() {
		return docuConsulta;
	}

	public void setDocuConsulta(Documento docuConsulta) {
		this.docuConsulta = docuConsulta;
	}

	public List<Cliente> getClienteList() {
		if (clienteList == null) {
			clienteList = clienteService.getByAll();
		}
		return clienteList;
	}

	public void setClienteList(List<Cliente> clienteList) {
		this.clienteList = clienteList;
	}

	public Long getCliente() {
		return cliente;
	}

	public void setCliente(Long cliente) {
		this.cliente = cliente;
	}
	
	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<TipoDocumento> getTipoDocumentoList() {
		if(tipoDocumentoList==null) {
			List<Long> tipos = new ArrayList<>();
			tipos.add(2l);
			tipos.add(3l);
			tipos.add(4l);
			tipos.add(5l);
			tipos.add(6l);
			tipos.add(8l);
			tipos.add(10l);
			tipoDocumentoList= tipoDocumentoService.getById(tipos);
		}
		return tipoDocumentoList;
	}

	public void setTipoDocumentoList(List<TipoDocumento> tipoDocumentoList) {
		this.tipoDocumentoList = tipoDocumentoList;
	}
	
	

}
