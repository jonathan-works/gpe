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

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(ProcessoDocumentoHome.NAME)
public class ProcessoDocumentoHome extends AbstractHome<ProcessoDocumento> {

    public static final String NAME = "processoDocumentoHome";

    private static final long serialVersionUID = 1L;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    private static final String PROCESSO_DOCUMENTO_BIN_HOME_NAME = "processoDocumentoBinHome";
    private boolean isModelo = Boolean.TRUE;

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

        return super.persist();
    }

    protected void setJbpmTask() {
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            getInstance().setIdJbpmTask(idJbpmTask);
        }
    }

    private ProcessoDocumentoBinHome getProcessoDocumentoBinHome() {
        return getComponent(PROCESSO_DOCUMENTO_BIN_HOME_NAME);
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

}
