package com.fact.beam;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fact.api.Calculos;
import com.fact.api.FactException;
import com.fact.api.Impresion;
import com.fact.model.Cliente;
import com.fact.model.Configuracion;
import com.fact.model.ConsecutivoDian;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empleado;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.Grupo;
import com.fact.model.InfoDiario;
import com.fact.model.Invoice;
import com.fact.model.OpcionUsuario;
import com.fact.model.Producto;
import com.fact.model.ProductoEmpresa;
import com.fact.model.Proporcion;
import com.fact.model.SubProducto;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoEvento;
import com.fact.model.TipoPago;
import com.fact.model.Usuario;
import com.fact.service.ClienteService;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.GrupoService;
import com.fact.service.OpcionUsuarioService;
import com.fact.service.ProductoEmpresaService;
import com.fact.service.ProductoService;
import com.fact.service.UsuarioService;
import com.fact.utils.Conector;
import com.fact.vo.DocumentoDetalleVo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author luismg
 *
 */
@ManagedBean
@SessionScoped
public class PuntoVentaDia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2973927052075665647L;
	private static Logger log = Logger.getLogger(PuntoVentaDia.class);

	private static final String LISTA_PRODUCTOS_INLINE = "document.getElementById('prodList1').style.display='inline';";
	private static final String LISTA_PRODUCTOS = "dataList1";
	private static final String CAMPO_CANTIDAD = "document.getElementById('cantidad_in1').select();";
	private static final String CAMPO_ARTICULO = "art_11";

	/**
	 * luis Miguel gonzalez
	 */

	@EJB
	private ProductoService productoService;

	@EJB
	private ProductoEmpresaService productoEmpresaService;

	@EJB
	private DocumentoService documentoService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;

	@EJB
	private OpcionUsuarioService opcionUsuarioService;

	@EJB
	private ClienteService clienteService;

	@EJB
	private UsuarioService usuarioService;

	@EJB
	private EventoService eventoService;

	@EJB
	private GrupoService grupoService;

	String codigoBarras;
	Map<String, Producto> productosAllCodigo;
	List<ProductoEmpresa> productosAll;
	List<Producto> productosAll2;
	List<Cliente> clientesAll;
	List<Empleado> empleadosAll;
	String codigoInterno;
	Producto articulo;
	Double unidad;
	Double cantidad;
	Double parcial;
	Documento documento;
	Producto productoSelect = new Producto();
	Producto productoSelect2 = new Producto();
	Conector conector = null;
	DocumentoDetalleVo DetalleSelect = new DocumentoDetalleVo();// variable que
																// controla el
																// producto del
																// c cambia
	Cliente clienteSelect = new Cliente();
	Empleado empleadoSelect = new Empleado();
	Long codigoCiente;
	Long codigoEmpleado;
	String tipoDocumentoFactura;
	String tipoDocumentoFacturaNombre;
	String nombreCliente2;
	String nombreEmpleado2;
	String identificacionCliente;
	Date fechaCreacion;
	Double pesoTotal;
	String displayTipoDocumento = "none";

	List<DocumentoDetalleVo> productos;
	List<DocumentoDetalleVo> productosBorrarList;
	Boolean productosBorrar = Boolean.FALSE;
	Cliente cliente;
	Empleado empleado;

	// creacion de cliente
	private Date fechaIngresoClienteNew;
	private String nombreClienteNew;
	private String documentoClienteNew;
	private String barrioClienteNew;
	private String direcionClienteNew;
	private String mail;
	private String celularClienteNew;
	private String fijoClienteNew;
	private Date cumpleanosClienteNew;
	private String creditoActivoClienteNew;
	private Long cupoCreditoClienteNew;
	private String retencionClienteNew;
	private String guiaTransporteClienteNew;
	private String crearNew;
	//

	Double excento;
	Double gravado;
	Double iva;
	Double total;
	String tarjeta;
	String cartera;
	Long efectivo;
	Double cambio;
	String impresion;
	Long valorTargeta;
	Double px01;
	String parciaPopup;
	Double descuento;
	String impresoras;

	// modificar factura
	String modFactura;
	Boolean actModFactura = Boolean.FALSE;
	String enPantalla = "";
	String pathFactura="";

	// saber si el codigo de barras esta activo
	public OpcionUsuario codBarrasActivo;
	// saber si copia de factura esta activa esta activo
	OpcionUsuario copiaFacuta;
	// saber si STOCK max y min esta activo
	OpcionUsuario stock;
	// saber si aparecen los campos de precio sugerido en el inventario f
	OpcionUsuario preciosSugeridos;
	// saber si la cartera de clientes esta activa
	OpcionUsuario carteraClientesActivo;
	// saber si esta activo la facturacion pora cotizaciones ,remisiones y
	// factura
	OpcionUsuario facturacionGuiaActivo;
	// saber si la cartera de clientes esta activa
	OpcionUsuario claveBorradoActivo;

	// saber si la cartera de clientes esta activa
	OpcionUsuario descuentosActivo;
	// saber si el cliente en la factura es obligatirio
	OpcionUsuario clienteObligatorio;

	// actvar el cambio de precio en los producto durante la facturacion
	OpcionUsuario cambioPrecio;

	// se activa si se aplica o no los descuentos en la factura impresa
	OpcionUsuario descuentoEnFactura;

	// se activa el bloqueo de cuadre de caja
	OpcionUsuario bloqCuadreCaja;

	// se activa la asignacion de los empleados a la factura
	OpcionUsuario asignarEmpleadoFactura;

	// se activa las comandas para los restaurantes
	OpcionUsuario activarComandas;

	// se activa las impresion en pantalla para qeu no gaste papel
	OpcionUsuario activarImpresionPantalla;

	// se activa las proporciones entre remisiones y facturas
	OpcionUsuario activarProporcion;

	// se activa impresion por varias impresoras en el sistem
	OpcionUsuario activarMultiplesImpresoras;
	
	// se activa impresion para sucursales y ip fija
	OpcionUsuario activarImpresionRemota;

	// factura siguiente y anterior
	List<Documento> listaDocumento;
	Documento documentoActual;

	// cambio de precio
	Double cambioTemp;// variable para almacednar el cambio dde precio temporal
	DocumentoDetalleVo dCambio;// variable que contiene el detalle del producto
								// que se le cambiara el precio

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Usuario usuario() {
		return (Usuario) sessionMap.get("userLogin");
	}

	private Configuracion configuracion() {
		return (Configuracion) sessionMap.get("configuracion");
	}

	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}

	private String impresora(String impresora) {
		return (String) sessionMap.get("impresora" + impresora);
	}

	private void productoFind(String completo) {
		Producto p = null;
		p = getProductosAllCodigo().get(completo);
		if (p != null) {
			log.info("entra al else");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').focus();");
			setCodigoInterno(p.getProductoId().toString());
			setArticulo(p);
			setUnidad(p.getCostoPublico());
			productoSelect = p;
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').select();");
			RequestContext.getCurrentInstance().update("art_11");
			RequestContext.getCurrentInstance().update("art_11_input");
			RequestContext.getCurrentInstance().update("cod_1_input");
			RequestContext.getCurrentInstance().update("cod_1");
			RequestContext.getCurrentInstance().update("unidad_1");
		}
	}

	public void buscarProductoCodBarras() {
		String completo = getCodigoBarras();
		String codigoProducoString = "";
		String codigoProducoUnidad = "";
		if (completo == null || completo.isEmpty()) {
			return;
		}
		Producto p = null;
		try {
			codigoProducoString = completo.substring(0, 7);
			codigoProducoUnidad = completo.substring(0, 6);
			boolean codigo2 = false;
			log.info("codigoP:" + codigoProducoString);
			log.info("codigoPunidad:" + codigoProducoUnidad);
			p = getProductosAllCodigo().get(codigoProducoString);
			if (p == null) {
				p = getProductosAllCodigo().get("2" + codigoProducoUnidad);
				completo = "2" + completo;
				codigo2 = true;
			}

			if (p != null) {
				log.info("producto carnes: " + p.getNombre());
				String pesoProducoString = completo.substring(7, 12);
				String parte1 = pesoProducoString.substring(0, 2);
				String parte2 = pesoProducoString.substring(2, 5);
				Double peso = Double.valueOf(parte1 + "." + parte2);
				log.info("Peso" + peso);
				if (codigo2) {
					peso = peso * 1000;
				}
				setCantidad(peso);
				productoSelect = p;
				cantidadEnter(null);
			} else {
				productoFind(getCodigoBarras());
			}
		} catch (Exception e) {
			try {
				productoFind(getCodigoBarras());
			} catch (Exception e2) {

			}
		}
	}

	public List<String> completeCodigo(String query) {
		List<String> codProductos = new ArrayList<>();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId() != null) {
				String articul = p.getProductoId().getProductoId().toString();
				// if (articul.indexOf(query) != -1) {
				if (articul.startsWith(query.toUpperCase().trim())) {
					codProductos.add(articul);
				}
			}
		}
		return codProductos;
	}

	public void buscarProductoCodigo(SelectEvent event) {
		String completo = event.getObject().toString();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().getProductoId().toString().contains(completo)) {
				setCodigoInterno(p.getProductoId().getProductoId().toString());
				setArticulo(p.getProductoId());
				setUnidad(p.getProductoId().getCostoPublico());
				productoSelect = p.getProductoId();
				RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
				RequestContext.getCurrentInstance().update("art_11_input");
				RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').focus();");
				RequestContext.getCurrentInstance().execute(CAMPO_CANTIDAD);
				break;
			}

		}
		event.getObject().toString();
	}

	public void buscarProducto(SelectEvent event) throws IOException {
		productoSelect = (Producto) event.getObject();
		if (productoSelect != null && (productoSelect.getProductoId() == 0l || productoSelect.getProductoId() == 1l
				|| productoSelect.getProductoId() == 2l)) {
			RequestContext.getCurrentInstance().execute("PF('px01').show();");
			setUnidad(0.0);
			RequestContext.getCurrentInstance().execute("document.getElementById('px01_input_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('px01_input_input').select();");
			RequestContext.getCurrentInstance().update("px01_input");
		} else {
			ProductoEmpresa productoEmpresaSelect = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(),
					productoSelect.getProductoId());
			if (productoSelect != null && productoSelect.getBalanza() == 1l) {
				RequestContext.getCurrentInstance().execute("pupupCantidad();");
				determinarBalanza();
				setParciaPopup("S");
			} else {
				setUnidad(productoEmpresaSelect.getPrecio() == null ? 0.0 : productoEmpresaSelect.getPrecio());
				Configuracion configuracion = configuracion();
				Long server = configuracion.getServer();
				if (server == 2l) {
					productoSelect2 = productoService.getById(productoSelect.getProductoId());
				}
				RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').focus();");
				RequestContext.getCurrentInstance().execute(CAMPO_CANTIDAD);
				RequestContext.getCurrentInstance().execute(
						"document.getElementById('cantidad_in1').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			}
		}
	}

	public List<Producto> completeText(String query) {
		List<Producto> nombProductos = new ArrayList<>();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().getNombre() != null) {
				String articul = p.getProductoId().getNombre().toUpperCase().trim();
				// si en algun momento se necesita
				if (articul.indexOf(query.toUpperCase()) != -1) {
					// if (articul.startsWith(query.toUpperCase().trim())) {
					Producto producto = p.getProductoId();
					producto.setCantidad(p.getCantidad());
					producto.setCostoPublico(p.getPrecio());
					nombProductos.add(producto);
				}
			}
		}
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if (server == 2 && !nombProductos.isEmpty()) {
			return cantidadproductoServer2(nombProductos, server);
		}
		return nombProductos;
	}

	/**
	 * Metodo que busca productos en el server 2 y actualiza la cantidad
	 * 
	 * @param nombProductos
	 * @return
	 */
	private List<Producto> cantidadproductoServer2(List<Producto> nombProductos, Long server) {
		try {
			List<Producto> nombProductos2 = productoService.getByList(nombProductos, server);
			List<Producto> nombProductos1 = productoService.getByList(nombProductos, 1L);
			for (Producto p : nombProductos1) {
				String producto1 = p.getProductoId().toString();
				for (Producto p2 : nombProductos2) {
					String producto2 = p2.getProductoId().toString();
					if (producto1.equals(producto2)) {
						Double cantidadNew = p.getCantidad() + p2.getCantidad();
						p.setCantidad(cantidadNew);
						break;
					}
				}
			}
		} catch (Exception e) {
			log.info("no found product server thow");
			return nombProductos;
		}
		return nombProductos;
	}

	public String determinarBalanza() {
		if (productoSelect.getProductoId() == 1l || productoSelect.getBalanza() == null
				|| productoSelect.getBalanza() != 1l) {
			return "";
		}
		String gramera = "" + configuracion().getGramera();
		if (conector == null) {
			conector = new Conector();
		}
		Double costoP;
		try {
			Double canti = Calculos.determinarBalanza(conector, gramera);

			if (Calculos.validarPromo(productoSelect, canti)) {
				costoP = productoSelect.getPubPromo();
			} else {
				costoP = productoSelect.getCostoPublico();
			}
			setCantidad(canti);
			setParcial(canti * costoP);
		} catch (Exception e) {
			setCantidad(0.0);
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error en el uso de la Gramera, por favor vuelva a pesar: " + e.getMessage()));
		}
		return "";
	}

	public String precioX01() {
		if (getPx01() != null) {
			Long server = 1l;
			log.info("dio enter en precio x01");
			if (getDocumento().getDocumentoId() == null) {
				Documento docOjb = new Documento();
				TipoDocumento td = new TipoDocumento();
				td.setTipoDocumentoId(10l);
				// se le envia tipo documento 10 que es igual a factura
				docOjb.setTipoDocumentoId(td);
				docOjb.setFechaRegistro(new Date());
				docOjb.setUsuarioId(usuario());
				// se deja factura sin enviar por defecto para facturacion electronica
				Invoice invoice = new Invoice();
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				if (clienteSelect == null) {
					clienteSelect = new Cliente();
					clienteSelect.setClienteId(1l); // se le envia cliente
													// varios por defecto
					clienteSelect.setGuiaTransporte(0l);
					clienteSelect.setNombre("Varios");
					docOjb.setClienteId(clienteSelect);
				}
				// se deja factura sin enviar por defecto para facturacion electronica
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				documentoService.save(docOjb, server);
				setDocumento(docOjb);
				log.info("Documento: " + getDocumento().getDocumentoId());
				log.info("Usuario: " + usuario().getLogin());
			}
			RequestContext.getCurrentInstance().execute("PF('px01').hide();");
			DocumentoDetalle docDetalle = new DocumentoDetalle();
			DocumentoDetalleVo docDetalleVo = new DocumentoDetalleVo();
			Producto pnew = new Producto();
			pnew.setCostoPublico(getPx01());
			pnew.setIva(0.0);
			pnew.setNombre(productoSelect.getNombre());
			pnew.setCodigoInterno(productoSelect.getProductoId().toString());
			docDetalle.setCantidad(1.0);
			docDetalle.setProductoId(productoSelect);
			docDetalle.setDocumentoId(getDocumento());
			docDetalle.setEstado(1l);
			docDetalle.setParcial(getPx01());
			Date fecha = new Date();
			docDetalle.setFechaRegistro(fecha);
			documentoDetalleService.save(docDetalle, server);
			docDetalleVo.setCantidad(1.0);
			docDetalleVo.setProductoId(pnew);
			docDetalleVo.setDocumentoId(getDocumento());
			docDetalleVo.setFechaRegistro(fecha);
			docDetalleVo.setUnitario(getPx01());
			docDetalleVo.setParcial(getPx01());
			getProductos().add(0, docDetalleVo);
			setDocumento(Calculos.calcularExcento(getDocumento(), getProductos())); // en
																					// esta
																					// funcion
																					// de
																					// calcula
																					// el
																					// excento,
																					// iva,
																					// total
			documentoService.update(getDocumento(), server);
			setExcento(getDocumento().getExcento());
			setGravado(getDocumento().getGravado());
			setIva(getDocumento().getIva());
			setTotal(getDocumento().getTotal());
			setPesoTotal(getDocumento().getPesoTotal());
			RequestContext.getCurrentInstance().execute(LISTA_PRODUCTOS_INLINE); // campos
			setArticulo(null);
			setPx01(null);
			setCodigoInterno(null);
			setCodigoBarras(null);
			RequestContext.getCurrentInstance().execute("document.getElementById('px01_input_input').value='';");

			RequestContext.getCurrentInstance().update(LISTA_PRODUCTOS);
			RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
			RequestContext.getCurrentInstance().update("cod_1");
			RequestContext.getCurrentInstance().update(LISTA_PRODUCTOS);
			RequestContext.getCurrentInstance().update("excento");
			RequestContext.getCurrentInstance().update("gravado");
			RequestContext.getCurrentInstance().update("iva");
			RequestContext.getCurrentInstance().update("total");
			if (codBarrasActivo != null) {
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('busquedaCodBarras1').style.display='inline';");
				RequestContext.getCurrentInstance().update("codBarras_input1");
				RequestContext.getCurrentInstance().update("busquedaCodBarras1");
			} else {
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').select();");
			}

		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('px01_input_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('px01_input_input').select();");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('px01_input_input').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
		return "";
	}

	public String cantidadBalanza(AjaxBehaviorEvent event) {
		if (getParciaPopup().equalsIgnoreCase("S")) {
			cantidadEnter(null);
		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('art_11_input').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
		RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("excento");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("iva");
		RequestContext.getCurrentInstance().update("total");
		return "";
	}

	/**
	 * Metodo que ejecuta las acciones de restar de inventario cuando se agrega la
	 * cantidad de un producto;
	 * 
	 * @param event
	 * @return
	 */
	public String cantidadEnter(AjaxBehaviorEvent event) {
		if (getCantidad() != null) {
			log.info("dio enter en cantidad");
			RequestContext.getCurrentInstance().execute(LISTA_PRODUCTOS_INLINE); // campos

			Configuracion configuracion = configuracion();
			if (codBarrasActivo != null) {
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('busquedaCodBarras1').style.display='inline';");
				RequestContext.getCurrentInstance().update("codBarras_input1");
				RequestContext.getCurrentInstance().update("busquedaCodBarras1");
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').select();");
			} else {
				RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
			}
			if (getCantidad() == 0 || getCantidad() < 0) {
				return "";
			}
			// se agrega un tope maximo de cantidad de 1500
			if (getCantidad() > 1500) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad maxima es de 1500"));
				return "";
			}
			// si stock esta activo se valida que la cantidad en existencia es
			// menor que en existencia
			// se envida una alerta
			ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(),
					productoSelect.getProductoId());
			if (stock != null && productoEmpresa.getCantidad() < productoEmpresa.getProductoId().getStockMin()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Tiene poca cantidad en inventario de " + productoSelect.getNombre()));

			}
			if (productoEmpresa.getCantidad() < 0) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(productoEmpresa.getProductoId().getNombre() + " se ha agotado "));
			}
			RequestContext.getCurrentInstance().update("growl1");
			Long server = configuracion.getServer();
			Boolean promo = Calculos.validarPromo(productoSelect, getCantidad());
			if (getDocumento().getTipoDocumentoId() != null && server == 2l) {
				// si es una remision
				if (getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 9l) {
					server = 2l;
				} else {
					server = 1l;
				}
			} else {
				server = 1l;
			}
			if (getDocumento().getDocumentoId() == null) {
				Documento docOjb = new Documento();
				TipoDocumento td = new TipoDocumento();
				td.setTipoDocumentoId(10l); // se le envia tipo documento 10 que
											// es igual a factura
				docOjb.setTipoDocumentoId(td);
				docOjb.setFechaRegistro(new Date());
				docOjb.setUsuarioId(usuario());
				if (clienteSelect == null) {
					clienteSelect = new Cliente();
					clienteSelect.setClienteId(1l); // se le envia cliente
													// varios por defecto
					clienteSelect.setGuiaTransporte(0l);
					clienteSelect.setNombre("Varios");
					docOjb.setClienteId(clienteSelect);
				}
				// se deja factura sin enviar por defecto para facturacion electronica
				Invoice invoice = new Invoice();
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				documentoService.save(docOjb, server);
				setDocumento(docOjb);
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
			}
			// se agrega la logica del c cambia
			DocumentoDetalle docDetalle = new DocumentoDetalle();
			DocumentoDetalleVo docDetalleVo = new DocumentoDetalleVo();
			if (DetalleSelect != null) {
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('borrarTabla:checkboxDT').style.display='none';");
				RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
				RequestContext.getCurrentInstance().execute("document.getElementById('confir').style.display='none';");
				actModFactura = Boolean.FALSE;
				documentoDetalleService.borrar(DetalleSelect.getDocumentoDetalleId().getDocumentoDetalleId(), 0l,
						server);
				docDetalle.setDocumentoDetalleId(DetalleSelect.getDocumentoDetalleId().getDocumentoDetalleId());
				docDetalleVo = DetalleSelect;
				getProductos().remove(DetalleSelect);
				DetalleSelect = null;
			}
			docDetalle.setCantidad(getCantidad());
			if (productoSelect != null) {
				docDetalle.setProductoId(productoSelect);
				docDetalle.setDocumentoId(getDocumento());
				Date fecha = new Date();
				docDetalle.setFechaRegistro(fecha);
				docDetalle.setEstado(1l);
				// TODO validar las promociones para sucursales, hay que agregarle el campo de
				// precio promocion
				if (promo) {
					Double precioPromo = productoSelect.getPubPromo();
					Double cantidadPromo = productoSelect.getkGPromo();
					Double unitarioPromo = precioPromo / cantidadPromo;
					docDetalle.setParcial(getCantidad() * unitarioPromo);
				} else {
					if (getCantidad() != null && productoEmpresa.getPrecio() != null) {
						docDetalle.setParcial(getCantidad() * productoEmpresa.getPrecio());
					} else {
						docDetalle.setParcial(0.0);
					}
				}
				// logica de cantidades del server2
				Double newCantidad2 = 0.0;
				if (productoSelect2 != null) {
					newCantidad2 = productoSelect2.getCantidad() == null ? 0.0 : productoSelect2.getCantidad();
				}

				Double newCantidad = productoEmpresa.getCantidad();
				if (server == 2) {
					if (newCantidad2 > 0) {
						if (newCantidad2 > getCantidad()) {
							docDetalle.setS1(0l);
							docDetalle.setS2(1l);
							docDetalle.setCantidad1(0.0);
							docDetalle.setCantidad2(getCantidad());
							docDetalleVo.setCantidad1(0.0);
							docDetalleVo.setCantidad2(getCantidad());
						} else {
							docDetalle.setS1(1l);
							docDetalle.setS2(1l);
							docDetalle.setCantidad2(newCantidad2);
							docDetalle.setCantidad1(getCantidad() - newCantidad2);
							docDetalleVo.setCantidad1(newCantidad2);
							docDetalleVo.setCantidad2(getCantidad() - newCantidad2);
						}
					} else {
						docDetalle.setS1(1l);
						docDetalle.setS2(0l);
						docDetalle.setCantidad2(0.0);
						docDetalle.setCantidad1(getCantidad());
						docDetalleVo.setCantidad1(getCantidad());
						docDetalleVo.setCantidad2(0.0);
					}
				} else {
					docDetalle.setS1(1l);
					docDetalle.setS2(0l);
					docDetalle.setCantidad2(0.0);
					docDetalle.setCantidad1(getCantidad());
					docDetalleVo.setCantidad1(getCantidad());
					docDetalleVo.setCantidad2(0.0);
				}
				ProductoEmpresa proCantidad = productoEmpresa;
				Producto proCantidad2 = productoSelect2;
				proCantidad.setCantidad(newCantidad - docDetalle.getCantidad1());
				restarCantidadesSubProducto(docDetalle, server);
				documentoDetalleService.save(docDetalle, server);
				productoEmpresaService.update(proCantidad);
				if (server == 2l && proCantidad2 != null) {
					proCantidad2.setCantidad(newCantidad2 - docDetalle.getCantidad2());
					productoService.update(proCantidad2, 2l);
				}
				docDetalleVo.setCantidad(getCantidad());
				docDetalleVo.setProductoId(productoSelect);
				docDetalleVo.setDocumentoId(getDocumento());
				docDetalleVo.setFechaRegistro(fecha);
				docDetalleVo.setDocumentoDetalleId(docDetalle);
				if (promo) {
					Double precioPromo = productoSelect.getPubPromo();
					Double cantidadPromo = productoSelect.getkGPromo();
					Double unitarioPromo = precioPromo / cantidadPromo;
					docDetalleVo.setUnitario(unitarioPromo);
					docDetalleVo.setParcial(getCantidad() * unitarioPromo);
				} else {
					if (getCantidad() != null && productoEmpresa.getPrecio() != null) {
						docDetalleVo.setParcial(getCantidad() * productoEmpresa.getPrecio());
						docDetalleVo.setUnitario(productoEmpresa.getPrecio());
					} else {
						docDetalleVo.setParcial(0.0);
					}
				}
				if (getCantidad() != 0l) {
					getProductos().add(0, docDetalleVo);
				}
			}
			setDocumento(Calculos.calcularExcento(getDocumento(), getProductos())); // en
																					// esta
																					// funcion
																					// de
																					// calcula
																					// el
																					// excento,
																					// iva,
																					// total

			documentoService.update(getDocumento(), server);
			setExcento(getDocumento().getExcento());
			setGravado(getDocumento().getGravado());
			setIva(getDocumento().getIva());
			setTotal(getDocumento().getTotal());
			setPesoTotal(getDocumento().getPesoTotal());
			setArticulo(null);
			setCantidad(0.0);
			setUnidad(null);
			setCodigoInterno(null);
			setCodigoBarras(null);
			setParciaPopup(null);
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').value='';");
			RequestContext.getCurrentInstance().update(LISTA_PRODUCTOS);
		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').focus();");
			RequestContext.getCurrentInstance().execute(CAMPO_CANTIDAD);
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('cantidad_in1').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
		return "";
	}

	/**
	 * Metodo que actualiza y resta las cantidades de inventario de los subproductos
	 * 
	 * @param productoSelect3
	 */
	private void restarCantidadesSubProducto(DocumentoDetalle productoSelect3, Long server) {
		List<SubProducto> subProductos = productoService
				.subProductoByProducto(productoSelect3.getProductoId().getProductoId());
		for (SubProducto s : subProductos) {
			Double cantidadAnterior = s.getProductoHijo().getCantidad();
			Double cantidadNueva = cantidadAnterior - (s.getCantidad() * productoSelect3.getCantidad());
			s.getProductoHijo().setCantidad(cantidadNueva);
			DocumentoDetalle docDetalle = new DocumentoDetalle();
			docDetalle.setCantidad1(s.getCantidad() * productoSelect3.getCantidad());
			docDetalle.setCantidad(s.getCantidad() * productoSelect3.getCantidad());
			docDetalle.setDocumentoId(getDocumento());
			docDetalle.setEstado(1l);
			docDetalle.setFechaRegistro(new Date());
			docDetalle.setProductoId(s.getProductoHijo());
			documentoDetalleService.save(docDetalle, server);
			productoService.update(s.getProductoHijo(), 1l);
		}
	}

	public String efectivoFactura(AjaxBehaviorEvent event) {
		log.info("efectivo:" + getEfectivo());
		Double vefectivo = getEfectivo() == null ? 0.0 : getEfectivo().doubleValue();
		getDocumento().setEfectivo(vefectivo);
		if (getTotal() == null) {
			setTotal(0.0);
		}
		Double vtotal = getTotal();
		Double vvalortarget = getValorTargeta() == null ? 0.0 : getValorTargeta().doubleValue();
		getDocumento().setValorTarjeta(vvalortarget);
		Double vcambio = vefectivo + vvalortarget - vtotal;
		setImpresion(null);
		if (vefectivo.equals(0.0)) {
			setCambio(0.0);
		} else {
			setCambio(vcambio);
		}
		getDocumento().setCambio(vcambio);
		return "";
	}

	public String valorTargeta(AjaxBehaviorEvent event) {
		log.info("valor targeta:" + getEfectivo());
		Double vtotal = getTotal().doubleValue();
		Double vvalortarget = getValorTargeta() == null ? 0.0 : getValorTargeta().doubleValue();
		setCambio(vtotal - vvalortarget);
		return "";
	}

	public String imprimirFactura() throws IOException, DocumentException, PrinterException, PrintException {
		if (getDocumento().getDocumentoId() == null) {
			return "";
		}
		Configuracion configuracion = configuracion();
		int numeroImpresiones = configuracion.getNumImpresion() == null ? 1 : configuracion.getNumImpresion();
		Long server = configuracion.getServer();
		enPantalla = (enPantalla == null ? "false" : enPantalla);
		enPantalla = (enPantalla.equals("") ? "false" : enPantalla);
		log.info("en pantalla:" + enPantalla);
		String impresora = impresora(getImpresoras() == null ? "1" : getImpresoras());
		boolean impresoraGrande = (getImpresoras() == null ? "1" : getImpresoras()).equals("2")?true:false;
		if (getImpresion() != null && getImpresion().equalsIgnoreCase("S")) {
			Empresa e = getEmpresa();
			String tituloFactura = "";

			
			
			// calcular tipo de documento
			calcularTipoDocumento(e, server);

			getDocumento().setImpreso(1l);
			getDocumento().setEntregado(0l);

			// se verifica si hay descuento para aplicar
			verificarDescuento();

			// se le envia cliente varios por defecto
			if (getDocumento().getClienteId() == null) {
				Cliente c = new Cliente();
				c.setClienteId(1l);
				c.setGuiaTransporte(0l);
				c.setNombre("Varios");
				c.setDireccion("");
				getDocumento().setClienteId(c);
			}

			// se asigna un tipo de pago
			numeroImpresiones = asignartipoPago(numeroImpresiones);
			// se busca la mac del equipo y se le asigna a la factura
			getDocumento().setMac(Calculos.conseguirMAC2());
			log.info("mac:" + getDocumento().getMac());
			documentoService.update(getDocumento(), server);
			// se manda a que se agregue el documento a la suma del informe
			// diario parcial
			calcularInfoDiario(e);
			String imp = e.getImpresion().toUpperCase();
			log.info("numero de impresiones: " + numeroImpresiones);
			// si la factura fue a credito se imprime dos veces
			for (int i = 0; i < numeroImpresiones; i++) {
				setProductos(Calculos.ordenar(getProductos()));
				switch (imp) {
				case "TXT":
					pathFactura=Impresion.imprimirTxt(getDocumento(), getProductos(), usuario(), configuracion, impresora,
							enPantalla, e);
					break;
				case "BIG":
					// quitar la dependencia del ireport
					pathFactura=imprimirTemporal(tituloFactura);
					break;
				case "PDF":
					pathFactura=Impresion.imprimirPDF(getDocumento(), getProductos(), usuario(), configuracion, impresora,
							enPantalla, e);
					log.info("sale de impresion pdf");
					break;
				case "PDF_PAGE":
					pathFactura=Impresion.imprimirPDFPage(getDocumento(), getProductos(), usuario(), configuracion, impresora,
							enPantalla, e);
					break;
				case "BIG_PDF":
					pathFactura=Impresion.imprimirBig(getDocumento(), getProductos(), usuario(), configuracion, descuentoEnFactura,
							impresora, e);
					break;
				case "SMALL_PDF":
					pathFactura=Impresion.imprimirPDFSmall(getDocumento(), getProductos(), usuario(), configuracion, impresora,enPantalla, e);
					break;
				default:
					Impresion.imprimirPDF(getDocumento(), getProductos(), usuario(), configuracion, impresora,
							enPantalla, e);
					break;

				}

			}		
			limpiar();
			//se verifica si se esta trabajando con una impresora remota
			if(activarImpresionRemota!=null) {
				log.info("impresion remota click");
				RequestContext.getCurrentInstance().execute("document.getElementById('remotoForm:imp_remoto').click();");
			}	
			RequestContext.getCurrentInstance().execute("PF('imprimir').hide();");
			RequestContext.getCurrentInstance().execute("document.getElementById('prod1').style.display='none';");
			RequestContext.getCurrentInstance().execute("document.getElementById('prodList1').style.display='none';");
			RequestContext.getCurrentInstance().execute("pagina='submenu';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('opciones:op_mov_mes1_content').style.display='inline';");
			RequestContext.getCurrentInstance().execute("document.getElementById('excento_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('gravado_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('iva_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('total_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('valorTargeta_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('efectivo_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('cambio_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('opciones:Sig_movi_mes1').focus();");

		}
		if (getImpresion() == null || getImpresion().equalsIgnoreCase("N")) {
			limpiar();
			RequestContext.getCurrentInstance().execute("PF('imprimir').hide();");
			RequestContext.getCurrentInstance().execute("document.getElementById('prod1').style.display='none';");
			RequestContext.getCurrentInstance().execute("document.getElementById('prodList1').style.display='none';");
			RequestContext.getCurrentInstance().execute("pagina='submenu';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('opciones:op_mov_mes1_content').style.display='inline';");
			// RequestContext.getCurrentInstance().execute("opciones:Imp_movi_mes1");
			RequestContext.getCurrentInstance().execute("document.getElementById('excento_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('gravado_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('iva_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('total_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('valorTargeta_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('efectivo_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('cambio_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('opciones:Sig_movi_mes1').focus();");
		}
		enPantalla = "";
		return "";
	}

	public StreamedContent getFacturaRemota()
			throws DocumentException, IOException, PrinterException, ParseException {
		StreamedContent file = null;
		File f = new File(pathFactura);
		InputStream stream = new FileInputStream(f);
		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/pdf", pathFactura);
		}
		return file;
	}
	

	private void verificarDescuento() {
		Double des1 = getDescuento() == null ? 0.0 : getDescuento();
		if (des1 != 0.0) {
			Double desTemp = 0.0;
			getAplicarDescuento();
			if (getDescuento() < -100.0 || getDescuento() > 100.0) {
				getDocumento().setDescuento(getDescuento());
				desTemp = (getDescuento() * 100) / getDocumento().getTotal();
				log.info("% descuento:" + desTemp);
			} else {
				getDocumento().setDescuento((getDocumento().getTotal() * getDescuento()) / 100);
				desTemp = getDescuento();
				log.info("% descuento:" + desTemp);
			}
			if (desTemp < -15 || desTemp > 15) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("El descuento no puede ser mayor o menor al 15%"));
				return;
			}
		}

	}

	private int asignartipoPago(int numeroImpresiones) {
		TipoPago tipa = new TipoPago();
		// si el documento es un vale se agrega sus tipos de pago
		if (getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 8l) {
			log.info("se asigna tipo de pago al vale");
			tipa.setTipoPagoId(6l);
			getDocumento().setTipoPagoId(tipa);
		} else {
			if (getCartera() != null && getCartera().equalsIgnoreCase("S")) {
				tipa.setTipoPagoId(2l);// pago a credito
				getDocumento().setTipoPagoId(tipa);
				numeroImpresiones = 2;
				// si la factura es acredito se hace un vale
				crearVale();
			} else {
				if (getTarjeta() != null && getTarjeta().equalsIgnoreCase("S")) {
					tipa.setTipoPagoId(5l);// pago con targeta
					getDocumento().setTipoPagoId(tipa);
				} else {
					tipa.setTipoPagoId(1l);// pago en efectivo
					getDocumento().setTipoPagoId(tipa);
				}
			}
		}

		return numeroImpresiones;
	}

	private void crearVale() {
		Documento docu = new Documento();
		TipoDocumento tido = new TipoDocumento();
		TipoPago pago = new TipoPago();
		Usuario usuario = usuario();
		long server = 1;
		docu.setFechaRegistro(new Date());

		docu.setClienteId(getDocumento().getClienteId());
		docu.setSaldo(getDocumento().getTotal());
		// se le envia tipo documento vale (Vale) por que zohan dijo
		tido.setTipoDocumentoId(8l);
		docu.setTipoDocumentoId(tido);
		// se envia tipo pago con vale, debido a que el vale es un documento a credito
		pago.setTipoPagoId(6l);
		docu.setTipoPagoId(pago);
		docu.setTotal(getDocumento().getTotal());
		docu.setUsuarioId(usuario);
		docu.setDetalleEntrada(getDocumento().getConsecutivoDian());
		documentoService.save(docu, server);
		log.info("se crea el vale por factura venta a cretido:" + docu.getDocumentoId());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vale Creado exitosamente"));
	}

	private void calcularTipoDocumento(Empresa e, Long server) {
		TipoDocumento tipo = new TipoDocumento();
		if (activarProporcion != null && (getDocumento().getTipoDocumentoId() == null
				|| getDocumento().getTipoDocumentoId().getTipoDocumentoId() == null
				|| getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 10l
				|| getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 9l)) {
			try {
				Proporcion proporcion = usuarioService.getProporcion();
				double numero = proporcion.getVariable().doubleValue() / proporcion.getBase().doubleValue();
				log.info("numero : " + numero);
				Long contaRemision = proporcion.getContadorRemision();
				Long contaFactura = proporcion.getContadorFactura();
				double numeroProporcion = contaRemision.doubleValue() / contaFactura.doubleValue();
				log.info("numero proporcion: " + numeroProporcion);
				Calendar fecha = Calendar.getInstance();
				int dia = fecha.get(Calendar.DAY_OF_MONTH);
				double rangoA;
				double rangoB;
				if (dia % 2 == 0) {
					rangoA = proporcion.getRangoParA();
					rangoB = proporcion.getRangoParB();
				} else {
					rangoA = proporcion.getRangoImparA();
					rangoB = proporcion.getRangoImparB();
				}

				if ((getDocumento().getTotal() >= rangoA && getDocumento().getTotal() <= rangoB)) {
					tipo.setTipoDocumentoId(9l);// se asigna factura
					getDocumento().setTipoDocumentoId(tipo);
				} else {
					if (numero < numeroProporcion || getDocumento().getClienteId().getClienteId() != 1) {
						tipo.setTipoDocumentoId(10l);// se asigna factura
						proporcion.setContadorFactura(contaFactura + 1);
						getDocumento().setTipoDocumentoId(tipo);
					} else {
						proporcion.setContadorRemision(contaRemision + 1);
						tipo.setTipoDocumentoId(9l);// se asigna remision
						getDocumento().setTipoDocumentoId(tipo);
					}
				}
				usuarioService.update(proporcion);
			} catch (Exception e2) {
				e2.printStackTrace();
				log.error("Error en la proporción");
			}
		}
		String tituloFactura = "";
		// este if controla el titulo de la factura para quia de remi si no tiene
		// activado proporcion
		ConsecutivoDian consecutivoDian;
		Long con;
		String consecutivo ;
		if (getDocumento().getTipoDocumentoId() == null) {
			if (getDocumento() != null && getDocumento().getClienteId().getGuiaTransporte() == 1l) {
				consecutivoDian = documentoService.getConsecutivoDian();
				if (consecutivoDian.getSecuencia() == 0l) {
					con = Long.valueOf(e.getAutorizacionDesde());
				} else {
					con = consecutivoDian.getSecuencia() + 1;
				}
				// dentro de try se valida si faltan 500 facturas para
				// llegar hasta el tope
				try {
					Long topeConsecutivo = Long.valueOf(e.getAutorizacionHasta());
					Long consegutivo = con;
					if (consegutivo + 500 > topeConsecutivo) {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(" se esta agotando el consegutivo DIAN "));
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}
				consecutivo = e.getLetraConsecutivo() + con;
				log.info("consecutivo Dian: " + consecutivo);
				getDocumento().setConsecutivoDian(consecutivo);
				tituloFactura = "FACTURA DE VENTA";
				getDocumento().setReduccion(0l);
				server = 1l;
				consecutivoDian.setSecuencia(con);
				documentoService.update(consecutivoDian);
			} else {
				consecutivoDian = documentoService.getConsecutivoDian();
				if (consecutivoDian.getSecuencia() == 0l) {
					con = Long.valueOf(e.getAutorizacionDesde());
				} else {
					con = consecutivoDian.getSecuencia() + 1;
				}
				// dentro de try se valida si faltan 500 facturas para
				// llegar hasta el tope
				try {
					Long topeConsecutivo = Long.valueOf(e.getAutorizacionHasta());
					Long consegutivo = con;
					if (consegutivo + 500 > topeConsecutivo) {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(" se esta agotando el consegutivo DIAN "));
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}
				consecutivo = e.getLetraConsecutivo() + con;
				log.info("consecutivo Dian: " + consecutivo);
				getDocumento().setConsecutivoDian(consecutivo);
				tituloFactura = "FACTURA DE VENTA";
				getDocumento().setReduccion(0l);
				server = 1l;
				consecutivoDian.setSecuencia(con);
				documentoService.update(consecutivoDian);
			}
		} else {
			if (getDocumento().getTipoDocumentoId().getTipoDocumentoId() == null) {
				TipoDocumento ti = new TipoDocumento();
				ti.setTipoDocumentoId(10l);
				getDocumento().setTipoDocumentoId(ti);
			}
			
			switch (getDocumento().getTipoDocumentoId().getTipoDocumentoId() == null ? "10"
					: getDocumento().getTipoDocumentoId().getTipoDocumentoId().toString()) {
			case "9":
				consecutivoDian = documentoService.getConsecutivoDian();
				con = consecutivoDian.getSecuencia();
				 consecutivo = e.getLetraConsecutivo() + con;
				getDocumento().setConsecutivoDian(consecutivo);
				log.info("consecutivo documentoId: " + consecutivo);
				tituloFactura = "FACTURA DE VENTA.";
				getDocumento().setReduccion(1l);
				break;
			case "10":
				consecutivoDian = documentoService.getConsecutivoDian();
				if (consecutivoDian.getSecuencia() == 0l) {
					con = Long.valueOf(e.getAutorizacionDesde());
				} else {
					con = consecutivoDian.getSecuencia() + 1;
				}
				// dentro de try se valida si faltan 500 facturas para
				// llegar hasta el tope
				try {
					Long topeConsecutivo = Long.valueOf(e.getAutorizacionHasta());
					Long consegutivo = con;
					if (consegutivo + 500 > topeConsecutivo) {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(" se esta agotando el consegutivo DIAN "));
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}
				consecutivo = e.getLetraConsecutivo() + con;
				log.info("consecutivo Dian: " + consecutivo);
				getDocumento().setConsecutivoDian(consecutivo);
				tituloFactura = "FACTURA DE VENTA";
				getDocumento().setReduccion(0l);
				server = 1l;
				consecutivoDian.setSecuencia(con);
				documentoService.update(consecutivoDian);
				break;
			case "4":
				getDocumento().setConsecutivoDian("" + getDocumento().getDocumentoId());// es necesario asignar el consecutivo dian
				log.info("consecutivo Cotizacion: " + getDocumento().getDocumentoId());
				tituloFactura = "No. DE COTIZACIÓN";
				server = 1l;
				break;
			default:
				break;
			}
		}

	}

	private void calcularInfoDiario(Empresa e) {
		Date fechaDocumento = getDocumento().getFechaRegistro();
		Date fechaInicio = Calculos.fechaInicial(fechaDocumento);
		Date fechaFinal = Calculos.fechaFinal(fechaDocumento);
		List<InfoDiario> infoList = documentoService.buscarInfodiarioByFecha(fechaInicio, fechaFinal);
		try {
			InfoDiario infoDiario = Calculos.calcularInfoDiario(getDocumento(), infoList, e);

			if (infoDiario.getInfoDiarioId() == null) {
				documentoService.save(infoDiario);
			} else {
				documentoService.update(infoDiario);
			}

		} catch (FactException e1) {
			log.error("Error calculando registro de informe diario" + e1.getMessage());
		}
	}

	// funcion encargada de aplicar el descuento si el valor getdescuento es
	// diferente de nulo
	private void getAplicarDescuento() {
		Double desTemp = 0.0;
		// si el descuento es mayor o menor que 100 entonces se calcula el
		// descuento en %
		desTemp = 0.0;
		if (getDescuento() < -100.0 || getDescuento() > 100.0) {
			getDocumento().setDescuento(getDescuento());
			desTemp = (getDescuento() * 100) / getDocumento().getTotal();
			log.info("% descuento:" + desTemp);
		} else {
			getDocumento().setDescuento((getDocumento().getTotal() * getDescuento()) / 100);
			desTemp = getDescuento();
			log.info("% descuento:" + desTemp);
		}
		if (desTemp < -15 || desTemp > 15) {
			return;
		}
		Double des = desTemp / 100;
		List<DocumentoDetalleVo> temp = new ArrayList<>();
		for (DocumentoDetalleVo d : getProductos()) {
			Double parcialDescuento = d.getParcial() + (d.getParcial() * des);
			Double unitarioDescuento = d.getUnitario() + (d.getUnitario() * des);
			d.setParcial(parcialDescuento);
			d.setUnitario(unitarioDescuento);
			temp.add(d);
		}
		Double totalTemp = getDocumento().getTotal();
		Double ivaTemp = getDocumento().getIva() + (getDocumento().getIva() * des);
		Double excentoTemp = getDocumento().getExcento() + (getDocumento().getExcento() * des);
		Double gravadoTemp = getDocumento().getGravado() + (getDocumento().getGravado() * des);
		getDocumento().setTotal(totalTemp);
		getDocumento().setSaldo(totalTemp);
		getDocumento().setIva(ivaTemp);
		getDocumento().setExcento(excentoTemp);
		getDocumento().setGravado(gravadoTemp);
		setProductos(temp);
		// se valida si el descuento es mayor o menor a 1.5
		if (desTemp >= 1.5 || desTemp <= -1.5) {
			Evento evento = new Evento();
			TipoEvento tipoEvento = new TipoEvento();
			tipoEvento.setTipoEventoId(2l); // se asigna tipo evento igual a
											// descuento mayor al 1.5
			evento.setFechaRegistro(new Date());
			evento.setTipoEventoId(tipoEvento);
			evento.setUsuarioId(usuario());
			evento.setCampo("" + getDocumento().getDocumentoId());
			evento.setValorActual("" + totalTemp);
			evento.setValorAnterior("" + getDescuento());
			eventoService.save(evento);
		}
	}

	private String imprimirTemporal(String tituloFactura) throws IOException {
		DecimalFormat formatea = new DecimalFormat("###,###.##");
		// log.info("entra a imprimir");
		Empresa e = Login.getEmpresaLogin();
		String pdf = "C:\\facturas\\factura_" + getDocumento().getDocumentoId() + ".txt";
		File archivo = new File(pdf);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyIni = df.format(getDocumento().getFechaRegistro());
		BufferedWriter bw;
		String dirCliente = "";
		try {
			dirCliente = getDocumento().getClienteId().getDireccion();
		} catch (Exception e2) {
			log.info("cliente sin direccion");
			dirCliente = "";
		}

		try {
			dirCliente = dirCliente.trim().substring(0, 20);
		} catch (Exception e7) {
			// dirCliente= "";

		}
		bw = new BufferedWriter(new FileWriter(archivo));
		bw.write("                                        \n");
		bw.write("                                        \n");
		bw.write("                                                                    "
				+ getDocumento().getConsecutivoDian() + "\n");
		bw.write("                                        \n");
		bw.write("      " + Calculos.cortarDescripcion(getDocumento().getClienteId().getNombre().toUpperCase(), 22)
				+ " " + Calculos.cortarDescripcion(dirCliente, 12) + "  "
				+ Calculos.cortarDescripcion(e.getResolucionDian(), 12) + " " + Calculos.cortarDescripcion(fhoyIni, 19)
				+ "\n");
		bw.write("      " + getDocumento().getClienteId().getDocumento() + "                            "
				+ e.getAutorizacionDesde() + "  " + e.getAutorizacionHasta() + e.getFechaResolucion() + "\n");
		bw.write("      " + getDocumento().getUsuarioId().getUsuarioId() + " "
				+ getDocumento().getUsuarioId().getNombre().toUpperCase() + " "
				+ getDocumento().getUsuarioId().getApellido().toUpperCase());
		bw.write("\n");
		bw.write("\n");
		int tope = 16;
		int pagina = 0;
		int numProductos = tope;
		setProductos(Calculos.ordenar(getProductos()));
		for (DocumentoDetalleVo ddV : getProductos()) {
			String nombreProducto = "";
			String cantidadProducto = "";
			String unidadProducto = "";
			Double total = 0.0;
			String iva = "";
			String totalProducto = "";
			Double des = getDescuento() == null ? 0.0 : getDescuento();
			des = des / 100;
			Double ivaTem = ddV.getProductoId().getIva() == null ? 0.0 : ddV.getProductoId().getIva();

			nombreProducto = Calculos.cortarDescripcion(ddV.getProductoId().getNombre(), 34);
			cantidadProducto = Calculos.cortarCantidades(ddV.getCantidad(), 6);
			Double uniProductoTemp = ddV.getProductoId().getCostoPublico()
					+ (ddV.getProductoId().getCostoPublico() * des);
			unidadProducto = Calculos.cortarCantidades(formatea.format(uniProductoTemp), 13);
			String totalTep = "";
			total = (uniProductoTemp * ddV.getCantidad());
			try {
				totalTep = formatea.format(total);
			} catch (Exception e2) {
				log.info("error tratando de dar formato a valor parcial");
			}
			totalProducto = Calculos.cortarCantidades(totalTep, 12);
			iva = Calculos.cortarCantidades(ivaTem, 2);
			if (numProductos > 0) {
				bw.write("\n");
				String varios = ddV.getProductoId().getVarios() == 1l ? "V" : " ";
				bw.write(" (" + varios + ") " + cantidadProducto + "  " + nombreProducto + " " + unidadProducto + "  "
						+ totalProducto + " " + iva);
			} else {
				pagina++;
				String totalpuntos = Calculos.cortarCantidades(formatea.format(totalXpagina(pagina, tope)), 13);
				bw.write("\n        " + Calculos.cortarCantidades(formatea.format(getDocumento().getExcento()), 13)
						+ "         " + Calculos.cortarCantidades(formatea.format(getDocumento().getGravado()), 11)
						+ "       " + Calculos.cortarCantidades(formatea.format(getDocumento().getIva()), 10) + "    "
						+ totalpuntos);
				bw.write("\n");
				bw.write(" \n      " + formatea.format(getDocumento().getPesoTotal()) + " Kgs");
				bw.write("\nCONTINUA....\n ");
				bw.write("\n ");
				bw.write("\n ");
				bw.write("\n ");
				bw.write("\n .");
				bw.write("                                        \n");
				bw.write("                                        \n");
				bw.write("                                                                    "
						+ getDocumento().getConsecutivoDian() + "\n");
				bw.write("                                        \n");
				bw.write("      "
						+ Calculos.cortarDescripcion(getDocumento().getClienteId().getNombre().toUpperCase(), 22) + " "
						+ Calculos.cortarDescripcion(dirCliente, 12) + "  "
						+ Calculos.cortarDescripcion(e.getResolucionDian(), 12) + " "
						+ Calculos.cortarDescripcion(fhoyIni, 19) + "\n");
				bw.write("      " + getDocumento().getClienteId().getDocumento() + "                            "
						+ e.getAutorizacionDesde() + "  " + e.getAutorizacionHasta() + e.getFechaResolucion() + "\n");
				bw.write("      " + getDocumento().getUsuarioId().getUsuarioId() + " "
						+ getDocumento().getUsuarioId().getNombre().toUpperCase() + " "
						+ getDocumento().getUsuarioId().getApellido().toUpperCase());
				bw.write("\n");
				bw.write("\n");
				bw.write("\n");
				String varios = ddV.getProductoId().getVarios() == 1l ? "V" : " ";
				bw.write(" (" + varios + ") " + cantidadProducto + "  " + nombreProducto + " " + unidadProducto + "  "
						+ totalProducto + " " + iva);
				numProductos = tope;
			}

			numProductos--;
		}
		for (int i = 1; i < numProductos; i++) {
			bw.write("\n");
		}

		String totalpuntos = Calculos.cortarCantidades(formatea.format(getDocumento().getTotal()), 13);
		bw.write("\n\n        " + Calculos.cortarCantidades(formatea.format(getDocumento().getExcento()), 13)
				+ "         " + Calculos.cortarCantidades(formatea.format(getDocumento().getGravado()), 11) + "       "
				+ Calculos.cortarCantidades(formatea.format(getDocumento().getIva()), 10) + "   " + totalpuntos);
		bw.write("\n");
		bw.write(" \n      " + formatea.format(getDocumento().getPesoTotal()) + " Kgs");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n ");
		bw.write("\n .");
		bw.close();

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(pdf);
			log.info(pdf);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		if (inputStream == null) {
			// return;
		}
		DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc document = new SimpleDoc(inputStream, docFormat, null);
		PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
		PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

		String impresara = impresora(getImpresoras());
		log.info("impresoraUsuario: " + impresara);
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		log.info("Number of printers configured: " + printServices.length);
		for (PrintService printer : printServices) {
			log.info("Printer:" + printer.getName());
			if (printer.getName().equals(impresara)) {
				log.info("Comparacion:" + printer.getName() + ":" + impresara);
				defaultPrintService = printer;
			}
		}
		if (defaultPrintService != null) {
			DocPrintJob printJob = defaultPrintService.createPrintJob();
			try {
				printJob.print(document, attributeSet);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			log.error("No existen impresoras instaladas");
		}
		return pdf;
	}

	private Double totalXpagina(int pagina, int tope) {
		Double totalPagina = 0.0;
		for (int i = tope * (pagina - 1); i < tope * (pagina - 1) + tope; i++) {
			totalPagina = getProductos().get(i).getParcial() + totalPagina;
		}
		return totalPagina;
	}

	public void preAccion(String op) {
		limpiar();
		log.info("entra: " + op);
		Usuario usuario = usuario();
		List<String> rutas = new ArrayList<>();
		Map<String, OpcionUsuario> opcionesActivas = new HashMap<>();
		;
		List<OpcionUsuario> ou = new ArrayList<>();
		if (op.equals("cierre_diario")) {
			// RequestContext.getCurrentInstance().execute("PF('cierre_diario').hide();");
			RequestContext.getCurrentInstance().execute("PF('confirmarCierreDiario').show();");
			RequestContext.getCurrentInstance().execute("document.getElementById('salirCierre1').focus();");
		}
		if (op.equals("usuarios_sistema")) {

		}
		if (op.equals("inventario_fisico")) {
			rutas.add("PRECIOS_SUGERIDOS");
			ou = opcionUsuarioService.getByRutas(rutas, usuario.getUsuarioId());
			opcionesActivas = agregarRutas(ou);
			activarPreciosSugerido(opcionesActivas);
		}
		if (op.equals("punto_venta")) {
			rutas.add("FACTURACION_GUIA");
			rutas.add("CARTERA_CLIENTES");
			rutas.add("DESCUENTOS");
			rutas.add("CLAVE_BORRADO");
			rutas.add("CODIGO_BARRAS");
			rutas.add("COPIA_FACTURA");
			rutas.add("STOCK");
			rutas.add("CLIENTE_OBLIAGATORIO");
			rutas.add("CAMBIO_PRECIO");
			rutas.add("DESCUENTO_ENFACTURA");
			rutas.add("BLOQUEO_CUADRE_CAJA");
			rutas.add("ASIGNAR_EMPLEADO_FACTURA");
			rutas.add("ACTIVAR_COMANDAS");
			rutas.add("ACTIVAR_PROPORCION");
			rutas.add("ACTIVAR_MULTIPLE_IMPRESORA");
			rutas.add("IMPRESION_REMOTA");
			ou = opcionUsuarioService.getByRutas(rutas, usuario.getUsuarioId());
			opcionesActivas = agregarRutas(ou);
			activarMultipesImpresoras(opcionesActivas);
			activarImpresionRemota(opcionesActivas);
			activarCodigoBarras(usuario, opcionesActivas);
			activarCarteraCliente(usuario, opcionesActivas);
			activarGuiFacturacion(usuario, opcionesActivas);
			activarClaveBorrado(usuario, opcionesActivas);
			activardescuentos(usuario, opcionesActivas);
			activarCopiaFactura(usuario, opcionesActivas);
			activarStock(opcionesActivas);
			activarClienteObliatorio(usuario, opcionesActivas);
			activarCambioPrecio(usuario, opcionesActivas);
			activarDescuentoEnFactura(usuario, opcionesActivas);
			activarbloqCuadreCaja(usuario, opcionesActivas);
			activarAsignacionEmpleadoFactura(usuario, opcionesActivas);
			activarComandas(usuario, opcionesActivas);
			activarImpresionPantalla(usuario, opcionesActivas);
			activarProporcion(opcionesActivas);
		}
		if (op.equals("movimiento_mes")) {
			rutas.add("CLAVE_BORRADO");
			rutas.add("STOCK");
			rutas.add("CAMBIO_PRECIO");
			ou = opcionUsuarioService.getByRutas(rutas, usuario.getUsuarioId());
			opcionesActivas = agregarRutas(ou);
			activarClaveBorrado(usuario, opcionesActivas);
			activarStock(opcionesActivas);
			activarCambioPrecio(usuario, opcionesActivas);
		}
	}

	private Map<String, OpcionUsuario> agregarRutas(List<OpcionUsuario> ou) {
		Map<String, OpcionUsuario> opcionesActivas = new HashMap<>();
		for (OpcionUsuario ruta : ou) {
			opcionesActivas.put(ruta.getRuta(), ruta);
		}
		return opcionesActivas;
	}

	public void activarGuiFacturacion(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		if (opcionesActivas.containsKey("FACTURACION_GUIA")) {
			RequestContext.getCurrentInstance().execute("guiaFacturacion=1;");
			facturacionGuiaActivo = opcionesActivas.get("FACTURACION_GUIA");
		} else {
			RequestContext.getCurrentInstance().execute("guiaFacturacion=0;");
		}
	}

	public void activarCarteraCliente(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "CARTERA_CLIENTES";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("carteraClientes=1;");
			carteraClientesActivo = opcionesActivas.get(ruta);
		} else {
			RequestContext.getCurrentInstance().execute("carteraClientes=0;");
		}
	}

	public void activardescuentos(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "DESCUENTOS";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("descuentos=1;");
			descuentosActivo = opcionesActivas.get(ruta);
		} else {
			RequestContext.getCurrentInstance().execute("descuentos=0;");
		}
	}

	public void activarClaveBorrado(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "CLAVE_BORRADO";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("claveBorrado=1;");
			claveBorradoActivo = opcionesActivas.get(ruta);
		} else {
			claveBorradoActivo = null;
			RequestContext.getCurrentInstance().execute("claveBorrado=0;");
		}
		sessionMap.put("claveBorradoActivo", claveBorradoActivo);
	}

	public void activarCodigoBarras(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "CODIGO_BARRAS";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("codigoBarras=1;");
			codBarrasActivo = opcionesActivas.get(ruta);
		} else {
			RequestContext.getCurrentInstance().execute("codigoBarras=0;");
		}
	}

	public void activarClienteObliatorio(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "CLIENTE_OBLIAGATORIO";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("clienteObligatorio=1;");
			clienteObligatorio = opcionesActivas.get(ruta);
		} else {
			RequestContext.getCurrentInstance().execute("clienteObligatorio=0;");
		}
	}

	public void activarCambioPrecio(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "CAMBIO_PRECIO";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("cambioPrecio=1;");
			cambioPrecio = opcionesActivas.get(ruta);
		} else {
			cambioPrecio = null;
			RequestContext.getCurrentInstance().execute("cambioPrecio=0;");
		}
		sessionMap.put("cambioPrecio", cambioPrecio);
	}

	public void activarCopiaFactura(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "COPIA_FACTURA";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("copiaFactura=1;");
			copiaFacuta = opcionesActivas.get(ruta);
		} else {
			copiaFacuta = null;
			RequestContext.getCurrentInstance().execute("copiaFactura=0;");
		}
		sessionMap.put("copiaFacuta", copiaFacuta);
	}

	public void activarbloqCuadreCaja(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "BLOQUEO_CUADRE_CAJA";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("bloqCuadreCaja=1;");
			bloqCuadreCaja = opcionesActivas.get(ruta);
		} else {
			bloqCuadreCaja = null;
			RequestContext.getCurrentInstance().execute("bloqCuadreCaja=0;");
		}
		sessionMap.put("bloqCuadreCaja", bloqCuadreCaja);
	}

	public void activarDescuentoEnFactura(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "DESCUENTO_ENFACTURA";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("descuentoEnFactura=1;");
			descuentoEnFactura = opcionesActivas.get(ruta);
		} else {
			descuentoEnFactura = null;
			RequestContext.getCurrentInstance().execute("descuentoEnFactura=0;");
		}
		sessionMap.put("descuentoEnFactura", descuentoEnFactura);
	}

	public void activarStock(Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "STOCK";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("stock=1;");
			stock = opcionesActivas.get(ruta);
		} else {
			stock = null;
			RequestContext.getCurrentInstance().execute("stock=0;");
		}
		sessionMap.put("stock", stock);
	}

	public void activarPreciosSugerido(Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "PRECIOS_SUGERIDOS";
		if (opcionesActivas.containsKey(ruta)) {
			RequestContext.getCurrentInstance().execute("preciosSugeridos=1;");
			preciosSugeridos = opcionesActivas.get(ruta);
		} else {
			preciosSugeridos = null;
			RequestContext.getCurrentInstance().execute("preciosSugeridos=0;");
		}
		// sessionMap.put("preciosSugeridos", preciosSugeridos);
	}

	public void activarAsignacionEmpleadoFactura(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "ASIGNAR_EMPLEADO_FACTURA";
		if (opcionesActivas.containsKey(ruta)) {
			log.info("tiene asignarEmpleadoFactura  activo");
			RequestContext.getCurrentInstance().execute("asignarEmpleadoFactura=1;");
			asignarEmpleadoFactura = opcionesActivas.get(ruta);
		} else {
			asignarEmpleadoFactura = null;
			RequestContext.getCurrentInstance().execute("asignarEmpleadoFactura=0;");
		}
	}

	public void activarComandas(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "ACTIVAR_COMANDAS";
		if (opcionesActivas.containsKey(ruta)) {
			log.info("tiene comanda  activo");
			RequestContext.getCurrentInstance().execute("activarComandas=1;");
			activarComandas = opcionesActivas.get(ruta);
		} else {
			activarComandas = null;
			RequestContext.getCurrentInstance().execute("activarComandas=0;");
		}
	}

	public void activarImpresionPantalla(Usuario usuario, Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "ACTIVAR_IMPRESION_PANTALLA";
		if (opcionesActivas.containsKey(ruta)) {
			log.info("tiene ImpresionPantalla  activo");
			RequestContext.getCurrentInstance().execute("activarImpresionPantalla=1;");
			activarImpresionPantalla = opcionesActivas.get(ruta);
		} else {
			activarImpresionPantalla = null;
			RequestContext.getCurrentInstance().execute("activarImpresionPantalla=0;");
		}
	}

	public void activarProporcion(Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "ACTIVAR_PROPORCION";
		if (opcionesActivas.containsKey(ruta)) {
			log.info("proporcion activo");
			activarProporcion = opcionesActivas.get(ruta);
		} else {
			activarProporcion = null;
		}
	}

	public void activarMultipesImpresoras(Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "ACTIVAR_MULTIPLE_IMPRESORA";
		if (opcionesActivas.containsKey(ruta)) {
			getActivarMultiplesImpresoras();
			log.info("tiene multiple impresora activo");
			RequestContext.getCurrentInstance().execute("activarMultiplesImpresoras=1;");
			activarMultiplesImpresoras = opcionesActivas.get(ruta);
		} else {
			activarMultiplesImpresoras = null;
			RequestContext.getCurrentInstance().execute("activarMultiplesImpresoras=0;");
		}
	}
	
	public void activarImpresionRemota(Map<String, OpcionUsuario> opcionesActivas) {
		String ruta = "IMPRESION_REMOTA";
		if (opcionesActivas.containsKey(ruta)) {
					
			activarImpresionRemota = opcionesActivas.get(ruta);
			log.info("tiene impresion remota: "+getActivarImpresionRemota());	
			RequestContext.getCurrentInstance().execute("activarImpresoraRemota=1;");
		} else {
			activarMultiplesImpresoras = null;
			RequestContext.getCurrentInstance().execute("activarImpresoraRemota=0;");
		}
	}

	public void limpiar() {
		log.info("limpiar");
		setProductos(null);
		setArticulo(null);
		setCantidad(null);
		setCodigoInterno(null);
		setCodigoBarras(null);
		setTipoDocumentoFactura(null);
		productoSelect = null;
		productoSelect2 = null;
		DetalleSelect = null;
		clienteSelect = null;
		setDisplayTipoDocumento("none");
		setDocumento(null);
		setExcento(null);
		setGravado(null);
		setIva(null);
		setTotal(null);
		setValorTargeta(null);
		setEfectivo(null);
		setImpresion(null);
		setProductosAll(null);
		setProductosAll2(null);
		setProductosBorrar(Boolean.FALSE);
		setDocumentoActual(null);
		setListaDocumento(null);
		setProductosBorrarList(null);
		setIdentificacionCliente(null);
		setPesoTotal(null);
		setTipoDocumentoFacturaNombre("");
		setNombreCliente2("");
		setCliente(null);
		setUnidad(null);
		setProductosAllCodigo(null);
		setClientesAll(null);
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('borrarTabla:checkboxDT').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('confir').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('nombreCliente_input').value='';");
		RequestContext.getCurrentInstance().update("cod_1");
		RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("cantidad_in1");
		RequestContext.getCurrentInstance().update(LISTA_PRODUCTOS);
		RequestContext.getCurrentInstance().update("excento");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("iva");
		RequestContext.getCurrentInstance().update("total");
		RequestContext.getCurrentInstance().update("unidad_1");
		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
		RequestContext.getCurrentInstance().update("idCliente");
		RequestContext.getCurrentInstance().update("pesoTotal");
		RequestContext.getCurrentInstance().update("nombreCliente2");
		actModFactura = Boolean.FALSE;
	}

	public void valoresDefectoImp() {
		popupImprimir();
	}

	public String getCodNom(String nombreP) {
		String cod = "";
		for (Producto p : getProductosAll2()) {
			if (p.getNombre() != null) {
				if (nombreP.equals(p.getNombre().toUpperCase())) {
					cod = p.getProductoId().toString();
					break;
				}
			}
		}
		return cod;
	}

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			List<Producto> newP = new ArrayList<>();
			newP = getProductosAll2();
			return newP.get(Integer.parseInt(value));
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Producto) object).getProductoId());
		} else {
			return null;
		}
	}

	public void popupImprimir() {
		if (actModFactura) {
			log.info("i en modificar factura");
			// todo lo de modificar, activar cosas y asi....

			RequestContext.getCurrentInstance()
					.execute("document.getElementById('busquedaCodBarras1').style.display='inline';");
			RequestContext.getCurrentInstance().execute("document.getElementById('prod1').style.display='inline';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('art_11_input').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').value='';");
			RequestContext.getCurrentInstance().execute("pagina='creando_factura';");
			RequestContext.getCurrentInstance().update("busquedaCodBarras1");
			RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
			RequestContext.getCurrentInstance().update("codBarras_input1");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
			RequestContext.getCurrentInstance().execute("pagina='borrando_factura';");
			RequestContext.getCurrentInstance().execute("ruta=='punto_venta';");
			RequestContext.getCurrentInstance().execute("document.getElementById('confir').style.display='none';");

			actModFactura = Boolean.FALSE;
		} else {
			if (activarComandas != null || activarImpresionPantalla != null) {
				RequestContext.getCurrentInstance().execute("PF('dialogComanda').show();");
			} else {
				abrirDialogImprimir();
			}
		}
	}

	public void imprimirComanda() throws FileNotFoundException, DocumentException {
		log.info("imprimir comanda");
		if (getDocumento().getDocumentoId() == null) {
			return;
		}
		Configuracion configuracion = configuracion();
		String impresora = impresora(getImpresoras());
		Empresa e = Login.getEmpresaLogin();
		String imp = e.getImpresion().toUpperCase();
		List<Long> gruposId = new ArrayList<>();
		for (DocumentoDetalleVo ddvo : getProductos()) {
			Boolean contieneGrupo = !gruposId.contains(ddvo.getProductoId().getGrupoId().getGrupoId());
			Integer productoImpreso = ddvo.getDocumentoDetalleId().getImpresoComanda() == null ? 0
					: ddvo.getDocumentoDetalleId().getImpresoComanda();
			if (contieneGrupo && productoImpreso == 0) {
				gruposId.add(ddvo.getProductoId().getGrupoId().getGrupoId());
			}
		}
		for (Long g : gruposId) {
			switch (imp) {
			case "TXT":
				log.info("no se imprime comandas en txt");
				break;
			case "BIG":
				log.info("no se imprime comandas en big");
				break;
			case "PDF":
				Grupo g2 = grupoService.getById(g);
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				Date hoy = new Date();
				String pdf = "C:\\facturas\\comandas\\comanda_" + getDocumento().getDocumentoId() + "_" + g2.getNombre()
						+ df.format(hoy).replace(":", "_") + ".pdf";
				FileOutputStream archivo = new FileOutputStream(pdf);
				Document documento = new Document();
				float fntSize, lineSpacing;
				fntSize = 11f;
				lineSpacing = 10f;
				PdfWriter.getInstance(documento, archivo);
				documento.setMargins(10, 1, 1, 1);
				documento.open();
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "-------------------------------------------------"))); // REPRESENTANTE
				documento.add(new Paragraph(new Phrase(lineSpacing, "Comanda para: " + g2.getNombre(),
						FontFactory.getFont(FontFactory.COURIER_BOLD, 14f))));
				documento.add(new Paragraph(new Phrase(lineSpacing, "Documento: " + getDocumento().getDocumentoId(),
						FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); //
				documento.add(
						new Paragraph(new Phrase(lineSpacing, "-------------------------------------------------"))); // REPRESENTANTE
				documento.add(new Paragraph(new Phrase(lineSpacing, "Producto               Cantidad     ",
						FontFactory.getFont(FontFactory.COURIER_BOLD, 14f)))); //
				for (DocumentoDetalleVo ddvo2 : getProductos()) {
					Integer productoImpreso = ddvo2.getDocumentoDetalleId().getImpresoComanda() == null ? 0
							: ddvo2.getDocumentoDetalleId().getImpresoComanda();
					if (g == ddvo2.getProductoId().getGrupoId().getGrupoId() && productoImpreso == 0) {
						// descripcion
						String nombre = "";
						int maxTamanoNombre = 24;
						nombre = Calculos.cortarDescripcion(ddvo2.getProductoId().getNombre(), maxTamanoNombre);

						// Cantidad
						String cant = "";
						int maxTamañoCant = 3;
						cant = Calculos.cortarCantidades(ddvo2.getCantidad(), maxTamañoCant);
						documento.add(new Paragraph(new Phrase(lineSpacing, nombre + " " + cant,
								FontFactory.getFont(FontFactory.COURIER_BOLD, fntSize)))); //
						DocumentoDetalle edict = ddvo2.getDocumentoDetalleId();
						edict.setImpresoComanda(1);
						documentoDetalleService.update(edict, 1l);
					}
				}
				documento.close();
				PrinterJob job = PrinterJob.getPrinterJob();
				PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
				log.info("Number of printers configured1: " + printServices.length);
				for (PrintService printer : printServices) {
					log.info("Printer: " + printer.getName());
					log.info("comparacion:" + impresora + ":" + printer.getName());
					if (printer.getName().equals(impresora)) {
						try {
							job.setPrintService(printer);
							log.info(impresora + " : " + printer.getName());
							break;
						} catch (PrinterException ex) {
							ex.printStackTrace();
						}
					}
				}
				PDDocument document = null;
				try {
					document = PDDocument.load(new File(pdf));
					job.setPageable(new PDFPageable(document));
					try {
						job.print();
					} catch (PrinterException er) {
						er.printStackTrace();
					}
					document.close();
				} catch (IOException ef) {
					ef.printStackTrace();
				}
				if (configuracion.getGuardarFacturas() == 0l) {
					File borrar = new File(pdf);
					if (!borrar.delete()) {
						log.error("Error borrando facturas");
					} else {
						log.info("Documento borrado");
					}
				}

				break;
			case "BIG_PDF":
				log.info("no se imprime comandas en big_pdf");

				break;
			case "SMALL_PDF":
				log.info("no se imprime comandas en small_pdf");

				break;
			default:
				break;

			}

		}
		RequestContext.getCurrentInstance().execute("PF('dialogComanda').hide();");
		RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
		RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
		RequestContext.getCurrentInstance().update("art_11");
	}

	public void imprimirPantalla() {
		log.info("impresion en pantalla");
		setImpresion("s");
		enPantalla = "true";
		RequestContext.getCurrentInstance().execute("PF('dialogComanda').hide();");
		abrirDialogImprimir();

	}

	public void abrirDialogImprimir() {
		log.info("imprimir");
		RequestContext.getCurrentInstance().execute("PF('dialogComanda').hide();");
		RequestContext.getCurrentInstance().execute("PF('imprimir').show();");
		RequestContext.getCurrentInstance().execute("document.getElementById('pogoTargeta').value='N';");
		if (descuentosActivo != null) {
			RequestContext.getCurrentInstance().execute("document.getElementById('descuento').value=0;");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('descuento').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			RequestContext.getCurrentInstance().execute("document.getElementById('descuento').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('descuento').select();");
		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').value='N';");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('cartera').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').select();");
			RequestContext.getCurrentInstance().update("cartera");
		}
		setCartera("N");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().update("excento_tag");
		RequestContext.getCurrentInstance().update("gravado_tag");
		RequestContext.getCurrentInstance().update("iva_tag");
		RequestContext.getCurrentInstance().update("total1");
		RequestContext.getCurrentInstance().update("iva_tag");
		RequestContext.getCurrentInstance().update("pogoTargeta");
		RequestContext.getCurrentInstance().update("descuento");
	}

	public void buscarUltimaFactura() {
		log.info("buscar ultima factura");
		Documento ultimoFactura;
		Usuario usuario = usuario();
		Long idFactura = 10l; // id de tipo documento factura
		List<DocumentoDetalle> dd = new ArrayList<>();
		ultimoFactura = documentoService.getByLastAndTipo(idFactura, usuario.getUsuarioId());
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if (ultimoFactura != null) {
			setDocumento(ultimoFactura);
			dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
			if (server == 2l) {
				dd.addAll(documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), server));
			}
			setDocumentoActual(ultimoFactura);
			detalles(dd);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas NO Impresas"));
		}
	}

	public void siguienteFactura() throws ParseException {
		log.info("siguiente_factura");
		List<DocumentoDetalle> dd = new ArrayList<>();
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				log.info("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				if (server == 2l) {
					dd.addAll(documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), server));
				}
				detalles(dd);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		} else {
			Integer pos = getListaDocumento().indexOf(getDocumentoActual());
			if (pos > 0) {
				setDocumentoActual(getListaDocumento().get(pos - 1));
				log.info("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				if (server == 2l) {
					dd.addAll(documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), server));
				}
				detalles(dd);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}

		}
	}

	public void anteriorFactura() throws ParseException {
		log.info("anterior_factura");
		List<DocumentoDetalle> dd = new ArrayList<>();
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				log.info("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				if (server == 2l) {
					dd.addAll(documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), server));
				}
				detalles(dd);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		} else {
			Integer pos = getListaDocumento().indexOf(getDocumentoActual());
			if (pos < getListaDocumento().size() - 1) {
				setDocumentoActual(getListaDocumento().get(pos + 1));
				log.info("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				if (server == 2l) {
					dd.addAll(documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), server));
				}
				detalles(dd);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		}
	}

	public List<DocumentoDetalleVo> detalles(List<DocumentoDetalle> dd) {
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		for (DocumentoDetalle d1 : dd) {
			DocumentoDetalleVo vo = new DocumentoDetalleVo();
			vo.setCantidad(d1.getCantidad());
			vo.setDocumentoDetalleId(d1);
			vo.setDocumentoId(d1.getDocumentoId());
			vo.setFechaRegistro(d1.getFechaRegistro());
			vo.setParcial(d1.getParcial());
			vo.setProductoId(d1.getProductoId());
			log.info("productos:" + d1.getProductoId().getNombre());
			if (Calculos.validarPromo(d1.getProductoId(), cantidad)) {
				Double precioPromo = d1.getProductoId().getPubPromo();
				Double cantidadPromo = d1.getProductoId().getkGPromo();
				Double unitarioPromo = precioPromo / cantidadPromo;
				vo.setUnitario(unitarioPromo);
				vo.setParcial(getCantidad() * unitarioPromo);
			} else {
				vo.setUnitario(vo.getParcial() / vo.getCantidad());
			}
			ddVo.add(vo);
		}
		setProductos(ddVo);
		setTotal(getDocumentoActual().getTotal());
		setExcento(getDocumentoActual().getExcento());
		setIva(getDocumentoActual().getIva());
		setGravado(getDocumentoActual().getGravado());
		setNombreCliente2(getDocumentoActual().getClienteId() == null ? "VARIOS"
				: getDocumentoActual().getClienteId().getNombre());
		// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('dataList1_content').style.display='inline';");
		RequestContext.getCurrentInstance().execute(LISTA_PRODUCTOS_INLINE);
		RequestContext.getCurrentInstance().update("cod_1");
		RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("cantidad_in1");
		RequestContext.getCurrentInstance().update(LISTA_PRODUCTOS);
		RequestContext.getCurrentInstance().update("excento");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("iva");
		RequestContext.getCurrentInstance().update("total");
		RequestContext.getCurrentInstance().update("unidad_1");
		RequestContext.getCurrentInstance().update("nombreCliente2");
		return ddVo;
	}

	public void activarBorrado() {
		log.info("presiona b para borrar factura");
		setProductosBorrar(Boolean.TRUE);
		setProductosBorrarList(getProductos());
		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT:0:row");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('dataList1_content').style.display='none';");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('borrarTabla:checkboxDT').style.display='inline';");
		// RequestContext.getCurrentInstance().execute("document.getElementById('checkboxDT:0:row_input').className='
		// ui-inputfield ui-inputtext ui-widget ui-state-default
		// ui-corner-all state-focus';");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('borrarTabla:checkboxDT:0:rowDelete_').focus();");
		// RequestContext.getCurrentInstance().execute("document.getElementById('checkboxDT:0:row').select();");

		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT:0:rowDelete_");
		RequestContext.getCurrentInstance().execute("document.getElementById('confir').style.display='none';");
		RequestContext.getCurrentInstance().execute("pagina='borrando_factura';");
		RequestContext.getCurrentInstance().execute("ruta=='punto_venta';");
		actModFactura = Boolean.FALSE;
	}

	public void verificarClaveBorrado() {
		log.info("verificando clave borrado" + getModFactura());
		List<Usuario> uList = new ArrayList<>();
		uList = usuarioService.getByRol(1l);// busca usuarios administradores
		Boolean correcto = Boolean.FALSE;
		;
		for (Usuario u : uList) {
			if (u.getClave().equals(getModFactura())) {
				correcto = Boolean.TRUE;
				break;
			}
		}
		if (correcto) {
			activarBorrado();
			RequestContext.getCurrentInstance().execute("PF('modificarFactura').hide();");
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Clave Erronea"));
		}

	}

	public void borrarFactura() {

		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			if (claveBorradoActivo != null) {
				setModFactura("");
				RequestContext.getCurrentInstance().execute("PF('modificarFactura').show();");
			} else {
				activarBorrado();
			}

		}
	}

	public void cCambia() {
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			log.info("presiona b para borrar factura");
			setProductosBorrar(Boolean.TRUE);
			setProductosBorrarList(getProductos());
			RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
			RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT:0:row");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('dataList1_content').style.display='none';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('borrarTabla:checkboxDT').style.display='inline';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('borrarTabla:checkboxDT:0:rowC_').focus();");
		}
	}

	public void recalcularPrecio(DocumentoDetalleVo d) {
		log.info("cambia de precio antes");
		dCambio = d;
	}

	public void recalcularPrecio() {
		log.info("cambio de precio:" + getCambioTemp());
		// se comenta el pedaso de codigo que hace que se pueda bajar el precio
		int pos = getProductos().indexOf(dCambio);
		Double descuentoTemp = getDocumento().getDescuento() == null ? 0.0 : getDocumento().getDescuento();
		if (dCambio.getUnitario() > getCambioTemp()) {
			descuentoTemp += (dCambio.getUnitario() - getCambioTemp());
		}
		getDocumento().setDescuento(descuentoTemp);
		Double cantidadtemp = dCambio.getCantidad();
		dCambio.setUnitario(getCambioTemp());
		dCambio.setParcial(cantidadtemp * getCambioTemp());
		getProductos().set(pos, dCambio);

		setDocumento(Calculos.calcularExcento(getDocumento(), getProductos()));
		Long tipo = getDocumento().getTipoDocumentoId().getTipoDocumentoId();
		Long server = configuracion().getServer();
		DocumentoDetalle d = documentoDetalleService.getById(dCambio.getDocumentoDetalleId().getDocumentoDetalleId());
		d.setParcial(cantidadtemp * getCambioTemp());
		if (tipo == 9l && server == 2) {

			documentoService.update(getDocumento(), server);
			documentoDetalleService.update(d, server);
		} else {
			documentoService.update(getDocumento(), 1l);
			documentoDetalleService.update(d, 1l);
		}
		setTotal(getDocumento().getTotal());
		setIva(getDocumento().getIva());
		setExcento(getDocumento().getExcento());
		setGravado(getDocumento().getGravado());
	}

	public void modificarUltimaFactura() {
		log.info("buscar ultima factura");
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			RequestContext.getCurrentInstance().execute("document.getElementById('confir').style.display='inline';");
			actModFactura = Boolean.TRUE;
		}
		// colocar una variable para saber si esta modificando factura,
	}

	public void cambiarDetalle(DocumentoDetalleVo d) {

		RequestContext.getCurrentInstance().execute("document.getElementById('prod1').style.display='inline';");
		RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').value='';");
		RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in1').focus();");
		RequestContext.getCurrentInstance().execute(CAMPO_CANTIDAD);
		RequestContext.getCurrentInstance().update("cantidad_in1");
		DetalleSelect = d;
		productoSelect = d.getProductoId();
	}

	public void borrarFacturaSelect(DocumentoDetalleVo d) {
		Long server = 1l;
		getProductos().remove(d);
		try {
			// en esta funcion de calcula el excento, iva, total
			setDocumento(Calculos.calcularExcento(d.getDocumentoId(), getProductos()));
			documentoService.update(getDocumento(), server);
			setExcento(getDocumento().getExcento());
			setGravado(getDocumento().getGravado());
			setIva(getDocumento().getIva());
			setTotal(getDocumento().getTotal());
			setPesoTotal(getDocumento().getPesoTotal());
			documentoDetalleService.borrar(d.getDocumentoDetalleId().getDocumentoDetalleId(), 0l, server);
			ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(),
					d.getProductoId().getProductoId());
			Double cantidad1 = productoEmpresa.getCantidad() + d.getCantidad();
			productoEmpresa.setCantidad(cantidad1);
			productoEmpresaService.update(productoEmpresa);

			if (server == 2l) {
				productoEmpresa = productoEmpresaService.getById(d.getProductoId().getProductoId());
				cantidad = productoEmpresa.getCantidad() + d.getCantidad2();
				productoEmpresa.setCantidad(cantidad);
				productoEmpresaService.update(productoEmpresa);
			}
		} catch (Exception e) {
			log.error("!!error borrando el producto:" + d.getProductoId().getProductoId());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error borrando Productos"));
		}
		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
		RequestContext.getCurrentInstance().update("excento");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("iva");
		if (!getProductos().isEmpty()) {
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('borrarTabla:checkboxDT:0:rowDelete_').focus();");
		}
		RequestContext.getCurrentInstance().update("borrarTabla:checkboxDT");
		log.info("documentoDetalle borrado:" + d.getDocumentoDetalleId());
	}

	public List<Cliente> completeTextCliente(String query) {
		List<Cliente> nombClientes = new ArrayList<>();
		for (Cliente c : getClientesAll()) {
			if (c.getNombre() != null) {
				String nombreC = c.getNombre().toUpperCase().trim();
				String documento = c.getDocumento() == null ? "" : c.getDocumento().trim();
				// if (articul.indexOf(query.toUpperCase()) != -1) {
				if (nombreC.startsWith(query.toUpperCase().trim())
						|| documento.startsWith(query.toUpperCase().trim())) {
					nombClientes.add(c);
				}
			}
		}
		return nombClientes;
	}

	public List<Empleado> completeTextEmpleado(String query) {
		List<Empleado> nombEmpleados = new ArrayList<>();
		for (Empleado e : getEmpleadosAll()) {
			if (e.getNombre() != null) {
				String nombreE = e.getNombre().toUpperCase().trim();
				// if (articul.indexOf(query.toUpperCase()) != -1) {
				if (nombreE.startsWith(query.toUpperCase().trim())) {
					nombEmpleados.add(e);
				}
			}
		}
		return nombEmpleados;
	}

	public List<Cliente> getClientesAll() {
		if (clientesAll == null || clientesAll.isEmpty()) {
			clientesAll = clienteService.getByAll();
		}
		return clientesAll;
	}

	public void setClientesAll(List<Cliente> clientesAll) {
		this.clientesAll = clientesAll;
	}

	public List<Empleado> getEmpleadosAll() {
		if (empleadosAll == null || empleadosAll.isEmpty()) {
			empleadosAll = usuarioService.getByEmpleadosAll();
		}
		return empleadosAll;
	}

	public void setEmpleadosAll(List<Empleado> empleadosAll) {
		this.empleadosAll = empleadosAll;
	}

	public void tipoFacturaSelect() {
		TipoDocumento td = new TipoDocumento();
		Invoice invoice = new Invoice();
		Long server = 1l;
		if (getTipoDocumentoFactura() != null && !getTipoDocumentoFactura().equals("")) {
			switch (getTipoDocumentoFactura().toUpperCase()) {
			case "F":
				log.info("tipo documento igual a factura de venta");
				// tipo documento igual a factura de venta
				td.setTipoDocumentoId(10l);
				setTipoDocumentoFacturaNombre("Factura de venta");
				server = 1l;
				// se deja factura sin enviar por defecto para facturacion electronica
				invoice.setInvoiceId(1l);
				break;
			case "R":
				log.info("tipo documento igual a factura de venta con remision");
				setTipoDocumentoFacturaNombre("Remisión");
				td.setTipoDocumentoId(9l); // tipo documento igual a factura
											// de// venta con remision
				server = configuracion().getServer();
				break;
			case "C":
				log.info("tipo documento igual a cotizacion");
				setTipoDocumentoFacturaNombre("Cotización");
				td.setTipoDocumentoId(4l); // tipo documento igual a cotizacion
				server = 1l;
				break;
			case "V":
				log.info("tipo documento igual a vale");
				setTipoDocumentoFacturaNombre("Vale");
				td.setTipoDocumentoId(8l); // tipo documento igual a vale
				server = 1l;
				break;
			case "":
			default:
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tipo documento no valido"));
				return;
			}
			RequestContext.getCurrentInstance().update("tipoNombre");
			if (getDocumento().getDocumentoId() == null) {
				Documento docOjb = new Documento();
				docOjb.setTipoDocumentoId(td);
				docOjb.setFechaRegistro(new Date());
				docOjb.setUsuarioId(usuario());
				docOjb.setInvoiceId(invoice);
				if (clienteSelect == null) {
					clienteSelect = new Cliente();
					clienteSelect.setClienteId(1l); // se le envia cliente
													// varios por defecto
					clienteSelect.setGuiaTransporte(0l);
					clienteSelect.setNombre("Varios");
					docOjb.setClienteId(clienteSelect);
				}
				// docOjb.setClienteId(clienteSelect);
				documentoService.save(docOjb, server);
				setDocumento(docOjb);
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
			} else {
				Documento docOjb = getDocumento();
				docOjb.setTipoDocumentoId(td);
				if (clienteSelect == null) {
					clienteSelect = new Cliente();
					clienteSelect.setClienteId(1l); // se le envia cliente
													// varios por defecto
					clienteSelect.setGuiaTransporte(0l);
					clienteSelect.setNombre("Varios");
					docOjb.setClienteId(clienteSelect);
				}
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
				if (server == 2l) {
					Long idBorrar = getDocumento().getDocumentoId();
					documentoService.save(docOjb, server);
					Long idCrear = docOjb.getDocumentoId();
					getDocumento().setDocumentoId(idBorrar);
					documentoService.delete(getDocumento());
					docOjb.setDocumentoId(idCrear);
					setDocumento(docOjb);
				} else {
					documentoService.update(docOjb, server);
				}

			}
			RequestContext.getCurrentInstance().update("tipoNombre");
		} else {
			log.info("tipo documento igual a factura de venta por defecto");
			setNombreCliente2("Factura de venta");
			Documento docOjb = new Documento();
			if (getDocumento().getDocumentoId() == null) {
				td.setTipoDocumentoId(10l); // se le envia tipo documento 10 que es igual a factura
				// se deja factura sin enviar por defecto para facturacion electronica
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				docOjb.setTipoDocumentoId(td);
				docOjb.setFechaRegistro(new Date());
				docOjb.setUsuarioId(usuario());
				documentoService.save(docOjb, server);
				setDocumento(docOjb);
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
			} else {
				td.setTipoDocumentoId(10l);
				// se le envia tipo documento 10 que es igual a factura
				getDocumento().setTipoDocumentoId(td);
				// se deja factura sin enviar por defecto para facturacion electronica
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				documentoService.update(getDocumento(), server);
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
			}
		}

		// // campos
		if (asignarEmpleadoFactura != null) {
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('nombreEmpleado').style.display='inline';");
			RequestContext.getCurrentInstance().execute("document.getElementById('nombreEmpleado_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('nombreEmpleado_input').select();");
			RequestContext.getCurrentInstance().update("nombreEmpleado");
		} else {
			if (codBarrasActivo != null) {
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('busquedaCodBarras1').style.display='inline';");
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input1').select();");
				RequestContext.getCurrentInstance().update("codBarras_input1");
				RequestContext.getCurrentInstance().update("busquedaCodBarras1");
			} else {
				RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('art_11_input').select();");
				RequestContext.getCurrentInstance().update(CAMPO_ARTICULO);
			}
		}
	}

	public void buscarCliente(SelectEvent event) {
		Invoice invoice = new Invoice();
		Long server = 1l;
		clienteSelect = (Cliente) event.getObject();
		setDisplayTipoDocumento("inline");
		log.info("Cliente select:" + clienteSelect.getNombre());
		setNombreCliente2(clienteSelect.getNombre());
		setCodigoCiente(clienteSelect.getClienteId());
		setIdentificacionCliente(clienteSelect.getDocumento());
		setFechaCreacion(new Date());
		if (getDocumento().getDocumentoId() == null) {
			Documento docOjb = new Documento();
			TipoDocumento td = new TipoDocumento();
			td.setTipoDocumentoId(10l); // se le envia tipo documento 10 que es igual a factura
			docOjb.setTipoDocumentoId(td);
			docOjb.setFechaRegistro(new Date());
			docOjb.setUsuarioId(usuario());
			docOjb.setClienteId(clienteSelect);
			// se deja factura sin enviar por defecto para facturacion electronica
			invoice.setInvoiceId(1l);
			docOjb.setInvoiceId(invoice);
			documentoService.save(docOjb, server);
			setDocumento(docOjb);
		} else {
			getDocumento().setClienteId(clienteSelect);
		}
	}

	public void buscarEmpleado(SelectEvent event) throws IOException {
		Long server = 1l;
		empleadoSelect = (Empleado) event.getObject();
		log.info("Empleado select:" + empleadoSelect.getNombre());
		setNombreEmpelado2(empleadoSelect.getNombre());
		setCodigoEmpleado(empleadoSelect.getEmpleadoId());
		if (getDocumento().getDocumentoId() == null) {
			Documento docOjb = new Documento();
			TipoDocumento td = new TipoDocumento();
			td.setTipoDocumentoId(10l); // se le envia tipo documento 10 que es igual a factura
			docOjb.setTipoDocumentoId(td);
			docOjb.setFechaRegistro(new Date());
			docOjb.setUsuarioId(usuario());
			docOjb.setEmpleadoId(empleadoSelect);
			// se deja factura sin enviar por defecto para facturacion electronica
			Invoice invoice = new Invoice();
			invoice.setInvoiceId(1l);
			docOjb.setInvoiceId(invoice);
			documentoService.save(docOjb, server);
			setDocumento(docOjb);
			log.info("Documento:" + getDocumento().getDocumentoId());
		} else {
			getDocumento().setEmpleadoId(empleadoSelect);
		}
	}

	public void crearNewCliente() {
		String crearN = getCrearNew().toUpperCase();

		if (crearN.equals("S")) {
			if (getDirecionClienteNew() == null || getDocumentoClienteNew().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El documento es obligatorio"));
				return;
			}

			List<Cliente> temp = clienteService.getByDocumento(getDocumentoClienteNew());
			if (!temp.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("El cliente con el documento " + getDocumentoClienteNew() + " ya existe"));
				return;
			}

			RequestContext.getCurrentInstance().execute("revisar3();");
			log.info("crear cliente desde punto de venta");
			RequestContext.getCurrentInstance().execute("PF('nuevoCliente').hide();");
			Long server = 1l;
			Cliente cNew = new Cliente();
			cNew.setBarrio(getBarrioClienteNew() == null ? "" : getBarrioClienteNew());
			cNew.setMail(getMail() == null ? "" : getMail());
			cNew.setCelular(getCelularClienteNew() == null ? "0" : getCelularClienteNew());
			if (getCreditoActivoClienteNew() != null) {
				cNew.setCreditoActivo(getCreditoActivoClienteNew().equalsIgnoreCase("S") ? 1l : 0l);
			} else {
				cNew.setCreditoActivo(0l);
			}

			cNew.setCupoCredito(getCupoCreditoClienteNew() == null ? 0l : getCupoCreditoClienteNew());
			cNew.setDireccion(getDirecionClienteNew() == null ? "" : getDirecionClienteNew());
			cNew.setDocumento(getDocumentoClienteNew() == null ? "" : getDocumentoClienteNew());
			cNew.setFechaRegistro(new Date());
			cNew.setFijo(getFijoClienteNew() == null ? "0" : getFijoClienteNew());
			if (getGuiaTransporteClienteNew() != null) {
				cNew.setGuiaTransporte(getGuiaTransporteClienteNew().equalsIgnoreCase("S") ? 1l : 0l);
			} else {
				cNew.setGuiaTransporte(0l);
			}
			cNew.setNombre(getNombreClienteNew() == null ? "" : getNombreClienteNew());
			setNombreCliente2(cNew.getNombre());
			if (getRetencionClienteNew() != null) {
				cNew.setRetencion(getRetencionClienteNew().equalsIgnoreCase("S") ? 1.0 : 0.0);
			} else {
				cNew.setRetencion(0.0);
			}
			clienteService.save(cNew);
			setCliente(cNew);
			getClientesAll().add(cNew);
			clienteSelect = cNew;
			if (getDocumento().getDocumentoId() == null) {
				Documento docOjb = new Documento();
				TipoDocumento td = new TipoDocumento();
				server = 1l;
				td.setTipoDocumentoId(10l); // se le envia tipo documento 10 que
											// es igual a factura
				docOjb.setTipoDocumentoId(td);
				docOjb.setFechaRegistro(new Date());
				docOjb.setUsuarioId(usuario());
				docOjb.setClienteId(clienteSelect);
				// se deja factura sin enviar por defecto para facturacion electronica
				Invoice invoice = new Invoice();
				invoice.setInvoiceId(1l);
				docOjb.setInvoiceId(invoice);
				documentoService.save(docOjb, server);

				setDocumento(docOjb);
				log.info("Documento:" + getDocumento().getDocumentoId());
				log.info("Usuario:" + usuario().getLogin());
			} else {
				Documento docOjb = getDocumento();
				if (getDocumento().getTipoDocumentoId() != null
						&& getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 9l) {
					server = 2l;
				}
				docOjb.setClienteId(clienteSelect);
				documentoService.update(docOjb, server);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente Creado exitosamente"));
		} else {
			RequestContext.getCurrentInstance().execute("PF('nuevoCliente').hide();");
			setNombreCliente2("VARIOS");
		}
		setCrearNew("N");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('deseaGuardarCliente').style.display='none';");
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public Producto getArticulo() {
		return articulo;
	}

	public void setArticulo(Producto articulo) {
		this.articulo = articulo;
	}

	public Double getUnidad() {
		return unidad;
	}

	public void setUnidad(Double unidad) {
		this.unidad = unidad;
	}

	public List<DocumentoDetalleVo> getProductos() {
		if (productos == null) {
			productos = new ArrayList<>();
		}
		return productos;
	}

	public void setProductos(List<DocumentoDetalleVo> productos) {
		this.productos = productos;
	}

	public Boolean getProductosBorrar() {
		return productosBorrar;
	}

	public String getDisplayTipoDocumento() {
		return displayTipoDocumento;
	}

	public void setDisplayTipoDocumento(String displayTipoDocumento) {
		this.displayTipoDocumento = displayTipoDocumento;
	}

	public void setProductosBorrar(Boolean productosBorrar) {
		this.productosBorrar = productosBorrar;
	}

	public Double getCantidad() {
		if (cantidad == null) {
			cantidad = 0.0;
		}
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Documento getDocumento() {
		if (documento == null) {
			documento = new Documento();
		}
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Double getParcial() {
		return parcial;
	}

	public void setParcial(Double parcial) {
		this.parcial = parcial;
	}

	public Double getExcento() {
		return excento;
	}

	public void setExcento(Double excento) {
		this.excento = excento;
	}

	public Double getGravado() {
		return gravado;
	}

	public void setGravado(Double gravado) {
		this.gravado = gravado;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getCartera() {
		return cartera;
	}

	public void setCartera(String cartera) {
		this.cartera = cartera;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public Long getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(Long efectivo) {
		this.efectivo = efectivo;
	}

	public Double getCambio() {
		return cambio;
	}

	public void setCambio(Double cambio) {
		this.cambio = cambio;
	}

	public String getImpresion() {

		return impresion;
	}

	public void setImpresion(String impresion) {
		this.impresion = impresion;
	}

	public Long getValorTargeta() {
		return valorTargeta;
	}

	public void setValorTargeta(Long valorTargeta) {
		this.valorTargeta = valorTargeta;
	}

	public Map<String, Producto> getProductosAllCodigo() {
		if (productosAllCodigo == null || productosAllCodigo.isEmpty()) {
			List<ProductoEmpresa> l = productoService.getProductoByEmpresa(getEmpresa().getEmpresaId());
			log.info(l.size());
			productosAllCodigo = new HashMap<>();
			for (ProductoEmpresa p : l) {
				if (p.getProductoId().getCodigoBarras() != null) {
					productosAllCodigo.put(p.getProductoId().getCodigoBarras(), p.getProductoId());
				}
			}
		}
		return productosAllCodigo;
	}

	public void setProductosAllCodigo(Map<String, Producto> productosAllCodigo) {
		this.productosAllCodigo = productosAllCodigo;
	}

	public List<ProductoEmpresa> getProductosAll() {
		if (productosAll == null || productosAll.isEmpty()) {
			productosAll = productoService.getProductoByEmpresa(getEmpresa().getEmpresaId());
		}
		return productosAll;
	}

	public void setProductosAll(List<ProductoEmpresa> productosAll) {
		this.productosAll = productosAll;
	}

	public List<Producto> getProductosAll2() {
		if (productosAll2 == null || productosAll2.isEmpty()) {
			productosAll2 = productoService.getAllByCompany((getEmpresa()));
		}
		return productosAll2;
	}

	public void setProductosAll2(List<Producto> productosAll2) {
		this.productosAll2 = productosAll2;
	}

	public Double getPx01() {
		return px01;
	}

	public void setPx01(Double px01) {
		this.px01 = px01;
	}

	public String getParciaPopup() {
		return parciaPopup;
	}

	public void setParciaPopup(String parciaPopup) {
		this.parciaPopup = parciaPopup;
	}

	public String getModFactura() {
		return modFactura;
	}

	public void setModFactura(String modFactura) {
		this.modFactura = modFactura;
	}

	public OpcionUsuario getCodBarrasActivo() {
		return codBarrasActivo;
	}

	public void setCodBarrasActivo(OpcionUsuario codBarrasActivo) {
		this.codBarrasActivo = codBarrasActivo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Long getCodigoCiente() {
		return codigoCiente;
	}

	public void setCodigoCiente(Long codigoCiente) {
		this.codigoCiente = codigoCiente;
	}

	public Long getCodigoEmpleado() {
		return codigoEmpleado;
	}

	public void setCodigoEmpleado(Long codigoEmpleado) {
		this.codigoEmpleado = codigoEmpleado;
	}

	public String getTipoDocumentoFactura() {
		return tipoDocumentoFactura;
	}

	public String getTipoDocumentoFacturaNombre() {
		return tipoDocumentoFacturaNombre;
	}

	public void setTipoDocumentoFacturaNombre(String tipoDocumentoFacturaNombre) {
		this.tipoDocumentoFacturaNombre = tipoDocumentoFacturaNombre;
	}

	public void setTipoDocumentoFactura(String tipoDocumentoFactura) {
		this.tipoDocumentoFactura = tipoDocumentoFactura;
	}

	public String getIdentificacionCliente() {
		return identificacionCliente;
	}

	public void setIdentificacionCliente(String identificacionCliente) {
		this.identificacionCliente = identificacionCliente;
	}

	public Date getFechaCreacion() {
		if (fechaCreacion == null) {
			fechaCreacion = new Date();
		}
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public List<Documento> getListaDocumento() {
		if (listaDocumento == null) {
			List<Long> tipoDocumentoId = new ArrayList<>();
			tipoDocumentoId.add(10l); // tipo documento factura de salida
			tipoDocumentoId.add(4l); // tipo documento cotizacion
			tipoDocumentoId.add(9l); // numero de guia
			// se agrega al anterior siguiente cotizaciones y remisiones por que
			// zohan dijo que lo tenia que hacer
			Usuario usuario = usuario();
			Configuracion configuracion = configuracion();
			Long server = configuracion.getServer();
			listaDocumento = documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId, 1l);
			if (server == 2l) {
				listaDocumento.addAll(documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId, server));
			}

		}
		return listaDocumento;
	}

	public void setListaDocumento(List<Documento> listaDocumento) {
		this.listaDocumento = listaDocumento;
	}

	public Documento getDocumentoActual() {
		return documentoActual;
	}

	public void setDocumentoActual(Documento documentoActual) {
		this.documentoActual = documentoActual;
	}

	public List<DocumentoDetalleVo> getProductosBorrarList() {
		if (productosBorrarList == null) {
			productosBorrarList = new ArrayList<>();
		}
		return productosBorrarList;
	}

	public void setProductosBorrarList(List<DocumentoDetalleVo> productosBorrarList) {
		this.productosBorrarList = productosBorrarList;
	}

	public Date getFechaIngresoClienteNew() {
		if (fechaIngresoClienteNew == null) {
			fechaIngresoClienteNew = new Date();
		}
		return fechaIngresoClienteNew;
	}

	public void setFechaIngresoClienteNew(Date fechaIngresoClienteNew) {
		this.fechaIngresoClienteNew = fechaIngresoClienteNew;
	}

	public String getNombreClienteNew() {
		return nombreClienteNew;
	}

	public void setNombreClienteNew(String nombreClienteNew) {
		this.nombreClienteNew = nombreClienteNew;
	}

	public String getDocumentoClienteNew() {
		return documentoClienteNew;
	}

	public void setDocumentoClienteNew(String documentoClienteNew) {
		this.documentoClienteNew = documentoClienteNew;
	}

	public String getBarrioClienteNew() {
		return barrioClienteNew;
	}

	public void setBarrioClienteNew(String barrioClienteNew) {
		this.barrioClienteNew = barrioClienteNew;
	}

	public String getDirecionClienteNew() {
		return direcionClienteNew;
	}

	public void setDirecionClienteNew(String direcionClienteNew) {
		this.direcionClienteNew = direcionClienteNew;
	}

	public String getCelularClienteNew() {
		return celularClienteNew;
	}

	public void setCelularClienteNew(String celularClienteNew) {
		this.celularClienteNew = celularClienteNew;
	}

	public String getFijoClienteNew() {
		return fijoClienteNew;
	}

	public void setFijoClienteNew(String fijoClienteNew) {
		this.fijoClienteNew = fijoClienteNew;
	}

	public Date getCumpleanosClienteNew() {
		return cumpleanosClienteNew;
	}

	public void setCumpleanosClienteNew(Date cumpleanosClienteNew) {
		this.cumpleanosClienteNew = cumpleanosClienteNew;
	}

	public String getCreditoActivoClienteNew() {
		return creditoActivoClienteNew;
	}

	public void setCreditoActivoClienteNew(String creditoActivoClienteNew) {
		this.creditoActivoClienteNew = creditoActivoClienteNew;
	}

	public Long getCupoCreditoClienteNew() {
		return cupoCreditoClienteNew;
	}

	public void setCupoCreditoClienteNew(Long cupoCreditoClienteNew) {
		this.cupoCreditoClienteNew = cupoCreditoClienteNew;
	}

	public String getRetencionClienteNew() {
		return retencionClienteNew;
	}

	public void setRetencionClienteNew(String retencionClienteNew) {
		this.retencionClienteNew = retencionClienteNew;
	}

	public String getGuiaTransporteClienteNew() {
		return guiaTransporteClienteNew;
	}

	public void setGuiaTransporteClienteNew(String guiaTransporteClienteNew) {
		this.guiaTransporteClienteNew = guiaTransporteClienteNew;
	}

	public String getCrearNew() {
		return crearNew;
	}

	public void setCrearNew(String crearNew) {
		this.crearNew = crearNew;
	}

	public String getNombreCliente2() {
		return nombreCliente2;
	}

	public void setNombreCliente2(String nombreCliente2) {
		this.nombreCliente2 = nombreCliente2;
	}

	public String getNombreEmpleado2() {
		return nombreEmpleado2;
	}

	public void setNombreEmpelado2(String nombreEmpleado2) {
		this.nombreEmpleado2 = nombreEmpleado2;
	}

	public Double getPesoTotal() {
		return pesoTotal;
	}

	public void setPesoTotal(Double pesoTotal) {
		this.pesoTotal = pesoTotal;
	}

	public String getDescuentosActivo() {
		return descuentosActivo == null ? "none" : "inline";
	}

	public void setDescuentosActivo(OpcionUsuario descuentosActivo) {
		this.descuentosActivo = descuentosActivo;
	}

	public String getActivarMultiplesImpresoras() {
		return activarMultiplesImpresoras == null ? "none" : "block";
	}

	public void setActivarMultiplesImpresoras(OpcionUsuario activarMultiplesImpresoras) {
		this.activarMultiplesImpresoras = activarMultiplesImpresoras;
	}

	public String getCopiaFacuta() {
		return copiaFacuta == null ? "none" : "inline";
	}

	public void setCopiaFacuta(OpcionUsuario copiaFacuta) {
		this.copiaFacuta = copiaFacuta;
	}

	public String getPreciosSugeridos() {
		return preciosSugeridos == null ? "none" : "inline";
	}

	public void setPreciosSugeridos(OpcionUsuario preciosSugeridos) {
		this.preciosSugeridos = preciosSugeridos;
	}

	public String getCambioPrecio() {
		return cambioPrecio == null ? "false" : "true";
	}

	public void setCambioPrecio(OpcionUsuario cambioPrecio) {
		this.cambioPrecio = cambioPrecio;
	}

	public String getBloqCuadreCaja() {
		return bloqCuadreCaja == null ? "false" : "true";
	}

	public void setBloqCuadreCaja(OpcionUsuario bloqCuadreCaja) {
		this.bloqCuadreCaja = bloqCuadreCaja;
	}

	public Double getCambioTemp() {
		return cambioTemp;
	}

	public void setCambioTemp(Double cambioTemp) {
		this.cambioTemp = cambioTemp;
	}

	public Producto getProductoSelect() {
		return productoSelect;
	}

	public void setProductoSelect(Producto productoSelect) {
		this.productoSelect = productoSelect;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getImpresoras() {
		return impresoras;
	}

	public void setImpresoras(String impresoras) {
		this.impresoras = impresoras;
	}

	public String getActivarImpresionRemota() {
		return activarImpresionRemota== null ? "false" : "true";
	}

	public void setActivarImpresionRemota(OpcionUsuario activarImpresionRemota) {
		this.activarImpresionRemota = activarImpresionRemota;
	}
	
}
