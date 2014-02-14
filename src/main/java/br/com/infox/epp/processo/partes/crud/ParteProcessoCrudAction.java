package br.com.infox.epp.processo.partes.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.partes.manager.HistoricoParteProcessoManager;
import br.com.infox.epp.processo.partes.manager.ParteProcessoManager;

@Name(ParteProcessoCrudAction.NAME)
public class ParteProcessoCrudAction extends AbstractCrudAction<ParteProcesso, ParteProcessoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parteProcessoCrudAction";

    @In
    private HistoricoParteProcessoManager historicoParteProcessoManager;

    private String motivoModificacao;

    public String getMotivoModificacao() {
        return motivoModificacao;
    }

    public void setMotivoModificacao(String motivoModificacao) {
        this.motivoModificacao = motivoModificacao;
    }

    public void inverterSituacao() {
        try {
            historicoParteProcessoManager.createHistorico(getInstance(), motivoModificacao);
            getInstance().setAtivo(!getInstance().getAtivo());
        } catch (DAOException daoException) {
            onDAOExcecption(daoException);
        }
        super.save();
        newInstance();
        motivoModificacao = null;
    }

}
