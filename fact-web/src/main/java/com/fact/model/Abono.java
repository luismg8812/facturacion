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
@Table(name="ABONO")
public class Abono {

	@Id
	@SequenceGenerator(name="S_ABONO", sequenceName="S_ABONO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_ABONO")
	@NotNull
	@Column(name="ABONO_ID")
	private Long abonoId;
	@NotNull
	
	@ManyToOne
	@JoinColumn(name="DOCUMENTO_ID")
	private Documento documentoId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;
	
	@ManyToOne
	@JoinColumn(name="TIPO_PAGO_ID")
	private TipoPago tipoPagoId;
	
	@Column(name="CANTIDAD")
	private Double cantidadAbono;
	
	@Column(name="FECHA_INGRESO")
	private Date fechaRegistro;

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

	public Double getCantidadAbono() {
		return cantidadAbono;
	}

	public void setCantidadAbono(Double cantidadAbono) {
		this.cantidadAbono = cantidadAbono;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public TipoPago getTipoPagoId() {
		return tipoPagoId;
	}

	public void setTipoPagoId(TipoPago tipoPagoId) {
		this.tipoPagoId = tipoPagoId;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}
	
}
