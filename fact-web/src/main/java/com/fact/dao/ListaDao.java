package com.fact.dao;



import javax.ejb.Local;

import com.fact.model.Lista;

@Local
public interface ListaDao {

	void save(Lista lista) ;
	void update(Lista lista);
	void delete(Lista lista);
	Lista getById(Long ListaId);
	Lista getByProductoId(Long productoId);
		
}
