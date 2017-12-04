package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.EventoDao;
import com.fact.model.Evento;
import com.fact.service.EventoService;

@Stateless
public class EventoServiceImpl implements EventoService{

	 @EJB
	 private EventoDao eventoDao;
	
	
	@Override
	public void save(Evento evento) throws FactException {
		eventoDao.save(evento);
	}

	@Override
	public void update(Evento evento) throws FactException {
		if(evento.getEventoId()==null){
			throw new FactException("El id es obligatorio");
		}		
		eventoDao.update(evento);
	}

	@Override
	public void delete(Evento evento) throws FactException {
		if(evento.getUsuarioId()==null){
			throw new FactException("El documento es obligatorio");
		}
		eventoDao.delete(evento);
	}

	@Override
	public Evento getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return eventoDao.getById(id);
	}

	@Override
	public List<Evento> getByAll() throws FactException {
		return eventoDao.getByAll();
	}

	@Override
	public List<Evento> getByFechaAndTipo(Date fechaInicio,Date fechaFin, Long tipoEvento) throws FactException {
		return eventoDao.getByFechaAndTipo(fechaInicio,fechaFin,tipoEvento);
	}
	
	
}
