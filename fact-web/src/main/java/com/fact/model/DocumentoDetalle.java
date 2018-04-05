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
@Table(name="DOCUMENTO_DETALLE")
public class DocumentoDetalle {

	@Id
	@SequenceGenerator(name="S_DOCUMENTO_DETALLE", sequenceName="S_DOCUMENTO_DETALLE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_DOCUMENTO_DETALLE")
	@NotNull
	@Column(name="DOCUMENTO_DETALLE_ID")
	private Long documentoDetalleId;
	
	@ManyToOne
	@JoinColumn(name="DOCUMENTO_ID")
	private Documento documentoId;
	
	@ManyToOne
	@JoinColumn(name="PRODUCTO_ID")
	private Producto productoId;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="FECHA_ACTUALIZA")
	private Date fechaActualiza;
	
	@Column(name="ESTADO")
	private Long estado;
	
	@Column(name="PARCIAL")
	private Double parcial;
	
	@Column(name="S_1")
	private Long s1;
	
	@Column(name="S_2")
	private Long s2;
	
	@Column(name="CANTIDAD_1")
	private Double cantidad1;
	
	@Column(name="CANTIDAD_2")
	private Double cantidad2;
	
	@Column(name="IMPRESO_COMANDA")
	private Integer impresoComanda;

	public Long getDocumentoDetalleId() {
		return documentoDetalleId;
	}
	
	@Column(name="CANTIDAD")
	private Double cantidad;

	public void setDocumentoDetalleId(Long documentoDetalleId) {
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

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Long getEstado() {
		return estado;
	}

	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public Double getParcial() {
		return parcial;
	}

	public void setParcial(Double parcial) {
		this.parcial = parcial;
	}

	public Date getFechaActualiza() {
		return fechaActualiza;
	}

	public void setFechaActualiza(Date fechaActualiza) {
		this.fechaActualiza = fechaActualiza;
	}

	public Long getS1() {
		return s1;
	}

	public void setS1(Long s1) {
		this.s1 = s1;
	}

	public Long getS2() {
		return s2;
	}

	public void setS2(Long s2) {
		this.s2 = s2;
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

	public Integer getImpresoComanda() {
		return impresoComanda;
	}

	public void setImpresoComanda(Integer impresoComanda) {
		this.impresoComanda = impresoComanda;
	}	
	
}
