package com.fact.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Grupo;
import com.fact.model.Marca;
import com.fact.service.GrupoService;
import com.fact.service.MarcaService;


@FacesConverter("grupoConverter")
@ManagedBean
public class GrupoConverter  implements Converter{
	
	@EJB
	private GrupoService grupoService;
	
	List<Grupo> grupoAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {	        	
	        	Grupo p=new Grupo(); 
	        	String[]  parts1 = value.split(",");
	        	p.setNombre(parts1[0].replace("[", ""));	
	        	p.setGrupoId(Long.valueOf(parts1[1].trim()));
	        	p.setCodigo(Long.valueOf(parts1[2].replace("]", "").trim()));
	        	return p;
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {	
	        	Grupo p =(Grupo) object;
	        	List<String> pNew = new ArrayList<>();
	        	pNew.add(p.getNombre()==null?"":p.getNombre());
	        	pNew.add(p.getGrupoId()==null?"":p.getGrupoId().toString());
	        	pNew.add(p.getCodigo()==null?"0":p.getCodigo().toString());
	        	return pNew.toString();
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<Grupo> getGrupoAll() {
			if (grupoAll == null || grupoAll.isEmpty()) {
				grupoAll = grupoService.getByAll();
			}
			return grupoAll;
		}

		public void setGrupoAll(List<Grupo> grupoAll) {
			this.grupoAll = grupoAll;
		}
}
