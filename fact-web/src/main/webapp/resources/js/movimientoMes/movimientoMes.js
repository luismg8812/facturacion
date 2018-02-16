/**
 * 
 */
function pupupCantidadMM() {
	PF('cantidadPopupMM').show();
	document.getElementById('cantidadMM:facturar').focus();
	document.getElementById('cantidadMM:facturar').select();
	document.getElementById('cantidadMM:facturar').className = ' ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all state-focus ui-state-focus';
	// alert("ejecuta");
}

function controlTeclasMM(e,element){
	if(e.keyCode==79){
		document.getElementById('movMesForm:Opc_movi_mes_menuButton').click();
		//alert("tecla o")		movimiento mes		
	}
	if(e.keyCode==85){
		document.getElementById('movMesForm:Ult_movi_mes').click();
		//alert("tecla u")		movimiento mes		
	}
	if(e.keyCode==77){
		document.getElementById('movMesForm:Mod_movi_mes').click();
		//alert("tecla m")		movimiento mes		
	}
	if(e.keyCode==73){
		document.getElementById('movMesForm:Imp_movi_mes').click();
		//alert("tecla i")		movimiento mes		
	}
	
	if(e.keyCode==66){
		document.getElementById('movimiento_mes_bor').click();
		//alert("tecla b")		movimiento mes		
	}

	if(e.keyCode==65){
		document.getElementById('movMesForm:Ant_movi_mes').click();
		//alert("tecla a")		movimiento mes		
	}

	if(e.keyCode==83){
		document.getElementById('movMesForm:Sig_movi_mes').click();
		//alert("tecla s")		movimiento mes		
	}
}	