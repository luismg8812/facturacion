package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Departamento;

@Local
public interface DepartamentoService {

	Departamento getById(Long id)throws FactException;
	Departamento getByName(String nombre) throws FactException;
	List<Departamento> getByAll() throws FactException;
	
}
