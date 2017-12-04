package com.fact.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="MENU")
public class Menu {

	@Id
	@SequenceGenerator(name="S_MENU", sequenceName="S_MENU", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_MENU")
	@NotNull
	@Column(name="MENU_ID")
	private Long menuId;
	@NotNull
	
	@Column(name="NOMBRE")
	private String nombre;
	
	@Column(name="ESTADO")
	private Long estado;
	
	@Column(name="URL")
	private String url;
	
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
