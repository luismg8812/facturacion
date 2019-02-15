package com.fact.model;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="LISTA")
public class Lista {

	@Id
	@SequenceGenerator(name="S_LISTA", sequenceName="S_LISTA", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_LISTA")
	@NotNull
	@Column(name="LISTA_ID")
	private Long listaId;
	
	@ManyToOne
	@JoinColumn(name="PRODUCTO_ID")
	private Producto productoId;
	
	@Column(name="LISTA_1")
	private Double lista1;
	
	@Column(name="LISTA_2")
	private Double lista2;
	
	@Column(name="LISTA_3")
	private Double lista3;
	
	@Column(name="LISTA_4")
	private Double lista4;

	public Long getListaId() {
		return listaId;
	}

	public void setListaId(Long listaId) {
		this.listaId = listaId;
	}

	public Producto getProductoId() {
		return productoId;
	}

	public void setProductoId(Producto productoId) {
		this.productoId = productoId;
	}

	public Double getLista1() {
		return lista1;
	}

	public void setLista1(Double lista1) {
		this.lista1 = lista1;
	}

	public Double getLista2() {
		return lista2;
	}

	public void setLista2(Double lista2) {
		this.lista2 = lista2;
	}

	public Double getLista3() {
		return lista3;
	}

	public void setLista3(Double lista3) {
		this.lista3 = lista3;
	}

	public Double getLista4() {
		return lista4;
	}

	public void setLista4(Double lista4) {
		this.lista4 = lista4;
	}

}
