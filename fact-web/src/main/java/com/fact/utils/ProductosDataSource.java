//package com.fact.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fact.vo.DocumentoDetalleVo;
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRField;
//
//public class ProductosDataSource implements JRDataSource {
//	
//	private List<DocumentoDetalleVo> productos = new ArrayList<>();
//    private int indiceParticipanteActual = -1;
//    
//    public Object getFieldValue(JRField jrField) throws JRException
//    {
//    	Object valor = null;  
//
//        if("cantidad".equals(jrField.getName())) 
//        { 
//            valor = productos.get(indiceParticipanteActual).getCantidad(); 
//        }   
//        else if ("articulo".equals(jrField.getName()))
//        {
//            valor = productos.get(indiceParticipanteActual).getProductoId().getNombre();
//        }
//        else if ("vUnitario".equals(jrField.getName()))
//        {
//            valor = productos.get(indiceParticipanteActual).getProductoId().getCostoPublico();
//        }
//        else if ("vrParcial".equals(jrField.getName()))
//        {
//            valor = productos.get(indiceParticipanteActual).getParcial();
//        }
//        else if ("iva".equals(jrField.getName()))
//        {
//            valor = productos.get(indiceParticipanteActual).getProductoId().getIva();
//        }
//        return valor; 
//    }
//
//    public boolean next() throws JRException
//    {
//    	 return ++indiceParticipanteActual < productos.size();
//    }
//    
//    public void addProducto(DocumentoDetalleVo p)
//    {
//        this.productos.add(p);
//    }
//
//}
