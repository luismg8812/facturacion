package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.ProductoEmpresaDao;
import com.fact.model.ProductoEmpresa;
import com.fact.service.ProductoEmpresaService;

@Stateless
public class ProductoEmpresaServiceImpl implements ProductoEmpresaService{

	 @EJB
	 private ProductoEmpresaDao productoEmpresaDao;
	
	@Override
	public void save(ProductoEmpresa productoEmpresa) throws FactException {			
		productoEmpresaDao.save(productoEmpresa);
	}

	@Override
	public void update(ProductoEmpresa productoEmpresa) throws FactException {
		if(productoEmpresa.getProductoEmpresaId()==null){
			throw new FactException("El id es obligatorio");
		}		
		productoEmpresaDao.update(productoEmpresa);
	}

	@Override
	public void delete(ProductoEmpresa productoEmpresa) throws FactException {
		if(productoEmpresa.getProductoEmpresaId()==null){
			throw new FactException("El documento es obligatorio");
		}
		productoEmpresaDao.delete(productoEmpresa);
	}

	@Override
	public ProductoEmpresa getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return productoEmpresaDao.getById(id);
	}

	@Override
	public List<ProductoEmpresa> getByEmpresa(Long empresaId) throws FactException {
		return productoEmpresaDao.getByEmpresa(empresaId);		
	}
}
