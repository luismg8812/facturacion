package com.fact.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.hibernate.Query;
import org.hibernate.Session;

import com.fact.api.FactException;
import com.fact.dao.DepartamentoDao;
import com.fact.model.Departamento;
import com.fact.utils.HibernateUtil;

@Stateless()
public class DepartamentoDaoImpl implements DepartamentoDao{
		
	
	@Override
	public Departamento getById(Long id){
		Session session = HibernateUtil.getSessionFactory().openSession();;
		session.beginTransaction();
		Departamento departamento=null;
		try {
			departamento = (Departamento) session.get(Departamento.class, id);
		} catch (Exception e) {
			throw e;
		}
		return departamento;
	}

	@Override
	public Departamento getByName(String nombre) throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Departamento departamento=null;
		try {
			String sql = "select d from Departamento d where d.nombre =:nombre";
			Query query = session.createQuery(sql);
			query.setParameter("nombre", nombre.toUpperCase());
			departamento = (Departamento) query.uniqueResult(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return departamento;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Departamento> getByAll() throws FactException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Departamento> DepartamentoList = new ArrayList<>(); 
		try {
			String sql = "select d from Departamento d";
			Query query = session.createQuery(sql);
			
			DepartamentoList=  query.list(); 
			session.close();
		} catch (FactException e) {
			throw e;
		}
		return DepartamentoList;
	}
	
}
