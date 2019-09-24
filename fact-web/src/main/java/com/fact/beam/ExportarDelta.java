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

import com.fact.api.Calculos;
import com.fact.model.Cliente;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Proveedor;
import com.fact.model.TipoDocumento;
import com.fact.model.TipoPago;
import com.fact.model.Usuario;
import com.fact.service.ClienteService;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.ProveedorService;
import com.fact.service.TipoDocumentoService;
import com.fact.service.TipoPagoService;
import com.fact.service.UsuarioService;
import com.fact.vo.ConvinacionDelta;
import com.fact.vo.ExportarDetalleDeltaVo;

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
	private DocumentoDetalleService documentoDetalleService;
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

	// exportar documentos
	private Date fechaInicio;
	private Date fechafin;
	private List<TipoDocumento> tipoDocumentos;
	private Long tipoDocumento;
	private Long tipoDocumentoEmp;
	private Long tipoEstructura;
	private List<TipoPago> tipoPagos;
	private Long tipoPago;
	private String cuenta;
	private String prefixTipoDoc;
	private String exentasGravadas;
	private List<ConvinacionDelta> convinacionDeltas = new ArrayList<>();

	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();

	public void agregarPrefijo() {
		if (tipoDocumento == 0l) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El tipo documento es obligatorio"));
		}
		TipoDocumento tipo = tipoDocumentoService.getById(getTipoDocumento());
		tipo.setPrefijo(getPrefixTipoDoc());
		tipoDocumentoService.update(tipo);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Prefijo agregado exitosamente"));
		setTipoDocumentos(null);
	}

	public void agregar() {
		log.info("agregar convinacion");
		// TODO agregar validaciones de los campos para que quede bien evitar nulos
		ConvinacionDelta convinacionDelta = new ConvinacionDelta();
		convinacionDelta.setCuenta(getCuenta());
		convinacionDelta.setDocumentosIva(getExentasGravadas());
		convinacionDelta.setTipodocumentoId(getTipoDocumentoEmp());
		convinacionDelta.setTipoDocumentoNombre(tipoDocumentoService.getById(getTipoDocumentoEmp()).getNombre());
		convinacionDelta.setTipoPagoId(getTipoPago());
		convinacionDelta.setTipoPagoNombre(tipoPagoService.getById(getTipoPago()).getNombre());
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
//		if(getConvinacionDeltas().isEmpty()) {
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No hay convinaciones agregadas"));
//			return null;
//		}
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
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String fhoyIni = df.format(new Date());
		String pdf = "DocumentosDELTA_" + fhoyIni + ".txt";
		File folder = new File(carpeta);
		folder.mkdirs();
		folder = new File(carpeta + pdf);
		String tab = "	";
		String[] cuentas = getCuenta().split(";");
		BufferedWriter bw;
		List<ExportarDetalleDeltaVo> documentos = new ArrayList<>();
		
		for (ConvinacionDelta c : getConvinacionDeltas()) {
			
			try {
				bw = new BufferedWriter(new FileWriter(folder));
				String heder="";
				
				switch (""+c.getTipodocumentoId()) {
				case "10":
					documentos = documentoService.getDocumentosSalidasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"iva");
					log.info("totalDocumentos:" + documentos.size());
					heder = tipoDocumentoService.getById(getTipoDocumentoEmp()).getPrefijo() + tab + documentoService.getByUltimoId() + tab
							+ df2.format(getFechaInicio()) + tab + "iva corespondiente a las ventas de clientes" + 
							tab + "" + tab + "" + tab + "" + tab + ""
							+ tab + "";
					bw.write(heder);
					bw.newLine();
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[0] + tab + e.getCodigotercero() + tab + "0" + tab
								+ e.getTotal() + tab + "0" + tab + "0" + tab + "" + tab + "iva en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					
					documentos = documentoService.getDocumentosSalidasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"base");
					log.info("totalDocumentos:" + documentos.size());
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[1] + tab + e.getCodigotercero() + tab + "0" + tab
								+ e.getTotal() + tab + "0" + tab + "0" + tab + "" + tab + "base en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					
					documentos = documentoService.getDocumentosSalidasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"exento");
					log.info("totalDocumentos:" + documentos.size());
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[2] + tab + e.getCodigotercero() + tab + "0" + tab
								+ e.getTotal() + tab + "0" + tab + "0" + tab + "" + tab + "exento en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					break;
				case "2":
					documentos = documentoService.getDocumentosEntradasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"iva");
					log.info("totalDocumentos:" + documentos.size());
					heder = tipoDocumentoService.getById(getTipoDocumentoEmp()).getPrefijo() + tab + documentoService.getByUltimoId() + tab
							+ df2.format(getFechaInicio()) + tab + "iva corespondiente a las ventas de clientes" + 
							tab + "" + tab + "" + tab + "" + tab + ""
							+ tab + "";
					bw.write(heder);
					bw.newLine();
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[0] + tab + e.getCodigotercero() + tab +e.getTotal()  + tab
								+ "0" + tab + "0" + tab + "0" + tab + "" + tab + "iva en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					
					documentos = documentoService.getDocumentosEntradasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"base");
					log.info("totalDocumentos:" + documentos.size());
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[1] + tab + e.getCodigotercero() + tab +  e.getTotal() + tab
								+ "0" + tab + "0" + tab + "0" + tab + "" + tab + "base en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					
					documentos = documentoService.getDocumentosEntradasDelta(c.getTipodocumentoId(),Calculos.fechaInicial(getFechaInicio()),Calculos.fechaFinal(getFechafin()),"exento");
					log.info("totalDocumentos:" + documentos.size());
					for(ExportarDetalleDeltaVo e:documentos) {
						String lineaDocumento2 = "" + tab + cuentas[2] + tab + e.getCodigotercero() + tab +e.getTotal() + tab
								+  "0" + tab + "0" + tab + "0" + tab + "" + tab + "exento en ventas de "+e.getNombretercero();
						bw.write(lineaDocumento2);
						bw.newLine();
					}
					break;
					
				default:
					break;
				}
				bw.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("error exportando archivo de documentos de delta");
			}

			//documentos.addAll(documentosTemp);
		}
		
