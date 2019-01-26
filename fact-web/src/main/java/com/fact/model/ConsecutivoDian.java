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
@Table(name="CONSECUTIVO_DIAN")
public class ConsecutivoDian {

	@Id
	@SequenceGenerator(name="S_CONSECUTIVO", sequenceName="S_CONSECUTIVO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_CONSECUTIVO")
	@NotNull
	@Column(name="CONSECUTIVO_ID")
	private Long consecutivoDianId;
		
	
	@Column(name="SECUENCIA")
	private Long secuencia;


	public Long getConsecutivoDianId() {
		return consecutivoDianId;
	}


	public void setConsecutivoDianId(Long consecutivoDianId) {
		this.consecutivoDianId = consecutivoDianId;
	}


	public Long getSecuencia() {
		return secuencia;
	}


	public void setSecuencia(Long secuencia) {
		this.secuencia = secuencia;
	}

	
	
}
