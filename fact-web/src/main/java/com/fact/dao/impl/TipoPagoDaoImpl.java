package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import com.fact.api.FactException;
import com.fact.dao.TipoPagoDao;
import com.fact.model.TipoPago;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TipoPagoDaoImpl implements TipoPagoDao{
	
	@Override
	public TipoPago getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		TipoPago tipoPago=null;
		try {
			tipoPago = (TipoPago) session.get(TipoPago.class, id);
		} catch (Exception e) {
			throw e;
		}
		return tipoPago;
	}

	@Override
	public TipoPago getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TipoPago tipoPago=null;
		try {
			String sql = "select t from TipoPago t where u.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			tipoPago = (TipoPago) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return tipoPago;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TipoPago> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TipoPago> tipoPagoList = new ArrayList<>(); 
		try {
			String sql = "select t from TipoPago t";
			Query query = session.createQuery(sql);		
			tipoPagoList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return tipoPagoList;
	}
	
}
