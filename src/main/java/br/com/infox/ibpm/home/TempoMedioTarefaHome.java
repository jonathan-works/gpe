package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epa.entity.TempoMedioProcesso;
import br.com.infox.epa.entity.TempoMedioTarefa;
import br.com.infox.epa.list.TempoMedioTarefaList;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(TempoMedioTarefaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaHome extends AbstractHome<TempoMedioTarefa> {
    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "temposMediosTarefa.xls";
    public static final String NAME = "tempoMedioTarefaHome";
    
    private List<TempoMedioProcesso> listTempoMedioProcesso;
    
    public List<TempoMedioProcesso> getListTempoMedioProcesso() {
        if (listTempoMedioProcesso == null) {
            listTempoMedioProcesso = EntityUtil.getEntityList(TempoMedioProcesso.class);
        }
        return listTempoMedioProcesso;
    }

    public void setListTempoMedioProcesso(
            List<TempoMedioProcesso> listTempoMedioProcesso) {
        this.listTempoMedioProcesso = listTempoMedioProcesso;
    }
    
    @Override
    public EntityList<TempoMedioTarefa> getBeanList() {
        return ComponentUtil.getComponent(TempoMedioTarefaList.NAME);
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
    
}
