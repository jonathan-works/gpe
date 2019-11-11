package br.com.infox.epp.pessoa.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.infox.epp.pessoa.annotation.Data;

public class DataValidator implements ConstraintValidator<Data, String>{
	
	private boolean past;
	private boolean future;
	private String pattern;

	@Override
	public void initialize(Data data) {
		this.past = data.past();
		this.future = data.future();
		this.pattern = data.pattern();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null){
			return true;
		}
		Date parsed = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			parsed = dateFormat.parse(value);
			Date hoje = new Date();
			if (past) {
				return hoje.after(parsed);
			}
			if(future) {
				return hoje.before(parsed);
			}
			return true;
		} catch (ParseException | IllegalArgumentException e) {
			return false;
		}
	}

}
