package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.InfoDiario;

@Local
public interface InformeDiarioService {
	void save(InfoDiario informeDiario)throws FactException;
	void update(InfoDiario informeDiario)throws FactException;
	void delete(InfoDiario informeDiario)throws FactException;
	List<InfoDiario> getByAll() throws FactException;
	String consecutivoInformePropietario()throws FactException;
}
