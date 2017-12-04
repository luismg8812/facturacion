package com.fact.dao.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.ClienteDao;
import com.fact.model.Cliente;
import com.fact.utils.HibernateUtil;

@Stateless()
public class ClienteDaoImpl implements ClienteDao{
	
	@Override
	public void save(Cliente cliente)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(cliente);
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
	public void update(Cliente cliente){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(cliente);
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
	public void delete(Cliente cliente){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(cliente);
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
	public Cliente getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Cliente cliente=null;
		try {
			cliente = (Cliente) session.get(Cliente.class, id);
		} catch (Exception e) {
			throw e;
		}
		return cliente;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Cliente> clienteList = new ArrayList<>(); 
		try {
			String sql = "select c from Cliente c order by c.nombre";
			Query query = session.createQuery(sql);
			
			clienteList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return clienteList;
	}
	
	//SELECT BLOG_ITEM_SEQ.nextval FROM DUAL
	@Override
	public BigDecimal getByUltimoId() throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();
		BigDecimal id ; 
		try {
			String sql = "SELECT S_CLIENTE.nextval FROM DUAL";
			Query query = session.createSQLQuery(sql);		
			id=  (BigDecimal) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return id;
	}

	@Override
	public Cliente getByName(String upperCase) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Cliente cliente=null;
		try {
			String sql = "select c from Cliente c where c.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", upperCase.toUpperCase());
			cliente = (Cliente) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return cliente;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> getByDocumento(String documento) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Cliente> clienteList = new ArrayList<>(); 
		try {
			String sql = "select c from Cliente c where c.documento =:documento";
			Query query = session.createQuery(sql);
			query.setParameter("documento", documento);
			clienteList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return clienteList;
	}
		
}
