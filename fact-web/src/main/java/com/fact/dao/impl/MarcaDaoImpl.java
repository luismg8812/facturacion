package com.fact.dao.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.MarcaDao;
import com.fact.model.Marca;
import com.fact.utils.HibernateUtil;

@Stateless()
public class MarcaDaoImpl implements MarcaDao{
	
	@Override
	public void save(Marca marca)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(marca);
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
	public void update(Marca marca){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(marca);
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
	public void delete(Marca marca){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(marca);
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
	public Marca getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Marca marca=null;
		try {
			marca = (Marca) session.get(Marca.class, id);
		} catch (Exception e) {
			throw e;
		}
		return marca;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Marca> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Marca> proveedorList = new ArrayList<>(); 
		try {
			String sql = "select p from Marca p order by p.nombre";
			Query query = session.createQuery(sql);
			
			proveedorList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return proveedorList;
	}
	
	//SELECT BLOG_ITEM_SEQ.nextval FROM DUAL
	@Override
	public BigDecimal getByUltimoId() throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();
		BigDecimal id ; 
		try {
			String sql = "SELECT S_MARCA.nextval FROM DUAL";
			Query query = session.createSQLQuery(sql);		
			id=  (BigDecimal) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return id;
	}

	@Override
	public Marca getByName(String upperCase) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Marca marca=null;
		try {
			String sql = "select p from Marca p where p.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", upperCase.toUpperCase());
			marca = (Marca) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return marca;
	}
		
}
