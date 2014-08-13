package br.com.infox.epp.tce.prestacaocontas.modelo.action;

import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.GrupoPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.GrupoPrestacaoContasManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.ModeloPrestacaoContasManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.TipoParteManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.EsferaGovernamental;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.TipoPrestacaoContas;

@Name(ModeloPrestacaoContasAction.NAME)
public class ModeloPrestacaoContasAction extends AbstractCrudAction<ModeloPrestacaoContas, ModeloPrestacaoContasManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasAction";

    @In
    private GrupoPrestacaoContasManager grupoPrestacaoContasManager;
    @In
    private TipoParteManager tipoParteManager;

    private SelectItem[] anosExercicio;
    private List<GrupoPrestacaoContas> gruposPrestacaoContas;
    private SelectItem[] esferas;
    private SelectItem[] tiposPrestacaoContas;
    
    @Override
    public void newInstance() {
        super.newInstance();
    }
    
    public SelectItem[] getAnosExercicio() {
        if (anosExercicio == null) {
            Calendar now = Calendar.getInstance();
            int ano = now.get(Calendar.YEAR);
            String descricaoAno = String.valueOf(ano);
            anosExercicio = new SelectItem[] {
                new SelectItem(ano, descricaoAno),
                new SelectItem(ano - 1, String.valueOf(ano - 1))
            };
        }
        return anosExercicio;
    }
    
    public List<GrupoPrestacaoContas> getGruposPrestacaoContas() {
        if (gruposPrestacaoContas == null) {
            gruposPrestacaoContas = grupoPrestacaoContasManager.findAll();
        }
        return gruposPrestacaoContas;
    }
    
    public SelectItem[] getEsferas() {
        if (esferas == null) {
            EsferaGovernamental[] values = EsferaGovernamental.values();
            esferas = new SelectItem[values.length];
            for (int i = 0; i < values.length; i++) {
                esferas[i] = new SelectItem(values[i], values[i].getLabel());
            }
        }
        return esferas;
    }
    
    public SelectItem[] getTiposPrestacaoContas() {
        if (tiposPrestacaoContas == null) {
            TipoPrestacaoContas[] values = TipoPrestacaoContas.values();
            tiposPrestacaoContas = new SelectItem[values.length];
            for (int i = 0; i < values.length; i++) {
                tiposPrestacaoContas[i] = new SelectItem(values[i], values[i].getLabel());
            }
        }
        return tiposPrestacaoContas;
    }
    
    public TipoProcessoDocumento getClassificacaoDocumento() {
        return null;
    }
    
    public void setClassificacaoDocumento(TipoProcessoDocumento classificacao) {
        getInstance().getClassificacoesDocumento().add(classificacao);
    }
}
