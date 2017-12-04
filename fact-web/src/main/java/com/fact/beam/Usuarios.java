package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.fact.model.Empleado;
import com.fact.model.OpcionUsuario;
import com.fact.model.Rol;
import com.fact.model.SubMenu;
import com.fact.model.Usuario;
import com.fact.service.OpcionUsuarioService;
import com.fact.service.RolService;
import com.fact.service.SubMenuService;
import com.fact.service.UsuarioService;

@ManagedBean
@SessionScoped
public class Usuarios<hacer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private SubMenuService subMenuService;
	
	@EJB
	private RolService rolService;
	
	@EJB
	private UsuarioService usuarioService;
	
	@EJB
	private OpcionUsuarioService opcionUsuarioService;
	
	
	List<SubMenu> subMenuSource; 
	List<Rol> roles;
	List<Usuario> usuarios;
	Long rol;
	List<String> subMenuSelect; 
	String login ;
	String nombre;
	String apellido;
	String correo;
	Long identificacion;
	Boolean activo;
	String clave;
	String usuarioSelect;
	String usuarioOpcion;
	
	//empleados
	private String nombreEmpleado;
	private String aplellidoEmpleado;
	private String correoEmpleado;
	private String identificacionEmpleado;
	private List<Empleado> empleadoList;
	
	public boolean validar(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getRol() == null ||getRol()==0l) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Rol es obligatorio",""));
            valido = false;         
        }
    	if (getClave() == null || getClave().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La Clave es obligatoria",""));
            valido = false;         
        }
    	if (getLogin() == null || getLogin().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El login es obligatorio",""));
            valido = false;         
        }else{
        	Usuario u=usuarioService.getByName(getLogin().toUpperCase());
        	if(u!=null){
        		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Usuario ya existe",""));
                valido = false;         
        	}
        	//hacer la parte de que busca usaurio por login
        }
    	return valido;
	}
	
	public boolean validarEdicion(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getRol() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El Rol es obligatorio",""));
            valido = false;         
        }
    	if (getClave() == null || getClave().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La Clave es obligatoria",""));
            valido = false;         
        }
    	if (getLogin() == null || getLogin().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, El login es obligatorio",""));
            valido = false;         
        }
        	//hacer la parte de que busca usaurio por login
        
    	return valido;
	}
	
	public String guardarUsuario(){
		System.out.println("entra a guardar");
		if(validar()){
			Usuario usuario = new Usuario();
			usuario.setClave(getClave());
			usuario.setCorreo(getCorreo()==null?"":getCorreo().toUpperCase());
			usuario.setApellido(getApellido()==null?"":getApellido());
			usuario.setEstado(getActivo()==Boolean.TRUE?"1":"0");
			usuario.setNombre(getNombre()==null?"":getNombre());
			usuario.setLogin(getLogin().toUpperCase());
			usuario.setIdentificacion(getIdentificacion()==null?"":getIdentificacion().toString());
			Rol ro =new Rol();
			ro.setRolId(getRol());;
			usuario.setRolId(ro);
			usuario.setFechaRegistro(new Date());
			usuarioService.save(usuario);
			getUsuarios().add(usuario);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Usuario Creado exitosamente"));
		}
		return "";
	}
	
	public String llenarCampos(){
		System.out.println(getUsuarioSelect());
		Usuario u =new Usuario();
		for(Usuario uSelect:getUsuarios()){
			if(uSelect.getUsuarioId().toString().equals(getUsuarioSelect())){
				u=uSelect;
				break;
			}
		}
		setRol(u.getRolId().getRolId());
		setNombre(u.getNombre());
		setApellido(u.getApellido());
		setCorreo(u.getCorreo());
		setClave(u.getClave());
		setIdentificacion(u.getIdentificacion()==null?0l:Long.valueOf(u.getIdentificacion()));
	    setActivo(u.getEstado().equals("1")?Boolean.TRUE:Boolean.FALSE);
	    setLogin(u.getLogin());
		return "";
	}
	
	public String llenarOpciones(){
		List<OpcionUsuario> op = new ArrayList<>();
		setSubMenuSelect(null);
		op=opcionUsuarioService.getByUsuario(getUsuarioOpcion());
		if(op==null || op.isEmpty()){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No existen opciones configuradas para este usuario"));
		}else{
			for(OpcionUsuario o: op){
				getSubMenuSelect().add(o.getSubMenuId().getSubMenuId().toString());
			}
		}
		return "";
	}
	
	public String editarUsuario(){
		System.out.println("entra a guardar");
		if(validarEdicion()){
			Usuario usuario = new Usuario();
			usuario.setUsuarioId(Long.valueOf(getUsuarioSelect()));
			usuario.setClave(getClave());
			usuario.setCorreo(getCorreo()==null?"":getCorreo().toUpperCase());
			usuario.setApellido(getApellido()==null?"":getApellido());
			usuario.setEstado(getActivo()==Boolean.TRUE?"1":"0");
			usuario.setNombre(getNombre()==null?"":getNombre());
			usuario.setLogin(getLogin().toUpperCase());
			Rol ro =new Rol();
			ro.setRolId(getRol());;
			usuario.setRolId(ro);
			usuarioService.update(usuario);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Usuario Editado exitosamente"));
		}
		return "";		
	}
	
	public void guardarOpciones(){
		System.out.println("entra a guardar opciones");
		List<OpcionUsuario> op = new ArrayList<>();
		List<OpcionUsuario> opSource = new ArrayList<>();
		op=opcionUsuarioService.getByUsuario(getUsuarioOpcion());
		opSource=opcionUsuarioService.getByUsuario("1");
		if(op!=null && !op.isEmpty()){
			for(OpcionUsuario o: op){
				opcionUsuarioService.delete(o);
			}			
		}
		for(String o: getSubMenuSelect()){
			for(OpcionUsuario ops: opSource){
				if(ops.getSubMenuId().getSubMenuId().toString().equals(o)){
					OpcionUsuario opOj =  ops;
					Usuario usua = new Usuario();
					usua.setUsuarioId(Long.valueOf(getUsuarioOpcion()));
					opOj.setUsuarioId(usua);
					opOj.setOpcionUsuarioId(null);
					opOj.setFechaRegistro(new Date());
					opOj.setEstado(1l);
					opcionUsuarioService.save(opOj);
					break;
				}
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Opciones Editadas exitosamente"));
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public void buscarEmpleados(){
		setEmpleadoList(usuarioService.getByFiltrosEmpleados(getNombreEmpleado(),getAplellidoEmpleado(),getCorreoEmpleado(),getIdentificacionEmpleado()));
	}

	public List<SubMenu> getSubMenuSource() {
		if(subMenuSource==null ||subMenuSource.isEmpty()){
			subMenuSource=subMenuService.getByAll();
		}
		return subMenuSource;
	}

	public void setSubMenuSource(List<SubMenu> subMenuSource) {
		this.subMenuSource = subMenuSource;
	}

	public List<String> getSubMenuSelect() {
		if(subMenuSelect==null){
			subMenuSelect = new ArrayList<>();
		}
		return subMenuSelect;
	}

	public void setSubMenuSelect(List<String> subMenuSelect) {
		this.subMenuSelect = subMenuSelect;
	}

	public List<Rol> getRoles() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		String userDeveloper = (String) sessionMap.get("userDeveloper");
		    if(userDeveloper.equals("true")){
		    	roles=rolService.getByAll();
		    }else{
		    	roles=rolService.getSinPropietario();
		    }
			
		
		return roles;
	}
	
	public void crearEmpreado(){
		Empleado emp= new Empleado();
		emp.setNombre(getAplellidoEmpleado());
		emp.setCorreo(getCorreoEmpleado());
		emp.setApellido(getAplellidoEmpleado());
		emp.setIdentificacion(getIdentificacionEmpleado());
		emp.setEstado("S");
		emp.setFechaRegistro(new Date());
		usuarioService.save(emp);
		RequestContext.getCurrentInstance().execute("PF('crearEmpleado').hide();");	
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Opciones Editadas exitosamente"));
		
	}

	public void setRoles(List<Rol> roles) {
		this.roles = roles;
	}

	public Long getRol() {
		return rol;
	}

	public void setRol(Long rol) {
		this.rol = rol;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Long getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(Long identificacion) {
		this.identificacion = identificacion;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public List<Usuario> getUsuarios() {
		if(usuarios==null||usuarios.isEmpty()){
			usuarios= usuarioService.getByAll();
		}
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public String getUsuarioSelect() {
		return usuarioSelect;
	}

	public void setUsuarioSelect(String usuarioSelect) {
		this.usuarioSelect = usuarioSelect;
	}

	public String getUsuarioOpcion() {
		return usuarioOpcion;
	}

	public void setUsuarioOpcion(String usuarioOpcion) {
		this.usuarioOpcion = usuarioOpcion;
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getAplellidoEmpleado() {
		return aplellidoEmpleado;
	}

	public void setAplellidoEmpleado(String aplellidoEmpleado) {
		this.aplellidoEmpleado = aplellidoEmpleado;
	}

	public String getCorreoEmpleado() {
		return correoEmpleado;
	}

	public void setCorreoEmpleado(String correoEmpleado) {
		this.correoEmpleado = correoEmpleado;
	}

	public String getIdentificacionEmpleado() {
		return identificacionEmpleado;
	}

	public void setIdentificacionEmpleado(String identificacionEmpleado) {
		this.identificacionEmpleado = identificacionEmpleado;
	}

	public List<Empleado> getEmpleadoList() {
		return empleadoList;
	}

	public void setEmpleadoList(List<Empleado> empleadoList) {
		this.empleadoList = empleadoList;
	}
	
	

	
}
