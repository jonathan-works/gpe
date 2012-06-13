function chkTextLength(field, max, out) {
	if (field.value.length > max)
		field.value = field.value.substring(0, max);
	else { 
		$(out).innerHTML = max - field.value.length;
		$(out).style.visibility = 'visible';
	}
}