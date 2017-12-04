package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.EntregaMercanciaDao;
import com.fact.model.EntregaMercancia;
import com.fact.utils.HibernateUtil;

@Stateless()
public class EntregaMercanciaDaoImpl implements EntregaMercanciaDao{
	
	@Override
	public void save(EntregaMercancia entregaMercancia)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(entregaMercancia);
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
	public void update(EntregaMercancia entregaMercancia){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(entregaMercancia);
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
	public void delete(EntregaMercancia entregaMercancia){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(entregaMercancia);
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
	public EntregaMercancia getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		EntregaMercancia entregaMercancia=null;
		try {
			entregaMercancia = (EntregaMercancia) session.get(EntregaMercancia.class, id);
		} catch (Exception e) {
			throw e;
		}
		return entregaMercancia;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EntregaMercancia> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<EntregaMercancia> entregaMercanciaList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(EntregaMercancia.class);				
			detached.addOrder(org.hibernate.criterion.Order.desc("entregaMercanciaId"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			entregaMercanciaList =criteria.list(); 
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return entregaMercanciaList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntregaMercancia> getEntregasByUsuario(Long usuarioId, Date inicio, Date fin) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<EntregaMercancia> entregaMercanciaList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(EntregaMercancia.class);		
			detached.add(Restrictions.eq("usuarioId.usuarioId", usuarioId));
			detached.add(Restrictions.between("fechaRegistro", inicio, fin));
			detached.addOrder(org.hibernate.criterion.Order.desc("entregaMercanciaId"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			entregaMercanciaList =criteria.list(); 
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return entregaMercanciaList;
	}
	
		
}
