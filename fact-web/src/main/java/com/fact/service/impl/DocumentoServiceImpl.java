package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.DocumentoDao;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.InfoDiario;
import com.fact.service.DocumentoService;

@Stateless
public class DocumentoServiceImpl implements DocumentoService{

	 @EJB
	 private DocumentoDao documentoDao;
	
	@Override
	public void save(Documento documento, Long server) throws FactException {
		if(documento.getUsuarioId()==null){
			throw new FactException("El usuario no esta autorizado, por favor ingrese nuevamente");
		}	
		documentoDao.save(documento,server);
	}

	@Override
	public void update(Documento documento, Long server) throws FactException {
		if(documento.getDocumentoId()==null){
			throw new FactException("El id es obligatorio");
		}		
		documentoDao.update(documento,server);
	}

	@Override
	public void delete(Documento documento) throws FactException {
		if(documento.getDocumentoId()==null){
			throw new FactException("El documento es obligatorio");
		}
		documentoDao.delete(documento);
	}

	@Override
	public Documento getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return documentoDao.getById(id);
	}
	
	@Override
	public List<Documento> getByTipo(Long tipoDocumentoId,Date hoy,Date hoyFin,Long usuarioId) throws FactException {		
		return documentoDao.getByTipo(tipoDocumentoId,hoy,hoyFin,usuarioId);
	}

	@Override
	public List<Documento> getDocNoImp(Long usuarioId,List<Long> tipoDocumentoId,Long server) throws FactException {
		return documentoDao.getDocNoImp(usuarioId,tipoDocumentoId,server) ;
	}

	@Override
	public Documento getByLastAndTipo(Long idFactura,Long usuarioId) throws FactException {
		if(idFactura==null){
			throw new FactException("El tipo documento es obligatorio");
		}
		return documentoDao.getByLastAndTipo(idFactura, usuarioId);
	}

	@Override
	public List<Documento> getByTipoSinUsuario(List<Long> tipoDocumentoId, Date hoy, Date hoyfin) throws FactException {
		return documentoDao.getByTipoSinUsuario(tipoDocumentoId,hoy,hoyfin);
	}

	@Override
	public List<Documento> buscarPorAbonos(Long proveedorId, Long tipoFacturas, Date fechaInicio, Date fechafin,
			String detalle) throws FactException {
		return documentoDao.buscarPorAbonos(proveedorId,tipoFacturas,fechaInicio,fechafin,detalle);
	}

	@Override
	public List<Documento> getByFacturaByDia(Long tipoDocumentoId, Date hoy, Date hoyfin) throws FactException {
		return documentoDao.getByFacturaByDia(tipoDocumentoId,hoy,hoyfin);
	}

	@Override
	public List<Documento> getByHoyNoImpresas(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId)throws FactException {
		return documentoDao.getByHoyNoImpresas(tipoDocumentoId,hoy,hoyfin);
	}

	@Override
	public List<Documento> buscarPorAbonosByClient(Long clienteId, Date fechaInicio, Date fechafin)
			throws FactException {
		return documentoDao.buscarPorAbonosByClient(clienteId,fechaInicio,fechafin);
	}

	@Override
	public List<Documento> buscarPorFechaAndCajero(Long usuarioSelect, String documentoId, Date fechaIni,
			Date fechaFin, String conDian,Long clienteId)throws FactException {
		return documentoDao.buscarPorFechaAndCajero(usuarioSelect,documentoId,fechaIni,fechaFin,conDian,clienteId);
	}

	@Override
	public String getByUltimoId() throws FactException {
		return documentoDao.getByUltimoId();
	}

	@Override
	public List<Documento> getRemisionesByUsuario(Long tipoDocumentoId, Date hoy, Date hoyFin, Long usuarioId, Boolean conCierre,Long server)
			throws FactException {
		return documentoDao.getRemisionesByUsuario(tipoDocumentoId,hoy,hoyFin,usuarioId, conCierre,server);
	}

	@Override
	public void save(InfoDiario infoDiario) throws FactException {		
		documentoDao.save(infoDiario);		
	}

	@Override
	public void update(InfoDiario infoDiario) throws FactException {
		if(infoDiario.getInfoDiarioId()==null){
			throw new FactException("El id es obligatorio");
		}		
		documentoDao.update(infoDiario);
		
	}

	@Override
	public List<InfoDiario> buscarInfodiarioByFecha(Date fechaInicio, Date fechafin) throws FactException {
		return documentoDao.buscarInfodiarioByFecha(fechaInicio,fechafin);
	}

	@Override
	public List<Documento> getByfacturasReales(List<Long> tipoDocumentoId, Date hoy, Date hoyFin, Long usuarioId, Boolean conCierre,Long server)
			throws FactException {
		return documentoDao.getByfacturasReales(tipoDocumentoId,hoy,hoyFin,usuarioId,conCierre,server);
	}

	@Override
	public List<Documento> getByFacturaSinCierre(List<Long> tipoDocumentoId, Date hoy, Date hoyfin)
			throws FactException {
		return documentoDao.getByFacturaSinCierre(tipoDocumentoId,hoy,hoyfin);
	}

	@Override
	public Documento getByConsecutivoDian(String documento) throws FactException {
		return documentoDao.getByConsecutivoDian(documento);
	}

	@Override
	public List<Documento> getByEntrega(Long entrega) throws FactException {
		return documentoDao.getByEntrega(entrega);
	}

	@Override
	public List<Documento> getByTipoPago(List<Long> tipoPago, Date hoy, Date hoyFin, Long usuarioId, Boolean conCierre,
			Long server) throws FactException {
		return documentoDao.getByTipoPago(tipoPago,hoy,hoyFin,usuarioId, conCierre,server);
	}

	@Override
	public List<String> getMagList() throws FactException {
		return documentoDao.getMagList();
	}

	@Override
	public List<Documento> getByMacAndTipoDocumento(List<Long> tipoDocumentoId, String mac, Date hoy, Date hoyfin,
			Boolean conCierre, Long server) throws FactException {
			return documentoDao.getByMacAndTipoDocumento(tipoDocumentoId,mac,hoy,hoyfin,conCierre,server);
	}

	@Override
	public List<DocumentoDetalle> getByGrupo(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId,
			Boolean conCierre, Long server, Long grupoId) throws FactException {
		return documentoDao.getByGrupo(tipoDocumentoId,hoy,hoyfin,usuarioId,conCierre,server,grupoId);
	}

	@Override
	public List<Documento> getByCliente(Long clienteId, List<Long> tipoDocumentoId, Date fechaInicio, Date fechafin)
			throws FactException {
		return documentoDao.getByCliente(clienteId,tipoDocumentoId,fechaInicio,fechafin);
	}
	
}
