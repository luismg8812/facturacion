package com.fact.dao;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Abono;
import com.fact.model.Usuario;

@Local
public interface AbonoDao {

	void save(Abono abono)throws FactException;
	void update(Abono abono)throws FactException;
	void delete(Abono abono)throws FactException;
	Abono getById(Long abono)throws FactException;
	List<Abono> getByDocumento(Long documentoId) throws FactException;
	List<Abono> abonosDia(Date hoy, Date hoyfin, Usuario usuario,List<Long> tipoDocumentoId) throws FactException;
		
}
