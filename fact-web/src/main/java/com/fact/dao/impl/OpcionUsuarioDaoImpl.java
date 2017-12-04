package com.fact.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.fact.api.FactException;
import com.fact.dao.OpcionUsuarioDao;
import com.fact.model.OpcionUsuario;
import com.fact.model.Usuario;
import com.fact.utils.HibernateUtil;

@Stateless()
public class OpcionUsuarioDaoImpl implements OpcionUsuarioDao {

	@Override
	public OpcionUsuario getById(Long id) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		session.beginTransaction();
		OpcionUsuario opcionUsuario = null;
		try {
			opcionUsuario = (OpcionUsuario) session.get(OpcionUsuario.class, id);
		} catch (Exception e) {
			throw e;
		}
		return opcionUsuario;
	}

	@Override
	public OpcionUsuario getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		OpcionUsuario opcionUsuario = null;
		try {
			String sql = "select o from opcionUsuario u where u.login =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			opcionUsuario = (OpcionUsuario) query.uniqueResult();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return opcionUsuario;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OpcionUsuario> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<OpcionUsuario> OpcionUsuarioList = new ArrayList<>();
		try {
			String sql = "select ou from OpcionUsuario ou";
			Query query = session.createQuery(sql);

			OpcionUsuarioList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return OpcionUsuarioList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OpcionUsuario> getByMenuId(String menuId, Long usuarioId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<OpcionUsuario> OpcionUsuarioList = new ArrayList<>();
		try {
			String sql = "select ou from OpcionUsuario ou where ou.menuId.menuId =:menuId and ou.usuarioId.usuarioId =:usuarioId and ou.estado=1 order by ou.opcionUsuarioId";
			Query query = session.createQuery(sql);
			query.setParameter("menuId", Long.valueOf(menuId));
			query.setParameter("usuarioId", usuarioId);
			OpcionUsuarioList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return OpcionUsuarioList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OpcionUsuario> getByRuta(String ruta, Long usuarioId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<OpcionUsuario> OpcionUsuarioList = new ArrayList<>();
		try {
			String sql = "select ou from OpcionUsuario ou where ou.ruta =:ruta and ou.usuarioId.usuarioId =:usuarioId and ou.estado = 1 and ou.subMenuId.ruta is not null"
					+ " order by ou.opcionUsuarioId ";
			Query query = session.createQuery(sql);
			query.setParameter("ruta", ruta);
			query.setParameter("usuarioId", usuarioId);
			OpcionUsuarioList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return OpcionUsuarioList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OpcionUsuario> getByUsuario(String usuarioOpcion) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<OpcionUsuario> OpcionUsuarioList = new ArrayList<>();
		try {
			String sql = "select ou from OpcionUsuario ou where ou.usuarioId.usuarioId =:usuarioId order by ou.opcionUsuarioId";
			Query query = session.createQuery(sql);
			query.setParameter("usuarioId", Long.valueOf(usuarioOpcion));
			OpcionUsuarioList = query.list();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return OpcionUsuarioList;
	}

	@Override
	public void delete(OpcionUsuario opcionUsuario) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(opcionUsuario);
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
	public void save(OpcionUsuario opcionUsuario) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.save(opcionUsuario);
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
	public List<OpcionUsuario> getByRutas(List<String> rutas, Long usuarioId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<OpcionUsuario> opcionUsuarioList = new ArrayList<>();
		try {
			DetachedCriteria detached = DetachedCriteria.forClass(OpcionUsuario.class);
			detached.add(Restrictions.in("ruta", rutas));
			detached.add(Restrictions.eq("usuarioId.usuarioId", usuarioId));
			detached.addOrder(org.hibernate.criterion.Order.desc("opcionUsuarioId"));
			Criteria criteria = detached.getExecutableCriteria(session);
			opcionUsuarioList = criteria.list();
		} catch (FactException e) {
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return opcionUsuarioList;
	}

	@Override
	public OpcionUsuario getbySubMenuAndUsuario(Usuario u, Long subMenuId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		OpcionUsuario opcionUsuario = null;
		try {
			String sql = "select ou from OpcionUsuario ou where ou.usuarioId.usuarioId =:usuarioId and ou.subMenuId.subMenuId =:subMenuId";
			Query query = session.createQuery(sql);
			query.setParameter("usuarioId", u.getUsuarioId());
			query.setParameter("subMenuId", subMenuId);
			opcionUsuario = (OpcionUsuario) query.uniqueResult();
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return opcionUsuario;
	}

	@Override
	public void update(OpcionUsuario cuadre) throws FactException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		Transaction transaction = session.beginTransaction();
		try {
			session.update(cuadre);
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

}
