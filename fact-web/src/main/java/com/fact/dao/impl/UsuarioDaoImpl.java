package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.UsuarioDao;
import com.fact.model.Configuracion;
import com.fact.model.Empleado;
import com.fact.model.Empresa;
import com.fact.model.Usuario;
import com.fact.utils.HibernateUtil;

@Stateful()
public class UsuarioDaoImpl implements UsuarioDao{

	
	//private SessionFactory = HibernateUtil.getSessionFactory();
	
	@Override
	public void save(Usuario usuario)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(usuario);
			transaction.commit();
		} catch (Exception e) {
			if (transaction!=null) {
				transaction.rollback();
			}
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
	}
	
	@Override
	public void save(Empleado empleado)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(empleado);
			transaction.commit();
		} catch (Exception e) {
			if (transaction!=null) {
				transaction.rollback();
			}
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
	}
	
	@Override
	public void update(Usuario usuario){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(usuario);
			transaction.commit();
		} catch (Exception e) {
			if (transaction!=null) {
				transaction.rollback();
			}
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
	}
	
	@Override
	public void delete(Usuario usuario){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(usuario);
			transaction.commit();
		} catch (Exception e) {
			if (transaction!=null) {
				transaction.rollback();
			}
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
	}
	
	@Override
	public Usuario getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Usuario usuario=null;
		try {
			usuario = (Usuario) session.get(Usuario.class, id);
		} catch (Exception e) {
			throw e;
		}
		return usuario;
	}

	@Override
	public Usuario getByName(String nombre) throws FactException {
		//Session session = HibernateUtil.getSessionFactory().openSession();
		StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession();
		Usuario usuario=null;
		try {
			String sql = "select u from Usuario u where u.login =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			usuario = (Usuario) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return usuario;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Usuario> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Usuario> UsuarioList = new ArrayList<>(); 
		try {
			String sql = "select m from Usuario m";
			Query query = session.createQuery(sql);
			
			UsuarioList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return UsuarioList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Empresa> getByAllEmpresa() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Empresa> UsuarioList = new ArrayList<>(); 
		try {
			String sql = "select m from Empresa m";
			Query query = session.createQuery(sql);			
			UsuarioList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return UsuarioList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Empleado> getByEmpleadosAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Empleado> UsuarioList = new ArrayList<>(); 
		try {
			String sql = "select m from Empleado m";
			Query query = session.createQuery(sql);
			
			UsuarioList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return UsuarioList;
	}

	@Override
	public Empresa getByEmpresa() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Empresa empresa=null;
		try {
			String sql = "select e from Empresa e where e.empresaId = 1";
			Query query = session.createQuery(sql);
			empresa = (Empresa) query.uniqueResult(); 
			session.close();
		} catch (Exception e) {
			throw e;
		}
		return empresa;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Usuario> getByRol(Long rol) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Usuario> UsuarioList = new ArrayList<>(); 
		try {
			String sql = "select u from Usuario u where u.rolId.rolId =:rol";
			Query query = session.createQuery(sql);
			query.setParameter("rol", rol);
			UsuarioList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return UsuarioList;
	}

	@Override
	public Configuracion getConfiguracion() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Configuracion configuracion=null;
		try {
			String sql = "select c from Configuracion c where c.configuracionId = 1";
			Query query = session.createQuery(sql);
			configuracion = (Configuracion) query.uniqueResult(); 
			session.close();
		} catch (Exception e) {
			throw e;
		}
		return configuracion;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Empleado> getByFiltrosEmpleados(String nombreEmpleado, String aplellidoEmpleado, String correoEmpleado,
			String identificacionEmpleado) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Empleado> productoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(Empleado.class);
			if(nombreEmpleado!=null && !nombreEmpleado.isEmpty()){
				detached.add(Restrictions.eq("nombre", nombreEmpleado.toUpperCase()));
			}
			if(aplellidoEmpleado!=null && !aplellidoEmpleado.isEmpty()){
				detached.add(Restrictions.eq("apellido", aplellidoEmpleado.toUpperCase()));
			}
			if(correoEmpleado!=null && !correoEmpleado.isEmpty()){
				detached.add(Restrictions.eq("correo", correoEmpleado));
			}
			if(identificacionEmpleado!=null && !identificacionEmpleado.isEmpty() ){
				detached.add(Restrictions.eq("identificacionEmpleado", identificacionEmpleado));
			}
			//detached.add(Restrictions.eq("estado", 1l));
			detached.addOrder(org.hibernate.criterion.Order.asc("nombre"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			productoList =criteria.list(); 
			
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return productoList;
	}
	
	
}
