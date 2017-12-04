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
@Table(name="DOCUMENTO_REDUCIDO")
public class DocumentoReducido {

	@Id
	@SequenceGenerator(name="S_DOCUMENTO_REDUCIDO", sequenceName="S_DOCUMENTO_REDUCIDO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_DOCUMENTO_REDUCIDO")
	@NotNull
	@Column(name="DOCUMENTO_REDUCIDO_ID")
	private Long documentoReducidoId;
	
	@ManyToOne
	@JoinColumn(name="DOCUMENTO_ID")
	private Documento documentoId;
	
	@ManyToOne
	@JoinColumn(name="INFO_DIARIO_ID")
	private InfoDiario infoDiarioId;
	
	@Column(name="TOTAL_ORIGINAL")
	private Double totalOriginal;
	
	@Column(name="TOTAL_REDUCIDO")
	private Double totalReducido;
	
	@Column(name="FECHA_INGRESO")
	private Date fechaIngreso;

	public Long getDocumentoReducidoId() {
		return documentoReducidoId;
	}

	public void setDocumentoReducidoId(Long documentoReducidoId) {
		this.documentoReducidoId = documentoReducidoId;
	}

	public Documento getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(Documento documentoId) {
		this.documentoId = documentoId;
	}

	public InfoDiario getInfoDiarioId() {
		return infoDiarioId;
	}

	public void setInfoDiarioId(InfoDiario infoDiarioId) {
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

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
}
