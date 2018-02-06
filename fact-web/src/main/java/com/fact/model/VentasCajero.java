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
@Table(name="VENTAS_CAJERO")
public class VentasCajero {

	@Id
	@SequenceGenerator(name="S_VENTAS_CAJERO", sequenceName="S_VENTAS_CAJERO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_VENTAS_CAJERO")
	@NotNull
	@Column(name="VENTAS_CAJERO_ID")
	private Long abonoId;
	@NotNull
	
	@ManyToOne
	@JoinColumn(name="INFO_DIARIO_ID")
	private Documento documentoId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;
	
	
	
	@Column(name="TOTAL")
	private Double cantidadAbono;
	
	@Column(name="TOTAL_REAL")
	private Double totalReal;
	
	@Column(name="TOTAL_REMISIONES")
	private Double totalRemisiones;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="TOTAL_ADELANTO")
	private Double totalAdelanto;

	public Long getAbonoId() {
		return abonoId;
	}

	public void setAbonoId(Long abonoId) {
		this.abonoId = abonoId;
	}

	public Documento getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(Documento documentoId) {
		this.documentoId = documentoId;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Double getCantidadAbono() {
		return cantidadAbono;
	}

	public void setCantidadAbono(Double cantidadAbono) {
		this.cantidadAbono = cantidadAbono;
	}

	public Double getTotalReal() {
		return totalReal;
	}

	public void setTotalReal(Double totalReal) {
		this.totalReal = totalReal;
	}

	public Double getTotalRemisiones() {
		return totalRemisiones;
	}

	public void setTotalRemisiones(Double totalRemisiones) {
		this.totalRemisiones = totalRemisiones;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Double getTotalAdelanto() {
		return totalAdelanto;
	}

	public void setTotalAdelanto(Double totalAdelanto) {
		this.totalAdelanto = totalAdelanto;
	}
	
	

}
