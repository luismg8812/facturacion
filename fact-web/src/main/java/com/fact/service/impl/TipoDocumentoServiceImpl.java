package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.TipoDocumentoDao;
import com.fact.model.TipoDocumento;
import com.fact.service.TipoDocumentoService;

@Stateless
public class TipoDocumentoServiceImpl implements TipoDocumentoService{

	 @EJB
	 private TipoDocumentoDao tipoDocumentoDao;
	
	
	

	@Override
	public TipoDocumento getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return tipoDocumentoDao.getById(id);
	}

	@Override
	public TipoDocumento getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return tipoDocumentoDao.getByName(nombre);
	}

	@Override
	public List<TipoDocumento> getByAll() throws FactException {
		return tipoDocumentoDao.getByAll();
	}

	@Override
	public List<TipoDocumento> getById(List<Long> ids) {
		return tipoDocumentoDao.getById(ids);
	}

	@Override
	public void update(TipoDocumento tipoDocumento) {
		 tipoDocumentoDao.update(tipoDocumento);
		
	}
	
	
}
