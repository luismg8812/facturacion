package com.fact.dao.impl;


import javax.ejb.Stateless;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.TransferenciaEmpresaDao;
import com.fact.model.TransferenciaEmpresa;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TransferenciaEmpresaDaoImpl implements TransferenciaEmpresaDao{
	
	@Override
	public void save(TransferenciaEmpresa transferenciaEmpresa)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(transferenciaEmpresa);
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
	public void update(TransferenciaEmpresa transferenciaEmpresa){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(transferenciaEmpresa);
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
	public void delete(TransferenciaEmpresa transferenciaEmpresa){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(transferenciaEmpresa);
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
	public TransferenciaEmpresa getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		TransferenciaEmpresa abono=null;
		try {
			abono = (TransferenciaEmpresa) session.get(TransferenciaEmpresa.class, id);
		} catch (Exception e) {
			throw e;
		}
		return abono;
	}
	
			
}
