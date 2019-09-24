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
@Table(name="DOCUMENTO")
public class Documento {

	@Id
	@SequenceGenerator(name="S_DOCUMENTO", sequenceName="S_DOCUMENTO", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="S_DOCUMENTO")
	@NotNull
	@Column(name="DOCUMENTO_ID")
	private Long documentoId;
	
	@ManyToOne
	@JoinColumn(name="TIPO_DOCUMENTO_ID")
	private TipoDocumento tipoDocumentoId;
	
	@ManyToOne
	@JoinColumn(name="PROVEEDOR_ID")
	private Proveedor proveedorId;
	
	@ManyToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuarioId;
	
	@ManyToOne
	@JoinColumn(name="CLIENTE_ID")
	private Cliente clienteId;
	
	@ManyToOne
	@JoinColumn(name="EMPLEADO_ID")
	private Empleado empleadoId;
	
	@ManyToOne
	@JoinColumn(name="TIPO_PAGO_ID")
	private TipoPago tipoPagoId;
	
	@ManyToOne
	@JoinColumn(name="INVOICE")
	private Invoice invoiceId;
	
	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;
	
	@Column(name="FECHA_ACTUALIZA")
	private Date fechaActualiza;
	
	@Column(name="CONSECUTIVO_DIAN")
	private String consecutivoDian;
	
	@Column(name="IMPRESO")
	private Long impreso;
	
	@Column(name="TOTAL")
	private Double total;
	
	@Column(name="EXCENTO")
	private Double excento;
	
	@Column(name="GRAVADO")
	private Double gravado;
	
	@Column(name="IVA")
	private Double iva;
	
	@Column(name="CIERRE_DIARIO")
	private Long cierreDiario;
	
	@Column(name="DETALLE_ENTRADA")
	private String detalleEntrada;
	
	@Column(name="SALDO")
	private Double saldo;
	
	@Column(name="REDUCCION")
	private Long reduccion;
	
	@Column(name="ANULADO")
	private Long anulado;
	
	@Column(name="PESO_TOTAL")
	private Double pesoTotal;
	
	@Column(name="DESCUENTO")
	private Double descuento;
	
	@Column(name="VALOR_TARJETA")
	private Double valorTarjeta;
	
	@Column(name="EFECTIVO")
	private Double efectivo;
	
	@Column(name="CAMBIO")
	private Double cambio;
	
	@Column(name="ENTREGADO")
	private Long entregado;
	
	@Column(name="RETENIDO")
	private Long retenido;
	
	@Column(name="MAC")
	private String mac;
	
	@Column(name="IVA_19")
	private Double iva19;
	
	@Column(name="IVA_5")
	private Double iva5;
	
	@Column(name="BASE_19")
	private Double base19;
	
	@Column(name="BASE_5")
	private Double base5;
	
	@Column(name="RETEFUENTE")
	private Double retefuente;
	
	@Column(name="INTERES")
	private Double interes;
	
	@Column(name="TOTAL_COSTO")
	private Double totalCosto;
	
	@Column(name="IMPRESORA")
	private Long impresora;
	
	public Long getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(Long documentoId) {
		this.documentoId = documentoId;
	}

	public TipoDocumento getTipoDocumentoId() {
		return tipoDocumentoId;
	}

	public void setTipoDocumentoId(TipoDocumento tipoDocumentoId) {
		this.tipoDocumentoId = tipoDocumentoId;
	}

	public Proveedor getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Proveedor proveedorId) {
		this.proveedorId = proveedorId;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
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

	
	
	public Double getInteres() {
		return interes;
	}

	public void setInteres(Double interes) {
		this.interes = interes;
	}

	public String getConsecutivoDian() {
		return consecutivoDian;
	}

	public void setConsecutivoDian(String consecutivoDian) {
		this.consecutivoDian = consecutivoDian;
	}

	public Long getImpreso() {
		return impreso;
	}

	public void setImpreso(Long impreso) {
		this.impreso = impreso;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getExcento() {
		return excento;
	}

	public void setExcento(Double excento) {
		this.excento = excento;
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

	public Long getCierreDiario() {
		return cierreDiario;
	}

	public void setCierreDiario(Long cierreDiario) {
		this.cierreDiario = cierreDiario;
	}

	public String getDetalleEntrada() {
		return detalleEntrada;
	}

	public void setDetalleEntrada(String detalleEntrada) {
		this.detalleEntrada = detalleEntrada;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Cliente getClienteId() {
		return clienteId;
	}

	public void setClienteId(Cliente clienteId) {
		this.clienteId = clienteId;
	}

	public TipoPago getTipoPagoId() {
		return tipoPagoId;
	}

	public void setTipoPagoId(TipoPago tipoPagoId) {
		this.tipoPagoId = tipoPagoId;
	}

	public Long getReduccion() {
		return reduccion;
	}

	public void setReduccion(Long reduccion) {
		this.reduccion = reduccion;
	}

	public Double getPesoTotal() {
		return pesoTotal;
	}

	public void setPesoTotal(Double pesoTotal) {
		this.pesoTotal = pesoTotal;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public Double getValorTarjeta() {
		return valorTarjeta;
	}

	public void setValorTarjeta(Double valorTarjeta) {
		this.valorTarjeta = valorTarjeta;
	}

	public Double getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(Double efectivo) {
		this.efectivo = efectivo;
	}

	public Double getCambio() {
		return cambio;
	}

	public void setCambio(Double cambio) {
		this.cambio = cambio;
	}

	public Long getEntregado() {
		return entregado;
	}

	public void setEntregado(Long entregado) {
		this.entregado = entregado;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Long getRetenido() {
		return retenido;
	}

	public void setRetenido(Long retenido) {
		this.retenido = retenido;
	}

	public Empleado getEmpleadoId() {
		return empleadoId;
	}

	public void setEmpleadoId(Empleado empleadoId) {
		this.empleadoId = empleadoId;
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

	public Double getRetefuente() {
		return retefuente;
	}

	public void setRetefuente(Double retefuente) {
		this.retefuente = retefuente;
	}

	public Double getTotalCosto() {
		return totalCosto;
	}

	public void setTotalCosto(Double totalCosto) {
		this.totalCosto = totalCosto;
	}

	public Invoice getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Invoice invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getAnulado() {
		return anulado;
	}

	public void setAnulado(Long anulado) {
		this.anulado = anulado;
	}

	public Long getImpresora() {
		return impresora;
	}

	public void setImpresora(Long impresora) {
		this.impresora = impresora;
	}
	
	

	
}
