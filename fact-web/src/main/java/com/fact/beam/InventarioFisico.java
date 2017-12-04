package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import com.fact.api.Impresion;
import com.fact.model.Configuracion;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.Grupo;
import com.fact.model.Marca;
import com.fact.model.Producto;
import com.fact.model.Proveedor;
import com.fact.model.TipoEvento;
import com.fact.model.Usuario;
import com.fact.service.EventoService;
import com.fact.service.GrupoService;
import com.fact.service.MarcaService;
import com.fact.service.ProductoService;
import com.fact.service.ProveedorService;
import com.fact.vo.ProductoVo;

@ManagedBean
@SessionScoped
public class InventarioFisico implements Serializable {

	/**
	 * luis Miguel gonzalez
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private ProductoService productoService;

	@EJB
	private EventoService eventoService;

	@EJB
	private ProveedorService proveedorService;

	@EJB
	private MarcaService marcaService;
	
	@EJB
	private GrupoService grupoService;

	// filtros
	private List<Long> productoFilter;
	private String[] proveedorFilter;
	private String[] marcaFilter;
	private String[] grupoFilter;
	
	private List<Producto> productosListFilter;
	private Double iva;
	private Long codigo;
	private Long tipoPeso;

	private List<ProductoVo> productosAll;
	private List<Producto> productosList;
	private List<Proveedor> proveedorList;
	private List<Marca> marcaList;
	private List<Grupo> grupoList;
	private List<Integer> productosSelect = new ArrayList<>();;
	List<ProductoVo> selected;

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	private Configuracion configuracion() {
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}
	
	private String impresora() {
		String yourVariable = (String) sessionMap.get("impresora");
		return yourVariable;
	}

	public void onRowEdit(RowEditEvent event) {
		ProductoVo vo = (ProductoVo) event.getObject();
		System.out.println("celda:" + vo.getNombre());
	}

	public Usuario usuario() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		Usuario usaurio = (Usuario) sessionMap.get("userLogin");
		return usaurio;
	}

	public void guardarInventarioFisico2() {

		List<ProductoVo> edits = new ArrayList<>();
		for (Integer pVo : productosSelect) {
			edits.add(getProductosAll().get(pVo));
		}
		for (ProductoVo pVo : edits) {
			// se existe un cambio de precio se registra el evento
			if (!pVo.getPublico().equals(pVo.getProducto().getCostoPublico())) {
				Evento evento = new Evento();
				TipoEvento tipoEvento = new TipoEvento();
				tipoEvento.setTipoEventoId(3l); // se envia tipo evento igual a
				evento.setCampo(pVo.getProducto().getNombre()); // cambio de
																// precio
				evento.setTipoEventoId(tipoEvento);
				evento.setFechaRegistro(new Date());
				evento.setUsuarioId(usuario());
				evento.setValorActual("" + pVo.getPublico());
				evento.setValorAnterior("" + pVo.getProducto().getCostoPublico());
				eventoService.save(evento);
			}
			if(!pVo.getCantidad().equals(pVo.getProducto().getCantidad())){
				Evento evento = new Evento();
				TipoEvento tipoEvento = new TipoEvento();
				tipoEvento.setTipoEventoId(5l); // se envia tipo evento igual a
				evento.setCampo(pVo.getProducto().getNombre()); // cambio de
																// cantidad
				evento.setTipoEventoId(tipoEvento);
				evento.setFechaRegistro(new Date());
				evento.setUsuarioId(usuario());
				evento.setValorActual("" + pVo.getCantidad());
				evento.setValorAnterior("" + pVo.getProducto().getCantidad());
				eventoService.save(evento);
			}
			// System.out.println("borrar: "+pVo.getBorrar());
			// System.out.println(pVo.getProducto().getBalanza());
			Producto p = pVo.getProducto();
			p = pVo.getProducto();
			if (pVo.getBorrar().toUpperCase().equals("B")) {
				p.setEstado(0l);
			}
			p.setCosto(pVo.getCosto() == null ? 0.0 : pVo.getCosto());
			p.setCostoPublico(pVo.getPublico() == null ? 0.0 : pVo.getPublico());
			p.setCantidad(pVo.getCantidad() == null ? 0.0 : pVo.getCantidad());
			p.setIva(pVo.getIva() == null ? 0.0 : pVo.getIva());
			p.setHipoconsumo(pVo.getHipoconsumo() == null ? 0.0 : pVo.getHipoconsumo());
			p.setkGPromo(pVo.getkGPromo() == null ? 0.0 : pVo.getkGPromo());
			p.setPubPromo(pVo.getPubPromo() == null ? 0l : pVo.getPubPromo());
			if (pVo.getBalanza() != null) {
				p.setBalanza(pVo.getBalanza().toUpperCase().equals("N") ? 0l : 1l);
			} else {
				p.setBalanza(0l);
			}
			if (pVo.getPromo() != null) {
				p.setPromo(pVo.getPromo().toUpperCase().equals("N") ? 0l : 1l);
			} else {
				p.setBalanza(0l);
			}
			p.setNombre(pVo.getNombre().toUpperCase().trim());
			p.setUtilidadSugerida(pVo.getUtilidadSugerida());
			p.setCostoSugerida(pVo.getCostoSugerida());
			Double stock =p.getStockMax()==null?0.0:p.getCantidad();
			// if(stock!=null){
			if (p.getCantidad() > stock) {
				System.out.println("entro");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("La cantidad max sugerida para " + p.getNombre() + " es " + p.getStockMax()));
			}
			// }
			productoService.update(p,1l);
			Configuracion configuracion = configuracion();
			Long server = configuracion.getServer();
			if(server==2l){			
				productoService.update(p,server);
			}
		}
		setProductosAll(null);
		RequestContext.getCurrentInstance().update("inventarioFisicoForm");

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(edits.size() + " Productos editados exitosamente"));
		System.out.println("tama�o edit:" + edits.size());
	}

	public void limpiar() {
		setProductosAll(null);
	}

	private List<Long> convertir(String[] lista) {
		List<String> stri = Arrays.asList(lista);
		List<Long> lista2 = new ArrayList<>();
		for (String s : stri) {
			lista2.add(Long.valueOf(s));
		}
		return lista2;
	}

	public void actualizar() {
		setProductosAll(null);
		productosAll = new ArrayList<>();
		try {
			// List<Producto> productos=productoService.getByAll();
			List<Producto> productos = productoService.find(convertir(getMarcaFilter()), getIva(), getCodigo(),
					getTipoPeso(), convertir(getProveedorFilter()),convertir(getGrupoFilter()));
			for (Producto p : productos) {
				ProductoVo pVo = new ProductoVo();
				pVo.setBorrar("");
				pVo.setNombre(p.getNombre() == null ? "" : p.getNombre().toUpperCase().trim());
				pVo.setPublico(p.getCostoPublico() == null ? 0.0 : p.getCostoPublico());
				pVo.setCosto(p.getCosto() == null ? 0.0 : p.getCosto());
				pVo.setCantidad(p.getCantidad() == null ? 0.0 : p.getCantidad());
				pVo.setIva(p.getIva() == null ? 0.0 : p.getIva());
				pVo.setHipoconsumo(p.getHipoconsumo() == null ? 0.0 : p.getHipoconsumo());
				pVo.setPubPromo(p.getPubPromo() == null ? 0l : p.getPubPromo());
				pVo.setkGPromo(p.getkGPromo() == null ? 0l : p.getkGPromo());
				if (p.getBalanza() != null) {
					pVo.setBalanza(p.getBalanza() == 1l ? "S" : "N");
				}
				if (p.getPromo() != null) {
					pVo.setPromo(p.getPromo() == 1l ? "S" : "N");
				} else {
					pVo.setPromo("N");
				}

				Double utilidadSugerida = p.getUtilidadSugerida() == null ? 0.0 : p.getUtilidadSugerida();
				Double costoSugerida = p.getCostoSugerida() == null ? 0.0 : p.getCostoSugerida();
				Double costo = p.getCosto() == null ? 0.0 : p.getCosto();
				Double publico = p.getCostoPublico() == null ? 0.0 : p.getCostoPublico();
				Double publicoSugerido = costo + (costo * (utilidadSugerida / 100));
				Double utilidadReal = ((publico - costo) * 100) / costo;
				Double diferencia = publico - costo;
				pVo.setUtilidadSugerida(utilidadSugerida);
				pVo.setPublicoSugerida(publicoSugerido);
				pVo.setUtilidadReal(utilidadReal);
				pVo.setDiferencia(diferencia);
				pVo.setProducto(p);
				pVo.setCostoSugerida(costoSugerida);
				productosAll.add(pVo);
				productosSelect = new ArrayList<>();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error en la busqueda de productos"));
		}

	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();
		int rowIndex = event.getRowIndex();
		// con el row index ir a buscarlo en la lista e agregarlo en una nueva
		// lista para editar cuando se unda el boton guardar;
		if (newValue != null && !newValue.equals(oldValue)) {
			System.out.println("producto editado: " + newValue);
			// ProductoVo pEdict = productosAll.get(rowIndex);
			int esta = 0;
			for (Integer p : productosSelect) {
				if (p.toString().equals("" + rowIndex)) {
					esta = 1;
				}
			}
			if (esta == 0) {
				productosSelect.add(rowIndex);
			}
		}
	}

	public String imprimirInventario() {
		List<Producto> lista = productoService.getByAll();
		Empresa e = Login.getEmpresaLogin();
		Configuracion configuracion = configuracion();
		String imp = e.getImpresion().toUpperCase();
		String impresora=impresora();
		switch (imp) {
		case "TXT":
			// pdf = imprimirTxt();
			break;
		case "BIG":
			Impresion.imprimirInventarioPDF(lista, usuario(), configuracion,impresora);
			break;
		case "PDF":
			Impresion.imprimirInventarioPDF(lista, usuario(), configuracion,impresora);
			break;
		default:
			break;
		}

		return "";
	}

	public List<ProductoVo> getProductosAll() {
		return productosAll;
	}

	public void setProductosAll(List<ProductoVo> productosAll) {
		this.productosAll = productosAll;
	}

	public List<Producto> getProductosList() {
		productosList = productoService.getByAll();
		return productosList;
	}

	public void setProductosList(List<Producto> productosList) {
		this.productosList = productosList;
	}

	public List<Long> getProductoFilter() {
		return productoFilter;
	}

	public void setProductoFilter(List<Long> productoFilter) {
		this.productoFilter = productoFilter;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getTipoPeso() {
		return tipoPeso;
	}

	public void setTipoPeso(Long tipoPeso) {
		this.tipoPeso = tipoPeso;
	}

	public List<Producto> getProductosListFilter() {
		return productosListFilter;
	}

	public void setProductosListFilter(List<Producto> productosListFilter) {
		this.productosListFilter = productosListFilter;
	}

	public String[] getProveedorFilter() {

		return proveedorFilter;
	}

	public void setProveedorFilter(String[] proveedorFilter) {
		this.proveedorFilter = proveedorFilter;
	}

	public List<Proveedor> getProveedorList() {
		proveedorList = proveedorService.getByAll();
		return proveedorList;
	}

	public void setProveedorList(List<Proveedor> proveedorList) {
		this.proveedorList = proveedorList;
	}

	public String[] getMarcaFilter() {
		return marcaFilter;
	}

	public void setMarcaFilter(String[] marcaFilter) {
		this.marcaFilter = marcaFilter;
	}

	public List<Marca> getMarcaList() {
		marcaList = marcaService.getByAll();
		return marcaList;
	}

	public void setMarcaList(List<Marca> marcaList) {
		this.marcaList = marcaList;
	}

	public List<ProductoVo> getSelected() {
		return selected;
	}

	public void setSelected(List<ProductoVo> selected) {
		this.selected = selected;
	}
	
	public String[] getGrupoFilter() {
		return grupoFilter;
	}

	public void setGrupoFilter(String[] grupoFilter) {
		this.grupoFilter = grupoFilter;
	}

	public List<Grupo> getGrupoList() {
		grupoList=grupoService.getByAll();
		return grupoList;
	}

	public void setGrupoList(List<Grupo> grupoList) {
		this.grupoList = grupoList;
	}

}
