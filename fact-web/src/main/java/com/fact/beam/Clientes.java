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

import org.jboss.logging.Logger;

import com.fact.api.Calculos;
import com.fact.model.Ciudad;
import com.fact.model.Cliente;
import com.fact.model.Departamento;
import com.fact.model.Documento;
import com.fact.service.CiudadService;
import com.fact.service.ClienteService;
import com.fact.service.DepartamentoService;
import com.fact.service.DocumentoService;
import com.fact.vo.ClienteVo;

@ManagedBean
@SessionScoped
public class Clientes implements Serializable {


	
	private static final long serialVersionUID = -5189988812307879612L;

	private static Logger log = Logger.getLogger(Clientes.class);

	@EJB
	private DepartamentoService departamentoService;

	@EJB
	private CiudadService ciudadService;

	@EJB
	private ClienteService clienteService;

	@EJB
	private DocumentoService documentoService;

	private String nombre;
	private Long ciudad;
	private Long departamento;
	private String documento;
	private String Barrio;
	private String direccion;
	private Long celular;
	private Long fijo;
	private Date Cumpleaños;
	private Boolean creditoActivo = Boolean.FALSE;
	private Long cupoCredito;
	private double retencion;
	private Long clienteId;
	private Boolean guiaTrasnsporte = Boolean.FALSE;

	private List<Departamento> departamentos;
	private List<Ciudad> ciudades;
	private List<Cliente> clientes;

	// terceros
	private Date fechaInicio;
	private Date fechafin;
	private Long clienteIdTerceros;
	private List<ClienteVo> terceros;
	
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
	
