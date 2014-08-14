package br.com.infox.epp.tce.prestacaocontas.modelo.action;

import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.GrupoPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContasClassificacaoDocumento;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ResponsavelModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.TipoParte;
import br.com.infox.epp.tce.prestacaocontas.modelo.list.ClassificacaoDocumentoPrestacaoContasList;
import br.com.infox.epp.tce.prestacaocontas.modelo.list.ResponsavelModeloPrestacaoContasList;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.GrupoPrestacaoContasManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.ModeloPrestacaoContasManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.manager.TipoParteManager;
import br.com.infox.epp.tce.prestacaocontas.modelo.service.ModeloPrestacaoContasService;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.EsferaGovernamental;
import br.com.infox.epp.tce.prestacaocontas.modelo.type.TipoPrestacaoContas;

@Name(ModeloPrestacaoContasAction.NAME)
public class ModeloPrestacaoContasAction extends AbstractCrudAction<ModeloPrestacaoContas, ModeloPrestacaoContasManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasAction";
    private static final LogProvider LOG = Logging.getLogProvider(ModeloPrestacaoContasAction.class);

    @In
    private GrupoPrestacaoContasManager grupoPrestacaoContasManager;
    @In
    private TipoParteManager tipoParteManager;
    @In
    private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    @In
    private ClassificacaoDocumentoPrestacaoContasList classificacaoDocumentoPrestacaoContasList;
    @In
    private ResponsavelModeloPrestacaoContasList responsavelModeloPrestacaoContasList;
    @In
    private ModeloPrestacaoContasService modeloPrestacaoContasService;
    @In
    private ActionMessagesService actionMessagesService;

    private SelectItem[] anosExercicio;
    private List<GrupoPrestacaoContas> gruposPrestacaoContas;
    private SelectItem[] esferas;
    private SelectItem[] tiposPrestacaoContas;
    private List<TipoProcessoDocumento> documentosDisponiveis;
    private List<TipoParte> tiposParteDisponiveis;
    private ResponsavelModeloPrestacaoContas responsavel = new ResponsavelModeloPrestacaoContas();
    private TipoProcessoDocumento classificacao = new TipoProcessoDocumento();
    
    @Override
    public void newInstance() {
        super.newInstance();
        documentosDisponiveis = null;
        tiposParteDisponiveis = null;
    }
    
    @Override
    public void setInstance(ModeloPrestacaoContas instance) {
        super.setInstance(instance);
        if (isManaged()) {
            responsavelModeloPrestacaoContasList.getEntity().setModeloPrestacaoContas(getInstance());
            classificacaoDocumentoPrestacaoContasList.getEntity().setModeloPrestacaoContas(getInstance());
        }
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
    
    public void addClassificacaoDocumento() {
        ModeloPrestacaoContasClassificacaoDocumento doc = new ModeloPrestacaoContasClassificacaoDocumento();
        doc.setModeloPrestacaoContas(getInstance());
        doc.setClassificacaoDocumento(classificacao);
        try {
            modeloPrestacaoContasService.adicionarClassificacaoDocumento(doc);
            documentosDisponiveis = null;
            classificacao = null;
        } catch (DAOException e) {
            LOG.error("addClassificacaoDocumento", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void addResponsavel() {
        responsavel.setModeloPrestacaoContas(getInstance());
        try {
            modeloPrestacaoContasService.adicionarResponsavel(responsavel);
            tiposParteDisponiveis = null;
            responsavel = new ResponsavelModeloPrestacaoContas();
        } catch (DAOException e) {
            LOG.error("addResponsavel", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void removeClassificacaoDocumento(ModeloPrestacaoContasClassificacaoDocumento classificacaoDocumento) {
        try {
            modeloPrestacaoContasService.removerClassificacaoDocumento(classificacaoDocumento);
            documentosDisponiveis = null;
        } catch (DAOException e) {
            LOG.error("removeClassificacaoDocumento", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void removeResponsavel(ResponsavelModeloPrestacaoContas responsavel) {
        try {
            modeloPrestacaoContasService.removerResponsavel(responsavel);
            tiposParteDisponiveis = null;
        } catch (DAOException e) {
            LOG.error("removeResponsavel", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public List<TipoParte> getTiposParteDisponiveis() {
        if (tiposParteDisponiveis == null) {
            tiposParteDisponiveis = tipoParteManager.listTiposParteParaModeloPrestacaoContas();
        }
        return tiposParteDisponiveis; 
    }
    
    public List<TipoProcessoDocumento> getDocumentosDisponiveis() {
        if (documentosDisponiveis == null) {
            documentosDisponiveis = tipoProcessoDocumentoManager.listClassificacoesParaModeloPrestacaoContas();
        }
        return documentosDisponiveis;
    }
    
    public ResponsavelModeloPrestacaoContas getResponsavel() {
        return responsavel;
    }

    public TipoProcessoDocumento getClassificacao() {
        return classificacao;
    }
    
    public void setClassificacao(TipoProcessoDocumento classificacao) {
        this.classificacao = classificacao;
    }
    
    public void importar(ModeloPrestacaoContas modeloPrestacaoContas) {
        newInstance();
        ModeloPrestacaoContas modelo = getInstance();
        
        modelo.setAnoExercicio(modeloPrestacaoContas.getAnoExercicio());
        modelo.setEsfera(modeloPrestacaoContas.getEsfera());
        modelo.setGrupoPrestacaoContas(modeloPrestacaoContas.getGrupoPrestacaoContas());
        modelo.setNome(modeloPrestacaoContas.getNome());
        modelo.setQuestionarExistenciaSetoresContabilidade(modeloPrestacaoContas.getQuestionarExistenciaSetoresContabilidade());
        modelo.setQuestionarExistenciaSetoresControleInterno(modeloPrestacaoContas.getQuestionarExistenciaSetoresControleInterno());
        modelo.setTipoPrestacaoContas(modeloPrestacaoContas.getTipoPrestacaoContas());
        modelo.getClassificacoesDocumento().addAll(modeloPrestacaoContas.getClassificacoesDocumento());
        for (ResponsavelModeloPrestacaoContas responsavel : modelo.getResponsaveis()) {
            ResponsavelModeloPrestacaoContas novoResponsavel = new ResponsavelModeloPrestacaoContas();
            novoResponsavel.setModeloPrestacaoContas(modelo);
            novoResponsavel.setObrigatorio(responsavel.getObrigatorio());
            novoResponsavel.setTipoParte(responsavel.getTipoParte());
            modelo.getResponsaveis().add(novoResponsavel);
        }
        
        setTab("form");
    }
}
