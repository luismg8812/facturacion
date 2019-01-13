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
	private final static String  LINEA = "-------------------------------------------------";
	private final static String  LINEA_GRANDE = "-------------------------------------------------------------------------------------------------------------------------------";

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
			TituloFactura1 ="DE VENTA";
			break;
		case "9":
			TituloFactura = "GUÍA DE ";
			TituloFactura1 ="REMISIÓN";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
			TituloFactura1="";
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
			//total = Calculos.cortarCantidades(formatea.format(documentoImp.getTotal() + descuento), 16);
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
					new Phrase(lineSpacing, TituloFactura, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)), 430f, 360, 0);// tituo
																												// factura
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, TituloFactura1, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)), 430f, 350, 0);// tituo
																												// factura1
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,e.getLetraConsecutivo()+
					documentoImp.getConsecutivoDian(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)),
					500f, 355, 0);// # DOCUMENTO
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + documentoImp.getDocumentoId(), FontFactory.getFont(fuente, fntSize)),
					50f, 334 - resta, 0);// guia
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
				    documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
					+ " " + documentoImp.getUsuarioId().getApellido(), FontFactory.getFont(fuente, fntSize)), 190f, 334 - resta,
					0);// cliente
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[0], FontFactory.getFont(fuente, fntSize)), 385f, 334 - resta,
					0);// fecha
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(lineSpacing, "" + fechaHora[1], FontFactory.getFont(fuente, fntSize)), 500f, 330 - resta,
					0);// hora
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
					"" +documentoImp.getClienteId().getNombre()+" "+documentoImp.getClienteId().getDireccion(),
					FontFactory.getFont(fuente, fntSize)), 60f, 318 - resta, 0);// cajero
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
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(lineSpacing,
					"" + documentoImp.getUsuarioId().getUsuarioId() + " " + documentoImp.getUsuarioId().getNombre()
							+ " " + documentoImp.getUsuarioId().getApellido(),
					FontFactory.getFont(fuente, fntSize)), 60f, 346, 0);
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
				if(productos.get(j).getProductoId().getVarios()!=null){
					varios=	productos.get(j).getProductoId().getVarios() == 1l ? "V" : "   ";
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
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, unidadProducto, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 375f,
						yDetalle, 0);// precio unitario
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
						new Phrase(lineSpacing, totalProducto, FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize + 2)), 456f,
						yDetalle, 0);// parcial
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
	
	public static String imprimirEntadaAlmacenPDF(Documento documentoImp, List<DocumentoDetalleVo> productos, Usuario usuario,
			Configuracion config,String impresora, Empresa e) throws MalformedURLException, IOException, DocumentException{
		log.info("imprimir entrada almacen");
		String pdf = "C:\\facturas\\entradas\\entrada_" + documentoImp.getDocumentoId() + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen=null;
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
		if(imagen!=null){
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"PROVEEDOR: " + (documentoImp.getProveedorId() == null ? "VARIOS"
								: documentoImp.getProveedorId().getNombre()),
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Exento:          "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Gravado:         "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento
		.add(new Paragraph(new Phrase(lineSpacing,
				"Retefuente:         "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getRetefuente()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); //retefuente
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
			Configuracion config,String impresora, String enPantalla ,Empresa empresa) throws DocumentException, IOException, PrinterException, PrintException {
		log.info("todo el codigo de imprimir");
		Empresa e = empresa;
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen=null;
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
			TituloFactura = "GUÍA DE REMISIÓN";
			break;
		case "4":
			TituloFactura = "COTIZACIÓN";
		
			break;
		default:
			break;
		}
		documento.open();
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA))); // REPRESENTANTE
		if(imagen!=null){
			documento.add(imagen); // LEGAL
		}
		documento.add(new Paragraph(
				new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE
																													// EMPRESA
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getSlogan().toUpperCase() == null ? "" : e.getSlogan(),
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
		documento.add(new Paragraph(new Phrase(lineSpacing, TituloFactura+": " + documentoImp.getConsecutivoDian(),
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"CLIENTE: " + (documentoImp.getClienteId() == null ? "VARIOS"
								: documentoImp.getClienteId().getNombre()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getClienteId().getDocumento() == null ? ""
						: documentoImp.getClienteId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		if(documentoImp.getEmpleadoId()!=null){
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "MESERO: " + documentoImp.getEmpleadoId().getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Exento:          "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Gravado:         "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
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
		if(enPantalla.equals("false")){
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
			Configuracion config, String impresora,Empresa empresa) throws DocumentException, IOException, PrinterException, PrintException {
		log.info("todo el codigo de imprimir");
		Empresa e = empresa;
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".pdf";
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"CLIENTE: " + (documentoImp.getClienteId() == null ? "VARIOS"
								: documentoImp.getClienteId().getNombre()),
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Exento:          "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Gravado:         "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
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
		printer(impresora, pdf, config);
		return pdf;
	}

	public static void printer(String impresora, String rutaArchivo, Configuracion configuracion) {
		PrinterJob job = PrinterJob.getPrinterJob();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		log.info("Number of printers configured1: " + printServices.length);
		for (PrintService printer : printServices) {
			log.info("Printer: " + printer.getName());
			log.info("comparacion:"+impresora+":"+printer.getName());
			if (printer.getName().equals(impresora)) {
				try {
					job.setPrintService(printer);
					log.info("coincide:"+ impresora+" : " + printer.getName());
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
				log.info("imprime doc..." );
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
	public static String imprimirInventarioPDF(List<Producto> productos, Usuario usuario, Configuracion con, String  impresora) {
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
			documento.add(new Paragraph(new Phrase(lineSpacing,  e.getSlogan() == null ? "" : e.getSlogan(),
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
			Configuracion config,String impresora,String enPantalla) throws IOException {
		log.info("entra a imprimir");
		Empresa e = Login.getEmpresaLogin();
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".txt";
		File archivo = new File(pdf);
		BufferedWriter bw;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("----------------------------------------\n");
		bw.write(">>" + e.getNombre() + "<<\n");
		bw.write("" + e.getRepresentante() + "\n");
		bw.write("NIT. " + e.getNit() + "   " + e.getRegimen() + "\n");
		bw.write("" + e.getDireccion() + "\n");
		bw.write("" + e.getBarrio() + "            \n");
		bw.write("" + e.getCiudad() + "-" + e.getDepartamento() + "\n");
		bw.write("TEL: " + e.getTelefonoFijo() + "\n");
		bw.write("FACTURA DE VENTA: " + documentoImp.getConsecutivoDian());
		bw.write("\n" +df.format(documentoImp.getFechaRegistro())) ;
		bw.write("\nCAJERO: " + documentoImp.getUsuarioId().getUsuarioId() + " "
				+ documentoImp.getUsuarioId().getNombre() + " " + documentoImp.getUsuarioId().getApellido()
				+ "\nCLIENTE: ");
		bw.write("\nCLIENTE: " + (documentoImp.getClienteId() == null ? "VARIOS": documentoImp.getClienteId().getNombre()));
		bw.write("\nNIT/CC:" + documentoImp.getClienteId().getDocumento());
		if(documentoImp.getEmpleadoId()!=null){
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
		bw.write("\nValor Exento:                " + Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 11));
		bw.write("\nValor Gravado:               " + Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 11));
		bw.write("\nIva:                         " + Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 11));
		bw.write("\n----------------------------------------\n");
		bw.write("\nTOTAL A PAGAR:               " +  Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 11));
		bw.write("\n----------------------------------------\n");
		bw.write("         **** FORMA DE PAGO****        ");
		Double pago = documentoImp.getValorTarjeta() == null ? 0l : documentoImp.getValorTarjeta();
		bw.write("\nVr. Pago con Tarjeta:        " + Calculos.cortarCantidades(formatea.format(pago), 11));
		bw.write("\nVr. Comisión Tarjeta:        " + Calculos.cortarCantidades(formatea.format(0l), 11));
		bw.write("\nVr. Total Factura:           " + Calculos.cortarCantidades(formatea.format(documentoImp.getTotal()), 11));
		bw.write("\nEfectivo:		 	  " + Calculos.cortarCantidades(formatea.format(documentoImp.getEfectivo()==null?0.0:documentoImp.getEfectivo()), 11));
		bw.write("\nCambio:			      " + Calculos.cortarCantidades(formatea.format(documentoImp.getCambio()==null?0.0:documentoImp.getCambio()), 11));
		bw.write("\n");
		bw.write("\nEl servicio voluntario no es obligatorio");
		bw.write("\ny puede ser modificado por el cliente.");
		
		bw.write("\n  *****GRACIAS POR SU COMPRA*****   \n");
		bw.write("          Software NICESOFT      \n");
		bw.write(" LUIS MIGUEL GONZALEZ CEL: 3184222474  \n");
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
		if(enPantalla.equals("false")){
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
		
		PrintService defaultPrintService=PrintServiceLookup.lookupDefaultPrintService();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		log.info("Number of printers configured1: " + printServices.length);
		for (PrintService printer : printServices) {
			log.info("Printer: " + printer.getName());
			log.info("comparacion:"+impresora+":"+printer.getName());
			if (printer.getName().equals(impresora)) {
				defaultPrintService=printer;
				log.info( impresora+" : " + printer.getName());
				break;
			}
		}
		
		
		
		
		//defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
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
			Configuracion configuracion, String impresora, String enPantalla, Empresa e) throws DocumentException, FileNotFoundException {
		log.info("todo el codigo de imprimir");
		String pdf = "C:\\facturas\\factura_" + documentoImp.getDocumentoId() + ".pdf";
		FileOutputStream archivo = new FileOutputStream(pdf);
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		Document documento = new Document();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Image imagen=null;
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
		default:
			break;
		}
		documento.open();
		
		if(imagen!=null){
			documento.add(imagen); // LEGAL
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, 13f)))); // NOMBRE EMPRESA																	// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, "NIT. " + e.getNit() + " " + e.getRegimen(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // NIT
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getDireccion() + " - " + e.getBarrio(),FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // DIRECCION  Y BARRIO
		documento.add(new Paragraph(new Phrase(lineSpacing, "" + e.getCiudad() + "- " + e.getDepartamento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // ciudad
		documento.add(new Paragraph(new Phrase(lineSpacing, "TEL: " + e.getTelefonoFijo() + " - " + e.getCel(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // tel
		documento.add(new Paragraph(new Phrase(lineSpacing, "Resolución. " + e.getResolucionDian() + " Fecha: " + e.getFechaResolucion()+ " Rango autorizado desde: " + e.getAutorizacionDesde() + " a " + e.getAutorizacionHasta() ,
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE LEGAL	
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing, TituloFactura+": " + documentoImp.getConsecutivoDian(),
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
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"CLIENTE: " + (documentoImp.getClienteId() == null ? "VARIOS"
								: documentoImp.getClienteId().getNombre()),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		documento.add(new Paragraph(new Phrase(lineSpacing,
				"CC/NIT: " + documentoImp.getClienteId().getDocumento() == null ? ""
						: documentoImp.getClienteId().getDocumento(),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		if(documentoImp.getEmpleadoId()!=null){
			documento.add(new Paragraph(
					new Phrase(lineSpacing, "MESERO: " + documentoImp.getEmpleadoId().getNombre(), FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // fecha
		}
		// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// lineSpacing, "Descripción CANT UNIDAD TOTAL" LEGAL
		String impuesto = e.getImpuesto().equals("IVA") ? "IVA" : "IPO";
		documento.add(new Paragraph(new Phrase(lineSpacing, "CANTIDAD     DESCRIPCION                 UNIDAD       TOTAL     " + impuesto,
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
				documento.add(new Paragraph(
						new Phrase(lineSpacing, cant + "     " + nombre + "   " + unit + "        " + total + "    " + iva,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // CANTIDAD
																							// NOMBRE
			}

			// LEGAL
		}
		documento.add(new Paragraph(new Phrase(lineSpacing, LINEA_GRANDE))); // REPRESENTANTE
		// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Exento:          "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getExcento()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento
				.add(new Paragraph(new Phrase(lineSpacing,
						"Valor Gravado:         "
								+ Calculos.cortarCantidades(formatea.format(documentoImp.getGravado()), 13),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
																					// LEGAL
		documento.add(new Paragraph(new Phrase(lineSpacing,
				(e.getImpuesto().equals("IVA") ? "IVA" : "IPO") + ":                   "
						+ Calculos.cortarCantidades(formatea.format(documentoImp.getIva()), 13),
				FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); // REPRESENTANTE
		double porcRetencion = documentoImp.getClienteId().getRetencion()==null?0.0:documentoImp.getClienteId().getRetencion();
		porcRetencion=porcRetencion/100;
		Double rete =  ((documentoImp.getTotal() / (1 +porcRetencion )) * porcRetencion);
		documento
		.add(new Paragraph(new Phrase(lineSpacing,
				"Retención:             "
						+ Calculos.cortarCantidades(formatea.format(rete), 13),
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
		if(enPantalla.equals("false")){
			System.out.println("imprime en papel");
			printer(impresora, pdf, configuracion);
		}
		

		return pdf;
		
	}

}
