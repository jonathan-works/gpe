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
    }
    
    @Override
    protected boolean beforeSave() {
        if (getInstance().getEstrutura()){
            getInstance().setEstruturaFilho(null);
            getInstance().setLocalizacaoPai(null);
        }
        return super.beforeSave();
    }
    
    @Override
    protected void afterSave() {
        super.afterSave();
    }
    
    @Override
    protected void limparTrees(){
        final LocalizacaoTreeHandler ret = (LocalizacaoTreeHandler) Component.getInstance(LocalizacaoTreeHandler.NAME);
        ret.clearTree();
    }
    
    @Override
    protected String update() {
        String ret = null;
        if (getInstance().getAtivo() || inativarFilhos(getInstance())) {
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
