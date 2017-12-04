package com.fact.dao.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.ProveedorDao;
import com.fact.model.Proveedor;
import com.fact.utils.HibernateUtil;

@Stateless()
public class ProveedorDaoImpl implements ProveedorDao{
	
	@Override
	public void save(Proveedor proveedor)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(proveedor);
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
	public void update(Proveedor proveedor){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(proveedor);
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
	public void delete(Proveedor proveedor){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(proveedor);
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
	public Proveedor getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Proveedor proveedor=null;
		try {
			proveedor = (Proveedor) session.get(Proveedor.class, id);
		} catch (Exception e) {
			throw e;
		}
		return proveedor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Proveedor> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Proveedor> proveedorList = new ArrayList<>(); 
		try {
			String sql = "select p from Proveedor p order by p.nombre";
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
			String sql = "SELECT S_PROVEEDOR.nextval FROM DUAL";
			Query query = session.createSQLQuery(sql);		
			id=  (BigDecimal) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return id;
	}

	@Override
	public Proveedor getByName(String upperCase) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Proveedor proveedor=null;
		try {
			String sql = "select p from Proveedor p where p.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", upperCase.toUpperCase());
			proveedor = (Proveedor) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return proveedor;
	}
		
}
