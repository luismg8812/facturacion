package com.fact.service.impl;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.ClienteDao;
import com.fact.model.Cliente;
import com.fact.service.ClienteService;

@Stateless
public class ClienteServiceImpl implements ClienteService{

	 @EJB
	 private ClienteDao clienteDao;
	
	@Override
	public void save(Cliente cliente) throws FactException {	
		clienteDao.save(cliente);
	}

	@Override
	public void update(Cliente cliente) throws FactException {
		if(cliente.getClienteId()==null){
			throw new FactException("El id es obligatorio");
		}		
		clienteDao.update(cliente);
	}

	@Override
	public void delete(Cliente cliente) throws FactException {
		if(cliente.getClienteId()==null){
			throw new FactException("El id  es obligatorio");
		}
		clienteDao.delete(cliente);
	}

	@Override
	public Cliente getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return clienteDao.getById(id);
	}
	
	@Override
	public List<Cliente> getByAll() throws FactException {
		return clienteDao.getByAll();
	}
	
	@Override
	public BigDecimal getByUltimoId()throws FactException{
		return clienteDao.getByUltimoId();
	}

	@Override
	public Cliente getByName(String upperCase) {
		return clienteDao.getByName(upperCase);
	}

	@Override
	public List<Cliente> getByDocumento(String documento) throws FactException {
		return clienteDao.getByDocumento(documento);
	}
	
}
