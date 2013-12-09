package br.com.infox.epp.access.crud;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

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
        getInstance().setEstrutura(false);
    }
    
    @Override
    protected boolean beforeSave() {
        Localizacao localizacao = getInstance();
        if (localizacao.getEstrutura()){
            localizacao.setEstruturaFilho(null);
            localizacao.setLocalizacaoPai(null);
        }
        return super.beforeSave();
    }
    
    protected void limparTrees(){
        final LocalizacaoTreeHandler ret = (LocalizacaoTreeHandler) Component.getInstance(LocalizacaoTreeHandler.NAME);
        ret.clearTree();
    }
    
    @Override
    protected String update() {
        String ret = null;
        Localizacao localizacao = getInstance();
        if (localizacao.getAtivo() || inativarFilhos(localizacao)) {
            ret = super.update();
        }
        return ret;
    }
    
    private boolean inativarFilhos(Localizacao localizacao) {
        if (localizacao.getItemTipoDocumentoList().size() <= 0) {
            localizacao.setAtivo(false);
            for (int i = 0, quantidadeFilhos = localizacao.getLocalizacaoList().size(); i < quantidadeFilhos; i++) {
                inativarFilhos(localizacao.getLocalizacaoList().get(i));
            }
            return true;
        } else {
            return false;
        }
    }
    
    public List<Localizacao> getLocalizacoesEstrutura(){
        return localizacaoManager.getLocalizacoesEstrutura();
    }

}
