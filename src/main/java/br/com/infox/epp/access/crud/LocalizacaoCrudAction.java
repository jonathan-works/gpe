package br.com.infox.epp.access.crud;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;

@Name(LocalizacaoCrudAction.NAME)
public class LocalizacaoCrudAction extends AbstractRecursiveCrudAction<Localizacao> {
    
    public static final String NAME = "localizacaoCrudAction";
    
    @In private LocalizacaoManager localizacaoManager;
    
    @Override
    public void newInstance() {
        limparTrees();
        super.newInstance();
        getInstance().setEstrutura(Boolean.FALSE);
    }
    
    @Override
    protected boolean isInstanceValid() {
        final Localizacao localizacao = getInstance();
        
        final Boolean isEstrutura = localizacao.getEstrutura();
        if (isEstrutura != null) {
            if (isEstrutura){
                localizacao.setEstruturaFilho(null);
                localizacao.setLocalizacaoPai(null);
            } else {
                final Localizacao localizacaoPai = localizacao.getLocalizacaoPai();
                if (localizacaoPai != null && localizacaoPai.equals(localizacao.getEstruturaFilho())) {
                	final StatusMessages messagesHandler = getMessagesHandler();
                    messagesHandler.clear();
                	messagesHandler.add("#{messages['localizacao.localizacaoPaiIgualEstruturaFilho']}");
                	return Boolean.FALSE;
                }
            }
        }
        return super.isInstanceValid();
    }
    
    protected void limparTrees(){
        final LocalizacaoTreeHandler ret = (LocalizacaoTreeHandler) Component.getInstance(LocalizacaoTreeHandler.NAME);
        if (ret != null) {
            ret.clearTree();
        }
    }
    
    @Override
    protected String update() {
        String ret = null;
        final Localizacao localizacao = getInstance();
        if ((localizacao.getAtivo() != null) && (localizacao.getAtivo() || inativarFilhos(localizacao))) {
            ret = super.update();
        }
        return ret;
    }
    
    private boolean inativarFilhos(final Localizacao localizacao) {
        if (localizacao.getItemTipoDocumentoList().size() <= 0) {
            localizacao.setAtivo(Boolean.FALSE);
            for (int i = 0, quantidadeFilhos = localizacao.getLocalizacaoList().size(); i < quantidadeFilhos; i++) {
                inativarFilhos(localizacao.getLocalizacaoList().get(i));
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public List<Localizacao> getLocalizacoesEstrutura(){
        return localizacaoManager.getLocalizacoesEstrutura();
    }

}
