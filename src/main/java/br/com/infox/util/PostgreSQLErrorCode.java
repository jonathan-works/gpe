package br.com.infox.util;

public enum PostgreSQLErrorCode {
	
	/*
	 * Códigos de erro da Classe 23 do postgresql
	 * http://www.postgresql.org/docs/9.1/static/errcodes-appendix.html
	 * */
	
	integrity_constraint_violation("23000"),
	restrict_violation("23001"), not_null_violation("23502"), foreign_key_violation("23503"), unique_violation("23505"),
	check_violation("23514"), exclusion_violation("23P01");
	
	private String code;

	private PostgreSQLErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	
}
