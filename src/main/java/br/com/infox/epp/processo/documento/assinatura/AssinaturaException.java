package br.com.infox.epp.processo.documento.assinatura;

import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(end = false, rollback = false)
public class AssinaturaException extends Exception {

    private static final long serialVersionUID = 1L;

    public enum Motivo {
        USUARIO_SEM_PESSOA_FISICA, CADASTRO_USUARIO_NAO_ASSINADO,
        CERTIFICADO_USUARIO_DIFERENTE_CADASTRO, SEM_CERTIFICADO,
        CPF_CERTIFICADO_DIFERENTE_USUARIO
    }

    private Motivo motivo;
    private String message;

    public AssinaturaException() {
    }

    public AssinaturaException(String message) {
        super(message);
        this.message = message;
    }

    public AssinaturaException(Throwable cause) {
        super(cause);
    }

    public AssinaturaException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public AssinaturaException(Motivo motivo) {
        this.motivo = motivo;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    @Override
    public String getMessage() {
        if (this.motivo == null || this.message != null) {
            return this.message;
        }

        switch (this.motivo) {
            case USUARIO_SEM_PESSOA_FISICA:
                return "O usuário não possui pessoa física associada.";

            case CADASTRO_USUARIO_NAO_ASSINADO:
                return "O cadastro do usuário não está assinado.";

            case CERTIFICADO_USUARIO_DIFERENTE_CADASTRO:
                return "O certificado não é o mesmo do cadastro do usuario";

            case SEM_CERTIFICADO:
                return "Sem certificado";

            case CPF_CERTIFICADO_DIFERENTE_USUARIO:
                return "CPF do certificado diferente da pessoa física do usuário logado";
            default:
                return null;
        }
    }
}
