package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;

import com.fact.api.FactException;
import com.fact.dao.TipoEventoDao;
import com.fact.model.TipoEvento;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TipoEventoDaoImpl implements TipoEventoDao{
	
	@Override
	public TipoEvento getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		TipoEvento tipoEvento=null;
		try {
			tipoEvento = (TipoEvento) session.get(TipoEvento.class, id);
		} catch (Exception e) {
			throw e;
		}
		return tipoEvento;
	}

	@Override
	public TipoEvento getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TipoEvento tipoEvento=null;
		try {
			String sql = "select t from TipoEvento t where t.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			tipoEvento = (TipoEvento) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return tipoEvento;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TipoEvento> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TipoEvento> menuList = new ArrayList<>(); 
		try {
			String sql = "select te from TipoEvento te";
			Query query = session.createQuery(sql);		
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}
	
}
