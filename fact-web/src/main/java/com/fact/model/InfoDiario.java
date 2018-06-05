package com.fact.model;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="INFO_DIARIO")
public class InfoDiario {

	@Id
	@SequenceGenerator(name="S_INFO_DIARIO", sequenceName="S_INFO_DIARIO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_INFO_DIARIO")
	@NotNull
	@Column(name="INFO_DIARIO_ID")
	private Long infoDiarioId;
	
	
	@Column(name="TOTAL_ORIGINAL")
	private Double totalOriginal;
	
	@Column(name="TOTAL_REDUCIDO")
	private Double totalReducido;
	
	@Column(name="IVA_ORIGINAL")
	private Double ivaOriginal;
	
	@Column(name="IVA_REDUCIDO")
	private Double ivaReducido;
	
	@Column(name="FECHA_INGRESO")
	private Date fechaIngreso;
	
	@Column(name="FECHA_INFORME")
	private Date fechaInforme;
	
	@Column(name="COSTO_ORIGINAL")
	private Double costoOriginal;
	
	@Column(name="COSTO_REDUCIDO")
	private Double costoReducido;
	
	@Column(name="TOTAL_REMISIONES")
	private Double totalRemisiones;
	
	@Column(name="PORC_REDUCCION")
	private Long porcReduccion;
	
	@Column(name="DOCUMENTO_INICIO")
	private String documentoInicio;
	
	@Column(name="DOCUMENTO_FIN")
	private Long documentoFin;
	
	@Column(name="AVANCE_EFECTIVO")
	private Double avanceEfectivo;
	
	@Column(name="CANTIDADDOCUMENTOS")
	private Double cantidadDocumentos;
	
	@Column(name="IVA_19")
	private Double iva19;
	
	@Column(name="IVA_5")
	private Double iva5;
	
	@Column(name="BASE_5")
	private Double base5;
	
	@Column(name="BASE_19")
	private Double base19;

	@Column(name="EXCENTO")
	private Double excento;
	
	@Column(name="IVA_19_REDUCIDO")
	private Double iva19Reducido;
	
	@Column(name="IVA_5_REDUCIDO")
	private Double iva5Reducido;
	
	@Column(name="BASE_5_REDUCIDO")
	private Double base5Reducido;
	
	@Column(name="BASE_19_REDUCIDO")
	private Double base19Reducido;

	@Column(name="EXCENTO_REDUCIDO")
	private Double excentorReducido;

	public Long getInfoDiarioId() {
		return infoDiarioId;
	}

	public void setInfoDiarioId(Long infoDiarioId) {
		this.infoDiarioId = infoDiarioId;
	}

	public Double getTotalOriginal() {
		return totalOriginal;
	}

	public void setTotalOriginal(Double totalOriginal) {
		this.totalOriginal = totalOriginal;
	}

	public Double getTotalReducido() {
		return totalReducido;
	}

	public void setTotalReducido(Double totalReducido) {
		this.totalReducido = totalReducido;
	}

	public Double getIvaOriginal() {
		return ivaOriginal;
	}

	public void setIvaOriginal(Double ivaOriginal) {
		this.ivaOriginal = ivaOriginal;
	}

	public Double getIvaReducido() {
		return ivaReducido;
	}

	public void setIvaReducido(Double ivaReducido) {
		this.ivaReducido = ivaReducido;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Date getFechaInforme() {
		return fechaInforme;
	}

	public void setFechaInforme(Date fechaInforme) {
		this.fechaInforme = fechaInforme;
	}

	public Double getCostoOriginal() {
		return costoOriginal;
	}

	public void setCostoOriginal(Double costoOriginal) {
		this.costoOriginal = costoOriginal;
	}

	public Double getCostoReducido() {
		return costoReducido;
	}

	public void setCostoReducido(Double costoReducido) {
		this.costoReducido = costoReducido;
	}

	public Double getTotalRemisiones() {
		return totalRemisiones;
	}

	public void setTotalRemisiones(Double totalRemisiones) {
		this.totalRemisiones = totalRemisiones;
	}

	public Long getPorcReduccion() {
		return porcReduccion;
	}

	public void setPorcReduccion(Long porcReduccion) {
		this.porcReduccion = porcReduccion;
	}

	public String getDocumentoInicio() {
		return documentoInicio;
	}

	public void setDocumentoInicio(String documentoInicio) {
		this.documentoInicio = documentoInicio;
	}

	public Long getDocumentoFin() {
		return documentoFin;
	}

	public void setDocumentoFin(Long documentoFin) {
		this.documentoFin = documentoFin;
	}

	public Double getAvanceEfectivo() {
		return avanceEfectivo;
	}

	public void setAvanceEfectivo(Double avanceEfectivo) {
		this.avanceEfectivo = avanceEfectivo;
	}

	public Double getCantidadDocumentos() {
		return cantidadDocumentos;
	}

	public void setCantidadDocumentos(Double cantidadDocumentos) {
		this.cantidadDocumentos = cantidadDocumentos;
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

	public Double getBase5() {
		return base5;
	}

	public void setBase5(Double base5) {
		this.base5 = base5;
	}

	public Double getBase19() {
		return base19;
	}

	public void setBase19(Double base19) {
		this.base19 = base19;
	}

	public Double getExcento() {
		return excento;
	}

	public void setExcento(Double excento) {
		this.excento = excento;
	}

	public Double getIva19Reducido() {
		return iva19Reducido;
	}

	public void setIva19Reducido(Double iva19Reducido) {
		this.iva19Reducido = iva19Reducido;
	}

	public Double getIva5Reducido() {
		return iva5Reducido;
	}

	public void setIva5Reducido(Double iva5Reducido) {
		this.iva5Reducido = iva5Reducido;
	}

	public Double getBase5Reducido() {
		return base5Reducido;
	}

	public void setBase5Reducido(Double base5Reducido) {
		this.base5Reducido = base5Reducido;
	}

	public Double getBase19Reducido() {
		return base19Reducido;
	}

	public void setBase19Reducido(Double base19Reducido) {
		this.base19Reducido = base19Reducido;
	}

	public Double getExcentorReducido() {
		return excentorReducido;
	}

	public void setExcentorReducido(Double excentorReducido) {
		this.excentorReducido = excentorReducido;
	}

	
}
