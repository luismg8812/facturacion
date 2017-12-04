package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Ciudad;

@Local
public interface CiudadService {

	Ciudad getById(Long id)throws FactException;
	Ciudad getByName(String nombre) throws FactException;
	List<Ciudad> getByAll() throws FactException;
	List<Ciudad> getByDepartamento(Long departamentoId) throws FactException;
	
}
