package com.fact.beam;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.fact.api.Calculos;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Evento;
import com.fact.model.Grupo;
import com.fact.model.Marca;
import com.fact.model.OpcionUsuario;
import com.fact.model.Producto;
import com.fact.model.Proveedor;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoEvento;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.GrupoService;
import com.fact.service.MarcaService;
import com.fact.service.ProductoService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoDocumentoService;
import com.fact.service.UsuarioService;
import com.fact.vo.DocumentoDetalleVo;

@ManagedBean
@SessionScoped
public class MovimientoMes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2599894690114718736L;

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

	
	private List<TipoDocumento> tipoDocumentos;
	List<DocumentoDetalleVo> productos;
	List<Producto> productosAll;
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
	Date fechaCreacion;
	Proveedor proveedor;
	

	

	String codigoInterno;
	Producto articulo;
	String codigoBarras;
	Double cantidad;
	Double Unidad;
	Long Parcial;
	String Detalle;

	String focus = "";
	String crear;
	String crearNew;

	// variables nuevo producto
	BigDecimal codigoNew;
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
	Long codigoBarrasNew;
	Double pesoKgNew;
	String unidadNew;
	String valanzaNew;
	String variosNew;
	String subProductoNew;
	//
	
	public String getSubProductoNew() {
		return subProductoNew;
	}

	public void setSubProductoNew(String subProductoNew) {
		this.subProductoNew = subProductoNew;
	}

	// variables producto edit producto
	Producto productoEdict;
	BigDecimal codigoEdit;
	Date fechaEdit;
	String editarNew;
	
	//total
	Double iva;
	Double execento;
	Double total;
	
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
			
	// factura siguiente y anterior
	List<Documento> listaDocumento;
	Documento documentoActual;
	
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	
	
	public List<Producto> completeText(String query) {
		List<Producto> nombProductos = new ArrayList<>();
		for (Producto p : getProductosAll()) {
			if(p.getNombre()!=null){
				String articul = p.getNombre().toUpperCase().trim();
				//if (articul.indexOf(query.toUpperCase()) != -1) {
				if (articul.startsWith(query.toUpperCase().trim())) {					 
					nombProductos.add(p);
				}
			}
		}
		Configuracion configuracion = configuracion();
		Long server = configuracion.getServer();
		if(server==2 && !nombProductos.isEmpty()){
			return cantidadproductoServer2(nombProductos,server);
		}
		return nombProductos;
	}
	
	/**
	 * Metodo que busca productos en el server 2 y actualiza la cantidad
	 * @param nombProductos
	 * @return
	 */
	private List<Producto> cantidadproductoServer2(List<Producto> nombProductos,Long server){
		try {
			List<Producto> nombProductos2= productoService.getByList(nombProductos,server);
			for(Producto p: nombProductos){
				String producto1 =p.getProductoId().toString();
				for(Producto p2: nombProductos2){
					String producto2 =p2.getProductoId().toString();
					if(producto1.equals(producto2)){
					 Double cantidadNew = p.getCantidad()+p2.getCantidad();
					 p.setCantidad(cantidadNew);
					 break;
					}
				}		
			}
		} catch (Exception e) {
			System.out.println("no found product server thow");
			return nombProductos;
		}		
		return nombProductos;
	}
	
	public List<Proveedor> completeTextProveedor(String query) {
		List<Proveedor> nombProveedores = new ArrayList<>();
		for (Proveedor p : getProveedoresAll()) {
			if(p.getNombre()!=null){
				String articul = p.getNombre().toUpperCase().trim();
				//if (articul.indexOf(query.toUpperCase()) != -1) {
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
			if(g.getNombre()!=null){
				String articul = g.getNombre().toUpperCase().trim();
				if (articul.indexOf(query.toUpperCase()) != -1) {
//				if (articul.startsWith(query.toUpperCase().trim())) {					 
					nombGrupos.add(g);
				}
			}
		}
		return nombGrupos;
	}
	
	public List<Marca> completeTextMarca(String query) {
		List<Marca> nombMarcas = new ArrayList<>();
		for (Marca p : getMarcasAll()) {
			if(p.getNombre()!=null){
				String articul = p.getNombre().toUpperCase().trim();
				//if (articul.indexOf(query.toUpperCase()) != -1) {
				if (articul.startsWith(query.toUpperCase().trim())) {					 
					nombMarcas.add(p);
				}
			}
		}
		return nombMarcas;
	}

	public List<String> completeCodigo(String query) {
		List<String> codProductos = new ArrayList<String>();
		for (Producto p : getProductosAll()) {
			if (p.getProductoId() != null) {
				String articul = p.getProductoId().toString();
				if (articul.indexOf(query) != -1) {
					codProductos.add(articul);
				}
			}
		}
		return codProductos;
	}

	public String buscarProductoCodBarras(AjaxBehaviorEvent event) {
		String completo = getCodigoBarras();
		for (Producto p : getProductosAll()) {
			if(p.getCodigoBarras()!=null){
			if (completo.equals(p.getCodigoBarras().toString())) {
				setCodigoInterno(p.getProductoId().toString());
				setArticulo(p);
				setUnidad(p.getCosto());
				productoSelect = p;
				// if(p.getBalanza()!=null && p.getBalanza()==1l){
				RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').focus();");
				RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').select();");
				RequestContext.getCurrentInstance().update("art_1");
				RequestContext.getCurrentInstance().update("art_1_input");
				RequestContext.getCurrentInstance().update("cod_");
				// setFocus("cantidad_in");
				// }else{
				// setFocus("unidad_");
				// RequestContext.getCurrentInstance().execute("document.getElementById('unidad_')addEventListener('focus',
				// ponerFondoAmarillo);");
				// RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').focus();");

				// }
				break;
			}
			}
		}
		return "";
	}

	public void buscarProducto(SelectEvent event) {	
		productoSelect= (Producto) event.getObject();
	}
	
	public void detalleEnDocumento(AjaxBehaviorEvent event){
		getDocumento().setDetalleEntrada(getDetalle());
	}
	
	public void buscarProveedor(SelectEvent event) throws IOException {
		
		proveedorSelect= (Proveedor) event.getObject();
		long server =1;
		System.out.println("proveedor select:"+ proveedorSelect.getNombre());
		setCodigoProveedor(proveedorSelect.getProveedorId());
		setTipoDocumentoEntrada(getDocumento().getTipoDocumentoId().getNombreCorto());
		setIdentificacionProveedor(proveedorSelect.getDocumento());
		setFechaCreacion(new Date());
		getDocumento().setProveedorId(proveedorSelect);
		getDocumento().setDetalleEntrada(getDetalle());
		documentoService.update(getDocumento(),server);
		RequestContext.getCurrentInstance().execute("document.getElementById('busquedaCodBarras').style.display='inline';");// se
		RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='inline';"); // campos
		RequestContext.getCurrentInstance().execute("entrarFactura();");// art_1_input
	}

	public void buscarProductoCodigo(SelectEvent event) {
		String completo = event.getObject().toString();
		for (Producto p : getProductosAll()) {
			if (p.getProductoId().toString().contains(completo)) {
				setCodigoInterno(p.getProductoId().toString());
				setArticulo(p);
				setUnidad(p.getCosto());
				productoSelect = p;
				break;
			}
		}
		event.getObject().toString();
	}
	
	public Configuracion configuracion(){		
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}

	public String selectedTipoDoc(TipoDocumento td) {
		Documento docOjb = new Documento();
		docOjb.setTipoDocumentoId(td);
		docOjb.setFechaRegistro(new Date());
		Usuario usuario = (Usuario) sessionMap.get("userLogin");
		Configuracion configuracion = configuracion();
		Long server=configuracion.getServer();
		if(td.getTipoDocumentoId()==1l && server==2l){ //si es una entrada de almacen se guarda el en el server 2
			server=2l;
			System.out.println("oracle_2");
		}else{
			server=1l;
		}
		docOjb.setUsuarioId(usuario);
		Proveedor pro = new Proveedor();
		pro.setProveedorId(1l);
		docOjb.setProveedorId(pro);
		documentoService.save(docOjb,server);
		setDocumento(docOjb);
		setTipoDocumentoEntrada(td.getNombre());
		RequestContext.getCurrentInstance().execute("document.getElementById('tipo_documento').style.display='none';"); // opciones   de  tipo documento
		RequestContext.getCurrentInstance().execute("document.getElementById('op_mov_mes_content').style.display='none';");// opciones
		//RequestContext.getCurrentInstance().execute("document.getElementById('busquedaCodBarras').style.display='inline';");// se
		//RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='inline';"); // campos
		//RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input').focus();");// art_1_input
		RequestContext.getCurrentInstance().execute("document.getElementById('detalleEntrada').style.display='inline';"); 
		RequestContext.getCurrentInstance().execute("document.getElementById('detalleEntrada').focus();");
		RequestContext.getCurrentInstance().execute("pagina='opcNuevo';");
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
		productoSelect=null;
		actModFactura = Boolean.FALSE;
		RequestContext.getCurrentInstance().update("detalleEntrada");
		RequestContext.getCurrentInstance().update("codig");
		RequestContext.getCurrentInstance().update("nombcop_mov_mes");
		RequestContext.getCurrentInstance().update("art_1");
		RequestContext.getCurrentInstance().update("nombc");	
		RequestContext.getCurrentInstance().update("cod_");
		RequestContext.getCurrentInstance().update("cantidad_in");
		RequestContext.getCurrentInstance().update("unidad_");
		RequestContext.getCurrentInstance().update("dataList");
		RequestContext.getCurrentInstance().update("codBarras_input");	
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");	
		RequestContext.getCurrentInstance().execute("document.getElementById('prod').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('prodList').style.display='none';");
		return "";
	}

	public String cantidadEnter(AjaxBehaviorEvent event) {
		if (getCantidad() != null) {
			Double  cantidad = 0.0;
			Configuracion configuracion = configuracion();
			Long server=configuracion.getServer();
			if(getDocumento().getTipoDocumentoId().getTipoDocumentoId()==1l && server==2l){ //si es una entrada de almacen se guarda el en el server 2
				server=2l;
				//System.out.println("oracle_2");
			}else{
				server=1l;
			}
			
			System.out.println("dio enter en cantidad");
			DocumentoDetalle docDetalle = new DocumentoDetalle();
			DocumentoDetalleVo docDetalleVo = new DocumentoDetalleVo();
			docDetalle.setCantidad(getCantidad());
			docDetalle.setProductoId(productoSelect);
			docDetalle.setDocumentoId(getDocumento());
			Date fecha = new Date();
			docDetalle.setFechaRegistro(fecha);
			docDetalle.setEstado(1l);
			docDetalle.setParcial(getCantidad()*productoSelect.getCosto());
			documentoDetalleService.save(docDetalle,server);
			Producto productEdit =  new Producto();
			productEdit=productoSelect;
			cantidad =productoSelect.getCantidad()==null?0.0:productoSelect.getCantidad().doubleValue();
			System.out.println("cantidad actual:"+cantidad);
			OpcionUsuario stock=new OpcionUsuario();
			switch (getDocumento().getTipoDocumentoId().getTipoDocumentoId().toString()) {
			case "6": 	//tipo documento igual a salida de almacen
				System.out.println("salida de almacen");
				cantidad = cantidad-getCantidad();
				System.out.println("cantidad actualizada:"+cantidad);
				break;
			case "2" ://tipo documento igual a entrada de almacen
				cantidad = cantidad+getCantidad();
				stock = (OpcionUsuario) sessionMap.get("stock");
				if(stock!=null && productoSelect.getStockMax()!=null){
					Double cantidadTempo = productoSelect.getCantidad()==null?0.0:productoSelect.getCantidad();
					if(cantidadTempo>productoSelect.getStockMax()){
						System.out.println("entro");
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lacantidad max sugerida para "+productoSelect.getNombre()+" "+productoSelect.getStockMax() ));				
					}		
				}
				RequestContext.getCurrentInstance().update("growl");
				System.out.println("entrada de almacen");
				System.out.println("cantidad actualizada:"+cantidad);
				break;
			case "1" ://tipo documento igual a entrada de almacen
				cantidad = cantidad+getCantidad();
				stock = (OpcionUsuario) sessionMap.get("stock");
				if(stock!=null && productoSelect.getStockMax()!=null){
					if(productoSelect.getCantidad()>productoSelect.getStockMax()){
						System.out.println("entro");
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lacantidad max sugerida para "+productoSelect.getNombre()+" "+productoSelect.getStockMax() ));				
					}		
				}
				RequestContext.getCurrentInstance().update("growl");
				System.out.println("entrada de almacen");
				System.out.println("cantidad actualizada:"+cantidad);
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
				evento.setValorActual("" + cantidad);
				evento.setValorAnterior("" + productoSelect.getCantidad());
				eventoService.save(evento);
			
			
			productEdit.setCantidad(cantidad);
			productoService.update(productEdit,server);
			docDetalleVo.setCantidad(getCantidad());
			docDetalleVo.setProductoId(productoSelect);
			docDetalleVo.setDocumentoId(getDocumento());
			docDetalleVo.setFechaRegistro(fecha);
			if (getCantidad() != null && productoSelect.getCosto() != null) {
				docDetalleVo.setParcial(getCantidad() * productoSelect.getCosto());
			} else {
				docDetalleVo.setParcial(0.0);
			}
			if(getCantidad()>0 ){
				getProductos().add(docDetalleVo);
			}
			Calculos.calcularExcento(getDocumento(), getProductos());// en esta funcion de calcula el excento, iva, total
			documentoService.update(getDocumento(),server);		
			RequestContext.getCurrentInstance().execute("document.getElementById('prodList').style.display='inline';"); // campos// del
																														// nuevo																												// producto
			setArticulo(null);
			setCantidad(null);
			setCodigoInterno(null);
			setCodigoBarras(null);
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').value='';");
			RequestContext.getCurrentInstance().update("prodList");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('busquedaCodBarras').style.display='inline';");
			RequestContext.getCurrentInstance().update("codBarras_input");
			RequestContext.getCurrentInstance().update("busquedaCodBarras");
			RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('codBarras_input').select();");
			

		}else{
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').select();");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
		}
		return "";
	}
	

	public void crearProducto(AjaxBehaviorEvent event) {
		if(getCrear().toUpperCase().equals("S")){
			setArticuloNew("");
			RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProductoForm:articuloNew').value='';");
			RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProductoForm:articuloNew').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProductoForm:articuloNew').select();");
			RequestContext.getCurrentInstance().execute("PF('nuevoProducto').show();");			
			RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProductoForm:articuloNew').className+=' state-focus';");
			RequestContext.getCurrentInstance().execute("pagina='crearProducto';");
			//faltan los valores por defecto del formulario de guardar
			setCrearNew("N");
			System.out.println("presiono s entra popup creacion crear");
		}else{
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			System.out.println("presiono ota cosa no entra al formulario de crear");
		}		
		setCrear("N");
	}
		
	public void crearNewProducto() {
		String crearN = getCrearNew().toUpperCase();
		Configuracion configuracion = configuracion();
		Long server=configuracion.getServer();
		
		if (crearN.equals("S")) {
			//RequestContext.getCurrentInstance().execute("alert('entra al if')");
			Producto prodNew = new Producto();
			prodNew.setProductoId(getCodigoNew().longValue());
			prodNew.setUnidad(getUnidadNew());
			if(getCodigoBarrasNew()!=null){
				Producto p =productoService.getByCodigoBarras(getCodigoBarrasNew());
				if(p!=null){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El Producto Con código de barras "+ getCodigoBarrasNew()+" ya se encuentra registrado"));
					return;
				}
			}
			if (getValanzaNew() != null) {
				prodNew.setBalanza(getValanzaNew().toUpperCase().equals("S")? 1l : 0l);
			}else{
				prodNew.setBalanza(0l);
			}
			if (getVariosNew() != null) {
				prodNew.setVarios(getVariosNew().toUpperCase().equals("S")? 1l : 0l);
			}else{
				prodNew.setVarios(0l);
			}
			if (getSubProductoNew() != null) {
				prodNew.setSubProducto(getSubProductoNew().toUpperCase().equals("S")? 1l : 0l);
			}else{
				prodNew.setSubProducto(0l);;
			}
			
			prodNew.setCodigoBarras(getCodigoBarrasNew());
			if(getCostoNew()!=null){
				prodNew.setCosto(getCostoNew());
			}else{
				prodNew.setCosto(0.0);
			}
			if(getPublicoNew()!=null){
				prodNew.setCostoPublico(getPublicoNew());
			}else{
				prodNew.setCostoPublico(0.0);
			}
			prodNew.setFechaRegistro(new Date());
			if(getIvaNew()!=null){
				prodNew.setIva(getIvaNew());
			}else{
				prodNew.setIva(0.0);
			}
			if(getHipoconsumo()!=null){
				prodNew.setHipoconsumo(getHipoconsumo());
			}else{
				prodNew.setHipoconsumo(0.0);
			}
			if(getProveedorNew()!=null ){
				prodNew.setProveedorId(getProveedorNew());
			}
			if(getGrupoNew()!=null ){
				prodNew.setGrupoId(getGrupoNew());
			}
			if(getMarcaNew()!=null){
				prodNew.setMarcaId(getMarcaNew());
			}
			prodNew.setNombre(getArticuloNew().toUpperCase().trim());
			if(getStockMaxNew()!=null){
				prodNew.setStockMax(getStockMaxNew());
			}else{
				prodNew.setStockMax(1000000l);
			}
			if(getStockMinNew()!=null){
				prodNew.setStockMin(getStockMinNew());
			}else{
				prodNew.setStockMin(-1000000l);
			}
			prodNew.setPeso(getPesoKgNew());
			prodNew.setEstado(1l);	
			prodNew.setCantidad(0.0);
			productoService.save(prodNew,1l);
			if(server==2l){
				productoService.save(prodNew,server);
			}
			setCodigoNew(null);
			productoSelect = prodNew;
			setCodigoInterno(prodNew.getProductoId().toString());
			setArticulo(prodNew);
			setUnidad(prodNew.getCosto());
			getProductosAll().add(productoSelect);
			//setCantidad(0l);
			RequestContext.getCurrentInstance().update("art_1");
			RequestContext.getCurrentInstance().update("art_1_input");
			RequestContext.getCurrentInstance().update("cod_");
			RequestContext.getCurrentInstance().update("cantidad_in");
			
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').select();");
			RequestContext.getCurrentInstance().execute("document.getElementById('cantidad_in').className='ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Creado exitosamente"));
		} else {
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
		}
		setCrearNew("N");		
		RequestContext.getCurrentInstance().execute("pagina='opcNuevo';");
		RequestContext.getCurrentInstance().execute("document.getElementById('deseaGuardar').style.display='none';");
		RequestContext.getCurrentInstance().execute("document.getElementById('nuevoProducto').style.display='none';");
	}
	
	public String editarNewProducto(AjaxBehaviorEvent event) {
		String crearN = getEditarNew().toUpperCase();
		if (crearN.equals("S")) {
			
			String nombre = getProductoEdict().getNombre().trim();
			getProductoEdict().setNombre(nombre);
			Producto prodNew = getProductoEdict();
			if(getProductoEdict().getProductoId()==1l){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No es posible editar el producto ¡Varios!"));	
				return "";	
			}
			
			//se existe un cambio de precio se registra el evento
			if(getPublicoNew()!=null && getPublicoNew().equals(getProductoEdict().getCostoPublico())){
				Evento evento = new Evento();
				TipoEvento tipoEvento= new TipoEvento();
				tipoEvento.setTipoEventoId(3l); // se envia tipo evento igual a cambio de precio
				evento.setTipoEventoId(tipoEvento);
				evento.setFechaRegistro(new Date());
				evento.setUsuarioId(usuario());
				evento.setValorActual(""+getPublicoNew());
				evento.setValorAnterior(""+getProductoEdict().getCostoPublico());
				eventoService.save(evento);
			}
			
			if (getValanzaNew() != null) {
				prodNew.setBalanza(getValanzaNew().equalsIgnoreCase("N") ? 0l : 1l);
				if(getValanzaNew().equalsIgnoreCase("S")){
					setUnidadNew("N");
				}else{
					setUnidadNew("S");
				}
			}else{
				prodNew.setBalanza(0l);
				setUnidadNew("S");
			}
			
			if (getVariosNew() != null) {
				prodNew.setVarios((getVariosNew().toUpperCase().equals("N") ? 0l : 1l));
			}else{
				prodNew.setVarios(0l);
			}
			
			if (getUnidadNew() != null) {
				prodNew.setUnidad(getUnidadNew().toUpperCase().equals("N") ? "N" : "S");
			}else{
				prodNew.setUnidad("N");
			}
			prodNew.setCodigoBarras(getCodigoBarrasNew());
			if(getCostoNew()!=null){
				prodNew.setCosto(getCostoNew());
			}else{
				prodNew.setCosto(0.0);
			}
			if(getPublicoNew()!=null){
				prodNew.setCostoPublico(getPublicoNew());
			}else{
				prodNew.setCostoPublico(0.0);
			}
			prodNew.setFechaActualiza(getFechaEdit());
			if(getIvaNew()!=null){
				prodNew.setIva(getIvaNew());
			}else{
				prodNew.setIva(0.0);
			}
			if(getHipoconsumo()!=null){
				prodNew.setHipoconsumo(getHipoconsumo());
			}else{
				prodNew.setHipoconsumo(0.0);
			}
			if(getGrupoNew()!=null){
				prodNew.setGrupoId(getGrupoNew());
			}
			prodNew.setMarcaId(getMarcaNew());
			prodNew.setPeso(getPesoKgNew());
			prodNew.setStockMax(getStockMaxNew());
			prodNew.setStockMin(getStockMinNew());
			prodNew.setEstado(1l);
			Configuracion configuracion = configuracion();
			Long server = configuracion.getServer();
			productoService.update(prodNew,1l);
			if(server==2l){
				productoService.update(prodNew,2l);
			}
			RequestContext.getCurrentInstance().execute("PF('info_articulos').hide();");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto editado Exitosamente"));		
			limpiarEditar();
		} else {
			RequestContext.getCurrentInstance().execute("PF('info_articulos').hide();");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto NO editado"));	
		}
		RequestContext.getCurrentInstance().execute("document.getElementById('opciones:Sig_movi_mes1').focus();");
		return "";
	}
	
	public void asignarGrupo(SelectEvent event) {
		Grupo pr= (Grupo) event.getObject();
		setGrupoNew(pr);
		System.out.println("grupo asignado:"+pr.getNombre());
	}

	public void editarProducto(SelectEvent event) {
		Producto pr= (Producto) event.getObject();
		setProductoEdict(pr);
		setCodigoEdit(new BigDecimal(pr.getProductoId()));
		setFechaEdit(pr.getFechaRegistro());
		setCostoNew(pr.getCosto());
		setPublicoNew(pr.getCostoPublico());
		setIvaNew(pr.getIva());
		setStockMaxNew(pr.getStockMax());
		setStockMinNew(pr.getStockMin());
		
		if(pr.getGrupoId()!=null){
			pr.setGrupoId(grupoService.getById(pr.getGrupoId().getGrupoId()));
			setGrupoNew(pr.getGrupoId());
		}
		
		setMarcaNew(pr.getMarcaId());
		//setProveedorNew(set);
		setCodigoBarrasNew(pr.getCodigoBarras());
		setPesoKgNew(pr.getPeso());
		//setEditarNew("S");
		if(pr.getUnidad()==null){
			setUnidadNew("N");
		}else{
			setUnidadNew(pr.getUnidad().toUpperCase());		
		}	
		if(pr.getBalanza()==null){
			setValanzaNew("N");
		}else{
			setValanzaNew(pr.getBalanza()==1l?"S":"N");
		}	
		if(pr.getVarios()==null){
			setVariosNew("N");
		}else{
			setVariosNew(pr.getBalanza()==1l?"S":"N");
		}	
	}
	
	public void limpiarEditar(){
		//if("info_articulos".equals(bar)){
			System.out.println("entra a limpiar editar: ");
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
			//setProveedorNew(set);
			setCodigoBarrasNew(null);
			setPesoKgNew(null);
			setEditarNew(null);
			setUnidadNew(null);	
			setValanzaNew(null);
			setProductosAll(null);
			setVariosNew(null);
	}
	
	public Usuario usuario(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		Usuario yourVariable = (Usuario) sessionMap.get("userLogin");
		return yourVariable;
	}
	
	public void buscarUltimaFactura() {
		System.out.println("buscar ultima factura");
		Documento ultimoFactura;
		Usuario usuario =  usuario();
		Long idFactura = 2l; // id de tipo documento entrada de almacen
		List<DocumentoDetalle> dd = new ArrayList<>();
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		ultimoFactura = documentoService.getByLastAndTipo(idFactura, usuario.getUsuarioId());
		if (ultimoFactura != null) {
			setDocumento(ultimoFactura);
			dd = documentoDetalleService.getByDocumento(ultimoFactura.getDocumentoId(),1l);
			for (DocumentoDetalle d1 : dd) {
				DocumentoDetalleVo vo = new DocumentoDetalleVo();
				vo.setCantidad(d1.getCantidad());
				vo.setDocumentoDetalleId(d1.getDocumentoDetalleId());
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
			//setGravado(ultimoFactura.getGravado());
			// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('dataList_content').style.display='inline';");
			RequestContext.getCurrentInstance().execute("document.getElementById('prodList').style.display='inline';");
			RequestContext.getCurrentInstance().update("cod_");
			RequestContext.getCurrentInstance().update("nombc");		
			RequestContext.getCurrentInstance().update("art_1");
			RequestContext.getCurrentInstance().update("cantidad_in");
			RequestContext.getCurrentInstance().update("dataList");
			RequestContext.getCurrentInstance().update("execentoFact");
			//RequestContext.getCurrentInstance().update("gravado");
			RequestContext.getCurrentInstance().update("ivaFact");
			RequestContext.getCurrentInstance().update("totalFact");
			RequestContext.getCurrentInstance().update("unidad_");
			// falta poner el focus en codigo de barras
			// actualizar los campos y ocultar los que no se ven y mostrar los
			// que se ven
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay Entradas de almacen"));
		}
	}
	
	public void modificarUltimaFactura() {
		System.out.println("buscar ultima factura MM");
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			RequestContext.getCurrentInstance().execute("document.getElementById('confir_mm').style.display='inline';");
			actModFactura = Boolean.TRUE;
		}
		// colocar una variable para saber si esta modificando factura,
	}
	
	public void borrarFactura() {
	    claveBorradoActivo = (OpcionUsuario) sessionMap.get("claveBorradoActivo");
		if (getDocumento() != null && getDocumento().getTipoDocumentoId() != null) {
			if(claveBorradoActivo!=null){
				 RequestContext.getCurrentInstance().execute("PF('modificarFactura').show();");
			}else{
				activarBorrado();
			}
		}
	}
	
	public void activarBorrado(){
		System.out.println("presiona b para borrar factura MM");
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
		DocumentoDetalle dd = new DocumentoDetalle();
		Documento docu = new Documento();
		long server =1;
		dd.setDocumentoDetalleId(d.getDocumentoDetalleId());
		dd.setCantidad(d.getCantidad());
		dd.setDocumentoDetalleId(d.getDocumentoDetalleId());
		dd.setDocumentoId(d.getDocumentoId());
		dd.setFechaRegistro(d.getFechaRegistro());
		dd.setProductoId(d.getProductoId());
		dd.setEstado(0l);
		dd.setParcial(d.getParcial());
		docu = dd.getDocumentoId();
		Double ivaDocu = docu.getIva()==null?0.0:docu.getIva();
		Double ivaProd = (d.getProductoId().getIva() == null ? 0.0 : d.getProductoId().getIva()) / 100;
		ivaDocu = ivaDocu - (ivaProd * d.getParcial());
		Double excentoB = docu.getExcento();
		Double canti = docu.getTotal();
		canti = canti - (d.getParcial());
		double temp = d.getParcial() * ivaProd;
		temp=d.getParcial()-temp;
		excentoB = excentoB - temp;
		docu.setExcento(excentoB);
		docu.setIva(ivaDocu);
		docu.setTotal(canti);
		setTotal(canti);
		setExecento(excentoB);
		setIva(ivaDocu);
		Producto productoEdit = d.getProductoId();
		Double cantidad = productoEdit.getCantidad() - d.getCantidad();
		productoEdit.setCantidad(cantidad);
		productoService.update(productoEdit,1l);
		if(server==2l){
			productoEdit = productoService.getById(d.getProductoId().getProductoId());
		    cantidad = productoEdit.getCantidad() + d.getCantidad2();
			productoEdit.setCantidad(cantidad);
			productoService.update(productoEdit,server);
		}
		documentoDetalleService.update(dd,server);
		documentoService.update(docu,server);
		getProductos().remove(d);
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		RequestContext.getCurrentInstance().update("execentoFact");
		RequestContext.getCurrentInstance().update("gravado");
		RequestContext.getCurrentInstance().update("ivaFact");
		RequestContext.getCurrentInstance().update("totalFact");
		if (!getProductos().isEmpty()) {
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('borrarTablaMM:checkboxDT:0:rowDelete_').focus();");
		}
		RequestContext.getCurrentInstance().update("borrarTablaMM:checkboxDT");
		System.out.println("documentoDetalle borrado:" + d.getDocumentoDetalleId());
	}
	
	public void popupImprimir() {
		if (actModFactura) {
			System.out.println("i en modificar factura");
			// todo lo de modificar, activar cosas y asi....
			
			RequestContext.getCurrentInstance()
					.execute("document.getElementById('busquedaCodBarras').style.display='inline';");
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
			RequestContext.getCurrentInstance().update("art_1");
			RequestContext.getCurrentInstance().update("codBarras_input");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').focus();");
			RequestContext.getCurrentInstance().execute("document.getElementById('art_1_input').select();");
			RequestContext.getCurrentInstance().execute("pagina='borrando_facturaMM';");
			RequestContext.getCurrentInstance().execute("ruta=='movimiento_mes';");
			RequestContext.getCurrentInstance().execute("document.getElementById('confir_mm').style.display='none';");
			actModFactura = Boolean.FALSE;
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El módulo de impresión de movimientos mes esta desactivado, comuniquese con su proveedor del sistema"));
//			System.out.println("imprimir");
//			RequestContext.getCurrentInstance().execute("PF('imprimir').show();");
//			RequestContext.getCurrentInstance().execute("document.getElementById('pogoTargeta').value='N';");
//			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').value='N';");
//			RequestContext.getCurrentInstance().execute(
//					"document.getElementById('cartera').className=' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus';");
//			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').focus();");
//			RequestContext.getCurrentInstance().execute("document.getElementById('cartera').select();");
//			setCartera("N");
//			RequestContext.getCurrentInstance().update("excento_tag");
//			RequestContext.getCurrentInstance().update("cartera");
//			RequestContext.getCurrentInstance().update("gravado_tag");
//			RequestContext.getCurrentInstance().update("iva_tag");
//			RequestContext.getCurrentInstance().update("total1");
//			RequestContext.getCurrentInstance().update("iva_tag");
//			RequestContext.getCurrentInstance().update("pogoTargeta");
		}
	}
	
	public void siguienteFactura() throws ParseException {
		System.out.println("siguiente_factura_MM");
		List<DocumentoDetalle> dd = new ArrayList<>();
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				System.out.println("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(),1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1.getDocumentoDetalleId());
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
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				//setGravado(getDocumentoActual().getGravado());
				//setNombreCliente2(getDocumentoActual().getClienteId()==null?"VARIOS":getDocumentoActual().getClienteId().getNombre());
				// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('prodList').style.display='inline';");
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");	
				RequestContext.getCurrentInstance().update("art_1");
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("unidad_");
				//RequestContext.getCurrentInstance().update("nombreCliente2");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		} else {
			Integer pos = getListaDocumento().indexOf(getDocumentoActual());
			if (pos > 0) {
				setDocumentoActual(getListaDocumento().get(pos - 1));
				System.out.println("documento actual: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(),1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1.getDocumentoDetalleId());
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setProductos(ddVo);
				setTotal(getDocumentoActual().getTotal());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				//setGravado(getDocumentoActual().getGravado());
				//setNombreCliente2(getDocumentoActual().getClienteId()==null?"VARIOS":getDocumentoActual().getClienteId().getNombre());
				// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('prodList').style.display='inline';");
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");	
				RequestContext.getCurrentInstance().update("art_1");
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("unidad_");
				//RequestContext.getCurrentInstance().update("nombreCliente2");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}

		}
	}
	
	public void anteriorFactura() throws ParseException {
		System.out.println("siguiente_factura_MM");
		List<DocumentoDetalle> dd = new ArrayList<>();
		List<DocumentoDetalleVo> ddVo = new ArrayList<>();
		if (getDocumentoActual() == null) {
			if (!getListaDocumento().isEmpty()) {
				setDocumentoActual(getListaDocumento().get(getListaDocumento().size() - 1));
				System.out.println("documento actual MM: " + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(),1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1.getDocumentoDetalleId());
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setProductos(ddVo);
				setTotal(getDocumentoActual().getTotal());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				//setGravado(getDocumentoActual().getGravado());
				//setNombreCliente2(getDocumentoActual().getClienteId()==null?"VARIOS":getDocumentoActual().getClienteId().getNombre());
				// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('prodList').style.display='inline';");
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("nombc");	
				RequestContext.getCurrentInstance().update("art_1");
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("unidad_");
				//RequestContext.getCurrentInstance().update("nombreCliente2");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}
		} else {
			Integer pos = getListaDocumento().indexOf(getDocumentoActual());
			if (pos < getListaDocumento().size() - 1) {
				setDocumentoActual(getListaDocumento().get(pos + 1));
				System.out.println("documento actual: MM" + getDocumentoActual().getDocumentoId());
				setDocumento(getDocumentoActual());
				dd = documentoDetalleService.getByDocumento(getDocumentoActual().getDocumentoId(),1l);
				for (DocumentoDetalle d1 : dd) {
					DocumentoDetalleVo vo = new DocumentoDetalleVo();
					vo.setCantidad(d1.getCantidad());
					vo.setDocumentoDetalleId(d1.getDocumentoDetalleId());
					vo.setDocumentoId(d1.getDocumentoId());
					vo.setFechaRegistro(d1.getFechaRegistro());
					vo.setParcial(d1.getParcial());
					d1.getProductoId().setCostoPublico(vo.getParcial() / vo.getCantidad());
					vo.setProductoId(d1.getProductoId());
					ddVo.add(vo);
				}
				setProductos(ddVo);
				setTipoDocumentoEntrada(getDocumentoActual().getTipoDocumentoId().getNombre());
				setTotal(getDocumentoActual().getTotal());
				setExecento(getDocumentoActual().getExcento());
				setIva(getDocumentoActual().getIva());
				//setGravado(getDocumentoActual().getGravado());
				//setNombreCliente2(getDocumentoActual().getClienteId()==null?"VARIOS":getDocumentoActual().getClienteId().getNombre());
				// RequestContext.getCurrentInstance().execute("document.getElementById('opciones:op_mov_mes1_content').style.display='none';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('dataList_content').style.display='inline';");
				RequestContext.getCurrentInstance()
						.execute("document.getElementById('prodList').style.display='inline';");
				RequestContext.getCurrentInstance().update("cod_");
				RequestContext.getCurrentInstance().update("art_1");
				RequestContext.getCurrentInstance().update("nombc");	
				RequestContext.getCurrentInstance().update("cantidad_in");
				RequestContext.getCurrentInstance().update("dataList");
				RequestContext.getCurrentInstance().update("execentoFact");
				RequestContext.getCurrentInstance().update("gravado");
				RequestContext.getCurrentInstance().update("ivaFact");
				RequestContext.getCurrentInstance().update("totalFact");
				RequestContext.getCurrentInstance().update("unidad_1");
				//RequestContext.getCurrentInstance().update("nombreCliente2");
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay facturas Disponibles"));
			}

		}
	}
	
	public List<Documento> getListaDocumento() throws ParseException {
		if (listaDocumento == null) {
			List<Long> tipoDocumentoId = new ArrayList<>();
			tipoDocumentoId.add(2l); // tipo documento factura de salida		
			Usuario usuario =  usuario();
			listaDocumento = documentoService.getDocNoImp(usuario.getUsuarioId(),tipoDocumentoId,1l);
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
		List<Long> ids= new ArrayList<>();
		ids.add(1l);//entradas por remision
		ids.add(2l);//entrada de almacen
		ids.add(6l);//salidas de almacen
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

	public Long getParcial() {
		return Parcial;
	}

	public void setParcial(Long parcial) {
		Parcial = parcial;
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

	public BigDecimal getCodigoNew() {
		if (codigoNew == null) {
			codigoNew = productoService.getByUltimoId();
		}
		return codigoNew;
	}

	public void setCodigoNew(BigDecimal codigoNew) {
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
		if(ivaNew==null){
			ivaNew =19.0;
		}
		return ivaNew;
	}

	public void setIvaNew(Double ivaNew) {
		this.ivaNew = ivaNew;
	}
	
	public Double getHipoconsumo() {
		if(hipoconsumoNew==null){
			hipoconsumoNew =0.0;
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

	public Long getCodigoBarrasNew() {
		return codigoBarrasNew;
	}

	public void setCodigoBarrasNew(Long codigoBarrasNew) {
		this.codigoBarrasNew = codigoBarrasNew;
	}

	public Double getPesoKgNew() {
		return pesoKgNew;
	}

	public void setPesoKgNew(Double pesoKgNew) {
		this.pesoKgNew = pesoKgNew;
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
	
	public  List<Producto> getProductosAll() {
		//if (productosAll == null || productosAll.isEmpty()) {
			productosAll = productoService.getByAll();
		//}
		return productosAll;
	}

	public void setProductosAll(List<Producto> productosAll) {
		this.productosAll = productosAll;
	}
	
	public  List<Proveedor> getProveedoresAll() {
		if (proveedoresAll == null || proveedoresAll.isEmpty()) {
			proveedoresAll = proveedorService.getByAll();
		}
		return proveedoresAll;
	}

	public void setProveedorestosAll(List<Proveedor> proveedoresAll) {
		this.proveedoresAll = proveedoresAll;
	}
	
	public  List<Grupo> getGruposAll() {
		if (gruposAll == null || gruposAll.isEmpty()) {
			gruposAll = grupoService.getByAll();
		}
		return gruposAll;
	}

	public void setGruposAll(List<Grupo> gruposAll) {
		this.gruposAll = gruposAll;
	}

	public  List<Marca> getMarcasAll() {
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
}
