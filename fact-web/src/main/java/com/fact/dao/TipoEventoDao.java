package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoEvento;

@Local
public interface TipoEventoDao {

	
	TipoEvento getById(Long Id)throws FactException;
	TipoEvento getByName(String nombre) throws FactException;
	List<TipoEvento> getByAll() throws FactException;
	
	
}
