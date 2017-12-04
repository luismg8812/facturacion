package com.fact.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Marca;
import com.fact.service.MarcaService;


@FacesConverter("marcaConverter")
@ManagedBean
public class MarcaConverter  implements Converter{
	
	@EJB
	private MarcaService marcaService;
	
	List<Marca> marcaAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {	        	
	        	Marca p=new Marca(); 
	        	String[]  parts1 = value.split(",");
	        	p.setNombre(parts1[0].replace("[", ""));
	        	p.setEstado(Long.valueOf(parts1[1]));	        	
	        	p.setMarcaId(Long.valueOf(parts1[2].replace("]", "").trim()));
	        	return p;
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {	
	        	Marca p =(Marca) object;
	        	List<String> pNew = new ArrayList<>();
	        	pNew.add(p.getNombre()==null?"":p.getNombre());
	        	pNew.add(p.getEstado()==null?"":p.getEstado().toString());
	        	pNew.add(p.getMarcaId()==null?"":p.getMarcaId().toString());
	        	return pNew.toString();
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<Marca> getMarcaAll() {
			if (marcaAll == null || marcaAll.isEmpty()) {
				marcaAll = marcaService.getByAll();
			}
			return marcaAll;
		}

		public void setMarcaAll(List<Marca> marcaAll) {
			this.marcaAll = marcaAll;
		}
}
