package com.fact.service;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.InfoDiario;

@Local
public interface DocumentoService {

	void save(Documento documento,Long server)throws FactException;
	void save(InfoDiario infoDiario)throws FactException;
	void update(Documento documento, Long server)throws FactException;
	void update(InfoDiario infoDiario)throws FactException;
	void delete(Documento documento)throws FactException;
	Documento getById(Long id)throws FactException;
	List<Documento> getByTipo(Long tipoDocumentoId,Date hoy,Date hoyFin,Long usuarioId) throws FactException;
	List<Documento> getDocNoImp(Long usuarioId, List<Long> tipoDocumentoId,Long server)throws FactException;
	Documento getByLastAndTipo(Long idFactura,Long usuarioId)throws FactException;
	List<Documento> getByTipoSinUsuario(List<Long> tipoDocumentoId, Date hoy, Date hoyfin) throws FactException;
	List<Documento> buscarPorAbonos(Long proveedorId, Long tipoFacturas, Date fechaInicio, Date fechafin,String detalle) throws FactException;
	List<Documento> getByFacturaByDia(Long tipoDocumentoId, Date hoy, Date hoyfin) throws FactException;
	List<Documento> getByHoyNoImpresas(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId) throws FactException;
	List<Documento> buscarPorAbonosByClient(Long clienteId, Date fechaInicio, Date fechafin)throws FactException;
	List<Documento> buscarPorFechaAndCajero(Long usuarioSelect, String documentoId, Date fechaIni, Date fechaFin,String conDian,Long clienteId)throws FactException;
	String getByUltimoId()throws FactException;
	List<Documento> getRemisionesByUsuario(Long tipoDocumentoId,Date hoy,Date hoyFin,Long usuarioId, Boolean conCierre,Long server) throws FactException;
	List<InfoDiario> buscarInfodiarioByFecha(Date fechaInicio, Date fechafin)throws FactException;
	List<Documento> getByfacturasReales(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId, Boolean sinCierre, Long server)throws FactException;
	List<Documento> getByFacturaSinCierre(List<Long> tipoDocumentoId, Date hoy, Date hoyfin)throws FactException;
	Documento getByConsecutivoDian(String documento)throws FactException;
	List<Documento> getByEntrega(Long entrega)throws FactException;
	List<Documento> getByTipoPago(List<Long> tipoPago, Date hoy, Date hoyfin, Long usuarioId, Boolean conCierre, Long server)throws FactException;
	List<String> getMagList()throws FactException;
	List<Documento> getByMacAndTipoDocumento(List<Long> tipoDocumentoId, String mac, Date hoy, Date hoyfin,
			Boolean conCierre, Long server)throws FactException;
	List<DocumentoDetalle> getByGrupo(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId, Boolean conCierre,
			Long server, Long grupoId)throws FactException;
	List<Documento> getByCliente(Long clienteId, List<Long> tipoDocumentoId, Date fechaInicio, Date fechafin)throws FactException;
}
