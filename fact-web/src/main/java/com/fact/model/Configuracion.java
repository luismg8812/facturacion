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
@Table(name="CONFIGURACION")
public class Configuracion {

	@Id
	@SequenceGenerator(name="S_CONFIGURACION", sequenceName="S_CONFIGURACION", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_CONFIGURACION")
	@NotNull
	@Column(name="CONFIGURACION_ID")
	private Long configuracionId;
	
	@Column(name="DEMO")
	private String demo;
	
	@Column(name="FECHA_INGRESO")
	private Date fechaIngreso;
	
	@Column(name="SERVER")
	private Long server;
	
	@Column(name="FECHA_COMBINADA")
	private Long fechaCombinada;
	
	@Column(name="IMPRESION_CONTINUA")
	private Long impresionContinua;
	
	@Column(name="NOMBRE_PRODUCTO_LARGO")
	private Long nombreProductoLargo;

	@Column(name="GUARDAR_FACTURAS")
	private Long guardarFacturas;
	
	@Column(name="GRAMERA")
	private Long gramera;
	
	@Column(name="NUM_IMPRESION")
	private Long numImpresion;
	
	public Long getNumImpresion() {
		return numImpresion;
	}

	public void setNumImpresion(Long numImpresion) {
		this.numImpresion = numImpresion;
	}

	public Long getConfiguracionId() {
		return configuracionId;
	}

	public void setConfiguracionId(Long configuracionId) {
		this.configuracionId = configuracionId;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Long getServer() {
		return server;
	}

	public void setServer(Long server) {
		this.server = server;
	}

	public Long getFechaCombinada() {
		return fechaCombinada;
	}

	public void setFechaCombinada(Long fechaCombinada) {
		this.fechaCombinada = fechaCombinada;
	}

	public Long getImpresionContinua() {
		return impresionContinua;
	}

	public void setImpresionContinua(Long impresionContinua) {
		this.impresionContinua = impresionContinua;
	}

	public Long getNombreProductoLargo() {
		return nombreProductoLargo;
	}

	public void setNombreProductoLargo(Long nombreProductoLargo) {
		this.nombreProductoLargo = nombreProductoLargo;
	}

	public Long getGuardarFacturas() {
		return guardarFacturas;
	}

	public void setGuardarFacturas(Long guardarFacturas) {
		this.guardarFacturas = guardarFacturas;
	}

	public Long getGramera() {
		return gramera;
	}

	public void setGramera(Long gramera) {
		this.gramera = gramera;
	}
	
	
}
