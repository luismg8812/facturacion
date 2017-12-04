package com.fact.beam;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.transform.ToListResultTransformer;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.api.Calculos;
import com.fact.api.Impresion;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.DocumentoReducido;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.InfoDiario;
import com.fact.model.TipoEvento;
import com.fact.model.Usuario;
import com.fact.service.AbonoService;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.InformeDiarioService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoEventoService;
import com.fact.service.TipoPagoService;
import com.fact.service.UsuarioService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean
@SessionScoped
public class reduccion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private ProveedorService proveedorService;
	@EJB
	private DocumentoService documentoService;
	@EJB
	private AbonoService abonoService;
	@EJB
	private TipoPagoService tipoPagoService;
	@EJB
	private UsuarioService usuarioService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;

	@EJB
	private TipoEventoService tipoEventoService;

	@EJB
	private EventoService eventoService;
	@EJB
	private InformeDiarioService informeDiarioService;

	private Long reduccion;
	private Date fechaInicio;
	private Date fechafin;

	private Double totalDia=0.0;
	private Date fechaHoy;
	private Usuario usuarioDia;
	public Empresa empresa;
	private Double ivaTotal=0.0;
	private Double iva19=0.0;
	private Double iva5=0.0;
	private Double base5=0.0;
	private Double base19=0.0;
	private Double execento=0.0;
	//entradas
	private Double ivaTotalEntrada=0.0;
	private Double iva19Entrada=0.0;
	private Double iva5Entrada=0.0;
	private Double base5Entrada=0.0;
	private Double base19Entrada=0.0;
	private Double execentoEntrada=0.0;
	//
	private String primeraFactura;
	private String ultimaFactura;
	private Double costoTotal=0.0;
	private Double numeroDocumentos;
	private Double numeroDocumentosMac;
	
	private Double total;
	private Double totalIva;
	private Double totalIva5;
	private Double totalIva19;
	private Double totalbase5;
	private Double totalbase19;
	private Double totalExcento;
	
	//total entradas
	private Double totalEntrada;
	private Double totalIvaEntrada;
	private Double totalIva5Entrada;
	private Double totalIva19Entrada;
	private Double totalbase5Entrada;
	private Double totalbase19Entrada;
	private Double totalExcentoEntrada;
	//
	
	//total diferencia
		private Double totalDiferencia=0.0;
		private Double totalIvaDiferencia=0.0;
		private Double totalIva5Diferencia=0.0;
		private Double totalIva19Diferencia=0.0;
		private Double totalbase5Diferencia=0.0;
		private Double totalbase19Diferencia=0.0;
		private Double totalExcentoDiferencia=0.0;
		//
	private List<InfoDiario> reduccionList;
	private List<InfoDiario> reduccionEntradasList;
	private List<DocumentoReducido> documentosReduccionList;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	public void imprimirinforme(InfoDiario id) throws DocumentException, IOException, PrinterException, ParseException {
		Empresa e = Login.getEmpresaLogin();
		String imp = e.getImpresion().toUpperCase();
		int numeroImpresiones=1;
		for (int i = 0; i < numeroImpresiones; i++) {
			switch (imp) {
			case "TXT":
				 imprimirBig(id);
				break;
			case "BIG":
				 imprimirBig(id);
				break;
			case "PDF":
				imprimirInfoPDF(id);
				break;
			case "BIG_PDF":
				imprimirInfoPDF(id);
				break;
				
			default:
				break;
			}
		}
		
		// imprimirTXT(id);
		
	}
	
	public void imprimirinformePropietario(InfoDiario id) throws DocumentException, IOException, PrinterException, ParseException {
		Empresa e = Login.getEmpresaLogin();
		String imp = e.getImpresion().toUpperCase();
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
		String numeroInforme = informeDiarioService.consecutivoInformePropietario();
		List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo
		// los
		// cajeros
		int numeroImpresiones=1;
		for (int i = 0; i < numeroImpresiones; i++) {
			switch (imp) {
			case "TXT":
				 //imprimirBig(id);
				break;
			case "BIG":
				 //imprimirBig(id);
				break;
			case "PDF":
				imprimirInfoPDF(id);
				break;
			case "BIG_PDF":
				imprimirInformePropietarioPDF(id,numeroInforme,usuario,configuracion,uList);
				break;
				
			default:
				break;
			}
		}
			
	}
	
	public  String imprimirInformePropietarioPDF(InfoDiario id, String numeroImforme, Usuario usuario,
			Configuracion config, List<Usuario> uList) throws DocumentException, FileNotFoundException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(id.getFechaInforme());
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		String carpeta = "C:\\facturas\\infoDiario";
		String pdf = "\\informePropietario_" + numeroImforme + ".pdf";
		File folder = new File(carpeta);
		folder.mkdirs();
		FileOutputStream archivo = new FileOutputStream(carpeta + pdf);
		Document documento = new Document();
		float fntSize = 11f, lineSpacing = 15f;
		PdfWriter.getInstance(documento, archivo);
		
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		Date hoy = Calculos.fechaInicial(id.getFechaInforme());
		Date hoyfin = Calculos.fechaFinal(id.getFechaInforme());
		Double totalVenasTemp = 0.0,totalFacturasTemp=0.0,totalVentasTemp=0.0,totalRemisionesTemp=0.0, totalFajosTemp=0.0, totalEfectivoTemp=0.0,totalVentasDia=0.0,totalRemisionesDia=0.0;
				
		documento.setMargins(10, 10, 20, 20);
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, "RESUMEN N°: " + numeroImforme + "     FECHA: " + fhoyIni,FontFactory.getFont(FontFactory.COURIER, 20f)))); // numero de informe y fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,"______________________________________________________________________________",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing, "CAJERO          FACT      VENTAS  REMISIONES      FAJOS   EFECTIVO DIFERENCIA",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,"______________________________________________________________________________",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		for (Usuario u : uList) {
			Double VentasTemp = 0.0;
			Double RemisionesTemp = 0.0;
			List<Documento> factDia = documentoService.getByfacturasReales(tipoDocumentoId, hoy, hoyfin,u.getUsuarioId(), Boolean.FALSE, 1l);
			List<Documento> remisiList = documentoService.getRemisionesByUsuario(9l, hoy, hoyfin, u.getUsuarioId(), Boolean.FALSE, config.getServer());
			totalFacturasTemp+=factDia.size();
			String nombreCajero = Calculos.cortarDescripcion(u.getNombre()+" "+u.getApellido(), 15);
			String cantidadFacturas = Calculos.cortarCantidades(""+factDia.size(), 4);
			for(Documento d: factDia){
				VentasTemp+=d.getTotal();
				totalVenasTemp+=d.getTotal();
			}
			for(Documento d: remisiList){
				RemisionesTemp+=d.getTotal();
				totalRemisionesTemp+=d.getTotal();
			}
			String ventas= Calculos.cortarCantidades(formatea.format(VentasTemp), 11);
			String remisiones =Calculos.cortarCantidades(formatea.format(RemisionesTemp), 11);
			String fajos =Calculos.cortarCantidades("0.0", 10);
			String efectivo=Calculos.cortarCantidades("0.0", 10);
			String diferencia=Calculos.cortarCantidades("0.0", 10);
			totalVentasDia = totalVentasTemp+totalVentasDia;
			totalRemisionesDia = totalRemisionesTemp+totalRemisionesDia;
			documento.add(new Paragraph(new Phrase(lineSpacing,nombreCajero+" "+cantidadFacturas+" "+ventas+" "+remisiones+" "+fajos+" "+efectivo+diferencia,FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan		
		}
		String totalVentas = Calculos.cortarCantidades(formatea.format(totalVenasTemp), 11);
		String totalRemisiones = Calculos.cortarCantidades(formatea.format(totalRemisionesTemp), 11);
		String totalFacturas = Calculos.cortarCantidades(""+totalFacturasTemp, 4);
		String totalFajos = Calculos.cortarCantidades(""+totalFajosTemp, 10);
		String totalEfectivo = Calculos.cortarCantidades(""+totalEfectivoTemp, 10);
		documento.add(new Paragraph(new Phrase(lineSpacing,"______________________________________________________________________________",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing, "               "+" "+totalFacturas+" "+totalVentas+" "+totalRemisiones+" "+totalFajos+" "+totalEfectivo,FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,"______________________________________________________________________________",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		Double totalDia = totalVenasTemp+totalRemisionesTemp;
		Double porcentajeVentas = (totalVenasTemp*100)/totalDia;
		Double porcentajeRemisiones=(totalRemisionesTemp*100)/totalDia;
		documento.add(new Paragraph(new Phrase(lineSpacing,"DÍA      : "+formatea.format(porcentajeVentas)+"%     "+ formatea.format(porcentajeRemisiones)+"%",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,"ACUMULADO: "+formatea.format(porcentajeVentas)+"%     "+ formatea.format(porcentajeRemisiones)+"%",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,"______________________________________________________________________________",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,"",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,"REMISIONES",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		for (Usuario u : uList) {
			List<Documento> remisiList = documentoService.getRemisionesByUsuario(9l, hoy, hoyfin, u.getUsuarioId(), Boolean.FALSE, config.getServer());
			for(Documento d: remisiList){
				String nombreCajero = Calculos.cortarDescripcion(u.getNombre()+" "+u.getApellido(), 15);
				String guia = Calculos.cortarCantidades(""+d.getDocumentoId(),6);
				String cliente = Calculos.cortarCantidades(d.getClienteId().getNombre(), 16);
				String valor = Calculos.cortarCantidades(formatea.format(d.getTotal()), 11);
				String descuento = Calculos.cortarCantidades(formatea.format(d.getDescuento()==null?0.0:d.getDescuento()), 11);
				Double desc = ((d.getDescuento()==null?0.0:d.getDescuento())*100)/d.getTotal();
				String porcentaje = Calculos.cortarCantidades(""+desc,7);
				documento.add(new Paragraph(new Phrase(lineSpacing,""+nombreCajero+" "+"N° GUIA CLIENTE               VALOR   DESCUENTO   %DESC",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
				documento.add(new Paragraph(new Phrase(lineSpacing,"                "+guia+" "+cliente+" "+valor+" "+descuento+" "+porcentaje,FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			}		
		}
		List<TipoEvento> tiposEventos = tipoEventoService.getByAll();
		int maxTamEvento = 22;
		for (TipoEvento tipo : tiposEventos) {
			// Empresa tipos;
			documento.add(new Paragraph(new Phrase(lineSpacing,"",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(new Paragraph(new Phrase(lineSpacing," ",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(new Paragraph(new Phrase(lineSpacing,  tipo.getNombre(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			List<Evento> evento = eventoService.getByFechaAndTipo(Calculos.fechaInicial(id.getFechaInforme()),
					Calculos.fechaFinal(id.getFechaInforme()), tipo.getTipoEventoId());
			for (Evento ev : evento) {
				switch ("" + ev.getTipoEventoId().getTipoEventoId()) {
				case "1":						
					documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. devolución..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad devolución.: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario devolución..: " + Calculos.cortarDescripcion(ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan						
					documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "3":
					documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. Cambio preci: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Precio anterior.....: " + ev.getValorAnterior(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Precio actual.......: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario realiza camb: " + Calculos.cortarDescripcion(	ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "2":
					documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. Descuento...: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad Descuento..: " + ev.getValorAnterior(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad sin Descuen: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan	
					documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario devolución..: " + Calculos.cortarDescripcion(ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "4":
					documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario copia factur: " + Calculos.cortarDescripcion(ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Documento: " + ev.getCampo(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan	
					
					
					break;	
				default:
					break;
				}
			}
		}
		documento.add(new Paragraph(new Phrase(lineSpacing,"",FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.close();
		//String impresara = usuario.getImpresora();
		//printer(impresara, folder + pdf, config);
		return "";
	}

	private void imprimirBig(InfoDiario id) throws ParseException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		Boolean conCierre = Boolean.FALSE;
		int maxTamaño = 15;
		Empresa e = Login.getEmpresaLogin();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(id.getFechaInforme());
		String carpeta = "C:\\facturas\\infoDiario";
		String pdf = "\\informeDiario_" + fhoyIni + ".txt";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		BufferedWriter bw;
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		try {
			bw = new BufferedWriter(new FileWriter(folder));
			bw.write("                                        \n");
			bw.write(e.getNombre().toUpperCase() + "\n");
			bw.write("NIT. " + e.getNit() + "\n");
			bw.write("Comprobante de Informe Diario\n");
			bw.write("Fecha: " + df2.format(id.getFechaInforme()) + "\n");
			bw.write("                                        \n");
			bw.write("Identificador del servidor: " + Calculos.conseguirMAC() + "\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("N° Factura Inicial  N° de Factura Final  Cantidad Fac.  Valor Total Facturado\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("" + Calculos.cortarDescripcion(id.getDocumentoInicio(), 18) + "  "
					+ Calculos.cortarDescripcion(id.getDocumentoFin(), 19) + "  "
					+ Calculos.cortarCantidades(id.getCantidadDocumentos(), 13) + "  "
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 21) + "\n");
			bw.write("                                        \n");
			bw.write("Descriminación de ventas atendidas por Computador.\n");
			bw.write("______________________________________________________\n");
			bw.write("Computador            Cant. Fac.  Vr. Total Facturado\n");
			bw.write("______________________________________________________\n");
			// se trae la lista de las mac que facturaron
			List<String> macs = documentoService.getMagList();
			for (String m : macs) {
				numeroDocumentosMac = 0.0;

				try {
					String totalMac = Calculos.cortarCantidades(
							formatea.format(getTotalFaturasMac(id.getFechaInforme(), m, conCierre, 1l)), 19);
					bw.write("" + Calculos.cortarDescripcion(m, 20) + "  "
							+ Calculos.cortarCantidades(numeroDocumentosMac, 10) + "  " + totalMac + "\n");
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			bw.write("______________________________________________________\n");
			bw.write("                      " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 10) + "  "
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 19) + "\n");
			bw.write("                                        \n");
			bw.write("Discriminación de Ventas por forma de Pago\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("Forma de Pago                        Cantidad Facturas         Valor Facturado\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("EFECTIVO                             " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 17)
					+ "" + Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 24) + "\n");
			bw.write("CRÉDITO                                              0                       0\n");
			bw.write("CHEQUES                                              0                       0\n");
			bw.write("TARJETAS DÉBITO                                      0                       0\n");
			bw.write("TARJETAS CRÉDITO                                     0                       0\n");
			bw.write("BONOS                                                0                       0\n");
			bw.write("VALES                                                0                       0\n");
			bw.write("OTROS                                                0                       0\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("                                        \n");
			bw.write("                                        \n");
			if (userPropietario.equals("true")) {
				bw.write("%:..........................: " + id.getPorcReduccion() + "\n");
			}
			bw.write("TOTAL VENTAS:...............: "
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), maxTamaño) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("TOTAL REAL:.................: "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalOriginal()), maxTamaño) + "\n");
			}
			bw.write("IVA TOTAL:..................: "
					+ Calculos.cortarCantidades(formatea.format(id.getIvaReducido()), maxTamaño) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("IVA TOTAL REAL:.............: "
						+ Calculos.cortarCantidades(formatea.format(id.getIvaOriginal()), maxTamaño) + "\n");
			}
			bw.write("COSTO TOTAL:................: "
					+ Calculos.cortarCantidades(formatea.format(id.getCostoReducido()), maxTamaño) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("COSTO REAL:.................: "
						+ Calculos.cortarCantidades(formatea.format(id.getCostoOriginal()), maxTamaño) + "\n");
			}
			if (userPropietario.equals("true")) {
				bw.write("TOTAL REMISIONES:...........: "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalRemisiones()), maxTamaño) + "\n");
			}
			Double redu =  (id.getPorcReduccion() == 0 ? 0.0 : id.getPorcReduccion())/100;
			Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
			Long server = configuracion.getServer();
			List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo
																// los
			// cajeros
			for (Usuario u : uList) {
				bw.write("------------------------------------------------------------------" + "\n");
				bw.write("CAJERO:.....................: " + u.getNombre() + " " + u.getApellido() + "\n");
				bw.write("TOTAL VENTAS x CAJERO:......: " + Calculos.cortarCantidades((getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l) - (getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l) * redu)),maxTamaño) + "\n");
				if (userPropietario.equals("true")) {
					bw.write("TOTAL VENTAS X CAJERO REAL..: " + Calculos.cortarCantidades(
							formatea.format(getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, server)),
							maxTamaño) + "\n");
				}
				if (userPropietario.equals("true")) {
					bw.write("TOTAL REMISIONES X CAJERO...: " + Calculos.cortarCantidades(formatea.format(getTotalRemisionesToDay(id.getFechaInforme(), u, conCierre, server)),maxTamaño) + "\n");
				}
				if (userPropietario.equals("true")) {
					bw.write(
							"TOTAL ADELANTO EFECTI X CAJE: " + Calculos.cortarCantidades(
									formatea.format(
											getTotalAvanceEfectivoToDay(id.getFechaInforme(), u, conCierre, server)),
									maxTamaño) + "\n");
				}
			}
			bw.write("------------------------------------------------------------------" + "\n");
			// se verifica si se trata del propietario y se imprime los eventos
			int maxTamEvento = 22;
			if (userPropietario.equals("true")) {

				List<TipoEvento> tiposEventos = tipoEventoService.getByAll();
				for (TipoEvento tipo : tiposEventos) {
					// Empresa tipos;
					bw.write("        " + tipo.getNombre() + ":\n");
					List<Evento> evento = eventoService.getByFechaAndTipo(Calculos.fechaInicial(id.getFechaInforme()),
							Calculos.fechaFinal(id.getFechaInforme()), tipo.getTipoEventoId());
					for (Evento ev : evento) {
						switch ("" + ev.getTipoEventoId().getTipoEventoId()) {
						case "1":
							bw.write("Produc. devolución..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento)
									+ "\n");
							bw.write("Cantidad devolución.: " + ev.getValorActual() + "\n");
							bw.write("Usuario devolución..: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento)
									+ "\n");
							bw.write("\n");
							break;
						case "3":
							bw.write("Produc. Cambio preci: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento)
									+ "\n");
							bw.write("Precio anterior.....: " + ev.getValorAnterior() + "\n");
							bw.write("Precio actual.......: " + ev.getValorActual() + "\n");
							bw.write("Usuario realiza camb: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento)
									+ "\n");
							bw.write("\n");
							break;
						case "2":
							bw.write("Produc. devolución..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento)
									+ "\n");
							bw.write("Cantidad devolución.: " + ev.getValorActual() + "\n");
							bw.write("Usuario devolución..: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento)
									+ "\n");
							bw.write("\n");
							break;
						default:
							break;
						}
					}
					bw.write("------------------------------------------------------------------" + "\n");

				}
			}

			bw.write("\n");
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(carpeta + pdf);
			System.out.println(carpeta + pdf);
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
		System.out.println("nombre impresora predeterminada: " + defaultPrintService.getName());
		String impresara = usuario.getImpresora();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		System.out.println("Number of printers configured: " + printServices.length);
		for (PrintService printer : printServices) {
			System.out.println("Printer:" + printer.getName());
			if (printer.getName().equals(impresara)) {
				System.out.println("Comparacion:" + printer.getName() + ":" + impresara);
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
		} 

	}

	

	private void imprimirInfoPDF(InfoDiario id)
			throws DocumentException, ParseException, IOException, PrinterException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		Empresa e = Login.getEmpresaLogin();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(id.getFechaInforme());
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		String carpeta = "C:\\facturas\\infoDiario";
		String pdf = "\\informeDiario_" + fhoyIni + ".pdf";
		Boolean conCierre = Boolean.FALSE;
		File folder = new File(carpeta);
		folder.mkdirs();
		FileOutputStream archivo = new FileOutputStream(carpeta + pdf);
		Document documento = new Document();
		float fntSize, lineSpacing;
		fntSize = 11f;
		lineSpacing = 15f;
		PdfWriter.getInstance(documento, archivo);
		documento.setMargins(10, 10, 20, 20);
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getNombre().toUpperCase(),
				FontFactory.getFont(FontFactory.COURIER, 20f)))); // NOMBRE
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "Comprobante de Informe Diario" + e.getDireccion(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // DIRECCION
		documento.add(new Paragraph(new Phrase(lineSpacing, "FECHA INFORME: " + df2.format(id.getFechaInforme()),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "Identificador del servidor: " + Calculos.conseguirMAC(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "N° Factura Inicial  N° de Factura Final  Cantidad Fac.  Valor Total Facturado",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,
				Calculos.cortarDescripcion(id.getDocumentoInicio(), 18) + "  "
						+ Calculos.cortarDescripcion(id.getDocumentoFin(), 19) + "  "
						+ Calculos.cortarCantidades(id.getCantidadDocumentos(), 13) + "  "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 21),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "Descriminación de ventas atendidas por Computador.",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "______________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "Computador            Cant. Fac.  Vr. Total Facturado",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "______________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		// se trae la lista de las mac que facturaron
		List<String> macs = documentoService.getMagList();
		for (String m : macs) {
			numeroDocumentosMac = 0.0;

			try {
				String totalMac = Calculos.cortarCantidades(
						formatea.format(getTotalFaturasMac(id.getFechaInforme(), m, conCierre, 1l)), 19);
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"" + Calculos.cortarDescripcion(m, 20) + "  "
								+ Calculos.cortarCantidades(numeroDocumentosMac, 10) + "  " + totalMac,
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "______________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"                      " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 10) + "  "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 19),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "                                        ",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "Discriminación de Ventas por forma de Pago",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Forma de Pago                        Cantidad Facturas         Valor Facturado",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"EFECTIVO                             " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 17) + ""
						+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 24),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CRÉDITO                                              0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CHEQUES                                              0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"TARJETAS DÉBITO                                      0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"TARJETAS CRÉDITO                                     0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"BONOS                                                0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"VALES                                                0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"OTROS                                                0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "%: " + id.getPorcReduccion(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL VENTAS : " + id.getTotalReducido(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL REAL: " + id.getTotalOriginal(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "IVA TOTAL: " + id.getIvaReducido(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "IVA TOTAL REAL: " + id.getIvaOriginal(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "COSTO TOTAL: " + id.getCostoReducido(),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "COSTO REAL: " + id.getCostoOriginal(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL REMISIONES: " + id.getTotalRemisiones(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo los// cajeros
		Double redu =  (id.getPorcReduccion() / 100.0);
		System.out.println(redu);
		Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
		Long server = configuracion.getServer();
		for (Usuario u : uList) {
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "------------------------------------------------------------------",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(new Paragraph(new Phrase(lineSpacing, "CAJERO: " + u.getNombre() + " " + u.getApellido(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			documento
					.add(new Paragraph(new Phrase(lineSpacing,
							"TOTAL VENTAS x CAJERO: "
									+ (getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l) - (getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l) * redu)),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			if (userPropietario.equals("true")) {
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"TOTAL VENTAS X CAJERO REAL: "
								+ getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}
			if (userPropietario.equals("true")) {
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"TOTAL REMISIONES X CAJERO : "
								+ getTotalRemisionesToDay(id.getFechaInforme(), u, conCierre, server),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}
			if (userPropietario.equals("true")) {
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"TOTAL ADELANTO EFECTIVO X CAJERO: "
								+ getTotalAvanceEfectivoToDay(id.getFechaInforme(), u, conCierre, server),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}

		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "------------------------------------------------------------------",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		// se verifica si se trata del propietario y se imprime los eventos
		int maxTamEvento = 22;
		if (userPropietario.equals("true")) {
			List<TipoEvento> tiposEventos = tipoEventoService.getByAll();
			for (TipoEvento tipo : tiposEventos) {
				// Empresa tipos;
				documento.add(new Paragraph(new Phrase(lineSpacing, "        " + tipo.getNombre(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
				List<Evento> evento = eventoService.getByFechaAndTipo(Calculos.fechaInicial(id.getFechaInforme()),
						Calculos.fechaFinal(id.getFechaInforme()), tipo.getTipoEventoId());
				for (Evento ev : evento) {
					switch ("" + ev.getTipoEventoId().getTipoEventoId()) {
					case "1":						
						documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. devolución..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad devolución.: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario devolución..: " + Calculos.cortarDescripcion(ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan						
						documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					case "3":
						documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. Cambio preci: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Precio anterior.....: " + ev.getValorAnterior(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Precio actual.......: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario realiza camb: " + Calculos.cortarDescripcion(	ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					case "2":
						documento.add(new Paragraph(new Phrase(lineSpacing, "Produc. devolución..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad devolución.: " + ev.getValorActual(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "Usuario devolución..: " + Calculos.cortarDescripcion(ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(), maxTamEvento), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					default:
						break;
					}
				}
			}
		}
		documento.close();
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		String impresara = usuario.getImpresora();
		PrinterJob job = PrinterJob.getPrinterJob();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		System.out.println("Number of printers configured: " + printServices.length);
		for (PrintService printer : printServices) {
			System.out.println("Printer: " + printer.getName());
			if (printer.getName().equals(impresara)) {
				try {
					job.setPrintService(printer);
				} catch (PrinterException ex) {
				}
			}
		}
		PDDocument document = PDDocument.load(new File(carpeta + pdf));
		job.setPageable(new PDFPageable(document));
		job.print();
		document.close();

	}

	public void guardar() {
		System.out.println("guardar informes diarios");
		for (InfoDiario i : getReduccionList()) {
			if (i.getInfoDiarioId() == null) {
				documentoService.save(i);
			} else {
				documentoService.update(i);
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Informes diarios Guardados Exitosamente"));
	}

	public void buscar() throws ParseException {
		System.out.println("informe diario usuario no propietario");
		reducir();
	}

	public void detalleInforme(InfoDiario id) {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		Double reduccion = (double) (id.getPorcReduccion());
		reduccion = reduccion / 100;
		List<Documento> drList = new ArrayList<>();
		drList = getDetalleFacturas(id.getFechaInforme());
		for (Documento d : drList) {
			if ((d.getReduccion() != null && d.getReduccion() == 1l) || d.getConsecutivoDian() == null) {
				if (userPropietario.equals("true")) {
					DocumentoReducido dr = new DocumentoReducido();
					dr.setDocumentoId(d);
					dr.setFechaIngreso(new Date());
					dr.setTotalOriginal(d.getTotal());
					dr.setTotalReducido(d.getTotal());
					getDocumentosReduccionList().add(dr);
				}
			} else {
				DocumentoReducido dr = new DocumentoReducido();
				dr.setDocumentoId(d);
				dr.setFechaIngreso(new Date());
				dr.setTotalOriginal(d.getTotal());
				dr.setTotalReducido(d.getTotal() - (d.getTotal() * reduccion));
				getDocumentosReduccionList().add(dr);
			}
		}
		RequestContext.getCurrentInstance().execute("PF('detalles').show();");
	}
	
	public void entadasSalidas() throws ParseException{
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		//String userPropietario = (String) sessionMap.get("userPropietario");	
		if (validarfiltros()) {
			setReduccionEntradasList(null);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			List<Usuario> usu = new ArrayList<>();
			usu = usuarioService.getByAll();
			for (Long i = hoy; i <= hoyfin; i++) {
				InfoDiario rvo = new InfoDiario();
				Double porcenta = (double) ((getReduccion() == null ? 0.0 : getReduccion()) / 100);
				Double cantidadOriginal = 0.0;
				Double cantidadReducida = 0.0;
				Double totalRemisiones = 0.0;
				Double totalAvanceEfectivo = 0.0;
				Boolean conCierre = Boolean.FALSE;
				ivaTotalEntrada = 0.0;
				iva19Entrada=0.0;
				iva5Entrada=0.0;
				base5Entrada=0.0;
				base19Entrada=0.0;
				execentoEntrada=0.0;
				for (Usuario u : usu) {
					cantidadOriginal = getTotalFaturasToDayEntrada(df.parse(i.toString()), u, conCierre, 1l)+ cantidadOriginal;
				}
				rvo.setTotalOriginal(cantidadOriginal);
				rvo.setFechaInforme(df.parse(i.toString()));
				rvo.setFechaIngreso(new Date());
				rvo.setIvaOriginal(ivaTotalEntrada);
				rvo.setIva19(iva19Entrada- (iva19Entrada * porcenta));
				rvo.setIva5(iva5Entrada- (iva5Entrada * porcenta));
				rvo.setBase19(base19Entrada- (base19Entrada * porcenta));
				rvo.setBase5(base5Entrada- (base5Entrada * porcenta));
				rvo.setExcento(execentoEntrada- (execentoEntrada * porcenta));
				rvo.setIvaReducido(ivaTotalEntrada - (ivaTotalEntrada * porcenta));
				getReduccionEntradasList().add(rvo);
			}
			Double totalTemp= 0.0;
			Double totalIvaTemp= 0.0;
			Double totalIva19Temp=0.0;
			Double totalIva5Temp=0.0;
			Double totalBase5Temp=0.0;
			Double totalBase19Temp=0.0;
			Double totalExcentoTemp=0.0;
			for(InfoDiario in: getReduccionEntradasList()){
				totalTemp=totalTemp+(in.getTotalOriginal()==null?0.0:in.getTotalOriginal());
				totalIvaTemp= totalIvaTemp+(in.getIvaReducido()==null?0.0:in.getIvaReducido());
				totalIva19Temp = totalIva19Temp+in.getIva19();
				totalIva5Temp = totalIva5Temp+in.getIva5();
				totalBase5Temp = totalBase5Temp+in.getBase5();
				totalBase19Temp = totalBase19Temp+in.getBase19();
				totalExcentoTemp = totalExcentoTemp+in.getExcento();
			}
			setTotalEntrada(totalTemp);
			setTotalIvaEntrada(totalIvaTemp);
			setTotalIva19Entrada(totalIva19Temp);
			setTotalIva5Entrada(totalIva5Temp);
			setTotalbase19Entrada(totalBase19Temp);
			setTotalbase5Entrada(totalBase5Temp);
			setTotalExcentoEntrada(totalExcentoTemp);
			RequestContext.getCurrentInstance().execute("PF('entradasSG').show();");
		}
		
	}

	public void reducir() throws ParseException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		if (validarfiltros()) {
			setReduccionList(null);
			setDocumentosReduccionList(null);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			List<InfoDiario> infoList = new ArrayList<>();
			infoList = documentoService.buscarInfodiarioByFecha(getFechaInicio(), getFechafin());
			List<Usuario> usu = new ArrayList<>();
			usu = usuarioService.getByRol(2l);
			for (Long i = hoy; i <= hoyfin; i++) {
				InfoDiario rvo = new InfoDiario();
				for (InfoDiario r : infoList) {
					if (df.format(r.getFechaInforme()).equals(i.toString())) {
						rvo = r;
						break;
					}
				}
				if (rvo.getInfoDiarioId() == null) {
					
					Double porcenta = (double) ((getReduccion() == null ? 0.0 : getReduccion()) / 100);
					Double cantidadOriginal = 0.0;
					Double cantidadReducida = 0.0;
					Double totalRemisiones = 0.0;
					Double totalAvanceEfectivo = 0.0;
					Boolean conCierre = Boolean.FALSE;
					Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
					Long server = configuracion.getServer();
					ivaTotal = 0.0;
					iva19=0.0;
					iva5=0.0;
					base5=0.0;
					base19=0.0;
					execento=0.0;
					primeraFactura = "";
					ultimaFactura = "";
					costoTotal = 0.0;
					numeroDocumentos = 0.0;
					for (Usuario u : usu) {
						//System.out.println(df.parse(i.toString()));
						cantidadOriginal = getTotalFaturasToDay(df.parse(i.toString()), u, conCierre, 1l)
								+ cantidadOriginal;
						totalRemisiones = getTotalRemisionesToDay(df.parse(i.toString()), u, conCierre, server)
								+ totalRemisiones;
						totalAvanceEfectivo = getTotalAvanceEfectivoToDay(df.parse(i.toString()), u, conCierre, server)
								+ totalAvanceEfectivo;
						// ivaOriginal=
						// System.out.println(rvo.getCantidadReducida());
					}
					cantidadReducida = cantidadOriginal - (cantidadOriginal * porcenta);
					rvo.setCostoOriginal(costoTotal);
					rvo.setCostoReducido(costoTotal - (costoTotal * porcenta));
					rvo.setFechaInforme(df.parse(i.toString()));
					rvo.setFechaIngreso(new Date());
					rvo.setIvaOriginal(ivaTotal);
					rvo.setIva19(iva19- (iva19 * porcenta));
					rvo.setIva5(iva5- (iva5 * porcenta));
					rvo.setBase19(base19- (base19 * porcenta));
					rvo.setBase5(base5- (base5 * porcenta));
					rvo.setExcento(execento- (execento * porcenta));
					rvo.setIvaReducido(ivaTotal - (ivaTotal * porcenta));
					rvo.setTotalOriginal(cantidadOriginal);
					rvo.setTotalReducido(cantidadReducida);
					rvo.setTotalRemisiones(totalRemisiones);
					rvo.setPorcReduccion(getReduccion() == null ? 0l : getReduccion());
					rvo.setDocumentoInicio(primeraFactura);
					rvo.setDocumentoFin(ultimaFactura);
					rvo.setCantidadDocumentos(numeroDocumentos);

					rvo.setAvanceEfectivo(totalAvanceEfectivo);
				}
				getReduccionList().add(rvo);
			}
			Double totalTemp= 0.0;
			Double totalIvaTemp= 0.0;
			Double totalIva19Temp=0.0;
			Double totalIva5Temp=0.0;
			Double totalBase5Temp=0.0;
			Double totalBase19Temp=0.0;
			Double totalExcentoTemp=0.0;
			for(InfoDiario in: getReduccionList()){
				totalTemp=totalTemp+in.getTotalReducido();
				totalIvaTemp= totalIvaTemp+in.getIvaReducido();
				totalIva19Temp = totalIva19Temp+in.getIva19();
				totalIva5Temp = totalIva5Temp+in.getIva5();
				totalBase5Temp = totalBase5Temp+in.getBase5();
				totalBase19Temp = totalBase19Temp+in.getBase19();
				totalExcentoTemp = totalExcentoTemp+in.getExcento();
			}
			setTotal(totalTemp);
			setTotalIva(totalIvaTemp);
			setTotalIva19(totalIva19Temp);
			setTotalIva5(totalIva5Temp);
			setTotalbase19(totalBase19Temp);
			setTotalbase5(totalBase5Temp);
			setTotalExcento(totalExcentoTemp);
			if (userPropietario.equals("true")) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reducción Completa"));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Busqueda  Completa"));
			}

		}
	}

	private boolean validarfiltros() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
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
		if (userPropietario.equals("true")) {
			if (getReduccion() == null) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El % de reduccion es obligario", ""));
				valido = false;
			}
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

	public List<Documento> getDetalleFacturas(Date dia) {
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dia);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date hoy;
		hoy = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date hoyfin;
		hoyfin = calendar.getTime();
		List<Documento> factDia = documentoService.getByTipoSinUsuario(tipoDocumentoId, hoy, hoyfin);
		return factDia;
	}

	public Double getTotalFaturasToDay(Date dia, Usuario usuario, Boolean conCierre, Long server)
			throws ParseException {

		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		tipoDocumentoId.add(8l);
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		List<Documento> factDia = documentoService.getByfacturasReales(tipoDocumentoId, hoy, hoyfin,
				usuario.getUsuarioId(), conCierre, server);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
			if (d!=null && d.getIva() != null) {
				ivaTotal = ivaTotal + (d.getIva()==null?0.0:d.getIva());
			}
			if (d!=null && d.getIva19() != null) {
				iva19 = iva19 + (d.getIva19()==null?0.0:d.getIva19());
			}
			if (d!=null && d.getIva5() != null) {
				iva5 = iva5 + (d.getIva5()==null?0.0:d.getIva5());
			}
			if (d!=null && d.getBase5() != null) {
				base5 = base5 + (d.getBase5()==null?0.0:d.getBase5());
			}
			if (d!=null && d.getBase19() != null) {
				base19 = base19 + (d.getBase19()==null?0.0:d.getBase19());
			}
			if (d!=null && d.getExcento() != null) {
				execento = execento + (d.getExcento()==null?0.0:d.getExcento());
			}
			List<DocumentoDetalle> dd = new ArrayList<>();
			dd = documentoDetalleService.getByDocumento(d.getDocumentoId(),1l);
			for (DocumentoDetalle dode : dd) {
				costoTotal = dode.getProductoId().getCosto() + costoTotal;
			}
		}
		if (factDia != null && !factDia.isEmpty()) {
			primeraFactura = factDia.get(0).getConsecutivoDian();
			ultimaFactura = factDia.get(factDia.size() - 1).getConsecutivoDian();
			numeroDocumentos = (double) factDia.size();
		}
		return total;
	}
	
	public Double getTotalFaturasToDayEntrada(Date dia, Usuario usuario, Boolean conCierre, Long server)
			throws ParseException {

		Long tipoDocumentoId = 2l; // tipo documento factura de salida
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin, usuario.getUsuarioId());
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
			if (d!=null && d.getIva() != null) {
				ivaTotalEntrada = ivaTotalEntrada + (d.getIva()==null?0.0:d.getIva());
			}
			if (d!=null && d.getIva19() != null) {
				iva19Entrada = iva19Entrada + (d.getIva19()==null?0.0:d.getIva19());
			}
			if (d!=null && d.getIva5() != null) {
				iva5Entrada = iva5Entrada + (d.getIva5()==null?0.0:d.getIva5());
			}
			if (d!=null && d.getBase5() != null) {
				base5Entrada = base5Entrada + (d.getBase5()==null?0.0:d.getBase5());
			}
			if (d!=null && d.getBase19() != null) {
				base19Entrada = base19Entrada + (d.getBase19()==null?0.0:d.getBase19());
			}
			if (d!=null && d.getExcento() != null) {
				execentoEntrada = execentoEntrada + (d.getExcento()==null?0.0:d.getExcento());
			}
			
		}
		return total;
	}

	public Double getTotalFaturasMac(Date dia, String mac, Boolean conCierre, Long server) throws ParseException {

		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		tipoDocumentoId.add(8l);
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		List<Documento> factDia = documentoService.getByMacAndTipoDocumento(tipoDocumentoId, mac, hoy, hoyfin,
				conCierre, server);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		numeroDocumentosMac = (double) factDia.size();
		return total;
	}

	public Double getTotalRemisionesToDay(Date dia, Usuario usuario, Boolean conCierre, Long server)
			throws ParseException {
		Long tipoDocumentoId = 9l; // tipo documento remisiones
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dia);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date hoy;
		hoy = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date hoyfin;
		hoyfin = calendar.getTime();

		List<Documento> factDia = documentoService.getRemisionesByUsuario(tipoDocumentoId, hoy, hoyfin,
				usuario.getUsuarioId(), conCierre, server);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
	}

	public Double getTotalAvanceEfectivoToDay(Date dia, Usuario usuario, Boolean conCierre, Long server)
			throws ParseException {
		Long tipoDocumentoId = 5l; // tipo documento avance efectivo
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dia);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date hoy;
		hoy = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date hoyfin;
		hoyfin = calendar.getTime();

		List<Documento> factDia = documentoService.getRemisionesByUsuario(tipoDocumentoId, hoy, hoyfin,
				usuario.getUsuarioId(), conCierre, server);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
	}

	public Long getTotalFaturasNoImp() {
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		// tipoDocumentoId.add(4l); // tipo documento cotizacion
		// tipoDocumentoId.add(9l); // numero de guia
		List<Documento> factDia = documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId,1l);
		return (long) (factDia.size());
	}

	public String imprimirCuadre() throws IOException, ParseException {

		return "";
	}

	public StreamedContent getFileXls(InfoDiario info) {
		StreamedContent file = null;
		InputStream stream;
		stream = this.crearLibro(info);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/xls", "InformeDiario.xls");
		}
		return file;
	}

	@SuppressWarnings("deprecation")
	public InputStream crearLibro(InfoDiario info) {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		InputStream inputStream = null;
		Boolean conCierre = Boolean.FALSE;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			HSSFWorkbook workbook = new HSSFWorkbook();
			// Se crea la hoja del xls
			HSSFSheet worksheet = workbook.createSheet("IPV-Productos");
			HSSFRow row;
			int i = 3;

			// Se define la cabecera
			HSSFRow rowTitles = worksheet.createRow((short) 0);

			HSSFCell cellA1 = rowTitles.createCell((short) 0);
			cellA1.setCellValue("FECHA");

			HSSFCell cellB1 = rowTitles.createCell((short) 1);
			cellB1.setCellValue("TOTAL VENTA");

			HSSFCell cellC1 = rowTitles.createCell((short) 2);
			cellC1.setCellValue("IVA TOTAL");

			HSSFCell cellD1 = rowTitles.createCell((short) 3);
			cellD1.setCellValue("COSTO TOTAL");

			HSSFCell cellE1 = rowTitles.createCell((short) 4);
			cellE1.setCellValue(userPropietario.equals("true") ? "TOTAL REMISIONES" : "");

			HSSFCell cellF1 = rowTitles.createCell((short) 5);
			cellF1.setCellValue("INICIO FACTURAS");

			HSSFCell cellG1 = rowTitles.createCell((short) 6);
			cellG1.setCellValue("FIN FACTURAS");

			HSSFCell cellAi;
			HSSFCell cellBi;
			HSSFCell cellCi;
			HSSFCell cellDi;
			HSSFCell cellEi;
			HSSFCell cellFi;
			HSSFCell cellGi;

			row = worksheet.createRow((short) 1);
			cellAi = row.createCell((short) 0);
			cellAi.setCellValue(info.getFechaInforme().toString()); // id

			cellBi = row.createCell((short) 1);
			cellBi.setCellValue(userPropietario.equals("true") ? info.getTotalOriginal() : info.getTotalReducido()); // id

			cellCi = row.createCell((short) 2);
			cellCi.setCellValue(userPropietario.equals("true") ? info.getIvaOriginal() : info.getIvaReducido()); // id

			cellDi = row.createCell((short) 3);
			cellDi.setCellValue(userPropietario.equals("true") ? info.getCostoOriginal() : info.getCostoReducido()); // id

			cellEi = row.createCell((short) 4);
			cellEi.setCellValue(userPropietario.equals("true") ? "" + info.getTotalRemisiones() : ""); // id

			cellFi = row.createCell((short) 5);
			cellFi.setCellValue("" + info.getDocumentoInicio()); // id

			cellGi = row.createCell((short) 6);
			cellGi.setCellValue("" + info.getDocumentoFin()); // id

			row = worksheet.createRow((short) 2);
			cellAi = row.createCell((short) 0);
			cellAi.setCellValue(""); // id

			cellBi = row.createCell((short) 1);
			cellBi.setCellValue(""); // id

			cellCi = row.createCell((short) 2);
			cellCi.setCellValue(""); // id

			cellDi = row.createCell((short) 3);
			cellDi.setCellValue(""); // id

			cellEi = row.createCell((short) 4);
			cellEi.setCellValue(""); // id

			cellFi = row.createCell((short) 5);
			cellFi.setCellValue(""); // id

			cellGi = row.createCell((short) 6);
			cellGi.setCellValue(""); // id

			row = worksheet.createRow((short) 3);
			cellAi = row.createCell((short) 0);
			cellAi.setCellValue("NOMBRE CAJERO"); // id

			cellBi = row.createCell((short) 1);
			cellBi.setCellValue("TOTAL VENTAS x CAJERO"); // id

			cellCi = row.createCell((short) 2);
			cellCi.setCellValue(userPropietario.equals("true") ? "TOTAL REMISIONES x CAJERO" : ""); // id

			cellDi = row.createCell((short) 3);
			cellDi.setCellValue(userPropietario.equals("true") ? "TOTAL ADELANTO EFECTIVO x CAJERO" : ""); // id

			List<Usuario> uList = usuarioService.getByRol(2l);//// se traen solo
																//// los cajeros
			Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
			Long server = configuracion.getServer();
			for (Usuario u : uList) {
				i++;
				row = worksheet.createRow((short) i);
				cellAi = row.createCell((short) 0);
				cellAi.setCellValue(u.getNombre() + " " + u.getApellido()); // id

				Double redu = (double) (info.getPorcReduccion() / 100);
				Double ventas = userPropietario.equals("true")
						? getTotalFaturasToDay(info.getFechaInforme(), u, conCierre, 1l)
						: (getTotalFaturasToDay(info.getFechaInforme(), u, conCierre, 1l) - getTotalFaturasToDay(info.getFechaInforme(), u, conCierre, 1l) * redu);
				cellBi = row.createCell((short) 1);
				cellBi.setCellValue("" + ventas);

				cellCi = row.createCell((short) 2);
				cellCi.setCellValue(userPropietario.equals("true")
						? "" + getTotalRemisionesToDay(info.getFechaInforme(), u, conCierre, server) : "");

				cellDi = row.createCell((short) 3);
				cellDi.setCellValue(userPropietario.equals("true")
						? "" + getTotalAvanceEfectivoToDay(info.getFechaInforme(), u, conCierre, server) : "");
			}

			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			byte[] bytes = bos.toByteArray();
			inputStream = new ByteArrayInputStream(bytes);

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;

	}

	public Long getReduccion() {

		return reduccion;
	}

	public void setReduccion(Long reduccion) {
		this.reduccion = reduccion;
	}

	public Date getFechaInicio() {
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

	public List<InfoDiario> getReduccionList() {
		if (reduccionList == null) {
			reduccionList = new ArrayList<>();
		}
		return reduccionList;
	}

	public void setReduccionList(List<InfoDiario> reduccionList) {
		this.reduccionList = reduccionList;
	}

	public String getUltimaFactura() {
		return ultimaFactura;
	}

	public void setUltimaFactura(String ultimaFactura) {
		this.ultimaFactura = ultimaFactura;
	}

	public Double getTotalDia() {
		return totalDia;
	}

	public void setTotalDia(Double totalDia) {
		this.totalDia = totalDia;
	}

	public Date getFechaHoy() {
		return fechaHoy;
	}

	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}

	public Usuario getUsuarioDia() {
		return usuarioDia;
	}

	public void setUsuarioDia(Usuario usuarioDia) {
		this.usuarioDia = usuarioDia;
	}

	public Empresa getEmpresa() {
		if (empresa == null) {
			empresa = Login.getEmpresaLogin();
		}
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<DocumentoReducido> getDocumentosReduccionList() {
		if (documentosReduccionList == null) {
			documentosReduccionList = new ArrayList<>();
		}
		return documentosReduccionList;
	}

	public void setDocumentosReduccionList(List<DocumentoReducido> documentosReduccionList) {
		this.documentosReduccionList = documentosReduccionList;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getTotalIva() {
		return totalIva;
	}

	public void setTotalIva(Double totalIva) {
		this.totalIva = totalIva;
	}

	public Double getTotalIva5() {
		return totalIva5;
	}

	public void setTotalIva5(Double totalIva5) {
		this.totalIva5 = totalIva5;
	}

	public Double getTotalIva19() {
		return totalIva19;
	}

	public void setTotalIva19(Double totalIva19) {
		this.totalIva19 = totalIva19;
	}

	public Double getTotalbase5() {
		return totalbase5;
	}

	public void setTotalbase5(Double totalbase5) {
		this.totalbase5 = totalbase5;
	}

	public Double getTotalbase19() {
		return totalbase19;
	}

	public void setTotalbase19(Double totalbase19) {
		this.totalbase19 = totalbase19;
	}

	public Double getTotalExcento() {
		return totalExcento;
	}

	public void setTotalExcento(Double totalExcento) {
		this.totalExcento = totalExcento;
	}

	public List<InfoDiario> getReduccionEntradasList() {
		if(reduccionEntradasList==null){
			reduccionEntradasList=new ArrayList<>();
		}
		return reduccionEntradasList;
	}

	public void setReduccionEntradasList(List<InfoDiario> reduccionEntradasList) {
		this.reduccionEntradasList = reduccionEntradasList;
	}

	public Double getTotalEntrada() {
		return totalEntrada;
	}

	public void setTotalEntrada(Double totalEntrada) {
		this.totalEntrada = totalEntrada;
	}

	public Double getTotalIvaEntrada() {
		return totalIvaEntrada;
	}

	public void setTotalIvaEntrada(Double totalIvaEntrada) {
		this.totalIvaEntrada = totalIvaEntrada;
	}

	public Double getTotalIva5Entrada() {
		return totalIva5Entrada;
	}

	public void setTotalIva5Entrada(Double totalIva5Entrada) {
		this.totalIva5Entrada = totalIva5Entrada;
	}

	public Double getTotalIva19Entrada() {
		return totalIva19Entrada;
	}

	public void setTotalIva19Entrada(Double totalIva19Entrada) {
		this.totalIva19Entrada = totalIva19Entrada;
	}

	public Double getTotalbase5Entrada() {
		return totalbase5Entrada;
	}

	public void setTotalbase5Entrada(Double totalbase5Entrada) {
		this.totalbase5Entrada = totalbase5Entrada;
	}

	public Double getTotalbase19Entrada() {
		return totalbase19Entrada;
	}

	public void setTotalbase19Entrada(Double totalbase19Entrada) {
		this.totalbase19Entrada = totalbase19Entrada;
	}

	public Double getTotalExcentoEntrada() {
		return totalExcentoEntrada;
	}

	public void setTotalExcentoEntrada(Double totalExcentoEntrada) {
		this.totalExcentoEntrada = totalExcentoEntrada;
	}

	public Double getTotalDiferencia() {
		try {
			totalDiferencia=getTotal()-getTotalEntrada();
		} catch (Exception e) {
			totalDiferencia=0.0;
		}
		
		return totalDiferencia;
	}

	public void setTotalDiferencia(Double totalDiferencia) {
		this.totalDiferencia = totalDiferencia;
	}

	public Double getTotalIvaDiferencia() {
		try {
			totalIvaDiferencia= getTotalIva()-getTotalIvaEntrada();
		} catch (Exception e) {
			totalIvaDiferencia= 0.0;
		}
		
		return totalIvaDiferencia;
	}

	public void setTotalIvaDiferencia(Double totalIvaDiferencia) {
		this.totalIvaDiferencia = totalIvaDiferencia;
	}

	public Double getTotalIva5Diferencia() {
		try {
			totalIva5Diferencia=getTotalIva5()-getTotalIva5Entrada();
		} catch (Exception e) {
			totalIva5Diferencia=0.0;
		}	
		return totalIva5Diferencia;
	}

	public void setTotalIva5Diferencia(Double totalIva5Diferencia) {
		this.totalIva5Diferencia = totalIva5Diferencia;
	}

	public Double getTotalIva19Diferencia() {
		try {
			totalIva19Diferencia=getTotalIva19()-getTotalIva19Entrada();
		} catch (Exception e) {
			totalIva19Diferencia=0.0;
		}
		
		return totalIva19Diferencia;
	}

	public void setTotalIva19Diferencia(Double totalIva19Diferencia) {
		this.totalIva19Diferencia = totalIva19Diferencia;
	}

	public Double getTotalbase5Diferencia() {
		try {
			totalbase5Diferencia= getTotalbase5()-getTotalbase5Entrada();
		} catch (Exception e) {
			totalbase5Diferencia=0.0;
		}
		
		return totalbase5Diferencia;
	}

	public void setTotalbase5Diferencia(Double totalbase5Diferencia) {
		this.totalbase5Diferencia = totalbase5Diferencia;
	}

	public Double getTotalbase19Diferencia() {
		try {
			totalbase19Diferencia=getTotalbase19()-getTotalbase19Entrada();
		} catch (Exception e) {
			totalbase19Diferencia=0.0;
		}
		
		return totalbase19Diferencia;
	}

	public void setTotalbase19Diferencia(Double totalbase19Diferencia) {
		this.totalbase19Diferencia = totalbase19Diferencia;
	}

	public Double getTotalExcentoDiferencia() {
		try {
			totalExcentoDiferencia=getTotalExcento()-getTotalExcentoEntrada();
		} catch (Exception e) {
			totalExcentoDiferencia=0.0;
		}		
		return totalExcentoDiferencia;
	}

	public void setTotalExcentoDiferencia(Double totalExcentoDiferencia) {
		this.totalExcentoDiferencia = totalExcentoDiferencia;
	} 
	
	
	
}
