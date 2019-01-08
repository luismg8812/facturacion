package com.fact.model.invoice;



import java.util.Date;



public class Documento {

	private Long documentoId;
	private Long tipoDocumentoId;
	private Receptor receptorId;
	private Date fechaRegistro;
	private Date fechaActualiza;
	private String numeroDocumento;
	private Double total;
	private Double exento;
	private Double gravado;
	private Double iva;
	private Double iva19;
	private Double iva5;
	private Double base19;
	private Double base5;
	private String prefijo;
	
	public Long getDocumentoId() {
		return documentoId;
	}

	public Long getTipoDocumentoId() {
		return tipoDocumentoId;
	}

	public void setTipoDocumentoId(Long tipoDocumentoId) {
		this.tipoDocumentoId = tipoDocumentoId;
	}



	public Receptor getReceptorId() {
		return receptorId;
	}

	public void setReceptorId(Receptor receptorId) {
		this.receptorId = receptorId;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getFechaActualiza() {
		return fechaActualiza;
	}

	public void setFechaActualiza(Date fechaActualiza) {
		this.fechaActualiza = fechaActualiza;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getExento() {
		return exento;
	}

	public void setExento(Double exento) {
		this.exento = exento;
	}

	public Double getGravado() {
		return gravado;
	}

	public void setGravado(Double gravado) {
		this.gravado = gravado;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Double getIva19() {
		return iva19;
	}

	public void setIva19(Double iva19) {
		this.iva19 = iva19;
	}

	public Double getIva5() {
		return iva5;
	}

	public void setIva5(Double iva5) {
		this.iva5 = iva5;
	}

	public Double getBase19() {
		return base19;
	}

	public void setBase19(Double base19) {
		this.base19 = base19;
	}

	public Double getBase5() {
		return base5;
	}

	public void setBase5(Double base5) {
		this.base5 = base5;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public void setDocumentoId(Long documentoId) {
		this.documentoId = documentoId;
	}
	
}
