package com.fact.beam;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


	public List<CuadrePorCajeroVo> getCuadrePorCajeroVos()  {
	    List<Usuario> usuarios = usuarioService.getByAll();
	    List<CuadrePorCajeroVo> cuadretemp= new ArrayList<>();
	    for(Usuario u: usuarios){
	    	CuadrePorCajeroVo cpc= new CuadrePorCajeroVo();
	    	cpc.setUsuarioId(u);
	    	cpc.setUsuarioId2(u.getUsuarioId());
	    	cpc.setTotalFacturas(getTotalFaturasToDay(u));
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
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
	}

}
