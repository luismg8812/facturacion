package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Menu;

@Local
public interface MenuService {

	Menu getById(Long id)throws FactException;
	Menu getByName(String nombre) throws FactException;
	List<Menu> getByAll() throws FactException;
	
}
