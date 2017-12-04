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
	
	
	List<CuadrePorCajeroVo> cuadrePorCajeroVos= new ArrayList<>();


	public List<CuadrePorCajeroVo> getCuadrePorCajeroVos() throws ParseException {
	    List<Usuario> usuarios = usuarioService.getByAll();
	    List<CuadrePorCajeroVo> cuadretemp= new ArrayList<>();
	    for(Usuario u: usuarios){
	    	CuadrePorCajeroVo cpc= new CuadrePorCajeroVo();
	    	cpc.setUsuarioId(u);
	    	cpc.setUsuarioId2(u.getUsuarioId());
	    	cpc.setTotalFacturas(getTotalFaturasToDay(u));
	    	//cpc.set
	    	cuadretemp.add(cpc);
	    }
	    cuadrePorCajeroVos=cuadretemp;
		return cuadrePorCajeroVos;
	}


	public void setCuadrePorCajeroVos(List<CuadrePorCajeroVo> cuadrePorCajeroVos) {
		this.cuadrePorCajeroVos = cuadrePorCajeroVos;
	}
	
	public Double getTotalFaturasToDay(Usuario usuario) throws ParseException {
		Long tipoDocumentoId = 10l; // tipo documento factura de salida
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fhoyIni = df.format(new Date());
		String fhoyFin = df.format(new Date());
		Date hoy = df.parse(fhoyIni);
		Date hoyfin = df.parse(fhoyFin);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		hoy = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		hoyfin = calendar.getTime();
		//Usuario usuario = Login.getUsuarioLogin();
		List<Documento> factDia = documentoService.getByTipo(tipoDocumentoId, hoy, hoyfin,usuario.getUsuarioId());
		Double total = 0.0;
		for (Documento d : factDia) {
			if (d.getTotal() != null) {
				total = total + d.getTotal().doubleValue();
			}
		}
		return total;
	}

}
