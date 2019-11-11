package br.com.infox.hibernate.postgres.error;

public enum PostgreSQLErrorCode {

    /*
     * Códigos de erro da Classe 23 do postgresql
     * http://www.postgresql.org/docs/9.1/static/errcodes-appendix.html
     */

    INTEGRITY_CONSTRAINT_VIOLATION("23000"), RESTRICT_VIOLATION("23001"),
    NOT_NULL_VIOLATION("23502"), FOREIGN_KEY_VIOLATION("23503"),
    UNIQUE_VIOLATION("23505"), CHECK_VIOLATION("23514"), EXCLUSION_VIOLATION(
            "23P01");

    private String code;

    private PostgreSQLErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
