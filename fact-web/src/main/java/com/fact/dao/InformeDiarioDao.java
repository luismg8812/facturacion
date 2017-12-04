package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.InfoDiario;

@Local
public interface InformeDiarioDao {
	void save(InfoDiario infoDiario )throws FactException;
	void update(InfoDiario infoDiario)throws FactException;
	void delete(InfoDiario infoDiario)throws FactException;
	InfoDiario getById(Long cliente)throws FactException;
	List<InfoDiario> getByAll() throws FactException;
	String consecutivoInformePropietario() throws FactException;
}
