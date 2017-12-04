package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.OpcionUsuarioDao;
import com.fact.model.OpcionUsuario;
import com.fact.model.Usuario;
import com.fact.service.OpcionUsuarioService;

@Stateless
public class OpcionUsuarioServiceImpl implements OpcionUsuarioService{

	 @EJB
	 private OpcionUsuarioDao opcionUsuarioDao;
	
	
	

	@Override
	public OpcionUsuario getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El id es obligatorio");
		}
		return opcionUsuarioDao.getById(id);
	}

	@Override
	public OpcionUsuario getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return opcionUsuarioDao.getByName(nombre);
	}

	@Override
	public List<OpcionUsuario> getByAll() throws FactException {
		return opcionUsuarioDao.getByAll();
	}
	
	@Override
	public List<OpcionUsuario> getByMenuId(String menuId,Long usuarioId) throws FactException {
		if(menuId==null || menuId.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return opcionUsuarioDao.getByMenuId(menuId,usuarioId);
	}
	
	@Override
	public List<OpcionUsuario> getByRuta(String ruta,Long usuarioId) throws FactException {
		if(ruta==null || ruta.length()<1){
			throw new FactException("La ruta es obligatoria");
		}
		return opcionUsuarioDao.getByRuta(ruta,usuarioId);
	}

	@Override
	public List<OpcionUsuario> getByUsuario(String usuarioOpcion) throws FactException {
		if(usuarioOpcion==null || usuarioOpcion.length()<1){
			throw new FactException("La OpcionUsuario es obligatoria");
		}
		return opcionUsuarioDao.getByUsuario(usuarioOpcion);
	}

	@Override
	public void delete(OpcionUsuario opcionUsuario) throws FactException {
		if(opcionUsuario==null ){
			throw new FactException("La OpcionUsuario es obligatoria");
		}
		opcionUsuarioDao.delete(opcionUsuario);
	}

	@Override
	public void save(OpcionUsuario opOj) throws FactException {
		opcionUsuarioDao.save(opOj);
	}

	@Override
	public List<OpcionUsuario> getByRutas(List<String> rutas, Long usuarioId) throws FactException {
		return opcionUsuarioDao.getByRutas(rutas,usuarioId);
	}

	@Override
	public OpcionUsuario getbySubMenuAndUsuario(Usuario u, Long opcionUsuarioId) throws FactException {
		return opcionUsuarioDao.getbySubMenuAndUsuario(u,opcionUsuarioId);
	}

	@Override
	public void update(OpcionUsuario cuadre) throws FactException {
		opcionUsuarioDao.update(cuadre);
		
	}
	
	
}
