package com.fact.service.impl;



import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.AbonoDao;
import com.fact.model.Abono;
import com.fact.model.Usuario;
import com.fact.service.AbonoService;

@Stateless
public class AbonoServiceImpl implements AbonoService{

	 @EJB
	 private AbonoDao abonoDao;
	
	@Override
	public void save(Abono abono) throws FactException {			
		abonoDao.save(abono);
	}

	@Override
	public void update(Abono abono) throws FactException {
		if(abono.getDocumentoId()==null){
			throw new FactException("El id es obligatorio");
		}		
		abonoDao.update(abono);
	}

	@Override
	public void delete(Abono abono) throws FactException {
		if(abono.getDocumentoId()==null){
			throw new FactException("El documento es obligatorio");
		}
		abonoDao.delete(abono);
	}

	@Override
	public Abono getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return abonoDao.getById(id);
	}
	
	@Override
	public List<Abono> getByDocumento(Long documentoId) throws FactException {
		return abonoDao.getByDocumento(documentoId);
	}

	@Override
	public List<Abono> abonosDia(Date hoy, Date hoyfin, Usuario usuario,List<Long> tipoDocumentoId) throws FactException {
		return abonoDao.abonosDia(hoy,hoyfin,usuario,tipoDocumentoId);
	}
	
}
