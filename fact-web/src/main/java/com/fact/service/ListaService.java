package com.fact.service;



import javax.ejb.Local;

import com.fact.model.Lista;

@Local
public interface ListaService {
	void save(Lista abono);
	void update(Lista abono);
	void delete(Lista abono);
	Lista getById(Long id);
	Lista getByProductoId(Long productoId);
}
