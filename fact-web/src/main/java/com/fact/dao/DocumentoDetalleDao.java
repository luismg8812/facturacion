package com.fact.dao;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.Empresa;

@Local
public interface DocumentoDetalleDao {

	void save(DocumentoDetalle documentoDetalle,Long server)throws FactException;
	void update(DocumentoDetalle documentoDetalle,Long server)throws FactException;
	void delete(DocumentoDetalle documentoDetalle)throws FactException;
	DocumentoDetalle getById(Long documentoDetalle)throws FactException;
	List<DocumentoDetalle> getByDocumento(Long documentoId,Long server) throws FactException;
	List<DocumentoDetalle> getByDocumento(List<Documento> documentos)throws FactException;
	void borrar(Long documentoDetalleId,Long estado, Long server)throws FactException;
	List<DocumentoDetalle> getPropinasByUsuario(Long tipoDocumentoId, Date hoy, Date hoyfin, Date hoyfin2,Long usuarioId, Boolean conCierre, Long server)throws FactException;
	List<DocumentoDetalle> getbyEmpleado(Long empleadoId, Date hoy, Date hoyfin)throws FactException;
	List<DocumentoDetalle> getByProductoId(long productoId,Date hoy, Date hoyfin)throws FactException;
	List<DocumentoDetalle> getCardex(Empresa empresa, Long productoId, Date fechaIni, Date fechaFin);
	List<DocumentoDetalle> getByTipoAndFecha(Long tipoDocumento, Date fechaInicial, Date fechaFinal);
	
}
