package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.AbonoDao;
import com.fact.model.Abono;
import com.fact.model.Usuario;
import com.fact.utils.HibernateUtil;

@Stateless()
public class AbonoDaoImpl implements AbonoDao{
	
	@Override
	public void save(Abono abono)throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction= session.beginTransaction();
		try {
			session.save(abono);
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
	public void update(Abono abono){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction= session.beginTransaction();
		try {
			session.update(abono);
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
	public void delete(Abono abono) {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(abono);
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
	public Abono getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Abono abono=null;
		try {
			abono = (Abono) session.get(Abono.class, id);
		} catch (Exception e) {
			throw e;
		}
		return abono;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Abono> getByDocumento(Long documentoId)  {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Abono> abonoList = new ArrayList<>(); 
		try {
			String sql = "select a from Abono a where a.documentoId.documentoId =:documentoId ";
			Query query = session.createQuery(sql);
			query.setParameter("documentoId", documentoId);			
			abonoList=  query.list(); 
			
		} catch (FactException e) {
			
		}finally{
			session.close();
		}
		return abonoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Abono> abonosDia(Date hoy, Date hoyfin, Usuario usuario, List<Long> tipoDocumentoId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Abono> abonoList = new ArrayList<>(); 
		try {
			String sql = "select d from Abono d where (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin) "
					+ " and d.usuarioId.usuarioId =:usuarioId and d.documentoId.tipoDocumentoId.tipoDocumentoId in :tipoDocumentoId "
					+ " order by d.abonoId asc";
			Query query = session.createQuery(sql);
			query.setParameter("hoy", hoy );
			query.setParameter("hoyFin", hoyfin );
			query.setParameter("usuarioId", usuario.getUsuarioId() );
			query.setParameterList("tipoDocumentoId", tipoDocumentoId );
			abonoList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return abonoList;
	}
		
}
