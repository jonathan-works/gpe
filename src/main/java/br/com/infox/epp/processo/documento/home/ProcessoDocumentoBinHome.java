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

import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.processo.documento.service.AssinaturaDocumentoService;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.FileHome;
import br.com.itx.util.Crypto;

@Name(ProcessoDocumentoBinHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoBinHome extends AbstractHome<ProcessoDocumentoBin> {

    private static final long serialVersionUID = 1L;
    private String certChain;
    private String signature;

    public static final String NAME = "processoDocumentoBinHome";

    private boolean isModelo;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDocumentoBinHome.class);

    private boolean houveErroAoAssinar = false;

    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;
    @In
    private DocumentoBinManager documentoBinManager;

    public String getSignature() {
        return signature;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return certChain;
    }

    public void assinarDocumento(ProcessoDocumento processoDocumento) {
        FacesMessages.instance().clear();
        try {
            assinaturaDocumentoService.verificaCertificadoUsuarioLogado(certChain, Authenticator.getUsuarioLogado());
        } catch (CertificadoException | AssinaturaException e) {
            LOG.error("Não foi possível verificar o certificado do usuário "
                    + Authenticator.getUsuarioLogado(), e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add(e.getMessage());
            this.houveErroAoAssinar = true;
            return;
        }
        setId(processoDocumento.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
        processoDocumento.setLocalizacao(Authenticator.getLocalizacaoAtual());
        processoDocumento.setPapel(Authenticator.getPapelAtual());
        instance.setUsuarioUltimoAssinar(Authenticator.getUsuarioLogado().getNomeUsuario());
        instance.setSignature(signature);
        instance.setCertChain(certChain);
        instance.setDataInclusao(new Date());
        processoDocumento.setProcessoDocumentoBin(instance);
        getEntityManager().merge(processoDocumento);
        getEntityManager().flush();
        FacesMessages.instance().add(Messages.instance().get("assinatura.assinadoSucesso"));
    }

    public boolean isModelo() {
        return isModelo;
    }

    public void isModelo(boolean isModelo) {
        this.isModelo = isModelo;
    }

    @Override
    public void newInstance() {
        FileHome.instance().clear();
        super.newInstance();
    }

    private boolean isModeloVazio() {
        boolean modeloVazio = isModeloVazio(getInstance());
        if (modeloVazio) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O modelo está vazio.");
        }
        return modeloVazio;
    }

    private static boolean isModeloVazio(ProcessoDocumentoBin bin) {
        return bin == null || Strings.isEmpty(bin.getModeloDocumento())
                || Strings.isEmpty(removeTags(bin.getModeloDocumento()));
    }

    private static String removeTags(String modelo) {
        return modelo.replaceAll("\\<.*?\\>", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll("&nbsp;", "");
    }

    @Override
    protected boolean beforePersistOrUpdate() {
        if (isModelo) {
            if (!isModeloVazio()) {
                getInstance().setUsuario(Authenticator.getUsuarioLogado());
                getInstance().setMd5Documento(Crypto.encodeMD5(getInstance().getModeloDocumento()));
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public String persist() {
        String ret = null;
        if (isModelo) {
            ret = super.persist();
        } else {
            FileHome file = FileHome.instance();
            if (processoDocumentoBinManager.isDocumentoBinValido(file)) {
                ProcessoDocumentoBin instance = getInstance();
                instance.setUsuario(Authenticator.getUsuarioLogado());
                instance.setExtensao(file.getFileType());
                instance.setMd5Documento(file.getMD5());
                instance.setNomeArquivo(file.getFileName());
                instance.setSize(file.getSize());
                instance.setModeloDocumento(null);
                ret = super.persist();
                try {
                    documentoBinManager.salvarBinario(instance.getIdProcessoDocumentoBin(), file.getData());
                } catch (DAOException e) {
                    LOG.error("Não foi possível gravar o binário do documento " + instance, e);
                    ret = null;
                }
            }
        }
        if (ret == null) {
            FacesMessages.instance().add(Severity.ERROR, "Erro ao gravar o documento.");
        } else {
            FacesMessages.instance().clear();
            if (getInstance().getUsuario() != null) {
                List<ProcessoDocumentoBin> usuarioList = getInstance().getUsuario().getProcessoDocumentoBinList();
                if (!usuarioList.contains(instance)) {
                    getEntityManager().refresh(getInstance().getUsuario());
                }
            }
        }
        return ret;
    }

    @Override
    protected ProcessoDocumentoBin createInstance() {
        ProcessoDocumentoBin processoDocumentoBin = new ProcessoDocumentoBin();
        processoDocumentoBin.setUsuario(Authenticator.getUsuarioLogado());
        return processoDocumentoBin;
    }

    public boolean isHouveErroAoAssinar() {
        return houveErroAoAssinar;
    }
}
