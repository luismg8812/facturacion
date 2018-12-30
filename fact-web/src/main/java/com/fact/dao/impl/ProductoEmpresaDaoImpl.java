package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.ProductoEmpresaDao;
import com.fact.model.Empresa;
import com.fact.model.ProductoEmpresa;
import com.fact.utils.HibernateUtil;

@Stateless()
public class ProductoEmpresaDaoImpl implements ProductoEmpresaDao{
	
	@Override
	public void save(ProductoEmpresa productoEmpresa)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(productoEmpresa);
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
	public void update(ProductoEmpresa productoEmpresa){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(productoEmpresa);
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
	public void delete(ProductoEmpresa productoEmpresa){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(productoEmpresa);
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
	public ProductoEmpresa  getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		ProductoEmpresa productoEmpresa=null;
		try {
			productoEmpresa = (ProductoEmpresa) session.get(ProductoEmpresa.class, id);
		} catch (Exception e) {
			throw e;
		}
		return productoEmpresa;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductoEmpresa> getByEmpresa(Long empresaId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<ProductoEmpresa> documentoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(ProductoEmpresa.class);				
			detached.add(Restrictions.eq("empresaId.empresaId", empresaId));
			//detached.addOrder(org.hibernate.criterion.Order.asc("productoId.nombre"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			documentoList =criteria.list(); 
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductoEmpresa getByProductoAndEmpresa(Empresa empresaId, Long productoId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<ProductoEmpresa> documentoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(ProductoEmpresa.class);				
			detached.add(Restrictions.eq("empresaId.empresaId", empresaId.getEmpresaId()));
			detached.add(Restrictions.eq("productoId.productoId", productoId));
			//detached.addOrder(org.hibernate.criterion.Order.asc("productoId.nombre"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			documentoList =criteria.list(); 
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return documentoList.isEmpty()?null:documentoList.get(0);
	}
		
}
