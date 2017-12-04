package com.fact.beam;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import com.fact.model.Cliente;
import com.fact.model.Producto;
import com.fact.service.ProductoService;

@ManagedBean
@SessionScoped
public class VentaUnitaria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@EJB
	private ProductoService productoService;
	
	
	private Producto articulo;
	private Cliente cliente;
	


	public Producto getArticulo() {
		return articulo;
	}

	public void setArticulo(Producto articulo) {
		this.articulo = articulo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	

}
