package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoManager.NAME)
@AutoCreate
public class LocalizacaoManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    private static final Class<Localizacao> CLASS = Localizacao.class;
    public static final String NAME = "localizacaoManager";

    @In
    private LocalizacaoDAO localizacaoDAO;
    
    public Localizacao find(Integer id){
        return find(CLASS, id);
    }

    public List<Localizacao> getLocalizacoesEstrutura() {
        return localizacaoDAO.getLocalizacoesEstrutura();
    }

}
