package com.fact.service.impl;



import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.ListaDao;
import com.fact.model.Lista;
import com.fact.service.ListaService;

@Stateless
public class ListaServiceImpl implements ListaService{

	 @EJB
	 private ListaDao listaDao;
	
	@Override
	public void save(Lista lista) throws FactException {			
		listaDao.save(lista);
	}

	@Override
	public void update(Lista lista) throws FactException {
		if(lista.getListaId()==null){
			throw new FactException("El id es obligatorio");
		}		
		listaDao.update(lista);
	}

	@Override
	public void delete(Lista lista) throws FactException {
		if(lista.getListaId()==null){
			throw new FactException("El documento es obligatorio");
		}
		listaDao.delete(lista);
	}

	@Override
	public Lista getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return listaDao.getById(id);
	}

	@Override
	public Lista getByProductoId(Long productoId) {
		return listaDao.getByProductoId(productoId);
	}
		
}
