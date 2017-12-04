package com.fact.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Grupo;
import com.fact.model.Marca;
import com.fact.model.Producto;
import com.fact.model.Proveedor;
import com.fact.service.ProductoService;


@FacesConverter("productoConverter")
@ManagedBean
public class ProductoConverter  implements Converter{
	
	@EJB
	private ProductoService productoService;
	
	List<Producto> productosAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {       	
	        	Producto p=new Producto(); 
	        	
	        	String[]  parts1 = value.split(",");
	        	p.setCodigoInterno(parts1[0].replace("[", "")); 	
	       		Marca marca = new Marca();
	        	marca.setMarcaId(Long.valueOf(parts1[1].trim().equals("")?"1":parts1[1].trim()));// se asigna por defecto la marca varios si esta el campo vacio 
		        p.setMarcaId(marca);	        	
	        	p.setNombre(parts1[2].trim());
	        	p.setUnidad(parts1[3].trim());
	        	p.setBalanza(Long.valueOf(parts1[4].trim().equals("")?"0":parts1[4].trim()));
	        	p.setCantidad(Double.valueOf(parts1[5].trim().equals("")?"0.0":parts1[5].trim()));
	        	p.setCodigoBarras(Long.valueOf(parts1[6].trim().equals("")?"0":parts1[6].trim()));
	        	p.setCosto(Double.valueOf(parts1[7].trim().equals("")?"0.0":parts1[7].trim()));
	        	p.setCostoPublico(Double.valueOf(parts1[8].trim().equals("")?"0.0":parts1[8].trim()));
	        	p.setFechaActualiza(new Date());
	        	//p.setFechaRegistro(parts1[10]);
	        	if(!parts1[11].trim().equals("")){
	        		Grupo grupo = new Grupo();
		        	grupo.setGrupoId(Long.valueOf(parts1[11].trim()));
		        	p.setGrupoId(grupo);
	        	}
	        	
	        	p.setIva(Double.valueOf(parts1[12].trim().equals("")?"0":parts1[12].trim()));
	        	p.setPeso(Double.valueOf(parts1[13].trim().equals("")?"0.0":parts1[13].trim()));
	        	p.setProductoId(Long.valueOf(parts1[14].trim()));
	        	p.setStockMax(Long.valueOf(parts1[15].trim().equals("")?"0":parts1[15].trim()));
	        	p.setEstado(Long.valueOf(parts1[16].trim().equals("")?"0":parts1[16].trim()));
	        	p.setPromo(Long.valueOf(parts1[17].trim().equals("")?"0":parts1[17].trim()));
	        	p.setPubPromo(Double.valueOf(parts1[18].trim().equals("")?"0":parts1[18].trim()));
	        	p.setkGPromo(Double.valueOf(parts1[19].trim().equals("")?"0":parts1[19].trim()));
	        	Proveedor pro = new Proveedor();
	        	pro.setProveedorId(Long.valueOf(parts1[20].trim().equals("")?"1":parts1[20].trim())); // se asigna el proveedor "varios" si es vacio
	        	p.setProveedorId(pro);
	        	p.setHipoconsumo(Double.valueOf(parts1[21].trim().equals("")?"0":parts1[21].trim()));
	        	p.setVarios(Long.valueOf(parts1[22].trim().equals("")?"0":parts1[22].trim()));
	        	p.setUtilidadSugerida(Double.valueOf(parts1[23].trim().equals("")?"0.0":parts1[23].trim()));
	        	p.setCostoSugerida(Double.valueOf(parts1[24].trim().equals("")?"0.0":parts1[24].trim()));
	        	p.setEsCarne(Long.valueOf(parts1[25].trim().equals("")?"0":parts1[25].trim()));
	        	p.setSubProducto(Long.valueOf(parts1[26].trim().equals("")?"0":parts1[26].trim()));
	        	p.setStockMin(Long.valueOf(parts1[27].replace("]", "").trim().equals("")?"0":parts1[27].replace("]", "").trim()));       	
	        	return p;
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {	
	        	Producto p =(Producto) object;
	        	List<String> pNew = new ArrayList<>();
	        	pNew.add(p.getCodigoInterno()==null?"":p.getCodigoInterno());
	        	pNew.add(p.getMarcaId()==null?"":p.getMarcaId().getMarcaId().toString());
	        	pNew.add(p.getNombre()==null?"":p.getNombre().trim());
	        	pNew.add(p.getUnidad()==null?"":p.getUnidad());
	        	pNew.add(p.getBalanza()==null?"":p.getBalanza().toString());
	        	pNew.add(p.getCantidad()==null?"":p.getCantidad().toString());
	        	pNew.add(p.getCodigoBarras()==null?"":p.getCodigoBarras().toString());
	        	pNew.add(p.getCosto()==null?"":p.getCosto().toString());
	        	pNew.add(p.getCostoPublico()==null?"":p.getCostoPublico().toString());
	        	pNew.add(p.getFechaActualiza()==null?"":p.getFechaActualiza().toString());
	        	pNew.add(p.getFechaRegistro()==null?"":p.getFechaRegistro().toString());
	        	pNew.add(p.getGrupoId()==null?"":p.getGrupoId().getGrupoId().toString());
	        	pNew.add(p.getIva()==null?"":p.getIva().toString());
	        	pNew.add(p.getPeso()==null?"":p.getPeso().toString());
	        	pNew.add(p.getProductoId()==null?"":p.getProductoId().toString());
	        	pNew.add(p.getStockMax()==null?"":p.getStockMax().toString());
	        	pNew.add(p.getEstado()==null?"":p.getEstado().toString());
	        	pNew.add(p.getPromo()==null?"":p.getPromo().toString());
	        	pNew.add(p.getPubPromo()==null?"":p.getPubPromo().toString());
	        	pNew.add(p.getkGPromo()==null?"":p.getkGPromo().toString());
	        	pNew.add(p.getProveedorId()==null?"":p.getProveedorId().getProveedorId().toString());
	        	pNew.add(p.getHipoconsumo()==null?"":p.getHipoconsumo().toString());
	        	pNew.add(p.getVarios()==null?"":p.getVarios().toString());
	        	pNew.add(p.getUtilidadSugerida()==null?"":p.getUtilidadSugerida().toString());
	        	pNew.add(p.getCostoSugerida()==null?"":p.getCostoSugerida().toString());
	        	pNew.add(p.getEsCarne()==null?"":p.getEsCarne().toString());
	        	pNew.add(p.getSubProducto()==null?"":p.getSubProducto().toString());
	        	pNew.add(p.getStockMin()==null?"":p.getStockMin().toString());
	        	
	        	return pNew.toString();
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<Producto> getProductosAll() {
			if (productosAll == null || productosAll.isEmpty()) {
				productosAll = productoService.getByAll();
			}
			return productosAll;
		}

		public void setProductosAll(List<Producto> productosAll) {
			this.productosAll = productosAll;
		}
}
