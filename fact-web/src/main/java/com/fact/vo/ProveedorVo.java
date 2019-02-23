package com.fact.vo;

import com.fact.model.Proveedor;

public class ProveedorVo {

	private Proveedor proveedorId;
	private Double totalCompras;
	private Double retefuente;
	private Double base19;
	private Double base5;
	private Double iva19;
	private Double iva5;
	private Double exento;
	
	public Proveedor getProveedorId() {
		return proveedorId;
	}
	public void setProveedorId(Proveedor proveedorId) {
		this.proveedorId = proveedorId;
	}
	public Double getTotalCompras() {
		return totalCompras;
	}
	public void setTotalCompras(Double totalCompras) {
		this.totalCompras = totalCompras;
	}
	public Double getRetefuente() {
		return retefuente;
	}
	public void setRetefuente(Double retefuente) {
		this.retefuente = retefuente;
	}
	public Double getBase19() {
		return base19;
	}
	public void setBase19(Double base19) {
		this.base19 = base19;
	}
	public Double getBase5() {
		return base5;
	}
	public void setBase5(Double base5) {
		this.base5 = base5;
	}
	public Double getIva19() {
		return iva19;
	}
	public void setIva19(Double iva19) {
		this.iva19 = iva19;
	}
	public Double getIva5() {
		return iva5;
	}
	public void setIva5(Double iva5) {
		this.iva5 = iva5;
	}
	public Double getExento() {
		return exento;
	}
	public void setExento(Double exento) {
		this.exento = exento;
	}
	
}
