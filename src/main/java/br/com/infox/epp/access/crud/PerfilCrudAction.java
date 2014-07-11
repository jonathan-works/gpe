package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;

@Name(PerfilCrudAction.NAME)
public class PerfilCrudAction extends AbstractCrudAction<UsuarioLocalizacao, UsuarioLocalizacaoManager> {

    public static final String NAME = "perfilCrudAction";
    private static final long serialVersionUID = 1L;

    private Localizacao estrutura;
    
    @Override
    protected void beforeSave() {
        getInstance().setEstrutura(estrutura);
    }

    public Localizacao getEstrutura() {
        return estrutura;
    }

    public void setEstrutura(Localizacao estrutura) {
        this.estrutura = estrutura;
    }
    
}
