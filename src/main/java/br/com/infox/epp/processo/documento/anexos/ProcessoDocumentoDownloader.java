package br.com.infox.epp.processo.documento.anexos;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.itx.component.FileHome;
import br.com.itx.component.Util;

@Name(ProcessoDocumentoDownloader.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoDocumentoDownloader {

    @In
    ProcessoDocumentoManager processoDocumentoManager;
    
    @In
    DocumentoBinManager documentoBinManager;

    public static final String NAME = "processoDocumentoDownloader";
    private static final String PAGINA_DOWNLOAD = "/download.xhtml";
    private SimpleDateFormat dfCodData = new SimpleDateFormat("HHmmssSSS");
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoDownloader.class);
    private static final String URL_DOWNLOAD_PROCESSO_DOCUMENTO_EXPRESSION = "/downloadProcessoDocumento.seam?id={0}&codIni={1}&md5={2}";

    public String setDownloadInstance(ProcessoDocumento doc) {
        exportData(doc.getProcessoDocumentoBin());
        return PAGINA_DOWNLOAD;
    }

    public void exportData(ProcessoDocumentoBin doc) {
        FileHome file = FileHome.instance();
        file.setFileName(doc.getNomeArquivo());
        try {
            file.setData(documentoBinManager.getData(doc.getIdProcessoDocumentoBin()));
        } catch (Exception e) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao descarregar o documento.");
            LOG.error(".exportData()", e);
        }
        Contexts.getConversationContext().set("fileHome", file);
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
        ProcessoDocumento pd = processoDocumentoManager.find(id);
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
            data = documentoBinManager.getData(pd.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
        } else {
            data = bin.getModeloDocumento().getBytes();
        }
        fileHome.setData(data);
        fileHome.setFileName(isBin ? bin.getNomeArquivo() : pd.getProcessoDocumento()
                + ".html");
    }

    private boolean isCodDataValido(String codIni, ProcessoDocumento pd) {
        String codData = getCodData(pd);
        if (Strings.isEmpty(codIni) || Strings.isEmpty(codData)) {
            return false;
        }
        return codData.equals(codIni);
    }

    private String getCodData(ProcessoDocumento pd) {
        return dfCodData.format(pd.getDataInclusao());
    }
    
    public String getUrlDownloadProcessoDocumento(ProcessoDocumento processoDocumento) {
        String retorno = MessageFormat.format(URL_DOWNLOAD_PROCESSO_DOCUMENTO_EXPRESSION, Integer.toString(processoDocumento.getIdProcessoDocumento()), getCodData(processoDocumento), processoDocumento.getProcessoDocumentoBin().getMd5Documento());
        return new Util().getUrlProject() + retorno;
    }

}
