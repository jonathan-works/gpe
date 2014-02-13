package br.com.infox.epp.processo.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.Messages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.exception.ApplicationException;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.RelacionamentoManager;
import br.com.infox.epp.processo.manager.RelacionamentoProcessoManager;

@Name(RelacionamentoProcessoCrudAction.NAME)
public class RelacionamentoProcessoCrudAction extends AbstractCrudAction<RelacionamentoProcesso,RelacionamentoProcessoManager> {

    public static final String NAME = "relacionamentoProcessoCrudAction";
    private static final long serialVersionUID = 1L;
    
    private ProcessoEpa processo;
    @In
    private RelacionamentoManager relacionamentoManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    
    public ProcessoEpa getProcesso() {
        return processo;
    }
    
    @Override
    protected void beforeSave() {
        try {
            final RelacionamentoProcesso relProcesso = getInstance();
            final ProcessoEpa processo = processoEpaManager.getProcessoEpaByNumeroProcesso(relProcesso.getNumeroProcesso());
            Relacionamento relacionamento = relProcesso.getRelacionamento();
            if (relacionamento == null && processo == null) {
                final RelacionamentoProcesso primaryRelacProc = new RelacionamentoProcesso(relProcesso.getTipoRelacionamentoProcesso(), relacionamento,this.processo,relProcesso.getMotivo());
                relacionamento = getManager().persist(primaryRelacProc).getRelacionamento();
            }
            relProcesso.setProcesso(processo);
            relProcesso.setRelacionamento(relacionamento);
        } catch (final Exception e) {
            throw new ApplicationException(Messages.instance().get("relacionamento.persist.error"), e);
        }
    }
    
    public void setProcesso(final ProcessoEpa processo) {
        this.processo = processo;
        final Relacionamento relacionamento = relacionamentoManager.getRelacionamentoByProcesso(processo);
        getInstance().setRelacionamento(relacionamento);
    }
    
}
