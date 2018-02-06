package com.fact.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.TransferenciaEmpresaDao;
import com.fact.model.Documento;
import com.fact.model.TipoPago;
import com.fact.model.TransferenciaEmpresa;
import com.fact.utils.HibernateUtil;

@Stateless()
public class TransferenciaEmpresaDaoImpl implements TransferenciaEmpresaDao {

	@Override
	public void save(TransferenciaEmpresa transferenciaEmpresa) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.save(transferenciaEmpresa);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			
				session.close();
			
		}
	}

	@Override
	public void update(TransferenciaEmpresa transferenciaEmpresa) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(transferenciaEmpresa);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
			
				session.close();
			
		}
	}

	@Override
	public void delete(TransferenciaEmpresa transferenciaEmpresa) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(transferenciaEmpresa);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw e;
		} finally {
		
				session.close();
			
		}
	}

	@Override
	public TransferenciaEmpresa getById(Long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		session.beginTransaction();
		TransferenciaEmpresa abono = null;
		try {
			abono = (TransferenciaEmpresa) session.get(TransferenciaEmpresa.class, id);
		} catch (Exception e) {
			throw e;
		}
		return abono;
	}

	@Override
	public List<TransferenciaEmpresa> find(Date fechaIni, Date fechaFin, Long desdeReporte, Long hastaReporte)
			throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TransferenciaEmpresa> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(TransferenciaEmpresa.class);
			if (fechaIni != null && fechaFin != null) {
				detached.add(Restrictions.between("fechaRegistro", fechaIni, fechaFin));
			}
			if (desdeReporte != null) {
				detached.add(Restrictions.eq("empresaDesde.empresaId", desdeReporte));
			}
			if (hastaReporte != null) {
				detached.add(Restrictions.eq("empresaHasta.empresaId", hastaReporte));
			}
			detached.addOrder(org.hibernate.criterion.Order.desc("fechaRegistro"));
			Criteria criteria = detached.getExecutableCriteria(session);
			documentoList = criteria.list();
		} catch (FactException e) {
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return documentoList;
	}

}
