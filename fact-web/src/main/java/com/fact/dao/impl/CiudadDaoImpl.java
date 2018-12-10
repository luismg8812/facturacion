package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;

import com.fact.api.FactException;
import com.fact.dao.CiudadDao;
import com.fact.model.Ciudad;
import com.fact.utils.HibernateUtil;

@Stateless()
public class CiudadDaoImpl implements CiudadDao{
		
	
	@Override
	public Ciudad getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Ciudad ciudad=null;
		try {
			ciudad = (Ciudad) session.get(Ciudad.class, id);
		} catch (Exception e) {
			throw e;
		}
		return ciudad;
	}

	@Override
	public Ciudad getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Ciudad ciudad=null;
		try {
			String sql = "select c from Ciudad c where c.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			ciudad = (Ciudad) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return ciudad;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ciudad> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Ciudad> ciudadList = new ArrayList<>(); 
		try {
			String sql = "select c from Ciudad c";
			Query query = session.createQuery(sql);
			
			ciudadList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return ciudadList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ciudad> getByDepartamento(Long departamentoId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Ciudad> ciudadList = new ArrayList<>(); 
		try {
			String sql = "select c from Ciudad c where c.departamentoId.departamentoId =:departamentoId";
			Query query = session.createQuery(sql);
			query.setParameter("departamentoId", departamentoId);
			ciudadList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return ciudadList;
	}
	
}
