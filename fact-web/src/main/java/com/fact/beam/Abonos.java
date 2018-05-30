package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import com.fact.model.Abono;
import com.fact.model.Cliente;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.Proveedor;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoPago;
import com.fact.model.Usuario;
import com.fact.service.AbonoService;
import com.fact.service.ClienteService;
import com.fact.service.DocumentoService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoPagoService;
import com.fact.service.UsuarioService;

@ManagedBean
@SessionScoped
public class Abonos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7074628949042545489L;
	private static Logger log = Logger.getLogger(Abonos.class);

	@EJB
	private ProveedorService proveedorService;
	@EJB
	private DocumentoService documentoService;
	@EJB
	private AbonoService abonoService;
	@EJB
	private TipoPagoService tipoPagoService;

	@EJB
	private ClienteService clienteService;

	@EJB
	private UsuarioService usuarioService;

	// proveedores
	private Long proveedorId;
	private Long tipoFacturas;
	private Long tipoPago;
	private Date fechaInicio;
	private Date fechafin;
	private String detalle;
	private Abono abonoNew;
	private Double cantidadNew;
	private Date fecha;
	private Double cantidadTotal;
	private String detalleNew;
	private Double total;
	private Double saldoTotal;

	// clientes
	private Long clienteId;
	private List<Documento> documentosCliente;
	private List<Cliente> clientes;
	private Documento documentoSelect;

	// avance efectivo
	private List<Documento> avances;

	private List<Proveedor> proveedores;
	private List<Documento> documentos;
	private List<Abono> abonosByDocumento;
	private List<TipoPago> TipoPagos;
	private Double interes;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Usuario usuario() {
		return (Usuario) sessionMap.get("userLogin");
	}

	public void crearAvance() {
		if (getCantidadTotal() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("La cantidad de avance es obligatoria"));
			return;
		}
		
		if(getInteres()!=null && getInteres()<0 || getInteres()>100){
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("El interes debe estar entre 0 y 100"));
			return;
		}
		Proveedor pro = null;
		Cliente cli = null;
		Documento docu = new Documento();
		if (getProveedorId() == null && getClienteId() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Debe seleccionar un proveedor o un cliente"));
			return;
		}
		if (getProveedorId() != null && getClienteId() != null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Solo puede seleccionar o un cliente o un proveedor"));
			return;
		}

		if(getProveedorId()!=null){
			pro = proveedorService.getById(getProveedorId());
			if ( pro.getCreditoActivo() == null || pro.getCreditoActivo() != 1l) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("El proveedor NO esta activo para creditos "));
				return;
			}
			if (getProveedorId() != null) {
				List<Long> tipos = new ArrayList<>();
				tipos.add(5l);
				List<Documento> docum = documentoService.getByClienteAndProveedorAndTipo(null, getProveedorId(),tipos );
				Double tope = 0.0;
				for(Documento d: docum){
					tope+=d.getTotal();
				}
				if((tope+getCantidadTotal()) > pro.getCupoCredito()){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("El proveedor solo tiene un cupo maximo de " + pro.getCupoCredito()));
					return;
				}
				
			}
			docu.setProveedorId(pro);
			
		}
		
		if(getClienteId()!=null){
			cli = clienteService.getById(getClienteId());
			if (getClienteId() == null || cli.getCreditoActivo() == null || cli.getCreditoActivo() != 1l) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("El cliente NO esta activo para creditos "));
				return;
			}
			if ( getClienteId()!=null) {
				
				List<Long> tipos = new ArrayList<>();
				tipos.add(5l);
				List<Documento> docum = documentoService.getByClienteAndProveedorAndTipo(null, getClienteId(),tipos );
				Double tope = 0.0;
				for(Documento d: docum){
					tope+=d.getTotal();
				}
				if((tope+getCantidadTotal()) > cli.getCupoCredito()){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("El Cliente solo tiene un cupo maximo de " + cli.getCupoCredito()));
					return;
				}
			}
			docu.setClienteId(cli);	
		
		}

		

		
		TipoDocumento tido = new TipoDocumento();
		Usuario usuario = usuario();
		Configuracion configuracion = (Configuracion) sessionMap.get("configuracion");
		Long server = configuracion.getServer();
		docu.setFechaRegistro(new Date());
		
		tido.setTipoDocumentoId(5l); // se le envia tipo documento avance
										// efectivo
		docu.setTipoDocumentoId(tido);
		Double interesG = (getInteres()!=null?(getInteres()/100):getInteres());
		log.info("interesg"+interesG);
		Double totalG=getCantidadTotal()+(getCantidadTotal()*interesG);
		docu.setSaldo(totalG);
		docu.setTotal(totalG);
		docu.setUsuarioId(usuario);
		docu.setInteres(getInteres()==null?0.0:getInteres());
		try {
			documentoService.save(docu, server);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Creando Avance efectivo, por favor consulte a su proveedor del programa"));
			log.info("Server two desactivate: ");
		}
		log.info("se crea el adelanto efectivo: " + docu.getDocumentoId());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Avance efectivo Creado exitosamente"));
	}

	public void eliminarAvanceEfectivo(Documento docu) {
		getAvances().remove(docu);
		documentoService.delete(docu);
		RequestContext.getCurrentInstance().update("avances");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Avance efectivo Eliminado exitosamente"));
		RequestContext.getCurrentInstance().update("growl1");
	}

	public void buscarAvancesEfectivo() {
		List<Long> tipoId = new ArrayList<>();
		tipoId.add(5l);// se agrega tipo documento avance efectivo
		setAvances(documentoService.getByClienteAndProveedorAndTipo(getClienteId(), getProveedorId(), tipoId));
	}

	public void crearVale() {
		Documento docu = new Documento();
		Cliente cli = new Cliente();
		TipoDocumento tido = new TipoDocumento();
		TipoPago pago = new TipoPago();
		Usuario usuario = usuario();
		long server = 1;
		docu.setFechaRegistro(new Date());
		cli.setClienteId(getClienteId());
		docu.setClienteId(cli);
		docu.setSaldo(getCantidadTotal());
		tido.setTipoDocumentoId(8l); // se le envia tipo documento vale (Vale)
										// // por que zohan dijo
		docu.setTipoDocumentoId(tido);
		pago.setTipoPagoId(6l); // se envia tipo pago con vale, debido a que el
								// vale es un documento a credito
		docu.setTipoPagoId(pago);
		docu.setTotal(getCantidadTotal());
		docu.setUsuarioId(usuario);
		docu.setDetalleEntrada(getDetalleNew());
		documentoService.save(docu, server);
		log.info("se crea el vale por abonos Cliente:" + docu.getDocumentoId());
		RequestContext.getCurrentInstance().execute("PF('crearVale').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vale Creado exitosamente"));
	}

	public void crearDocumento() {
		Documento docu = new Documento();
		Proveedor pro = new Proveedor();
		TipoDocumento tido = new TipoDocumento();
		Usuario usuario = usuario();
		long server = 1;
		docu.setFechaRegistro(new Date());
		pro.setProveedorId(getProveedorId());
		docu.setProveedorId(pro);
		docu.setSaldo(getCantidadTotal());
		tido.setTipoDocumentoId(2l); // se le envia tipo documento entrada de
										// almacen
		docu.setTipoDocumentoId(tido);
		docu.setTotal(getCantidadTotal());
		docu.setUsuarioId(usuario);
		docu.setDetalleEntrada(getDetalleNew());
		documentoService.save(docu, server);
		log.info("se crea el documento por abonos:" + docu.getDocumentoId());
		RequestContext.getCurrentInstance().execute("PF('crearDocumento').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Documento Creado exitosamente"));
	}

	public void crearAbono() {
		getAbonoNew().setCantidadAbono(getCantidadNew());
		TipoPago tipa = new TipoPago();
		tipa.setTipoPagoId(getTipoPago());
		Usuario usuario = usuario();
		long server = 1;
		getAbonoNew().setUsuarioId(usuario);
		getAbonoNew().setTipoPagoId(tipa);
		abonoService.save(getAbonoNew());
		Documento docu = getAbonoNew().getDocumentoId();
		Double saldoNew = docu.getSaldo() == null ? 0.0 : docu.getSaldo();
		docu.setSaldo(saldoNew - getCantidadNew());
		documentoService.update(docu, server);
		log.info("se crea el abono:" + getAbonoNew().getAbonoId());
		RequestContext.getCurrentInstance().execute("PF('crearAbono').hide();");
		RequestContext.getCurrentInstance().execute("PF('crearAbonoCliente').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Abono Creado exitosamente"));
		setAbonoNew(null);
	}

	public void consultarAbonos(Documento docu) {
		setAbonosByDocumento(abonoService.getByDocumento(docu.getDocumentoId()));
		RequestContext.getCurrentInstance().execute("PF('consultarAbono').show();");

	}

	public void consultarAbonosCliente(Documento docu) {
		setDocumentoSelect(docu);
		log.info("documento id" + docu.getDocumentoId());
		setAbonosByDocumento(abonoService.getByDocumento(docu.getDocumentoId()));
		RequestContext.getCurrentInstance().execute("PF('consultarAbonoCliente').show();");
		RequestContext.getCurrentInstance().update("consultarAbono");
	}

	public void abrirPopupAbono(Documento docu) {
		log.info("entra a popup crear abono:" + docu.getDocumentoId());
		getAbonoNew().setDocumentoId(docu);
		getAbonoNew().setFechaRegistro(new Date());
		setCantidadNew(null);
		RequestContext.getCurrentInstance().execute("PF('crearAbono').show();");
		RequestContext.getCurrentInstance().update("@form");
	}

	public void abrirPopupAbonoCliente(Documento docu) {
		log.info("entra a popup crear abono:" + docu.getDocumentoId());
		getAbonoNew().setDocumentoId(docu);
		getAbonoNew().setFechaRegistro(new Date());
		setCantidadNew(null);
		RequestContext.getCurrentInstance().execute("PF('crearAbonoCliente').show();");
		RequestContext.getCurrentInstance().update("@form");
	}

	public void borrarVale(Documento docu) {
		log.info("entra a borrar vale:" + docu.getDocumentoId());
		if (docu.getTipoDocumentoId().getTipoDocumentoId() == 8l) {
			documentoService.delete(docu);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vale borrado exitosamente"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("El documento que intenta borrar no es un Vale"));
		}

		RequestContext.getCurrentInstance().update("@form");
	}

	public String abonoByDocumento(Documento docu) {
		if (docu != null) {
			List<Abono> aList = new ArrayList<>();
			aList = abonoService.getByDocumento(docu.getDocumentoId());
			if (aList == null || aList.isEmpty()) {
				return "0";
			} else {
				return "" + aList.size();
			}
		} else {
			return "0";
		}

	}

	public String buscar() {
		setDocumentos(documentoService.buscarPorAbonos(getProveedorId(), getTipoFacturas(), getFechaInicio(),
				getFechafin(), getDetalle()));
		Double totalTemp = 0.0;
		Double saldoTotalTemp = 0.0;
		for (Documento d : getDocumentos()) {
			totalTemp = totalTemp + (d.getTotal() == null ? 0.0 : d.getTotal());
			saldoTotalTemp = saldoTotalTemp + (d.getSaldo() == null ? 0.0 : d.getSaldo());
		}
		setSaldoTotal(saldoTotalTemp);
		setTotal(totalTemp);
		return "";
	}

	public String buscarAbonosCliente() {
		setDocumentosCliente(documentoService.buscarPorAbonosByClient(getClienteId(), getFechaInicio(), getFechafin()));
		return "";
	}

	public List<Proveedor> getProveedores() {
		if (proveedores == null) {
			proveedores = proveedorService.getByAll();
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

	public Long getTipoFacturas() {
		return tipoFacturas;
	}

	public void setTipoFacturas(Long tipoFacturas) {
		this.tipoFacturas = tipoFacturas;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
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

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public Abono getAbonoNew() {
		if (abonoNew == null) {
			abonoNew = new Abono();
		}
		return abonoNew;
	}

	public void setAbonoNew(Abono abonoNew) {
		this.abonoNew = abonoNew;
	}

	public Double getCantidadNew() {
		return cantidadNew;
	}

	public void setCantidadNew(Double cantidadNew) {
		this.cantidadNew = cantidadNew;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public List<Abono> getAbonosByDocumento() {
		return abonosByDocumento;
	}

	public void setAbonosByDocumento(List<Abono> abonosByDocumento) {
		this.abonosByDocumento = abonosByDocumento;
	}

	public List<TipoPago> getTipoPagos() {
		if (TipoPagos == null) {
			TipoPagos = tipoPagoService.getByAll();
		}
		return TipoPagos;
	}

	public void setTipoPagos(List<TipoPago> tipoPagos) {
		TipoPagos = tipoPagos;
	}

	public Long getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(Long tipoPago) {
		this.tipoPago = tipoPago;
	}

	public Double getCantidadTotal() {
		return cantidadTotal;
	}

	public void setCantidadTotal(Double cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public List<Documento> getDocumentosCliente() {
		return documentosCliente;
	}

	public void setDocumentosCliente(List<Documento> documentosCliente) {
		this.documentosCliente = documentosCliente;
	}

	public String getDetalleNew() {
		return detalleNew;
	}

	public void setDetalleNew(String detalleNew) {
		this.detalleNew = detalleNew;
	}

	public List<Cliente> getClientes() {
		clientes = clienteService.getByAll();
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Documento getDocumentoSelect() {
		return documentoSelect;
	}

	public void setDocumentoSelect(Documento documentoSelect) {
		this.documentoSelect = documentoSelect;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getSaldoTotal() {
		return saldoTotal;
	}

	public void setSaldoTotal(Double saldoTotal) {
		this.saldoTotal = saldoTotal;
	}

	public List<Documento> getAvances() {
		return avances;
	}

	public void setAvances(List<Documento> avances) {
		this.avances = avances;
	}

	public Double getInteres() {
		return interes;
	}

	public void setInteres(Double interes) {
		this.interes = interes;
	}
	
	

}
