package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoDocumento;

@Local
public interface TipoDocumentoDao {

	
	TipoDocumento getById(Long Id)throws FactException;
	TipoDocumento getByName(String nombre) throws FactException;
	List<TipoDocumento> getByAll() throws FactException;
	List<TipoDocumento> getById(List<Long> ids)throws FactException;
	
	
}
