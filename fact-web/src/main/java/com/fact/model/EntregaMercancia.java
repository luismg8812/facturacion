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
@Table(name="ENTREGA_MERCANCIA")
public class EntregaMercancia {

	@Id
	@SequenceGenerator(name="S_ENTREGA_MERCANCIA", sequenceName="S_ENTREGA_MERCANCIA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_ENTREGA_MERCANCIA")
	@NotNull
	@Column(name="ENTREGA_MERCANCIA_ID")
	private Long entregaMercanciaId;
	
	@ManyToOne
	@JoinColumn(name="DOCUMENTO_ID")
	private Documento documentoId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;
	
	@Column(name="ENTREGADO")
	private Long entregado;
	
	@Column(name="FECHA_ENTREGA")
	private Date fechaEntrega;
	
	@Column(name="FECHA_REGISTRO")
	private  Date fechaRegistro;

	public Long getEntregaMercanciaId() {
		return entregaMercanciaId;
	}

	public void setEntregaMercanciaId(Long entregaMercanciaId) {
		this.entregaMercanciaId = entregaMercanciaId;
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

	public Long getEntregado() {
		return entregado;
	}

	public void setEntregado(Long entregado) {
		this.entregado = entregado;
	}

	public Date getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
}
