package com.controller;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

public class Scaner {

	private static Logger log = Logger.getLogger(Scaner.class);

	private static final String PATH_ENTRADA = "C:\\facturas\\sinImprimir";
	private static final String PATH_SALIDA = "C:\\facturas\\impresos";

	public static void archivosImprimir() {
		final File carpeta = new File(PATH_ENTRADA);
		carpeta.mkdirs();
		for (final File ficheroEntrada : carpeta.listFiles()) {
			try {
				if (!ficheroEntrada.getName().toLowerCase().contains("pantalla")) {
					if (ficheroEntrada.getName().toLowerCase().contains(".txt")) {
						imprimirTXT(ficheroEntrada);
					}
					if (ficheroEntrada.getName().toLowerCase().contains(".pdf")) {
						printer(ficheroEntrada);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error imprimiendoArchivo el archvo");
			} finally {
				try {
					moverArchivo(ficheroEntrada);
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Error moviendo el archvo");
				}
			}

		}
	}

	private static void moverArchivo(File ficheroEntrada) throws IOException {
		String pathDestino = PATH_SALIDA; // Define aqui tu directorio destino
		File ficheroDestino = new File(pathDestino, ficheroEntrada.getName());
		if (ficheroEntrada.exists()) {
			Files.copy(Paths.get(ficheroEntrada.getAbsolutePath()), Paths.get(ficheroDestino.getAbsolutePath()),
					StandardCopyOption.REPLACE_EXISTING);
			if (!ficheroEntrada.delete()) {
				log.error("Error borrando facturas");
			} else {
				log.info("Documento borrado");
			}
		} else {
			log.error("El fichero " + ficheroEntrada.getName() + " no existe en el directorio " + PATH_ENTRADA);
		}

	}

	private static void imprimirTXT(final File ficheroEntrada) {
		String impresora = "";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(ficheroEntrada);
			log.info(ficheroEntrada.getName());
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
	}

	public static void printer(final File ficheroEntrada) {
		String impresora = "";
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
			document = PDDocument.load(ficheroEntrada);
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
	}
}
