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
@Table(name="PRODUCTO")
public class Producto {

	@Id
	@SequenceGenerator(name="S_PRODUCTO", sequenceName="S_PRODUCTO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_PRODUCTO")
	@NotNull
	@Column(name="PRODUCTO_ID")
	private Long productoId;
	
	@ManyToOne
	@JoinColumn(name="GRUPO_ID")
	private Grupo grupoId;
	
	@ManyToOne
	@JoinColumn(name="PROVEEDOR_ID")
	private Proveedor proveedorId;
	
	@ManyToOne
	@JoinColumn(name="MARCA_ID")
	private Marca marcaId;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="FECHA_ACTUALIZA")
	private Date fechaActualiza;
	
	@Column(name="COSTO")
	private Double costo;
	
	@Column(name="COSTO_PUBLICO")
	private Double costoPublico;
	
	@Column(name="CODIGO_INTERNO")
	private String codigoInterno;
	
	@Column(name="IVA")
	private Double iva;
	
	@Column(name="HIPOCONSUMO")
	private Double hipoconsumo;
	
	@Column(name="STOCK_MIN")
	private Long stockMin;

	@Column(name="STOCK_MAX")
	private Long stockMax;
	
	@Column(name="CODIGO_BARRAS")
	private Long codigoBarras;
	
	@Column(name="PESO")
	private Double peso;
	
	@Column(name="PESO_EMPAQUE")
	private Double pesoEmpaque;
	
	@Column(name="UNIDAD")
	private String unidad;
	
	@Column(name="BALANZA")
	private Long balanza;
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="CANTIDAD")
	private Double cantidad;
	
	@Column(name="PROMO")
	private Long promo;
	
	@Column(name="PUB_PROMO")
	private Double pubPromo;
	
	@Column(name="ESTADO")
	private Long estado;
	
	@Column(name="KG_PROMO")
	private Double kGPromo;
	
	@Column(name="VARIOS")
	private Long varios;
	
	@Column(name="ACTIVO")
	private Long activo;
	
	@Column(name="UTILIDAD_SUGERIDA")
	private Double utilidadSugerida;
	
	@Column(name="COSTO_SUGERIDA")
	private Double costoSugerida;
	
	@Column(name="ES_CARNE")
	private Long esCarne;
	
	@Column(name="SUB_PRODUCTO")
	private Long subProducto;

	public Long getSubProducto() {
		return subProducto;
	}

	public void setSubProducto(Long subProducto) {
		this.subProducto = subProducto;
	}

	public Long getEsCarne() {
		return esCarne;
	}

	public void setEsCarne(Long esCarne) {
		this.esCarne = esCarne;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public Grupo getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(Grupo grupoId) {
		this.grupoId = grupoId;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getFechaActualiza() {
		return fechaActualiza;
	}

	public void setFechaActualiza(Date fechaActualiza) {
		this.fechaActualiza = fechaActualiza;
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

	public Long getStockMin() {
		return stockMin;
	}

	public void setStockMin(Long stockMin) {
		this.stockMin = stockMin;
	}

	public Long getStockMax() {
		return stockMax;
	}

	public void setStockMax(Long stockMax) {
		this.stockMax = stockMax;
	}

	public Marca getMarcaId() {
		return marcaId;
	}

	public void setMarcaId(Marca marcaId) {
		this.marcaId = marcaId;
	}

	public Long getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(Long codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public Long getBalanza() {
		return balanza;
	}

	public void setBalanza(Long balanza) {
		this.balanza = balanza;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public Long getPromo() {
		return promo;
	}

	public void setPromo(Long promo) {
		this.promo = promo;
	}

	public Double getPubPromo() {
		return pubPromo;
	}

	public void setPubPromo(Double pubPromo) {
		this.pubPromo = pubPromo;
	}

	public Long getEstado() {
		return estado;
	}

	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public Double getkGPromo() {
		return kGPromo;
	}

	public void setkGPromo(Double kGPromo) {
		this.kGPromo = kGPromo;
	}

	public Proveedor getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Proveedor proveedorId) {
		this.proveedorId = proveedorId;
	}

	public Double getHipoconsumo() {
		return hipoconsumo;
	}

	public void setHipoconsumo(Double hipoconsumo) {
		this.hipoconsumo = hipoconsumo;
	}

	public Long getVarios() {
		return varios;
	}

	public void setVarios(Long varios) {
		this.varios = varios;
	}

	public Long getActivo() {
		return activo;
	}

	public void setActivo(Long activo) {
		this.activo = activo;
	}

	public Double getUtilidadSugerida() {
		return utilidadSugerida;
	}

	public void setUtilidadSugerida(Double utilidadSugerida) {
		this.utilidadSugerida = utilidadSugerida;
	}

	public Double getCostoSugerida() {
		return costoSugerida;
	}

	public void setCostoSugerida(Double costoSugerida) {
		this.costoSugerida = costoSugerida;
	}

	public Double getPesoEmpaque() {
		return pesoEmpaque;
	}

	public void setPesoEmpaque(Double pesoEmpaque) {
		this.pesoEmpaque = pesoEmpaque;
	}
	
	
	
}
