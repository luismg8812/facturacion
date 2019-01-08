package com.fact.model.invoice;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


public class Receptor {

	private Long receptorId;
	private String nombre;
	private String identificacion;
	private Long tipoOrganizacionJuridicaId ;
	private Long tipoIdenificacionId ;
	private Long regimen;
	private String razonSocial;
	private String apellidos;
	private String direccion;
	private String email;

	public Long getReceptorId() {
		return receptorId;
	}

	public void setReceptorId(Long receptorId) {
		this.receptorId = receptorId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public Long getTipoOrganizacionJuridicaId() {
		return tipoOrganizacionJuridicaId;
	}

	public void setTipoOrganizacionJuridicaId(Long tipoOrganizacionJuridicaId) {
		this.tipoOrganizacionJuridicaId = tipoOrganizacionJuridicaId;
	}

	public Long getTipoIdenificacionId() {
		return tipoIdenificacionId;
	}

	public void setTipoIdenificacionId(Long tipoIdenificacionId) {
		this.tipoIdenificacionId = tipoIdenificacionId;
	}

	public Long getRegimen() {
		return regimen;
	}

	public void setRegimen(Long regimen) {
		this.regimen = regimen;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
