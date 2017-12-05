package com.fact.beam;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name="commun", eager = true)
@SessionScoped
public class Commun  implements Serializable{
	
	
	
	/**
	 * luis Miguel gonzalez
	 */
	private static final long serialVersionUID = 1L;
	
	
//	@EJB
//	private static ProductoService productoService;
//	
//	static List<Producto> productosAll;
//	
//	@PostConstruct
//	 public void init() {
//		if (productosAll == null || productosAll.isEmpty()) {
//			productosAll = productoService.getByAll();
//		}
//	}
// 
//	public static List<Producto> getProductosAll() {
//		if (productosAll == null || productosAll.isEmpty()) {
//			productosAll = productoService.getByAll();
//		}
//		return productosAll;
//	}
//
//	public void setProductosAll(List<Producto> productosAll) {
//		Commun.productosAll = productosAll;
//	}
}