//		if (getTipoEstructura() == 1) {
//			try {
//				String heder = "PrefijoDoc" + tab + "NumDoc" + tab + "FechaDoc" + tab + "Concepto";
//				String heder2 = "" + tab + "CodCuenta" + tab + "CodTercero" + tab + "Debito" + tab + "Credito" + tab
//						+ "ValorBase" + tab + "Cheque" + tab + "CodCC" + tab + "Detalle";
//				bw = new BufferedWriter(new FileWriter(folder));
//				// bw.write(heder+"\n");
//				// bw.write(heder2+"\n");
//				for (Documento d : documentos) {
//					String terc = d.getClienteId() == null ? d.getProveedorId().getNombre()
//							: d.getClienteId().getNombre();
//					String codigoTercero="";
//					switch (""+d.getTipoDocumentoId().getTipoDocumentoId()) {
//					case "2":
//						codigoTercero= "P" + d.getProveedorId().getProveedorId();
//						break;
//
//					default:
//						codigoTercero= "C" + d.getClienteId().getClienteId();
//						break;
//					}
//					String lineaDocumento = d.getTipoDocumentoId().getPrefijo() + tab + d.getDocumentoId() + tab
//							+ df2.format(d.getFechaRegistro()) + tab + d.getTipoDocumentoId().getNombre() + " N° "
//							+ d.getConsecutivoDian() + " (" + terc + ")" + tab + "" + tab + "" + tab + "" + tab + ""
//							+ tab + "";
//					
//					List<DocumentoDetalle> detalle = documentoDetalleService.getByDocumento(d.getDocumentoId(), 1l);
//					
//					long ban = 0;
//					for (DocumentoDetalle dd : detalle) {
//						String iva=dd.getProductoId().getIva()>0?"gravada":"exenta";
//						if(iva.equals(getExentasGravadas())) {
//							if(ban==0l) {
//								bw.write(lineaDocumento);
//								bw.newLine();
//								ban=1;
//							}
//							double baseg=0.0;
//							if(dd.getProductoId().getIva()>0) {
//								double iva1 = dd.getProductoId().getIva()/100;
//								baseg= (dd.getParcial() / (1 + iva1));	
//							}else {
//								baseg=dd.getParcial();
//							}						 
//							String lineaDocumento2 = "" + tab + getCuenta() + tab + codigoTercero + tab + dd.getParcial() + tab
//									+ "0" + tab + baseg + tab + "0" + tab + "" + tab + dd.getProductoId().getNombre() +"X" +dd.getCantidad();
//							bw.write(lineaDocumento2);
//							bw.newLine();
//						}
//						
//					}
//
//				}
//				bw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error("error exportando archivo de documentos de delta");
//			}
//		} else {
//			try {
//				String heder = "CodTipoDoc" + tab + "NumDoc" + tab + "Fecha" + tab + "MesAdicional" + tab
//						+ "NroControlInt" + tab + "Concepto" + tab + "CodCuenta" + tab + "CodTercero" + tab + "Debito"
//						+ tab + "Credito" + tab + "ValorBase" + tab + "Cheque" + tab + "CodCentroCosto" + tab
//						+ "DetalleItem";
//				bw = new BufferedWriter(new FileWriter(folder));
//				bw.write(heder);
//				for (Documento d : documentos) {
//					String terc = d.getClienteId() == null ? d.getProveedorId().getNombre()
//							: d.getClienteId().getNombre();
//					String codigoTercero = "C" + d.getClienteId().getClienteId();
//					String lineaDocumento = d.getTipoDocumentoId().getPrefijo() + tab + d.getDocumentoId() + " " + tab
//							+ df2.format(d.getFechaRegistro()) + tab + "0" + tab + "" + tab
//							+ d.getTipoDocumentoId().getNombre() + " N° " + d.getConsecutivoDian() + " (" + terc + ")"
//							+ tab + getCuenta() + tab + codigoTercero + tab + "0" + tab + "0" + tab + "0" + tab + ""
//							+ tab + "" + tab + "";
//					bw.write("\n" + lineaDocumento);
//				}
//				bw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error("error exportando archivo de documentos de delta");
//			}
//		}

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
		List<Cliente> cliente = new ArrayList<>();
		List<Proveedor> proveedor = new ArrayList<>();
		for (int i = 0; i < getSelectedTerceros().length; i++) {
			if (getSelectedTerceros()[i].equals("clientes")) {
				cliente = clienteService.getByAll();
			}
			if (getSelectedTerceros()[i].equals("proveedores")) {
				proveedor = proveedorService.getByAll();
			}
		}
		if (getTipoEstructura() == 1) {
			try {

				String heder = "CodTercero" + tab + "Nombre" + tab + "Zona" + tab + "Identificacion" + tab + "DV" + tab
						+ "De" + tab + "Direccion" + tab + "Ciudad" + tab + "Telefono";
				bw = new BufferedWriter(new FileWriter(folder));
				bw.write(heder);
				for (Cliente c : cliente) {
					String lineaCliente = "C" + c.getClienteId() + tab + c.getNombre() + tab + "01" + tab
							+ c.getDocumento() + tab + "" + tab + " " + tab
							+ (c.getDireccion() == null ? "" : c.getDireccion()) + tab
							+ (c.getCiudadId() == null ? "" : c.getCiudadId().getNombre()) + tab
							+ (c.getCelular() == null ? "" : c.getCelular());
					bw.write("\n" + lineaCliente);
				}
				for (Proveedor c : proveedor) {
					String lineaCliente = "P" + c.getProveedorId() + tab + c.getNombre() + tab + "01" + tab
							+ c.getDocumento() + tab + "" + tab + "" + tab
							+ (c.getDireccion() == null ? "" : c.getDireccion()) + tab
							+ (c.getCiudadId() == null ? "" : c.getCiudadId().getNombre()) + tab
							+ (c.getCelular() == null ? "" : c.getCelular());
					bw.write("\n" + lineaCliente);
				}
				bw.close();

			} catch (IOException e1) {

				e1.printStackTrace();
				log.error("error exportando archivo de terceros de delta");
			}
		} else {
			try {

				String heder = "CodTercero" + tab + "Nombre" + tab + "Apellidos" + tab + "CCNit" + tab + "DV" + tab
						+ "TipoDocId" + tab + "Empresa" + tab + "Regimen" + tab + "Naturaleza" + tab + "Direccion" + tab
						+ "CodBarrio" + tab + "CodCiudad" + tab + "CodPais" + tab + "Telefono" + tab + "Celular" + tab
						+ "Email" + tab + "CodActivEco" + tab + "CodTipoTerc" + tab + "CodZona" + tab + "Contacto"
						+ tab;
				bw = new BufferedWriter(new FileWriter(folder));
				bw.write(heder);
				for (Cliente c : cliente) {
					String lineaCliente = "C" + c.getClienteId() + tab + c.getNombre() + tab + c.getApellidos() + tab
							+ c.getDocumento() + tab + "" + tab + "" + tab + "" + tab + "" + tab + "" + tab
							+ (c.getDireccion() == null ? "" : c.getDireccion()) + tab + "" + tab + "" + tab + "" + tab
							+ (c.getFijo() == null ? "" : c.getFijo()) + tab
							+ (c.getCelular() == null ? "" : c.getCelular()) + tab
							+ (c.getMail() == null ? "" : c.getMail()) + tab + "" + tab + "" + tab + "" + tab + ""
							+ tab;
					bw.write("\n" + lineaCliente);
				}
				for (Proveedor c : proveedor) {
					String lineaCliente = "P" + c.getProveedorId() + tab + c.getNombre() + tab + c.getApellidos() + tab
							+ c.getDocumento() + tab + "" + tab + "" + tab + "" + tab + "" + tab + "" + tab +

							(c.getDireccion() == null ? "" : c.getDireccion()) + tab + "" + tab + "" + tab + "" + tab
							+ (c.getFijo() == null ? "" : c.getFijo()) + tab
							+ (c.getCelular() == null ? "" : c.getCelular()) + tab + "" + tab + "" + tab + "" + tab + ""
							+ tab + "" + tab;
					bw.write("\n" + lineaCliente);
				}
				bw.close();

			} catch (IOException e1) {

				e1.printStackTrace();
				log.error("error exportando archivo de terceros de delta");
			}
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
		if (tipoDocumentos == null) {
			tipoDocumentos = tipoDocumentoService.getByAll();
		}
		return tipoDocumentos;
	}

	public void setTipoDocumentos(List<TipoDocumento> tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}

	public List<TipoPago> getTipoPagos() {
		if (tipoPagos == null) {
			tipoPagos = tipoPagoService.getByAll();
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

	public String getPrefixTipoDoc() {
		return prefixTipoDoc;
	}

	public void setPrefixTipoDoc(String prefixTipoDoc) {
		this.prefixTipoDoc = prefixTipoDoc;
	}

	public Long getTipoDocumentoEmp() {
		return tipoDocumentoEmp;
	}

	public void setTipoDocumentoEmp(Long tipoDocumentoEmp) {
		this.tipoDocumentoEmp = tipoDocumentoEmp;
	}

	public Long getTipoEstructura() {
		return tipoEstructura;
	}

	public void setTipoEstructura(Long tipoEstructura) {
		this.tipoEstructura = tipoEstructura;
	}

	public String getExentasGravadas() {
		if (exentasGravadas == null) {
			exentasGravadas = "";
		}
		return exentasGravadas;
	}

	public void setExentasGravadas(String exentasGravadas) {
		this.exentasGravadas = exentasGravadas;
	}

}
