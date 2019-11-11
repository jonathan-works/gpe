package br.com.infox.hibernate.sqlserver.error;

public enum SQLServer2012ErrorCode {
    UNIQUE_VIOLATION_INDEX(2601), UNIQUE_VIOLATION_CONSTRAINT(2627), NOT_NULL_VIOLATION(515), FOREIGN_KEY_VIOLATION(547), DEADLOCK(1205);
    
    private int code;
    
    private SQLServer2012ErrorCode(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
