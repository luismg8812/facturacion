package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Rol;

@Local
public interface RolService {

	Rol getById(Long id)throws FactException;
	Rol getByName(String nombre) throws FactException;
	List<Rol> getByAll() throws FactException;
	List<Rol> getSinPropietario()throws FactException;;
	
}
