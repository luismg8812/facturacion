package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Rol;

@Local
public interface RolDao {
	Rol getById(Long Id)throws FactException;
	Rol getByName(String nombre) throws FactException;
	List<Rol> getByAll() throws FactException;
	List<Rol> getSinPropietario() throws FactException;;
		
}
