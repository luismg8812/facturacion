package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.GrupoDao;
import com.fact.model.Grupo;
import com.fact.utils.HibernateUtil;

@Stateless()
public class GrupoDaoImpl implements GrupoDao{
		
	
	@Override
	public Grupo getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Grupo grupo=null;
		try {
			grupo = (Grupo) session.get(Grupo.class, id);
		} catch (Exception e) {
			throw e;
		}
		return grupo;
	}

	@Override
	public Grupo getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Grupo grupo=null;
		try {
			String sql = "select g from Grupo g where g.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			grupo = (Grupo) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return grupo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Grupo> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Grupo> grupoList = new ArrayList<>(); 
		try {
			String sql = "select r from Grupo r";
			Query query = session.createQuery(sql);
			
			grupoList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return grupoList;
	}

	@Override
	public void save(Grupo grupo) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(grupo);
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
	public void update(Grupo grupo) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(grupo);
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
	public void delete(Grupo grupo) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(grupo);
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

	
	
}
