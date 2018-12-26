package com.fact.beam;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.csvreader.CsvReader;
import com.fact.api.FactException;
import com.fact.model.Producto;
import com.fact.service.MarcaService;
import com.fact.service.ProductoService;
import com.fact.vo.ProductoVo;




@ManagedBean
@SessionScoped
public class importar  implements Serializable{
	
	/**
	 * luis Miguel gonzalez
	 */
	private static final long serialVersionUID = 1L;
	
	List<ProductoVo> pVoList = new ArrayList<>();
	List<ProductoVo> error = new ArrayList<>();
	
	@EJB
	private ProductoService productoService;
	
	@EJB
	private MarcaService marcaService;
 
	public String importarCsv() throws IOException {
		System.out.println("entra a importar" );
		String ruta="C:\\facturacion\\importar.csv";
		File filePrueba = new File(ruta);
		List<ProductoVo> pVoList1 = new ArrayList<>();
		List<ProductoVo> error1 = new ArrayList<>();
        //pregunto si el directorio existe sino lo creo
        if (!filePrueba.exists()) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Por favor agregar el archivo importar.csv en C:/facturacion"));
        	//mensaje de que no existe el archivo inportar
        	return "";
        }
        
		try {
			CsvReader usuarios_import = new CsvReader(ruta);
			usuarios_import.readHeaders();			
			while (usuarios_import.readRecord()){
				Producto p = new Producto();
				ProductoVo pVo = new ProductoVo();
				ProductoVo pVoError = new ProductoVo();
				try {
					//System.out.println("costoU"+usuarios_import.get("COSTOU"));
					String Nombre =usuarios_import.get("NOMBRE")==null?"":usuarios_import.get("NOMBRE");
					String costo = usuarios_import.get("COSTOU").equals("")?"0.0":usuarios_import.get("COSTOU");
					String costoP = usuarios_import.get("PUBLIC").equals("")?"0.0":usuarios_import.get("PUBLIC") ;
					String PESOKG = usuarios_import.get("PESOKG").equals("")?"0.0":usuarios_import.get("PESOKG") ;
					String UNIDAD  = (usuarios_import.get("UNIDAD")==null?"":usuarios_import.get("UNIDAD")) ;
					String STOMIN = usuarios_import.get("STOMIN").equals("")?"0":usuarios_import.get("STOMIN") ;
					String STOMAX = usuarios_import.get("STOMAX").equals("")?"0":usuarios_import.get("STOMAX") ;
					String marca  = (usuarios_import.get("REFER1")==null?"":usuarios_import.get("REFER1")) ;
					String codB = usuarios_import.get("REFER3").equals("")?"0":usuarios_import.get("REFER3") ;
					String CANTID = usuarios_import.get("SACTUA").equals("")?"0":usuarios_import.get("SACTUA");
					String BALANZA =  usuarios_import.get("BALANZA").equals("")?"":usuarios_import.get("BALANZA");
					String PROMO =  (usuarios_import.get("PROMO").equals("")?"":usuarios_import.get("PROMO")) ;
					String PUBPRO =  usuarios_import.get("PUBPRO").equals("")?"0.0":usuarios_import.get("PUBPRO") ;
					String CANPRO =  usuarios_import.get("CANPRO").equals("")?"0.0":usuarios_import.get("CANPRO") ;
					String VALIVA =  usuarios_import.get("VALIVA").equals("")?"0.0":usuarios_import.get("VALIVA") ;
					String REFER1 =  (usuarios_import.get("REFER1")==null?"":usuarios_import.get("REFER1")) ;
										
					p.setNombre(Nombre);
					p.setCosto(Double.valueOf(costo));
					p.setCostoPublico(Double.valueOf(costoP));
					p.setPeso(Double.valueOf(PESOKG));
					p.setUnidad(UNIDAD.toUpperCase().equals("S")?BALANZA.toUpperCase():"N");
					p.setStockMax(Long.valueOf(STOMAX));
					p.setStockMin(Long.valueOf(STOMIN));
					if(!marca.equals("")){
//						Marca mar = new Marca();
//						mar.setEstado(1l);
//						mar.setNombre(marca);	
//						marcaService.save(mar);
//						p.setMarcaId(mar);
					}					
					p.setCodigoBarras(codB);
					p.setCantidad(Double.valueOf(CANTID));
					p.setBalanza(BALANZA.toUpperCase().equals("S")?1l:0l);
					p.setPromo(PROMO.toUpperCase().equals("S")?1l:0l);
					p.setPubPromo(Double.valueOf(PUBPRO));
					p.setkGPromo(Double.valueOf(CANPRO));
					p.setIva(Double.valueOf(VALIVA));
					p.setEstado(1l);
					p.setFechaRegistro(new Date());
					p.setVarios(Long.valueOf(REFER1.trim().equals("V")?"1":"0"));
							
					pVo.setProducto(p);
					pVo.setBalanza(BALANZA.toUpperCase().equals("S")?BALANZA.toUpperCase():"N");
					pVo.setNombre(Nombre);
					pVo.setPublico(Double.valueOf(costoP));
					pVo.setPromo(PROMO.toUpperCase().equals("S")?PROMO.toUpperCase():"N");
					pVo.setPubPromo(Double.valueOf(PUBPRO));
					pVo.setkGPromo(Double.valueOf(CANPRO));
					pVoList1.add(pVo);
					productoService.save(pVo.getProducto(),1l);
				} catch (Exception e) {
					String Nombre =usuarios_import.get("NOMBRE")==null?"":usuarios_import.get("NOMBRE");
					pVoError.setNombre(Nombre);
					error1.add(pVoError);
					System.out.println("no se pudo esportar el producto: "+Nombre);
				}
				//p.setu;
				//p.setCosto();
				//productoService.save(p);
//				contador++;
//				if(contador>5){
//					break;
//				}
			}
			 usuarios_import.close();
			 pVoList=pVoList1;
			 error=error1;
			
			 RequestContext.getCurrentInstance().update("tablaInventario");
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Importación Completa"));
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(pVoList1.size() +" Importados exitosamente"));
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error.size() +" No Importados"));
		} catch (FactException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error en importación"));
			//error en la importación
			
		}        
        return "";
	}
	
	public String guardarImportacion(){
		System.out.println("entra a guardar importación");
		for(ProductoVo pv: getpVoList()){
			if(pv.getBorrar().toUpperCase().equals("B")){
				productoService.save(pv.getProducto(),1l);
			}
		}
		RequestContext.getCurrentInstance().execute("PF('importar').hide();");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Importación Completa"));
		return "";
	}

	public List<ProductoVo> getpVoList() {
		return pVoList;
	}

	public void setpVoList(List<ProductoVo> pVoList) {
		this.pVoList = pVoList;
	}

	public List<ProductoVo> getError() {
		return error;
	}

	public void setError(List<ProductoVo> error) {
		this.error = error;
	}	
	
	
}
