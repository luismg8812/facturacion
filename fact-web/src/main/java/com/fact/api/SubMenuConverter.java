package com.fact.api;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Producto;
import com.fact.model.SubMenu;
import com.fact.service.SubMenuService;


@FacesConverter("subMenuConverter")
@ManagedBean
public class SubMenuConverter  implements Converter{
	
	@EJB
	private SubMenuService subMenuService;
	
	List<SubMenu> subMenuAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {
	        	for(SubMenu p: getSubMenuAll()){
	            	if(p.getSubMenuId().toString().equals(value)){
		            	return p;
		            }
	            }	                   
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {
	            return String.valueOf(((Producto) object).getProductoId());
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<SubMenu> getSubMenuAll() {
			if (subMenuAll == null || subMenuAll.isEmpty()) {
				subMenuAll = subMenuService.getByAll();
			}
			return subMenuAll;
		}

		public void setSubMenuAll(List<SubMenu> subMenuAll) {
			this.subMenuAll = subMenuAll;
		}
}
