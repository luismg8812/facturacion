package com.fact.model.invoice;

public class Producto {

	
	private Long productoId;
	private Double costo;
	private Double costoPublico;
	private Double iva;
	private String nombre;
	private String codigoInterno;

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Double getCostoPublico() {
		return costoPublico;
	}

	public void setCostoPublico(Double costoPublico) {
		this.costoPublico = costoPublico;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}
	
	
	
}
