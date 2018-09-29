package com.fact.beam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.model.Configuracion;
import com.fact.model.Empresa;
import com.fact.model.Usuario;
import com.fact.service.UsuarioService;

@ManagedBean
@SessionScoped
public class Login implements Serializable {

	/**
	 * luis Miguel gonzalez
	 */
	private static final long serialVersionUID = 1L;

	String usuario;
	String password;

	public Usuario usuarioLogin = new Usuario();
	public static Empresa EmpresaLogin = new Empresa();
	public static Long activo = 0l;

	Boolean propietario = Boolean.FALSE;
	public Boolean propietarioLogin = Boolean.FALSE;
	public Boolean developerLogin = Boolean.FALSE;
	public String impresora;
	
	private StreamedContent graphicImage;
	
	

	@EJB
	private UsuarioService usuarioService;
	
	

	  public StreamedContent getGraphicImage() {  
		  	String path = "C:\\facturacion\\logoEmpresa.jpg";
		  	try {
				graphicImage=new DefaultStreamedContent(new FileInputStream(path), "image/png");
			} catch (FileNotFoundException e) {
				System.out.println("Error cargando imagen principal");
				e.printStackTrace();
			}
	        return graphicImage;
	    }

	    public void setGraphicImage(StreamedContent graphicImage) {
	        this.graphicImage = graphicImage;
	    }

	public boolean validar(){
    	Usuario usu= new Usuario();
    	FacesContext context = FacesContext.getCurrentInstance();
    	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
    	boolean valido= true;
    	if (getUsuario() == null || getUsuario().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,El usuario es obligatorio",""));
            valido = false;
            setUsuario("");
            setPassword("");
        }else {      	
        	usu=usuarioService.getByName(getUsuario().toUpperCase()); 
        	if(usu==null){
        	    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,Usuario o Password incorectos",""));
                valido = false;
                setUsuario("");
                setPassword("");
            }else{
            	if(!usu.getClave().equals(getPassword())){
            		 context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,Usuario o Password incorectos",""));
                     valido = false;
                     setUsuario("");
                     setPassword("");
            	}
            }
        }
    	
    	Configuracion configuracion = usuarioService.getConfiguracion();
    	if(configuracion==null){
    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, Existe un problema con su versión del software",""));
            valido = false;
    	}else{
    		if(!configuracion.getDemo().toUpperCase().equals("FULL")){
    			final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
    			Date hoy = new Date();
    			   			   		   			
        		if(hoy.compareTo(configuracion.getFechaIngreso())>0){
        			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, su versión 'DEMO' del software expliró, por favor consulte al proveedor",""));
                    valido = false;
        		}else{
        			Calendar calendar = new GregorianCalendar();
        			calendar.setTime(configuracion.getFechaIngreso());
        			java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());
        			long diferencia = (fecha.getTime()- hoy.getTime()   )/MILLSECS_PER_DAY;
        			if(diferencia<7){
        				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "Su versión 'DEMO' del software esta apunto de expirar, por favor consulte al proveedor",""));
        			}
        		}
    		} 		
    	}
    	
    	if (getPassword() == null || getPassword().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!,El password es obligatorio",""));
            valido = false;
        }
    	
    	if(valido){		
    		sessionMap.put("userLogin",  usu);
    		sessionMap.put("configuracion",  configuracion); //manejo de los dos servidores uno para respaldo 
   		 	setUsuarioLogin(usu);
    	}
    	return valido;
    	
    }

	public void logueo() throws IOException {
		if (validar()) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    		Map<String, Object> sessionMap = externalContext.getSessionMap();
			if (getUsuarioLogin().getRolId().getRolId() == 3l) { // verifica que
																	// el
																	// usuario
																	// logueado
																	// tenga el
																	// rol de
																	// propietario
				
	    		sessionMap.put("userPropietario","true");
				setPropietario(Boolean.TRUE);
				setPropietarioLogin(Boolean.TRUE);
			} else {
				sessionMap.put("userPropietario","false");
				setPropietario(Boolean.FALSE);
				setPropietarioLogin(Boolean.FALSE);
			}
			if (getUsuarioLogin().getRolId().getRolId() == 4l) {
				setDeveloperLogin(Boolean.TRUE);
				sessionMap.put("userDeveloper","true");
				
			} else {
				sessionMap.put("userDeveloper","false");
				setDeveloperLogin(Boolean.FALSE);
				
			}
			System.out.println("usuarioLogin: " + getUsuarioLogin().getLogin());
			//setImpresora(getUsuarioLogin().getImpresora() == null ? "" : getUsuarioLogin().getImpresora());
			sessionMap.put("impresora",leerArchivoImpresora());
			//System.out.println("impresora:" +(String) sessionMap.get("impresora"));
			sessionMap.put("empresa",usuarioService.getByEmpresa(getUsuarioLogin().getUsuarioId()));
			//setEmpresaLogin(); // consulta la // empresa
			
			String url = "/fact-web/pages/administracion/menu/menuPrincipal.jsf"; //url donde se redirige la pantalla
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().redirect(url);// redirecciona la página
		}
	}
	
	 private String leerArchivoImpresora() {
	      File archivo = null;
	      FileReader fr = null;
	      BufferedReader br = null;
	      String linea="";
	      try {
	       
	    	  HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	  		//request.getRemoteAddr();	  		
	         archivo = new File ("\\\\"+request.getRemoteAddr()+"\\facturacion\\impresora.txt");
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);

	         // Lectura del fichero
	         
	         while((linea=br.readLine())!=null)
	           return linea;
	      }
	      catch(Exception e){
	         e.printStackTrace();
	         return linea;
	      }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	            return linea;
	         }
	      }
	      return linea;
	   }

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Usuario getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Usuario usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public static Long getActivo() {
		return activo;
	}

	public static void setActivo(Long activo) {
		Login.activo = activo;
	}

	public static Empresa getEmpresaLogin() {
		return EmpresaLogin;
	}

	public static void setEmpresaLogin(Empresa empresaLogin) {
		EmpresaLogin = empresaLogin;
	}

	public Boolean getPropietario() {
		return propietario;
	}

	public void setPropietario(Boolean propietario) {
		this.propietario = propietario;
	}

	public Boolean getPropietarioLogin() {
		return propietarioLogin;
	}

	public void setPropietarioLogin(Boolean propietarioLogin) {
		this.propietarioLogin = propietarioLogin;
	}

	public Boolean getDeveloperLogin() {
		return developerLogin;
	}

	public void setDeveloperLogin(Boolean developerLogin) {
		this.developerLogin = developerLogin;
	}

	public  String getImpresora() throws IOException {
		if (impresora == null) {

		}
		return impresora;
	}

	public  void setImpresora(String impresora) {
		this.impresora = impresora;
	}

}
