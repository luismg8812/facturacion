package com.fact.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Empleado;
import com.fact.service.UsuarioService;


@FacesConverter("empleadoConverter")
@ManagedBean
public class EmpleadoConverter  implements Converter{
	
	@EJB
	private UsuarioService usuarioService;
	
	List<Empleado> empleadoAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {	        	
	        	Empleado p=new Empleado(); 
	        	String[]  parts1 = value.split(",");
	        	p.setNombre(parts1[0].replace("[", "").trim());
	        	p.setCorreo(parts1[1]);	        	
	        	p.setIdentificacion(parts1[2].trim());
	        	p.setEmpleadoId(Long.valueOf(parts1[3].trim()));
	        	p.setApellido(parts1[4].replace("]", "").trim().equals("")?"0":parts1[4].replace("]", "").trim());
	        	return p;
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {	
	        	Empleado p =(Empleado) object;
	        	List<String> pNew = new ArrayList<>();
	        	pNew.add(p.getNombre()==null?"":p.getNombre());
	        	pNew.add(p.getApellido()==null?"":p.getApellido());
	        	pNew.add(p.getCorreo()==null?"":p.getCorreo());
	        	pNew.add(p.getEmpleadoId().toString());
	        	pNew.add(p.getIdentificacion()==null?"":p.getIdentificacion());
	        	
	        	return pNew.toString();
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<Empleado> getEmpleadoAll() {
			if (empleadoAll == null || empleadoAll.isEmpty()) {
				empleadoAll = usuarioService.getByEmpleadosAll();
			}
			return empleadoAll;
		}

		public void setEmpleadoAll(List<Empleado> empleadoAll) {
			this.empleadoAll = empleadoAll;
		}
}
