package com.fact.service.impl;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.MarcaDao;
import com.fact.model.Marca;
import com.fact.service.MarcaService;

@Stateless
public class MarcaServiceImpl implements MarcaService{

	 @EJB
	 private MarcaDao marcaDao;
	
	@Override
	public void save(Marca marca) throws FactException {	
		marcaDao.save(marca);
	}

	@Override
	public void update(Marca marca) throws FactException {
		if(marca.getMarcaId()==null){
			throw new FactException("El id es obligatorio");
		}		
		marcaDao.update(marca);
	}

	@Override
	public void delete(Marca marca) throws FactException {
		if(marca.getMarcaId()==null){
			throw new FactException("El id  es obligatorio");
		}
		marcaDao.delete(marca);
	}

	@Override
	public Marca getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return marcaDao.getById(id);
	}
	
	@Override
	public List<Marca> getByAll() throws FactException {
		return marcaDao.getByAll();
	}
	
	@Override
	public BigDecimal getByUltimoId()throws FactException{
		return marcaDao.getByUltimoId();
	}

	@Override
	public Marca getByName(String name) {
		return marcaDao.getByName(name);
	}
	
}
