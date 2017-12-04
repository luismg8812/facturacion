package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import com.fact.api.FactException;
import com.fact.dao.RolDao;
import com.fact.model.Rol;
import com.fact.utils.HibernateUtil;

@Stateless()
public class RolDaoImpl implements RolDao{
		
	
	@Override
	public Rol getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Rol rol=null;
		try {
			rol = (Rol) session.get(Rol.class, id);
		} catch (Exception e) {
			throw e;
		}
		return rol;
	}

	@Override
	public Rol getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Rol rol=null;
		try {
			String sql = "select r from Rol r where r.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			rol = (Rol) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return rol;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rol> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Rol> RolList = new ArrayList<>(); 
		try {
			String sql = "select r from Rol r";
			Query query = session.createQuery(sql);
			
			RolList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return RolList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rol> getSinPropietario() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Rol> RolList = new ArrayList<>(); 
		try {
			String sql = "select r from Rol r where r.rolId in (1,2)";
			Query query = session.createQuery(sql);		
			RolList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return RolList;
	}
	
}
