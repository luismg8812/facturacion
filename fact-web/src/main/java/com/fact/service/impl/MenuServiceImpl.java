package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.MenuDao;
import com.fact.model.Menu;
import com.fact.service.MenuService;

@Stateless
public class MenuServiceImpl implements MenuService{

	 @EJB
	 private MenuDao menuDao;
	
	
	

	@Override
	public Menu getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return menuDao.getById(id);
	}

	@Override
	public Menu getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return menuDao.getByName(nombre);
	}

	@Override
	public List<Menu> getByAll() throws FactException {
		return menuDao.getByAll();
	}
	
	
}
