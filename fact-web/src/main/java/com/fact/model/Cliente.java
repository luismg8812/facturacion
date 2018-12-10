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
@Table(name="CLIENTE")
public class Cliente {

	@Id
	@SequenceGenerator(name="S_CLIENTE", sequenceName="S_CLIENTE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_CLIENTE")
	@NotNull
	@Column(name="CLIENTE_ID")
	private Long clienteId;
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="DOCUMENTO")
	private String documento;
	
	@ManyToOne
	@JoinColumn(name="CIUDAD_ID")
	private Ciudad ciudadId;
	
	@Column(name="BARRIO")
	private String barrio;
	
	@Column(name="DIRECCION")
	private String direccion;
	
	@Column(name="CELULAR")
	private Long celular;

	@Column(name="FIJO")
	private Long fijo;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="CUMPLEAÑOS")
	private Date cumpleaños;
	
	@Column(name="CREDITO_ACTIVO")
	private Long creditoActivo;
	
	@Column(name="CUPO_CREDITO")
	private Long cupoCredito;
	
	@Column(name="RETENCION")
	private Double retencion;
	
	@Column(name="GUIA_TRANSPORTE")
	private Long guiaTransporte;
	
	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Ciudad getCiudadId() {
		return ciudadId;
	}

	public void setCiudadId(Ciudad ciudadId) {
		this.ciudadId = ciudadId;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Long getCelular() {
		return celular;
	}

	public void setCelular(Long celular) {
		this.celular = celular;
	}

	public Long getFijo() {
		return fijo;
	}

	public void setFijo(Long fijo) {
		this.fijo = fijo;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getCumpleaños() {
		return cumpleaños;
	}

	public void setCumpleaños(Date cumpleaños) {
		this.cumpleaños = cumpleaños;
	}

	public Long getCreditoActivo() {
		return creditoActivo;
	}

	public void setCreditoActivo(Long creditoActivo) {
		this.creditoActivo = creditoActivo;
	}

	public Long getCupoCredito() {
		return cupoCredito;
	}

	public void setCupoCredito(Long cupoCredito) {
		this.cupoCredito = cupoCredito;
	}

	public Double getRetencion() {
		return retencion;
	}

	public void setRetencion(Double retencion) {
		this.retencion = retencion;
	}

	public Long getGuiaTransporte() {
		return guiaTransporte;
	}

	public void setGuiaTransporte(Long guiaTransporte) {
		this.guiaTransporte = guiaTransporte;
	}
	
	
}
