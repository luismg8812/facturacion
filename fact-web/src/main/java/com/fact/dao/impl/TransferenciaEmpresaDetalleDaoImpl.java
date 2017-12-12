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
import com.fact.dao.TransferenciaEmpresaDetalleDao;
import com.fact.model.Documento;
import com.fact.model.TransferenciaEmpresaDetalle;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TransferenciaEmpresaDetalleDaoImpl implements TransferenciaEmpresaDetalleDao {

	@Override
	public void save(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.save(transferenciaEmpresaDetalle);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public void update(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(transferenciaEmpresaDetalle);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public void delete(TransferenciaEmpresaDetalle transferenciaEmpresaDetalle) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(transferenciaEmpresaDetalle);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public TransferenciaEmpresaDetalle getById(Long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		TransferenciaEmpresaDetalle abono = null;
		try {
			abono = (TransferenciaEmpresaDetalle) session.get(TransferenciaEmpresaDetalle.class, id);
		} catch (Exception e) {
			throw e;
		}
		return abono;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TransferenciaEmpresaDetalle> getByTrasferenciaId(Long transferenciaEmpresaId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TransferenciaEmpresaDetalle> documentoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(TransferenciaEmpresaDetalle.class);				
			detached.add(Restrictions.eq("transferenciaEmpresaId.transferenciaEmpresaId", transferenciaEmpresaId));			
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
}
