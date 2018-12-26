package com.fact.dao;



import java.math.BigInteger;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Empresa;
import com.fact.model.Producto;
import com.fact.model.ProductoEmpresa;
import com.fact.model.SubProducto;

@Local
public interface ProductoDao {

	void save(Producto producto,Long server)throws FactException;
	void update(Producto producto,Long server)throws FactException;
	void delete(Producto producto)throws FactException;
	Producto getById(Long producto)throws FactException;
	List<Producto> getByAll() throws FactException;
	BigInteger getByUltimoId()throws FactException;
	List<Producto> find(List<Long> marcaFilter, Double iva, Long codigo, Long tipoPeso,  List<Long> proveedorFilter,List<Long> grupoFilter)throws FactException;
	List<Producto> getByList(List<Producto> nombProductos, Long server);
	List<Producto> getBySubProducto(int subProducto)throws FactException;
	List<SubProducto> subProductoByProducto(Long productoPadre) throws FactException;
	void save(SubProducto sub) throws FactException;
	void delete(SubProducto s) throws FactException;
	Producto getByCodigoBarras(String codigoBarrasNew) throws FactException;
	List<Producto> getByGrupo(Long grupoId)throws FactException;
	List<Producto> getAllByCompany(Empresa empresa)throws FactException;
	List<ProductoEmpresa> getProductoByEmpresa(Long empresaId);
}
