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
@Table(name="TIPO_PAGO")
public class TipoPago {

	@Id
	@SequenceGenerator(name="S_TIPO_PAGO", sequenceName="S_TIPO_PAGO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_TIPO_PAGO")
	@NotNull
	@Column(name="TIPO_PAGO_ID")
	private Long tipoPagoId;
	
	@Column(name="NOMBRE")
	private String nombre;

	public Long getTipoPagoId() {
		return tipoPagoId;
	}

	public void setTipoPagoId(Long tipoPagoId) {
		this.tipoPagoId = tipoPagoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
		
	
	
}
