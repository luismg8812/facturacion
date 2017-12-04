package com.fact.service;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Cliente;

@Local
public interface ClienteService {
	void save(Cliente cliente)throws FactException;
	void update(Cliente cliente)throws FactException;
	void delete(Cliente cliente)throws FactException;
	Cliente getById(Long id)throws FactException;	
	List<Cliente> getByAll() throws FactException;
	BigDecimal getByUltimoId()throws FactException;
	Cliente getByName(String upperCase);
	List<Cliente> getByDocumento(String documento)throws FactException;
}
