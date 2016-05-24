function showModal(modalId) {
	setTimeout('RichFaces.$("' + modalId + '").show()', 100);
}

function hideModal(modalId) {
	setTimeout('RichFaces.$("' + modalId + '").hide()', 100);
} 

function setMesmaData(di, df) {
	if( $(df).val() == "" || $(df).val() == null || $(df).val().startsWith("_") ) {
		$(df).val($(di).val());
	}
	if( $(di).val() == "" || $(di).val() == null || $(di).val().startsWith("_") ) {
		$(di).val($(df).val());
	}
} 

function validarPeriodo(di, df, mesmaData, dataInicio) {
	if (mesmaData) {
		setMesmaData(di, df);
	}
	 if (toDate($(di).val()).getTime() > toDate($(df).val()).getTime()) {
	   alert("A data inicial deve ser menor ou igual a data final.");
	   if (dataInicio)	{
		   $(di).val("");
	   } else {
		   $(df).val("");
	   }
	 }
}

function validarPeriodoAnoMes(di, df, mesmaData) {
	if (mesmaData) {
		setMesmaData(di, df);
	}
	if (getDateFirstDayOfMonth($(di).val()).getTime() > getDateLastDayOfMonth($(df).val()).getTime()) {
		alert("A data inicial deve ser menor ou igual a data final.");
		$(df).val("");
	}
}

function validarDataMaxima(data) {
	if (getDateFirstDayOfMonth(data.value).getTime() > new Date().getTime() ) {
		alert("A data n\u00E3o pode ser superior que a data atual.");
		data.value = "";
	} 
}

function toDate(string) {
	 data = string.split("/");
	 return new Date(data[2],data[1] - 1,data[0]);
}

function getDateFirstDayOfMonth(string) {
	 data = string.split("/");
	 return new Date(data[1], data[0]-1, 1);
}

function getDateLastDayOfMonth(string){
	 data = string.split("/");
	 return new Date(data[1], data[0], 0);
}

function validarDataPassada(campoData){
	if(toDate(campoData.value).getTime() > new Date().getTime()){
		alert('A data informada n\u00E3o pode ser maior que a data atual.');
		campoData.value = "";
		campoData.focus();
		return false;
	}
	return true;
}

function validarDataBaseCalculoRpv(campoData){
    var dataLimite = '01/07/1994';
	if(toDate(campoData.value).getTime() > new Date().getTime()){
		alert('A data informada n\u00E3o pode ser maior que a data atual.');
		campoData.value = "";
		campoData.focus();
		return false;
	}
	
	if(toDate(campoData.value).getTime() < toDate(dataLimite).getTime()){
		alert('A data informada n\u00E3o pode ser menor que 01/07/1994.');
		campoData.value = "";
		campoData.focus();
		return false;
	}
	return true;
}

function validarData(digData) {
    var patternDMA = /\d{2}[/]\d{2}[/]\d{4}/;
    var patternMA = /\d{2}[/]\d{4}/;
	var data = digData.value;
	if (data.replace(/[_|\/]/g, "") == "") {
		return true;
	}
    if (patternDMA.test(data)){
        var dia = data.substr(0,2);
        var mes = data.substr(3,2);
        var ano = data.substr(6,4);
    } else if (patternMA.test(data)){
        var dia = 1;
        var mes = data.substr(0,2);
        var ano = data.substr(3,4);
    }else{
        alert(decodeURI("A Data tem um formato inv\u00E1lido."));
        digData.value = "";
        return false;
    }
    if (!validarDataDMA(dia, mes, ano)) {
        alert("A Data "+data+" \u00E9 inv\u00E1lida!");
        digData.value = "";
	return false;
    }
}

function validarDataDMA(dia, mes, ano) {
    if (ano > 1500){
    switch (mes){
        case '01':
        case '03':
        case '05':
        case '07':
        case '08':
        case '10':
        case '12':
        if  (dia <= 31)
            return true;
        break
        case '04':              
        case '06':
        case '09':
        case '11':
        if  (dia <= 30) 
            return true;
        break
        case '02':
                var bissexto = 0;
                if ((ano % 4 == 0) || (ano % 100 == 0) || (ano % 400 == 0)) 
                        bissexto = 1; 
                if ((bissexto == 1) && (dia <= 29))
                        return true;
                if ((bissexto != 1) && (dia <= 28)) 
                        return true; 
                break                                           
    }
    }
    return false;
}

function onlyNumber(obj){
    valor = obj.value;
    signal = valor.indexOf('-') == 0 ? '-' : '';
    obj.value = signal + valor.replace(/\D/g,"");
}

function onlyPositiveNumber(obj){
    valor = obj.value;
    obj.value = valor.replace(/\D/g,"");
}

function formatValuePercentage(obj){
	formatDecimalNumber(obj);
}

function addPercentageSymbol(obj) {
	valor = obj.value;
	valor = valor.replace(/%/g, '');
	if (valor.length > 0) {
		obj.value = valor + '%';
	} else {
		obj.value = '';
	}
}

function formatMoney(obj) {
	formatDecimalNumber(obj, {symbol: 'R$ '});
}

