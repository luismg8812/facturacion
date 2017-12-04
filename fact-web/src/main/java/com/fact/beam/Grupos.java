package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.fact.model.Configuracion;
import com.fact.model.Grupo;
import com.fact.model.Producto;
import com.fact.service.GrupoService;
import com.fact.service.ProductoService;

/**
 * @author luismg
 *
 */
@ManagedBean
@SessionScoped
public class Grupos implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -402426289576657404L;

	@EJB
	private GrupoService grupoService;

	@EJB
	private ProductoService productoService;

	
	private Long grupoId;
	private List<Grupo> gruposList;
	private List<Grupo> grupos;
	private String nombre;
	private Long codigo;
	private Grupo GrupoSelect;
	
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	public Configuracion configuracion(){		
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}


	/**
	 * Metodo encargado de guardar el grupo
	 */
	public void guardar(){
		if(getNombre()== null || getNombre().isEmpty()){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El nombre es obligatorio"));
			return;
		}
		Grupo grupo = getGrupoSelect();
		grupo.setNombre(getNombre());
		grupo.setCodigo(getCodigo());
		if(grupo.getGrupoId()==null){
			grupoService.save(grupo);
		}else{
			grupoService.update(grupo);
		}
		
		RequestContext.getCurrentInstance().execute("PF('crearGrupo').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Grupo creado exitosamente"));
		setGruposList(new ArrayList<>());
		getGruposList().add(grupo);
		getGrupos().add(grupo);
		
	}
	
	/**
	 * Metodo encargado de buscar los grupos
	 */
	public void buscar(){
		if(getGrupoId()!=null ){
			setGruposList(new ArrayList<>());
			Grupo g=grupoService.getById(getGrupoId());
			getGruposList().add(g);
		}else{
			setGruposList(grupoService.getByAll());
		}
		
	}
	
	/**
	 * Metodo encargado de abrir el popup de creacion de grupo
	 */
	public void crearGrupo(){
		limpiar();
		setGrupoSelect(new Grupo());
		RequestContext.getCurrentInstance().execute("PF('crearGrupo').show();");
	}
	
	/**
	 * Metodo encargago de abrir el popup para edicion del grupo
	 * @param grupo
	 */
	public void editarGrupo(Grupo grupo){
		setGrupoSelect(grupo);
		setNombre(grupo.getNombre());
		setCodigo(grupo.getCodigo());
		RequestContext.getCurrentInstance().execute("PF('crearGrupo').show();");
	}
	
	public void eliminar(Grupo grupo){
		List<Producto> productos = productoService.getByGrupo(grupo.getGrupoId());
		Configuracion configuracion = configuracion();
		for(Producto p: productos){
			p.setGrupoId(new Grupo());
			productoService.update(p, 1l);
			if(configuracion.getServer()==2l){
				productoService.update(p, configuracion.getServer());
			}
		}
		getGruposList().remove(grupo);
		grupoService.delete(grupo);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Grupo Eliminado exitosamente exitosamente"));
	}
	
	private void limpiar() {
		setNombre("");
		setCodigo(null);
	}

	public Long getGrupoId() {
		return grupoId;
	}
	public void setGrupoId(Long grupoId) {
		this.grupoId = grupoId;
	}
	public List<Grupo> getGruposList() {
		return gruposList;
	}
	public void setGruposList(List<Grupo> gruposList) {
		this.gruposList = gruposList;
	}
	public List<Grupo> getGrupos() {
		grupos=grupoService.getByAll();
		return grupos;
	}
	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}	
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public Grupo getGrupoSelect() {
		return GrupoSelect;
	}

	public void setGrupoSelect(Grupo grupoSelect) {
		GrupoSelect = grupoSelect;
	}
}
