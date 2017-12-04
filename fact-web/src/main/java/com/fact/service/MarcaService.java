package com.fact.service;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Marca;

@Local
public interface MarcaService {
	void save(Marca marca)throws FactException;
	void update(Marca marca)throws FactException;
	void delete(Marca marca)throws FactException;
	Marca getById(Long id)throws FactException;	
	List<Marca> getByAll() throws FactException;
	BigDecimal getByUltimoId()throws FactException;
	Marca getByName(String nombre);
}
