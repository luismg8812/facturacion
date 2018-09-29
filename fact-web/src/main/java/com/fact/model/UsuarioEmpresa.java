package com.fact.model;



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
@Table(name="USUARIO_EMPRESA")
public class UsuarioEmpresa {

	@Id
	@SequenceGenerator(name="S_USUARIO_EMPRESA", sequenceName="S_USUARIO_EMPRESA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_USUARIO_EMPRESA")
	@NotNull
	@Column(name="USUARIO_EMPRESA_ID")
	private Long usuarioEmpresaId;
	
	@ManyToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresaId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;

	public Long getUsuarioEmpresaId() {
		return usuarioEmpresaId;
	}

	public void setUsuarioEmpresaId(Long usuarioEmpresaId) {
		this.usuarioEmpresaId = usuarioEmpresaId;
	}

	public Empresa getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Empresa empresaId) {
		this.empresaId = empresaId;
	}

	public Usuario getUsusarioId() {
		return usuarioId;
	}

	public void setUsusarioId(Usuario ususarioId) {
		this.usuarioId = ususarioId;
	}
	
	
}
