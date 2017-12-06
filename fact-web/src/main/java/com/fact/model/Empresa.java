package com.fact.model;



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
@Table(name="EMPRESA")
public class Empresa {

	@Id
	@SequenceGenerator(name="S_EMPRESA", sequenceName="S_EMPRESA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_EMPRESA")
	@NotNull
	@Column(name="EMPRESA_ID")
	private Long empresaId;
	
	@ManyToOne
	@JoinColumn(name="TIPO_EMPRESA_ID")
	private TipoEmpresa tipoEmpresaId;
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="REPRESENTE")
	private String representante;
	
	@Column(name="NIT")
	private String nit;

	@Column(name="REGIMEN")
	private String regimen;
	
	@Column(name="DIRECCION")
	private String direccion;
	
	@Column(name="BARRIO")
	private String barrio;
	
	@Column(name="CIUDAD")
	private String ciudad;
	
	@Column(name="DEPARTAMENTO")
	private String departamento;
	
	@Column(name="TELEFONO_FIJO")
	private String telefonoFijo;
	
	@Column(name="CEL")
	private String cel;
	
	@Column(name="IMPRESION")
	private String impresion;
	
	@Column(name="LETRA_CONSECUTIVO")
	private String letraConsecutivo;
	
	@Column(name="RESOLUCION_DIAN")
	private String resolucionDian;
	
	@Column(name="FECHA_RESOLUCION")
	private String fechaResolucion;
	
	@Column(name="T_FACTURA")  //EN NABOR AL FINAL DECIA ALGO COMO FACTURA: P.0.S.
	private String tFactura;
	
	@Column(name="AUTORIZACION_DESDE")
	private String autorizacionDesde;
	
	@Column(name="AUTORIZACION_HASTA")
	private String autorizacionHasta;
	
	@Column(name="SLOGAN")
	private String slogan;
	
	@Column(name="IMPUESTO")
	private String impuesto;

	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRepresentante() {
		return representante;
	}

	public void setRepresentante(String representante) {
		this.representante = representante;
	}

	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public String getCel() {
		return cel;
	}

	public void setCel(String cel) {
		this.cel = cel;
	}

	public String getImpresion() {
		return impresion;
	}

	public void setImpresion(String impresion) {
		this.impresion = impresion;
	}

	public String getLetraConsecutivo() {
		return letraConsecutivo;
	}

	public void setLetraConsecutivo(String letraConsecutivo) {
		this.letraConsecutivo = letraConsecutivo;
	}

	public String getResolucionDian() {
		return resolucionDian;
	}

	public void setResolucionDian(String resolucionDian) {
		this.resolucionDian = resolucionDian;
	}

	public String getFechaResolucion() {
		return fechaResolucion;
	}

	public void setFechaResolucion(String fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public String gettFactura() {
		return tFactura;
	}

	public void settFactura(String tFactura) {
		this.tFactura = tFactura;
	}

	public String getAutorizacionDesde() {
		return autorizacionDesde;
	}

	public void setAutorizacionDesde(String autorizacionDesde) {
		this.autorizacionDesde = autorizacionDesde;
	}

	public String getAutorizacionHasta() {
		return autorizacionHasta;
	}

	public void setAutorizacionHasta(String autorizacionHasta) {
		this.autorizacionHasta = autorizacionHasta;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(String impuesto) {
		this.impuesto = impuesto;
	}

	public TipoEmpresa getTipoEmpresaId() {
		return tipoEmpresaId;
	}

	public void setTipoEmpresaId(TipoEmpresa tipoEmpresaId) {
		this.tipoEmpresaId = tipoEmpresaId;
	}
	
}
