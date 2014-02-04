package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoManager.NAME)
@AutoCreate
public class LocalizacaoManager extends Manager<LocalizacaoDAO, Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoManager";

    public List<Localizacao> getLocalizacoesEstrutura() {
        return getDao().getLocalizacoesEstrutura();
    }

}
