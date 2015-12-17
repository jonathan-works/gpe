package br.com.infox.epp.processo.action;

import static java.text.MessageFormat.format;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.exception.EppSystemException;
import br.com.infox.core.exception.StandardErrorCode;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso.TipoProcesso;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.manager.RelacionamentoManager;
import br.com.infox.epp.processo.manager.RelacionamentoProcessoManager;
import br.com.infox.epp.processo.manager.TipoRelacionamentoProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;

@Name(RelacionamentoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class RelacionamentoCrudAction extends
        AbstractCrudAction<Relacionamento, RelacionamentoManager> {

    public static final String NAME = "relacionamentoCrudAction";
    public static final String PROCESSO_RELAC_MSG = "Processo já está relacionado.";
    private static final long serialVersionUID = 1L;

    @In
    private RelacionamentoProcessoManager relacionamentoProcessoManager;
    @In
    private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;
    @In
    private ProcessoManager processoManager;
    @In
    private InfoxMessages infoxMessages;

    private String processo;
    private String processoRelacionado;
    private List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList;

    public String getProcesso() {
        return processo;
    }

    public void setEditMode(String numeroProcesso, Object id) {
        setId(id);
        this.processoRelacionado = numeroProcesso;
    }

    @Override
    public String inactive(Relacionamento t) {
        return super.inactive((Relacionamento) HibernateUtil.removeProxy(t));
    }

    @Override
    protected boolean isInstanceValid() {
        boolean result = true;
    	if (getInstance().getMotivo().trim().length() > 0 && !isManaged()){
    		if (relacionamentoProcessoManager.existeRelacionamento(processo, TipoProcesso.ELE, processoRelacionado, TipoProcesso.FIS)){
    			getMessagesHandler().add(PROCESSO_RELAC_MSG);
    			return result = false;
    		}
    	}
    	return result;
    }

    @Override
    protected void beforeSave() {
        final Relacionamento relacionamento = getInstance();
        if (relacionamento.getAtivo() == null) {
            relacionamento.setAtivo(Boolean.TRUE);
        }
        relacionamento.setDataRelacionamento(new Date());
        relacionamento.setNomeUsuario(Authenticator.getUsuarioLogado()
                .getNomeUsuario());
    }

    @Override
    protected void afterSave(String ret) {
        if (AbstractAction.PERSISTED.equals(ret)) {
            try {
                Relacionamento relacionamento = getManager().find(
                        EntityUtil.getIdValue(getInstance()));
                relacionamentoProcessoManager
                        .persist(new RelacionamentoProcesso(relacionamento,
                                processo, TipoProcesso.ELE));
                relacionamentoProcessoManager
                        .persist(new RelacionamentoProcesso(relacionamento,
                                processoRelacionado, TipoProcesso.FIS));
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | DAOException e) {
                throw EppSystemException.create(StandardErrorCode.UNKNOWN, e);
            }
            newInstance();
        }
    }

    @Override
    public void newInstance() {
        super.newInstance();
        processoRelacionado = "";
    }

    public void setProcesso(final String processo) {
        this.processo = processo;
    }

    public List<TipoRelacionamentoProcesso> getTipoRelacionamentoProcessoList() {
        if (tipoRelacionamentoProcessoList == null) {
            tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoManager
                    .findAll();
        }
        return tipoRelacionamentoProcessoList;
    }

    public void setTipoRelacionamentoProcessoList(
            List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList) {
        this.tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoList;
    }

    public String getProcessoRelacionado() {
        return processoRelacionado;
    }

    public void setProcessoRelacionado(String processoRelacionado) {
        this.processoRelacionado = processoRelacionado;
    }

}
