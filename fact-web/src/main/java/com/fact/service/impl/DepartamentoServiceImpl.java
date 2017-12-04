package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.DepartamentoDao;
import com.fact.model.Departamento;
import com.fact.service.DepartamentoService;

@Stateless
public class DepartamentoServiceImpl implements DepartamentoService{

	 @EJB
	 private DepartamentoDao departamentoDao;
	
	
	

	@Override
	public Departamento getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return departamentoDao.getById(id);
	}

	@Override
	public Departamento getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return departamentoDao.getByName(nombre);
	}

	@Override
	public List<Departamento> getByAll() throws FactException {
		return departamentoDao.getByAll();
	}
	
	
}
