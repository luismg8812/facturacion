package com.fact.vo;

import com.fact.model.Producto;

public class ProductoVo {

	Producto producto;
	String borrar;
	String nombre;
	String balanza;
	String Promo;
	String activo;
	Double publico;
	Double costo;
	Double kGPromo;
	Double pubPromo;
	Double cantidad;
	Double iva;
	Double hipoconsumo;
	Double utilidadSugerida;
	Double costoSugerida;
	Double PublicoSugerida;
	Double utilidadReal;
	Double diferencia;
	Double porMayor;
	
	
	
	public Double getPorMayor() {
		return porMayor;
	}
	public void setPorMayor(Double porMayor) {
		this.porMayor = porMayor;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
	public Producto getProducto() {
		return producto;		
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	public String getBorrar() {
		return borrar;
	}
	public void setBorrar(String borrar) {
		this.borrar = borrar;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getBalanza() {
		return balanza;
	}
	public void setBalanza(String balanza) {
		this.balanza = balanza;
	}
	public String getPromo() {
		return Promo;
	}
	public void setPromo(String promo) {
		Promo = promo;
	}
	public Double getPublico() {
		return publico;
	}
	public void setPublico(Double publico) {
		this.publico = publico;
	}
	public Double getPubPromo() {
		return pubPromo;
	}
	public void setPubPromo(Double pubPromo) {
		this.pubPromo = pubPromo;
	}
	public Double getkGPromo() {
		return kGPromo;
	}
	public void setkGPromo(Double kGPromo) {
		this.kGPromo = kGPromo;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getIva() {
		return iva;
	}
	public void setIva(Double iva) {
		this.iva = iva;
	}
	public Double getHipoconsumo() {
		return hipoconsumo;
	}
	public void setHipoconsumo(Double hipoconsumo) {
		this.hipoconsumo = hipoconsumo;
	}
	public String getActivo() {
		return activo;
	}
	public void setActivo(String activo) {
		this.activo = activo;
	}
	public Double getUtilidadSugerida() {
		return utilidadSugerida;
	}
	public void setUtilidadSugerida(Double utilidadSugerida) {
		this.utilidadSugerida = utilidadSugerida;
	}
	public Double getPublicoSugerida() {
		return PublicoSugerida;
	}
	public void setPublicoSugerida(Double publicoSugerida) {
		PublicoSugerida = publicoSugerida;
	}
	public Double getUtilidadReal() {
		return utilidadReal;
	}
	public void setUtilidadReal(Double utilidadReal) {
		this.utilidadReal = utilidadReal;
	}
	public Double getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(Double diferencia) {
		this.diferencia = diferencia;
	}
	public Double getCostoSugerida() {
		return costoSugerida;
	}
	public void setCostoSugerida(Double costoSugerida) {
		this.costoSugerida = costoSugerida;
	}
	
	
}
