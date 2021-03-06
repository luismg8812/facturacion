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
	
	@Column(name="EMPRESA")
	private String empresa;
	
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
	private String celular;

	@Column(name="FIJO")
	private String fijo;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="CUMPLEANOS")
	private Date cumpleanos;
	
	@Column(name="CREDITO_ACTIVO")
	private Long creditoActivo;
	
	@Column(name="CUPO_CREDITO")
	private Long cupoCredito;
	
	@Column(name="RETENCION")
	private Double retencion;
	
	@Column(name="GUIA_TRANSPORTE")
	private Long guiaTransporte;
	
	@Column(name="MAIL")
	private String mail;
	
	@Column(name="APELLIDOS")
	private String apellidos;
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public String getNombre() {
		return nombre;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
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

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getFijo() {
		return fijo;
	}

	public void setFijo(String fijo) {
		this.fijo = fijo;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getCumpleanos() {
		return cumpleanos;
	}

	public void setCumpleanos(Date cumpleanos) {
		this.cumpleanos = cumpleanos;
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

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	
}
