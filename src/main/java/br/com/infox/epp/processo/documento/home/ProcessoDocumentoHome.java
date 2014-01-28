/*
 * IBPM - Ferramenta de produtividade Java Copyright (c) 1986-2009 Infox
 * Tecnologia da Informação Ltda.
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob
 * os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela Free
 * Software Foundation; versão 2 da Licença. Este programa é distribuído na
 * expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 * ESPECÍFICA.
 * 
 * Consulte a GNU GPL para mais detalhes. Você deve ter recebido uma cópia da
 * GNU GPL junto com este programa; se não, veja em http://www.gnu.org/licenses/
 */
package br.com.infox.epp.processo.documento.home;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.home.DocumentoBinHome;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.FileHome;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;

@Name(ProcessoDocumentoHome.NAME)
public class ProcessoDocumentoHome extends AbstractHome<ProcessoDocumento> {

    public static final String NAME = "processoDocumentoHome";

    private static final long serialVersionUID = 1L;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    private static final String PROCESSO_DOCUMENTO_BIN_HOME_NAME = "processoDocumentoBinHome";
    public static final String PETICAO_INSERIDA = "peticaoInseridaMap";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoHome.class);
    private ModeloDocumento modeloDocumentoCombo;
    private boolean isModelo = Boolean.TRUE;
    private SimpleDateFormat dfCodData = new SimpleDateFormat("HHmmssSSS");
    private String documento;
    private static final String URL_DOWNLOAD_PROCESSO_DOCUMENTO_EXPRESSION = "/downloadProcessoDocumento.seam?id={0}&codIni={1}&md5={2}";

    public static ProcessoDocumentoHome instance() {
        return ComponentUtil.getComponent(NAME);
    }

    @Override
    public String persist() {
        instance.setNumeroDocumento(processoDocumentoManager.getNextNumeracao(instance.getTipoProcessoDocumento(), instance.getProcesso()));
        String ret = persistDetalhesDoDocumento();
        newInstance();
        return ret;
    }

    @Override
    public void newInstance() {
        setModelo(false);
        setModeloDocumentoCombo(null);
        ProcessoDocumentoBinHome procDocBin = getProcessoDocumentoBinHome();
        procDocBin.newInstance();
        super.newInstance();
    }

    // Vindo do AbstractProcessoDocumentoHome

    public boolean getModelo() {
        return isModelo;
    }

    public void setModelo(boolean isModelo) {
        this.isModelo = isModelo;
    }

    public ModeloDocumento getModeloDocumentoCombo() {
        return modeloDocumentoCombo;
    }

    public void setModeloDocumentoCombo(ModeloDocumento modeloDocumentoCombo) {
        this.modeloDocumentoCombo = modeloDocumentoCombo;
    }

    @Override
    protected ProcessoDocumento createInstance() {
        ProcessoDocumento processoDocumento = new ProcessoDocumento();
        ProcessoHome processoHome = (ProcessoHome) Component.getInstance("processoHome", false);
        if (processoHome != null) {
            processoDocumento.setProcesso(processoHome.getDefinedInstance());
        }
        return processoDocumento;
    }

    @Override
    public String remove() {
        ProcessoHome processo = (ProcessoHome) Component.getInstance("processoHome", false);
        if (processo != null) {
            processo.getInstance().getProcessoDocumentoList().remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(ProcessoDocumento obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    private String persistDetalhesDoDocumento() {
        ProcessoDocumentoBinHome procDocBinHome = getProcessoDocumentoBinHome();
        procDocBinHome.isModelo(isModelo);
        if (procDocBinHome.persist() == null) {
            return null;
        }
        ProcessoDocumento instance = getInstance();
        instance.setProcessoDocumentoBin(procDocBinHome.getInstance());
        instance.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        instance.setProcesso(ProcessoHome.instance().getInstance());
        setJbpmTask();

        String ret = super.persist();
        if (ret != null) {
            if (isModelo) {
                @SuppressWarnings(UNCHECKED) List<Integer> lista = (List<Integer>) Contexts.getSessionContext().get(PETICAO_INSERIDA);
                if (lista == null) {
                    lista = new ArrayList<Integer>();
                }
                lista.add(instance.getProcesso().getIdProcesso());
                Contexts.getSessionContext().set(PETICAO_INSERIDA, lista);
            }
        }
        return ret;
    }

    public String persistSemLista() {
        return super.persist();
    }

    protected void setJbpmTask() {
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            getInstance().setIdJbpmTask(idJbpmTask);
        }
    }

    public void processarModelo() {
        if (modeloDocumentoCombo != null) {
            ModeloDocumento modeloDocumento = getEntityManager().merge(modeloDocumentoCombo);
            ProcessoDocumentoBinHome procDocBinHome = getProcessoDocumentoBinHome();
            procDocBinHome.getInstance().setModeloDocumento(processarModelo(modeloDocumento.getModeloDocumento()));
        }
    }

    private ProcessoDocumentoBinHome getProcessoDocumentoBinHome() {
        return getComponent(PROCESSO_DOCUMENTO_BIN_HOME_NAME);
    }

    /**
     * Processa um modelo avaliando linha a linha.
     * 
     * @param modelo
     * @return
     */
    public static String processarModelo(String modelo) {
        if (modelo != null) {
            StringBuilder modeloProcessado = new StringBuilder();
            String[] linhas = modelo.split("\n");
            for (int i = 0; i < linhas.length; i++) {
                if (modeloProcessado.length() > 0) {
                    modeloProcessado.append('\n');
                }
                Object o = null;
                try {
                    o = Expressions.instance().createValueExpression(linhas[i]).getValue();
                } catch (RuntimeException e) {
                    LOG.warn("Erro ao avaliar expressão na linha: '"
                            + linhas[i] + "': " + e.getMessage(), e);
                }
                modeloProcessado.append(o);
            }
            return modeloProcessado.toString();
        }
        return modelo;
    }

    private boolean isCodDataValido(String codIni, ProcessoDocumento pd) {
        String codData = getCodData(pd);
        if (Strings.isEmpty(codIni) || Strings.isEmpty(codData)) {
            return false;
        }
        return codData.equals(codIni);
    }

    public String getCodData(ProcessoDocumento pd) {
        return dfCodData.format(pd.getDataInclusao());
    }

    public String getUrlDownloadProcessoDocumento(
            ProcessoDocumento processoDocumento) {
        String retorno = MessageFormat.format(URL_DOWNLOAD_PROCESSO_DOCUMENTO_EXPRESSION, Integer.toString(processoDocumento.getIdProcessoDocumento()), getCodData(processoDocumento), processoDocumento.getProcessoDocumentoBin().getMd5Documento());
        return new Util().getUrlProject() + retorno;
    }

    /**
     * Faz validações de segurança antes de baixar do documento e preencher os
     * dados do fileHome.
     * 
     * @param id - id do ProcessoDocumento
     * @param codIni - String da data de inclusao no formato
     *        <code>HHmmssSSS</code>
     * @param md5 - Md5 do ProcessoDocumentoBin
     * @throws Exception
     */
    public void downloadDocumento(Integer id, String codIni, String md5) {
        FileHome fileHome = FileHome.instance();
        ProcessoDocumento pd = getEntityManager().find(ProcessoDocumento.class, id);
        if (pd == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Processo não encontrado: "
                    + id);
            return;
        } else if (!isCodDataValido(codIni, pd)) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Codigo de verificação inválido.");
            return;
        }
        ProcessoDocumentoBin bin = pd.getProcessoDocumentoBin();
        if (!bin.getMd5Documento().equals(md5)) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O md5 não bate com o do documento.");
            return;
        }

        byte[] data = null;
        boolean isBin = bin.isBinario();
        if (bin.isBinario()) {
            data = DocumentoBinHome.instance().getData(pd.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
        } else {
            data = bin.getModeloDocumento().getBytes();
        }
        fileHome.setData(data);
        fileHome.setFileName(isBin ? bin.getNomeArquivo() : pd.getProcessoDocumento()
                + ".html");
    }

    @Override
    public String update() {
        String ret = null;
        ProcessoDocumentoBinHome procDocBinHome = getProcessoDocumentoBinHome();
        if (procDocBinHome.update() != null) {
            ret = super.update();
        }
        return ret;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
