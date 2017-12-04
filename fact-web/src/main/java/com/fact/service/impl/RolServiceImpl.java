package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.RolDao;
import com.fact.model.Rol;
import com.fact.service.RolService;

@Stateless
public class RolServiceImpl implements RolService{

	 @EJB
	 private RolDao rolDao;
	
	
	

	@Override
	public Rol getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return rolDao.getById(id);
	}

	@Override
	public Rol getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return rolDao.getByName(nombre);
	}

	@Override
	public List<Rol> getByAll() throws FactException {
		return rolDao.getByAll();
	}

	@Override
	public List<Rol> getSinPropietario() throws FactException {
		return rolDao.getSinPropietario();
	}
	
	
}
