package br.com.infox.hibernate.oracle.error;

public enum OracleErrorCode {
	UNIQUE_VIOLATION(1), FOREIGN_KEY_VIOLATION_HAS_CHILDREN(2292), NOT_NULL_VIOLATION(1400), CHECK_CONSTRAINT_VIOLATION(2290);
	
	private int code;
	
	private OracleErrorCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
