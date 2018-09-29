package com.fact.service;



import java.util.List;

import javax.ejb.Local;

import com.fact.api.FactException;
import com.fact.model.Configuracion;
import com.fact.model.Empleado;
import com.fact.model.Empresa;
import com.fact.model.Usuario;

@Local
public interface UsuarioService {

	void save(Usuario usuario)throws FactException;
	void update(Usuario usuario)throws FactException;
	void delete(Usuario usuario)throws FactException;
	Usuario getById(Long id)throws FactException;
	Usuario getByName(String nombre) throws FactException;
	List<Usuario> getByAll() throws FactException;
	List<Empresa> getByAllEmpresa() throws FactException;
	Empresa getByEmpresa(Long usuarioId)throws FactException;
	List<Usuario> getByRol(Long rol)throws FactException;
	Configuracion getConfiguracion()throws FactException;
	List<Empleado> getByFiltrosEmpleados(String nombreEmpleado, String aplellidoEmpleado, String correoEmpleado,
			String identificacionEmpleado)throws FactException;
	void save(Empleado emp)throws FactException;
	List<Empleado> getByEmpleadosAll()throws FactException;
	
}
