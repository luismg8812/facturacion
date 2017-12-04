package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.TipoPagoDao;
import com.fact.model.TipoPago;
import com.fact.service.TipoPagoService;

@Stateless
public class TipoPagoServiceImpl implements TipoPagoService{

	 @EJB
	 private TipoPagoDao tipoPagoDao;
	
	
	

	@Override
	public TipoPago getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return tipoPagoDao.getById(id);
	}

	@Override
	public TipoPago getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return tipoPagoDao.getByName(nombre);
	}

	@Override
	public List<TipoPago> getByAll() throws FactException {
		return tipoPagoDao.getByAll();
	}
	
	
}
