package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoEvento;

@Local
public interface TipoEventoService {

	TipoEvento getById(Long id)throws FactException;
	TipoEvento getByName(String nombre) throws FactException;
	List<TipoEvento> getByAll() throws FactException;
}
