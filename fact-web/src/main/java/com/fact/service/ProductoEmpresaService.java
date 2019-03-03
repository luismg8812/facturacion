package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Empresa;
import com.fact.model.ProductoEmpresa;

@Local
public interface ProductoEmpresaService {
	void save(ProductoEmpresa productoEmpresa)throws FactException;
	void update(ProductoEmpresa productoEmpresa)throws FactException;
	void delete(ProductoEmpresa productoEmpresa)throws FactException;
	ProductoEmpresa  getById(Long id)throws FactException;
	List<ProductoEmpresa> getByEmpresa(Long empresaId)throws FactException;
	ProductoEmpresa getByProductoAndEmpresa(Empresa ed, Long productoId)throws FactException;
	List<ProductoEmpresa> getByProveedorYGrupo(Long grupo, Long proveedor, Long empresa, Boolean negativos);
}
