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
@Table(name="EVENTO")
public class Evento {

	@Id
	@SequenceGenerator(name="S_EVENTO", sequenceName="S_EVENTO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_EVENTO")
	@NotNull
	@Column(name="EVENTO_ID")
	private Long eventoId;
	
	@ManyToOne
	@JoinColumn(name="TIPO_EVENTO_ID")
	private TipoEvento tipoEventoId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;
	
	@Column(name="CAMPO")
	private String campo;
	
	@Column(name="VALOR_ACTUAL")
	private String valorActual;

	@Column(name="VALOR_ANTERIOR")
	private String valorAnterior;

	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;

	public Long getEventoId() {
		return eventoId;
	}

	public void setEventoId(Long eventoId) {
		this.eventoId = eventoId;
	}

	public TipoEvento getTipoEventoId() {
		return tipoEventoId;
	}

	public void setTipoEventoId(TipoEvento tipoEventoId) {
		this.tipoEventoId = tipoEventoId;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getValorActual() {
		return valorActual;
	}

	public void setValorActual(String valorActual) {
		this.valorActual = valorActual;
	}

	public String getValorAnterior() {
		return valorAnterior;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
}
