package com.fact.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

import com.fact.api.FactException;
import com.fact.dao.DocumentoDetalleDao;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.utils.HibernateUtil;

@Stateless()
public class DocumentoDetalleDaoImpl implements DocumentoDetalleDao {

	@Override
	public void save(DocumentoDetalle documentoDetalle, Long server) throws FactException {
		// Session session = HibernateUtil.getSessionFactory().openSession();;

		StatelessSession session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openStatelessSession();
		} else {
			session = HibernateUtil.getSessionFactory().openStatelessSession();
		}
		Transaction transaction = session.beginTransaction();
		try {
			session.insert(documentoDetalle);
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
	public void update(DocumentoDetalle documento, Long server) {
		StatelessSession session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openStatelessSession();
		} else {
			session = HibernateUtil.getSessionFactory().openStatelessSession();
		}
		Transaction transaction = session.beginTransaction();
		try {
			session.update(documento);
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
	public void delete(DocumentoDetalle documento) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(documento);
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
	public DocumentoDetalle getById(Long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		session.beginTransaction();
		DocumentoDetalle documentoDetalle = null;
		try {
			documentoDetalle = (DocumentoDetalle) session.get(DocumentoDetalle.class, id);
		} catch (Exception e) {
			throw e;
		}
		return documentoDetalle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentoDetalle> getByDocumento(Long documentoId,Long server) throws FactException {
		StatelessSession session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openStatelessSession();
		} else {
			session = HibernateUtil.getSessionFactory().openStatelessSession();
		}
		List<DocumentoDetalle> DocumentoDetalleList = new ArrayList<>();
		try {
			String sql = "select dd from DocumentoDetalle dd where dd.documentoId.documentoId =:documentoId and dd.estado = 1 ";
			Query query = session.createQuery(sql);
			query.setParameter("documentoId", documentoId);
			DocumentoDetalleList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return DocumentoDetalleList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentoDetalle> getByDocumento(List<Documento> documentos) throws FactException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<DocumentoDetalle> DocumentoDetalleList = new ArrayList<>();
		try {
			String sql = "select dd from DocumentoDetalle dd where dd.documentoId in :documentoId and dd.estado = 1 ";
			Query query = session.createQuery(sql);
			query.setParameterList("documentoId", documentos);
			DocumentoDetalleList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return DocumentoDetalleList;
	}

	@Override
	public void borrar(Long documentoDetalleId, Long estado, Long server) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			String sql = "update DocumentoDetalle dd set dd.estado =:estado where dd.documentoDetalleId =:documentoDetalleId";
			Query query = session.createQuery(sql);
			query.setParameter("estado", estado);
			query.setParameter("documentoDetalleId", documentoDetalleId);
			query.executeUpdate();

		} catch (FactException e) {
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentoDetalle> getPropinasByUsuario(Long tipoDocumentoId, Date hoy, Date hoyfin, Date hoyfin2,
			Long usuarioId, Boolean conCierre, Long server) throws FactException {
		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}

		List<DocumentoDetalle> documentoList = new ArrayList<>();
		try {
			String sql = "select dd from DocumentoDetalle dd where dd.documentoId.tipoDocumentoId.tipoDocumentoId  in :tipoDocumentoId "
					+ " and (dd.documentoId.fechaRegistro > :hoy) AND (dd.documentoId.fechaRegistro < :hoyFin) ";
			if (conCierre) {
				sql += " and dd.documentoId.cierreDiario is null ";
			}
			sql += " and dd.documentoId.impreso = 1";
			sql += " and dd.documentoId.consecutivoDian is not null ";
			sql += " and dd.documentoId.usuarioId.usuarioId =:usuarioId ";
			sql += " and dd.productoId.productoId=0) ";
			sql += " order by dd.documentoId.consecutivoDian asc";
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", tipoDocumentoId);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyfin);
			query.setParameter("usuarioId", usuarioId);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentoDetalle> getbyEmpleado(Long empleadoId, Date hoy, Date hoyfin) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<DocumentoDetalle> DocumentoDetalleList = new ArrayList<>();
		try {
			String sql = "select dd from DocumentoDetalle dd "
					+ " where dd.documentoId.empleadoId.empleadoId =:empleadoId "
				    + " and dd.productoId.productoId=0 "
				    + " and (dd.documentoId.fechaRegistro > :hoy) AND (dd.documentoId.fechaRegistro < :hoyFin) "
					+ "and dd.estado = 1 ";
			Query query = session.createQuery(sql);
			query.setParameter("empleadoId", empleadoId);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyfin);
			DocumentoDetalleList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return DocumentoDetalleList;
	}

	@Override
	public List<DocumentoDetalle> getByProductoId(long productoId,Date hoy, Date hoyfin) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<DocumentoDetalle> DocumentoDetalleList = new ArrayList<>();
		try {
			String sql = "select dd from DocumentoDetalle dd "
					+ " where dd.productoId.productoId=5 "
				    + " and (dd.documentoId.fechaRegistro >= :hoy) AND (dd.documentoId.fechaRegistro <= :hoyfin) "
					+ "and dd.estado = 1 ";
			Query query = session.createQuery(sql);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyfin", hoyfin);
			DocumentoDetalleList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return DocumentoDetalleList;
	}

}
