package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.SubMenu;

@Local
public interface SubMenuDao {

	
	SubMenu getById(Long Id)throws FactException;
	SubMenu getByName(String nombre) throws FactException;
	List<SubMenu> getByAll() throws FactException;
	
	
}
