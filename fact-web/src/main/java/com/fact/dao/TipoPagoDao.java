package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoPago;

@Local
public interface TipoPagoDao {

	
	TipoPago getById(Long Id)throws FactException;
	TipoPago getByName(String nombre) throws FactException;
	List<TipoPago> getByAll() throws FactException;
	
	
}
