package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoPago;

@Local
public interface TipoPagoService {

	TipoPago getById(Long id)throws FactException;
	TipoPago getByName(String nombre) throws FactException;
	List<TipoPago> getByAll() throws FactException;
	
}
