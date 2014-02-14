package br.com.infox.epp.processo.partes.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.partes.dao.HistoricoParteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParteProcesso;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;

@Name(HistoricoParteProcessoManager.NAME)
@AutoCreate
public class HistoricoParteProcessoManager extends Manager<HistoricoParteProcessoDAO, HistoricoParteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParteProcessoManager";

    public void createHistorico(ParteProcesso parte, String motivoModificacao) throws DAOException {
        HistoricoParteProcesso historicoParteProcesso = new HistoricoParteProcesso(parte, motivoModificacao);
        historicoParteProcesso.setAtivo(parte.getAtivo());
        historicoParteProcesso.setDataModificacao(new Date());
        historicoParteProcesso.setResponsavelPorModificacao(Authenticator.getUsuarioLogado());
        persist(historicoParteProcesso);
    }

}
