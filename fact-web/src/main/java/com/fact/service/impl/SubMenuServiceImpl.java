package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.SubMenuDao;
import com.fact.model.SubMenu;
import com.fact.service.SubMenuService;

@Stateless
public class SubMenuServiceImpl implements SubMenuService{

	 @EJB
	 private SubMenuDao subMenuDao;
	
	
	

	@Override
	public SubMenu getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return subMenuDao.getById(id);
	}

	@Override
	public SubMenu getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return subMenuDao.getByName(nombre);
	}

	@Override
	public List<SubMenu> getByAll() throws FactException {
		return subMenuDao.getByAll();
	}
	
	
}
