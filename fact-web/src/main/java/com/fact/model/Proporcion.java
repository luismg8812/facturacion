package com.fact.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="PROPORCION")
public class Proporcion {

	@Id
	@SequenceGenerator(name="S_PROPORCION", sequenceName="S_PROPORCION", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_PROPORCION")
	@NotNull
	@Column(name="PROPORCION_ID")
	private Long proporcionId;
	
	@Column(name="BASE")
	private Long base;
	
	@Column(name="VARIABLE")
	private Long variable;
	
	@Column(name="CONTADOR_FACTURA")
	private Long contadorFactura;
	
	@Column(name="CONTADOR_REMISION")
	private Long contadorRemision;
	
	@Column(name="RANGO_PAR_A")
	private Double rangoParA;
	
	@Column(name="RANGO_PAR_B")
	private Double rangoParB;
	
	@Column(name="RANGO_IMPAR_A")
	private Double rangoImparA;
	
	@Column(name="RANGO_IMPAR_B")
	private Double rangoImparB;
	

	public Long getProporcionId() {
		return proporcionId;
	}

	public void setProporcionId(Long proporcionId) {
		this.proporcionId = proporcionId;
	}

	public Long getBase() {
		return base;
	}

	public void setBase(Long base) {
		this.base = base;
	}

	public Long getVariable() {
		return variable;
	}

	public void setVariable(Long variable) {
		this.variable = variable;
	}

	public Long getContadorFactura() {
		return contadorFactura;
	}

	public void setContadorFactura(Long contadorFactura) {
		this.contadorFactura = contadorFactura;
	}

	public Long getContadorRemision() {
		return contadorRemision;
	}

	public void setContadorRemision(Long contadorRemision) {
		this.contadorRemision = contadorRemision;
	}

	public Double getRangoParA() {
		return rangoParA;
	}

	public void setRangoParA(Double rangoParA) {
		this.rangoParA = rangoParA;
	}

	public Double getRangoParB() {
		return rangoParB;
	}

	public void setRangoParB(Double rangoParB) {
		this.rangoParB = rangoParB;
	}

	public Double getRangoImparA() {
		return rangoImparA;
	}

	public void setRangoImparA(Double rangoImparA) {
		this.rangoImparA = rangoImparA;
	}

	public Double getRangoImparB() {
		return rangoImparB;
	}

	public void setRangoImparB(Double rangoImparB) {
		this.rangoImparB = rangoImparB;
	}
	
}
