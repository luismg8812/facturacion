package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.GrupoDao;
import com.fact.dao.RolDao;
import com.fact.model.Grupo;
import com.fact.model.Rol;
import com.fact.service.GrupoService;
import com.fact.service.RolService;

@Stateless
public class GrupoServiceImpl implements GrupoService{

	 @EJB
	 private GrupoDao grupoDao;
	
	
	

	@Override
	public Grupo getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return grupoDao.getById(id);
	}

	@Override
	public Grupo getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return grupoDao.getByName(nombre);
	}

	@Override
	public List<Grupo> getByAll() throws FactException {
		return grupoDao.getByAll();
	}

	@Override
	public void save(Grupo grupo) throws FactException {
		grupoDao.save(grupo);
		
	}

	@Override
	public void update(Grupo grupo) throws FactException {
		grupoDao.update(grupo);
		
	}

	@Override
	public void delete(Grupo grupo) throws FactException {
		grupoDao.delete(grupo);
		
	}
	
}
