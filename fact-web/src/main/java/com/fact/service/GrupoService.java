package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Grupo;

@Local
public interface GrupoService {

	Grupo getById(Long id)throws FactException;
	Grupo getByName(String nombre) throws FactException;
	List<Grupo> getByAll() throws FactException;
	void save(Grupo grupo)throws FactException;
	void update(Grupo grupo)throws FactException;
	void delete(Grupo grupo)throws FactException;
	
}
