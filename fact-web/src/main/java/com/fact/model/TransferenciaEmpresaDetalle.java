package com.fact.model;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="TRANSFERENCIA_EMPRESA_DETALLE")
public class TransferenciaEmpresaDetalle {

	@Id
	@SequenceGenerator(name="S_TRANSFERENCIA_EMPRESA_DETALLE", sequenceName="S_TRANSFERENCIA_EMPRESA_DETALLE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_TRANSFERENCIA_EMPRESA_DETALLE")
	@NotNull
	@Column(name="TRANSFERENCIA_EMPR_DETAL_ID")
	private Long transferenciaEmprDetalId;
	
	@ManyToOne
	@JoinColumn(name="TRANSFERENCIA_EMPRESA_ID")
	private TransferenciaEmpresa transferenciaEmpresaId;
	
	@ManyToOne
	@JoinColumn(name="PRODUCTO_ID")
	private Producto productoId;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;

	@Column(name="CANTIDAD")
	private Double cantidad;

	

	public Long getTransferenciaEmprDetalId() {
		return transferenciaEmprDetalId;
	}

	public void setTransferenciaEmprDetalId(Long transferenciaEmprDetalId) {
		this.transferenciaEmprDetalId = transferenciaEmprDetalId;
	}

	

	public TransferenciaEmpresa getTransferenciaEmpresaId() {
		return transferenciaEmpresaId;
	}

	public void setTransferenciaEmpresaId(TransferenciaEmpresa transferenciaEmpresaId) {
		this.transferenciaEmpresaId = transferenciaEmpresaId;
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

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	
}
