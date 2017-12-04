package com.fact.dao;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.OpcionUsuario;
import com.fact.model.Usuario;

@Local
public interface OpcionUsuarioDao {

	OpcionUsuario getById(Long Id)throws FactException;
	OpcionUsuario getByName(String nombre) throws FactException;
	List<OpcionUsuario> getByAll() throws FactException;
	List<OpcionUsuario> getByMenuId(String MenuId,Long usuarioId) throws FactException;
	List<OpcionUsuario> getByRuta(String ruta,Long usuarioId)throws FactException;
	List<OpcionUsuario> getByUsuario(String usuarioOpcion)throws FactException;
	void delete(OpcionUsuario opcionUsuario)throws FactException;
	void save(OpcionUsuario opOj)throws FactException;
	List<OpcionUsuario> getByRutas(List<String> rutas, Long usuarioId)throws FactException;
	OpcionUsuario getbySubMenuAndUsuario(Usuario u, Long opcionUsuarioId)throws FactException;
	void update(OpcionUsuario cuadre)throws FactException;;
	
}
