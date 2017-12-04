package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.EntregaMercanciaDao;
import com.fact.model.EntregaMercancia;
import com.fact.service.EntregaMercanciaService;

@Stateless
public class EntregaMercanciaServiceImpl implements EntregaMercanciaService{

	 @EJB
	 private EntregaMercanciaDao entregaMercanciaDao;
	
	@Override
	public void save(EntregaMercancia entregaMercancia) throws FactException {	
		entregaMercanciaDao.save(entregaMercancia);
	}

	@Override
	public void update(EntregaMercancia entregaMercancia) throws FactException {
		if(entregaMercancia.getEntregaMercanciaId()==null){
			throw new FactException("El id es obligatorio");
		}		
		entregaMercanciaDao.update(entregaMercancia);
	}

	@Override
	public void delete(EntregaMercancia entregaMercancia) throws FactException {
		if(entregaMercancia.getEntregaMercanciaId()==null){
			throw new FactException("El id  es obligatorio");
		}
		entregaMercanciaDao.delete(entregaMercancia);
	}

	@Override
	public EntregaMercancia getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return entregaMercanciaDao.getById(id);
	}
	
	@Override
	public List<EntregaMercancia> getByAll() throws FactException {
		return entregaMercanciaDao.getByAll();
	}

	@Override
	public List<EntregaMercancia> getEntregasByUsuario(Long usuarioId, Date inicio, Date fin) throws FactException {
		return entregaMercanciaDao.getEntregasByUsuario(usuarioId,inicio,fin);
	}
	
}
