package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.LocalizacaoFisicaDAO;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;

@Name(LocalizacaoFisicaManager.NAME)
@AutoCreate
public class LocalizacaoFisicaManager extends Manager<LocalizacaoFisicaDAO, LocalizacaoFisica> {
    private static final long serialVersionUID = 4455754174682600299L;
    public static final String NAME = "localizacaoFisicaManager";
}
