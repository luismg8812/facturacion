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
@Table(name="DEPARTAMENTO")
public class Departamento {

	@Id
	@SequenceGenerator(name="S_DEPARTAMENTO", sequenceName="S_DEPARTAMENTO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_DEPARTAMENTO")
	@NotNull
	@Column(name="DEPARTAMENTO_ID")
	private Long departamentoId;
	
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="ESTADO")
	private Long estado;

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getEstado() {
		return estado;
	}

	public void setEstado(Long estado) {
		this.estado = estado;
	}
	
	
	
}
