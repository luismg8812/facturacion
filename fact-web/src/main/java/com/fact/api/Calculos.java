package com.fact.api;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List; 
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Producto;
import com.fact.vo.DocumentoDetalleVo;

public class Calculos {
	private static Configuracion configuracion() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
		return configuracion;
	}

	/**
	 * metodo que optine la fecha de inicio de las consultas para los cuadres y
	 * los reportes dependiendo de del parametro fecha combinada hace la
	 * gusqueda un dia antes
	 * 
	 * @return retorna fecha de inicio de busqueda
	 */
	public static Date fechaInicial(Date hoy) {
		//
		Long fechaCombinada = configuracion().getFechaCombinada();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(hoy);
		if (fechaCombinada == 1l) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
			calendar.set(Calendar.HOUR_OF_DAY, 18);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
		} else {
			String fhoyIni = df.format(hoy);
			try {
				hoy = df.parse(fhoyIni);
			} catch (ParseException e) {
				System.out.println("Error en fecha inicial");
				e.printStackTrace();
			}
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
		}
		hoy = calendar.getTime();
		return hoy;
	}

	public static Date fechaFinal(Date fin) {
		Long fechaCombinada = configuracion().getFechaCombinada();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyFin = df.format(fin);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fin);
		// si la fecha es nula toma por defecto la fecha del sistema
		if (fechaCombinada == 1l) {
			calendar.set(Calendar.HOUR_OF_DAY, 17);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
		} else {
			try {
				fin = df.parse(fhoyFin);
			} catch (ParseException e) {
				System.out.println("Error en fecha Final");
				e.printStackTrace();
			}
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
		}
		fin = calendar.getTime();
		return fin;
	}

	public static boolean validarPromo(Producto ps, Double cantidad) {
		boolean valido = true;
		if (ps.getPromo() == null) {
			valido = false;
			return valido;
		}
		if (ps.getPromo() != 1l) {
			valido = false;
			return valido;
		}
		if (ps.getkGPromo() == null) {
			valido = false;
			return valido;
		} else {
			double can = cantidad == null ? 0.0 : cantidad;
			if (ps.getkGPromo() > can) {
				valido = false;
				return valido;
			}
		}
		return valido;
	}

	/**
	 * metodo encargado de cortar la descripcion para las facturas e informes
	 * 
	 * @param nombre
	 * @param maxTamañoNombre
	 * @return
	 */
	public static String cortarDescripcion(String nombre, int maxTamañoNombre) {
		int tamañoNombre = 0;
		nombre = nombre == null ? " " : nombre;
		try {
			nombre = nombre.trim().substring(0, maxTamañoNombre);
		} catch (Exception e2) {
			nombre = nombre.trim();
			tamañoNombre = nombre.length();
		}
		if (tamañoNombre != 0) {
			for (int j = tamañoNombre; j < maxTamañoNombre; j++) {
				nombre += " ";
			}
		}
		return nombre;
	}

	public static String cortarCantidades(Double cantidad, int maxTamañoUnit) {
		String unit = "";
		int tamañoUnit = 0;
		unit = cantidad == null ? "0" : "" + cantidad;
		unit = unit.replace(".0", "");
		try {
			unit = unit.substring(0, maxTamañoUnit);
		} catch (Exception e2) {
			tamañoUnit = unit.length();
		}
		if (tamañoUnit != 0) {
			for (int j = tamañoUnit; j < maxTamañoUnit; j++) {
				unit = " " + unit;
			}
		}
		return unit;
	}

	public static String cortarCantidades(String cantidad, int maxTamañoUnit) {
		String unit = "";
		int tamañoUnit = 0;
		unit = cantidad == null ? "0" : "" + cantidad;
		if (unit.endsWith(".0")) {
			unit = unit.substring(0, unit.length() - 2);
		}
		try {
			unit = unit.substring(0, maxTamañoUnit);
		} catch (Exception e2) {
			tamañoUnit = unit.length();
		}
		if (tamañoUnit != 0) {
			for (int j = tamañoUnit; j < maxTamañoUnit; j++) {
				unit = " " + unit;
			}
		}
		return unit;
	}

	// en esta funcion de calcula el excento, iva, total
	public static Documento calcularExcento(Documento doc, List<DocumentoDetalleVo> productos) {
		Double totalReal = 0.0, exectoReal = 0.0;
		Double gravado = 0.0;
		Double ivatotal = 0.0;
		Double peso = 0.0;
		Double iva5=0.0;
		Double iva19=0.0;	
		Double base5=0.0;
		Double base19=0.0;
		//aqui voy toca poner a sumar las variables nuebas para que se reflejen en el info diario
		for (DocumentoDetalleVo dDV : productos) {
			Double costoPublico = dDV.getParcial();
			Double iva1 = dDV.getProductoId().getIva().doubleValue() / 100;
			Double peso1 = dDV.getProductoId().getPeso() == null ? 0.0 : dDV.getProductoId().getPeso();//
			peso1 = peso1 * dDV.getCantidad();
			totalReal += costoPublico;
			double temp;
			ivatotal = ivatotal + ((costoPublico / (1 + iva1)) * iva1);
			peso = peso + peso1;
			//si es iva del 19 se agrega al documento junto con la base
			if(iva1==0.19){
				iva19=iva19+((costoPublico / (1 + iva1)) * iva1);
				base19=base19+(costoPublico / (1 + iva1));
			}
			//si es iva del 5 se agrega al documento junto con la base
			if(iva1==0.05){
				iva5=iva5+((costoPublico / (1 + iva1)) * iva1);
				base5=base5+(costoPublico / (1 + iva1));
			}
			if (iva1 > 0.0) {
				temp = costoPublico / (1 + iva1);
				gravado += temp;
				
			} else {
				temp = costoPublico;
				exectoReal += temp;
			}
		}
		doc.setTotal(totalReal);
		doc.setSaldo(totalReal);
		doc.setExcento(exectoReal);
		doc.setGravado(gravado);
		doc.setIva(ivatotal);
		doc.setPesoTotal(peso);
		doc.setIva5(iva5);
		doc.setIva19(iva19);
		doc.setBase5(base5);
		doc.setBase19(base19);
		return doc;
	}

	public static String conseguirMAC() {
		StringBuilder sb = new StringBuilder();
		NetworkInterface a;
		try {
			a = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			byte[] mac = a.getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "" + sb.toString();
	}
	
	public static String conseguirMAC2() throws UnknownHostException, SocketException {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//request.getRemoteAddr();
		 InetAddress address = InetAddress.getByName(request.getRemoteAddr());
		 StringBuilder sb = new StringBuilder();
		 /*
          * Get NetworkInterface for the current host and then read
          * the hardware address.
          */
         NetworkInterface ni = 
                 NetworkInterface.getByInetAddress(address);
         if (ni != null) {
             byte[] mac = ni.getHardwareAddress();
             if (mac != null) {
                 /*
                  * Extract each array of mac address and convert it 
                  * to hexa with the following format 
                  * 08-00-27-DC-4A-9E.
                  */
                 for (int i = 0; i < mac.length; i++) {
                	 sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                 }
             } else {
                 System.out.println("Address doesn't exist or is not " +
                         "accessible.");
             }
         } else {
             return request.getRemoteAddr();
         }
         return "" + sb.toString();
     
	}
	
	/**
	 * ordena los productos por varios
	 */
	public static List<DocumentoDetalleVo> ordenar(List<DocumentoDetalleVo> productos) {
		List<DocumentoDetalleVo> temp = new ArrayList<>();
		for (DocumentoDetalleVo ddV : productos) {
			if (ddV.getProductoId().getVarios()!=null && ddV.getProductoId().getVarios() != 1) {
				temp.add(ddV);
			}
		}
		for (DocumentoDetalleVo ddV : productos) {
			if (!temp.contains(ddV)) {
				temp.add(ddV);
			}
		}
		return temp;
	}
	
	public static List<DocumentoDetalleVo> llenarDocumentoDetalleVoList(List<DocumentoDetalle> detalles){
		List<DocumentoDetalleVo> voList = new ArrayList<>();
		for (DocumentoDetalle d : detalles) {
			DocumentoDetalleVo vo = new DocumentoDetalleVo();
			vo.setCantidad(d.getCantidad());
			vo.setDocumentoDetalleId(d);
			vo.setDocumentoId(d.getDocumentoId());
			vo.setFechaRegistro(d.getFechaRegistro());
			vo.setParcial(d.getParcial());
			vo.setProductoId(d.getProductoId());
			vo.setUnitario(d.getParcial() / d.getCantidad());
			voList.add(vo);
		}
		return voList;
	}
	
//	public static Image generarCodBaras(Long value) throws FileNotFoundException, DocumentException{
//		Document documento = new Document();
//		PdfWriter pdfw = PdfWriter.getInstance(documento, new FileOutputStream("C:\\facturas\\codigo"+value+".pdf"));
//		Barcode128 barcode = new Barcode128();
//		barcode.setCodeType(Barcode.CODE128);
//		barcode.setCode(value.toString());
//		Image img = barcode.createImageWithBarcode( pdfw.getDirectContent(), null, null);
//		documento.add(img);
//		documento.close();
//		return img;
//	}
}
