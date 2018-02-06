package com.fact.service;



import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Abono;
import com.fact.model.Usuario;

@Local
public interface AbonoService {
	void save(Abono abono);
	void update(Abono abono);
	void delete(Abono abono);
	Abono getById(Long id);
	List<Abono> getByDocumento(Long documentoId) ;
	List<Abono> abonosDia(Date hoy, Date hoyfin, Usuario usuario,List<Long> tipoDocumentoId)throws FactException;
}
