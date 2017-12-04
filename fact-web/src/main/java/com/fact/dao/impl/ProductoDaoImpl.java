package com.fact.dao.impl;


import java.math.BigDecimal;
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
import com.fact.dao.ProductoDao;
import com.fact.model.Producto;
import com.fact.model.SubProducto;
import com.fact.utils.HibernateUtil;

@Stateless()
public class ProductoDaoImpl implements ProductoDao{
	
	@Override
	public void save(Producto producto,Long server)throws FactException{
		Session session ;
		if(server==2l){
			 session = HibernateUtil.getSessionFactory2().openSession();
		}else{
			session = HibernateUtil.getSessionFactory().openSession();
		}
		Transaction transaction= session.beginTransaction();
		try {
			session.save(producto);
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
	public void update(Producto producto, Long server){
		Session session ;
		if(server==2l){
			 session = HibernateUtil.getSessionFactory2().openSession();
		}else{
			session = HibernateUtil.getSessionFactory().openSession();
		}
		Transaction transaction= session.beginTransaction();
		try {
			session.update(producto);
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
	public void delete(Producto producto){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(producto);
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
	public Producto getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Producto producto=null;
		try {
			producto = (Producto) session.get(Producto.class, id);
		} catch (Exception e) {
			throw e;
		}
		return producto;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Producto> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Producto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Producto m where m.estado=1 order by m.nombre asc";
			Query query = session.createQuery(sql);
			
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}
	
	//SELECT BLOG_ITEM_SEQ.nextval FROM DUAL
	@Override
	public BigDecimal getByUltimoId() throws FactException{
		Session session = HibernateUtil.getSessionFactory().openSession();
		BigDecimal id ; 
		try {
			String sql = "SELECT S_PRODUCTO.nextval FROM DUAL";
			Query query = session.createSQLQuery(sql);		
			id=  (BigDecimal) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Producto> find(List<Long> marcaFilter, Double iva, Long codigo, Long tipoPeso,  List<Long> proveedorFilter,List<Long> grupoFilter) throws FactException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Producto> productoList = new ArrayList<>(); 
		try {
			DetachedCriteria detached= DetachedCriteria.forClass(Producto.class);
			if(marcaFilter!=null && !marcaFilter.isEmpty()){
				detached.add(Restrictions.in("marcaId.marcaId", marcaFilter));
			}
			if(grupoFilter!=null && !grupoFilter.isEmpty()){
				detached.add(Restrictions.in("grupoId.grupoId", grupoFilter));
			}
			if(iva!=null){
				detached.add(Restrictions.eq("iva", iva));
			}
			if(codigo!=null){
				detached.add(Restrictions.eq("productoId", codigo));
			}
			if(proveedorFilter!=null && !proveedorFilter.isEmpty() ){
				detached.add(Restrictions.in("proveedorId.proveedorId", proveedorFilter));
			}
			detached.add(Restrictions.eq("estado", 1l));
			detached.addOrder(org.hibernate.criterion.Order.asc("nombre"));
			Criteria criteria =  detached.getExecutableCriteria(session);
			productoList =criteria.list(); 
			
		} catch (FactException e) {
			throw e;
		}finally{
			if (session!=null) {
				session.close();
			}
		}
		return productoList;
	}
		
	@Override
	public List<Producto> getByList(List<Producto> nombProductos, Long server) {
		Session session ;
		if(server==2l){
			 session = HibernateUtil.getSessionFactory2().openSession();
		}else{
			session = HibernateUtil.getSessionFactory().openSession();
		}
		List<Producto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Producto m where m.estado=1 and m in :nombProductos";
			Query query = session.createQuery(sql);
			query.setParameterList("nombProductos", nombProductos );
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Producto> getBySubProducto(int subProducto) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Producto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Producto m where m.estado=1 and m.subProducto =:subProducto ";
			if(subProducto==0){
				 sql += " or m.subProducto is null";
			}
			sql += " order by m.nombre asc";
			Query query = session.createQuery(sql);
			query.setParameter("subProducto",Long.valueOf( subProducto));
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubProducto> subProductoByProducto(Long productoPadre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<SubProducto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from SubProducto m where m.estado=1 and m.productoPadre.productoId =:productoPadre";
			Query query = session.createQuery(sql);
			query.setParameter("productoPadre",productoPadre);
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}

	@Override
	public void save(SubProducto sub) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		Transaction transaction= session.beginTransaction();
		try {
			session.save(sub);
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
	public void delete(SubProducto s) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();;
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(s);
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
	public Producto getByCodigoBarras(Long codigoBarrasNew) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Producto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Producto m where m.estado=1 and m.codigoBarras =:codigoBarrasNew";
			Query query = session.createQuery(sql);
			query.setParameter("codigoBarrasNew",codigoBarrasNew);
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList.isEmpty()?null:menuList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Producto> getByGrupo(Long grupoId) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Producto> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Producto m where m.estado=1 and m.grupoId.grupoId =:grupoId";
			Query query = session.createQuery(sql);
			query.setParameter("grupoId",grupoId);
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}
}
