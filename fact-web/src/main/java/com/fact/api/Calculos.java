package com.fact.api;

import java.io.IOException;
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

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.Producto;
import com.fact.utils.Conector;
import com.fact.vo.DocumentoDetalleVo;

public class Calculos {
	
	private static Configuracion configuracion() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		return (Configuracion) sessionMap.get("configuracion");
	}

	/**
	 * metodo que optine la fecha de inicio de las consultas para los cuadres y
	 * los reportes dependiendo de del parametro fecha combinada hace la
	 * gusqueda un dia antes
	 * 
	 * @return retorna fecha de inicio de busqueda
	 * @throws ParseException 
	 */
	public static Date fechaInicial(Date hoy) {
		//
		Long fechaCombinada = configuracion().getFechaCombinada();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(hoy);
		if (fechaCombinada == 1l) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
			calendar.set(Calendar.HOUR_OF_DAY, 18);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
		}
		hoy = calendar.getTime();
		return hoy;
	}

	public static Date fechaFinal(Date fin) {
		Long fechaCombinada = configuracion().getFechaCombinada();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fin);
		// si la fecha es nula toma por defecto la fecha del sistema
		if (fechaCombinada == 1l) {
			calendar.set(Calendar.HOUR_OF_DAY, 17);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
		} else {
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
	public static String cortarDescripcion(String nombre, int maxTamanoNombre) {
		int tamanoNombre = 0;
		nombre = nombre == null ? " " : nombre;
		try {
			nombre = nombre.trim().substring(0, maxTamanoNombre);
		} catch (Exception e2) {
			nombre = nombre.trim();
			tamanoNombre = nombre.length();
		}
		if (tamanoNombre != 0) {
			for (int j = tamanoNombre; j < maxTamanoNombre; j++) {
				nombre += " ";
			}
		}
		return nombre;
	}

	public static String cortarCantidades(Double cantidad, int maxTamanoUnit) {
		String unit = "";
		int tamanoUnit = 0;
		unit = cantidad == null ? "0" : "" + cantidad;
		unit = unit.replace(".0", "");
		try {
			unit = unit.substring(0, maxTamanoUnit);
		} catch (Exception e2) {
			tamanoUnit = unit.length();
		}
		if (tamanoUnit != 0) {
			for (int j = tamanoUnit; j < maxTamanoUnit; j++) {
				unit = " " + unit;
			}
		}
		return unit;
	}

	public static String cortarCantidades(String cantidad, int maxTamanoUnit) {
		String unit = "";
		int tamanoUnit = 0;
		unit = cantidad == null ? "0" : "" + cantidad;
		if (unit.endsWith(".0")) {
			unit = unit.substring(0, unit.length() - 2);
		}
		try {
			unit = unit.substring(0, maxTamanoUnit);
		} catch (Exception e2) {
			tamanoUnit = unit.length();
		}
		if (tamanoUnit != 0) {
			for (int j = tamanoUnit; j < maxTamanoUnit; j++) {
				unit += " ";
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
		Double iva5 = 0.0;
		Double iva19 = 0.0;
		Double base5 = 0.0;
		Double base19 = 0.0;
		// aqui voy toca poner a sumar las variables nuebas para que se reflejen
		// en el info diario
		for (DocumentoDetalleVo dDV : productos) {
			Double costoPublico = dDV.getParcial();
			Double iva1 = dDV.getProductoId().getIva().doubleValue() / 100;
			Double peso1 = dDV.getProductoId().getPeso() == null ? 0.0 : dDV.getProductoId().getPeso();//
			peso1 = peso1 * dDV.getCantidad();
			totalReal += costoPublico;
			double temp;
			ivatotal = ivatotal + ((costoPublico / (1 + iva1)) * iva1);
			peso = peso + peso1;
			// si es iva del 19 se agrega al documento junto con la base
			if (iva1 == 0.19) {
				iva19 = iva19 + ((costoPublico / (1 + iva1)) * iva1);
				base19 = base19 + (costoPublico / (1 + iva1));
			}
			// si es iva del 5 se agrega al documento junto con la base
			if (iva1 == 0.05) {
				iva5 = iva5 + ((costoPublico / (1 + iva1)) * iva1);
				base5 = base5 + (costoPublico / (1 + iva1));
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
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		InetAddress address = InetAddress.getByName(request.getRemoteAddr());
		StringBuilder sb = new StringBuilder();
		/*
		 * Get NetworkInterface for the current host and then read the hardware
		 * address.
		 */
		NetworkInterface ni = NetworkInterface.getByInetAddress(address);
		if (ni != null) {
			byte[] mac = ni.getHardwareAddress();
			if (mac != null) {
				/*
				 * Extract each array of mac address and convert it to hexa with
				 * the following format 08-00-27-DC-4A-9E.
				 */
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
				}
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
			if (ddV.getProductoId().getVarios() != null && ddV.getProductoId().getVarios() != 1) {
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

	/**
	 * Metodo que determina el tipo de gramera configurada y trae el valor de la
	 * pesa
	 * 
	 * @param event
	 * @return
	 * @throws IOException
	 */
	public static Double determinarBalanza(Conector conector, String gramera) {

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();

		String[] numerosComoArray;
		String invertido = "";
		String respuesta = "";
		String cant = "";
		Double canti = 0.0;
		respuesta = conector.inicio(request.getRemoteAddr(), gramera);
		switch (gramera) {
		case "1":
			numerosComoArray = respuesta.split("=");
			cant = numerosComoArray[1];
			cant = cant.replace("=", "");
			cant = cant.replace(" ", "");
			for (int x = cant.length() - 1; x >= 0; x--)
				invertido += cant.charAt(x);
			canti = Double.parseDouble(invertido);
			break;
		case "2":
			do {
				respuesta = respuesta == null ? "" : respuesta;
				respuesta = respuesta.trim();
				respuesta = respuesta.replace(".", "");
				numerosComoArray = respuesta.split(",");
				cant = "";
				for (int i = 0; i < numerosComoArray.length; i++) {
					if (numerosComoArray[i].contains("+")) {
						cant = numerosComoArray[i];
						break;
					}
				}
				cant = cant.replace("=", "");
				cant = cant.replaceAll(" ", "");
				cant = cant.replace("+", "");
				String parte1 = "000";
				String parte2 = "0000";
				try {
					parte1 = cant.substring(cant.length() - 3, cant.length());
					parte2 = cant.substring(0, cant.length() - 3);
				} catch (Exception e) {

				}
				cant = parte2 + "." + parte1;
			} while (numerosComoArray.length < 2);
			canti = Double.parseDouble(cant);
			break;
		case "3":
			do {
				if (respuesta == null || respuesta.equals("")) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("por vafor retire el producto de la gramera y vuelva a ponerlo"));
					RequestContext.getCurrentInstance().update("growl1");
				}
				respuesta = (respuesta == null ? "" : respuesta);

				try {
					canti = Double.parseDouble(respuesta);
				} catch (Exception e) {

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, por favor vuelva a pesar", ""));
					RequestContext.getCurrentInstance().update("growl1");
					break;
				}
			} while (respuesta.indexOf(".") == -1);
			break;
		case "4":
			respuesta = (respuesta == null ? "" : respuesta);
			respuesta = respuesta.replaceAll(",", "");
			canti = 0.0;
			try {
				canti = Double.parseDouble(respuesta);
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, por favor vuelva a pesar", ""));
				RequestContext.getCurrentInstance().update("growl1");
				break;
			}
			break;
		case "5":
			respuesta = (respuesta == null ? "" : respuesta);
			respuesta = respuesta.replaceAll(",", "");
			canti = Double.parseDouble(respuesta);
			break;
		default:
			break;
		}
		// este bloq de codigo hay que pasarlo a punto de venta dia.. por logica
		// propia de la venta
		// RequestContext.getCurrentInstance()
		// .execute("document.getElementById('cantidad_in1').value='" +
		// invertido + "';");
		return canti;
	}
}
