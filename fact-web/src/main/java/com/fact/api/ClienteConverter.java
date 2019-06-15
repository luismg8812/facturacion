package com.fact.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.fact.model.Ciudad;
import com.fact.model.Cliente;
import com.fact.service.ClienteService;


@FacesConverter("clienteConverter")
@ManagedBean
public class ClienteConverter  implements Converter{
	
	@EJB
	private ClienteService clienteService;
	
	List<Cliente> clienteAll;
	 public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {	        	
	        	Cliente p=new Cliente(); 
	        	String[]  parts1 = value.split(",");
	        	p.setBarrio(parts1[0].replace("[", ""));
	        	p.setDireccion(parts1[1]);
	        	p.setNombre(parts1[2]);
	        	p.setDocumento(parts1[3]);
	        	p.setCelular(parts1[4].trim());
	        	Ciudad ciudad= new Ciudad();
	        	ciudad.setCiudadId(Long.valueOf(parts1[5].trim().equals("")?"0":parts1[5].trim()));
	        	p.setCiudadId(ciudad);
	        	//p.setCodigo(Long.valueOf(parts1[6].trim().equals("")?"0":parts1[6].trim()));
	        	p.setCreditoActivo(Long.valueOf(parts1[7].trim().equals("")?"0":parts1[7].trim()));
	        	//p.setCumpleaños((Date)(parts1[8].trim().equals("")?new Date():parts1[8].trim()));
	        	//p.setFechaActualiza(new Date());
	        	p.setCupoCredito((Long.valueOf(parts1[10].trim().equals("")?"0":parts1[10].trim())));
	        	p.setFijo(parts1[11].trim().equals("")?"0":parts1[11].trim());
	        	p.setClienteId(Long.valueOf(parts1[12].trim()));
	        	p.setGuiaTransporte(Long.valueOf(parts1[13].trim().equals("")?"0":parts1[13].trim()));
	        	p.setMail(parts1[14].trim());
	        	p.setEmpresa(parts1[15].trim());
	        	p.setRetencion(Double.valueOf(parts1[16].replace("]", "").trim().equals("")?"0":parts1[16].replace("]", "").trim()));
	        	return p;
	        }	       
	        return null;
	    }
	 
	 public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {	
	        	Cliente p =(Cliente) object;
	        	List<String> pNew = new ArrayList<>();
	        	pNew.add(p.getBarrio()==null?"":p.getBarrio());
	        	pNew.add(p.getDireccion()==null?"":p.getDireccion());
	        	pNew.add(p.getNombre());
	        	pNew.add(p.getDocumento()==null?"":p.getDocumento());
	        	pNew.add(p.getCelular()==null?"":p.getCelular().toString());
	        	pNew.add(p.getCiudadId()==null?"":p.getCiudadId().getCiudadId().toString());
	        	pNew.add("");
	        	pNew.add(p.getCreditoActivo()==null?"":p.getCreditoActivo().toString());
	        	pNew.add(p.getCumpleanos()==null?"":p.getCumpleanos().toString());
	        	pNew.add(p.getFechaRegistro()==null?"":p.getFechaRegistro().toString());
	        	pNew.add(p.getCupoCredito()==null?"":p.getCupoCredito().toString());
	        	pNew.add(p.getFijo()==null?"":p.getFijo().toString());
	        	pNew.add(p.getClienteId()==null?"":p.getClienteId().toString());
	        	pNew.add(p.getGuiaTransporte()==null?"":p.getGuiaTransporte().toString());
	        	pNew.add(p.getMail()==null?"":p.getMail());
	        	pNew.add(p.getEmpresa()==null?"":p.getEmpresa());
	        	pNew.add(p.getRetencion()==null?"":p.getRetencion().toString());       	
	        	
	        	return pNew.toString();
	        }
	        else {
	            return null;
	        }
	    }
	 
	 public  List<Cliente> getClienteAll() {
			if (clienteAll == null || clienteAll.isEmpty()) {
				clienteAll = clienteService.getByAll();
			}
			return clienteAll;
		}

		public void setClienteAll(List<Cliente> clienteAll) {
			this.clienteAll = clienteAll;
		}
}
