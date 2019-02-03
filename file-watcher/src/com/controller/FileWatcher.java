package com.controller;

import org.jboss.logging.Logger;

public class FileWatcher {

	private static Logger log = Logger.getLogger(Scaner.class);
	
	public static void main(String[] args) {
		log.info("inicio escaneo de archivos para imprimir");
		while (1==1) {
			Scaner.archivosImprimir();
		}
		
	}
	

	
	
}
