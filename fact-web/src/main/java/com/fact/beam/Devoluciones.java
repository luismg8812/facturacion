package com.fact.beam;

import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import com.fact.api.Calculos;
import com.fact.api.Impresion;
import com.fact.model.Configuracion;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;
import com.fact.model.Evento;
import com.fact.model.Producto;
import com.fact.model.TipoEvento;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.EventoService;
import com.fact.service.ProductoService;
import com.fact.vo.DocumentoDetalleVo;
import com.itextpdf.text.DocumentException;


@ManagedBean
@SessionScoped
public class Devoluciones  implements Serializable{
	
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
	private EventoService eventoService;
 
	private String Documento;
	private Documento documentoSelect;
	private List<DocumentoDetalleVo> detalles;
	
	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	private Configuracion configuracion(){		
		Configuracion yourVariable = (Configuracion) sessionMap.get("configuracion");
		return yourVariable;
	}
	
	private String impresora(String impresora) {
		return (String) sessionMap.get("impresora"+impresora);
	}
	
	public String  buscar(){
		if(getDocumento()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El numero de documento es obligatorio"));
			return "";
		}
		System.out.println("busca_devoluciones");
		documentoSelect=documentoService.getByConsecutivoDian(getDocumento());
		if(documentoSelect==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("El documento no existe"));
			return "";
		}
		List<DocumentoDetalle> list = documentoDetalleService.getByDocumento(documentoSelect.getDocumentoId(),1l);
		setDetalles(new ArrayList<DocumentoDetalleVo>());
		setDetalles(Calculos.llenarDocumentoDetalleVoList(list));
		return "";
	}
	
	public void onRowSelect() throws DocumentException, IOException, PrinterException {
		Documento documentoSelect= new Documento();
		Evento evento = new Evento();
		long server =1;
		for(DocumentoDetalleVo d: getDetalles()){
			Double cDevolucion= d.getCantidadDevolucion()==null?0l:d.getCantidadDevolucion();
//			if(cDevolucion==0.0){
//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad de la Devolución no puede estar vacia o '0'"));
//				return ;
//			}
			if(d.getCantidad()<cDevolucion){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cantidad de la Devolución no puede ser mayor a la cantidad vendida"));
				return ;
			}
			Double cantidadNew =d.getCantidad()-cDevolucion;
			Double parcialNew = cantidadNew*d.getUnitario();
			Double cantidadProducto=d.getProductoId().getCantidad()+cDevolucion;
			Double total = d.getDocumentoId().getTotal();
			DocumentoDetalle dd = new DocumentoDetalle();
			if(cantidadNew==0.0){
				dd.setEstado(0l);
			}else{
				dd.setEstado(1l);
			}
			dd.setCantidad(cantidadNew);
			dd.setDocumentoDetalleId(d.getDocumentoDetalleId().getDocumentoDetalleId());
			dd.setDocumentoId(d.getDocumentoId());
			dd.setFechaRegistro(d.getFechaRegistro());
			dd.setProductoId(d.getProductoId());
			dd.setParcial(parcialNew);
			Producto pro= d.getProductoId();
			Documento docu = d.getDocumentoId();
			docu.setTotal(total-(d.getParcial()-parcialNew));
			pro.setCantidad(cantidadProducto);		
		    documentoDetalleService.update(dd,server);     
		    productoService.update(pro,1l);
		    documentoService.update(docu,server);
		    TipoEvento tEvento = new TipoEvento();
		    tEvento.setTipoEventoId(1l); // se asigna tipo evento igual a devolucion
		    evento.setCampo(pro.getNombre());
		    evento.setTipoEventoId(tEvento);
		    evento.setFechaRegistro(new Date());
		    evento.setUsuarioId(docu.getUsuarioId());
		    evento.setValorActual(""+cDevolucion);
		    eventoService.save(evento);
		    System.out.println("documentoDetalle borrado:"+d.getDocumentoDetalleId());
		}
		if(getDocumento()!=null && documentoSelect.getImpreso()!=null && documentoSelect.getImpreso()==1){
	    	imprimirFactura();
	    }
		buscar();
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Devolución Exitosa"));
	}
	
	public String imprimirFactura() throws DocumentException, IOException, PrinterException {
		System.out.println("entra a imprimir");
		Configuracion configuracion = configuracion();
		String impresora=impresora("1");
		String enPantalla = "false"; 
		long server =1;
			documentoSelect.setImpreso(1l);
			documentoService.update(documentoSelect,server);
			Empresa e =Login.getEmpresaLogin();
			String pdf= "";
			String imp=e.getImpresion().toUpperCase();
			if(imp.equals("TXT")){
				pdf=imprimirTxt();
			}else{				
				try {
				pdf=Impresion.imprimirPDF(documentoSelect, getDetalles(), documentoSelect.getUsuarioId(),configuracion,impresora,enPantalla,e);
				} catch (PrintException e1) {
					System.out.print("Error en impresion devolucion");
					e1.printStackTrace();
				}
				
		}
			
			if(imp.equals("TXT")){
				  			        
			        FileInputStream inputStream = null;
			          try {
			              inputStream = new FileInputStream("C:\\facturacion\\"+pdf);
			              System.out.println(pdf);
			          } catch (FileNotFoundException ex) {
			              ex.printStackTrace();
			          }
			          if (inputStream == null) {
			              //return;
			          }
			          DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			          Doc document = new SimpleDoc(inputStream, docFormat, null);
			          PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();			          
			          PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();			   
			          if (defaultPrintService != null) {
			              DocPrintJob printJob = defaultPrintService.createPrintJob();
			              try {
			                  printJob.print(document, attributeSet);
			   
			              } catch (Exception ex) {
			                  ex.printStackTrace();
			              }
			          } else {
			              System.err.println("No existen impresoras instaladas");
			          }
			   
			          //inputStream.close();
			}
			 
		return "";
	}
	
	public String imprimirTxt() throws IOException{
		System.out.println("entra a imprimir");
		Empresa e =Login.getEmpresaLogin();
		String pdf= "factura_"+documentoSelect.getDocumentoId()+".txt";
		File archivo = new File("C:\\facturacion\\"+pdf);
		BufferedWriter bw;
	    bw = new BufferedWriter(new FileWriter(archivo));
	    bw.write("---------------------------------------\n");
	    bw.write("         >>"+e.getNombre()+"<<\n");
	    bw.write("           "+e.getRepresentante()+"         \n");
	    bw.write("      NIT. "+e.getNit()+"   "+e.getRegimen()+"   \n");
	    bw.write("          "+e.getDireccion()+"           \n");
	    bw.write("           "+e.getBarrio()+"            \n");
	    bw.write("  	 	   "+e.getCiudad()+"-"+e.getDepartamento()+"	        \n");
	    bw.write("             TEL: "+e.getTelefonoFijo()+"             \n");
	    bw.write("FACTURA DE VANTA:    "+documentoSelect.getConsecutivoDian());
	    bw.write("\n"+documentoSelect.getFechaRegistro());
	    bw.write("\nCAJERO: "+documentoSelect.getUsuarioId().getUsuarioId()+" "+documentoSelect.getUsuarioId().getNombre()+" "+documentoSelect.getUsuarioId().getApellido());
	    bw.write("\n-----------------------------------\n");
	    bw.write("Descripción        CANT  UNIDAD  TOTAL\n");
	    bw.write("----------------------------------------");
	    for(DocumentoDetalleVo ddV: getDetalles()){
	    bw.write("\n"+ddV.getProductoId().getNombre().trim()+"     "+ddV.getCantidad()+
		    			  " "+ddV.getProductoId().getCostoPublico()+" "+(ddV.getProductoId().getCostoPublico()*ddV.getCantidad()));
	    }
	    bw.write("\n---------------------------------------");
	    bw.write("\nValor Exento:          "+ 0);
	    bw.write("\nValor Gravado:         "+ 0);
	    bw.write("\nIva:                   "+ 0);
	    bw.write("\n---------------------------------------");
	    bw.write( "\nTOTAL A PAGAR:         "+ documentoSelect.getTotal());
	    bw.write("\n---------------------------------------\n");
	    bw.write("         **** FORMA DE PAGO****        ");
	    //Long pago= getValorTargeta()==null?0l:getValorTargeta();
	    bw.write("\nVr. Pago con Tarjeta:  "+0);
	    bw.write("\nVr. Comisión Tarjeta:  "+ 0l);
	    bw.write("\nVr. Total Factura:     "+ documentoSelect.getTotal());  
	    bw.write("\nEfectivo:			"+ 0);
	    bw.write("\nCambio:			     "+ 0);
	    bw.write("\n  *****GRACIAS POR SU COMPRA*****   \n");
	    bw.write("             Software               \n");
	    bw.write("   NICESOFT Cel 3185222474-3112864974  ");
	    bw.write("\n");
	    bw.write("\n");
	    bw.write("\n");
	    bw.write(" \n");
	    bw.close();
	    return pdf;
	}

	public String getDocumento() {
		return Documento;
	}

	public void setDocumento(String documento) {
		Documento = documento;
	}

	public List<DocumentoDetalleVo> getDetalles() {
		if(detalles==null){
			detalles=new ArrayList<>();
		}
		return detalles;
	}

	public void setDetalles(List<DocumentoDetalleVo> detalles) {
		this.detalles = detalles;
	}
  
	
}
