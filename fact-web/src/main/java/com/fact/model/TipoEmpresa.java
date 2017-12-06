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
@Table(name="TIPO_EMPRESA")
public class TipoEmpresa {

	@Id
	@SequenceGenerator(name="S_TIPO_EMRPESA", sequenceName="S_TIPO_EMRPESA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_TIPO_EMRPESA")
	@NotNull
	@Column(name="TIPO_EMPRESA_ID")
	private Long tipoEmpresaId;
	
	@Column(name="NOMBRE")
	private String nombre;

	public Long getTipoEmpresaId() {
		return tipoEmpresaId;
	}

	public void setTipoEmpresaId(Long tipoEmpresaId) {
		this.tipoEmpresaId = tipoEmpresaId;
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
}