	/**
	 * Medo encargado de buscar los terceros en un rango de fechas establecido
	 */
	public void buscarTerceros() {
		if(!validarfiltros()){
			return;
		}
		List<Cliente> clientesTemp = new ArrayList<>();
		List<Long> tipoDocumentoId = new ArrayList<>();
		setTerceros(new ArrayList<>());
		tipoDocumentoId.add(10l);//se agrega tipo documento factura de venta
		if (getClienteId() == null) {
			clientesTemp = clienteService.getByAll();
		} else {
			Cliente cli = clienteService.getById(getClienteId());
			clientesTemp.add(cli);
		}
		for (Cliente c : clientesTemp) {
				
				List<Documento> facturas = documentoService.getByCliente(c.getClienteId(),tipoDocumentoId,Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()));
				Double total=0.0;
				Double excento=0.0;
				Double base19=0.0;
				Double base5=0.0;
				Double iva19=0.0;
				Double iva5=0.0;
				for(Documento d: facturas){
					total=(d.getTotal()==null?0.0:d.getTotal())+total;
					excento=(d.getExcento()==null?0.0:d.getExcento())+excento;
					base19=(d.getBase19()==null?0.0:d.getBase19())+base19;
					base5=(d.getBase5()==null?0.0:d.getBase5())+base5;
					iva19=(d.getIva19()==null?0.0:d.getIva19())+iva19;
					iva5=(d.getIva5()==null?0.0:d.getIva5())+iva5;
				}
				ClienteVo cl= new ClienteVo();
				cl.setClienteId(c);
				cl.setBaseIva19(base19);
				cl.setBaseIva5(base5);
				cl.setExcento(excento);
				cl.setIva19(iva19);
				cl.setIva5(iva5);
				cl.setTotalCompras(total);
				getTerceros().add(cl);
		}
	}

	public String llenarCampos() {
		log.info(getClienteId());
		Cliente p = new Cliente();
		for (Cliente pSelect : getClientes()) {
			if (pSelect.getClienteId().toString().equals(getClienteId().toString())) {
				p = pSelect;
				break;
			}
		}
		setBarrio(p.getBarrio());
		setCelular(p.getCelular());
		setCiudad(p.getCiudadId() != null ? p.getCiudadId().getCiudadId() : 0l);
		setCreditoActivo(p.getCreditoActivo() == 0 ? Boolean.FALSE : Boolean.TRUE);
		setGuiaTrasnsporte(p.getGuiaTransporte() == 0 ? Boolean.FALSE : Boolean.TRUE);
		setCumpleaños(p.getCumpleanos());
		setCupoCredito(p.getCupoCredito());
		setDireccion(p.getDireccion());
		setDocumento(p.getDocumento());
		setFijo(p.getFijo());
		setNombre(p.getNombre());
		setRetencion(p.getRetencion());
		// setDepartamento(p.getCiudadId()!=null?p.getCiudadId().getDepartamentoId().getDepartamentoId():0l);
		return "";
	}

	public boolean validarEdicion() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valido = true;
		if (getNombre() == null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Nombre es obligatorio", ""));
			valido = false;
		}
		// hacer la parte de que busca usaurio por login
		return valido;
	}

	public String editarCliente() {
		log.info("entra a guardar");
		if (validarEdicion()) {
			Cliente cliente = new Cliente();
			cliente.setBarrio(getBarrio());
			cliente.setCelular(getCelular());
			Ciudad ciu = new Ciudad();
			ciu.setCiudadId(getCiudad());
			cliente.setCiudadId(ciu);
			cliente.setCreditoActivo(getCreditoActivo() == Boolean.TRUE ? 1l : 0l);
			cliente.setGuiaTransporte(getGuiaTrasnsporte() == Boolean.TRUE ? 1l : 0l);
			cliente.setCumpleanos(getCumpleaños());
			cliente.setCupoCredito(getCupoCredito());
			cliente.setDocumento(getDocumento() == null ? "" : getDocumento());
			cliente.setFechaRegistro(new Date());
			cliente.setFijo(getFijo());
			cliente.setNombre(getNombre().toUpperCase());
			cliente.setRetencion(getRetencion());
			cliente.setDireccion(getDireccion());
			cliente.setClienteId(getClienteId());
			clienteService.update(cliente);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Proveedor Editado exitosamente"));
		}
		return "";
	}

	public boolean validar() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valido = true;
		if (getNombre() == null || getNombre().equals("")) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,El Nombre es obligatorio", ""));
			valido = false;
		} else {
			Cliente cli = new Cliente();
			cli = clienteService.getByName(getNombre().toUpperCase());
			if (cli != null) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Proveedor ya existe", ""));
				valido = false;
			}
		}
		return valido;
	}

	public void guardarCliente() {
		if (validar()) {
			Cliente cliente = new Cliente();
			cliente.setBarrio(getBarrio());
			cliente.setCelular(getCelular());
			if (getCiudad() != null && getCiudad() != 0l) {
				Ciudad ciu = new Ciudad();
				ciu.setCiudadId(getCiudad());
				cliente.setCiudadId(ciu);
			}
			cliente.setCreditoActivo(getCreditoActivo() == Boolean.TRUE ? 1l : 0l);
			cliente.setGuiaTransporte(getGuiaTrasnsporte() == Boolean.TRUE ? 1l : 0l);
			cliente.setCumpleanos(getCumpleaños());
			cliente.setCupoCredito(getCupoCredito());
			cliente.setDocumento(getDocumento() == null ? "" : getDocumento());
			cliente.setDireccion(getDireccion());
			cliente.setFechaRegistro(new Date());
			cliente.setFijo(getFijo());
			cliente.setNombre(getNombre().toUpperCase());
			cliente.setRetencion(getRetencion());
			clienteService.save(cliente);
			getClientes().add(cliente);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente creado exitosamente"));
		}
	}

	public String ciudadByDepartamento(AjaxBehaviorEvent event) {
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
		if (departamentos == null) {
			departamentos = departamentoService.getByAll();
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

	public List<Cliente> getClientes() {
		clientes = clienteService.getByAll();
		return clientes;
	}

	public void setProveedores(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public Boolean getGuiaTrasnsporte() {
		return guiaTrasnsporte;
	}

	public void setGuiaTrasnsporte(Boolean guiaTrasnsporte) {
		this.guiaTrasnsporte = guiaTrasnsporte;
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

	public Long getClienteIdTerceros() {
		return clienteIdTerceros;
	}

	public void setClienteIdTerceros(Long clienteIdTerceros) {
		this.clienteIdTerceros = clienteIdTerceros;
	}

	public List<ClienteVo> getTerceros() {
		if(terceros==null){
			terceros=new ArrayList<>();
		}
		return terceros;
	}

	public void setTerceros(List<ClienteVo> terceros) {
		this.terceros = terceros;
	}

}
