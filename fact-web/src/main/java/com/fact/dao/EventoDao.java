package com.fact.dao;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Evento;

@Local
public interface EventoDao {

	void save(Evento evento)throws FactException;
	void update(Evento evento)throws FactException;
	void delete(Evento evento)throws FactException;
	Evento getById(Long eventoId)throws FactException;
	List<Evento> getByAll() throws FactException;
	List<Evento> getByFechaAndTipo(Date fechaInicio,Date fechaFin, Long tipoEvento) throws FactException;
	
}
