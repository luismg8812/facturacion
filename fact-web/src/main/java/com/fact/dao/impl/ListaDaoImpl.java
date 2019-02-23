package com.fact.dao.impl;


import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.ListaDao;
import com.fact.model.Lista;
import com.fact.utils.HibernateUtil;

@Stateless()
public class ListaDaoImpl implements ListaDao{
	
	@Override
	public void save(Lista lista)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(lista);
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
	public void update(Lista lista){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(lista);
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
	public void delete(Lista lista) {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(lista);
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
	public Lista getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Lista lista=null;
		try {
			lista = (Lista) session.get(Lista.class, id);
		} catch (Exception e) {
			throw e;
		}
		return lista;
	}
	
	@Override
	public Lista getByProductoId(Long productoId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Lista lista=null;
		try {
			String sql = "select l from Lista l where l.productoId.productoId =:productoId";
			Query query = session.createQuery(sql);
			query.setParameter("productoId", productoId);
			lista = (Lista) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return lista;
	}
	
			
}
