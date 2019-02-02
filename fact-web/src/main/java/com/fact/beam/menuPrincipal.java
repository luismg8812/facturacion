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
	
	public void cerrarSesioin() throws FactException, IOException {
		sessionMap.clear();
		getMenus();
	}

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
	
	public List<OpcionUsuario> getOpcionUsuarios(String menuId) {
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		//long TInicio, TFin, tiempo; //Variables para determinar el tiempo de ejecución
		//  TInicio = System.currentTimeMillis(); //Tomamos la hora en que inicio el algoritmo y la almacenamos en la variable inicio
		opcionUsuarios= opcionUsuarioService.getByMenuId(menuId,usuario.getUsuarioId());
		//TFin = System.currentTimeMillis(); //Tomamos la hora en que finalizó el algoritmo y la almacenamos en la variable T
		//  tiempo = TFin - TInicio; //Calculamos los milisegundos de diferencia
		 // System.out.println("Tiempo occiones menu:"+menuId+":" + tiempo); //Mostramos en pantalla el tiempo de ejecución en milisegundos
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
