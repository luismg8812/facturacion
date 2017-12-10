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
import org.primefaces.event.RowEditEvent;

import com.fact.model.Empresa;
import com.fact.model.Producto;
import com.fact.model.ProductoEmpresa;
import com.fact.model.TransferenciaEmpresa;
import com.fact.model.TransferenciaEmpresaDetalle;
import com.fact.service.ProductoEmpresaService;
import com.fact.service.ProductoService;
import com.fact.service.TransferenciaEmpresaService;
import com.fact.service.UsuarioService;

@ManagedBean
@SessionScoped
public class Sucursales implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8482659843887410377L;

	@EJB
	private ProductoEmpresaService productoEmpresaService;
	
	@EJB
	private UsuarioService usuarioService;
	
	@EJB
	private ProductoService productoService;
	
	@EJB
	private TransferenciaEmpresaService transferenciaEmpresaService;

	//producto por sucursal
	private Long empresaId;
	private List<ProductoEmpresa> productoEmpresaList;
	private List<Empresa>  sucursalesList;
	
	//transferencias
	private Long desde;
	private Long hasta;
	private Long ProductoNew;
	private Double cantidad;
	private List<TransferenciaEmpresaDetalle>productosList= new ArrayList<>();
	
	
	public void confirmar(){
		TransferenciaEmpresa te = new TransferenciaEmpresa();
		Empresa ed= new Empresa();
		ed.setEmpresaId(getDesde());
		Empresa eh= new Empresa();
		eh.setEmpresaId(getHasta());
		te.setEmpresaDesde(ed);
		te.setEmpresaHasta(eh);
		transferenciaEmpresaService.save(te);
	}
	
	public void agregar(){
		if(getDesde()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Origen del traslado es obligatorio"));
			return;
		}	
		if(getHasta()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Destino del traslado es obligatorio"));
			return;
		}
		if(getProductoNew()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Debe seleccionar algun producto"));
			return;
		}
		if(getCantidad()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad es obligatorio"));
			return;
		}else{
			if(getCantidad()<0){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad no puede ser negativa"));
				return;
			}
		}
		TransferenciaEmpresaDetalle dt= new TransferenciaEmpresaDetalle();
		dt.setCantidad(getCantidad());
		Producto produ= productoService.getById(getProductoNew());
		dt.setProductoId(produ);
		dt.setFechaRegistro(new Date());
		getProductosList().add(dt);
	}
	
	public void onRowEdit(RowEditEvent event) {
		ProductoEmpresa pe=(ProductoEmpresa) event.getObject();
		System.out.println("cantidad:"+pe.getCantidad());
		System.out.println("cantidad:"+pe.getPrecio());
		productoEmpresaService.save(pe);
    }
	
	public void onRowCancel(RowEditEvent event) {
      
    }
	
	/**
	 * Metodo encargado de buscar los productos configurados para una determinada empresa
	 */
	public void buscarProductosXSucursal(){
		if(getEmpresaId()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Seleccione una sucursal"));
			return;		
		}
		setProductoEmpresaList(productoEmpresaService.getByEmpresa(getEmpresaId()));
		if(getProductoEmpresaList()==null||getProductoEmpresaList().isEmpty()){
			RequestContext.getCurrentInstance().execute("PF('confirmarBusqueda').show();");
		}
	}
	
	/**
	 * si se confirma la busqueda se crean los registros de Productos por empresa
	 */
	public void confirmarBusqueda(){
		List<Producto> productos=productoService.getByAll();
		for(Producto p: productos){
			ProductoEmpresa proEmpr = new ProductoEmpresa();
			proEmpr.setCantidad(p.getCantidad()==null?0.0:p.getCantidad());
			proEmpr.setPrecio(p.getCostoPublico()==null?0.0:p.getCostoPublico());
			Empresa empr=new Empresa();
			empr.setEmpresaId(getEmpresaId());
			proEmpr.setEmpresaId(empr);
			proEmpr.setFechaRegistro(new Date());
			proEmpr.setProductoId(p);
			getProductoEmpresaList().add(proEmpr);
			productoEmpresaService.save(proEmpr);		
			RequestContext.getCurrentInstance().execute("PF('confirmarBusqueda').hide();");
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Productos creados Exitosamente para la sucursal seleccionada"));
		setProductoEmpresaList(productoEmpresaService.getByEmpresa(getEmpresaId()));
	}

	public List<ProductoEmpresa> getProductoEmpresaList() {
		return productoEmpresaList;
	}

	public void setProductoEmpresaList(List<ProductoEmpresa> productoEmpresaList) {
		this.productoEmpresaList = productoEmpresaList;
	}

	public List<Empresa> getSucursalesList() {
		sucursalesList=usuarioService.getByAllEmpresa();
		return sucursalesList;
	}

	public void setSucursalesList(List<Empresa> sucursalesList) {
		this.sucursalesList = sucursalesList;
	}
	
	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}

	public Long getDesde() {
		return desde;
	}

	public void setDesde(Long desde) {
		this.desde = desde;
	}

	public Long getHasta() {
		return hasta;
	}

	public void setHasta(Long hasta) {
		this.hasta = hasta;
	}

	public Long getProductoNew() {
		return ProductoNew;
	}

	public void setProductoNew(Long productoNew) {
		ProductoNew = productoNew;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public List<TransferenciaEmpresaDetalle> getProductosList() {
		return productosList;
	}

	public void setProductosList(List<TransferenciaEmpresaDetalle> productosList) {
		this.productosList = productosList;
	}

	
}
