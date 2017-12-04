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
@Table(name="GRUPO")
public class Grupo {

	@Id
	@SequenceGenerator(name="S_GRUPO", sequenceName="S_GRUPO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_GRUPO")
	@NotNull
	@Column(name="GRUPO_ID")
	private Long grupoId;
	
	@Column(name="CODIGO")
	private Long codigo;	
	
	@Column(name="NOMBRE")
	private String nombre;

	public Long getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(Long grupoId) {
		this.grupoId = grupoId;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
	
}
