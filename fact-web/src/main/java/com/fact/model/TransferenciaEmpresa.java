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
@Table(name="TRANSFERENCIA_EMPRESA")
public class TransferenciaEmpresa {

	@Id
	@SequenceGenerator(name="S_TRANSFERENCIA_EMPRESA", sequenceName="S_TRANSFERENCIA_EMPRESA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_TRANSFERENCIA_EMPRESA")
	@NotNull
	@Column(name="TRANSFERENCIA_EMPRESA_ID")
	private Long productoEmpresaId;
	
	@ManyToOne
	@JoinColumn(name="EMPRESA_DESDE")
	private Empresa empresaDesde;
	
	@ManyToOne
	@JoinColumn(name="EMPRESA_HASTA")
	private Empresa empresaHasta;
	
	@ManyToOne
	@JoinColumn(name="USUARIO")
	private Usuario usuarioId ;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;

	public Long getProductoEmpresaId() {
		return productoEmpresaId;
	}

	public void setProductoEmpresaId(Long productoEmpresaId) {
		this.productoEmpresaId = productoEmpresaId;
	}

	public Empresa getEmpresaDesde() {
		return empresaDesde;
	}

	public void setEmpresaDesde(Empresa empresaDesde) {
		this.empresaDesde = empresaDesde;
	}

	public Empresa getEmpresaHasta() {
		return empresaHasta;
	}

	public void setEmpresaHasta(Empresa empresaHasta) {
		this.empresaHasta = empresaHasta;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
