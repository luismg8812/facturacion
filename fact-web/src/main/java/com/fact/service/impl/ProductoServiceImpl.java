package com.fact.service.impl;



import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.ProductoDao;
import com.fact.model.Empresa;
import com.fact.model.Producto;
import com.fact.model.ProductoEmpresa;
import com.fact.model.SubProducto;
import com.fact.service.ProductoService;

@Stateless
public class ProductoServiceImpl implements ProductoService{

	 @EJB
	 private ProductoDao productoDao;
	
	@Override
	public void save(Producto producto,Long server) throws FactException {
			
		productoDao.save(producto,server);
	}

	@Override
	public void update(Producto producto,Long server) throws FactException {
		if(producto.getProductoId()==null){
			throw new FactException("El id es obligatorio");
		}		
		productoDao.update(producto,server);
	}

	@Override
	public void delete(Producto producto) throws FactException {
		if(producto.getProductoId()==null){
			throw new FactException("El documento es obligatorio");
		}
		productoDao.delete(producto);
	}

	@Override
	public Producto getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return productoDao.getById(id);
	}
	
	@Override
	public List<Producto> getByAll() throws FactException {
		return productoDao.getByAll();
	}
	
	@Override
	public BigDecimal getByUltimoId()throws FactException{
		return productoDao.getByUltimoId();
	}

	@Override
	public List<Producto> find(List<Long> marcaFilter, Double iva, Long codigo, Long tipoPeso,  List<Long> proveedorFilter,List<Long> grupoFilter) throws FactException {
		return productoDao.find(marcaFilter,iva,codigo,tipoPeso,proveedorFilter,grupoFilter);
	}

	@Override
	public List<Producto> getByList(List<Producto> nombProductos, Long server) {
		return productoDao.getByList(nombProductos,server);
	}

	@Override
	public List<Producto> getBySubProducto(int subProducto) throws FactException {
		return productoDao.getBySubProducto(subProducto);
	}

	@Override
	public List<SubProducto> subProductoByProducto(Long productoPadre) throws FactException {
		return productoDao.subProductoByProducto(productoPadre);
	}

	@Override
	public void save(SubProducto sub) throws FactException {
		productoDao.save(sub);
	}

	@Override
	public void delete(SubProducto s) throws FactException {
		if(s.getSubProductoId()==null){
			throw new FactException("El documento es obligatorio");
		}
		productoDao.delete(s);
	}

	@Override
	public Producto getByCodigoBarras(Long codigoBarrasNew) throws FactException {
		return productoDao.getByCodigoBarras(codigoBarrasNew);
	}

	@Override
	public List<Producto> getByGrupo(Long grupoId) throws FactException {
		if(grupoId==null){
			throw new FactException("El id del grupo es obligatorio");
		}
		return productoDao.getByGrupo(grupoId);
	}

	@Override
	public List<Producto> getAllByCompany(Empresa empresa) throws FactException {
		return productoDao.getAllByCompany(empresa);
	}

	@Override
	public List<ProductoEmpresa> getProductoByEmpresa(Long empresaId) throws FactException {
		return productoDao.getProductoByEmpresa(empresaId);
	}
	
}
