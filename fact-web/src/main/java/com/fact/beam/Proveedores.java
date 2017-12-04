package com.fact.beam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import com.fact.model.Ciudad;
import com.fact.model.Departamento;
import com.fact.model.Proveedor;
import com.fact.service.CiudadService;
import com.fact.service.DepartamentoService;
import com.fact.service.ProveedorService;

@ManagedBean
@SessionScoped
public class Proveedores implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private DepartamentoService departamentoService;

	@EJB
	private CiudadService ciudadService;
	
	@EJB
	private ProveedorService proveedorService;
	
	private String nombre;
	private Long ciudad;
	private Long departamento;
	private String documento;
	private String Barrio;
	private String direccion;
	private Long celular;
	private Long fijo;
	private Date Cumpleaños;
	private Boolean creditoActivo=Boolean.FALSE;
	private Long cupoCredito;
	private double retencion;
	private Long proveedorId;
	
	private List<Departamento> departamentos;
	private List<Ciudad> ciudades;
	private List<Proveedor> proveedores;
	
	public String llenarCampos(){
		System.out.println(getProveedorId());
		Proveedor p =new Proveedor();
		for(Proveedor pSelect:getProveedores()){
			if(pSelect.getProveedorId().toString().equals(getProveedorId().toString())){
				p=pSelect;
				break;
			}
		}
		setBarrio(p.getBarrio());
		setCelular(p.getCelular());
		setCiudad(p.getCiudadId()!=null?p.getCiudadId().getCiudadId():0l);
		setCreditoActivo(p.getCreditoActivo()==0?Boolean.FALSE:Boolean.TRUE);
		setCumpleaños(p.getCumpleaños());
		setCupoCredito(p.getCupoCredito());
		setDireccion(p.getDireccion());
		setDocumento(p.getDocumento());
		setFijo(p.getFijo());
		setNombre(p.getNombre());
		setRetencion(p.getRetencion());
		//setDepartamento(p.getCiudadId()!=null?p.getCiudadId().getDepartamentoId().getDepartamentoId():0l);
		return "";
	}
	
	public boolean validarEdicion(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getNombre() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Nombre es obligatorio",""));
            valido = false;         
        }
        	//hacer la parte de que busca usaurio por login
    	return valido;
	}
	
	public String editarProveedor(){
		System.out.println("entra a guardar");
		if(validarEdicion()){
			Proveedor proveedor = new Proveedor();
			proveedor.setBarrio(getBarrio());
			proveedor.setCelular(getCelular());
			Ciudad ciu = new Ciudad();
			ciu.setCiudadId(getCiudad());
			proveedor.setCiudadId(ciu);
			proveedor.setCreditoActivo(getCreditoActivo()==Boolean.TRUE?1l:0l);
			proveedor.setCumpleaños(getCumpleaños());
			proveedor.setCupoCredito(getCupoCredito());
			proveedor.setDocumento(getDocumento());
			proveedor.setFechaRegistro(new Date());
			proveedor.setFijo(getFijo());
			proveedor.setNombre(getNombre().toUpperCase());
			proveedor.setRetencion(getRetencion());
			proveedor.setDireccion(getDireccion());
			proveedor.setProveedorId(getProveedorId());
			proveedorService.update(proveedor);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Proveedor Editado exitosamente"));
		}
		return "";		
	}
	
	public boolean validar(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getNombre() == null || getNombre().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,El Nombre es obligatorio",""));
            valido = false;
        }else {      	
        	Proveedor pro = new Proveedor();
        	pro=proveedorService.getByName(getNombre().toUpperCase()); 
        	if(pro!=null){
        		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Proveedor ya existe",""));
                valido = false;         
        	}
        }
    	return valido;
	}
	
	public void guardarProveedor(){
		if(validar()){
			Proveedor proveedor = new Proveedor();
			proveedor.setBarrio(getBarrio());
			proveedor.setCelular(getCelular());
			if(getCiudad()!=null && getCiudad()!=0l){
				Ciudad ciu = new Ciudad();
				ciu.setCiudadId(getCiudad());
				proveedor.setCiudadId(ciu);
			}
			proveedor.setCreditoActivo(getCreditoActivo()==Boolean.TRUE?1l:0l);
			proveedor.setCumpleaños(getCumpleaños());
			proveedor.setCupoCredito(getCupoCredito());
			proveedor.setDocumento(getDocumento());
			proveedor.setDireccion(getDireccion());
			proveedor.setFechaRegistro(new Date());
			proveedor.setFijo(getFijo());
			proveedor.setNombre(getNombre().toUpperCase());
			proveedor.setRetencion(getRetencion());
			proveedorService.save(proveedor);
			getProveedores().add(proveedor);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Proveedor creado exitosamente"));
		}
	}
	
	
	
	public String ciudadByDepartamento(AjaxBehaviorEvent event){
		setCiudades(ciudadService.getByDepartamento(getDepartamento()));
		return "";
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getCiudad() {
		return ciudad;
	}
	public void setCiudad(Long ciudad) {
		this.ciudad = ciudad;
	}
	public Long getDepartamento() {
		return departamento;
	}
	public void setDepartamento(Long departamento) {
		this.departamento = departamento;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getBarrio() {
		return Barrio;
	}
	public void setBarrio(String barrio) {
		Barrio = barrio;
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
	public Date getCumpleaños() {
		return Cumpleaños;
	}
	public void setCumpleaños(Date cumpleaños) {
		Cumpleaños = cumpleaños;
	}
	public Boolean getCreditoActivo() {
		return creditoActivo;
	}
	public void setCreditoActivo(Boolean creditoActivo) {
		this.creditoActivo = creditoActivo;
	}
	public Long getCupoCredito() {
		return cupoCredito;
	}
	public void setCupoCredito(Long cupoCredito) {
		this.cupoCredito = cupoCredito;
	}
	public double getRetencion() {
		return retencion;
	}
	public void setRetencion(double retencion) {
		this.retencion = retencion;
	}
	public List<Departamento> getDepartamentos() {
		if(departamentos==null){
			departamentos=departamentoService.getByAll();
		}
		return departamentos;
	}
	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}
	public List<Ciudad> getCiudades() {
		return ciudades;
	}
	public void setCiudades(List<Ciudad> ciudades) {
		this.ciudades = ciudades;
	}

	public List<Proveedor> getProveedores() {
		if(proveedores==null){
			proveedores=proveedorService.getByAll();
		}
		return proveedores;
	}

	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}

	public Long getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Long proveedorId) {
		this.proveedorId = proveedorId;
	}

	
}
