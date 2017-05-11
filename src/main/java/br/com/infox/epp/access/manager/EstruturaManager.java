package br.com.infox.epp.access.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.transaction.SystemException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.transaction.Transaction;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.EstruturaDAO;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.seam.exception.ApplicationException;

@Name(EstruturaManager.NAME)
@AutoCreate
@Stateless
public class EstruturaManager extends Manager<EstruturaDAO, Estrutura> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaManager";
    
    @In private LocalizacaoManager localizacaoManager;
    
    public List<Estrutura> getEstruturasDisponiveis() {
        return getDao().getEstruturasDisponiveis();
    }
    
    public Estrutura getEstruturaByNome(String nome){
    	return getDao().getEstruturaByNome(nome);
    }
    
    @Override
    public Estrutura update(Estrutura o) throws DAOException {
        if (!o.getAtivo()) {
            try {
                for (Localizacao localizacao : o.getLocalizacoes()) {
                    if (localizacao.getLocalizacaoPai() == null) {
                        localizacaoManager.inativar(localizacao);
                    }
                }
            } catch (DAOException e) {
                try {
                    Transaction.instance().rollback();
                    throw e;
                } catch (IllegalStateException | SecurityException | SystemException e1) {
                    throw new ApplicationException("Erro ao fazer rollback da transação", e1);
                }
            }
        }
        return super.update(o);
    }
}
