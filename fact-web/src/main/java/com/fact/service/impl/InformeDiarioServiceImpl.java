package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.InformeDiarioDao;
import com.fact.model.InfoDiario;
import com.fact.service.InformeDiarioService;

@Stateless
public class InformeDiarioServiceImpl implements InformeDiarioService{

	 @EJB
	 private InformeDiarioDao informeDiarioDao;
	
	@Override
	public void save(InfoDiario infoDiario ) throws FactException {	
		informeDiarioDao.save(infoDiario);
	}

	@Override
	public void update(InfoDiario infoDiario) throws FactException {
		if(infoDiario.getInfoDiarioId()==null){
			throw new FactException("El id es obligatorio");
		}		
		informeDiarioDao.update(infoDiario);
	}

	@Override
	public void delete(InfoDiario infoDiario) throws FactException {
		if(infoDiario.getInfoDiarioId()==null){
			throw new FactException("El id  es obligatorio");
		}
		informeDiarioDao.delete(infoDiario);
	}

	@Override
	public List<InfoDiario> getByAll() throws FactException {
		return informeDiarioDao.getByAll();
	}

	@Override
	public String consecutivoInformePropietario() throws FactException {
		return informeDiarioDao.consecutivoInformePropietario();
	}
	
	
	
}
