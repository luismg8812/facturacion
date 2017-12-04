package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Menu;

@Local
public interface MenuDao {

	
	Menu getById(Long Id)throws FactException;
	Menu getByName(String nombre) throws FactException;
	List<Menu> getByAll() throws FactException;
	
	
}
