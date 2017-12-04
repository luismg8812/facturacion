package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.SubMenu;

@Local
public interface SubMenuService {

	SubMenu getById(Long id)throws FactException;
	SubMenu getByName(String nombre) throws FactException;
	List<SubMenu> getByAll() throws FactException;
	
}
