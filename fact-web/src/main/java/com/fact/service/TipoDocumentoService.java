package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TipoDocumento;

@Local
public interface TipoDocumentoService {

	TipoDocumento getById(Long id)throws FactException;
	TipoDocumento getByName(String nombre) throws FactException;
	List<TipoDocumento> getByAll() throws FactException;
	//List<Documento> getByDocumento(Long tipoDocumentoId) throws FactException;
	List<TipoDocumento> getById(List<Long> ids);
	
}
