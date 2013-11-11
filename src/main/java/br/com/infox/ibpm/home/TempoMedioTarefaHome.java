package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.epp.entity.TempoMedioTarefa;
import br.com.infox.epp.processo.estatistica.entity.TempoMedioProcesso;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(TempoMedioTarefaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaHome extends AbstractHome<TempoMedioTarefa> {
    private static final long serialVersionUID = 1L;
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
    
}
