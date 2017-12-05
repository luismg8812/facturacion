package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.fact.api.Calculos;
import com.fact.api.FactException;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.EntregaMercancia;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EntregaMercanciaService;
import com.fact.service.UsuarioService;
import com.fact.vo.BodegueroVo;


@ManagedBean
@SessionScoped
public class EntregaMercancias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3362505989446959147L;
	/**
	 * luis Miguel gonzalez
	 */
	//entrega de mercancia
	private String documento;
	private Long usuario;
	private Double total;
	private Double peso;
	private String cliente;
	private String estadoEntrega;
	private String codigoBodeguero;
	private Documento documentoSelect;
	private Usuario usuarioSelect;
	private List<DocumentoDetalle> documentoDetalleList;
	//cronometro de bodegueros
	private List<BodegueroVo> bodeguerosList;
	private List<BodegueroVo> bodeguerosFechasList;
	private BodegueroVo bodegueroSelect;
	private Date fechaIni;
	private Date fechaFin;

	@EJB
	private DocumentoService documentoService;
	@EJB
	private DocumentoDetalleService documentoDetalleService;
	@EJB
	private UsuarioService usuarioService;
	@EJB
	private EntregaMercanciaService entregaMercanciaService;
	
	
	public void entregasPorFechas(){
		System.out.println("entro");
		List<Usuario>sList= usuarioService.getByRol(6l);
		setBodeguerosFechasList(new ArrayList<BodegueroVo>());
		for(Usuario u:sList){
			Date inicio = Calculos.fechaInicial(getFechaIni());
			Date fin = Calculos.fechaFinal(getFechaFin()); 
			Long usuarioId =u.getUsuarioId();
			List<EntregaMercancia> cantidadFacturas = entregaMercanciaService.getEntregasByUsuario(usuarioId,inicio,fin);
			BodegueroVo b = new BodegueroVo();
			b.setNombreBodeguero(u.getNombre()+" "+u.getApellido());
			b.setCantidadFacturas((long)cantidadFacturas.size());
			getBodeguerosFechasList().add(b);
		}
	}
	
	/**
	 * metodo encargado de setear las variables que muestran el detalle de la factura que tiene el bodeguero
	 * @param detalle
	 * @return
	 */
	public String detalle(BodegueroVo detalle){
		setBodegueroSelect(detalle);
		try {
			setDocumentoDetalleList(documentoDetalleService.getByDocumento(detalle.getEntregaMercancia().getDocumentoId().getDocumentoId(),1l));
		} catch (Exception e) {
			setDocumentoDetalleList(new ArrayList<DocumentoDetalle>());
		}
		
		RequestContext.getCurrentInstance().execute("PF('detalle').show();");
		return "";
	}

	/**
	 * metodo que busca una factura por codigo de barras para los bodegueros
	 */
	public void buscarDocumentoCodBarras() {
		String completo = getDocumento();
		if (completo != null) {
			Documento d = null;
			try {
				Long id = Long.valueOf(completo);
				d = documentoService.getById(id);
			} catch (Exception e) {
				System.out.println("cod de documento no numérico");
				return;
			}
			if (d != null ) {
				System.out.println("busqueda por codigo de barras:" + d.getDocumentoId());
				if(d.getRetenido() != null && d.getRetenido()==1l){
					setEstadoEntrega("RETENIDA");
				}else{
					if (d.getEntregado() != null && d.getEntregado() == 1l) {
						setEstadoEntrega("ENTREGADO");
						
					} else {
						RequestContext.getCurrentInstance().execute("PF('codBodeguero').show();");
						setEstadoEntrega("POR ENTREGAR");
					}
				}
				
				setTotal(d.getTotal());
				setPeso(d.getPesoTotal());
				setCliente(d.getClienteId().getNombre());
				setDocumentoSelect(d);
				setDocumentoDetalleList(documentoDetalleService.getByDocumento(d.getDocumentoId(),1l));
			}
		}
	}
	
	/**
	 * metodo que busca un bodeguero por codigo de barras o id
	 */
	public void buscarBodegueroCodBarras() {
		String completo = getCodigoBodeguero();
		if (completo != null && !completo.isEmpty()) {
			Usuario d = null;
			try {
				Long id = Long.valueOf(completo);
				d = usuarioService.getById(id);
			} catch (Exception e) {
				System.out.println("cod de usuario no es numérico");
				return;
			}
			if (d != null) {
				//se pregunta si la persona que ingreso el codigo es un propietario
				if(d.getRolId().getRolId()==3l){
					RequestContext.getCurrentInstance().execute("PF('retener').show();");
					System.out.println("propietario: "+d.getNombre());
				}else{
					RequestContext.getCurrentInstance().execute("PF('confir').show();");
				}
				RequestContext.getCurrentInstance().execute("document.getElementById('codBodegueroForm:confirmarSi').focus();");
				//RequestContext.getCurrentInstance().execute("document.getElementById('codBodegueroForm:confirmarSi').select();");
				
				
				System.out.println("busqueda por codigo de barras de usuario:" + d.getUsuarioId());
				setUsuarioSelect(d);
			}else{
				return;
			}
		}
	}
	
	/**
	 * metodo encargado de limpiar todas las bariables cuando se genera una operacioon
	 */
	public void limpiarTodo(){
		System.out.println("limpiar todo");
		setDocumento(null);
		setTotal(null);
		setPeso(null);
		setEstadoEntrega(null);
		setCodigoBodeguero(null);
		setDocumentoSelect(null);
		setUsuarioSelect(null);
		setUsuario(null);
		setDocumentoDetalleList(null);
		RequestContext.getCurrentInstance().execute("document.getElementById('entregaMercanciaForm:codBarras_input1').focus();");
		RequestContext.getCurrentInstance().execute("document.getElementById('entregaMercanciaForm:codBarras_input1').select();");
		RequestContext.getCurrentInstance().update("entregaMercanciaForm:codBarras_input1");
		
	}

	/**
	 * metodo encargado de confirmar la entrega si el bodeguero presiona si
	 */
	public void confirmarEntrega(){
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			System.out.println("se confirmo codigo de bodeguero y entrega");
			EntregaMercancia entregaMercancia= new EntregaMercancia();
			entregaMercancia.setDocumentoId(getDocumentoSelect());
			entregaMercancia.setEntregado(1l); //se marca como entregado
			entregaMercancia.setFechaEntrega(new Date());
			entregaMercancia.setFechaRegistro(new Date());
			entregaMercancia.setUsuarioId(usuarioSelect);
			entregaMercanciaService.save(entregaMercancia);
			getDocumentoSelect().setEntregado(1l);
			//hay que verificar la parte de remisiones, le envio el server uno por defecto
			//pero hay que hacer la logica que maneje los dos servidores
			documentoService.update(getDocumentoSelect(),1l);
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación exitosa",""));				 
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!, No fue posible ingresar la confirmación",""));
			e.printStackTrace();
		}
		limpiarTodo();
		
	}
	
	public void retenerDocumento(){
		System.out.println("se confirmo codigo de bodeguero y entrega");
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			getDocumentoSelect().setRetenido(1l);
			//hay que verificar la parte de remisiones, le envio el server uno por defecto
			//pero hay que hacer la logica que maneje los dos servidores
			documentoService.update(getDocumentoSelect(),1l);
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Retención documeton exitosa",""));		
			limpiarTodo();
		} catch (FactException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!, No fue posible ingresar la confirmación",""));
			e.printStackTrace();
		}
		
	}
	
	
	
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Long getUsuario() {
		return usuario;
	}

	public void setUsuario(Long usuario) {
		this.usuario = usuario;
	}

	public Documento getDocumentoSelect() {
		return documentoSelect;
	}

	public void setDocumentoSelect(Documento documentoSelect) {
		this.documentoSelect = documentoSelect;
	}

	public Usuario getUsuarioSelect() {
		return usuarioSelect;
	}

	public void setUsuarioSelect(Usuario usuarioSelect) {
		this.usuarioSelect = usuarioSelect;
	}

	public List<DocumentoDetalle> getDocumentoDetalleList() {
		return documentoDetalleList;
	}

	public void setDocumentoDetalleList(List<DocumentoDetalle> documentoDetalleList) {
		this.documentoDetalleList = documentoDetalleList;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	public String getEstadoEntrega() {
		return estadoEntrega;
	}

	public void setEstadoEntrega(String estadoEntrega) {
		this.estadoEntrega = estadoEntrega;
	}

	public String getCodigoBodeguero() {
		return codigoBodeguero;
	}

	public void setCodigoBodeguero(String codigoBodeguero) {
		this.codigoBodeguero = codigoBodeguero;
	}

	public List<BodegueroVo> getBodeguerosList() {
		//se asigna el rol 6=bodegueros
		bodeguerosList=new ArrayList<>();
		List<Usuario>sList= usuarioService.getByRol(6l);
		for(Usuario u:sList){
			Date inicio = Calculos.fechaInicial(new Date());
			Date fin = Calculos.fechaFinal(new Date()); 
			Long usuarioId =u.getUsuarioId();
			List<EntregaMercancia> cantidadFacturas = entregaMercanciaService.getEntregasByUsuario(usuarioId,inicio,fin);
			BodegueroVo b = new BodegueroVo();
			b.setNombreBodeguero(u.getNombre()+" "+u.getApellido());
			b.setCantidadFacturas((long)cantidadFacturas.size());
			b.setCodigo(u.getUsuarioId());
			if(cantidadFacturas.size()>0){
				//cronómetro
				Date mayor= new Date();				
				Long diferencia = mayor.getTime()-cantidadFacturas.get(0).getFechaRegistro().getTime();
				Long segundos=Math.abs (diferencia/1000);
				segundos=segundos%60;
				Long minutos=Math.abs (diferencia/(60*1000));
				minutos=minutos%60;
				Long horas=Math.abs (diferencia/(60*60*1000));
				b.setTiempo(""+horas+":"+minutos+":"+segundos);
				//nombre cliente
				String cliente= cantidadFacturas.get(0).getDocumentoId().getClienteId().getNombre();
				b.setNombreCliente(cliente);
				//factura
				String factura = cantidadFacturas.get(0).getDocumentoId().getConsecutivoDian();
				b.setFactura(factura);
				b.setEntregaMercancia(cantidadFacturas.get(0));
			}else{
				b.setTiempo("00:00");
				b.setNombreCliente("");
				b.setFactura("");
			}
			bodeguerosList.add(b);
			
		}
		return bodeguerosList;
	}

	public void setBodeguerosList(List<BodegueroVo> bodeguerosList) {
		this.bodeguerosList = bodeguerosList;
	}

	public BodegueroVo getBodegueroSelect() {
		return bodegueroSelect;
	}

	public void setBodegueroSelect(BodegueroVo bodegueroSelect) {
		this.bodegueroSelect = bodegueroSelect;
	}

	public DocumentoDetalleService getDocumentoDetalleService() {
		return documentoDetalleService;
	}

	public void setDocumentoDetalleService(DocumentoDetalleService documentoDetalleService) {
		this.documentoDetalleService = documentoDetalleService;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public List<BodegueroVo> getBodeguerosFechasList() {
		return bodeguerosFechasList;
	}

	public void setBodeguerosFechasList(List<BodegueroVo> bodeguerosFechasList) {
		this.bodeguerosFechasList = bodeguerosFechasList;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

}
