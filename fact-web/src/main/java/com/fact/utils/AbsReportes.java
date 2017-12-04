//package com.fact.utils;
//
//import java.util.Map;
//
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.util.JRLoader;
//import net.sf.jasperreports.view.JasperViewer;
//
//public abstract class AbsReportes implements JRDataSource{
//
//	private static JasperReport report;
//	private static JasperPrint reportFilled;
//	private static JasperViewer viewer;
//	
//	public static void createReport(ProductosDataSource conn,Map<String,Object> parametros,String path){
//		try {
//			//report = JasperCompileManager.compileReport(path);	
//			//reportFilled = JasperFillManager.fillReport(report, parametros, conn);		
//			report =(JasperReport) JRLoader.loadObjectFromFile(path);
//			reportFilled = JasperFillManager.fillReport(report, parametros, conn);
//			
//		} catch (JRException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void showViewer(){
//		viewer = new JasperViewer(reportFilled);
//		viewer.setVisible(true);
//	}
//	
//	public static void exportToPdf(String destination){
//		try {
//			JasperExportManager.exportReportToPdfFile(reportFilled,destination);
//		} catch (JRException e) {
//			e.printStackTrace();
//		}
//	}
//
//}
