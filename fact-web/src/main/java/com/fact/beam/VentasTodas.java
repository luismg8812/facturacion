package com.fact.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.fact.model.Documento;
import com.fact.model.Usuario;
import com.fact.service.DocumentoDetalleService;
import com.fact.service.DocumentoService;
import com.fact.service.UsuarioService;
import com.fact.vo.CuadrePorCajeroVo;

@ManagedBean
@SessionScoped
public class VentasTodas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private DocumentoService documentoService;

	@EJB
	private DocumentoDetalleService documentoDetalleService;
	
	@EJB
	private UsuarioService usuarioService;
	
	
	private List<CuadrePorCajeroVo> cuadrePorCajeroVos= new ArrayList<>();
	private Double remisiones;
	private Double facturas;

	public List<CuadrePorCajeroVo> getCuadrePorCajeroVos()  {
	    List<Usuario> usuarios = usuarioService.getByAll();
	    List<CuadrePorCajeroVo> cuadretemp= new ArrayList<>();
	    for(Usuario u: usuarios){
	    	CuadrePorCajeroVo cpc= new CuadrePorCajeroVo();
	    	Double facturas = getTotalFaturasToDay(u);
	    	Double remisiones = getTotalRemisionesToDay(u);
	    	Double costos = getTotalCostosToDay(u);
	    	cpc.setUsuarioId(u);
	    	cpc.setUsuarioId2(u.getUsuarioId());
	    	cpc.setTotalFacturas(facturas);
	    	cpc.setTotalRemisiones(remisiones);
	    	cpc.setBase(costos);
	    	cpc.setRecargas(facturas-costos);
	    	cuadretemp.add(cpc);
	    }
	    cuadrePorCajeroVos=cuadretemp;
		return cuadrePorCajeroVos;
	}


	public void setCuadrePorCajeroVos(List<CuadrePorCajeroVo> cuadrePorCajeroVos) {
		this.cuadrePorCajeroVos = cuadrePorCajeroVos;
	}
	
	public Double getTotalFaturasToDay(Usuario usuario)  {
		List<Long>tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		Boolean conCierre=true;		
		List<Documento> factDia = documentoService.getByfacturasReales(tipoDocumentoId, usuario.getUsuarioId(), conCierre, 1l);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal();
			}
		}
		return total;
	}
	
	public Double getTotalCostosToDay(Usuario usuario)  {
		List<Long>tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(10l); // tipo documento factura de salida
		Boolean conCierre=true;		
		List<Documento> factDia = documentoService.getByfacturasReales(tipoDocumentoId, usuario.getUsuarioId(), conCierre, 1l);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotalCosto() != null) {
				total = total + d.getTotalCosto();
			}
		}
		return total;
	}
	
	public Double getTotalRemisionesToDay(Usuario usuario)  {
		List<Long>tipoDocumentoId = new ArrayList<>();
		tipoDocumentoId.add(9l); // tipo documento factura de salida
		Boolean conCierre=true;		
		List<Documento> factDia = documentoService.getByfacturasReales(tipoDocumentoId, usuario.getUsuarioId(), conCierre, 1l);
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal();
			}
		}
		return total;
	}


	public Double getRemisiones() {
		remisiones=0.0;
		List<Usuario> usuarios = usuarioService.getByAll();
	    for(Usuario u: usuarios){
	    	remisiones+=getTotalRemisionesToDay(u);
	    }
		return remisiones;
	}


	public void setRemisiones(Double remisiones) {
		this.remisiones = remisiones;
	}


	public Double getFacturas() {
		facturas=0.0;
		List<Usuario> usuarios = usuarioService.getByAll();
	    for(Usuario u: usuarios){
	    	facturas+=getTotalFaturasToDay(u);
	    }
		return facturas;
	}


	public void setFacturas(Double facturas) {
		this.facturas = facturas;
	}
	
	

}
