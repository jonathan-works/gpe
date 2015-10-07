package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.processo.type.TipoProcesso;

public class PainelEntityNode extends EntityNode<Tuple> {
	
    private static final long serialVersionUID = 1L;
    public static final String FLUXO_TYPE = "Fluxo";
    public static final String TASK_TYPE = "Task";
    public static final String CAIXA_TYPE = "Caixa";
    
    private List<EntityNode<Tuple>> caixas;
    private TipoProcesso tipoProcesso;
    private boolean comunicacoesExpedidas;
    private boolean expanded = true;
    private String numeroProcessoRoot;

    public PainelEntityNode(TipoProcesso tipoProcesso, boolean comunicacoesExpedidas) {
        super("");
        this.tipoProcesso = tipoProcesso;
        this.comunicacoesExpedidas = comunicacoesExpedidas;
    }
    
    public PainelEntityNode(EntityNode<Tuple> parent, Tuple entity, TipoProcesso tipoProcesso, boolean comunicacoesExpedidas) {
        super(parent, entity, new String[0]);
        this.tipoProcesso = tipoProcesso;
        this.comunicacoesExpedidas = comunicacoesExpedidas;
    }
    
    public PainelEntityNode(EntityNode<Tuple> parent, Tuple entity, TipoProcesso tipoProcesso, boolean comunicacoesExpedidas, String numeroProcessoRoot) {
        super(parent, entity, new String[0]);
        this.tipoProcesso = tipoProcesso;
        this.comunicacoesExpedidas = comunicacoesExpedidas;
        this.numeroProcessoRoot = numeroProcessoRoot;
    }

    public List<EntityNode<Tuple>> getCaixas() {
        if (caixas == null) {
            caixas = new ArrayList<EntityNode<Tuple>>();
            if (!isLeaf()) {
            	List<Tuple> children = getSituacaoProcessoManager().getCaixaList(tipoProcesso, getTarefaId(), comunicacoesExpedidas, numeroProcessoRoot);
        		for (Tuple entity : children) {
        		    caixas.add(new PainelEntityNode(this, entity, tipoProcesso, comunicacoesExpedidas));
        		}
            }
        }
        return caixas;
    }
    
    @Override
    public String getType() {
        return getEntity().get("type", String.class);
    }

    public Integer getTarefaId() {
    	Integer idTarefa = getEntity().get("idTarefa", Integer.class); 
        return idTarefa == null ? 0 : idTarefa;
    }

    public Long getTaskId() {
    	Long idTask = getEntity().get("idTask", Long.class);
        return idTask == null ? 0L : idTask;
    }

    private SituacaoProcessoManager getSituacaoProcessoManager() {
        return BeanManager.INSTANCE.getReference(SituacaoProcessoManager.class);
    }

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
    
}
