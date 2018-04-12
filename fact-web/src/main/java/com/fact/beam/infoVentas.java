package com.fact.beam;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.fact.api.Calculos;
import com.fact.model.Documento;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.ProductoService;
import com.fact.service.UsuarioService;
import com.fact.vo.ReduccionVo;


@ManagedBean
@SessionScoped
public class infoVentas  implements Serializable{
	
	/**
	 * luis Miguel gonzalez
	 */
	private static final long serialVersionUID = 1L;
	
	
	@EJB
	private DocumentoDetalleService documentoDetalleService;
	@EJB
	private ProductoService productoService;
	@EJB
	private DocumentoService documentoService;
	@EJB
	private UsuarioService usuarioService;
 
	
 
	private Date fechaInicio;
	private Date fechafin;
	private Double totalVentas=0.0;
	
	private List<ReduccionVo> documentos;
	
	
	
	
	
	
	private boolean validarfiltros() {
		FacesContext context = FacesContext.getCurrentInstance();
    	boolean valido= true;
    	if (getFechaInicio() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria",""));
            valido = false;         
        }
    	if (getFechafin() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria",""));
            valido = false;         
        }
    	
    	if(getFechafin()!=null && getFechaInicio()!=null){
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		String fhoyIni = df.format(getFechaInicio());
    		String fhoyFin = df.format(getFechafin());
    		Long hoy = Long.valueOf(fhoyIni);
    		Long hoyfin = Long.valueOf(fhoyFin);
        	if(hoyfin<hoy){
        		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, Fecha fin es mayor a la fecha de inicio",""));
                valido = false;         
        	}
    	}
    	//hacer la parte de que busca usaurio por login
		return valido;
	}
	
	public void buscar() throws ParseException{
		if(validarfiltros()){
			setDocumentos(null);
			Double total =0.0;
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");			
			String fhoyIni = df.format(getFechaInicio());
			String fhoyFin = df.format(getFechafin());
			Long hoy = Long.valueOf(fhoyIni);
			Long hoyfin = Long.valueOf(fhoyFin);
			for(Long i = hoy; i<=hoyfin;i++){
				List<Usuario> usu=  usuarioService.getByRol(2l); // se bucan cajeros(rol=2)
				for(Usuario u: usu){
					System.out.println(df.parse(i.toString()));
					Double cantidadOriginal = getTotalFaturasToDay(df.parse(i.toString()),u);
					ReduccionVo rvo = new ReduccionVo();
					rvo.setCantidadOriginal(cantidadOriginal);
					rvo.setUsuarioId(u);
					rvo.setFecha(df.parse(i.toString()));
					getDocumentos().add(rvo);
				}
			}
			for(ReduccionVo r:getDocumentos()){
				total=r.getCantidadOriginal()+total;
			}
			setTotalVentas(total);
		}	
	}
	
	public Double getTotalFaturasToDay(Date dia, Usuario usuario) throws ParseException {
		Long tipoDocumentoId = 10l; // tipo documento factura de salida	
		Date hoy = Calculos.fechaInicial(dia);   
		Date hoyfin = Calculos.fechaFinal(dia);
		Boolean conCierre=Boolean.FALSE;
		List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin,usuario.getUsuarioId(),conCierre);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
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

	public List<ReduccionVo> getDocumentos() {
		if(documentos==null){
			documentos= new ArrayList<>();
		}
		return documentos;
	}

	public void setDocumentos(List<ReduccionVo> documentos) {
		this.documentos = documentos;
	}

	public Double getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(Double totalVentas) {
		this.totalVentas = totalVentas;
	}
	
	
}
