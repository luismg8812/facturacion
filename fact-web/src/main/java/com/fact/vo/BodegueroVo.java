package com.fact.vo;



import com.fact.model.EntregaMercancia;
import com.fact.model.OpcionUsuario;

public class BodegueroVo {

	
	private String nombreBodeguero;
	private String nombreCliente;
	private String tiempo;
	private String factura;
	private Long codigo;
	private Long  cantidadFacturas;
	private EntregaMercancia entregaMercancia;
	private Boolean liberarCuadre; // campo utilizado para activar la liberacion de cuadre
	private OpcionUsuario opcionCuadre;//variable utilizada para almacenar la opcion de cuadre de caja (id=15)
	public String getNombreBodeguero() {
		return nombreBodeguero;
	}
	public void setNombreBodeguero(String nombreBodeguero) {
		this.nombreBodeguero = nombreBodeguero;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public String getTiempo() {
		return tiempo;
	}
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public Long getCantidadFacturas() {
		return cantidadFacturas;
	}
	public void setCantidadFacturas(Long cantidadFacturas) {
		this.cantidadFacturas = cantidadFacturas;
	}
	public EntregaMercancia getEntregaMercancia() {
		return entregaMercancia;
	}
	public void setEntregaMercancia(EntregaMercancia entregaMercancia) {
		this.entregaMercancia = entregaMercancia;
	}
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public Boolean getLiberarCuadre() {
		return liberarCuadre;
	}
	public void setLiberarCuadre(Boolean liberarCuadre) {
		this.liberarCuadre = liberarCuadre;
	}
	public OpcionUsuario getOpcionCuadre() {
		return opcionCuadre;
	}
	public void setOpcionCuadre(OpcionUsuario opcionCuadre) {
		this.opcionCuadre = opcionCuadre;
	}
	
}
