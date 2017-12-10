package com.fact.service.impl;



import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fact.api.FactException;
import com.fact.dao.UsuarioDao;
import com.fact.model.Configuracion;
import com.fact.model.Empleado;
import com.fact.model.Empresa;
import com.fact.model.Usuario;
import com.fact.service.UsuarioService;

@Stateless
public class UsuarioServiceImpl implements UsuarioService{

	 @EJB
	 private UsuarioDao usuarioDao;
	
	
	@Override
	public void save(Usuario usuario) throws FactException {
		usuarioDao.save(usuario);
	}
	
	@Override
	public void save(Empleado empleado) throws FactException {
		usuarioDao.save(empleado);
		
	}

	@Override
	public void update(Usuario usuario) throws FactException {
		if(usuario.getUsuarioId()==null){
			throw new FactException("El id es obligatorio");
		}
		if(usuario.getNombre()==null || usuario.getNombre().length()<1 ){
			throw new FactException("El Nombre es obligatorio");
		}
		usuarioDao.update(usuario);
	}

	@Override
	public void delete(Usuario usuario) throws FactException {
		if(usuario.getUsuarioId()==null){
			throw new FactException("El documento es obligatorio");
		}
		usuarioDao.delete(usuario);
	}

	@Override
	public Usuario getById(Long id) throws FactException {
		if(id==null){
			throw new FactException("El documento es obligatorio");
		}
		return usuarioDao.getById(id);
	}

	@Override
	public Usuario getByName(String nombre) throws FactException {
		if(nombre==null || nombre.length()<1){
			throw new FactException("El nombre es obligatorio");
		}
		return usuarioDao.getByName(nombre);
	}

	@Override
	public List<Usuario> getByAll() throws FactException {
		return usuarioDao.getByAll();
	}
	
	@Override
	public List<Empresa> getByAllEmpresa() throws FactException {
		return usuarioDao.getByAllEmpresa();
	}

	@Override
	public Empresa getByEmpresa() throws FactException {
		return usuarioDao.getByEmpresa();
	}

	@Override
	public List<Usuario> getByRol(Long rol) throws FactException {
		return usuarioDao.getByRol(rol);
	}

	@Override
	public Configuracion getConfiguracion() throws FactException {
		return usuarioDao.getConfiguracion();
	}

	@Override
	public List<Empleado> getByFiltrosEmpleados(String nombreEmpleado, String aplellidoEmpleado, String correoEmpleado,
			String identificacionEmpleado) throws FactException {
		return usuarioDao.getByFiltrosEmpleados(nombreEmpleado,aplellidoEmpleado,correoEmpleado,identificacionEmpleado);
	}

	@Override
	public List<Empleado> getByEmpleadosAll() throws FactException {
		return usuarioDao.getByEmpleadosAll();
	}

	
	
	
}