//Máscara de número decimal com duas casas e sem valor default
function formatDecimalNumber (obj, options) {
	options = options || {};
	options.symbol = options.symbol || '';
	options.decimal = options.decimal || ',';
	options.thousands = options.thousands || '.';
	
	valor = obj.value;
	var normalized = valor.replace(/\D/g, '');
	if (normalized === '') {
		obj.value = '';
		return;
	}
	
	var result = [];
	if (normalized.length == 1) {
		obj.value = options.symbol + '0' + options.decimal + '0' + normalized;
		return;
	} else if (normalized.length == 2) {
		 obj.value = options.symbol + '0' + options.decimal + normalized;
		 return;
	}
	
	var count = 0;
	var hasDecimal = false;
	for (var i = normalized.length - 1; i >= 0; i--) {
		if (i == normalized.length - 3) {
			result.push(options.decimal);
			hasDecimal = true;
		}
		if (hasDecimal && count == 3) {
			result.push(options.thousands);
			count = 0;
		}
		result.push(normalized.charAt(i));
		if (hasDecimal) {
			count++;
		}
	}
	result.reverse();
	while ((result[0] === '0'  || result[0] === options.thousands) && result[1] !== options.decimal) {
		result.shift();
	}
	valor = result.join('');
	obj.value = options.symbol + valor;
}



function clamp(obj, min, max) {
    if ((!isNaN(obj.value)) && (isFinite(obj.value)) && (obj.value != "")) {
        obj.value = Math.min(max, Math.max(min, obj.value));
    }
}

//Validador de E-Mail
function validarEmail(obj) {
	var mail = obj.value;		
	var er = new RegExp(/^[A-Za-z0-9_\-\.]+@[A-Za-z0-9_\-\.]{2,}\.[A-Za-z0-9]{2,}(\.[A-Za-z0-9])?/);
	if(mail == "")return;
	if(!er.test(mail)){
		alert("Formato inv\u00E1lido do e-mail.");
		obj.value = "";
		obj.focus();
	}
} 

// Validador de CPF
function validarCpf(obj) {
	cpf = obj.value;
	cpf = cpf.replace(/\./g, "");
	cpf = cpf.replace(/-/g, "");
	cpf = cpf.replace(/_/g, "");
	if (cpf == ""){
		return;
	}
	var filtro = /^\d{11}$/;
	if (!filtro.test(cpf) || !isCpfValido(cpf)) {
		window.alert("CPF inv\u00E1lido.");
		obj.value = "";
		obj.focus();
	}
}

function isCpfValido(cpf) {
	soma = 0;
	for (i = 0; i < 9; i++)
		soma += parseInt(cpf.charAt(i)) * (10 - i);
	dv1 = 11 - (soma % 11);
	if (dv1 == 10 || dv1 == 11)
		dv1 = 0;
	if (dv1 != parseInt(cpf.charAt(9))) {
		return false;
	}
	soma = 0;
	for (i = 0; i < 10; i++)
		soma += parseInt(cpf.charAt(i)) * (11 - i);
	dv2 = 11 - (soma % 11);
	if (dv2 == 10 || dv2 == 11)
		dv2 = 0;
	if (dv2 != parseInt(cpf.charAt(10))) {
		return false;
	}
	return true;
}

// Validador de CNPJ
function validarCnpj(obj) {
	var numeros, digitos, soma, i, resultado, pos, tamanho, digitos_iguais, cnpj;
	cnpj = obj.value;
	cnpj = cnpj.replace(/\./g, "");
	cnpj = cnpj.replace(/-/g, "");
	cnpj = cnpj.replace(/_/g, "");
	cnpj = cnpj.replace(/\//g, "");
	if (cnpj == ""){
		return;
	}
	var filtro = /^\d{14}$/;
	if (!filtro.test(cnpj) || !isCnpjValido(cnpj)) {
		window.alert("CNPJ inv\u00E1lido.");
		obj.value = "";
		obj.focus();
	}
}

function isCnpjValido(cnpj) {
	soma = 0;
	for (i = 0; i < 4; i++) {
		soma = soma + parseInt(cnpj.charAt(i)) * (5 - i);
	}
	for (i = 4; i < 12; i++) {
		soma = soma + parseInt(cnpj.charAt(i)) * (13 - i);
	}
	dv1 = 11 - (soma % 11);
	if (dv1 >= 10) {
		dv1 = 0;
	}
	if (dv1 != parseInt(cnpj.charAt(12))) {
//		window.alert( dv1 + " " + parseInt(cnpj.charAt(12)));
		return false;
	}
	soma = 0;
	for (i = 0; i < 5; i++) {
		soma = soma + parseInt(cnpj.charAt(i)) * (6 - i);
	}
	for (i = 5; i < 12; i++) {
		soma = soma + parseInt(cnpj.charAt(i)) * (14 - i);
	}
	soma = soma + dv1 * 2;
	dv2 = 11 - (soma % 11);
	if (dv2 >= 10) {
		dv2 = 0;
	}
	if (dv2 != parseInt(cnpj.charAt(13))) {
//		window.alert( dv2 + " " + parseInt(cnpj.charAt(13)));
		return false;
	}
	return true;
}

/**
 * Usado pelo componente inputTextarea para contar e limitar o texto
 * @param obj � o campo do formulario
 * @param max � o limite m�ximo do texto
 * @return
 */
function counterTextarea(obj, max) {
	if (!max || max == '') return;
	typed = obj.nextElementSibling;
	var maxlength = parseInt(max);					  			
	if(obj.value.length > maxlength) {
		obj.value = obj.value.substring(0, maxlength);
	}
	typed.innerHTML = "( " + obj.value.length + " / " + maxlength +" )";
}

PrimeFaces.locales['pt_BR'] = { closeText: 'Fechar', prevText: 'Anterior', nextText: 'Próximo', currentText: 'Começo', monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'], monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'], dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'], dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'], dayNamesMin: ['D','S','T','Q','Q','S','S'], weekHeader: 'Semana', firstDay: 0, isRTL: false, showMonthAfterYear: false, yearSuffix: '', timeOnlyTitle: 'Só Horas', timeText: 'Tempo', hourText: 'Hora', minuteText: 'Minuto', secondText: 'Segundo', ampm: false, month: 'Mês', week: 'Semana', day: 'Dia', allDayText : 'Todo o Dia' }; 