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
@Table(name="INVOICE")
public class Invoice {

	@Id
	@SequenceGenerator(name="S_INVOICE", sequenceName="S_INVOICE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_INVOICE")
	@NotNull
	@Column(name="INVOICE_ID")
	private Long invoiceId;
	
	@Column(name="NOMBRE")
	private String nombre;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
}
