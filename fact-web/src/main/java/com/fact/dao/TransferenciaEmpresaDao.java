package com.fact.dao;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TransferenciaEmpresa;

@Local
public interface TransferenciaEmpresaDao {

	void save(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	void update(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	void delete(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	TransferenciaEmpresa getById(Long abono)throws FactException;
	List<TransferenciaEmpresa> find(Date fechaIni, Date fechaFin, Long desdeReporte, Long hastaReporte)throws FactException;;
		
}
