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
@Table(name="TIPO_EVENTO")
public class TipoEvento {

	@Id
	@SequenceGenerator(name="S_TIPO_EVENTO", sequenceName="S_TIPO_EVENTO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_TIPO_EVENTO")
	@NotNull
	@Column(name="TIPO_EVENTO_ID")
	private Long tipoEventoId;
	
	@Column(name="NOMBRE")
	private String nombre;

	public Long getTipoEventoId() {
		return tipoEventoId;
	}

	public void setTipoEventoId(Long tipoEventoId) {
		this.tipoEventoId = tipoEventoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
}
