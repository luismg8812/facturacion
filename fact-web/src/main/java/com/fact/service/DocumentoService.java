package com.fact.service;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.model.ConsecutivoDian;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.InfoDiario;
import com.fact.model.Usuario;
import com.fact.vo.ExportarDetalleDeltaVo;

@Local
public interface DocumentoService {

	void save(Documento documento,Long server);
	void save(InfoDiario infoDiario);
	void update(Documento documento, Long server);
	void update(InfoDiario infoDiario);
	void delete(Documento documento);
	Documento getById(Long id);
	List<Documento> getByTipo(Long tipoDocumentoId,Date hoy,Date hoyFin,List<Usuario> usuarioId,Boolean conCierre) ;
	List<Documento> getDocNoImp(Long usuarioId, List<Long> tipoDocumentoId,Long server);
	Documento getByLastAndTipo(Long idFactura,Long usuarioId);
	List<Documento> getByTipoSinUsuario(List<Long> tipoDocumentoId, Date hoy, Date hoyfin) ;
	List<Documento> buscarPorAbonos(Long proveedorId, Long tipoFacturas, Date fechaInicio, Date fechafin,String detalle) ;
	List<Documento> getByFacturaByDia(Long tipoDocumentoId, Date hoy, Date hoyfin,Boolean conCierre) ;
	List<Documento> getByHoyNoImpresas(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId) ;
	List<Documento> buscarPorAbonosByClient(Long clienteId, Date fechaInicio, Date fechafin);
	List<Documento> buscarPorFechaAndCajero(Long usuarioSelect, String documentoId, Date fechaIni, Date fechaFin,String conDian,Long clienteId, Long tipoDocumento);
	String getByUltimoId();
	List<Documento> getRemisionesByUsuario(Long tipoDocumentoId,Long usuarioId, Boolean conCierre,Long server) ;
	List<Documento> getRemisionesByUsuarioConFecha(Long tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId,
			Boolean conCierre, Long server);
	List<InfoDiario> buscarInfodiarioByFecha(Date fechaInicio, Date fechafin);
	List<Documento> getByfacturasReales(List<Long> tipoDocumentoId, Long usuarioId, Boolean sinCierre, Long server);
	List<Documento> getByfacturasRealesConFecha(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId,
			Boolean false1, Long l);
	List<Documento> getByFacturaSinCierre(List<Long> tipoDocumentoId, Date hoy, Date hoyfin);
	Documento getByConsecutivoDian(String documento);
	List<Documento> getByEntrega(Long entrega);
	List<Documento> getByTipoPago(List<Long> tipoPago, Date hoy, Date hoyfin, Long usuarioId, Boolean conCierre, Long server);
	List<String> getMagList();
	List<Documento> getByMacAndTipoDocumento(List<Long> tipoDocumentoId, String mac, Date hoy, Date hoyfin,
			Boolean conCierre, Long server) ;
	List<DocumentoDetalle> getByGrupo(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId, Boolean conCierre,
			Long server, Long grupoId);
	List<Documento> getByCliente(Long clienteId, List<Long> tipoDocumentoId, Date fechaInicio, Date fechafin);
	List<Documento> getByClienteAndProveedorAndTipo(Long clienteId, Long proveedorId,List<Long> tipoId);
	List<Documento> getByProveedor(Long proveedorId, List<Long> tipoDocumentoId, Date fechaInicio, Date fechafin);
	List<Documento> buscarPorInvoice(long invoice);
	ConsecutivoDian getConsecutivoDian();
	void update(ConsecutivoDian consecutivoDian);
	List<Documento> getconvinacion(Long tipodocumentoId, Long tipoPago, Date fechaInicio, Date fechafin);
	Documento getByDetalleEntrada(String detalleEntrada);
	List<ExportarDetalleDeltaVo> getDocumentosSalidasDelta(Long tipodocumentoId, Date fechaInicial, Date fechaFinal, String string);
	List<ExportarDetalleDeltaVo> getDocumentosEntradasDelta(Long tipodocumentoId, Date fechaInicial, Date fechaFinal,
			String string);
	
}
