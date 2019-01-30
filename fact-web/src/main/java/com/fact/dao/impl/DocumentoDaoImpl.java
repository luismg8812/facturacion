package com.fact.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.DocumentoDao;
import com.fact.model.ConsecutivoDian;
import com.fact.model.Documento;
import com.fact.model.DocumentoDetalle;
import com.fact.model.InfoDiario;
import com.fact.model.Invoice;
import com.fact.utils.HibernateUtil;

@Stateless()
public class DocumentoDaoImpl implements DocumentoDao {

	@Override
	public void save(Documento documento, Long server) {
		StatelessSession session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openStatelessSession();
		} else {
			session = HibernateUtil.getSessionFactory().openStatelessSession();
		}

		Transaction transaction = session.beginTransaction();
		try {
			session.insert(documento);
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
	public void save(InfoDiario infoDiario) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.save(infoDiario);
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
	public void update(Documento documento, Long server) {
		// Session session = HibernateUtil.getSessionFactory().openSession();
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
	public void update(InfoDiario infoDiario) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(infoDiario);
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
	public void delete(Documento documento) {
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
	public Documento getById(Long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Documento documento = null;
		try {
			documento = (Documento) session.get(Documento.class, id);
		} catch (Exception e) {
			throw e;
		}
		return documento;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByTipo(Long tipoDocumentoId, Date hoy, Date hoyFin, Long usuarioId,Boolean conCierre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId =:tipoDocumentoId ";
					sql+= " and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin)  ";
					if (conCierre) {
						sql += " and d.cierreDiario is null ";
					}
					sql+= " and d.usuarioId.usuarioId =:usuarioId order by d.documentoId asc";
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", Long.valueOf(tipoDocumentoId));
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyFin);
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
	public List<Documento> getDocNoImp(Long usuarioId, List<Long> tipoDocumentoId, Long server) {
		Session session;
		if (server == 2) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where (d.impreso is null or d.impreso=0) and d.tipoDocumentoId.tipoDocumentoId in :tipoDocumentoId  and d.documentoId  "
					+ "in (select dd.documentoId.documentoId from DocumentoDetalle dd where dd.documentoId.documentoId=d.documentoId and dd.estado=1) "
					+ "and d.usuarioId.usuarioId =:usuarioId"
					+ " order by d.documentoId desc ";
			Query query = session.createQuery(sql);
			query.setParameter("usuarioId", usuarioId);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@Override
	public Documento getByLastAndTipo(Long idFactura, Long usuarioId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Documento documento = null;
		try {
			String sql = "select d from Documento d where d.documentoId=(select max(dd.documentoId) "
					+ "from Documento dd where dd.tipoDocumentoId.tipoDocumentoId =:tipoDocumentoId and dd.impreso is null "
					+ "and dd.usuarioId.usuarioId =:usuarioId and dd.documentoId in (select dda.documentoId.documentoId from "
					+ "DocumentoDetalle dda where dda.documentoId.documentoId = dd.documentoId and dda.estado = 1 ))";
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", idFactura);
			query.setParameter("usuarioId", usuarioId);
			documento = (Documento) query.uniqueResult();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documento;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByTipoSinUsuario(List<Long> tipoDocumentoId, Date hoy, Date hoyFin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId in :tipoDocumentoId "
					+ "and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin) and d.cierreDiario is null ";
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyFin);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> buscarPorAbonos(Long proveedorId, Long tipoFacturas, Date fechaInicio, Date fechafin,
			String detalle) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		Long factura = 8l;
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId in :factura ";
			if (proveedorId != null && proveedorId != 0l) {
				sql += " and (d.proveedorId.proveedorId =:proveedorId )";
			}
			if (fechaInicio != null) {
				sql += " and (d.fechaRegistro >= :fechaInicio) ";
			}
			if (fechafin != null) {
				sql += " and (d.fechaRegistro <= :fechafin) ";
			}
			if (detalle != null && !detalle.isEmpty()) {
				sql += " and (d.detalleEntrada =:detalle )";
			}
			sql+=" and d.detalleEntrada in ( select d2.consecutivoDian from Documento d2 where d2.tipoDocumentoId.tipoDocumentoId = 2)";
			Query query = session.createQuery(sql);
			query.setParameter("factura", factura);
			if (proveedorId != null && proveedorId != 0l) {
				query.setParameter("proveedorId", proveedorId);
			}
			if (fechaInicio != null) {
				query.setParameter("fechaInicio", fechaInicio);
			}
			if (fechafin != null) {
				query.setParameter("fechafin", fechafin);
			}
			if (detalle != null && !detalle.isEmpty()) {
				query.setParameter("detalle", detalle);
			}
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByFacturaByDia(Long tipoDocumentoId, Date hoy, Date hoyfin,Boolean conCierre) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId =:tipoDocumentoId "
					+ " and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin)  ";
					if (conCierre) {
						sql += " and d.cierreDiario is null ";
					}
					sql += " and d.impreso=1" + " and d.consecutivoDian is not null " + " order by d.consecutivoDian asc";
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", Long.valueOf(tipoDocumentoId));
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyfin);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByHoyNoImpresas(List<Long> tipoDocumentoId, Date hoy, Date hoyfin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId  in :tipoDocumentoId "
					+ " and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin) and d.cierreDiario is null "
					+ " and d.impreso is null order by d.documentoId asc";
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyfin);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> buscarPorAbonosByClient(Long clienteId, Date fechaInicio, Date fechafin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		List<Long> documentoId = new ArrayList<>();
		List<Long> tipoPagoId = new ArrayList<>();
		tipoPagoId.add(2l);
		tipoPagoId.add(6l);
		documentoId.add(8l);
		//documentoId.add(10l);
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId  in :factura "
					+ " and d.tipoPagoId.tipoPagoId in :tipoPagoId";
			if (clienteId != null && clienteId != 0l) {
				sql += " and (d.clienteId.clienteId =:clienteId )";
			}
			if (fechaInicio != null) {
				sql += " and (d.fechaRegistro >= :fechaInicio) ";
			}
			if (fechafin != null) {
				sql += " and (d.fechaRegistro <= :fechafin) ";
			}

			
			Query query = session.createQuery(sql);
			query.setParameterList("factura", documentoId);
			query.setParameterList("tipoPagoId", tipoPagoId);
			if (clienteId != null && clienteId != 0l) {
				query.setParameter("clienteId", clienteId);
			}
			if (fechaInicio != null) {
				query.setParameter("fechaInicio", fechaInicio);
			}
			if (fechafin != null) {
				query.setParameter("fechafin", fechafin);
			}
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> buscarPorFechaAndCajero(Long usuarioSelect, String documentoId, Date fechaIni, Date fechaFin,
			String conDian, Long clienteId, Long tipoDocumento) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		Long factura = tipoDocumento==0?10l:tipoDocumento;
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId = :factura "
					+ "and d.impreso = 1 " + " and consecutivoDian is not null";
			if (usuarioSelect != null && usuarioSelect != 0l) {
				sql += " and d.usuarioId.usuarioId =:usuarioId ";
			}
			if (clienteId != null && clienteId != 0l) {
				sql += " and d.clienteId.clienteId = :clienteId ";
			}
		
			if (fechaIni != null) {
				sql += " and d.fechaRegistro  >=:fechaInicio ";
			}
			if (fechaFin != null) {
				sql += " and d.fechaRegistro  <=:fechafin  ";
			}
			if (documentoId != null && !documentoId.isEmpty()) {
				sql += " and (d.documentoId =:documentoId )";
			}
			if (conDian != null && !conDian.isEmpty()) {
				sql += " and (d.consecutivoDian =:conDian )";
			}
			sql += " order by consecutivoDian desc ";
			
			Query query = session.createQuery(sql);
			query.setParameter("factura", factura);
			if (usuarioSelect != null && usuarioSelect != 0l) {
				query.setParameter("usuarioId", usuarioSelect);
			}
			if (clienteId != null && clienteId != 0l) {
				query.setParameter("clienteId", clienteId);
			}
			
			if (fechaIni != null) {
				query.setParameter("fechaInicio", fechaIni);
			}
			if (fechaFin != null) {
				query.setParameter("fechafin", fechaFin);
			}
			if (documentoId != null && !documentoId.isEmpty()) {
				query.setParameter("documentoId", Long.valueOf(documentoId));
			}
			if (conDian != null && !conDian.isEmpty()) {
				query.setParameter("conDian", conDian);
			}
			query.setMaxResults(200);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;

	}

	// SELECT BLOG_ITEM_SEQ.nextval FROM DUAL
	@Override
	public String getByUltimoId() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		BigInteger id;
		try {
			String sql = "SELECT nextval('S_CONSECUTIVO_DIAN')";
			Query query = session.createSQLQuery(sql);
			id = (BigInteger) query.uniqueResult();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return "" + id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getRemisionesByUsuario(Long tipoDocumentoId,  Long usuarioId,
			Boolean conCierre, Long server) {
		Session session;
		if (server == 2) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId =:tipoDocumentoId ";
					if (conCierre) {
						sql += " and d.cierreDiario is null ";
					}
					sql += " and d.usuarioId.usuarioId =:usuarioId order by d.usuarioId.nombre asc";
			
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", Long.valueOf(tipoDocumentoId));
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
	public List<Documento> getRemisionesByUsuarioConFecha(Long tipoDocumentoId, Date hoy, Date hoyFin, Long usuarioId,
			Boolean conCierre, Long server) {
		Session session;
		if (server == 2) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId =:tipoDocumentoId "
					+ " and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin)  ";
					if (conCierre) {
						sql += " and d.cierreDiario is null ";
					}
					sql += " and d.usuarioId.usuarioId =:usuarioId order by d.usuarioId.nombre asc";
			Query query = session.createQuery(sql);
			query.setParameter("tipoDocumentoId", Long.valueOf(tipoDocumentoId));
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyFin);
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
	public List<InfoDiario> buscarInfodiarioByFecha(Date fechaInicio, Date fechafin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<InfoDiario> documentoList = new ArrayList<>();
		try {
			String sql = "select d from InfoDiario d where d.fechaInforme >= :hoy AND d.fechaInforme <= :hoyFin "; // tipo
																													// documento
																													// igual
																													// a
																													// factura
																													// de
																													// venta

			Query query = session.createQuery(sql);
			query.setParameter("hoy", fechaInicio);
			query.setParameter("hoyFin", fechafin);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByfacturasReales(List<Long> tipoDocumentoId, Long usuarioId,
			Boolean conCierre, Long server) {
		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}

		List<Documento> documentoList = new ArrayList<>();
		
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId  in :tipoDocumentoId ";
					
			if (conCierre) {
				sql += " and d.cierreDiario is null ";
			}
			sql += " and d.impreso = 1";
			sql += " and d.consecutivoDian is not null ";
			sql += " and d.usuarioId.usuarioId =:usuarioId order by d.consecutivoDian asc";
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);		
			query.setParameter("usuarioId", usuarioId);
			documentoList = query.list();
			session.close();
		
		return documentoList;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByfacturasRealesConFecha(List<Long> tipoDocumentoId, Date hoy, Date hoyFin, Long usuarioId,
			Boolean conCierre, Long server) {

		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}

		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId  in :tipoDocumentoId "
					+ " and (d.fechaRegistro > :hoy) AND (d.fechaRegistro < :hoyFin) ";
			if (conCierre) {
				sql += " and d.cierreDiario is null ";
			}
			sql += " and d.impreso = 1";
			sql += " and d.consecutivoDian is not null ";
			sql += " and d.usuarioId.usuarioId =:usuarioId order by d.consecutivoDian asc";
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			query.setParameter("hoy", hoy);
			query.setParameter("hoyFin", hoyFin);
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
	public List<Documento> getByFacturaSinCierre(List<Long> tipoDocumentoId, Date hoy, Date hoyfin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.tipoDocumentoId.tipoDocumentoId in :tipoDocumentoId "
					+ " and d.cierreDiario is null ";
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Documento getByConsecutivoDian(String documento) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			String sql = "select d from Documento d where d.consecutivoDian = :consecutivoDian ";
			Query query = session.createQuery(sql);
			query.setParameter("consecutivoDian", documento);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList.isEmpty() ? null : documentoList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByEntrega(Long entrega) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			detached.add(Restrictions.eq("entregado", entrega));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByTipoPago(List<Long> tipoPago, Date hoy, Date hoyFin, Long usuarioId, Boolean conCierre,
			Long server) {
		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			if (conCierre) {
				detached.add(Restrictions.isNull("cierreDiario"));
			}
			detached.add(Restrictions.in("tipoPagoId.tipoPagoId", tipoPago));
			detached.add(Restrictions.eq("usuarioId.usuarioId", usuarioId));
			detached.add(Restrictions.between("fechaRegistro", hoy, hoyFin));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMagList() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<String> documentoList = new ArrayList<>();
		try {
			String sql = "select DISTINCT(d.mac) from Documento d ";
			Query query = session.createQuery(sql);
			documentoList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return documentoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByMacAndTipoDocumento(List<Long> tipoDocumentoId, String mac, Date hoy, Date hoyfin,
			Boolean conCierre, Long server) {
		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			if (conCierre) {
				detached.add(Restrictions.isNull("cierreDiario"));
			}
			detached.add(Restrictions.in("tipoDocumentoId.tipoDocumentoId", tipoDocumentoId));
			detached.add(Restrictions.eq("mac", mac));
			detached.add(Restrictions.eq("impreso", 1l));
			detached.add(Restrictions.isNotNull("consecutivoDian"));
			detached.add(Restrictions.between("fechaRegistro", hoy, hoyfin));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentoDetalle> getByGrupo(List<Long> tipoDocumentoId, Date hoy, Date hoyfin, Long usuarioId,
			Boolean conCierre, Long server, Long grupoId) {
		Session session;
		if (server == 2l) {
			session = HibernateUtil.getSessionFactory2().openSession();
		} else {
			session = HibernateUtil.getSessionFactory().openSession();
		}

		List<DocumentoDetalle> documentoList = new ArrayList<>();
		try {
			String sql = "select dd from "
					+ "DocumentoDetalle dd where dd.productoId.grupoId.grupoId = :grupoId and  dd.estado = 1 "
					+ "and dd.fechaRegistro >= :hoy AND dd.fechaRegistro <= :hoyFin "
					+ "and dd.documentoId.tipoDocumentoId.tipoDocumentoId in :tipoDocumentoId ";
			sql += " and dd.documentoId.impreso = 1";
			sql += " and dd.documentoId.consecutivoDian is not null ";
			sql += " and dd.documentoId.usuarioId.usuarioId =:usuarioId ";
			if (conCierre) {
				sql += " and dd.documentoId.cierreDiario is null ";
			}
			Query query = session.createQuery(sql);
			query.setParameterList("tipoDocumentoId", tipoDocumentoId);
			query.setParameter("grupoId", grupoId);
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
	public List<Documento> getByCliente(Long clienteId, List<Long> tipoDocumentoId, Date fechaInicio, Date fechafin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			detached.add(Restrictions.eq("clienteId.clienteId", clienteId));
			detached.add(Restrictions.in("tipoDocumentoId.tipoDocumentoId", tipoDocumentoId));
			detached.add(Restrictions.between("fechaRegistro", fechaInicio, fechafin));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByClienteAndProveedorAndTipo(Long clienteId, Long proveedorId,
			List<Long> tipoDocumentoId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			if (clienteId != null) {
				detached.add(Restrictions.eq("clienteId.clienteId", clienteId));
			}
			if (proveedorId != null) {
				detached.add(Restrictions.eq("proveedorId.proveedorId", proveedorId));
			}

			detached.add(Restrictions.in("tipoDocumentoId.tipoDocumentoId", tipoDocumentoId));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getByProveedor(Long proveedorId, List<Long> tipoDocumentoId, Date fechaInicio,
			Date fechafin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			detached.add(Restrictions.eq("proveedorId.proveedorId", proveedorId));
			detached.add(Restrictions.in("tipoDocumentoId.tipoDocumentoId", tipoDocumentoId));
			detached.add(Restrictions.between("fechaRegistro", fechaInicio, fechafin));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> buscarPorInvoice(long invoice) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {
			Invoice objInvoice = new Invoice();
			objInvoice.setInvoiceId(invoice);
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			detached.add(Restrictions.eq("invoiceId", objInvoice));
			detached.addOrder(org.hibernate.criterion.Order.desc("documentoId"));
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

	@Override
	public ConsecutivoDian getConsecutivoDian() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<ConsecutivoDian> documentoList = new ArrayList<>();
		try {			
			DetachedCriteria detached = DetachedCriteria.forClass(ConsecutivoDian.class);
			detached.add(Restrictions.eq("consecutivoDianId", 1l));
			detached.addOrder(org.hibernate.criterion.Order.desc("consecutivoDianId"));
			Criteria criteria = detached.getExecutableCriteria(session);
			documentoList = criteria.list();
		} catch (FactException e) {
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return documentoList.isEmpty()?null:documentoList.get(0);
	}

	@Override
	public void update(ConsecutivoDian consecutivoDian) {
				StatelessSession session;			
					session = HibernateUtil.getSessionFactory().openStatelessSession();			
				Transaction transaction = session.beginTransaction();
				try {
					session.update(consecutivoDian);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Documento> getconvinacion(Long tipodocumentoId, Long tipoPago, Date fechaInicio, Date fechafin) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Documento> documentoList = new ArrayList<>();
		try {			
			DetachedCriteria detached = DetachedCriteria.forClass(Documento.class);
			detached.add(Restrictions.eq("tipoDocumentoId.tipoDocumentoId", tipodocumentoId));
			detached.add(Restrictions.eq("tipoPagoId.tipoPagoId", tipoPago));
			detached.add(Restrictions.between("fechaRegistro", fechaInicio, fechafin));
			detached.addOrder(org.hibernate.criterion.Order.desc("consecutivoDian"));
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
