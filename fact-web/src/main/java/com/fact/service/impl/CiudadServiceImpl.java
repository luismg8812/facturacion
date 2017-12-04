package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.CiudadDao;
import com.fact.model.Ciudad;
import com.fact.service.CiudadService;

@Stateless
public class CiudadServiceImpl implements CiudadService{

	 @EJB
	 private CiudadDao ciudadDao;
	
	
	

	@Override
	public Ciudad getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return ciudadDao.getById(id);
	}

	@Override
	public Ciudad getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return ciudadDao.getByName(nombre);
	}

	@Override
	public List<Ciudad> getByAll() throws FactException {
		return ciudadDao.getByAll();
	}

	@Override
	public List<Ciudad> getByDepartamento(Long departamentoId) throws FactException {
		return ciudadDao.getByDepartamento(departamentoId);
	}
	
	
}
