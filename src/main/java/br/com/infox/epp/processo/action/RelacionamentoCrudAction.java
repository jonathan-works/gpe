package br.com.infox.epp.processo.action;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.manager.RelacionamentoManager;
import br.com.infox.epp.processo.manager.RelacionamentoProcessoManager;
import br.com.infox.epp.processo.manager.TipoRelacionamentoProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.seam.exception.BusinessException;

@Name(RelacionamentoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class RelacionamentoCrudAction extends AbstractCrudAction<Relacionamento, RelacionamentoManager> {

    public static final String NAME = "relacionamentoCrudAction";
    private static final long serialVersionUID = 1L;

    @In
    private RelacionamentoProcessoManager relacionamentoProcessoManager;
    @In
    private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;

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
        return isManaged() || !relacionamentoProcessoManager.existeRelacionamento(processo, processoRelacionado);
    }
    
    @Override
    protected void beforeSave() {
        final Relacionamento relacionamento = getInstance();
        if (relacionamento.getAtivo() == null) {
            relacionamento.setAtivo(Boolean.TRUE);
        }
        relacionamento.setDataRelacionamento(new Date());
        relacionamento.setNomeUsuario(Authenticator.getUsuarioLogado().getNomeUsuario());
    }
    
    @Override
    protected void afterSave(String ret) {
        if (AbstractAction.PERSISTED.equals(ret)) {
            try {
                Relacionamento relacionamento = getManager().find(EntityUtil.getIdValue(getInstance()));
                relacionamentoProcessoManager.persist(new RelacionamentoProcesso(relacionamento, processo));
                relacionamentoProcessoManager.persist(new RelacionamentoProcesso(relacionamento, processoRelacionado));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | DAOException e) {
                throw new BusinessException("", e);
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
            tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoManager.findAll();
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
