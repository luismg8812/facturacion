package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.EventoDao;
import com.fact.model.Evento;
import com.fact.utils.HibernateUtil;

@Stateful()
public class EventoDaoImpl implements EventoDao{

	
	//private SessionFactory = HibernateUtil.getSessionFactory();
	
	@Override
	public void save(Evento evento)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(evento);
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
	public void update(Evento evento){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(evento);
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
	public void delete(Evento evento){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(evento);
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
	public Evento getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Evento evento=null;
		try {
			evento = (Evento) session.get(Evento.class, id);
		} catch (Exception e) {
			throw e;
		}
		return evento;
	}

	


	@SuppressWarnings("unchecked")
	@Override
	public List<Evento> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Evento> eventoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(Evento.class);
			Criteria criteria =  detached.getExecutableCriteria(session);
			eventoList =criteria.list(); 		
		} catch (FactException e) {
			throw e;
		}
		finally{
			if (session!=null) {
				session.close();
			}
		}
		return eventoList;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Evento> getByFechaAndTipo(Date fechaInicio, Date fechaFin, Long tipoEvento) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Evento> eventoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(Evento.class);
			detached.add(Restrictions.between("fechaRegistro", fechaInicio,fechaFin));
			detached.add(Restrictions.eq("tipoEventoId.tipoEventoId", tipoEvento));
			Criteria criteria =  detached.getExecutableCriteria(session);
			eventoList =criteria.list(); 		
		} catch (FactException e) {
			throw e;
		}
		finally{
			if (session!=null) {
				session.close();
			}
		}
		return eventoList;
	}

	
	
	
}
