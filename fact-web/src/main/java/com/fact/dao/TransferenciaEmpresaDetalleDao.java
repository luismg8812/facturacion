package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TransferenciaEmpresaDetalle;

@Local
public interface TransferenciaEmpresaDetalleDao {

	void save(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle)throws FactException;
	void update(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle)throws FactException;
	void delete(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle)throws FactException;
	TransferenciaEmpresaDetalle getById(Long id)throws FactException;
	List<TransferenciaEmpresaDetalle> getByTrasferenciaId(Long transferenciaEmpresaId)throws FactException;

}
