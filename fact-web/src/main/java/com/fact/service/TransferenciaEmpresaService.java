package com.fact.service;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.TransferenciaEmpresa;

@Local
public interface TransferenciaEmpresaService {
	void save(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	void update(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	void delete(TransferenciaEmpresa transferenciaEmpresa)throws FactException;
	TransferenciaEmpresa getById(Long id)throws FactException;
	List<TransferenciaEmpresa> find(Date fechaIni, Date fechaFin, Long desdeReporte, Long hastaReporte)throws FactException;	
}
