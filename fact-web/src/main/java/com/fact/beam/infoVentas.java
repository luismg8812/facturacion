package com.fact.beam;

import java.io.Serializable;
import java.text.ParseException;
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

import com.fact.api.Calculos;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;
import com.fact.model.ProductoEmpresa;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.ProductoEmpresaService;
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
	@EJB
	private ProductoEmpresaService productoEmpresaService;
 
	
 
	private Date fechaInicio;
	private Date fechafin;
	private Double totalVentas=0.0;
	
	private List<ReduccionVo> documentos;
	
	
	//cardex
	private Date fechaIni;
	private Date fechaFin;
	private Long productoId;
	List<ProductoEmpresa>productoList;
	List<DocumentoDetalle> cardexList;
	private ProductoEmpresa actual;
	private Double totalSalidas;
	private Double totalEntradas;
	
	//movimiento documento
	private Date fechaIniMovimiento;
	private Date fechaFinMovimiento;
	private Long tipoDocumento;
	private List<DocumentoDetalle> documentosMovimiento;
	
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}
	
	public void buscarMovimiento() {
		setDocumentosMovimiento(documentoDetalleService.getByTipoAndFecha(getTipoDocumento(),
				Calculos.fechaInicial(getFechaIniMovimiento()), Calculos.fechaInicial(getFechaFinMovimiento())));
	}
	
	public void buscarCardex() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (getProductoId() == 0 ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, EL producto es Obligatorio",""));
            return;        
        }
		if (getFechaIni() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de inicio es obligatoria",""));
           return;         
        }
    	if (getFechaFin() == null ) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!, La fecha de fin es obligatoria",""));
            return;         
        }
    	ProductoEmpresa productoEmpresa = productoEmpresaService.getById(getProductoId());
		setCardexList(documentoDetalleService.getCardex(getEmpresa(),productoEmpresa.getProductoId().getProductoId(),Calculos.fechaInicial(getFechaIni()),Calculos.fechaFinal(getFechaFin())));
		totalEntradas=0.0;
		totalSalidas=0.0;
		for(DocumentoDetalle d: getCardexList()) {
			
			if(d.getCantidad()!=null && d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId()==2) {
				 totalEntradas+=d.getCantidad();
			}
			if(d.getCantidad()!=null && d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId()==10 ||d.getDocumentoId().getTipoDocumentoId().getTipoDocumentoId()==6) {
				 totalSalidas+=d.getCantidad();
			}
		}
		setTotalEntradas(totalEntradas);
		setTotalSalidas(totalSalidas);
		setActual(productoEmpresa);
	}
	
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
		List<Usuario> usus = new ArrayList<>();
		usus.add(usuario);
		List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin,usus,conCierre);
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

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public List<ProductoEmpresa> getProductoList() {
		productoList = productoEmpresaService.getByEmpresa(getEmpresa().getEmpresaId());
		return productoList;
	}

	public void setProductoList(List<ProductoEmpresa> productoList) {
		this.productoList = productoList;
	}

	public List<DocumentoDetalle> getCardexList() {
		return cardexList;
	}

	public void setCardexList(List<DocumentoDetalle> cardexList) {
		this.cardexList = cardexList;
	}

	public ProductoEmpresa getActual() {
		return actual;
	}

	public void setActual(ProductoEmpresa actual) {
		this.actual = actual;
	}

	public Double getTotalSalidas() {
		return totalSalidas;
	}

	public void setTotalSalidas(Double totalSalidas) {
		this.totalSalidas = totalSalidas;
	}

	public Double getTotalEntradas() {
		return totalEntradas;
	}

	public void setTotalEntradas(Double totalEntradas) {
		this.totalEntradas = totalEntradas;
	}

	public Date getFechaIniMovimiento() {
		return fechaIniMovimiento;
	}

	public void setFechaIniMovimiento(Date fechaIniMovimiento) {
		this.fechaIniMovimiento = fechaIniMovimiento;
	}

	public Date getFechaFinMovimiento() {
		return fechaFinMovimiento;
	}

	public void setFechaFinMovimiento(Date fechaFinMovimiento) {
		this.fechaFinMovimiento = fechaFinMovimiento;
	}

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<DocumentoDetalle> getDocumentosMovimiento() {
		return documentosMovimiento;
	}

	public void setDocumentosMovimiento(List<DocumentoDetalle> documentosMovimiento) {
		this.documentosMovimiento = documentosMovimiento;
	}
	
		
}
