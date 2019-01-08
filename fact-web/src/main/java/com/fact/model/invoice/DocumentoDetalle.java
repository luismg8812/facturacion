package com.fact.model.invoice;



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
		
	@Column(name="PARCIAL")
	private Double parcial;
	
	@Column(name="CANTIDAD")
	private Double cantidad;
	
	@Column(name="CODIGO_IMPUESTO")
	private Double codigoImpuesto;
	

	public Long getDocumentoDetalleId() {
		return documentoDetalleId;
	}

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

	public Double getCodigoImpuesto() {
		return codigoImpuesto;
	}

	public void setCodigoImpuesto(Double codigoImpuesto) {
		this.codigoImpuesto = codigoImpuesto;
	}	
	
	
}
