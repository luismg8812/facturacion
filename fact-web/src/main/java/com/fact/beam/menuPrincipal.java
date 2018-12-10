package com.fact.beam;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.fact.api.FactException;
import com.fact.model.Menu;
import com.fact.model.OpcionUsuario;
import com.fact.model.Usuario;
import com.fact.service.MenuService;
import com.fact.service.OpcionUsuarioService;
import com.fact.service.UsuarioService;


@ManagedBean
@SessionScoped
public class menuPrincipal implements Serializable {

	/**
	 * luis miguel gonzalez
	 * LyM tecnologia e innovación
	 * luismg8812@hotmail.com
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private MenuService menuService;
	@EJB
	private OpcionUsuarioService opcionUsuarioService;
	
	@EJB
	private UsuarioService usuarioService;
	
	List<Menu> menus;
	List<OpcionUsuario> opcionUsuarios;
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	

	public List<Menu> getMenus() throws FactException, IOException{ 
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		if(usuario==null){
			FacesContext fc = FacesContext.getCurrentInstance();
	        fc.getExternalContext().redirect("/fact-web/index.jsf");//redirecciona la página}
	        return null;
		}
		menus= menuService.getByAll();
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	
	public List<OpcionUsuario> getOpcionUsuarios(String MenuId) {
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		opcionUsuarios= opcionUsuarioService.getByMenuId(MenuId,usuario.getUsuarioId());
		return opcionUsuarios;
	}

	public void setOpcionUsuarios(List<OpcionUsuario> opcionUsuarios) {
		this.opcionUsuarios = opcionUsuarios;
	}
	
	public List<OpcionUsuario> getOpcionUsuariosByRuta(String ruta) {
		
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		opcionUsuarios= opcionUsuarioService.getByRuta(ruta.toUpperCase(),usuario.getUsuarioId());
		return opcionUsuarios;
	}

	public void setOpcionUsuariosByRuta(List<OpcionUsuario> opcionUsuarios) {
		this.opcionUsuarios = opcionUsuarios;
	}
}
