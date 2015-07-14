if (!window.maskMoney) {
	window.maskMoney = function(input, options) {
	    options = options || {};
	    options.showSymbol = options.showSymbol || true;
	    options.symbol = options.symbol || 'R$';
	    options.decimal = options.decimal || ',';
	    options.thousands = options.thousands || '.';
	    
	    if (!input.value) {
	        showValue(input, '0');
	    }
	    
	    input.addEventListener('keyup', showMasked, false);
	    
	    function showMasked() {
	        showValue(this, this.value || '');
	    }
	    
	    function showValue(input, value) {
	        var normalized = value.replace(/\D/g, '');
	        if (normalized === '') {
	        	normalized = '0';
	        }
	        
	        var result = [];
	        if (normalized.length == 1) {
	      	  input.value = options.symbol + ' 0' + options.decimal + '0' + normalized;
	      	  return;
	        } else if (normalized.length == 2) {
	      	  input.value = options.symbol + ' 0' + options.decimal + normalized;
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
	        while ((result[0] === '0' || result[0] === options.thousands) && result[1] !== options.decimal) {
	      	  result.shift();
	        }
	        input.value = options.symbol + ' ' + result.join('');
	    }
	}
}