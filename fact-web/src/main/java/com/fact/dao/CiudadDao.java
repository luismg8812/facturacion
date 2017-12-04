package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Ciudad;

@Local
public interface CiudadDao {

	
	Ciudad getById(Long Id)throws FactException;
	Ciudad getByName(String nombre) throws FactException;
	List<Ciudad> getByAll() throws FactException;
	List<Ciudad> getByDepartamento(Long departamentoId) throws FactException;
	
}
