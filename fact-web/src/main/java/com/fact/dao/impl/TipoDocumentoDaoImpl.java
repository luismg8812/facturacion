package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.TipoDocumentoDao;
import com.fact.model.ProductoEmpresa;
import com.fact.model.TipoDocumento;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TipoDocumentoDaoImpl implements TipoDocumentoDao{
	
	@Override
	public TipoDocumento getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		TipoDocumento tipoDocumento=null;
		try {
			tipoDocumento = (TipoDocumento) session.get(TipoDocumento.class, id);
		} catch (Exception e) {
			throw e;
		}
		return tipoDocumento;
	}
	
	@Override
	public void update(TipoDocumento tipoDocumento){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(tipoDocumento);
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
	public TipoDocumento getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TipoDocumento tipoDocumento=null;
		try {
			String sql = "select u from TipoDocumento u where u.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			tipoDocumento = (TipoDocumento) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return tipoDocumento;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TipoDocumento> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TipoDocumento> menuList = new ArrayList<>(); 
		try {
			String sql = "select td from TipoDocumento td";
			Query query = session.createQuery(sql);		
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TipoDocumento> getById(List<Long> ids) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TipoDocumento> menuList = new ArrayList<>(); 
		try {
			String sql = "select td from TipoDocumento td where td.tipoDocumentoId in :tipoDocumentoId";
			Query query = session.createQuery(sql);		
			query.setParameterList("tipoDocumentoId", ids);
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}
	
}
