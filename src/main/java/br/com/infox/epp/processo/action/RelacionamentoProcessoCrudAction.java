package br.com.infox.epp.processo.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.manager.RelacionamentoManager;
import br.com.infox.epp.processo.manager.RelacionamentoProcessoManager;
import br.com.infox.epp.processo.manager.TipoRelacionamentoProcessoManager;
import br.com.infox.seam.exception.ApplicationException;

@Name(RelacionamentoProcessoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class RelacionamentoProcessoCrudAction extends AbstractCrudAction<RelacionamentoProcesso, RelacionamentoProcessoManager> {

    public static final String NAME = "relacionamentoProcessoCrudAction";
    private static final long serialVersionUID = 1L;

    @In
    private RelacionamentoManager relacionamentoManager;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;

    private ProcessoEpa processo;
    private List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList;

    public ProcessoEpa getProcesso() {
        return processo;
    }

    @Override
    protected void beforeSave() {
        try {
            RelacionamentoProcesso relacionamentoProcesso = getInstance();
            Relacionamento relacionamento = relacionamentoProcesso.getRelacionamento();
            ProcessoEpa processo = processoEpaManager.getProcessoEpaByNumeroProcesso(relacionamentoProcesso.getNumeroProcesso());
            getInstance().setProcesso(processo);
            if (relacionamento == null) {
                final Relacionamento relacionamentoByProcesso = relacionamentoManager.getRelacionamentoByProcesso(processo);
                final RelacionamentoProcesso primaryRelacProc = new RelacionamentoProcesso(relacionamentoProcesso.getTipoRelacionamentoProcesso(), relacionamentoByProcesso, this.processo, relacionamentoProcesso.getMotivo());
                if (relacionamentoByProcesso == null) {
                    relacionamento = getManager().persist(relacionamentoProcesso).getRelacionamento();
                } else {
                    relacionamento = relacionamentoByProcesso;
                }
                setInstance(primaryRelacProc);
            }
            getInstance().setRelacionamento(relacionamento);
        } catch (final Exception e) {
            throw new ApplicationException(Messages.instance().get("relacionamento.persist.error"), e);
        }
    }

    @Override
    protected void afterSave(String ret) {
        if (ret != null) {
            final int idProcesso = processo.getIdProcesso();
            newInstance();
            setProcesso(processoEpaManager.find(idProcesso));
        }
    }

    public void setProcesso(final ProcessoEpa processo) {
        this.processo = processo;
        final Relacionamento relacionamento = relacionamentoManager.getRelacionamentoByProcesso(processo);
        getInstance().setRelacionamento(relacionamento);
    }

    public List<TipoRelacionamentoProcesso> getTipoRelacionamentoProcessoList() {
        if (tipoRelacionamentoProcessoList == null) {
            tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoManager.findAll();
        }
        return tipoRelacionamentoProcessoList;
    }

    public void setTipoRelacionamentoProcessoList(
            List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList) {
        this.tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoList;
    }

}
