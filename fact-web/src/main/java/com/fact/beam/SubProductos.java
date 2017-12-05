package com.fact.beam;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.fact.model.Producto;
import com.fact.model.SubProducto;
import com.fact.service.ProductoService;

/**
 * @author luismg
 *
 */

@ManagedBean
@SessionScoped
public class SubProductos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7025045429756792297L;
	/**
	 * 
	 */
	

	
	@EJB
	private ProductoService productoService;
	
	private Long productoPadre;
	private Long productoHijo;
	private Double Cantidad;
	
	private List<Producto> productosList;
	private List<Producto> productosHijoList;
	private List<SubProducto> subProductosList;
	
	/**
	 * Metodo que consulta los subproductos por id de un producto
	 */
	public void subProductoPorProducto(){
		System.out.println("id: "+getProductoPadre());
		setSubProductosList(productoService.subProductoByProducto(getProductoPadre()));
		System.out.println("subproductos:"+getSubProductosList().size());
	}
	
	public void borrar(SubProducto s){
		FacesContext context = FacesContext.getCurrentInstance();
		productoService.delete(s);
		getSubProductosList().remove(s);
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Relación borrada",""));
	}
	
	public void agregar(){
		FacesContext context = FacesContext.getCurrentInstance();
		if(getProductoPadre()==null ){
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El producto es obligatorio",""));
			return ;
		}
		if(getProductoHijo()==null ){
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El subproducto es obligatorio",""));
			return ;
		}
		if(getCantidad()==null ){
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La cantidad es obligatoria",""));
			return ;
		}
		for(SubProducto s: getSubProductosList()){
			if(s.getProductoHijo().getProductoId()==getProductoHijo() && s.getProductoPadre().getProductoId()==getProductoPadre()){
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe la relación entre producto y subProducto",""));
				return ;
			}
		}
		SubProducto sub = new SubProducto();
		
		sub.setProductoPadre(productoService.getById(getProductoPadre()));
		sub.setProductoHijo(productoService.getById(getProductoHijo()));
		sub.setCantidad(getCantidad());
		sub.setEstado(1l);
		productoService.save(sub);
		getSubProductosList().add(sub);
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Relación creada exitosamente","")); 
	}
	
	public List<Producto> getProductosHijoList() {
		productosHijoList=productoService.getBySubProducto(1);
		return productosHijoList;
	}
	public void setProductosHijoList(List<Producto> productosHijoList) {
		this.productosHijoList = productosHijoList;
	}
	public List<Producto> getProductosList() {
		productosList=productoService.getBySubProducto(0);
		return productosList;
	}
	public void setProductosList(List<Producto> productosList) {
		this.productosList = productosList;
	}
	public List<SubProducto> getSubProductosList() {
		return subProductosList;
	}
	public void setSubProductosList(List<SubProducto> subProductosList) {
		this.subProductosList = subProductosList;
	}
	public Long getProductoPadre() {
		return productoPadre;
	}
	public void setProductoPadre(Long productoPadre) {
		this.productoPadre = productoPadre;
	}
	public Long getProductoHijo() {
		return productoHijo;
	}
	public void setProductoHijo(Long productoHijo) {
		this.productoHijo = productoHijo;
	}
	public Double getCantidad() {
		return Cantidad;
	}
	public void setCantidad(Double cantidad) {
		Cantidad = cantidad;
	}
	
	

}
