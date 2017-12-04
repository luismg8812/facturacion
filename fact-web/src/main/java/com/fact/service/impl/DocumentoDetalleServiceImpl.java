package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.DocumentoDetalleDao;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.service.DocumentoDetalleService;

@Stateless
public class DocumentoDetalleServiceImpl implements DocumentoDetalleService{

	 @EJB
	 private DocumentoDetalleDao documentoDetalleDao;
	
	@Override
	public void save(DocumentoDetalle documentoDetalle,Long server) throws FactException {
			
		documentoDetalleDao.save(documentoDetalle, server);
	}

	@Override
	public void update(DocumentoDetalle documentoDetalle,Long server) throws FactException {
		if(documentoDetalle.getDocumentoId()==null){
			throw new FactException("El id es obligatorio");
		}		
		documentoDetalleDao.update(documentoDetalle, server);
	}

	@Override
	public void delete(DocumentoDetalle documentoDetalle) throws FactException {
		if(documentoDetalle.getDocumentoId()==null){
			throw new FactException("El documento es obligatorio");
		}
		documentoDetalleDao.delete(documentoDetalle);
	}

	@Override
	public DocumentoDetalle getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return documentoDetalleDao.getById(id);
	}
	
	@Override
	public List<DocumentoDetalle> getByDocumento(Long documentoId, Long server) throws FactException {
		return documentoDetalleDao.getByDocumento(documentoId, server);
	}

	@Override
	public List<DocumentoDetalle> getByDocumento(List<Documento> documentos) throws FactException {
		return documentoDetalleDao.getByDocumento(documentos);
	}

	@Override
	public void borrar(Long documentoDetalleId,Long estado, Long server) throws FactException {
		documentoDetalleDao.borrar(documentoDetalleId,estado,server);
		
	}

	@Override
	public List<DocumentoDetalle> getPropinasByUsuario(Long tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId,
			Boolean conCierre, Long server) {
		return documentoDetalleDao.getPropinasByUsuario(tipoDocumentoId,hoy,hoyfin,hoyfin,usuarioId,conCierre,server);
	}

	@Override
	public List<DocumentoDetalle> getbyEmpleado(Long empleadoId, Date hoy, Date hoyfin) throws FactException {
		if(empleadoId==null){
			throw new FactException("El documento es obligatorio");
		}
		return documentoDetalleDao.getbyEmpleado(empleadoId,  hoy, hoyfin);
	}
	
}
