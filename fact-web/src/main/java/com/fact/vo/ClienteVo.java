package com.fact.vo;

import com.fact.model.Cliente;

public class ClienteVo {

	Cliente clienteId;
	Double totalCompras;
	Double excento;
	Double baseIva19;
	Double baseIva5;
	Double iva19;
	Double iva5;
	public Cliente getClienteId() {
		return clienteId;
	}
	public void setClienteId(Cliente clienteId) {
		this.clienteId = clienteId;
	}
	public Double getTotalCompras() {
		return totalCompras;
	}
	public void setTotalCompras(Double totalCompras) {
		this.totalCompras = totalCompras;
	}
	public Double getExcento() {
		return excento;
	}
	public void setExcento(Double excento) {
		this.excento = excento;
	}
	public Double getBaseIva19() {
		return baseIva19;
	}
	public void setBaseIva19(Double baseIva19) {
		this.baseIva19 = baseIva19;
	}
	public Double getBaseIva5() {
		return baseIva5;
	}
	public void setBaseIva5(Double baseIva5) {
		this.baseIva5 = baseIva5;
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
	
	
	
}
