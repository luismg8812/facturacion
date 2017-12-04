package com.fact.dao.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.InformeDiarioDao;
import com.fact.model.InfoDiario;
import com.fact.utils.HibernateUtil;

@Stateless()
public class InformeDiarioDaoImpl implements InformeDiarioDao{
	
	@Override
	public void save(InfoDiario infoDiario)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(infoDiario);
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
	public void update(InfoDiario infoDiario){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(infoDiario);
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
	public void delete(InfoDiario infoDiario){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(infoDiario);
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InfoDiario> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<InfoDiario> clienteList = new ArrayList<>(); 
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

	@Override
	public InfoDiario getById(Long cliente) throws FactException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String consecutivoInformePropietario() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		BigDecimal id ; 
		try {
			String sql = "SELECT S_CONSEQ_INFO_PROPIETARIO.nextval FROM DUAL";
			Query query = session.createSQLQuery(sql);		
			id=   (BigDecimal) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return ""+id;
	}
	

	
		
}
