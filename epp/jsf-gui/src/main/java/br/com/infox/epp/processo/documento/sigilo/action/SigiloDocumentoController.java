package br.com.infox.epp.processo.documento.sigilo.action;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;

@Named
@ViewScoped
public class SigiloDocumentoController implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MSG_REGISTRO_ALTERADO = "#{infoxMessages['sigiloDocumento.registroAlterado']}";

    public static enum FragmentoSigilo {
        MOTIVO_SIGILO, DETALHE_SIGILO, PERMISSOES_SIGILO
    }

    private FragmentoSigilo fragmentoARenderizar;
    private Documento documentoSelecionado;

    public FragmentoSigilo getFragmentoARenderizar() {
        return fragmentoARenderizar;
    }

    public void setFragmentoARenderizar(FragmentoSigilo fragmentoARenderizar) {
        this.fragmentoARenderizar = fragmentoARenderizar;
    }

    public Documento getDocumentoSelecionado() {
        return documentoSelecionado;
    }

    public void setDocumentoSelecionado(Documento documentoSelecionado) {
        this.documentoSelecionado = documentoSelecionado;
    }
}
