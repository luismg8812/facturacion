package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.TipoEventoDao;
import com.fact.model.TipoEvento;
import com.fact.service.TipoEventoService;

@Stateless
public class TipoEventoServiceImpl implements TipoEventoService{

	 @EJB
	 private TipoEventoDao tipoEventoDao;
	
	
	

	@Override
	public TipoEvento getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return tipoEventoDao.getById(id);
	}

	@Override
	public TipoEvento getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return tipoEventoDao.getByName(nombre);
	}

	@Override
	public List<TipoEvento> getByAll() throws FactException {
		return tipoEventoDao.getByAll();
	}
	
	
}
