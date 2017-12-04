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
@Table(name="ROL")
public class Rol {

	@Id
	@SequenceGenerator(name="S_ROL", sequenceName="S_ROL", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_ROL")
	@NotNull
	@Column(name="ROL_ID")
	private Long rolId;
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="ESTADO")
	private Long estado;
	
	
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getRolId() {
		return rolId;
	}
	public void setRolId(Long rolId) {
		this.rolId = rolId;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	
	
}
