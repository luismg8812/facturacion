package com.fact.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Conector {
	static Socket cliente;
	static int puerto = 7010;
	
	static BufferedReader entrada;
	static PrintStream salida;
	
	public  String inicio(String ip,String gramera){
		String dato="";
		try {
			cliente =  new Socket(ip, puerto);
			entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			String tec = "inicio";
			salida = new PrintStream(cliente.getOutputStream());
			salida.println(tec+":"+gramera);
			String msg=entrada.readLine();
			dato=msg;
			System.out.println(msg);
			entrada.close();
			salida.close();
			cliente.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dato;
	}

}
