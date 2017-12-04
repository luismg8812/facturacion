package com.fact.dao;



import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Proveedor;

@Local
public interface ProveedorDao {

	void save(Proveedor proveedor)throws FactException;
	void update(Proveedor proveedor)throws FactException;
	void delete(Proveedor proveedor)throws FactException;
	Proveedor getById(Long proveedor)throws FactException;
	List<Proveedor> getByAll() throws FactException;
	BigDecimal getByUltimoId()throws FactException;
	Proveedor getByName(String upperCase);
}
