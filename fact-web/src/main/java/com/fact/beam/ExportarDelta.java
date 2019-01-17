package com.fact.beam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.model.Cliente;
import com.fact.model.Proveedor;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoPago;
import com.fact.model.Usuario;
import com.fact.service.ClienteService;
import com.fact.service.DocumentoService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoDocumentoService;
import com.fact.service.TipoPagoService;
import com.fact.service.UsuarioService;
import com.fact.vo.ConvinacionDelta;

@ManagedBean
@SessionScoped
public class ExportarDelta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7074628949042545489L;
	private static Logger log = Logger.getLogger(ExportarDelta.class);

	@EJB
	private ProveedorService proveedorService;
	@EJB
	private DocumentoService documentoService;
	
	@EJB
	private TipoDocumentoService tipoDocumentoService;
	
	@EJB
	private TipoPagoService tipoPagoService;
	@EJB
	private ClienteService clienteService;
	@EJB
	private UsuarioService usuarioService;

	// exportar terceros
	private String[] selectedTerceros;

	//exportar documentos
	private Date fechaInicio;
	private Date fechafin;
	private List<TipoDocumento> tipoDocumentos;
	private Long tipoDocumento;
	private List<TipoPago> tipoPagos;
	private Long tipoPago;
	private String cuenta;
	private List<ConvinacionDelta> convinacionDeltas = new ArrayList<>();

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Usuario usuario() {
		return (Usuario) sessionMap.get("userLogin");
	}
	
	public void agregar() {
		log.info("agregar convinacion");
		//TODO agregar validaciones de los campos para que quede bien evitar nulos
		ConvinacionDelta convinacionDelta = new ConvinacionDelta();
		convinacionDelta.setCuenta(getCuenta());
		convinacionDelta.setTipodocumentoId(getTipoDocumento());
		convinacionDelta.setTipoDocumentoNombre(tipoDocumentoService.getById(getTipoDocumento()).getNombre());
		convinacionDelta.setTipoPagoId(getTipoPago());
		convinacionDelta.setTipoPagoNombre(tipoDocumentoService.getById(getTipoPago()).getNombre());
		getConvinacionDeltas().add(convinacionDelta);
	}
	
	public void borrar(ConvinacionDelta convinacionDelta) {
		getConvinacionDeltas().remove(convinacionDelta);
	}

	public StreamedContent getFile() throws FileNotFoundException {
		StreamedContent file = null;
		String ruta = documentoTerceros();
		File f = new File(ruta);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", ruta);
		}
		return file;
	}
	
	public StreamedContent getFileDocument() throws FileNotFoundException {
		StreamedContent file = null;
		String ruta = documento();
		if(getConvinacionDeltas().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay convinaciones agregadas"));
			return null;
		}
		File f = new File(ruta);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", ruta);
		}
		return file;
	}

	private String documento() {
		log.info("entra a exportar terceros delta");
		String carpeta = "C:\\facturas\\documentosDELTA\\";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String fhoyIni = df.format(new Date());
		String pdf = "DocumentosDELTA_" + fhoyIni + ".txt";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		return carpeta + pdf;
	}

	private String documentoTerceros() {
		log.info("entra a exportar terceros delta");
		String carpeta = "C:\\facturas\\tercerosDELTA\\";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String fhoyIni = df.format(new Date());
		String pdf = "tercerosDELTA_" + fhoyIni + ".txt";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		String tab = "	";
		BufferedWriter bw;
		List<Cliente> cliente =new ArrayList<>();
		List<Proveedor> proveedor =new ArrayList<>();
		for( int i=0; i<getSelectedTerceros().length; i++) {
			if(getSelectedTerceros()[i].equals("clientes")) {
				cliente=clienteService.getByAll();
			}
			if(getSelectedTerceros()[i].equals("proveedores")) {
				proveedor= proveedorService.getByAll();
			}
		}
		try {
			String heder = "CodTercero" + tab + "Nombre" + tab + "Apellidos" + tab + "CCNit" + tab + "DV" + tab
					+ "TipoDocId" + tab + "Empresa" + tab + "Regimen" + tab + "Naturaleza" + tab + "Direccion" + tab + "CodBarrio" + tab + "CodCiudad" + tab + "CodPais"
					+ tab + "Telefono" + tab + "Celular" + tab + "Email" + tab + "CodActivEco" + tab + "CodTipoTerc" + tab + "CodZona" + tab+ "Contacto" + tab;
			bw = new BufferedWriter(new FileWriter(folder));
			bw.write(heder);		
				for(Cliente c: cliente) {
					String lineaCliente ="C"+c.getClienteId()+tab+
										 c.getNombre()+
										 " "+tab+
										 c.getDocumento()+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 (c.getDireccion()==null?"":c.getDireccion())+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 (c.getFijo()==null?"":c.getFijo())+tab+
										 (c.getCelular()==null?"":c.getCelular())+tab+
										 (c.getMail()==null?"":c.getMail())+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab;
					bw.write("\n"+lineaCliente);
				}
				for(Proveedor c: proveedor) {
					String lineaCliente ="P"+c.getProveedorId()+tab+
										 c.getNombre()+
										 " "+tab+
										 c.getDocumento()+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 (c.getDireccion()==null?"":c.getDireccion())+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 (c.getFijo()==null?"":c.getFijo())+tab+
										 (c.getCelular()==null?"":c.getCelular())+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab+
										 ""+tab;
					bw.write("\n"+lineaCliente);
				}
				bw.close();
			
		} catch (IOException e1) {

			e1.printStackTrace();
			log.error("error exportando archivo de terceros de delta");
		}
		return carpeta + pdf;
	}
	
	

	public List<ConvinacionDelta> getConvinacionDeltas() {
		return convinacionDeltas;
	}

	public void setConvinacionDeltas(List<ConvinacionDelta> convinacionDeltas) {
		this.convinacionDeltas = convinacionDeltas;
	}

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String[] getSelectedTerceros() {
		return selectedTerceros;
	}

	public void setSelectedTerceros(String[] selectedTerceros) {
		this.selectedTerceros = selectedTerceros;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechafin() {
		return fechafin;
	}

	public void setFechafin(Date fechafin) {
		this.fechafin = fechafin;
	}
	
	public List<TipoDocumento> getTipoDocumentos() {
		if(tipoDocumentos==null) {
			tipoDocumentos=tipoDocumentoService.getByAll();
		}
		return tipoDocumentos;
	}

	public void setTipoDocumentos(List<TipoDocumento> tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}

	public List<TipoPago> getTipoPagos() {
		if(tipoPagos==null) {
			tipoPagos= tipoPagoService.getByAll();
		}
		return tipoPagos;
	}

	public void setTipoPagos(List<TipoPago> tipoPagos) {
		this.tipoPagos = tipoPagos;
	}

	public Long getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(Long tipoPago) {
		this.tipoPago = tipoPago;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	
	
	
}
