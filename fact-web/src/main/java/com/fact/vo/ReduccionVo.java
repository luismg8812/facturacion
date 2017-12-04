package com.fact.vo;



import java.util.Date;

import com.fact.model.Usuario;

public class ReduccionVo {

	
	private Date fecha;
	private Double cantidadOriginal;
	private Double cantidadReducida;
	private Double ivaOriginal;
	private Double ivaRedicudo;
	private Double porReduccion;
	private Usuario usuarioId;
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Double getCantidadOriginal() {
		return cantidadOriginal;
	}
	public void setCantidadOriginal(Double cantidadOriginal) {
		this.cantidadOriginal = cantidadOriginal;
	}
	public Double getCantidadReducida() {
		return cantidadReducida;
	}
	public void setCantidadReducida(Double cantidadReducida) {
		this.cantidadReducida = cantidadReducida;
	}
	public Double getIvaOriginal() {
		return ivaOriginal;
	}
	public void setIvaOriginal(Double ivaOriginal) {
		this.ivaOriginal = ivaOriginal;
	}
	public Double getIvaRedicudo() {
		return ivaRedicudo;
	}
	public void setIvaRedicudo(Double ivaRedicudo) {
		this.ivaRedicudo = ivaRedicudo;
	}
	public Double getPorReduccion() {
		return porReduccion;
	}
	public void setPorReduccion(Double porReduccion) {
		this.porReduccion = porReduccion;
	}
	public Usuario getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}
	
	
}
