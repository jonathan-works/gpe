package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.tarefaexterna.ClassificacaoDocumentoUploadTarefaExternaService;
import br.com.infox.epp.tarefaexterna.ParametrosTarefaExternaService;
import br.com.infox.epp.tarefaexterna.PastaUploadTarefaExternaService;
import lombok.Getter;

@Named
@ViewScoped
public class ConfiguracaoTarefaExternaView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private ConfiguracaoTarefaExternaVO vo;

    @Inject
    private PastaUploadTarefaExternaService pastaUploadTarefaExternaService;

    @Inject
    private ParametrosTarefaExternaService parametrosTarefaExternaService;


    @Inject
    private ClassificacaoDocumentoUploadTarefaExternaService classificacaoDocumentoUploadTarefaExternaService;
    @Inject
    private ConfiguracaoTarefaExternaViewSearch configuracaoTarefaExternaViewSearch;

    @Inject
    private ParametroManager parametroManager;

    @PostConstruct
    private void init() {
        this.vo = new ConfiguracaoTarefaExternaVO();
        clearFormPasta();
        refreshClassificacoes();
        refreshPastas();
        refreshModelos();
        refreshSinais();
        refreshParametros();
    }

    private void refreshParametros() {
        Parametro parametro = parametroManager.getParametro(Parametros.MODELO_DOC_CHAVE_CONSULTA.getLabel());
        getVo().setModeloDocumentoChaveConsulta(parametro.getValorVariavel());

        parametro = parametroManager.getParametro(Parametros.MODELO_DOC_DOWNLOAD_PDF.getLabel());
        getVo().setModeloDocumentoDownloadPDF(parametro.getValorVariavel());

        parametro = parametroManager.getParametro(Parametros.SINAL_TAREFA_EXTERNA.getLabel());
        getVo().setSignalTarefaExterna(parametro.getValorVariavel());
    }

    public void refreshPastas() {
        getVo().setPastas(configuracaoTarefaExternaViewSearch.getPastas());
    }

    public void refreshModelos() {
        getVo().setModelos(configuracaoTarefaExternaViewSearch.getModelos());
    }

    public void refreshSinais() {
        getVo().setSignais(configuracaoTarefaExternaViewSearch.getSignais());
    }

    public void refreshClassificacoes() {
        getVo().setClassificacoes(configuracaoTarefaExternaViewSearch.getClassificacoes());
        getVo().setClassificacoesDisponiveis(configuracaoTarefaExternaViewSearch.getClassificacoesDisponiveis());
    }

    @ExceptionHandled(value = MethodType.UPDATE)
    public void gravarParametros() {
        parametrosTarefaExternaService.update(getVo().getSignalTarefaExterna(), getVo().getModeloDocumentoChaveConsulta(), getVo().getModeloDocumentoDownloadPDF());
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void inserirClassificacaoDocumento() {
        ClassificacaoDocumentoVO selectItem = getVo()
            .getClassificacoesDisponiveis()
            .stream()
            .filter(o -> o.getId().equals(getVo().getClassificacaoParaInserir()))
            .findFirst()
            .get();
        if(selectItem != null) {
            classificacaoDocumentoUploadTarefaExternaService.inserir(getVo().getClassificacaoParaInserir());
            refreshClassificacoes();
        }
    }

    @ExceptionHandled(value = MethodType.REMOVE)
    public void removerClassificacaoDocumento(Integer idClassificacao) {
        classificacaoDocumentoUploadTarefaExternaService.remover(idClassificacao);
        refreshClassificacoes();
    }

    public void clearFormPasta() {
        getVo().setNomePastaParaInserir(null);
        getVo().setCodPastaParaInserir(null);
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void inserirPasta() {
        pastaUploadTarefaExternaService.inserir(getVo().getCodPastaParaInserir(), getVo().getNomePastaParaInserir());
        clearFormPasta();
        refreshPastas();
    }

    @ExceptionHandled(value = MethodType.REMOVE)
    public void removerPasta(Integer id) {
        pastaUploadTarefaExternaService.remover(id);
        refreshPastas();
    }

}