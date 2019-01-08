package com.fact.beam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import com.fact.model.Documento;
import com.fact.model.Empresa;
import com.fact.model.ProductoEmpresa;
import com.fact.model.Usuario;
import com.fact.model.invoice.DocumentoDetalle;
import com.fact.model.invoice.Producto;
import com.fact.model.invoice.Receptor;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.ProductoEmpresaService;
import com.fact.vo.DocumentoDeltaVo;
import com.google.gson.Gson;

@ManagedBean
@SessionScoped
public class Invoice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7074628949042545489L;
	private static Logger log = Logger.getLogger(Invoice.class);

	@EJB
	private DocumentoService documentoService;
	
	@EJB
	private DocumentoDetalleService documentoDetalleService;

	@EJB
	private ProductoEmpresaService productoEmpresaService;
	
	// busqueda
	private List<Documento> documentos;
	private List<Documento> documentosSelect;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Usuario usuario() {
		return (Usuario) sessionMap.get("userLogin");
	}
	
	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}
	
	

	public void buscarSinEnviar() {
		log.info("busqueda de facturas sin envio a la DIAN");
		// se envia invoice=1 que son documentos sin enviar a la DIAN
		setDocumentos(documentoService.buscarPorInvoice(1l));
	}

	public void descartar() {
		log.info("inicia Descarte de documentos para envios a la DIAN");
		if (getDocumentosSelect() == null || getDocumentosSelect().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay documentos seleccionados", ""));
			return;
		}
		for (Documento d : getDocumentosSelect()) {
			com.fact.model.Invoice invoice = new com.fact.model.Invoice();
			invoice.setInvoiceId(3l);
			d.setInvoiceId(invoice);
			documentoService.update(d, 1l);
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Se descartaron exitosamente los documentos para facturación electronica", ""));
		setDocumentosSelect(null);
		RequestContext.getCurrentInstance().execute("PF('dlg1').hide();");
	}

	public void enviarDocumentosDIAN() {
		log.info("inicia envio documentos a la dian");
		if (getDocumentosSelect() == null || getDocumentosSelect().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay documentos seleccionados", ""));
			return;
		}
		try {
			int contadorError = 0;
			int contadorEnvio = 0;
			for (Documento d : getDocumentosSelect()) {
				CredentialsProvider provider = new BasicCredentialsProvider();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("delta", "admin");
				provider.setCredentials(AuthScope.ANY, credentials);
				HttpPost post = new HttpPost("http://localhost:8060/empalmeDelta");
				DocumentoDeltaVo documentoInvoice= llenarDocumento(d);
				String json = new Gson().toJson(documentoInvoice);
				log.info("documento enviado: ");
				log.info(json);
				StringEntity input = new StringEntity(json);
				input.setContentType("application/json");
				HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
				post.setEntity(input);
				HttpResponse response = client.execute(post);
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";
				String conten = "";
				while ((line = rd.readLine()) != null) {
					log.info(line);
					conten += rd.readLine();
				}
				if (conten.contains("200")) {
					// se cambia el estado del documento a enviado
					com.fact.model.Invoice invoice = new com.fact.model.Invoice();
					invoice.setInvoiceId(2l);
					d.setInvoiceId(invoice);
					documentoService.update(d, 1l);
					contadorEnvio++;
				} else {
					contadorError++;
				}
			}
			if (contadorError > 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						contadorError + "tuvieron Errores en el envio.", ""));
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					contadorEnvio + " enviador para facturación electrónica", ""));
			setDocumentosSelect(null);
			log.info("fin envio documentos a la dian");

			RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Parece que no tiene conección a facturacion electrónica, contacte a su proveedor de Effective Software: 3185222474",
					""));
			e.printStackTrace();
		}

	}

	private DocumentoDeltaVo llenarDocumento(Documento d) {
		DocumentoDeltaVo invoice = new DocumentoDeltaVo();
		com.fact.model.invoice.Documento documentoInvoice = new com.fact.model.invoice.Documento();
		List<DocumentoDetalle> detalleInvoice = new ArrayList<>();	
		List<com.fact.model.DocumentoDetalle> detalles = documentoDetalleService.getByDocumento(d.getDocumentoId(), 1l);
		for(com.fact.model.DocumentoDetalle det: detalles) {
			Producto producto = new Producto();
			ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(), det.getProductoId().getProductoId());
			producto.setCosto(det.getProductoId().getCosto());
			producto.setCostoPublico(productoEmpresa.getPrecio());
			producto.setIva(det.getProductoId().getIva());
			producto.setNombre(det.getProductoId().getNombre());
			DocumentoDetalle detalleInvoiceTemp = new DocumentoDetalle();
			detalleInvoiceTemp.setCantidad(det.getCantidad());
			detalleInvoiceTemp.setCodigoImpuesto(determinarImpuesto(det));
			detalleInvoiceTemp.setFechaRegistro(det.getFechaRegistro());
			detalleInvoiceTemp.setParcial(det.getParcial());
			detalleInvoiceTemp.setProductoId(producto);
			detalleInvoice.add(detalleInvoiceTemp);
		}
		Receptor receptor = new Receptor();
		receptor.setApellidos("");
		receptor.setNombre(d.getClienteId().getNombre());
		receptor.setDireccion(d.getClienteId().getDireccion());
		receptor.setEmail(d.getClienteId().getMail());
		receptor.setIdentificacion(d.getClienteId().getDocumento());
		receptor.setRazonSocial(d.getClienteId().getNombre());
		//TODO hay que hacer las tablas de tipo documento y regimen
		//receptor.set
		documentoInvoice.setBase19(d.getBase19());
		documentoInvoice.setBase5(d.getBase5());
		documentoInvoice.setExento(d.getExcento());
		documentoInvoice.setFechaRegistro(d.getFechaRegistro());
		documentoInvoice.setGravado(d.getGravado());
		documentoInvoice.setIva(d.getIva());
		documentoInvoice.setIva19(d.getIva19());
		documentoInvoice.setIva5(d.getIva5());
		documentoInvoice.setNumeroDocumento(d.getConsecutivoDian());
		documentoInvoice.setPrefijo(getEmpresa().getLetraConsecutivo());
		documentoInvoice.setReceptorId(receptor);
		//se envia tipo documento factura por defacto mientras tanto
		documentoInvoice.setTipoDocumentoId(1l);
		documentoInvoice.setTotal(d.getTotal());
		invoice.setDocumentoDetalleId(detalleInvoice);
		invoice.setDocumentoId(documentoInvoice);
		return invoice;
	}

	private Double determinarImpuesto(com.fact.model.DocumentoDetalle det) {
		// TODO hay que hacer el metodo y las tablas si es necesario para determinar el codigo del impuesto
		return null;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public List<Documento> getDocumentosSelect() {
		return documentosSelect;
	}

	public void setDocumentosSelect(List<Documento> documentosSelect) {
		this.documentosSelect = documentosSelect;
	}

}
