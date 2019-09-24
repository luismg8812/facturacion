package com.fact.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClienteDescuento {
	static Socket cliente;
	static int puerto = 7020;
	
	static BufferedReader entrada;
	static PrintStream salida;
	
	public  String inicio(String ip,String NombreUsuario, String nombreArticulo, Double cantidad, Double valorUnidad, Double nuevoValor){
		String dato="";
		try {
			cliente =  new Socket(ip, puerto);
			entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			String tec = NombreUsuario+":"+nombreArticulo+":"+cantidad+":"+valorUnidad+":"+nuevoValor;
			salida = new PrintStream(cliente.getOutputStream());
			salida.println(tec);
			String msg=entrada.readLine();
			dato=msg;
			System.out.println(msg);
			entrada.close();
			salida.close();
			cliente.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dato;
	}

}
