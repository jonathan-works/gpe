package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class ResponderComunicacaoController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tab;
    private Processo processoComunicacao;
    
    @Inject
    private RespostaComunicacaoAction respostaComunicacaoAction;
    
    public void onClickTabComunicacaoes() {
        processoComunicacao = null;
    }
    
    public void onSelectComunicacao(Processo processoComunicacao) {
        respostaComunicacaoAction.init(processoComunicacao);
        this.processoComunicacao = processoComunicacao;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
    
    public Processo getProcessoComunicacao() {
        return processoComunicacao;
    }

}
