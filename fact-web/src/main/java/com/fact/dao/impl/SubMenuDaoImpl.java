package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;

import com.fact.api.FactException;
import com.fact.dao.SubMenuDao;
import com.fact.model.SubMenu;
import com.fact.utils.HibernateUtil;

@Stateless()
public class SubMenuDaoImpl implements SubMenuDao{
		
	
	@Override
	public SubMenu getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		SubMenu subMenu=null;
		try {
			subMenu = (SubMenu) session.get(SubMenu.class, id);
		} catch (Exception e) {
			throw e;
		}
		return subMenu;
	}

	@Override
	public SubMenu getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		SubMenu subMenu=null;
		try {
			String sql = "select sm from SubMenu sm where sm.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			subMenu = (SubMenu) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return subMenu;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubMenu> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<SubMenu> subMenuList = new ArrayList<>(); 
		try {
			String sql = "select sm from SubMenu sm order by sm.nombre asc";
			Query query = session.createQuery(sql);
			
			subMenuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return subMenuList;
	}
	
}
