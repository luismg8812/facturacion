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
@Table(name="PRODUCTO_EMPRESA")
public class ProductoEmpresa {

	@Id
	@SequenceGenerator(name="S_PRODUCTO_EMPRESA", sequenceName="S_PRODUCTO_EMPRESA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_PRODUCTO_EMPRESA")
	@NotNull
	@Column(name="PRODUCTO_EMPRESA_ID")
	private Long productoEmpresaId;
	
	@ManyToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresaId;
	
	@ManyToOne
	@JoinColumn(name="PRODUCTO_ID")
	private Producto productoId;
	
	@Column(name="CANTIDAD")
	private Double cantidad;
	
	@Column(name="PRECIO")
	private Double precio;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;

	public Long getProductoEmpresaId() {
		return productoEmpresaId;
	}

	public void setProductoEmpresaId(Long productoEmpresaId) {
		this.productoEmpresaId = productoEmpresaId;
	}

	public Empresa getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Empresa empresaId) {
		this.empresaId = empresaId;
	}

	public Producto getProductoId() {
		return productoId;
	}

	public void setProductoId(Producto productoId) {
		this.productoId = productoId;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
	
}
