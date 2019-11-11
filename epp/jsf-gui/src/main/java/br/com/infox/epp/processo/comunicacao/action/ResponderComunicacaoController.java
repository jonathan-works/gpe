package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Named
@ViewScoped
public class ResponderComunicacaoController implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private RespostaComunicacaoAction respostaComunicacaoAction;
    @Inject
    private ProcessoManager processoManager;
    
    private String tab;
    private Processo processoComunicacao;
    
    public void onClickTabComunicacaoes() {
        processoComunicacao = null;
    }
    
    public void onSelectComunicacao(Integer idProcesso) {
        Processo processoComunicacao = processoManager.find(idProcesso);
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
