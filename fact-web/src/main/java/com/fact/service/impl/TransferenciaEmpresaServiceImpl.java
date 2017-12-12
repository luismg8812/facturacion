package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.TransferenciaEmpresaDao;
import com.fact.model.TransferenciaEmpresa;
import com.fact.service.TransferenciaEmpresaService;

@Stateless
public class TransferenciaEmpresaServiceImpl implements TransferenciaEmpresaService{

	 @EJB
	 private TransferenciaEmpresaDao transferenciaEmpresaDao;
	
	@Override
	public void save(TransferenciaEmpresa transferenciaEmpresa) throws FactException {			
		transferenciaEmpresaDao.save(transferenciaEmpresa);
	}

	@Override
	public void update(TransferenciaEmpresa transferenciaEmpresa) throws FactException {
		if(transferenciaEmpresa.getTransferenciaEmpresaId()==null){
			throw new FactException("El id es obligatorio");
		}		
		transferenciaEmpresaDao.update(transferenciaEmpresa);
	}

	@Override
	public void delete(TransferenciaEmpresa transferenciaEmpresa) throws FactException {
		if(transferenciaEmpresa.getTransferenciaEmpresaId()==null){
			throw new FactException("El documento es obligatorio");
		}
		transferenciaEmpresaDao.delete(transferenciaEmpresa);
	}

	@Override
	public TransferenciaEmpresa getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return transferenciaEmpresaDao.getById(id);
	}

	@Override
	public List<TransferenciaEmpresa> find(Date fechaIni, Date fechaFin, Long desdeReporte, Long hastaReporte)
			throws FactException {		
		return transferenciaEmpresaDao.find(fechaIni,fechaFin,desdeReporte,hastaReporte);
	}
	

	
}
