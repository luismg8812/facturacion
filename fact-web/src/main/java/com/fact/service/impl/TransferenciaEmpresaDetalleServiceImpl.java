package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.TransferenciaEmpresaDetalleDao;
import com.fact.model.TransferenciaEmpresaDetalle;
import com.fact.service.TransferenciaEmpresaDetalleService;

@Stateless
public class TransferenciaEmpresaDetalleServiceImpl implements TransferenciaEmpresaDetalleService{

	 @EJB
	 private TransferenciaEmpresaDetalleDao transferenciaEmpresaDetalleDao;
	
	@Override
	public void save(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) throws FactException {			
		transferenciaEmpresaDetalleDao.save(transferenciaEmpresaDetalle);
	}

	@Override
	public void update(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) throws FactException {
		if(transferenciaEmpresaDetalle.getTransferenciaEmprDetalId()==null){
			throw new FactException("El id es obligatorio");
		}		
		transferenciaEmpresaDetalleDao.update(transferenciaEmpresaDetalle);
	}

	@Override
	public void delete(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) throws FactException {
		if(transferenciaEmpresaDetalle.getTransferenciaEmprDetalId()==null){
			throw new FactException("El id es obligatorio");
		}
		transferenciaEmpresaDetalleDao.delete(transferenciaEmpresaDetalle);
	}

	@Override
	public TransferenciaEmpresaDetalle getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return transferenciaEmpresaDetalleDao.getById(id);
	}

	@Override
	public List<TransferenciaEmpresaDetalle> getByTrasferenciaId(Long transferenciaEmpresaId) throws FactException {
		if(transferenciaEmpresaId==null){
			throw new FactException("El id es obligatorio");
		}
		return transferenciaEmpresaDetalleDao.getByTrasferenciaId(transferenciaEmpresaId);
	}	
}
