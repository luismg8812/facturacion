package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Departamento;

@Local
public interface DepartamentoDao {

	
	Departamento getById(Long Id)throws FactException;
	Departamento getByName(String nombre) throws FactException;
	List<Departamento> getByAll() throws FactException;
	
	
}
