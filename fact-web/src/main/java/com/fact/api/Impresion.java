package com.fact.api;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.jboss.logging.Logger;

import com.fact.beam.Login;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.Empresa;
import com.fact.model.OpcionUsuario;
import com.fact.model.Producto;
import com.fact.model.Usuario;
import com.fact.vo.DocumentoDetalleVo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Clase que contiene los metodos de impresion de las facturas
 * 
 * @author luismg
 *
 */
public class Impresion {

	private static Logger log = Logger.getLogger(Impresion.class);
	private final static String LINEA = "-------------------------------------------------";
	private final static String LINEA_GRANDE = "-------------------------------------------------------------------------------------------------------------------------------";

	/**
	 * Metodo que imprime la factura en formato a5 o media carta
	 * 
	 * @param documentoImp
	 * @param productos
	 * @param usuario
	 * @param config
	 * @param descuentoEnFactura
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws PrinterException
	 * @throws PrintException
	 */
	public static String imprimirBig(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, OpcionUsuario descuentoEnFactura, String impresora, Empresa e)
			throws DocumentException, IOException, PrinterException, PrintException {
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".pdf";
		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		String[] fechaHora = fhoyIni.split(" ");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Double tope = 15.0;// esta variable controla el nuero de productos por
							// pagina en la factura
		Double numPaginas = (double) productos.size();
		numPaginas = Math.ceil(numPaginas / tope);
		String numeroPaginas = Calculos.cortarCantidades(numPaginas, 7);
		int paginaActual = 1;
		int inicio = 0;
		Float yDetalle = 267f;
		Float yDetalle2 = 283f;
		int fin = productos.size();
		PdfReader pdfReader = new PdfReader("C://facturacion/factura_big.pdf");
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdf));
		PdfContentByte canvas = pdfStamper.getOverContent(1);
		log.info("productos: " + fin);
		String fuente = "arial";
		float resta = 0;// se utiliza esta variable si si se necesita bajar o
						// subir todo el texto
		// si hay mas de dos paginas se crean las paginas faltantes
		if (numPaginas >= 2) {
			int pagina = 3;
			for (int i = 1; i < numPaginas; i++) {
				pdfStamper.insertPage(pagina, pdfReader.getPageSize(1));
				log.info("se crea la pagina.:" + pagina);
				pdfStamper.replacePage(pdfReader, 1, pagina);
				pdfStamper.insertPage(pagina + 1, pdfReader.getPageSize(1));
				log.info("se crea la pagina:" + (pagina + 1));
				pdfStamper.replacePage(pdfReader, 2, pagina + 1);
				pagina++;
			}
		}
		String TituloFactura = "";
		String TituloFactura1 = "";
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA";
			TituloFactura1 = "DE VENTA";
			break;
		case "9":
			TituloFactura = "GUÍA DE ";
			TituloFactura1 = "REMISIÓN";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			TituloFactura1 = "";
			break;
		default:
			break;
		}
		String execto = "";
		String gravado = "";
		String iva = "";
		String total = "";
		// si valida si se desea que el descuento se refleje en la factura
		if (descuentoEnFactura != null) {
			execto = Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13);
			gravado = Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 21);
			iva = Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13);
			// total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal() +
			// descuento), 16);
			total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 16);
		} else {
			execto = Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13);
			gravado = Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 21);
			iva = Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13);
			total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 16);
		}
		String peso = Calculos.cortarCantidades(formatea.format(documentoImp.getPesoTotal()), 16);
		String resolucion = Calculos.cortarCantidades(e.getResolucionDian(), 23);
		String fechaResolucion = Calculos.cortarDescripcion(e.getFechaResolucion(), 10);
		String desde = Calculos.cortarCantidades(e.getAutorizacionDesde(), 12);
		String hasta = Calculos.cortarCantidades(e.getAutorizacionHasta(), 10);

		for (int i = 1; i < numPaginas * 2; i += 2) {
			// encabezado factura principal
			canvas = pdfStamper.getOverContent(i);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, TituloFactura, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)),
					430f, 360, 0);// tituo
			// factura
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, TituloFactura1, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)),
					430f, 350, 0);// tituo
			// factura1
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, e.getLetraConsecutivo() + documentoImp.getConsecutivoDian(),
							FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)),
					500f, 355, 0);// # DOCUMENTO
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + documentoImp.getDocumentoId(), FontFactory.getFont(fuente, fntSize)),
					50f, 334 - resta, 0);// guia
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
					documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre() + " "
							+ documentoImp.getUsuarioId().getApellido(),
					FontFactory.getFont(fuente, fntSize)), 190f, 334 - resta, 0);// cliente
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[0], FontFactory.getFont(fuente, fntSize)), 385f, 334 - resta,
					0);// fecha
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[1], FontFactory.getFont(fuente, fntSize)), 500f, 330 - resta,
					0);// hora
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing,
							"" + documentoImp.getClienteId().getNombre() + " "
									+ documentoImp.getClienteId().getDireccion(),
							FontFactory.getFont(fuente, fntSize)),
					60f, 318 - resta, 0);// cajero
			String telCliente = "" + documentoImp.getClienteId().getCelular();
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, telCliente, FontFactory.getFont(fuente, fntSize)), 259f, 315 - resta, 0);
			ColumnText
					.showTextAligned(
							canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
									documentoImp.getClienteId().getDocumento(), FontFactory.getFont(fuente, fntSize)),
							45f, 303 - resta, 0);// nit
			// fin encabezado factura principal

			// pie de pagina factura principal
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, execto, FontFactory.getFont(fuente, fntSize)), 70f, 76, 0);// execto
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, gravado, FontFactory.getFont(fuente, fntSize)), 170f, 76, 0);// gravado
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, iva, FontFactory.getFont(fuente, fntSize)), 289f, 76, 0);// iva
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, peso, FontFactory.getFont(fuente, fntSize)), 410f, 76, 0);// peso
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, total, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 463f,
					58, 0);// total
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, resolucion, FontFactory.getFont(fuente, fntSize - 1)), 55f, 41, 0);// resolucion
																												// dian
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, fechaResolucion, FontFactory.getFont(fuente, fntSize - 1)), 160f, 41, 0);// fecha
																														// resolcion
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, desde, FontFactory.getFont(fuente, fntSize - 1)), 215f, 41, 0);// desde
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, hasta, FontFactory.getFont(fuente, fntSize - 1)), 265f, 41, 0);// hasta
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, Calculos.cortarCantidades("" + i, 1), FontFactory.getFont(fuente, fntSize)),
					313f, 26, 0);// pagina actual
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, numeroPaginas, FontFactory.getFont(fuente, fntSize)), 320f, 26, 0);// numero
																												// de
																												// paginas
			// fin pie de pagina factura principal

			// inico encabezado control de entrega
			canvas = pdfStamper.getOverContent(i + 1);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + documentoImp.getDocumentoId(), FontFactory.getFont(fuente, fntSize)),
					50f, 360, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
					documentoImp.getClienteId().getNombre(), FontFactory.getFont(fuente, fntSize)), 175f, 360, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[0], FontFactory.getFont(fuente, fntSize)), 380f, 360, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[1], FontFactory.getFont(fuente, fntSize)), 510f, 360, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + documentoImp.getUsuarioId().getUsuarioId() + " "
							+ documentoImp.getUsuarioId().getNombre() + " " + documentoImp.getUsuarioId().getApellido(),
							FontFactory.getFont(fuente, fntSize)),
					60f, 346, 0);
			telCliente = "" + documentoImp.getClienteId().getCelular();
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, telCliente, FontFactory.getFont(fuente, fntSize)), 259f, 346, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
					documentoImp.getClienteId().getDocumento(), FontFactory.getFont(fuente, fntSize)), 40f, 331, 0);
			// fin encabezado control de entrega

			// inicio pie control de entrega
			// Image imar=
			// Calculos.generarCodBaras(documentoImp.getDocumentoId());
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + i + "  " + numeroPaginas, FontFactory.getFont(fuente, fntSize)), 391f,
					45, 0);
			// fin pie control de entrega
		}

		try {
			int pagina = 1;
			for (int j = inicio; j < fin; j++) {
				canvas = pdfStamper.getOverContent(paginaActual);
				String varios = " ";
				if (productos.get(j).getProductoId().getVarios() != null) {
					varios = productos.get(j).getProductoId().getVarios() == 1l ? "V" : "   ";
				}
				String cantidadProducto = Calculos.cortarCantidades(productos.get(j).getCantidad(), 11);
				String nombreProducto = Calculos.cortarDescripcion(productos.get(j).getProductoId().getNombre(), 34);
				String iva1 = Calculos.cortarCantidades(formatea.format(productos.get(j).getProductoId().getIva()), 6);
				String unidadProducto = "";
				String totalProducto = "";
				// si valida si se desea que el descuento se refleje en la
				// factura
				if (descuentoEnFactura == null) {
					unidadProducto = Calculos
							.cortarCantidades(formatea.format(productos.get(j).getProductoId().getCostoPublico()), 13);
					totalProducto = Calculos.cortarCantidades(formatea.format(
							productos.get(j).getProductoId().getCostoPublico() * productos.get(j).getCantidad()), 11);
				} else {
					unidadProducto = Calculos.cortarCantidades(formatea.format(productos.get(j).getUnitario()), 13);
					totalProducto = Calculos.cortarCantidades(
							formatea.format(productos.get(j).getUnitario() * productos.get(j).getCantidad()), 11);
				}
				// productos factura
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, " (" + varios + ") ", FontFactory.getFont(fuente, fntSize + 2)), 21f,
						yDetalle, 0);// varios
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, cantidadProducto, FontFactory.getFont(fuente, fntSize + 2)), 30f,
						yDetalle, 0);// cantidad
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, nombreProducto, FontFactory.getFont(fuente, fntSize + 2)), 80f,
						yDetalle, 0);// nombre producto
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing, unidadProducto,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 375f, yDetalle, 0);// precio
																										// unitario
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing, totalProducto,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 456f, yDetalle, 0);// parcial
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, iva1 + "%", FontFactory.getFont(fuente, fntSize + 1)), 538f, yDetalle,
						0); // iva
				// productos entrega mercancia
				canvas = pdfStamper.getOverContent(paginaActual + 1);
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, " (" + varios + ") ", FontFactory.getFont(fuente, fntSize + 2)), 25f,
						yDetalle2, 0);
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, cantidadProducto, FontFactory.getFont(fuente, fntSize + 2)), 50f,
						yDetalle2, 0);
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, nombreProducto, FontFactory.getFont(fuente, fntSize + 2)), 130f,
						yDetalle2, 0);
				yDetalle -= 12;
				yDetalle2 -= 12;
				if (j >= tope * pagina) {
					yDetalle = 267f;
					yDetalle2 = 283f;
					paginaActual += 2;
					pagina++;
					// break;
				}
			}
			// Image imar=
			// Calculos.generarCodBaras(documentoImp.getDocumentoId());
			pdfStamper.close();
			pdfReader.close();
			printer(impresora, pdf, config);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		return "";
	}
	
	/**
	 * Metodo que imprime la factura en formato a5 o media carta
	 * 
	 * @param documentoImp
	 * @param productos
	 * @param usuario
	 * @param config
	 * @param descuentoEnFactura
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws PrinterException
	 * @throws PrintException
	 */
	public static String imprimirPDFBigMedia(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, OpcionUsuario descuentoEnFactura, String impresora, Empresa e)
			throws DocumentException, IOException, PrinterException, PrintException {
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".pdf";
		float fntSize, lineSpacing;
		fntSize = 8f;
		lineSpacing = 10f;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		String[] fechaHora = fhoyIni.split(" ");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Double tope = 15.0;// esta variable controla el nuero de productos por
							// pagina en la factura
		Double numPaginas = (double) productos.size();
		numPaginas = Math.ceil(numPaginas / tope);
		String numeroPaginas = Calculos.cortarCantidades(numPaginas, 7);
		int paginaActual = 1;
		int inicio = 0;
		Float yDetalle = 262f;
		int fin = productos.size();
		PdfReader pdfReader = new PdfReader("C://facturacion/factura_big.pdf");
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdf));
		PdfContentByte canvas = pdfStamper.getOverContent(1);
		log.info("productos: " + fin);
		String fuente = "arial";
		float resta = 0;// se utiliza esta variable si si se necesita bajar o
						// subir todo el texto
		// si hay mas de dos paginas se crean las paginas faltantes
		if (numPaginas >= 2) {
			int pagina = 3;
			for (int i = 1; i < numPaginas; i++) {
				pdfStamper.insertPage(pagina, pdfReader.getPageSize(1));
				log.info("se crea la pagina.:" + pagina);
				pdfStamper.replacePage(pdfReader, 1, pagina);
				pdfStamper.insertPage(pagina + 1, pdfReader.getPageSize(1));
				log.info("se crea la pagina:" + (pagina + 1));
				pdfStamper.replacePage(pdfReader, 2, pagina + 1);
				pagina++;
			}
		}
		String TituloFactura = "";
		
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA DE VENTA";
			break;
		case "9":
			TituloFactura = "GUÍA DE REMISIÓN";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			break;
		default:
			break;
		}
		String execto = "";
		String gravado = "";
		String iva = "";
		String total = "";
		// si valida si se desea que el descuento se refleje en la factura
		if (descuentoEnFactura != null) {
			execto = Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13);
			gravado = Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 21);
			iva = Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13);
			// total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal() +
			// descuento), 16);
			total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 16);
		} else {
			execto = Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13);
			gravado = Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 21);
			iva = Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13);
			total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 16);
		}
		String peso = Calculos.cortarCantidades(formatea.format(documentoImp.getPesoTotal()), 16);
		String resolucion = Calculos.cortarCantidades(e.getResolucionDian(), 23);
		String fechaResolucion = Calculos.cortarDescripcion(e.getFechaResolucion(), 10);
		String desde = Calculos.cortarCantidades(e.getAutorizacionDesde(), 12);
		String hasta = Calculos.cortarCantidades(e.getAutorizacionHasta(), 10);

		Image imagen = null;
		try {
			imagen = Image.getInstance("C://facturacion/logoEmpresa.jpg");
			imagen.scaleAbsoluteWidth(180f);
			imagen.scaleAbsoluteHeight(50f);
			imagen.setAbsolutePosition(35f, 345);
		} catch (Exception e2) {
			// TODO: handle exception
		}

		
		for (int i = 1; i < numPaginas * 2; i += 2) {
			// encabezado factura principal
			canvas = pdfStamper.getOverContent(i);
			
			pdfStamper.getOverContent(1).addImage(imagen);
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, TituloFactura, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize+2)),
					430f, 375, 0);// tituo
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, e.getRepresentante()+" "+e.getNit()+" "+e.getRegimen(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize-1)),
					30f, 340, 0);// tituo
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, e.getDireccion()+" TEL: "+e.getCel(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize-1 )),
					30f, 330, 0);// tituo
			
			// factura
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, e.getLetraConsecutivo() + documentoImp.getConsecutivoDian(),
							FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)),
					510f, 360, 0);// # DOCUMENTO
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[0]+" "+fechaHora[1], FontFactory.getFont(fuente, fntSize)), 450f, 330 - resta,
					0);// fecha
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing,
							"" + documentoImp.getClienteId().getNombre() + " "
									,
							FontFactory.getFont(fuente, fntSize)),
					80f, 312 - resta, 0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing,
							"" + documentoImp.getClienteId().getDireccion(),
							FontFactory.getFont(fuente, fntSize)),
					85f, 302 - resta, 0);
			
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "Hoja "+i+" de "+numeroPaginas, FontFactory.getFont(fuente, fntSize)), 280f, 325, 0);// numero
																												// de
																												// paginas

					// fin encabezado factura principal

			// pie de pagina factura principal
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, total, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 463f,
					68, 0);// total
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, total, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 463f,
					48, 0);// total
			
				}

		try {
			int pagina = 1;
			for (int j = inicio; j < fin; j++) {
				canvas = pdfStamper.getOverContent(paginaActual);
				String cantidadProducto = Calculos.cortarCantidades(productos.get(j).getCantidad(), 11);
				String codigoProducto = Calculos.cortarCantidades(""+productos.get(j).getProductoId().getProductoId(), 11);
				String nombreProducto = Calculos.cortarDescripcion(productos.get(j).getProductoId().getNombre(), 34);
				String iva1 = Calculos.cortarCantidades(formatea.format(productos.get(j).getProductoId().getIva()), 6);
				String unidadProducto = "";
				String totalProducto = "";
				// si valida si se desea que el descuento se refleje en la
				// factura
				if (descuentoEnFactura == null) {
					unidadProducto = Calculos
							.cortarCantidades(formatea.format(productos.get(j).getProductoId().getCostoPublico()), 13);
					totalProducto = Calculos.cortarCantidades(formatea.format(
							productos.get(j).getProductoId().getCostoPublico() * productos.get(j).getCantidad()), 11);
				} else {
					unidadProducto = Calculos.cortarCantidades(formatea.format(productos.get(j).getUnitario()), 13);
					totalProducto = Calculos.cortarCantidades(
							formatea.format(productos.get(j).getUnitario() * productos.get(j).getCantidad()), 11);
				}
				// productos factura
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, codigoProducto, FontFactory.getFont(fuente, fntSize )), 35f,
						yDetalle, 0);// cantidad
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, cantidadProducto, FontFactory.getFont(fuente, fntSize )), 80f,
						yDetalle, 0);// cantidad
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, nombreProducto, FontFactory.getFont(fuente, fntSize )), 110f,
						yDetalle, 0);// nombre producto
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing, unidadProducto,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize )), 325f, yDetalle, 0);// precio
																										// unitario
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing, totalProducto,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize )), 506f, yDetalle, 0);// parcial
				yDetalle -= 12;
				if (j >= tope * pagina) {
					yDetalle = 262f;
					paginaActual += 2;
					pagina++;
					// break;
				}	
			}
			// Image imar=
			// Calculos.generarCodBaras(documentoImp.getDocumentoId());
			pdfStamper.close();
			pdfReader.close();
			printer(impresora, pdf, config);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		return pdf;
	}
	

	public static String imprimirEntadaAlmacenPDF(Documento documentoImp, List<DocumentoDetalleVo> productos,
			Usuario usuario, Configuracion config, String impresora, Empresa e)
			throws MalformedURLException, IOException, DocumentException {
		log.info("imprimir entrada almacen");
		String pdf = "C:\\facturas\\entradas\\entrada_" + documentoImp.getDocumentoId() + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen = null;
		try {
			imagen = Image.getInstance("C://facturacion/logoEmpresa.jpg");
			imagen.scaleAbsoluteWidth(200f);
			imagen.scaleAbsoluteHeight(80f);
		} catch (FactException e2) {
			log.info("impresion: sin imagen");
		}
		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		PdfWriter.getInstance(documento, archivo);
		documento.setMargins(10, 1, 1, 1);
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		if (imagen != null) {
			documento.add(imagen); // LEGAL
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
																													// EMPRESA
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getSlogan() == null ? "" : e.getSlogan(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getRepresentante(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion() + " - " + e.getBarrio(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
																			// Y
																			// BARRIO
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		documento.add(new Paragraph(new Phrase(lineSpacing, "Entrada de almacen: " + documentoImp.getDocumentoId(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, 11f)))); // numer
																		// de
																		// factura
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "FECHA: " + fhoyIni, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
						+ " " + documentoImp.getUsuarioId().getApellido(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "CAJA: " + Calculos.conseguirMAC(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing, "PROVEEDOR: "
				+ (documentoImp.getProveedorId() == null ? "VARIOS" : documentoImp.getProveedorId().getNombre()),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getProveedorId().getDocumento() == null ? ""
						: documentoImp.getProveedorId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		documento.add(new Paragraph(new Phrase(lineSpacing, "CANT Descripción      UNI  TOTAL  " + impuesto,
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		for (DocumentoDetalleVo ddV : productos) {
			// descripcion
			String nombre = "";
			int maxTamañoNombre = config.getNombreProductoLargo() == 1l ? 24 : 17;
			nombre = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), maxTamañoNombre);

			// Cantidad
			String cant = "";
			int maxTamañoCant = 3;
			cant = Calculos.cortarCantidades(ddV.getCantidad(), maxTamañoCant);

			// Unitario
			String unit = "";
			int maxTamañoUnit = 5;
			unit = Calculos.cortarCantidades(ddV.getUnitario(), maxTamañoUnit);

			// total
			Double totalDouble = 0.0;
			String total = "";
			int maxTamañoTotal = 6;
			try {
				totalDouble = ddV.getUnitario() * ddV.getCantidad();
			} catch (Exception e2) {
				totalDouble = 0.0;
			}
			total = Calculos.cortarCantidades(totalDouble, maxTamañoTotal);

			// iva
			String iva = "";
			iva = Calculos.cortarCantidades(ddV.getProductoId().getIva(), 2);
			if (config.getNombreProductoLargo() == 1l) {
				documento.add(new Paragraph(new Phrase(lineSpacing, cant + " " + nombre,
						FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // CANTIDAD
																				// NOMBRE
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "                  " + unit + " " + total + "  " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			} else {
				documento.add(new Paragraph(
						new Phrase(lineSpacing, cant + " " + nombre + " " + unit + " " + total + " " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // CANTIDAD
																							// NOMBRE
			}
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Exento:          " + Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Gravado:         " + Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Retefuente:         " + Calculos.cortarCantidades(formatea.format(documentoImp.getRetefuente()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // retefuente
		documento.add(new Paragraph(new Phrase(lineSpacing,
				(e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing, "TOTAL A PAGAR: " + formatea.format(documentoImp.getTotal()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, 14f)))); // REPRESENTANTE
																				// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL

		documento.add(new Paragraph(new Phrase(lineSpacing, "  *****GRACIAS POR SU COMPRA*****      ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, "         Software  NICESOTF            ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "LUIS MIGUEL GONZALEZ  Cel 3185222474   ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "JOHAN ANDRES ORDOÑEZ  Cel 3112864974  ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.close();

		printer(impresora, pdf, config);

		return pdf;
	}

	/**
	 * Metodo encargado de imprimir la factura en formato txt
	 * 
	 * @param documentoImp
	 * @param productos
	 * @param usuario
	 * @return retorna la ruta donde se almacena en disco
	 * @throws DocumentException
	 * @throws IOException
	 * @throws PrinterException
	 * @throws PrintException
	 */
	public static String imprimirPDF(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, String impresora, String enPantalla, Empresa empresa)
			throws DocumentException, IOException, PrinterException, PrintException {
		log.info("todo el codigo de imprimir");
		Empresa e = empresa;
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId()+(enPantalla.equals("true")?"_pantalla":"") + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen = null;
		try {
			imagen = Image.getInstance("C://facturacion/logoEmpresa.jpg");
			imagen.scaleAbsoluteWidth(200f);
			imagen.scaleAbsoluteHeight(80f);
		} catch (Exception e2) {
			// TODO: handle exception
		}

		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		PdfWriter.getInstance(documento, archivo);
		documento.setMargins(10, 1, 1, 1);
		String TituloFactura = "";
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA DE VENTA";
			break;
		case "9":
			TituloFactura = "FACTURA DE VENTA.";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";

			break;
		default:
			break;
		}
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		if (imagen != null) {
			documento.add(imagen); // LEGAL
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
																													// EMPRESA
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "" + e.getSlogan().toUpperCase() == null ? "" : e.getSlogan(),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getRepresentante(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion() + " - " + e.getBarrio(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
																			// Y
																			// BARRIO
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		documento.add(new Paragraph(new Phrase(lineSpacing, TituloFactura + ": " + documentoImp.getConsecutivoDian(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, 11f)))); // numer
																		// de
																		// factura
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "FECHA: " + fhoyIni, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
						+ " " + documentoImp.getUsuarioId().getApellido(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "CAJA: " + Calculos.conseguirMAC(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CLIENTE: "
						+ (documentoImp.getClienteId() == null ? "VARIOS" : documentoImp.getClienteId().getNombre()),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getClienteId().getDocumento() == null ? ""
						: documentoImp.getClienteId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		if (documentoImp.getEmpleadoId() != null) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "MESERO: " + documentoImp.getEmpleadoId().getNombre(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		}
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		documento.add(new Paragraph(new Phrase(lineSpacing, "CANT Descripción      UNI  TOTAL  " + impuesto,
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		for (DocumentoDetalleVo ddV : productos) {
			// descripcion
			String nombre = "";
			int maxTamañoNombre = config.getNombreProductoLargo() == 1l ? 24 : 17;
			nombre = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), maxTamañoNombre);

			// Cantidad
			String cant = "";
			int maxTamañoCant = 3;
			cant = Calculos.cortarCantidades(ddV.getCantidad(), maxTamañoCant);

			// Unitario
			String unit = "";
			int maxTamañoUnit = 5;
			unit = Calculos.cortarCantidades(ddV.getUnitario(), maxTamañoUnit);

			// total
			Double totalDouble = 0.0;
			String total = "";
			int maxTamanoTotal = 6;
			try {
				totalDouble = ddV.getUnitario() * ddV.getCantidad();
			} catch (Exception e2) {
				totalDouble = 0.0;
			}
			total = Calculos.cortarCantidades(totalDouble, maxTamanoTotal);

			// iva
			String iva = "";
			iva = Calculos.cortarCantidades(ddV.getProductoId().getIva(), 2);
			if (config.getNombreProductoLargo() == 1l) {
				documento.add(new Paragraph(new Phrase(lineSpacing, cant + " " + nombre,
						FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // CANTIDAD
																				// NOMBRE
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "                  " + unit + " " + total + "  " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			} else {
				documento.add(new Paragraph(
						new Phrase(lineSpacing, cant + " " + nombre + " " + unit + " " + total + " " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // CANTIDAD
																							// NOMBRE
			}

			// LEGAL
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Exento:          " + Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Gravado:         " + Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				(e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing, "TOTAL A PAGAR: " + formatea.format(documentoImp.getTotal()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, 14f)))); // REPRESENTANTE
																				// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         **** FORMA DE PAGO****        ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		Double pago = documentoImp.getValorTarjeta() == null ? 0l : documentoImp.getValorTarjeta();
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con Tarjeta:  " + Calculos.cortarCantidades(pago, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con cheque:   " + Calculos.cortarCantidades(0.0, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Efectivo:			             " + Calculos.cortarCantidades(
						(documentoImp.getEfectivo() == null ? "0" : "" + documentoImp.getEfectivo()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Cambio:	 		              " + Calculos
						.cortarCantidades((documentoImp.getCambio() == null ? "0" : "" + documentoImp.getCambio()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Res. " + e.getResolucionDian() + " Fecha: " + e.getFechaResolucion(),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Rango autorizado desde: " + e.getAutorizacionDesde() + " a " + e.getAutorizacionHasta(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "Factura: " + e.gettFactura(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "  *****GRACIAS POR SU COMPRA*****      ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, "El servicio voluntario no es obligatorio",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, "puede ser modificado por el cliente.",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         Software  NICESOTF            ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "LUIS MIGUEL GONZALEZ  Cel 3185222474   ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "JOHAN ANDRES ORDOÑEZ  Cel 3112864974  ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.close();
		if (enPantalla.equals("false")) {
			log.info("imprime en papel");
			printer(impresora, pdf, config);
		}

		return pdf;
	}

	/**
	 * Metodo encargado de imprimir la factura en formato txt
	 * 
	 * @param documentoImp
	 * @param productos
	 * @param usuario
	 * @return retorna la ruta donde se almacena en disco
	 * @throws DocumentException
	 * @throws IOException
	 * @throws PrinterException
	 * @throws PrintException
	 */
	public static String imprimirPDFSmall(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, String impresora,String enPantalla, Empresa empresa)
			throws DocumentException, IOException, PrinterException, PrintException {
		log.info("todo el codigo de imprimir");
		Empresa e = empresa;
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId()+(enPantalla.equals("true")?"_pantalla":"") + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen = Image.getInstance("C://facturacion/logoEmpresa.jpg");
		imagen.scaleAbsoluteWidth(120f);
		imagen.scaleAbsoluteHeight(50f);
		float fntSize, lineSpacing;
		fntSize = 6f;
		lineSpacing = 10f;
		PdfWriter.getInstance(documento, archivo);
		documento.setMargins(9, 1, 1, 1);
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		documento.add(imagen); // LEGAL
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 10f)))); // NOMBRE
																													// EMPRESA
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getSlogan() == null ? "" : e.getSlogan(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getRepresentante(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion() + " - " + e.getBarrio(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
																			// Y
																			// BARRIO
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		documento.add(new Paragraph(new Phrase(lineSpacing, "FACTURA DE VENTA: " + documentoImp.getConsecutivoDian(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, 9f)))); // numer
																		// de
																		// factura
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Fecha: " + fhoyIni, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
						+ " " + documentoImp.getUsuarioId().getApellido(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "CAJA: " + Calculos.conseguirMAC(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CLIENTE: "
						+ (documentoImp.getClienteId() == null ? "VARIOS" : documentoImp.getClienteId().getNombre()),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getClienteId().getDocumento() == null ? ""
						: documentoImp.getClienteId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		documento.add(new Paragraph(new Phrase(lineSpacing, "CANT Descripción      UNI  TOTAL  " + impuesto,
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		for (DocumentoDetalleVo ddV : productos) {
			// descripcion
			String nombre = "";
			int maxTamañoNombre = config.getNombreProductoLargo() == 1l ? 24 : 17;
			nombre = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), maxTamañoNombre);

			// Cantidad
			String cant = "";
			int maxTamañoCant = 3;
			cant = Calculos.cortarCantidades(ddV.getCantidad(), maxTamañoCant);

			// Unitario
			String unit = "";
			int maxTamañoUnit = 5;
			unit = Calculos.cortarCantidades(ddV.getUnitario(), maxTamañoUnit);

			// total
			Double totalDouble = 0.0;
			String total = "";
			int maxTamañoTotal = 6;
			try {
				totalDouble = ddV.getUnitario() * ddV.getCantidad();
			} catch (Exception e2) {
				totalDouble = 0.0;
			}
			total = Calculos.cortarCantidades(totalDouble, maxTamañoTotal);

			// iva
			String iva = "";
			iva = Calculos.cortarCantidades(ddV.getProductoId().getIva(), 2);
			if (config.getNombreProductoLargo() == 1l) {
				documento.add(new Paragraph(new Phrase(lineSpacing, cant + " " + nombre,
						FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // CANTIDAD
																				// NOMBRE
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "                  " + unit + " " + total + "  " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			} else {
				documento.add(new Paragraph(
						new Phrase(lineSpacing, cant + " " + nombre + " " + unit + " " + total + " " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // CANTIDAD
																							// NOMBRE
			}

			// LEGAL
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Exento:          " + Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Gravado:         " + Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				(e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing, "TOTAL A PAGAR: " + formatea.format(documentoImp.getTotal()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, 9f)))); // REPRESENTANTE
																				// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         **** FORMA DE PAGO****        ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		Double pago = documentoImp.getValorTarjeta() == null ? 0l : documentoImp.getValorTarjeta();
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con Tarjeta:  " + Calculos.cortarCantidades(pago, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con cheque:   " + Calculos.cortarCantidades(0.0, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Efectivo:			           " + Calculos.cortarCantidades(
						(documentoImp.getEfectivo() == null ? "0" : "" + documentoImp.getEfectivo()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Cambio:	 		            " + Calculos
						.cortarCantidades((documentoImp.getCambio() == null ? "0" : "" + documentoImp.getCambio()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "Res. " + e.getResolucionDian() + " Fecha: " + e.getFechaResolucion(),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Rango autorizado desde: " + e.getAutorizacionDesde() + " a " + e.getAutorizacionHasta(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "Factura: " + e.gettFactura(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "  *****GRACIAS POR SU COMPRA*****      ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         Software  NICESOTF            ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "LUIS MIGUEL GONZALEZ  Cel 3185222474   ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "JOHAN ANDRES ORDOÑEZ  Cel 3112864974  ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.close();
		if (enPantalla.equals("false")) {
			log.info("imprime en papel");
			printer(impresora, pdf, config);
		}
		
		return pdf;
	}

	public static void printer(String impresora, String rutaArchivo, Configuracion configuracion) {
		PrinterJob job = PrinterJob.getPrinterJob();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		log.info("Number of printers configured1: " + printServices.length);
		for (PrintService printer : printServices) {
			log.info("Printer: " + printer.getName());
			log.info("comparacion:" + impresora + ":" + printer.getName());
			if (printer.getName().equals(impresora)) {
				try {
					job.setPrintService(printer);
					log.info("coincide:" + impresora + " : " + printer.getName());
					break;
				} catch (PrinterException ex) {
					ex.printStackTrace();
				}
			}
		}
		PDDocument document = null;
		try {
			document = PDDocument.load(new File(rutaArchivo));
			job.setPageable(new PDFPageable(document));
			try {
				log.info("imprime doc...");
				job.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (configuracion.getGuardarFacturas() == 0l) {
			File borrar = new File(rutaArchivo);
			if (!borrar.delete()) {
				log.error("Error borrando facturas");
			} else {
				log.info("Documento borrado");
			}
		}

	}

	/**
	 * metodo encargado de realizar la impresion del inventario
	 * 
	 * @param productos
	 * @param usuario
	 * @return
	 */
	public static String imprimirInventarioPDF(List<Producto> productos, Usuario usuario, Configuracion con,
			String impresora) {
		log.info("todo el codigo de imprimir");
		Empresa e = Login.getEmpresaLogin();
		String carpeta = "C:\\facturacion\\inventarios";
		SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
		String fhoyIni = df2.format(new Date());
		String pdf = "\\inventario_" + fhoyIni + ".pdf";
		File folder = new File(carpeta);
		folder.mkdirs();
		FileOutputStream archivo = null;
		try {
			archivo = new FileOutputStream(carpeta + pdf);
		} catch (FileNotFoundException e1) {
			log.error("Error creando archivo de inventario pdf");
			e1.printStackTrace();
		}
		Document documento = new Document();
		float fntSize, lineSpacing;
		fntSize = 9f;
		lineSpacing = 10f;
		try {
			PdfWriter.getInstance(documento, archivo);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		documento.setMargins(1, 1, 1, 1);
		documento.open();
		try {
			documento.add(new Paragraph(new Phrase(lineSpacing, LINEA)));
			documento.add(new Paragraph(new Phrase(lineSpacing, ">>" + e.getNombre() + "<<",
					FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
																			// EMPRESA
			documento.add(new Paragraph(new Phrase(lineSpacing, e.getSlogan() == null ? "" : e.getSlogan(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // slogan
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getRepresentante(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																				// LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getBarrio(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // barrio
			documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
			documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
			documento.add(new Paragraph(new Phrase(lineSpacing, "			    INVENTARIO",
					FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
			documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
			// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
			documento.add(new Paragraph(new Phrase(lineSpacing, "Descripción                       CANT ",
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE

			for (Producto p : productos) {
				// descripcion
				String nombre = "";
				int maxTamañoNombre = 31;
				nombre = Calculos.cortarDescripcion(p.getNombre(), maxTamañoNombre);
				// cantidad
				String cant = "";
				int maxTamañoCant = 4;
				cant = Calculos.cortarCantidades(p.getCantidad(), maxTamañoCant);
				documento.add(new Paragraph(new Phrase(lineSpacing, nombre + " " + cant,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
			}
			documento.close();
			printer(impresora, carpeta + pdf, con);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	public static String imprimirTxt(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, String impresora, String enPantalla, Empresa e) throws IOException {
		log.info("entra a imprimir txt");
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId()+(enPantalla.equals("true")?"_pantalla":"")+ ".txt";
		File archivo = new File(pdf);
		BufferedWriter bw;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		String TituloFactura = "";
		int tamanoTxt = 40;
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA DE VENTA";
			break;
		case "9":
			TituloFactura = "FACTURA DE VENTA.";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			break;
		case "8":
			TituloFactura = "VALE";
			break;
		default:
			break;
		}
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("----------------------------------------\n");
		bw.write(Calculos.centrarDescripcion(e.getNombre(), tamanoTxt) + "\n");
		bw.write(Calculos.centrarDescripcion(e.getSlogan().toUpperCase() == null ? "" : e.getSlogan(), tamanoTxt)
				+ "\n");
		bw.write(Calculos.centrarDescripcion(e.getRepresentante(), tamanoTxt) + "\n");
		bw.write(Calculos.centrarDescripcion("NIT. " + e.getNit() + " " + e.getRegimen(), tamanoTxt) + "\n");
		bw.write(Calculos.centrarDescripcion(e.getDireccion() + " " + e.getBarrio(), tamanoTxt) + "\n");
		bw.write(Calculos.centrarDescripcion(e.getCiudad() + "-" + e.getDepartamento(), tamanoTxt) + "\n");
		bw.write(Calculos.centrarDescripcion("TEL: " + e.getTelefonoFijo() + " - " + e.getCel(), tamanoTxt) + "\n");
		bw.write("\n");
		bw.write(TituloFactura + ": " + documentoImp.getConsecutivoDian());
		bw.write("\nFECHA: " + df.format(documentoImp.getFechaRegistro()));
		bw.write("\nCAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " "
				+ documentoImp.getUsuarioId().getNombre() + " " + documentoImp.getUsuarioId().getApellido());
		bw.write("\nCAJA: " + Calculos.conseguirMAC());
		bw.write("\nCLIENTE: "
				+ (documentoImp.getClienteId() == null ? "VARIOS" : documentoImp.getClienteId().getNombre()));
		bw.write("\nNIT/CC:" + (documentoImp.getClienteId().getClienteId()==1l?"0":documentoImp.getClienteId().getDocumento()));
		if (documentoImp.getEmpleadoId() != null) {
			bw.write("\nMESERO: " + documentoImp.getEmpleadoId().getNombre());
		}
		bw.write("\n----------------------------------------\n");
		bw.write("DESCRIPCIÓN       CANT   UNIDAD    TOTAL\n");
		bw.write("----------------------------------------");
		for (DocumentoDetalleVo ddV : productos) {
			String nombreProducto = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), 17);
			String cantidadProducto = Calculos.cortarCantidades(formatea.format(ddV.getCantidad()), 4);
			String unidadProducto = Calculos.cortarCantidades(formatea.format(ddV.getUnitario()), 8);
			String totalProducto = Calculos.cortarCantidades(formatea.format(ddV.getParcial()), 8);
			bw.write("\n" + nombreProducto + " " + cantidadProducto + " " + unidadProducto + " " + totalProducto);
		}
		bw.write("\n----------------------------------------");
		bw.write("\nValor Exento:                "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 11));
		bw.write("\nValor Gravado:               "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 11));
		bw.write("\nIva:                         "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 11));
		bw.write("\n----------------------------------------\n");
		bw.write("\nTOTAL A PAGAR:               "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 11));
		bw.write("\n----------------------------------------\n");
		bw.write("         **** FORMA DE PAGO****        ");
		Double pago = documentoImp.getValorTarjeta() == null ? 0l : documentoImp.getValorTarjeta();
		bw.write("\nVr. Pago con Tarjeta:        " + Calculos.cortarCantidades(formatea.format(pago), 11));
		bw.write("\nVr. Comisión Tarjeta:        " + Calculos.cortarCantidades(formatea.format(0l), 11));
		bw.write("\nVr. Total Factura:           "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 11));
		bw.write("\nEfectivo:		 	         " + Calculos.cortarCantidades(
				formatea.format(documentoImp.getEfectivo() == null ? 0.0 : documentoImp.getEfectivo()), 11));
		bw.write("\nCambio:			             " + Calculos.cortarCantidades(
				formatea.format(documentoImp.getCambio() == null ? 0.0 : documentoImp.getCambio()), 11));
		bw.write("\n----------------------------------------");
		bw.write("\nRes. " + e.getResolucionDian() + " Fecha: " + e.getFechaResolucion());
		bw.write("\nRango autorizado desde: " + e.getAutorizacionDesde() + " a " + e.getAutorizacionHasta());
		bw.write("\nFactura: " + e.gettFactura());
		bw.write("\n");

		bw.write("\n  *****GRACIAS POR SU COMPRA*****   \n");
		bw.write("          Software NICESOFT      \n");
		bw.write(" LUIS MIGUEL GONZALEZ CEL: 3185222474  \n");
		bw.write(" JOHAN ANDRES ORDOÑES CEL: 3112864974  ");
		bw.write("\n");
		bw.write("\n");
		bw.write("\n");
		bw.write(" \n");
		bw.write(" \n");
		bw.write(" \n");
		bw.write(" \n");
		bw.write(" \n");
		bw.close();
		if (enPantalla.equals("false")) {
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
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			log.info("Number of printers configured1: " + printServices.length);
			for (PrintService printer : printServices) {
				log.info("Printer: " + printer.getName());
				log.info("comparacion:" + impresora + ":" + printer.getName());
				if (printer.getName().equals(impresora)) {
					defaultPrintService = printer;
					log.info(impresora + " : " + printer.getName());
					break;
				}
			}

			// defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			if (defaultPrintService != null) {
				DocPrintJob printJob = defaultPrintService.createPrintJob();

				try {
					printJob.print(document, attributeSet);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				log.info("No existen impresoras instaladas");
			}
		}
		return pdf;
	}
	
	public static String imprimirTxtBigMedia(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config, String impresora, String enPantalla, Empresa e) throws IOException {
		log.info("entra a imprimir txt");
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId()+(enPantalla.equals("true")?"_pantalla":"")+ ".txt";
		File archivo = new File(pdf);
		BufferedWriter bw;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		String TituloFactura = "";
		int tamanoTxt = 80;
		Double tope = 9.0;// esta variable controla el nuero de productos por
		// pagina en la factura
		Double numPaginas = (double) productos.size();
		numPaginas = Math.ceil(numPaginas / tope);
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA DE VENTA";
			break;
		case "9":
			TituloFactura = "FACTURA DE VENTA.";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			break;
		case "8":
			TituloFactura = "VALE";
			break;
		default:
			break;
		}
		bw = new BufferedWriter(new FileWriter(archivo));
		int temp=0;
		int cont=0;
		double espacio=tope;
		for(int i=0; i< numPaginas; i++) {
			bw.write(Calculos.agregarEspacio("Pag. "+(i+1)+" de "+numPaginas.intValue(),tamanoTxt-12)+ "\n");
			bw.write(Calculos.centrarDescripcion(e.getNombre(), tamanoTxt) + "\n");
			//bw.write(Calculos.centrarDescripcion(e.getSlogan().toUpperCase() == null ? "" : e.getSlogan()+ "\n", tamanoTxt) + "\n");
			bw.write(Calculos.centrarDescripcion(e.getRepresentante(), tamanoTxt) + "\n");
			bw.write(Calculos.centrarDescripcion("Nit. "+e.getNit()+" "+e.getRegimen(), tamanoTxt) + "\n");
			bw.write(Calculos.centrarDescripcion(e.getDireccion()+" Tel: "+e.getTelefonoFijo()+" "+e.getCiudad()+" - "+e.getDepartamento(), tamanoTxt) + "\n");
			bw.write("Fecha:     "+Calculos.cortarDescripcion(df.format(documentoImp.getFechaRegistro()),20)+TituloFactura+": " +documentoImp.getConsecutivoDian()+ "\n");
			bw.write("Señor(a):  "+Calculos.cortarDescripcion( (documentoImp.getClienteId() == null ? "VARIOS" : documentoImp.getClienteId().getNombre()),37)+" C.C/Nit: "+(documentoImp.getClienteId().getClienteId()==1l?"0":documentoImp.getClienteId().getDocumento()) +"\n");
			bw.write("Dirección: "+Calculos.cortarDescripcion( (documentoImp.getClienteId() == null ? "" : documentoImp.getClienteId().getDireccion()),37)+ " Telefono: "+ Calculos.centrarDescripcion( (documentoImp.getClienteId() == null ? "" : documentoImp.getClienteId().getCelular()),10)+"\n");
			bw.write("Obs:       "+"\n");
			bw.write("Cód.    Descripción                        Canti     unitario    IVA      TOTAL"+"\n");
			bw.write("-------------------------------------------------------------------------------");
			for (int p=temp; p<productos.size(); p++) {
				String idProducto =Calculos.cortarDescripcion(productos.get(p).getProductoId().getProductoId()+"", 7);
				String nombreProducto = Calculos.cortarDescripcion(productos.get(p).getProductoId().getNombre(), 35);
				String cantidadProducto = Calculos.cortarCantidades(formatea.format(productos.get(p).getCantidad()), 4);
				String unidadProducto = Calculos.cortarCantidades(formatea.format(productos.get(p).getUnitario()), 12);
				String ivaProducto = Calculos.cortarCantidades(formatea.format(productos.get(p).getProductoId().getIva()), 6);
				String totalProducto = Calculos.cortarCantidades(formatea.format(productos.get(p).getParcial()), 10);
				bw.write("\n" +idProducto+" "+nombreProducto + " " + cantidadProducto + " " + unidadProducto +" "+ivaProducto+" " + totalProducto);		
				cont++;
				espacio--;
				if(cont==tope) {
					cont=0;
					temp=(int) Math.ceil(tope);
					break;
				}
			}
			for (int p=0; p<espacio; p++) {
				bw.write(" \n");
			}		
			bw.write(" \n");
			String baseGravable= Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13);
			String totalAntesiVA= Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()+documentoImp.getBase19()+documentoImp.getBase5()), 16);
			String totalIVA= Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 16);
			String totalDocumento= Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 16);
			String totalExcluido= Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13);
			String totalGravado= Calculos.cortarCantidades(formatea.format(documentoImp.getBase19()+documentoImp.getBase5()+documentoImp.getIva()), 13);
			String pesoTotal= Calculos.cortarCantidades(formatea.format(documentoImp.getPesoTotal()), 14);
			
			bw.write("Tot. base Grav.: "+baseGravable+ "   Total antes de IVA: "+totalAntesiVA + "\n");
			bw.write("Total Gravado:   "+totalGravado+ "   Total IVA:          "+totalIVA+ "  \n");
			bw.write("Total excluido:  "+totalExcluido+"   Total Documento:    "+totalDocumento+  "\n");
			bw.write(Calculos.agregarEspacio("Peso total Kg: "+pesoTotal ,50)+ "\n");
			bw.write("Resolución DIAN N° "+e.getResolucionDian()+ " Fecha: "+e.getFechaResolucion()+" Desde: "+e.getLetraConsecutivo()+e.getAutorizacionDesde()+" Hasta: "+e.getAutorizacionHasta()+ " \n");
			//bw.write("La presente factura cambiaria de compraventa se asimila a la letra de cambio, de acuerdo al artículo 619 y 774 del "+" \n");
			//bw.write("A partir de 30 dias de la fecha de esta factura o sea el vencimiento se cobran intereses de mora a la tasa maxima."+" \n");
			bw.write("Cajero: "+usuario.getNombre()+" "+usuario.getApellido()+"  Aceptada:_____________________CC_____________"+" FACTURA IMPRESA POR COMPUTADOR"+ "\n");
			bw.write("software realizado por Niceso: www.effectivesoftware.com.co"+" \n");
			bw.write(" \n");
			espacio=tope;
		}
		
		bw.close();
		if (enPantalla.equals("false")) {
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
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			log.info("Number of printers configured1: " + printServices.length);
			for (PrintService printer : printServices) {
				log.info("Printer: " + printer.getName());
				log.info("comparacion:" + impresora + ":" + printer.getName());
				if (printer.getName().equals(impresora)) {
					defaultPrintService = printer;
					log.info(impresora + " : " + printer.getName());
					break;
				}
			}

			// defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			if (defaultPrintService != null) {
				DocPrintJob printJob = defaultPrintService.createPrintJob();

				try {
					printJob.print(document, attributeSet);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				log.info("No existen impresoras instaladas");
			}
		}
		return pdf;
	}

	public static String imprimirPDFPage(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion configuracion, String impresora, String enPantalla, Empresa e)
			throws DocumentException, FileNotFoundException {
		log.info("todo el codigo de imprimir");
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId()+(enPantalla.equals("true")?"_pantalla":"") + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen = null;
		try {
			imagen = Image.getInstance("C://facturacion/logoEmpresa.jpg");
			imagen.scaleAbsoluteWidth(200f);
			imagen.scaleAbsoluteHeight(80f);
		} catch (Exception e2) {
			// TODO: handle exception
		}

		float fntSize, lineSpacing;
		fntSize = 11f;
		lineSpacing = 15f;
		PdfWriter.getInstance(documento, archivo);
		documento.setMargins(10, 10, 20, 20);
		String TituloFactura = "";
		switch ("" + documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "10":
			TituloFactura = "FACTURA DE VENTA";
			break;
		case "9":
			TituloFactura = "GUÍA DE REMISIÓN";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			break;
		case "8":
			TituloFactura = "VALE";
			break;
		default:
			break;
		}
		documento.open();

		if (imagen != null) {
			documento.add(imagen); // LEGAL
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
																													// EMPRESA
																													// //
																													// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion() + " - " + e.getBarrio(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION Y BARRIO
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Resolución. " + e.getResolucionDian() + " Fecha: " + e.getFechaResolucion()
						+ " Rango autorizado desde: " + e.getAutorizacionDesde() + " a " + e.getAutorizacionHasta(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, TituloFactura + ": " + documentoImp.getConsecutivoDian(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, 11f)))); // numer
																		// de
																		// factura
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "FECHA: " + fhoyIni, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
						+ " " + documentoImp.getUsuarioId().getApellido(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "CAJA: " + Calculos.conseguirMAC(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CLIENTE: "
						+ (documentoImp.getClienteId() == null ? "VARIOS" : documentoImp.getClienteId().getNombre()),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getClienteId().getDocumento() == null ? ""
						: documentoImp.getClienteId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		if (documentoImp.getEmpleadoId() != null) {
			documento.add(new Paragraph(new Phrase(lineSpacing, "MESERO: " + documentoImp.getEmpleadoId().getNombre(),
					FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		}
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "CANTIDAD     DESCRIPCION                 UNIDAD       TOTAL     " + impuesto,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL
		for (DocumentoDetalleVo ddV : productos) {
			// descripcion
			String nombre = "";
			int maxTamañoNombre = configuracion.getNombreProductoLargo() == 1l ? 24 : 25;
			nombre = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), maxTamañoNombre);

			// Cantidad
			String cant = "";
			int maxTamañoCant = 8;
			cant = Calculos.cortarCantidades(ddV.getCantidad(), maxTamañoCant);

			// Unitario
			String unit = "";
			int maxTamañoUnit = 5;
			unit = Calculos.cortarCantidades(ddV.getUnitario(), maxTamañoUnit);

			// total
			Double totalDouble = 0.0;
			String total = "";
			int maxTamanoTotal = 6;
			try {
				totalDouble = ddV.getUnitario() * ddV.getCantidad();
			} catch (Exception e2) {
				totalDouble = 0.0;
			}
			total = Calculos.cortarCantidades(totalDouble, maxTamanoTotal);

			// iva
			String iva = "";
			iva = Calculos.cortarCantidades(ddV.getProductoId().getIva(), 2);
			if (configuracion.getNombreProductoLargo() == 1l) {
				documento.add(new Paragraph(new Phrase(lineSpacing, cant + "     " + nombre,
						FontFactory.getFont(FontFactory.COURIER_BOLD, 12f)))); // CANTIDAD
																				// NOMBRE
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "                  " + unit + " " + total + "  " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
			} else {
				documento.add(new Paragraph(new Phrase(lineSpacing,
						cant + "     " + nombre + "   " + unit + "        " + total + "    " + iva,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // CANTIDAD
																					// NOMBRE
			}

			// LEGAL
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Exento:          " + Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Valor Gravado:         " + Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				(e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		double porcRetencion = documentoImp.getClienteId().getRetencion() == null ? 0.0
				: documentoImp.getClienteId().getRetencion();
		porcRetencion = porcRetencion / 100;
		Double rete = ((documentoImp.getTotal() / (1 + porcRetencion)) * porcRetencion);
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Retención:             " + Calculos.cortarCantidades(formatea.format(rete), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing, "TOTAL A PAGAR: " + formatea.format(documentoImp.getTotal()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, 14f)))); // REPRESENTANTE
																				// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         **** FORMA DE PAGO****        ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		Double pago = documentoImp.getValorTarjeta() == null ? 0l : documentoImp.getValorTarjeta();
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con Tarjeta:   " + Calculos.cortarCantidades(pago, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(
				new Paragraph(new Phrase(lineSpacing, "Vr. Pago con cheque:    " + Calculos.cortarCantidades(0.0, 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Efectivo:			             " + Calculos.cortarCantidades(
						(documentoImp.getEfectivo() == null ? "0" : "" + documentoImp.getEfectivo()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"Cambio:	 		              " + Calculos
						.cortarCantidades((documentoImp.getCambio() == null ? "0" : "" + documentoImp.getCambio()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL

		documento.add(new Paragraph(new Phrase(lineSpacing, "Factura: " + e.gettFactura(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "  *****GRACIAS POR SU COMPRA*****      ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, "El servicio voluntario no es obligatorio",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, "puede ser modificado por el cliente.",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "         Software  NICESOTF            ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "LUIS MIGUEL GONZALEZ  Cel 3185222474   ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "JOHAN ANDRES ORDOÑEZ  Cel 3112864974  ",
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																			// LEGAL
		documento.close();
		if (enPantalla.equals("false")) {
			log.info("imprime en papel");
			printer(impresora, pdf, configuracion);
		}

		return pdf;

	}
	
	

	public static void imprimirEntadaAlmacenTXT(Documento documentoImp, List<DocumentoDetalleVo> productos,
			 Configuracion config, String impresora, Empresa e) throws IOException {
		log.info("imprimir entrada almacen txt");
		String carpeta = "C:\\facturas\\entradas";
		String pdf = "\\entrada_" + documentoImp.getDocumentoId() + ".txt";
		File folder = new File(carpeta);
		folder.mkdirs();
		File archivo = new File(carpeta+pdf);
		BufferedWriter bw;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		String TituloFactura = "";
		int tamanoTxt = 40;
		switch (""+documentoImp.getTipoDocumentoId().getTipoDocumentoId()) {
		case "6":
			TituloFactura="SALIDA DE ALMACEN: ";
			break;
		case "2":
			TituloFactura="ENTRADA DE ALMACEN: ";
			break;
		case "1":
			TituloFactura="ENTRADA POR GUIA: ";
			break;	
		default:
			break;
		}
		String fhoyIni = df.format(documentoImp.getFechaRegistro());
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("----------------------------------------\n");
		bw.write(Calculos.centrarDescripcion(e.getNombre(), tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion(e.getSlogan(), tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion(e.getRepresentante(), tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion("NIT. " + e.getNit() + " " + e.getRegimen(), tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion(e.getDireccion() + " - " + e.getBarrio(), tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion(e.getCiudad() + "- " + e.getDepartamento(), tamanoTxt)+"\n");												
		bw.write(Calculos.centrarDescripcion("TEL: " + e.getTelefonoFijo() + " - " + e.getCel()+"\n", tamanoTxt));
		bw.write(TituloFactura + documentoImp.getDocumentoId()+"\n");
		bw.write("FECHA: " + fhoyIni+"\n");
		bw.write("CAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
				+ " " + documentoImp.getUsuarioId().getApellido()+"\n");
		bw.write("CAJA: " + Calculos.conseguirMAC()+"\n");
		bw.write("PROVEEDOR: "+ (documentoImp.getProveedorId() == null ? "VARIOS" : documentoImp.getProveedorId().getNombre())+"\n");
		bw.write("CC/NIT: " + documentoImp.getProveedorId().getDocumento() == null ? ""
				: documentoImp.getProveedorId().getDocumento()+"\n");
		bw.write(LINEA+"\n");			
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		bw.write("CANT Descripción      UNI  TOTAL  " + impuesto+"\n");
		bw.write(LINEA+"\n");	
																			
		for (DocumentoDetalleVo ddV : productos) {
			// descripcion
			String nombre = "";
			int maxTamañoNombre = config.getNombreProductoLargo() == 1l ? 24 : 17;
			nombre = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), maxTamañoNombre);

			// Cantidad
			String cant = "";
			int maxTamañoCant = 3;
			cant = Calculos.cortarCantidades(ddV.getCantidad(), maxTamañoCant);

			// Unitario
			String unit = "";
			int maxTamañoUnit = 5;
			unit = Calculos.cortarCantidades(ddV.getUnitario(), maxTamañoUnit);

			// total
			Double totalDouble = 0.0;
			String total = "";
			int maxTamañoTotal = 6;
			try {
				totalDouble = ddV.getUnitario() * ddV.getCantidad();
			} catch (Exception e2) {
				totalDouble = 0.0;
			}
			total = Calculos.cortarCantidades(totalDouble, maxTamañoTotal);
			// iva
			String iva = "";			
			iva = Calculos.cortarCantidades(ddV.getProductoId().getIva(), 2);			
			bw.write(cant + " " + nombre + " " + unit + " " + total + " " + iva+"\n");				
		}
		bw.write(LINEA+"\n");	
		bw.write("Valor Exento:          "+Calculos.cortarCantidades(documentoImp.getExcento()==null?0.0:documentoImp.getExcento(), 13)+"\n");	
		bw.write("Valor Gravado:         "+Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13)+"\n");	
		bw.write("Retefuente:            "+Calculos.cortarCantidades(formatea.format(documentoImp.getRetefuente()==null?0.0:documentoImp.getRetefuente()), 13)+"\n");	
		bw.write((e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
				+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13)+"\n");	
		bw.write(LINEA+"\n");	
		bw.write("TOTAL A PAGAR: " + formatea.format(documentoImp.getTotal())+"\n");
		bw.write(LINEA+"\n");
		bw.write(Calculos.centrarDescripcion("*****GRACIAS POR SU COMPRA*****", tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion("Software  NICESOTF", tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion("LUIS MIGUEL GONZALEZ  Cel 3185222474", tamanoTxt)+"\n");
		bw.write(Calculos.centrarDescripcion("JOHAN ANDRES ORDOÑEZ  Cel 3112864974", tamanoTxt)+"\n");
		bw.write("\n");
		bw.write("\n");
		bw.write("\n");
		bw.write("\n");
		bw.write("\n");
		bw.close();
		//if (enPantalla.equals("false")) {
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(carpeta+pdf);
				log.info(carpeta+pdf);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}		
			DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			Doc document = new SimpleDoc(inputStream, docFormat, null);
			PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

			PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			log.info("Number of printers configured1: " + printServices.length);
			for (PrintService printer : printServices) {
				log.info("Printer: " + printer.getName());
				log.info("comparacion:" + impresora + ":" + printer.getName());
				if (printer.getName().equals(impresora)) {
					defaultPrintService = printer;
					log.info(impresora + " : " + printer.getName());
					break;
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
				log.info("No existen impresoras instaladas");
			}
		//}
			
	}

}
