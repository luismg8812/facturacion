package com.fact.service;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.EntregaMercancia;

@Local
public interface EntregaMercanciaService {
	void save(EntregaMercancia entregaMercancia)throws FactException;
	void update(EntregaMercancia entregaMercancia)throws FactException;
	void delete(EntregaMercancia entregaMercancia)throws FactException;
	EntregaMercancia getById(Long id)throws FactException;	
	List<EntregaMercancia> getByAll() throws FactException;
	List<EntregaMercancia> getEntregasByUsuario(Long usuarioId, Date inicio, Date fin)throws FactException;
}
