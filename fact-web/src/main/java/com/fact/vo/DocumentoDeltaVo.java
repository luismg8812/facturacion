package com.fact.vo;



import java.util.List;

import com.fact.model.invoice.Documento;
import com.fact.model.invoice.DocumentoDetalle;



public class DocumentoDeltaVo {
	
	private Documento documentoId;
	private List<DocumentoDetalle> documentoDetalleId;
	
	public Documento getDocumentoId() {
		return documentoId;
	}
	public void setDocumentoId(Documento documentoId) {
		this.documentoId = documentoId;
	}
	public List<DocumentoDetalle> getDocumentoDetalleId() {
		return documentoDetalleId;
	}
	public void setDocumentoDetalleId(List<DocumentoDetalle> documentoDetalleId) {
		this.documentoDetalleId = documentoDetalleId;
	}
	

}
