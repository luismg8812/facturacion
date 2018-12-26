package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;

import com.fact.api.FactException;
import com.fact.dao.MenuDao;
import com.fact.model.Menu;
import com.fact.utils.HibernateUtil;

@Stateless()
public class MenuDaoImpl implements MenuDao{
	
	
	
	
	
	
	
	@Override
	public Menu getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Menu menu=null;
		try {
			menu = (Menu) session.get(Menu.class, id);
		} catch (Exception e) {
			throw e;
		}
		return menu;
	}

	@Override
	public Menu getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Menu menu=null;
		try {
			String sql = "select u from Usuario u where u.login =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			menu = (Menu) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menu;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Menu> menuList = new ArrayList<>(); 
		try {
			String sql = "select m from Menu m";
			Query query = session.createQuery(sql);
			
			menuList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return menuList;
	}
	
}
