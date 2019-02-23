package com.fact.beam;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.fact.model.Documento;
import com.fact.model.Proveedor;
import com.fact.service.CiudadService;
import com.fact.service.DepartamentoService;
import com.fact.service.DocumentoService;
import com.fact.service.ProveedorService;
import com.fact.vo.ProveedorVo;

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
	
	@EJB
	private DocumentoService documentoService;
	
	private String nombre;
	private Long ciudad;
	private Long departamento;
	private String documento;
	private String barrio;
	private String direccion;
	private Long celular;
	private Long fijo;
	private Date cumpleanos;
	private Boolean creditoActivo=Boolean.FALSE;
	private Long cupoCredito;
	private double retencion;
	private double baseRetencion;
	private Long proveedorId;
	
	private List<Departamento> departamentos;
	private List<Ciudad> ciudades;
	private List<Proveedor> proveedores;
	
	// retefuente
		private Date fechaInicio;
		private Date fechafin;
		private Long proveedoresId;
		private List<ProveedorVo> proveedoresVo;
	
	public String llenarCampos(){		
		Proveedor p =new Proveedor();
		for(Proveedor pSelect:getProveedores()){
			if(pSelect.getProveedorId().toString().equals(getProveedorId().toString())){
				p=pSelect;
				break;
			}
		}
		setBarrio(p.getBarrio());
		setCelular(p.getCelular());
		setDepartamento(p.getCiudadId()!=null?p.getCiudadId().getDepartamentoId().getDepartamentoId():0l);
		setCiudad(p.getCiudadId()!=null?p.getCiudadId().getCiudadId():0l);
		setCreditoActivo(p.getCreditoActivo()==0?Boolean.FALSE:Boolean.TRUE);
		setCumpleanos(p.getCumpleanos());
		setCupoCredito(p.getCupoCredito());
		setDireccion(p.getDireccion());
		setDocumento(p.getDocumento());
		setFijo(p.getFijo());
		setNombre(p.getNombre());
		setRetencion(p.getRetencion()==null?0.0:p.getRetencion());		
		setBaseRetencion(p.getBaseRetencion()==null?0.0:p.getBaseRetencion());
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
		if(validarEdicion()){
			Proveedor proveedor = new Proveedor();
			proveedor.setBarrio(getBarrio());
			proveedor.setCelular(getCelular());
			Ciudad ciu = new Ciudad();
			ciu.setCiudadId(getCiudad());
			proveedor.setCiudadId(ciu);
			proveedor.setCreditoActivo(getCreditoActivo()==Boolean.TRUE?1l:0l);
			proveedor.setCumpleanos(getCumpleanos());
			proveedor.setCupoCredito(getCupoCredito());
			proveedor.setDocumento(getDocumento());
			proveedor.setFechaRegistro(new Date());
			proveedor.setFijo(getFijo());
			proveedor.setNombre(getNombre().toUpperCase());
			proveedor.setRetencion(getRetencion());
			proveedor.setDireccion(getDireccion());
			proveedor.setProveedorId(getProveedorId());
			proveedor.setBaseRetencion(getRetencion());
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
        	Proveedor pro =proveedorService.getByName(getNombre().toUpperCase()); 
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
			proveedor.setCumpleanos(getCumpleanos());
			proveedor.setCupoCredito(getCupoCredito());
			proveedor.setDocumento(getDocumento());
			proveedor.setDireccion(getDireccion());
			proveedor.setFechaRegistro(new Date());
			proveedor.setFijo(getFijo());
			proveedor.setNombre(getNombre().toUpperCase());
			proveedor.setRetencion(getRetencion());
			proveedor.setBaseRetencion(getBaseRetencion());
			proveedorService.save(proveedor);
			getProveedores().add(proveedor);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Proveedor creado exitosamente"));
		}
	}
	
	
	/**
	 * Medo encargado de buscar las compras a los proveedores en un rango de fechas establecido
	 */
	public void buscarProveedores() {
		if(!validarfiltros()){
			return;
		}
		List<Proveedor> clientesTemp = new ArrayList<>();
		List<Long> tipoDocumentoId = new ArrayList<>();
		setProveedoresVo(new ArrayList<>());
		tipoDocumentoId.add(2l);//se agrega tipo documento factura de venta
		if (getProveedorId() == null) {
			clientesTemp = proveedorService.getByAll();
		} else {
			Proveedor cli = proveedorService.getById(getProveedorId());
			clientesTemp.add(cli);
		}
		for (Proveedor c : clientesTemp) {
				
				List<Documento> facturas = documentoService.getByProveedor(c.getProveedorId(),tipoDocumentoId,getFechaInicio(), getFechafin());
				Double total=0.0;
				Double retefuente=0.0;
				Double base5=0.0;
				Double base19=0.0;
				Double iva19=0.0;
				Double iva5=0.0;
				Double exento=0.0;
				if(!facturas.isEmpty()) {
					for(Documento d: facturas){
						total+=(d.getTotal()==null?0.0:d.getTotal());
						retefuente+=(d.getRetefuente()==null?0.0:d.getRetefuente());
						base5+=(d.getBase5()==null?0.0:d.getBase5());
						base19+=(d.getBase19()==null?0.0:d.getBase19());
						iva19+=(d.getIva19()==null?0.0:d.getIva19());
						iva5+=(d.getIva5()==null?0.0:d.getIva5());
						exento+=(d.getExcento()==null?0.0:d.getExcento());
					}
					ProveedorVo cl= new ProveedorVo();
					cl.setRetefuente(retefuente);
					cl.setProveedorId(c);
					cl.setTotalCompras(total);
					cl.setIva19(iva19);
					cl.setIva5(iva5);
					cl.setBase19(base19);
					cl.setBase5(base5);
					cl.setExento(exento);
					getProveedoresVo().add(cl);
				}
				
		}
	}
	
	/**
	 * Metodo encargado de realizar las validaciones de los rangos de fechas engresadas
	 * @return
	 */
	private boolean validarfiltros() {
		FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getFechaInicio() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria",""));
            valido = false;         
        }
    	if (getFechafin() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria",""));
            valido = false;         
        }
    	
    	if(getFechafin()!=null && getFechaInicio()!=null){
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		String fhoyIni = df.format(getFechaInicio());
    		String fhoyFin = df.format(getFechafin());
    		Long hoy = Long.valueOf(fhoyIni);
    		Long hoyfin = Long.valueOf(fhoyFin);
        	if(hoyfin<hoy){
        		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, Fecha fin es mayor a la fecha de inicio",""));
                valido = false;         
        	}
    	}
    	return valido;
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
	public Date getCumpleanos() {
		return cumpleanos;
	}
	public void setCumpleanos(Date cumpleanos) {
		this.cumpleanos = cumpleanos;
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

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechafin() {
		return fechafin;
	}

	public void setFechafin(Date fechafin) {
		this.fechafin = fechafin;
	}

	public Long getProveedoresId() {
		return proveedoresId;
	}

	public void setProveedoresId(Long proveedoresId) {
		this.proveedoresId = proveedoresId;
	}

	public List<ProveedorVo> getProveedoresVo() {
		return proveedoresVo;
	}

	public void setProveedoresVo(List<ProveedorVo> proveedoresVo) {
		this.proveedoresVo = proveedoresVo;
	}

	public double getBaseRetencion() {
		return baseRetencion;
	}

	public void setBaseRetencion(double baseRetencion) {
		this.baseRetencion = baseRetencion;
	}

	
}
