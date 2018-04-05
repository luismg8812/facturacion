package com.fact.vo;



import java.util.Date;

import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Producto;

public class DocumentoDetalleVo {

	
	private DocumentoDetalle documentoDetalleId;	
	private Documento documentoId;
	private Producto productoId;
	private Date fechaRegistro;
	private Double parcial;
	private Double cantidad;
	private Double unitario;
	private Double cantidadDevolucion;
	private Double cambioPrecio;
	private String borrar;
	private Double cantidad1;
	private Double cantidad2;
	
	public DocumentoDetalle getDocumentoDetalleId() {
		return documentoDetalleId;
	}
	public void setDocumentoDetalleId(DocumentoDetalle documentoDetalleId) {
		this.documentoDetalleId = documentoDetalleId;
	}
	public Documento getDocumentoId() {
		return documentoId;
	}
	public void setDocumentoId(Documento documentoId) {
		this.documentoId = documentoId;
	}
	public Producto getProductoId() {
		return productoId;
	}
	public void setProductoId(Producto productoId) {
		this.productoId = productoId;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Double getParcial() {
		return parcial;
	}
	public void setParcial(Double parcial) {
		this.parcial = parcial;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public String getBorrar() {
		return borrar;
	}
	public void setBorrar(String borrar) {
		this.borrar = borrar;
	}
	public Double getCantidadDevolucion() {
		return cantidadDevolucion;
	}
	public void setCantidadDevolucion(Double cantidadDevolucion) {
		this.cantidadDevolucion = cantidadDevolucion;
	}
	public Double getUnitario() {
		return unitario;
	}
	public void setUnitario(Double unitario) {
		this.unitario = unitario;
	}
	public Double getCambioPrecio() {
		return cambioPrecio;
	}
	public void setCambioPrecio(Double cambioPrecio) {
		this.cambioPrecio = cambioPrecio;
	}
	public Double getCantidad1() {
		return cantidad1;
	}
	public void setCantidad1(Double cantidad1) {
		this.cantidad1 = cantidad1;
	}
	public Double getCantidad2() {
		return cantidad2;
	}
	public void setCantidad2(Double cantidad2) {
		this.cantidad2 = cantidad2;
	}
	
	
	
}

