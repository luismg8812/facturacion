package com.fact.beam;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.api.Calculos;
import com.fact.api.FactException;
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
public class Reduccion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(Reduccion.class);

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

	private Double totalDia = 0.0;
	private Date fechaHoy;
	private Usuario usuarioDia;
	public Empresa empresa;

	// entradas
	private Double ivaTotalEntrada = 0.0;
	private Double iva19Entrada = 0.0;
	private Double iva5Entrada = 0.0;
	private Double base5Entrada = 0.0;
	private Double base19Entrada = 0.0;
	private Double execentoEntrada = 0.0;
	//

	private String ultimaFactura;

	private Double numeroDocumentosMac;

	private Double total;
	private Double totalIva;
	private Double totalIva5;
	private Double totalIva19;
	private Double totalbase5;
	private Double totalbase19;
	private Double totalExcento;

	// total entradas
	private Double totalEntrada;
	private Double totalIvaEntrada;
	private Double totalIva5Entrada;
	private Double totalIva19Entrada;
	private Double totalbase5Entrada;
	private Double totalbase19Entrada;
	private Double totalExcentoEntrada;
	//

	// total diferencia
	private Double totalDiferencia = 0.0;
	private Double totalIvaDiferencia = 0.0;
	private Double totalIva5Diferencia = 0.0;
	private Double totalIva19Diferencia = 0.0;
	private Double totalbase5Diferencia = 0.0;
	private Double totalbase19Diferencia = 0.0;
	private Double totalExcentoDiferencia = 0.0;
	//
	private List<InfoDiario> reduccionList;
	private List<InfoDiario> reduccionEntradasList;
	private List<DocumentoReducido> documentosReduccionList;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private String impresora(String impresora) {
		return (String) sessionMap.get("impresora" + impresora);
	}

	private Configuracion configuracion() {
		return (Configuracion) sessionMap.get("configuracion");
	}

	public String imprimirinforme(InfoDiario id, String exportar)
			throws DocumentException, IOException, PrinterException, ParseException {
		Empresa e = getEmpresa();
		String imp = e.getImpresion().toUpperCase();
		int numeroImpresiones = 1;
		String ruta = "";
		for (int i = 0; i < numeroImpresiones; i++) {
			switch (imp) {
			case "TXT":
				ruta = imprimirBig(id, exportar);
				break;
			case "BIG":
				ruta = imprimirBig(id, exportar);
				break;
			case "PDF":
				ruta = imprimirInfoPDF(id, exportar);
				break;
			case "BIG_PDF":
				ruta = imprimirInfoPDF(id, exportar);
				break;

			default:
				ruta =imprimirInfoPDF(id, exportar);
				break;
			}
		}

		return ruta;

	}

	public String imprimirinformePropietario(InfoDiario id)
			throws DocumentException, IOException, PrinterException, ParseException {
		Empresa e = getEmpresa();
		String imp = e.getImpresion().toUpperCase();
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		Configuracion configuracion = configuracion();
		String numeroInforme = informeDiarioService.consecutivoInformePropietario();
		List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo
		// los
		// cajeros
		int numeroImpresiones = 1;
		String ruta = "";
		for (int i = 0; i < numeroImpresiones; i++) {
			switch (imp) {
			case "TXT":
				// imprimirBig(id);
				break;
			case "BIG":
				// imprimirBig(id);
				break;
			case "PDF":
				ruta = imprimirInfoPDF(id, "false");

				break;
			case "BIG_PDF":
				ruta = imprimirInformePropietarioPDF(id, numeroInforme, usuario, configuracion, uList);
				break;

			default:
				imprimirInfoPDF(id, "false");
				break;
			}
		}
		return ruta;
	}

	public String imprimirInformePropietarioPDF(InfoDiario id, String numeroImforme, Usuario usuario,
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
		float fntSize = 11f;
		float lineSpacing = 15f;
		PdfWriter.getInstance(documento, archivo);

		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		Date hoy = Calculos.fechaInicial(id.getFechaInforme());
		Date hoyfin = Calculos.fechaFinal(id.getFechaInforme());
		Double totalVenasTemp = 0.0;
		Double totalFacturasTemp = 0.0;
		Double totalVentasTemp = 0.0;
		Double totalRemisionesTemp = 0.0, totalFajosTemp = 0.0, totalEfectivoTemp = 0.0, totalVentasDia = 0.0,
				totalRemisionesDia = 0.0;

		documento.setMargins(10, 10, 20, 20);
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, "RESUMEN N�: " + numeroImforme + "     FECHA: " + fhoyIni,
				FontFactory.getFont(FontFactory.COURIER, 20f)))); // numero de informe y fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "CAJERO          FACT      VENTAS  REMISIONES      FAJOS   EFECTIVO DIFERENCIA",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		for (Usuario u : uList) {
			Double VentasTemp = 0.0;
			Double RemisionesTemp = 0.0;
			List<Documento> factDia = documentoService.getByfacturasRealesConFecha(tipoDocumentoId, hoy, hoyfin,
					u.getUsuarioId(), Boolean.FALSE, 1l);
			List<Documento> remisiList = documentoService.getRemisionesByUsuarioConFecha(9l, hoy, hoyfin,
					u.getUsuarioId(), Boolean.FALSE, config.getServer());
			totalFacturasTemp += factDia.size();
			String nombreCajero = Calculos.cortarDescripcion(u.getNombre() + " " + u.getApellido(), 15);
			String cantidadFacturas = Calculos.cortarCantidades("" + factDia.size(), 4);
			for (Documento d : factDia) {
				VentasTemp += d.getTotal();
				totalVenasTemp += d.getTotal();
			}
			for (Documento d : remisiList) {
				RemisionesTemp += d.getTotal();
				totalRemisionesTemp += d.getTotal();
			}
			String ventas = Calculos.cortarCantidades(formatea.format(VentasTemp), 11);
			String remisiones = Calculos.cortarCantidades(formatea.format(RemisionesTemp), 11);
			String fajos = Calculos.cortarCantidades("0.0", 10);
			String efectivo = Calculos.cortarCantidades("0.0", 10);
			String diferencia = Calculos.cortarCantidades("0.0", 10);
			totalVentasDia = totalVentasTemp + totalVentasDia;
			totalRemisionesDia = totalRemisionesTemp + totalRemisionesDia;
			documento
					.add(new Paragraph(new Phrase(
							lineSpacing, nombreCajero + " " + cantidadFacturas + " " + ventas + " " + remisiones + " "
									+ fajos + " " + efectivo + diferencia,
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}
		String totalVentas = Calculos.cortarCantidades(formatea.format(totalVenasTemp), 11);
		String totalRemisiones = Calculos.cortarCantidades(formatea.format(totalRemisionesTemp), 11);
		String totalFacturas = Calculos.cortarCantidades("" + totalFacturasTemp, 4);
		String totalFajos = Calculos.cortarCantidades("" + totalFajosTemp, 10);
		String totalEfectivo = Calculos.cortarCantidades("" + totalEfectivoTemp, 10);
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"               " + " " + totalFacturas + " " + totalVentas + " " + totalRemisiones + " "
								+ totalFajos + " " + totalEfectivo,
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		Double totalDia = totalVenasTemp + totalRemisionesTemp;
		Double porcentajeVentas = (totalVenasTemp * 100) / totalDia;
		Double porcentajeRemisiones = (totalRemisionesTemp * 100) / totalDia;
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"D�A      : " + formatea.format(porcentajeVentas) + "%     "
								+ formatea.format(porcentajeRemisiones) + "%",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"ACUMULADO: " + formatea.format(porcentajeVentas) + "%     "
								+ formatea.format(porcentajeRemisiones) + "%",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"______________________________________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "REMISIONES", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		for (Usuario u : uList) {
			List<Documento> remisiList = documentoService.getRemisionesByUsuarioConFecha(9l, hoy, hoyfin,
					u.getUsuarioId(), Boolean.FALSE, config.getServer());
			for (Documento d : remisiList) {
				String nombreCajero = Calculos.cortarDescripcion(u.getNombre() + " " + u.getApellido(), 15);
				String guia = Calculos.cortarCantidades("" + d.getDocumentoId(), 6);
				String cliente = Calculos.cortarCantidades(d.getClienteId().getNombre(), 16);
				String valor = Calculos.cortarCantidades(formatea.format(d.getTotal()), 11);
				String descuento = Calculos
						.cortarCantidades(formatea.format(d.getDescuento() == null ? 0.0 : d.getDescuento()), 11);
				Double desc = ((d.getDescuento() == null ? 0.0 : d.getDescuento()) * 100) / d.getTotal();
				String porcentaje = Calculos.cortarCantidades("" + desc, 7);
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"" + nombreCajero + " " + "N� GUIA CLIENTE               VALOR   DESCUENTO   %DESC",
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"                " + guia + " " + cliente + " " + valor + " " + descuento + " " + porcentaje,
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			}
		}
		List<TipoEvento> tiposEventos = tipoEventoService.getByAll();
		int maxTamEvento = 22;
		for (TipoEvento tipo : tiposEventos) {
			// Empresa tipos;
			documento
					.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(
					new Paragraph(new Phrase(lineSpacing, " ", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(new Paragraph(
					new Phrase(lineSpacing, tipo.getNombre(), FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			List<Evento> evento = eventoService.getByFechaAndTipo(Calculos.fechaInicial(id.getFechaInforme()),
					Calculos.fechaFinal(id.getFechaInforme()), tipo.getTipoEventoId());
			for (Evento ev : evento) {
				switch ("" + ev.getTipoEventoId().getTipoEventoId()) {
				case "1":
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Produc. devoluci�n..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad devoluci�n.: " + ev.getValorActual(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Usuario devoluci�n..: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
									maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(
							new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "3":
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Produc. Cambio preci: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento
							.add(new Paragraph(new Phrase(lineSpacing, "Precio anterior.....: " + ev.getValorAnterior(),
									FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Precio actual.......: " + ev.getValorActual(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Usuario realiza camb: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
									maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(
							new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "2":
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Produc. Descuento...: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento
							.add(new Paragraph(new Phrase(lineSpacing, "Cantidad Descuento..: " + ev.getValorAnterior(),
									FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Cantidad sin Descuen: " + ev.getValorActual(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Usuario devoluci�n..: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
									maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(
							new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					break;
				case "4":
					documento.add(new Paragraph(new Phrase(lineSpacing,
							"Usuario copia factur: " + Calculos.cortarDescripcion(
									ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
									maxTamEvento),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
					documento.add(new Paragraph(new Phrase(lineSpacing, "Documento: " + ev.getCampo(),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan

					break;
				default:
					break;
				}
			}
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		documento.close();
		// String impresara = usuario.getImpresora();
		// printer(impresara, folder + pdf, config);
		return "";
	}

	private String imprimirBig(InfoDiario id, String exportar) throws ParseException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userPropietario = (String) sessionMap.get("userPropietario");
		Boolean conCierre = Boolean.FALSE;
		int maxTama�o = 15;
		Empresa e = getEmpresa();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String fhoyIni = df.format(id.getFechaInforme());
		String carpeta = "C:\\facturas\\infoDiario\\";
		String pdf = "informeDiario_" + fhoyIni + ".txt";
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
			bw.write("FECHA INFORME: " + df2.format(id.getFechaInforme()) + "\n");
			bw.write("\n");
			bw.write("Identificador del servidor: " + Calculos.conseguirMAC() + "\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("N� Factura Inicial  N� de Factura Final  Cantidad Fac.  Valor Total Facturado\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("" + Calculos.cortarDescripcion(id.getDocumentoInicio(), 18) + "  "
					+ Calculos.cortarDescripcion(id.getDocumentoFin(), 19) + "  "
					+ Calculos.cortarCantidades(id.getCantidadDocumentos(), 13) + "  "
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 21) + "\n");
			bw.write("\n");
			bw.write("Descriminaci�n de ventas atendidas por Computador.\n");
			bw.write("______________________________________________________\n");
			bw.write("Computador            Cant. Fac.  Vr. Total Facturado\n");
			bw.write("______________________________________________________\n");
			// se trae la lista de las mac que facturaron
			List<String> macs = documentoService.getMagList();
			Long porcenReduccion = id.getPorcReduccion() == null ? getReduccion() : id.getPorcReduccion();
			Double redu = (porcenReduccion == null ? 0.0 : porcenReduccion / 100.0);
			for (String m : macs) {
				numeroDocumentosMac = 0.0;

				try {
					String totalMac = Calculos.cortarCantidades(
							formatea.format(getTotalFaturasMac(id.getFechaInforme(), m, conCierre, 1l)), 19);
					bw.write("" + Calculos.cortarDescripcion(m, 20) + "  "
							+ Calculos.cortarCantidades(numeroDocumentosMac, 10) + "  " + totalMac + "\n");
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			bw.write("______________________________________________________\n");
			bw.write("                      " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 10) + "  "
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 19) + "\n");
			bw.write("                                        \n");
			bw.write("Discriminaci�n de Ventas por forma de Pago\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("Forma de Pago                        Cantidad Facturas         Valor Facturado\n");
			bw.write("______________________________________________________________________________\n");
			bw.write("EFECTIVO                             " + Calculos.cortarCantidades(id.getCantidadDocumentos(), 17)
					+ "" + Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 24) + "\n");
			bw.write("CR�DITO                                              0                       0\n");
			bw.write("CHEQUES                                              0                       0\n");
			bw.write("TARJETAS D�BITO                                      0                       0\n");
			bw.write("TARJETAS CR�DITO                                     0                       0\n");
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
					+ Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), maxTama�o) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("TOTAL REAL:.................: "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalOriginal()), maxTama�o) + "\n");
			}
			bw.write("IVA TOTAL:..................: "
					+ Calculos.cortarCantidades(formatea.format(id.getIvaReducido()), maxTama�o) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("IVA TOTAL REAL:.............: "
						+ Calculos.cortarCantidades(formatea.format(id.getIvaOriginal()), maxTama�o) + "\n");
			}
			bw.write("COSTO TOTAL:................: "
					+ Calculos.cortarCantidades(formatea.format(id.getCostoReducido()), maxTama�o) + "\n");
			if (userPropietario.equals("true")) {
				bw.write("COSTO REAL:.................: "
						+ Calculos.cortarCantidades(formatea.format(id.getCostoOriginal()), maxTama�o) + "\n");
			}
			if (userPropietario.equals("true")) {
				bw.write("TOTAL REMISIONES:...........: "
						+ Calculos.cortarCantidades(formatea.format(id.getTotalRemisiones()), maxTama�o) + "\n");
			}

			Configuracion configuracion = configuracion();
			Long server = configuracion.getServer();
			List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo
																// los
			// cajeros
			for (Usuario u : uList) {
				bw.write("------------------------------------------------------------------" + "\n");
				bw.write("CAJERO:.....................: " + u.getNombre() + " " + u.getApellido() + "\n");
				bw.write(
						"TOTAL VENTAS x CAJERO:......: " + Calculos.cortarCantidades(
								(getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l)
										- (getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l) * redu)),
								maxTama�o) + "\n");
				if (userPropietario.equals("true")) {
					bw.write("TOTAL VENTAS X CAJERO REAL..: " + Calculos.cortarCantidades(
							formatea.format(getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, server)),
							maxTama�o) + "\n");
				}
				if (userPropietario.equals("true")) {
					bw.write("TOTAL REMISIONES X CAJERO...: " + Calculos.cortarCantidades(
							formatea.format(getTotalRemisionesToDay(id.getFechaInforme(), u, conCierre, server)),
							maxTama�o) + "\n");
				}
				if (userPropietario.equals("true")) {
					bw.write("TOTAL ADELANTO EFECTI X CAJE: " + Calculos.cortarCantidades(
							formatea.format(getTotalAvanceEfectivoToDay(id.getFechaInforme(), u, conCierre, server)),
							maxTama�o) + "\n");
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
							bw.write("Produc. devoluci�n..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento)
									+ "\n");
							bw.write("Cantidad devoluci�n.: " + ev.getValorActual() + "\n");
							bw.write("Usuario devoluci�n..: " + Calculos.cortarDescripcion(
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
							bw.write("Produc. devoluci�n..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento)
									+ "\n");
							bw.write("Cantidad devoluci�n.: " + ev.getValorActual() + "\n");
							bw.write("Usuario devoluci�n..: " + Calculos.cortarDescripcion(
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

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(carpeta + pdf);
			log.info(carpeta + pdf);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		if (inputStream == null) {
			// return;
		}
		if ("false".equals(exportar)) {
			DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			Doc document = new SimpleDoc(inputStream, docFormat, null);
			PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
			PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			log.info("nombre impresora predeterminada: " + defaultPrintService.getName());
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
			}
		}
		return carpeta + pdf + ";txt";
	}

	private String imprimirInfoPDF(InfoDiario id, String exportar)
			throws DocumentException, ParseException, IOException, PrinterException {
		String userPropietario = (String) sessionMap.get("userPropietario");
		Empresa e = getEmpresa();
		String impresora = impresora("1");
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
				new Phrase(lineSpacing, "N� Factura Inicial  N� de Factura Final  Cantidad Fac.  Valor Total Facturado",
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
		documento.add(new Paragraph(new Phrase(lineSpacing, "Descriminaci�n de ventas atendidas por Computador.",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "______________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "Computador            Cant. Fac.  Vr. Total Facturado",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "______________________________________________________",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		// se trae la lista de las mac que facturaron
		List<String> macs = documentoService.getMagList();
		Long porcenReduccion = id.getPorcReduccion() == null ? getReduccion() : id.getPorcReduccion();
		Double redu = (porcenReduccion == null ? 0.0 : porcenReduccion / 100.0);
		for (String m : macs) {

			try {
				numeroDocumentosMac = 0.0;
				Double totalFacturasMac = getTotalFaturasMac(id.getFechaInforme(), m, conCierre, 1l);
				String totalMac = Calculos
						.cortarCantidades(formatea.format(totalFacturasMac - (totalFacturasMac * redu)), 19);
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"" + Calculos.cortarDescripcion(m, 20) + "  "
								+ Calculos.cortarCantidades(numeroDocumentosMac, 10) + "  " + totalMac,
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			} catch (ParseException e1) {
				log.error(e1.getMessage());
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
		documento.add(new Paragraph(new Phrase(lineSpacing, "Discriminaci�n de Ventas por forma de Pago",
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
				"CR�DITO                                              0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CHEQUES                                              0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"TARJETAS D�BITO                                      0                       0",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"TARJETAS CR�DITO                                     0                       0",
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

		String totalVent = Calculos.cortarCantidades(formatea.format(id.getTotalReducido()), 12);
		String ivatotal = Calculos.cortarCantidades(formatea.format(id.getIvaReducido()), 10);
		String iva19t = Calculos.cortarCantidades(formatea.format(id.getIva19Reducido()), 10);
		String iva5t = Calculos.cortarCantidades(formatea.format(id.getIva5Reducido()), 10);
		String base19t = Calculos.cortarCantidades(formatea.format(id.getBase19Reducido()), 12);
		String base5t = Calculos.cortarCantidades(formatea.format(id.getBase5Reducido()), 10);
		String execnto = Calculos.cortarCantidades(formatea.format(id.getExcentorReducido()), 10);

		documento.add(new Paragraph(new Phrase(lineSpacing,
				"TOTAL VENTAS    IVA TOTAL     IVA 19      IVA 5    GRAVADO 19  GRAVADO 5   EXCLUIDO",
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento
				.add(new Paragraph(
						new Phrase(
								lineSpacing, "" + totalVent + "   " + ivatotal + " " + iva19t + "   " + iva5t + "  "
										+ base19t + " " + base5t + " " + execnto,
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan

		// if (userPropietario.equals("true")) {
		documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL : " + formatea.format(id.getTotalOriginal()),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		// }
		// if (userPropietario.equals("true")) {
		documento.add(new Paragraph(new Phrase(lineSpacing, "IVA TOTAL : " + formatea.format(id.getIvaOriginal()),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		// }
		// if (userPropietario.equals("true")) {
		documento.add(new Paragraph(new Phrase(lineSpacing, "COSTO: " + formatea.format(id.getCostoOriginal()),
				FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "GANANCIA: " + formatea.format(id.getTotalOriginal() - id.getCostoOriginal()),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		// }
		if (userPropietario.equals("true")) {
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "TOTAL REMISIONES: " + formatea.format(id.getTotalRemisiones()),
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
		}

		List<Usuario> uList = usuarioService.getByRol(2l); // se traen solo
															// los// cajeros

		log.info("redu: " + redu);
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		for (Usuario u : uList) {
			Double totalCajero = getTotalFaturasToDay(id.getFechaInforme(), u, conCierre, 1l);
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "------------------------------------------------------------------",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
			documento.add(new Paragraph(new Phrase(lineSpacing, "CAJERO: " + u.getNombre() + " " + u.getApellido(),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			documento.add(new Paragraph(new Phrase(lineSpacing,
					"TOTAL VENTAS x CAJERO: " + formatea.format((totalCajero - (totalCajero * redu))),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			if (userPropietario.equals("true")) {
				documento.add(new Paragraph(
						new Phrase(lineSpacing, "TOTAL VENTAS X CAJERO REAL: " + formatea.format(totalCajero),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}
			if (userPropietario.equals("true")) {
				Double totalRemisionesCajero = getTotalRemisionesToDay(id.getFechaInforme(), u, conCierre, server);
				documento.add(new Paragraph(
						new Phrase(lineSpacing, "TOTAL REMISIONES X CAJERO : " + formatea.format(totalRemisionesCajero),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}
			if (userPropietario.equals("true")) {
				Double avanceEfectivoCajero = getTotalAvanceEfectivoToDay(id.getFechaInforme(), u, conCierre, server);
				documento.add(new Paragraph(new Phrase(lineSpacing,
						"TOTAL ADELANTO EFECTIVO X CAJERO: " + formatea.format(avanceEfectivoCajero),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			}

		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "------------------------------------------------------------------",
						FontFactory.getFont(FontFactory.COURIER, fntSize))));
		List<DocumentoDetalle> bolsas = documentoDetalleService.getByProductoId(5l,
				Calculos.fechaInicial(id.getFechaInforme()), Calculos.fechaFinal(id.getFechaInforme()));// se
																										// busca
																										// la
																										// cantidad
																										// de
																										// detalles
																										// con
																										// producto
																										// 5=bolsas
		log.info("cantidad bolsas:" + bolsas.size());
		if (bolsas != null && !bolsas.isEmpty()) {
			Double totalBolsas = 0.0;
			Double cantidadBolsas = 0.0;
			for (DocumentoDetalle d : bolsas) {
				totalBolsas += d.getParcial();
				cantidadBolsas += d.getCantidad();
			}
			documento.add(new Paragraph(new Phrase(lineSpacing, "TOTAL DE BOLSAS: " + cantidadBolsas,
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			documento.add(new Paragraph(new Phrase(lineSpacing, "VALOR: " + formatea.format(totalBolsas),
					FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "------------------------------------------------------------------",
							FontFactory.getFont(FontFactory.COURIER, fntSize)))); // espacio
		}

		// se verifica si se trata del propietario y se imprime los eventos
		int maxTamEvento = 22;
		if (userPropietario.equals("true")) {
			List<TipoEvento> tiposEventos = tipoEventoService.getByAll();
			for (TipoEvento tipo : tiposEventos) {
				// Empresa tipos;
				documento.add(new Paragraph(new Phrase(lineSpacing, "        " + tipo.getNombre(),
						FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
				List<Evento> evento = eventoService.getByFechaAndTipo(Calculos.fechaInicial(id.getFechaInforme()),
						Calculos.fechaFinal(id.getFechaInforme()), tipo.getTipoEventoId());
				for (Evento ev : evento) {
					switch ("" + ev.getTipoEventoId().getTipoEventoId()) {
					case "1":
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Produc. devoluci�n..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(
								new Paragraph(new Phrase(lineSpacing, "Cantidad devoluci�n.: " + ev.getValorActual(),
										FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Usuario devoluci�n..: " + Calculos.cortarDescripcion(
										ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
										maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(
								new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					case "3":
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Produc. Cambio preci: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(
								new Paragraph(new Phrase(lineSpacing, "Precio anterior.....: " + ev.getValorAnterior(),
										FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(
								new Paragraph(new Phrase(lineSpacing, "Precio actual.......: " + ev.getValorActual(),
										FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Usuario realiza camb: " + Calculos.cortarDescripcion(
										ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
										maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(
								new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					case "2":
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Produc. devoluci�n..: " + Calculos.cortarDescripcion(ev.getCampo(), maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(
								new Paragraph(new Phrase(lineSpacing, "Cantidad devoluci�n.: " + ev.getValorActual(),
										FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(new Phrase(lineSpacing,
								"Usuario devoluci�n..: " + Calculos.cortarDescripcion(
										ev.getUsuarioId().getNombre() + " " + ev.getUsuarioId().getApellido(),
										maxTamEvento),
								FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						documento.add(new Paragraph(
								new Phrase(lineSpacing, "", FontFactory.getFont(FontFactory.COURIER, fntSize)))); // slogan
						break;
					default:
						break;
					}
				}
			}
		}
		documento.close();
		if ("false".equals(exportar)) {
			Usuario usuario = (Usuario) sessionMap.get("userLogin");
			String impresara = impresora("1");
			PrinterJob job = PrinterJob.getPrinterJob();
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			log.info("Number of printers configured: " + printServices.length);
			for (PrintService printer : printServices) {
				log.info("Printer: " + printer.getName());
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

		return carpeta + pdf ;
	}

	public void guardar() {
		log.info("guardar informes diarios");
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
		log.info("informe diario usuario no propietario");
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

	public void entadasSalidas() throws ParseException {	
		if (validarfiltros()) {
			List<Usuario> usu = usuarioService.getByAll();	
			Boolean conCierre = Boolean.FALSE;
			Double totalTemp = 0.0;
			Double totalIvaTemp = 0.0;
			Double totalIva19Temp = 0.0;
			Double totalIva5Temp = 0.0;
			Double totalBase5Temp = 0.0;
			Double totalBase19Temp = 0.0;
			Double totalExcentoTemp = 0.0;
			Long tipoDocumentoId = 2l; // tipo documento factura de salida
			Date hoy = Calculos.fechaInicial(getFechaInicio());
			Date hoyfin = Calculos.fechaFinal(getFechafin());
			List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin, usu, conCierre);
			for (Documento d : factDia) {
				if (d.getTotal() != null) {
					totalTemp = totalTemp + d.getTotal();
				}
				if (d.getIva() != null) {
					totalIvaTemp = totalIvaTemp + d.getIva();
				}
				if (d.getIva19() != null) {
					totalIva19Temp = totalIva19Temp + d.getIva19();
				}
				if (d != null && d.getIva5() != null) {
					totalIva5Temp = totalIva5Temp + d.getIva5();
				}
				if (d.getBase5() != null) {
					totalBase5Temp = totalBase5Temp + d.getBase5();
				}
				if (d.getBase19() != null) {
					totalBase19Temp = totalBase19Temp + d.getBase19();
				}
				if (d != null && d.getExcento() != null) {
					totalExcentoTemp = totalExcentoTemp + d.getExcento();
				}
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
		String userPropietario = (String) sessionMap.get("userPropietario");
		if (!validarfiltros()) {
			return;
		}
		try {
			setReduccionList(null);
			setDocumentosReduccionList(null);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			List<InfoDiario> infoList = documentoService.buscarInfodiarioByFecha(
					Calculos.fechaInicial(getFechaInicio()), Calculos.fechaFinal(getFechafin()));
			log.info("fecha: " + getFechaInicio());
			Double porcenta = ((getReduccion() == null ? 0.0 : getReduccion()) / 100);
			for (Long i = hoy; i <= hoyfin; i++) {
				InfoDiario rvo = new InfoDiario();
				for (InfoDiario r : infoList) {
					if (df.format(r.getFechaInforme()).equals(i.toString())) {
						rvo = r;
						Double costoReducido = (rvo.getCostoReducido() == null
								? rvo.getCostoOriginal() - (rvo.getCostoOriginal() * porcenta)
								: rvo.getCostoReducido());
						Double iva19Reducido = (rvo.getIva19Reducido() == null
								? (rvo.getIva19() - (rvo.getIva19() * porcenta))
								: rvo.getIva19Reducido());
						Double iva5Reducido = (rvo.getIva5Reducido() == null
								? (rvo.getIva5() - (rvo.getIva5() * porcenta))
								: rvo.getIva5Reducido());
						Double base19Reducido = (rvo.getBase19Reducido() == null
								? (rvo.getBase19() - (rvo.getBase19() * porcenta))
								: rvo.getBase19Reducido());
						Double base5Reducido = (rvo.getBase5Reducido() == null
								? (rvo.getBase5() - (rvo.getBase5() * porcenta))
								: rvo.getBase5Reducido());
						Double excentoReducido = (rvo.getExcentorReducido() == null
								? (rvo.getExcento() - (rvo.getExcento() * porcenta))
								: rvo.getExcentorReducido());
						Long porcenReduccion = rvo.getPorcReduccion() == null ? getReduccion() : rvo.getPorcReduccion();
						rvo.setTotalReducido(
								iva19Reducido + iva5Reducido + base19Reducido + base5Reducido + excentoReducido);
						rvo.setCostoReducido(costoReducido);
						rvo.setIvaReducido(iva19Reducido + iva5Reducido);
						rvo.setPorcReduccion(porcenReduccion);
						rvo.setIva19Reducido(iva19Reducido);
						rvo.setIva5Reducido(iva5Reducido);
						rvo.setBase19Reducido(base19Reducido);
						rvo.setBase5Reducido(base5Reducido);
						rvo.setExcentorReducido(excentoReducido);
						break;
					}
				}
				if (rvo.getInfoDiarioId() == null) {
					rvo.setCostoOriginal(0.0);
					rvo.setCostoReducido(0.0);
					rvo.setFechaInforme(df.parse(i.toString()));
					rvo.setFechaIngreso(new Date());
					rvo.setIvaOriginal(0.0);
					rvo.setIva19(0.0);
					rvo.setIva5(0.0);
					rvo.setBase19(0.0);
					rvo.setBase5(0.0);
					rvo.setExcento(0.0);
					rvo.setIvaReducido(0.0);
					rvo.setTotalOriginal(0.0);
					rvo.setTotalReducido(0.0);
					rvo.setTotalRemisiones(0.0);
					rvo.setPorcReduccion(0l);
					rvo.setDocumentoInicio("");
					rvo.setDocumentoFin("");
					rvo.setCantidadDocumentos(0.0);
					rvo.setAvanceEfectivo(0.0);
					rvo.setIva19Reducido(0.0);
					rvo.setIva5Reducido(0.0);
					rvo.setBase19Reducido(0.0);
					rvo.setBase5Reducido(0.0);
					rvo.setExcentorReducido(0.0);
				}
				getReduccionList().add(rvo);
			}
			Double totalTemp = 0.0;
			Double totalIvaTemp = 0.0;
			Double totalIva19Temp = 0.0;
			Double totalIva5Temp = 0.0;
			Double totalBase5Temp = 0.0;
			Double totalBase19Temp = 0.0;
			Double totalExcentoTemp = 0.0;
			for (InfoDiario in : getReduccionList()) {
				totalTemp += in.getTotalReducido();
				totalIvaTemp += in.getIvaReducido();
				totalIva19Temp += in.getIva19Reducido();
				totalIva5Temp += in.getIva5Reducido();
				totalBase5Temp += in.getBase5Reducido();
				totalBase19Temp += in.getBase19Reducido();
				totalExcentoTemp += in.getExcentorReducido();
			}
			setTotal(totalTemp);
			setTotalIva(totalIvaTemp);
			setTotalIva19(totalIva19Temp);
			setTotalIva5(totalIva5Temp);
			setTotalbase19(totalBase19Temp);
			setTotalbase5(totalBase5Temp);
			setTotalExcento(totalExcentoTemp);
			if (userPropietario.equals("true")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(userPropietario.equals("true") ? "Reducci�n Completa" : "Busqueda  Completa"));
			}
		} catch (FactException e) {
			log.error("arror realizando busqueda de informe diario:" + e.getMessage());
		}

	}

	private boolean validarfiltros() {
		FacesContext context = FacesContext.getCurrentInstance();
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
		if (userPropietario.equals("true") && getReduccion() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El % de reduccion es obligario", ""));
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
		// tipoDocumentoId.add(8l);
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		List<Documento> factDia = documentoService.getByfacturasRealesConFecha(tipoDocumentoId, hoy, hoyfin,
				usuario.getUsuarioId(), conCierre, server);
		Double total12 = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total12 += d.getTotal().doubleValue();
			}
		}
		return total12;
	}

	public Double getTotalFaturasMac(Date dia, String mac, Boolean conCierre, Long server) throws ParseException {

		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l);// tipo documento factura de salida
		// tipoDocumentoId.add(8l);
		Date hoy = Calculos.fechaInicial(dia);
		Date hoyfin = Calculos.fechaFinal(dia);
		List<Documento> factDia = documentoService.getByMacAndTipoDocumento(tipoDocumentoId, mac, hoy, hoyfin,
				conCierre, server);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal();
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

		List<Documento> factDia = documentoService.getRemisionesByUsuarioConFecha(tipoDocumentoId, hoy, hoyfin,
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

		List<Documento> factDia = documentoService.getRemisionesByUsuarioConFecha(tipoDocumentoId, hoy, hoyfin,
				usuario.getUsuarioId(), conCierre, server);
		Double total1 = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total1 += d.getTotal().doubleValue();
			}
		}
		return total1;
	}

	public Long getTotalFaturasNoImp() {
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		List<Long> tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		// tipoDocumentoId.add(4l); // tipo documento cotizacion
		// tipoDocumentoId.add(9l); // numero de guia
		List<Documento> factDia = documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId, 1l);
		return (long) (factDia.size());
	}

	public String imprimirCuadre() throws IOException, ParseException {

		return "";
	}

	public StreamedContent getFileXls(InfoDiario info)
			throws DocumentException, IOException, PrinterException, ParseException {
		StreamedContent file = null;
		String ruta = imprimirinforme(info, "true");	
		File f = new File(ruta);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", ruta);
		}
		return file;
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
		return (Empresa) sessionMap.get("empresa");
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
		if (reduccionEntradasList == null) {
			reduccionEntradasList = new ArrayList<>();
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
			totalDiferencia = getTotal() - getTotalEntrada();
		} catch (Exception e) {
			totalDiferencia = 0.0;
		}

		return totalDiferencia;
	}

	public void setTotalDiferencia(Double totalDiferencia) {
		this.totalDiferencia = totalDiferencia;
	}

	public Double getTotalIvaDiferencia() {
		try {
			totalIvaDiferencia = getTotalIva() - getTotalIvaEntrada();
		} catch (Exception e) {
			totalIvaDiferencia = 0.0;
		}

		return totalIvaDiferencia;
	}

	public void setTotalIvaDiferencia(Double totalIvaDiferencia) {
		this.totalIvaDiferencia = totalIvaDiferencia;
	}

	public Double getTotalIva5Diferencia() {
		try {
			totalIva5Diferencia = getTotalIva5() - getTotalIva5Entrada();
		} catch (Exception e) {
			totalIva5Diferencia = 0.0;
		}
		return totalIva5Diferencia;
	}

	public void setTotalIva5Diferencia(Double totalIva5Diferencia) {
		this.totalIva5Diferencia = totalIva5Diferencia;
	}

	public Double getTotalIva19Diferencia() {
		try {
			totalIva19Diferencia = getTotalIva19() - getTotalIva19Entrada();
		} catch (Exception e) {
			totalIva19Diferencia = 0.0;
		}

		return totalIva19Diferencia;
	}

	public void setTotalIva19Diferencia(Double totalIva19Diferencia) {
		this.totalIva19Diferencia = totalIva19Diferencia;
	}

	public Double getTotalbase5Diferencia() {
		try {
			totalbase5Diferencia = getTotalbase5() - getTotalbase5Entrada();
		} catch (Exception e) {
			totalbase5Diferencia = 0.0;
		}

		return totalbase5Diferencia;
	}

	public void setTotalbase5Diferencia(Double totalbase5Diferencia) {
		this.totalbase5Diferencia = totalbase5Diferencia;
	}

	public Double getTotalbase19Diferencia() {
		try {
			totalbase19Diferencia = getTotalbase19() - getTotalbase19Entrada();
		} catch (Exception e) {
			totalbase19Diferencia = 0.0;
		}

		return totalbase19Diferencia;
	}

	public void setTotalbase19Diferencia(Double totalbase19Diferencia) {
		this.totalbase19Diferencia = totalbase19Diferencia;
	}

	public Double getTotalExcentoDiferencia() {
		try {
			totalExcentoDiferencia = getTotalExcento() - getTotalExcentoEntrada();
		} catch (Exception e) {
			totalExcentoDiferencia = 0.0;
		}
		return totalExcentoDiferencia;
	}

	public void setTotalExcentoDiferencia(Double totalExcentoDiferencia) {
		this.totalExcentoDiferencia = totalExcentoDiferencia;
	}

}
