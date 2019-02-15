package com.fact.beam;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.print.PrintException;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.fact.api.Calculos;
import com.fact.api.FactException;
import com.fact.api.Impresion;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.Grupo;
import com.fact.model.Marca;
import com.fact.model.OpcionUsuario;
import com.fact.model.Producto;
import com.fact.model.ProductoEmpresa;
import com.fact.model.Proveedor;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoEvento;
import com.fact.model.TipoPago;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.GrupoService;
import com.fact.service.MarcaService;
import com.fact.service.ProductoEmpresaService;
import com.fact.service.ProductoService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoDocumentoService;
import com.fact.service.UsuarioService;
import com.fact.utils.Conector;
import com.fact.vo.DocumentoDetalleVo;
import com.itextpdf.text.DocumentException;

@ManagedBean
@SessionScoped
public class MovimientoMes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2599894690114718736L;
	private static Logger log = Logger.getLogger(MovimientoMes.class);

	/**
	 * luis miguel gonzalez NiceSoft luismg8812@hotmail.com
	 */

	@EJB
	private TipoDocumentoService tipoDocumentoService;

	@EJB
	private DocumentoService documentoService;

	@EJB
	private ProductoService productoService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;

	@EJB
	private ProveedorService proveedorService;

	@EJB
	private GrupoService grupoService;

	@EJB
	private MarcaService marcaService;

	@EJB
	private UsuarioService usuarioService;

	@EJB
	private EventoService eventoService;
	
	@EJB
	private ProductoEmpresaService productoEmpresaService;

	public static final String FOCUS_CANTIDAD = "document.getElementById('cantidad_in').focus();";
	public static final String SELECT_CANTIDAD = "document.getElementById('cantidad_in').select();";
	public static final String MOSTRAR_LA_LISTA = "document.getElementById('prodList').style.display='inline';";
	public static final String ACTIVAR_CAMPO_COD_BARRAS = "document.getElementById('busquedaCodBarras').style.display='inline';";
	public static final String UPDATE_CAMPO_ARTICULO = "art_1";

	private List<TipoDocumento> tipoDocumentos;
	List<DocumentoDetalleVo> productos;
	List<ProductoEmpresa> productosAll;
	List<Proveedor> proveedoresAll;
	List<Grupo> gruposAll;
	List<Marca> marcasAll;
	List<DocumentoDetalleVo> productosBorrarList;
	Boolean productosBorrar = Boolean.FALSE;

	Producto productoSelect = new Producto();
	Proveedor proveedorSelect = new Proveedor();
	Documento documento;

	Long tipoDocumento = 2l;
	Long codigoProveedor;
	String tipoDocumentoEntrada;
	String identificacionProveedor;
	private String nombreProveedor;
	Date fechaCreacion;
	Proveedor proveedor;

	String codigoInterno;
	Producto articulo;
	String codigoBarras;
	Double cantidad;
	Double Unidad;
	Double parcial;
	String Detalle;

	String focus = "";
	String crear;
	String crearNew;
	

	// variables nuevo producto
	BigInteger codigoNew;
	String articuloNew = "";
	Double costoNew;
	Double publicoNew;
	Date fechaIngreso;
	Double ivaNew;
	Double hipoconsumoNew;
	Long stockMinNew;
	Long stockMaxNew;
	Grupo grupoNew;

	Marca marcaNew;
	Proveedor proveedorNew;
	String codigoBarrasNew;
	Double pesoKgNew;
	Double pesoEmpaqueKgNew;
	String unidadNew;
	String valanzaNew;
	String variosNew;
	String subProductoNew;
	//
	
	// cambio de precio
	String cambioTemp;// variable para almacednar el cambio dde precio temporal
	DocumentoDetalleVo dCambio;// variable que contiene el detalle del producto
								// que se le cambiara el precio
	
	private String buscar;

	private String parciaPopup = "";
	Conector conector = null;
	
	//imprimir
	private String cartera;
	private String impresion;

	public String getSubProductoNew() {
		return subProductoNew;
	}

	public void setSubProductoNew(String subProductoNew) {
		this.subProductoNew = subProductoNew;
	}
	
	public String getCambioTemp() {
		return cambioTemp;
	}

	public void setCambioTemp(String cambioTemp) {
		this.cambioTemp = cambioTemp;
	}

	// variables producto edit producto
	Producto productoEdict;
	BigDecimal codigoEdit;
	Date fechaEdit;
	String editarNew;

	// total
	private Double iva;
	private Double execento;
	private Double total;
	private Double gravado;
	private Double retefuente;

	// modificar factura
	String modFactura;
	Boolean actModFactura = Boolean.FALSE;

	// saber si el codigo de barras esta activo
	OpcionUsuario codBarrasActivo;
	// saber si la cartera de clientes esta activa
	OpcionUsuario carteraClientesActivo;
	// saber si esta activo la facturacion pora cotizaciones ,remisiones y
	// factura
	OpcionUsuario facturacionGuiaActivo;
	// saber si la cartera de clientes esta activa
	OpcionUsuario claveBorradoActivo;
	// actvar el cambio de precio en los producto durante la facturacion
	OpcionUsuario cambioPrecio;

	// factura siguiente y anterior
	List<Documento> listaDocumento;
	Documento documentoActual;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private String impresora(String impresora) {
		return (String) sessionMap.get("impresora"+impresora);
	}
	
	private Empresa getEmpresa() {
		return (Empresa) sessionMap.get("empresa");
	}

	public List<Producto> completeText(String query) {
		List<Producto> nombProductos = new ArrayList<>();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().getNombre() != null) {
				String articul = p.getProductoId().getNombre().toUpperCase().trim();
				 if (articul.indexOf(query.toUpperCase()) != -1) {
				//if (articul.startsWith(query.toUpperCase().trim())) {
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
			for (Producto p : nombProductos) {
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
		} catch (FactException e) {
			log.info("error en busqueda de productos del server 2: " + e.getMessage());
			return nombProductos;
		}
		return nombProductos;
	}

	public List<Proveedor> completeTextProveedor(String query) {
		List<Proveedor> nombProveedores = new ArrayList<>();
		for (Proveedor p : getProveedoresAll()) {
			if (p.getNombre() != null) {
				String articul = p.getNombre().toUpperCase().trim();
				// if (articul.indexOf(query.toUpperCase()) != -1) {
				if (articul.startsWith(query.toUpperCase().trim())) {
					nombProveedores.add(p);
				}
			}
		}
		return nombProveedores;
	}

	public List<Grupo> completeTextGrupo(String query) {
		List<Grupo> nombGrupos = new ArrayList<>();
		for (Grupo g : getGruposAll()) {
			if (g.getNombre() != null) {
				String articul = g.getNombre().toUpperCase().trim();
				if (articul.indexOf(query.toUpperCase()) != -1) {
					// if (articul.startsWith(query.toUpperCase().trim())) {
					nombGrupos.add(g);
				}
			}
		}
		return nombGrupos;
	}

	public List<Marca> completeTextMarca(String query) {
		List<Marca> nombMarcas = new ArrayList<>();
		for (Marca p : getMarcasAll()) {
			if (p.getNombre() != null) {
				String articul = p.getNombre().toUpperCase().trim();
				// if (articul.indexOf(query.toUpperCase()) != -1) {
				if (articul.startsWith(query.toUpperCase().trim())) {
					nombMarcas.add(p);
				}
			}
		}
		return nombMarcas;
	}

	public List<String> completeCodigo(String query) {
		List<String> codProductos = new ArrayList<>();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().getProductoId() != null) {
				String articul = p.getProductoId().getProductoId().toString();
				if (articul.indexOf(query) != -1) {
					codProductos.add(articul);
				}
			}
		}
		return codProductos;
	}

	public String buscarProductoCodBarras(AjaxBehaviorEvent event) {
		String completo = getCodigoBarras();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().getCodigoBarras() != null && completo.equals(p.getProductoId().getCodigoBarras())) {
				setCodigoInterno(p.getProductoId().toString());
				setArticulo(p.getProductoId());
				setUnidad(p.getProductoId().getCosto());
				productoSelect = p.getProductoId();
				break;
			}
		}
		return "";
	}

	public void buscarProducto(SelectEvent event) {
		productoSelect = (Producto) event.getObject();
		setParciaPopup("S");
		if (productoSelect != null && productoSelect.getBalanza() == 1l) {
			RequestContext.getCurrentInstance().execute("pupupCantidadMM();");
			determinarBalanza();
		} else {
			RequestContext.getCurrentInstance().execute(FOCUS_CANTIDAD);
			RequestContext.getCurrentInstance().execute(SELECT_CANTIDAD);
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('cantidad_in').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
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
				costoP = productoSelect.getCosto();
			}
			setCantidad(canti);
			log.info("costo publico:"+costoP);
			setParcial(canti * costoP);
			RequestContext.getCurrentInstance().update("cantidadMM:nombrePro");
			RequestContext.getCurrentInstance().update("cantidadMM:valorParcial");
			RequestContext.getCurrentInstance().update("cantidadMM:cantidadPar");
		} catch (Exception e) {
			setCantidad(0.0);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error en el uso de la Gramera, por favor vuelva a pesar: " + e.getMessage()));
		}
		return "";
	}

	public void detalleEnDocumento(AjaxBehaviorEvent event) {
		getDocumento().setDetalleEntrada(getDetalle());
	}

	public void buscarProveedor(SelectEvent event) throws IOException {

		proveedorSelect = (Proveedor) event.getObject();
		long server = 1;
		log.info("proveedor select:" + proveedorSelect.getNombre());
		setCodigoProveedor(proveedorSelect.getProveedorId());
		setTipoDocumentoEntrada(getDocumento().getTipoDocumentoId().getNombre());
		setIdentificacionProveedor(proveedorSelect.getDocumento());
		setFechaCreacion(new Date());
		getDocumento().setProveedorId(proveedorSelect);
		getDocumento().setDetalleEntrada(getDetalle());
		documentoService.update(getDocumento(), server);
		RequestContext.getCurrentInstance().execute(ACTIVAR_CAMPO_COD_BARRAS);// se
		RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='inline';"); // campos
		RequestContext.getCurrentInstance().execute("entrarFactura();");// art_1_input
	}

	public void buscarProductoCodigo(SelectEvent event) {
		String completo = event.getObject().toString();
		for (ProductoEmpresa p : getProductosAll()) {
			if (p.getProductoId().toString().contains(completo)) {
				setCodigoInterno(p.getProductoId().toString());
				setArticulo(p.getProductoId());
				setUnidad(p.getProductoId().getCosto());
				productoSelect = p.getProductoId();
				break;
			}
		}
		event.getObject().toString();
	}

	public Configuracion configuracion() {
		return (Configuracion) sessionMap.get("configuracion");
	}

	public String selectedTipoDoc(TipoDocumento td) {
		Documento docOjb = new Documento();
		docOjb.setTipoDocumentoId(td);
		docOjb.setFechaRegistro(new Date());
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if (td.getTipoDocumentoId() == 1l && server == 2l) { // si es una
																// entrada de
																// almacen se
																// guarda el en
																// el server 2
			server = 2l;
		} else {
			server = 1l;
		}
		docOjb.setUsuarioId(usuario);
		Proveedor pro = new Proveedor();
		pro.setProveedorId(1l);
		docOjb.setProveedorId(pro);
		documentoService.save(docOjb, server);
		setDocumento(docOjb);
		setTipoDocumentoEntrada(td.getNombre());
		RequestContext.getCurrentInstance().execute("pagina='opcNuevo';");
		setTotal(null);
		setIva(null);
		setRetefuente(null);
		setGravado(null);
		setExecento(null);
		limpiar();
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");		
		return "";
	}

	private void limpiar() {
		RequestContext.getCurrentInstance().execute("document.getElementById('tipo_documento').style.display='none';"); // opciones
		// de
		// tipo
		// documento
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('op_mov_mes_content').style.display='none';");// opciones
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('detalleEntrada').style.display='inline';");
		RequestContext.getCurrentInstance()
		.execute("document.getElementById('detalleText').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('detalleEntrada').focus();");
		
		setArticulo(null);
		setCodigoBarras(null);
		setCantidad(null);
		setCodigoInterno(null);
		setUnidad(null);
		setProductos(null);
		setDetalle(null);
		setProductosBorrarList(null);
		setDocumentoActual(null);
		setListaDocumento(null);
		productoSelect = null;
		actModFactura = Boolean.FALSE;
		RequestContext.getCurrentInstance().update("detalleEntrada");
		RequestContext.getCurrentInstance().update("detalleText");
		RequestContext.getCurrentInstance().update("codig");
		RequestContext.getCurrentInstance().update("nombcop_mov_mes");
		RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("nombc");
		RequestContext.getCurrentInstance().update("cod_");
		RequestContext.getCurrentInstance().update("cantidad_in");
		RequestContext.getCurrentInstance().update("unidad_");
		RequestContext.getCurrentInstance().update("dataList");
		RequestContext.getCurrentInstance().update("codBarras_input");
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('prodList').style.display='none';");
	}

	public String cantidadEnter(AjaxBehaviorEvent event) {
		log.info("entra a cantidad enter");
		if (getCantidad() == null) {
			RequestContext.getCurrentInstance().execute(FOCUS_CANTIDAD);
			RequestContext.getCurrentInstance().execute(SELECT_CANTIDAD);
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('cantidad_in').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			return "";
		}
		Double cantidadTemp;
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		// si es una entrada de almacen se guarda el en el server 2
		if (getDocumento().getTipoDocumentoId().getTipoDocumentoId() == 1l && server == 2l) { 
			server = 2l;
		} else {
			server = 1l;
		}
		log.info("dio enter en cantidad");
		DocumentoDetalle docDetalle = new DocumentoDetalle();
		DocumentoDetalleVo docDetalleVo = new DocumentoDetalleVo();
		docDetalle.setCantidad(getCantidad());
		docDetalle.setProductoId(productoSelect);
		docDetalle.setDocumentoId(getDocumento());
		Date fecha = new Date();
		docDetalle.setFechaRegistro(fecha);
		docDetalle.setEstado(1l);
		docDetalle.setParcial(getCantidad() * productoSelect.getCosto());
		documentoDetalleService.save(docDetalle, server);
		Producto productEdit = productoSelect;
		 ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(), productoSelect.getProductoId());
		cantidadTemp =    productoEmpresa.getCantidad() == null ? 0.0 : productoEmpresa.getCantidad();
		log.info("cantidad actual:" + cantidadTemp);
		OpcionUsuario stock;
		switch (getDocumento().getTipoDocumentoId().getTipoDocumentoId().toString()) {
		case "6": // tipo documento igual a salida de almacen
			log.info("salida de almacen");
			cantidadTemp = cantidadTemp - getCantidad();
			log.info("cantidad actualizada:" + cantidadTemp);
			break;
		case "2":// tipo documento igual a entrada de almacen
			cantidadTemp = cantidadTemp + getCantidad();
			stock = (OpcionUsuario) sessionMap.get("stock");
			Double cantidadTempo = productoSelect.getCantidad() == null ? 0.0 : productoSelect.getCantidad();
			if (stock != null && productoSelect.getStockMax() != null && cantidadTempo > productoSelect.getStockMax()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad max sugerida para "
						+ productoSelect.getNombre() + " " + productoSelect.getStockMax()));
			}
			RequestContext.getCurrentInstance().update("growl");
			log.info("cantidad actualizada:" + cantidadTemp);
			break;
		case "1":// tipo documento igual a entrada de almacen
			cantidadTemp = cantidadTemp + getCantidad();
			stock = (OpcionUsuario) sessionMap.get("stock");
			if (stock != null && productoSelect.getStockMax() != null
					&& productoSelect.getCantidad() > productoSelect.getStockMax()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lacantidad max sugerida para "
						+ productoSelect.getNombre() + " " + productoSelect.getStockMax()));
			}
			RequestContext.getCurrentInstance().update("growl");
			log.info("cantidad actualizada:" + cantidadTemp);
			break;
		default:
			break;
		}

		Evento evento = new Evento();
		TipoEvento tipoEvento = new TipoEvento();
		tipoEvento.setTipoEventoId(5l); // se envia tipo evento igual a
		evento.setCampo(productoSelect.getNombre()); // cambio de
														// cantidad
		evento.setTipoEventoId(tipoEvento);
		evento.setFechaRegistro(new Date());
		evento.setUsuarioId(usuario());
		evento.setValorActual("" + cantidadTemp);
		evento.setValorAnterior("" + productoEmpresa.getCantidad());
		eventoService.save(evento);

		productoEmpresa.setCantidad(cantidadTemp);
		productoEmpresaService.update(productoEmpresa);
		//productoService.update(productEdit, server);
		docDetalleVo.setCantidad(getCantidad());
		docDetalleVo.setProductoId(productoSelect);
		docDetalleVo.setDocumentoId(getDocumento());
		docDetalleVo.setUnitario(productoSelect.getCosto());
		docDetalleVo.setDocumentoDetalleId(docDetalle);
		docDetalleVo.setFechaRegistro(fecha);
		if (getCantidad() != null && productoSelect.getCosto() != null) {
			docDetalleVo.setParcial(getCantidad() * productoSelect.getCosto());
		} else {
			docDetalleVo.setParcial(0.0);
		}
		if (getCantidad() > 0) {
			getProductos().add(docDetalleVo);
		}
		// en esta funcion de calcula el excento, iva, total
		setDocumento(Calculos.calcularExcento(getDocumento(), getProductos()));
		// se agrega re
		if (proveedorSelect != null && proveedorSelect.getRetencion() != null && getDocumento().getTipoDocumentoId().getTipoDocumentoId()==2l) {
			setDocumento(Calculos.calcularRetefuente(getDocumento(), proveedorSelect));
		}else {
			setDocumento(Calculos.calcularRetefuente(getDocumento(), proveedorService.getById(1l)));
		}
		setTotal(getDocumento().getTotal());
		setIva(getDocumento().getIva());
		setExecento(getDocumento().getExcento());
		setGravado(getDocumento().getGravado());
		setRetefuente(getDocumento().getRetefuente());
		documentoService.update(getDocumento(), server);
		RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA); // campos//
																		// del
																		// nuevo
																		// //
																		// producto
		setArticulo(null);
		setCantidad(null);
		setCodigoInterno(null);
		setCodigoBarras(null);
		RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').value='';");
		RequestContext.getCurrentInstance().update("prodList");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().execute(ACTIVAR_CAMPO_COD_BARRAS);
		RequestContext.getCurrentInstance().update("codBarras_input");
		RequestContext.getCurrentInstance().update("busquedaCodBarras");
		RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input').focus();");
		RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input').select();");
		return "";
	}

	public void crearProducto(AjaxBehaviorEvent event) {
		if (getCrear().equalsIgnoreCase("S")) {
			setArticuloNew("");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('nuevoProductoForm:articuloNew').value='';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('nuevoProductoForm:articuloNew').focus();");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('nuevoProductoForm:articuloNew').select();");
			RequestContext.getCurrentInstance().execute("PF('nuevoProducto').show();");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('nuevoProductoForm:articuloNew').className+=' state-focus';");
			RequestContext.getCurrentInstance().execute("pagina='crearProducto';");
			// faltan los valores por defecto del formulario de guardar
			setCrearNew("N");
			log.info("presiono s entra popup creacion crear");
		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			log.info("presiono ota cosa no entra al formulario de crear");
		}
		setCrear("N");
	}

	public void crearNewProducto() {
		String crearN = getCrearNew().toUpperCase();
		if (!crearN.equals("S")) {
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			return;
		}

		if (getCodigoBarrasNew() != null) {
			Producto p = productoService.getByCodigoBarras(getCodigoBarrasNew());
			if (p != null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						"El Producto Con código de barras " + getCodigoBarrasNew() + " ya se encuentra registrado"));
				return;
			}
		}
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		Producto prodNew = new Producto();
		prodNew.setProductoId(getCodigoNew().longValue());
		prodNew.setUnidad(getUnidadNew());
		if (getValanzaNew() != null) {
			prodNew.setBalanza(getValanzaNew().equalsIgnoreCase("S") ? 1l : 0l);
		} else {
			prodNew.setBalanza(0l);
		}
		if (getVariosNew() != null) {
			prodNew.setVarios(getVariosNew().equalsIgnoreCase("S") ? 1l : 0l);
		} else {
			prodNew.setVarios(0l);
		}
		if (getSubProductoNew() != null) {
			prodNew.setSubProducto(getSubProductoNew().equalsIgnoreCase("S") ? 1l : 0l);
		} else {
			prodNew.setSubProducto(0l);
		}

		prodNew.setCodigoBarras(getCodigoBarrasNew());
		if (getCostoNew() != null) {
			prodNew.setCosto(getCostoNew());
		} else {
			prodNew.setCosto(0.0);
		}
		if (getPublicoNew() != null) {
			prodNew.setCostoPublico(getPublicoNew());
		} else {
			prodNew.setCostoPublico(0.0);
		}
		prodNew.setFechaRegistro(new Date());
		if (getIvaNew() != null) {
			prodNew.setIva(getIvaNew());
		} else {
			prodNew.setIva(0.0);
		}
		if (getHipoconsumo() != null) {
			prodNew.setHipoconsumo(getHipoconsumo());
		} else {
			prodNew.setHipoconsumo(0.0);
		}
		if (getProveedorNew() != null) {
			prodNew.setProveedorId(getProveedorNew());
		}
		if (getGrupoNew() != null) {
			prodNew.setGrupoId(getGrupoNew());
		}
		if (getMarcaNew() != null) {
			prodNew.setMarcaId(getMarcaNew());
		}
		prodNew.setNombre(getArticuloNew().toUpperCase().trim());
		if (getStockMaxNew() != null) {
			prodNew.setStockMax(getStockMaxNew());
		} else {
			prodNew.setStockMax(1000000l);
		}
		if (getStockMinNew() != null) {
			prodNew.setStockMin(getStockMinNew());
		} else {
			prodNew.setStockMin(-1000000l);
		}
		prodNew.setPeso(getPesoKgNew());
		prodNew.setPesoEmpaque(getPesoEmpaqueKgNew()==null?0.0:getPesoEmpaqueKgNew());
		prodNew.setEstado(1l);
		prodNew.setCantidad(0.0);
		productoService.save(prodNew, 1l);
		if (server == 2l) {
			productoService.save(prodNew, server);
		}
		setCodigoNew(null);
		productoSelect = prodNew;
		setCodigoInterno(prodNew.getProductoId().toString());
		setArticulo(prodNew);
		setUnidad(prodNew.getCosto());
		ProductoEmpresa productoEmpresa = new ProductoEmpresa();
		productoEmpresa.setCantidad(0.0);
		productoEmpresa.setEmpresaId(getEmpresa());
		productoEmpresa.setFechaRegistro(new Date());
		productoEmpresa.setProductoId(prodNew);
		if (getPublicoNew() != null) {
			productoEmpresa.setPrecio(getPublicoNew());
		} else {
			productoEmpresa.setPrecio(0.0);
		}
		productoEmpresaService.save(productoEmpresa);
		RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("art_1_input");
		RequestContext.getCurrentInstance().update("cod_");
		RequestContext.getCurrentInstance().update("cantidad_in");
		RequestContext.getCurrentInstance().execute(FOCUS_CANTIDAD);
		RequestContext.getCurrentInstance().execute(SELECT_CANTIDAD);
		RequestContext.getCurrentInstance().execute(
				"document.getElementById('cantidad_in').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Creado exitosamente"));

		setCrearNew("N");
		RequestContext.getCurrentInstance().execute("pagina='opcNuevo';");
		RequestContext.getCurrentInstance().execute("document.getElementById('deseaGuardar').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProducto').style.display='none';");
	}
	
	public void buscarByIdFactura() {
		log.info("entra a buscar factura id: "+getBuscar());
		List<DocumentoDetalle> dd ;
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		Documento docu = documentoService.getById(Long.valueOf(getBuscar()==null?"0":getBuscar()));
		if(docu==null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No se encontró el documento con id: "+getBuscar()));
			return;
		}
//		//if(docu.getTipoDocumentoId().getTipoDocumentoId()!=2 || docu.getTipoDocumentoId().getTipoDocumentoId()!=6) {
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El documento solicitado no corresponde a una entrasa o salida de almacen"));
//			return;
//		}
		setDocumentoActual(docu);
		setDocumento(docu);
		dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
		for (DocumentoDetalle d1 : dd) {
			DocumentoDetalleVo vo = new DocumentoDetalleVo();
			vo.setCantidad(d1.getCantidad());
			vo.setDocumentoDetalleId(d1);
			vo.setDocumentoId(d1.getDocumentoId());
			vo.setFechaRegistro(d1.getFechaRegistro());
			vo.setParcial(d1.getParcial());
			d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
			vo.setProductoId(d1.getProductoId());
			ddVo.add(vo);
		}
		setDetalle(docu.getDetalleEntrada());
		setProveedor(docu.getProveedorId());
		setProductos(ddVo);
		setIdentificacionProveedor(docu.getProveedorId().getDocumento());
		setNombreProveedor(getDocumentoActual().getProveedorId().getNombre());
		setTotal(getDocumentoActual().getTotal());
		setExecento(getDocumentoActual().getExcento());
		setIva(getDocumentoActual().getIva());
		setRetefuente(getDocumentoActual().getRetefuente());
		setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('dataList_content').style.display='inline';");
		RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
		RequestContext.getCurrentInstance().update("cod_");
		RequestContext.getCurrentInstance().update("nombc");
		RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("cantidad_in");
		RequestContext.getCurrentInstance().update("dataList");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().update("unidad_");
	}
	
	public void recalcularPrecio(DocumentoDetalleVo d) {
		log.info("producto para cambio de precio: "+d.getUnitario());
		dCambio = d;
	}
	
	public String recalcularPrecio(AjaxBehaviorEvent event) {
		log.info("cambio de precio MM:" + getCambioTemp());
		if(getCambioTemp()==null  ){
			return "";
		}
		
		Double cambioTemp1;
		try {
		   cambioTemp1 = Double.valueOf(getCambioTemp().trim());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error cambiando el precio");
			return "";
		}
		log.info("paso:" + getCambioTemp());
		//se comenta el pedaso de codigo que hace que se pueda bajar el precio		
		int pos = getProductos().indexOf(dCambio);
		Double descuentoTemp = getDocumento().getDescuento() == null ? 0.0 : getDocumento().getDescuento();
		
		if ( dCambio.getUnitario() > cambioTemp1) {
			descuentoTemp += (dCambio.getUnitario() - cambioTemp1);
		}
		getDocumento().setDescuento(descuentoTemp);
		Double cantidadtemp = dCambio.getCantidad();
		dCambio.setUnitario(cambioTemp1);
		dCambio.setParcial(cantidadtemp * cambioTemp1);
		getProductos().set(pos, dCambio);

		setDocumento(Calculos.calcularExcento(getDocumento(), getProductos()));
		if (getDocumento().getProveedorId() != null && getDocumento().getProveedorId().getRetencion() != null && getDocumento().getTipoDocumentoId().getTipoDocumentoId()==2l) {
			setDocumento(Calculos.calcularRetefuente(getDocumento(), getDocumento().getProveedorId()));
		}else {
			setDocumento(Calculos.calcularRetefuente(getDocumento(), proveedorService.getById(1l)));
		}
		Long tipo = getDocumento().getTipoDocumentoId().getTipoDocumentoId();
		Long server = configuracion().getServer();
		DocumentoDetalle d = documentoDetalleService.getById(dCambio.getDocumentoDetalleId().getDocumentoDetalleId());
		Producto prod= dCambio.getDocumentoDetalleId().getProductoId();
		prod.setCosto(cambioTemp1);				
		d.setParcial(cantidadtemp * cambioTemp1);
		if (tipo == 9l && server == 2) {
			productoService.update(prod, server);
			documentoService.update(getDocumento(), server);
			documentoDetalleService.update(d, server);
		} else {
			productoService.update(prod, 1l);
			documentoService.update(getDocumento(), 1l);
			documentoDetalleService.update(d, 1l);
		}
		setTotal(getDocumento().getTotal());
		setIva(getDocumento().getIva());
		setExecento(getDocumento().getExcento());
		setGravado(getDocumento().getGravado());
		setRetefuente(getDocumento().getRetefuente());
		RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
		RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
		RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').value='';");
		RequestContext.getCurrentInstance().execute("PF('cambioPrecioMM').hide();");
		RequestContext.getCurrentInstance().update("dataList");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().update("art_1");
		setCambioTemp(null);
		RequestContext.getCurrentInstance().update("cambioPrecioFormMM:cambioPrecioIn");		
		return "";
	}
	

	public String editarNewProducto(AjaxBehaviorEvent event) {
		String crearN = getEditarNew().toUpperCase();
		if (!crearN.equals("S")) {
			RequestContext.getCurrentInstance().execute("PF('info_articulos').hide();");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto NO editado"));
		}

		String nombre = getProductoEdict().getNombre().trim();
		getProductoEdict().setNombre(nombre);
		Producto prodNew = getProductoEdict();
		ProductoEmpresa empresaEdit= productoEmpresaService.getByProductoAndEmpresa(getEmpresa(), getProductoEdict().getProductoId());
		if (getProductoEdict().getProductoId() == 1l) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No es posible editar el producto ¡Varios!"));
			return "";
		}


		
		// se existe un cambio de precio se registra el evento
		if (getPublicoNew() != null && getPublicoNew().equals(getProductoEdict().getCostoPublico())) {
			Evento evento = new Evento();
			TipoEvento tipoEvento = new TipoEvento();
			tipoEvento.setTipoEventoId(3l); // se envia tipo evento igual a
											// cambio de precio
			evento.setTipoEventoId(tipoEvento);
			evento.setFechaRegistro(new Date());
			evento.setUsuarioId(usuario());
			evento.setValorActual("" + getPublicoNew());
			evento.setValorAnterior("" + getProductoEdict().getCostoPublico());
			eventoService.save(evento);
		}

		if (getValanzaNew() != null) {
			prodNew.setBalanza(getValanzaNew().equalsIgnoreCase("N") ? 0l : 1l);
			if (getValanzaNew().equalsIgnoreCase("S")) {
				setUnidadNew("N");
			} else {
				setUnidadNew("S");
			}
		} else {
			prodNew.setBalanza(0l);
			setUnidadNew("S");
		}

		if (getVariosNew() != null) {
			prodNew.setVarios((getVariosNew().equalsIgnoreCase("N") ? 0l : 1l));
		} else {
			prodNew.setVarios(0l);
		}

		if (getUnidadNew() != null) {
			prodNew.setUnidad(getUnidadNew().equalsIgnoreCase("N") ? "N" : "S");
		} else {
			prodNew.setUnidad("N");
		}
		prodNew.setCodigoBarras(getCodigoBarrasNew());
		if (getCostoNew() != null) {
			prodNew.setCosto(getCostoNew());
		} else {
			prodNew.setCosto(0.0);
		}
		if (getPublicoNew() != null) {
			prodNew.setCostoPublico(getPublicoNew());
			empresaEdit.setPrecio(getPublicoNew());
		} else {
			prodNew.setCostoPublico(0.0);
		}
		prodNew.setFechaActualiza(getFechaEdit());
		if (getIvaNew() != null) {
			prodNew.setIva(getIvaNew());
		} else {
			prodNew.setIva(0.0);
		}
		if (getHipoconsumo() != null) {
			prodNew.setHipoconsumo(getHipoconsumo());
		} else {
			prodNew.setHipoconsumo(0.0);
		}
		if (getGrupoNew() != null) {
			prodNew.setGrupoId(getGrupoNew());
		}
		prodNew.setMarcaId(getMarcaNew());
		prodNew.setPeso(getPesoKgNew());
		prodNew.setStockMax(getStockMaxNew());
		prodNew.setStockMin(getStockMinNew());
		prodNew.setEstado(1l);
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		productoService.update(prodNew, 1l);
		productoEmpresaService.update(empresaEdit);
		if (server == 2l) {
			productoService.update(prodNew, 2l);
		}
		RequestContext.getCurrentInstance().execute("PF('info_articulos').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto editado Exitosamente"));
		limpiarEditar();

		RequestContext.getCurrentInstance().execute("document.getElementById('opciones:Sig_movi_mes1').focus();");
		return "";
	}

	public void asignarGrupo(SelectEvent event) {
		Grupo pr = (Grupo) event.getObject();
		setGrupoNew(pr);
		log.info("grupo asignado:" + pr.getNombre());
	}

	public void editarProducto(SelectEvent event) {
		Producto pr = (Producto) event.getObject();
		setProductoEdict(pr);
		setCodigoEdit(new BigDecimal(pr.getProductoId()));
		setFechaEdit(pr.getFechaRegistro());
		setCostoNew(pr.getCosto());
		setPublicoNew(pr.getCostoPublico());
		setIvaNew(pr.getIva());
		setStockMaxNew(pr.getStockMax());
		setStockMinNew(pr.getStockMin());

		if (pr.getGrupoId() != null) {
			pr.setGrupoId(grupoService.getById(pr.getGrupoId().getGrupoId()));
			setGrupoNew(pr.getGrupoId());
		}
		setMarcaNew(pr.getMarcaId());
		setCodigoBarrasNew(pr.getCodigoBarras());
		setPesoKgNew(pr.getPeso());
		if (pr.getUnidad() == null) {
			setUnidadNew("N");
		} else {
			setUnidadNew(pr.getUnidad().toUpperCase());
		}
		if (pr.getBalanza() == null) {
			setValanzaNew("N");
		} else {
			setValanzaNew(pr.getBalanza() == 1l ? "S" : "N");
		}
		if (pr.getVarios() == null) {
			setVariosNew("N");
		} else {
			setVariosNew(pr.getBalanza() == 1l ? "S" : "N");
		}
	}

	public void limpiarEditar() {
		log.info("entra a limpiar editar: ");
		setProductoEdict(null);
		setCodigoEdit(null);
		setFechaEdit(null);
		setCostoNew(null);
		setPublicoNew(null);
		setIvaNew(null);
		setStockMaxNew(null);
		setStockMinNew(null);
		setGrupoNew(null);
		setMarcaNew(null);
		setCodigoBarrasNew(null);
		setPesoKgNew(null);
		setEditarNew(null);
		setUnidadNew(null);
		setValanzaNew(null);
		setProductosAll(null);
		setVariosNew(null);
	}

	public String imprimirFactura() throws IOException, DocumentException, PrinterException, PrintException {
		if (getDocumento().getDocumentoId() == null || getImpresion()==null ||getImpresion().equalsIgnoreCase("n")) {
			RequestContext.getCurrentInstance().execute("PF('imprimirMM').hide();");
			return "";
		}
		
		log.info("entra a imprimir entrada almacen");
		Configuracion configuracion = configuracion();
		int numeroImpresiones = configuracion.getNumImpresion();
		Long server = configuracion.getServer();
		String impresora = impresora("1");
		Empresa e = getEmpresa();
		getDocumento().setImpreso(1l);
		getDocumento().setEntregado(0l);
		getDocumento().setConsecutivoDian(""+documento.getDocumentoId());
		if (getDocumento().getProveedorId() == null) {
			Proveedor c = new Proveedor();
			c.setProveedorId(1l); // se le envia proveedor varios por defecto
			c.setNombre("Varios");
			c.setDireccion("");
			getDocumento().setProveedorId(c);
		}
		if(getCartera().equalsIgnoreCase("s")) {
			log.info("documento para cartera");
			crearVale();
		}
		
		// se busca la mac del equipo y se le asigna a la factura
		getDocumento().setMac(Calculos.conseguirMAC2());
		documentoService.update(getDocumento(), server);

		String imp = e.getImpresion().toUpperCase();
		for (int i = 0; i < numeroImpresiones; i++) { // si la factura fue
														// a// credito se//
														// imprime dos veces
			setProductos(Calculos.ordenar(getProductos()));
			switch (imp) {
			case "TXT":
				Impresion.imprimirEntadaAlmacenTXT(getDocumento(), getProductos(), configuracion, impresora, e);
				break;
			case "BIG":
				// quitar la dependencia del ireport
				// imprimirTemporal(tituloFactura);
				// pdf = imprimirBig(tituloFactura);
				break;
			case "PDF":
				Impresion.imprimirEntadaAlmacenPDF(getDocumento(), getProductos(), usuario(), configuracion, impresora, e);
				break;
			case "BIG_PDF":
				Impresion.imprimirBig(getDocumento(), getProductos(), usuario(), configuracion, null, impresora,e);
				break;
			case "SMALL_PDF":
				Impresion.imprimirPDFSmall(getDocumento(), getProductos(), usuario(), configuracion, impresora,"false",e);
				break;
			default:
				Impresion.imprimirEntadaAlmacenPDF(getDocumento(), getProductos(), usuario(), configuracion, impresora, e);
				break;

			}

		}
		RequestContext.getCurrentInstance().execute("pagina='opcNuevo';");
		setTotal(null);
		setIva(null);
		setRetefuente(null);
		setGravado(null);
		setExecento(null);
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().execute("PF('imprimirMM').hide();");
		limpiar();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Impresión Completa"));
		return "";
	}
	
	private void crearVale() {
		Documento docu = new Documento();
		TipoDocumento tido = new TipoDocumento();
		TipoPago pago = new TipoPago();
		Usuario usuario = usuario();
		long server = 1;
		docu.setFechaRegistro(new Date());
		docu.setProveedorId(getDocumento().getProveedorId());
		docu.setRetefuente(getDocumento().getRetefuente());
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
		docu.setDetalleEntrada(getDocumento().getDetalleEntrada());
		docu.setConsecutivoDian(""+getDocumento().getDocumentoId());
		documentoService.save(docu, server);
		log.info("se crea el vale por factura venta a cretido: " + docu.getDocumentoId());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vale Creado exitosamente"));
	}

	public Usuario usuario() {
		return (Usuario) sessionMap.get("userLogin");
	}

	public void buscarUltimaFactura() {
		log.info("buscar ultima factura");
		Documento ultimoFactura;
		Usuario usuario = usuario();
		Long idFactura = 2l; // id de tipo documento entrada de almacen
		List<DocumentoDetalle> dd;
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		ultimoFactura = documentoService.getByLastAndTipo(idFactura, usuario.getUsuarioId());
		if (ultimoFactura != null) {
			setDocumento(ultimoFactura);
			dd = documentoDetalleService.getByDocumento(ultimoFactura.getDocumentoId(), 1l);
			for (DocumentoDetalle d1 : dd) {
				DocumentoDetalleVo vo = new DocumentoDetalleVo();
				vo.setCantidad(d1.getCantidad());
				vo.setDocumentoDetalleId(d1);
				vo.setDocumentoId(d1.getDocumentoId());
				vo.setFechaRegistro(d1.getFechaRegistro());
				vo.setParcial(d1.getParcial());
				d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
				vo.setProductoId(d1.getProductoId());
				ddVo.add(vo);
			}
			setTipoDocumentoEntrada(ultimoFactura.getTipoDocumentoId().getNombre());
			setProductos(ddVo);
			setTotal(ultimoFactura.getTotal());
			setExecento(ultimoFactura.getExcento());
			setIva(ultimoFactura.getIva());
			setRetefuente(ultimoFactura.getRetefuente());
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('dataList_content').style.display='inline';");
			RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
			RequestContext.getCurrentInstance().update("cod_");
			RequestContext.getCurrentInstance().update("nombc");
			RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
			RequestContext.getCurrentInstance().update("cantidad_in");
			RequestContext.getCurrentInstance().update("dataList");
			RequestContext.getCurrentInstance().update("execentoFact");
			RequestContext.getCurrentInstance().update("ivaFact");
			RequestContext.getCurrentInstance().update("totalFact");
			RequestContext.getCurrentInstance().update("gravadoFact");
			RequestContext.getCurrentInstance().update("unidad_");
			RequestContext.getCurrentInstance().update("retefuentelFact");
			// falta poner el focus en codigo de barras
			// actualizar los campos y ocultar los que no se ven y mostrar los
			// que se ven
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay Entradas de almacen"));
		}
	}

	public void modificarUltimaFactura() {
		log.info("buscar ultima factura MM");
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			RequestContext.getCurrentInstance().execute("document.getElementById('confir_mm').style.display='inline';");
			actModFactura = Boolean.TRUE;
		}
		// colocar una variable para saber si esta modificando factura,
	}

	public void borrarFactura() {
		claveBorradoActivo = (OpcionUsuario) sessionMap.get("claveBorradoActivo");
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			//if (claveBorradoActivo != null) {
			if(1!=1) {
				RequestContext.getCurrentInstance().execute("PF('modificarFactura').show();");
			} else {
				activarBorrado();
			}
		}
	}

	public void activarBorrado() {
		log.info("presiona b para borrar factura MM");
		setProductosBorrar(Boolean.TRUE);
		setProductosBorrarList(getProductos());
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT:0:row");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('dataList_content').style.display='none';");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('borrarTablaMM:checkboxDT').style.display='inline';");
		RequestContext.getCurrentInstance()
				.execute("document.getElementById('borrarTablaMM:checkboxDT:0:rowDelete_').focus();");
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT:0:rowDelete_");
		RequestContext.getCurrentInstance().execute("document.getElementById('confir_mm').style.display='none';");
		RequestContext.getCurrentInstance().execute("pagina='borrando_facturaMM';");
		RequestContext.getCurrentInstance().execute("ruta=='movimiento_mes';");
		actModFactura = Boolean.FALSE;
	}

	public void borrarFacturaSelect(DocumentoDetalleVo d) {
		Long server = 1l;
		getProductos().remove(d);
		try {
			setDocumento(Calculos.calcularExcento(d.getDocumentoId(), getProductos()));
			if (d.getDocumentoId().getProveedorId() != null && d.getDocumentoId().getProveedorId().getRetencion() != null && getDocumento().getTipoDocumentoId().getTipoDocumentoId()==2l) {
				setDocumento(Calculos.calcularRetefuente(getDocumento(), d.getDocumentoId().getProveedorId()));
			}else {
				setDocumento(Calculos.calcularRetefuente(getDocumento(), proveedorService.getById(1l)));
			}
			documentoService.update(getDocumento(), server);
			setExecento(getDocumento().getExcento());
			setGravado(getDocumento().getGravado());
			setIva(getDocumento().getIva());
			setTotal(getDocumento().getTotal());
			setRetefuente(getDocumento().getRetefuente());
			documentoDetalleService.borrar(d.getDocumentoDetalleId().getDocumentoDetalleId(), 0l, server);
			ProductoEmpresa productoEmpresa = productoEmpresaService.getByProductoAndEmpresa(getEmpresa(),
					d.getProductoId().getProductoId());
			Double cantidad1 = productoEmpresa.getCantidad() - d.getCantidad();
			productoEmpresa.setCantidad(cantidad1);
			productoEmpresaService.update(productoEmpresa);

			if (server == 2l) {
				productoEmpresa = productoEmpresaService.getById(d.getProductoId().getProductoId());
				cantidad = productoEmpresa.getCantidad() - d.getCantidad2();
				productoEmpresa.setCantidad(cantidad);
				productoEmpresaService.update(productoEmpresa);
			}

		} catch (Exception e) {
			log.error("!!error borrando el producto:" + d.getProductoId().getProductoId());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error borrando Productos"));
		}
		
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		
		if (!getProductos().isEmpty()) {
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('borrarTablaMM:checkboxDT:0:rowDelete_').focus();");
		}
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		log.info("documentoDetalle borrado:" + d.getDocumentoDetalleId());
	}

	public void popupImprimir() {
		if (actModFactura) {
			log.info("i en modificar factura");
			// todo lo de modificar, activar cosas y asi....

			RequestContext.getCurrentInstance().execute(ACTIVAR_CAMPO_COD_BARRAS);
			RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='inline';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('op_mov_mes_content').style.display='none';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('art_1_input').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').value='';");
			RequestContext.getCurrentInstance().execute("pagina='creando_facturaMM';");
			RequestContext.getCurrentInstance().update("busquedaCodBarras");
			RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
			RequestContext.getCurrentInstance().update("codBarras_input");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			RequestContext.getCurrentInstance().execute("pagina='borrando_facturaMM';");
			RequestContext.getCurrentInstance().execute("ruta=='movimiento_mes';");
			RequestContext.getCurrentInstance().execute("document.getElementById('confir_mm').style.display='none';");
			actModFactura = Boolean.FALSE;
		} else {
			log.info("imprimir");
			RequestContext.getCurrentInstance().execute("PF('imprimirMM').show();");
			
			setCartera("N");			
			RequestContext.getCurrentInstance().execute("document.getElementById('imprimirFormMM:cartera').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('imprimirFormMM:cartera').select();");
			RequestContext.getCurrentInstance().update("imprimirFormMM:cartera");
			//imprimirFactura();	
			RequestContext.getCurrentInstance().update("imprimirFormMM:excento_tag");
			RequestContext.getCurrentInstance().update("imprimirFormMM:gravado_tag");
			RequestContext.getCurrentInstance().update("imprimirFormMM:iva_tag");
			RequestContext.getCurrentInstance().update("imprimirFormMM:total1");
		}
	}

	public void siguienteFactura() throws ParseException {
		log.info("siguiente_factura_MM");
		List<DocumentoDetalle> dd = new ArrayList<>();
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				log.info("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1);
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setProductos(ddVo);
				setTotal(getDocumentoActual().getTotal());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				setRetefuente(getDocumentoActual().getRetefuente());
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setNombreProveedor(getDocumentoActual().getProveedorId().getNombre());
				setIdentificacionProveedor(getDocumentoActual().getProveedorId().getDocumento());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");
				RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("gravadoFact");
				RequestContext.getCurrentInstance().update("retefuentelFact");
				RequestContext.getCurrentInstance().update("unidad_");
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
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1);
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setNombreProveedor(getDocumentoActual().getProveedorId().getNombre());
				setIdentificacionProveedor(getDocumentoActual().getProveedorId().getDocumento());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				setProductos(ddVo);
				setTotal(getDocumentoActual().getTotal());
				setRetefuente(getDocumentoActual().getRetefuente());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");
				RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("gravadoFact");
				RequestContext.getCurrentInstance().update("retefuentelFact");
				RequestContext.getCurrentInstance().update("unidad_");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}

		}
	}

	public void anteriorFactura() throws ParseException {
		log.info("siguiente_factura_MM");
		List<DocumentoDetalle> dd = new ArrayList<>();
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				log.info("documento actual MM: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1);
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setNombreProveedor(getDocumentoActual().getProveedorId().getNombre());
				setIdentificacionProveedor(getDocumentoActual().getProveedorId().getDocumento());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				setProveedor(getDocumentoActual().getProveedorId());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				setProductos(ddVo);
				setTotal(getDocumentoActual().getTotal());
				setExecento(getDocumentoActual().getExcento());
				setRetefuente(getDocumentoActual().getRetefuente());
				setIva(getDocumentoActual().getIva());
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");
				RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("gravadoFact");
				RequestContext.getCurrentInstance().update("retefuentelFact");
				RequestContext.getCurrentInstance().update("unidad_");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		} else {
			Integer pos = getListaDocumento().indexOf(getDocumentoActual());
			if (pos < getListaDocumento().size() - 1) {
				setDocumentoActual(getListaDocumento().get(pos + 1));
				log.info("documento actual: MM" + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(), 1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1);
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setProductos(ddVo);
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setNombreProveedor(getDocumentoActual().getProveedorId().getNombre());
				setIdentificacionProveedor(getDocumentoActual().getProveedorId().getDocumento());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				setProveedor(getDocumentoActual().getProveedorId());
				setDetalle(getDocumentoActual().getDetalleEntrada());
				setTotal(getDocumentoActual().getTotal());
				setRetefuente(getDocumentoActual().getRetefuente());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance().execute(MOSTRAR_LA_LISTA);
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
				RequestContext.getCurrentInstance().update("nombc");
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("retefuentelFact");
				RequestContext.getCurrentInstance().update("gravadoFact");
				RequestContext.getCurrentInstance().update("unidad_1");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}

		}
	}

	public String cantidadBalanza(AjaxBehaviorEvent event) {
		if (getParciaPopup().equalsIgnoreCase("S")) {
			log.info("entra");
			cantidadEnter(null);
		} else {
			log.info("else");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			RequestContext.getCurrentInstance().execute(
					"document.getElementById('art_1_input').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
		RequestContext.getCurrentInstance().update("dataList");
		RequestContext.getCurrentInstance().update(UPDATE_CAMPO_ARTICULO);
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		RequestContext.getCurrentInstance().update("retefuentelFact");
		RequestContext.getCurrentInstance().update("gravadoFact");
		return "";
	}

	public List<Documento> getListaDocumento() throws ParseException {
		if (listaDocumento == null) {
			List<Long> tipoDocumentoId = new ArrayList<>();
			tipoDocumentoId.add(2l); // tipo documento factura de salida
			tipoDocumentoId.add(6l); // tipo documento factura de salida
			Usuario usuario = usuario();
			listaDocumento = documentoService.getDocNoImp(usuario.getUsuarioId(), tipoDocumentoId, 1l);
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

	public Boolean getProductosBorrar() {
		return productosBorrar;
	}

	public void setProductosBorrar(Boolean productosBorrar) {
		this.productosBorrar = productosBorrar;
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

	public List<TipoDocumento> getTipoDocumentos() {
		tipoDocumentos = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		ids.add(1l);// entradas por remision
		ids.add(2l);// entrada de almacen
		ids.add(6l);// salidas de almacen
		tipoDocumentos = tipoDocumentoService.getById(ids);
		return tipoDocumentos;
	}

	public void setTipoDocumentos(List<TipoDocumento> tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
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

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Double getUnidad() {
		return Unidad;
	}

	public void setUnidad(Double unidad) {
		Unidad = unidad;
	}

	public Double getParcial() {
		return parcial;
	}

	public void setParcial(Double parcial) {
		this.parcial = parcial;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getCrear() {
		return crear;
	}

	public void setCrear(String crear) {
		this.crear = crear;
	}

	public String getCrearNew() {
		return crearNew;
	}

	public void setCrearNew(String crearNew) {
		this.crearNew = crearNew;
	}

	public BigInteger getCodigoNew() {
		if (codigoNew == null) {
			codigoNew = productoService.getByUltimoId();
		}
		return codigoNew;
	}

	public void setCodigoNew(BigInteger codigoNew) {
		this.codigoNew = codigoNew;
	}

	public BigDecimal getCodigoEdit() {
		return codigoEdit;
	}

	public void setCodigoEdit(BigDecimal codigoEdit) {
		this.codigoEdit = codigoEdit;
	}

	public String getArticuloNew() {
		return articuloNew;
	}

	public void setArticuloNew(String articuloNew) {
		this.articuloNew = articuloNew;
	}

	public Double getPublicoNew() {
		return publicoNew;
	}

	public void setPublicoNew(Double publicoNew) {
		this.publicoNew = publicoNew;
	}

	public Double getIvaNew() {
		if (ivaNew == null) {
			ivaNew = 19.0;
		}
		return ivaNew;
	}

	public void setIvaNew(Double ivaNew) {
		this.ivaNew = ivaNew;
	}

	public Double getHipoconsumo() {
		if (hipoconsumoNew == null) {
			hipoconsumoNew = 0.0;
		}
		return hipoconsumoNew;
	}

	public void setHipoconsumo(Double hipoconsumoNew) {
		this.hipoconsumoNew = hipoconsumoNew;
	}

	public Long getStockMinNew() {
		return stockMinNew;
	}

	public void setStockMinNew(Long stockMinNew) {
		this.stockMinNew = stockMinNew;
	}

	public Long getStockMaxNew() {
		return stockMaxNew;
	}

	public void setStockMaxNew(Long stockMaxNew) {
		this.stockMaxNew = stockMaxNew;
	}

	public Grupo getGrupoNew() {
		return grupoNew;
	}

	public void setGrupoNew(Grupo grupoNew) {
		this.grupoNew = grupoNew;
	}

	public Marca getMarcaNew() {
		return marcaNew;
	}

	public void setMarcaNew(Marca marcaNew) {
		this.marcaNew = marcaNew;
	}

	public Proveedor getProveedorNew() {
		return proveedorNew;
	}

	public void setProveedorNew(Proveedor proveedorNew) {
		this.proveedorNew = proveedorNew;
	}

	public String getCodigoBarrasNew() {
		return codigoBarrasNew;
	}

	public void setCodigoBarrasNew(String codigoBarrasNew) {
		this.codigoBarrasNew = codigoBarrasNew;
	}

	public Double getPesoKgNew() {
		return pesoKgNew;
	}

	public void setPesoKgNew(Double pesoKgNew) {
		this.pesoKgNew = pesoKgNew;
	}
	
	public Double getPesoEmpaqueKgNew() {
		return pesoEmpaqueKgNew;
	}

	public void setPesoEmpaqueKgNew(Double pesoEmpaqueKgNew) {
		this.pesoEmpaqueKgNew = pesoEmpaqueKgNew;
	}

	public String getUnidadNew() {
		return unidadNew;
	}

	public void setUnidadNew(String unidadNew) {
		this.unidadNew = unidadNew;
	}

	public Double getCostoNew() {
		return costoNew;
	}

	public void setCostoNew(Double costoNew) {
		this.costoNew = costoNew;
	}

	public Date getFechaIngreso() {
		if (fechaIngreso == null) {
			fechaIngreso = new Date();
		}
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public String getValanzaNew() {
		return valanzaNew;
	}

	public void setValanzaNew(String valanzaNew) {
		this.valanzaNew = valanzaNew;
	}

	public List<ProductoEmpresa> getProductosAll() {
		productosAll = productoService.getProductoByEmpresa(getEmpresa().getEmpresaId());

		return productosAll;
	}

	public void setProductosAll(List<ProductoEmpresa> productosAll) {
		this.productosAll = productosAll;
	}

	public List<Proveedor> getProveedoresAll() {
		if (proveedoresAll == null || proveedoresAll.isEmpty()) {
			proveedoresAll = proveedorService.getByAll();
		}
		return proveedoresAll;
	}

	public void setProveedorestosAll(List<Proveedor> proveedoresAll) {
		this.proveedoresAll = proveedoresAll;
	}

	public List<Grupo> getGruposAll() {
		if (gruposAll == null || gruposAll.isEmpty()) {
			gruposAll = grupoService.getByAll();
		}
		return gruposAll;
	}

	public void setGruposAll(List<Grupo> gruposAll) {
		this.gruposAll = gruposAll;
	}

	public List<Marca> getMarcasAll() {
		if (marcasAll == null || marcasAll.isEmpty()) {
			marcasAll = marcaService.getByAll();
		}
		return marcasAll;
	}

	public void setMarcasAll(List<Marca> marcasAll) {
		this.marcasAll = marcasAll;
	}

	public Producto getProductoEdict() {
		return productoEdict;
	}

	public void setProductoEdict(Producto productoEdict) {
		this.productoEdict = productoEdict;
	}

	public Date getFechaEdit() {
		return fechaEdit;
	}

	public void setFechaEdit(Date fechaEdit) {
		this.fechaEdit = fechaEdit;
	}

	public String getEditarNew() {
		return editarNew;
	}

	public void setEditarNew(String editarNew) {
		this.editarNew = editarNew;
	}

	public String getDetalle() {
		return Detalle;
	}

	public void setDetalle(String detalle) {
		Detalle = detalle;
	}

	public Long getCodigoProveedor() {
		return codigoProveedor;
	}

	public void setCodigoProveedor(Long codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}

	public String getTipoDocumentoEntrada() {
		return tipoDocumentoEntrada;
	}

	public void setTipoDocumentoEntrada(String tipoDocumentoEntrada) {
		this.tipoDocumentoEntrada = tipoDocumentoEntrada;
	}

	public String getIdentificacionProveedor() {
		return identificacionProveedor;
	}

	public void setIdentificacionProveedor(String identificacionProveedor) {
		this.identificacionProveedor = identificacionProveedor;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Double getExecento() {
		return execento;
	}

	public void setExecento(Double execento) {
		this.execento = execento;
	}

	public String getVariosNew() {
		return variosNew;
	}

	public void setVariosNew(String variosNew) {
		this.variosNew = variosNew;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public String getParciaPopup() {
		return parciaPopup;
	}

	public void setParciaPopup(String parciaPopup) {
		this.parciaPopup = parciaPopup;
	}

	public Producto getProductoSelect() {
		return productoSelect;
	}

	public void setProductoSelect(Producto productoSelect) {
		this.productoSelect = productoSelect;
	}

	public Double getGravado() {
		return gravado;
	}

	public void setGravado(Double gravado) {
		this.gravado = gravado;
	}

	public String getCambioPrecio() {
		cambioPrecio = (OpcionUsuario) sessionMap.get("cambioPrecio");
		return cambioPrecio == null ? "false" : "true";
	}

	public void setCambioPrecio(OpcionUsuario cambioPrecio) {
		this.cambioPrecio = cambioPrecio;
	}

	public String getImpresion() {
		return impresion;
	}

	public void setImpresion(String impresion) {
		this.impresion = impresion;
	}

	public Double getRetefuente() {
		return retefuente;
	}

	public void setRetefuente(Double retefuente) {
		this.retefuente = retefuente;
	}

	public String getBuscar() {
		return buscar;
	}

	public void setBuscar(String buscar) {
		this.buscar = buscar;
	}

	public String getCartera() {
		return cartera;
	}

	public void setCartera(String cartera) {
		this.cartera = cartera;
	}

	public String getNombreProveedor() {
		return nombreProveedor;
	}

	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}
	
	
}
