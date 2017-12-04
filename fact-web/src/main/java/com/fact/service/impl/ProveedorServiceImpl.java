package com.fact.service.impl;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.ProveedorDao;
import com.fact.model.Proveedor;
import com.fact.service.ProveedorService;

@Stateless
public class ProveedorServiceImpl implements ProveedorService{

	 @EJB
	 private ProveedorDao proveedorDao;
	
	@Override
	public void save(Proveedor proveedor) throws FactException {	
		proveedorDao.save(proveedor);
	}

	@Override
	public void update(Proveedor proveedor) throws FactException {
		if(proveedor.getProveedorId()==null){
			throw new FactException("El id es obligatorio");
		}		
		proveedorDao.update(proveedor);
	}

	@Override
	public void delete(Proveedor proveedor) throws FactException {
		if(proveedor.getProveedorId()==null){
			throw new FactException("El id  es obligatorio");
		}
		proveedorDao.delete(proveedor);
	}

	@Override
	public Proveedor getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return proveedorDao.getById(id);
	}
	
	@Override
	public List<Proveedor> getByAll() throws FactException {
		return proveedorDao.getByAll();
	}
	
	@Override
	public BigDecimal getByUltimoId()throws FactException{
		return proveedorDao.getByUltimoId();
	}

	@Override
	public Proveedor getByName(String upperCase) {
		return proveedorDao.getByName(upperCase);
	}
	
}
