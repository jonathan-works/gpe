package br.com.infox.epp.documento.crud;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.list.ClassificacaoDocumentoPapelList;
import br.com.infox.epp.documento.list.ExtensaoArquivoList;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.type.LocalizacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.system.Parametros;

@Named
@ViewScoped
public class ClassificacaoDocumentoCrudAction extends AbstractCrudAction<ClassificacaoDocumento, ClassificacaoDocumentoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @Inject
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;
    @Inject
    private ClassificacaoDocumentoPapelCrudAction classificacaoDocumentoPapelCrudAction;
    @Inject
    private ClassificacaoDocumentoPapelList classificacaoDocumentoPapelList;
    @Inject
    private ExtensaoArquivoCrudAction extensaoArquivoCrudAction;
    @Inject
    private ExtensaoArquivoList extensaoArquivoList;

    private Collection<TipoModeloDocumento> tiposModeloDocumento;

    public Collection<TipoModeloDocumento> getTiposModeloDocumento() {
        if (tiposModeloDocumento == null) {
            populateTiposModeloDocumento();
        }
        return tiposModeloDocumento;
    }

    private void populateTiposModeloDocumento() {
        this.tiposModeloDocumento = tipoModeloDocumentoManager.getTiposModeloDocumentoAtivos();
    }

    public void setTiposModeloDocumento(Collection<TipoModeloDocumento> tiposModeloDocumento) {
        this.tiposModeloDocumento = tiposModeloDocumento;
    }

    public boolean getShowTiposModeloDocumento() {
        return getInstance() != null
                && (TipoDocumentoEnum.T.equals(getInstance().getInTipoDocumento()) || TipoDocumentoEnum.P
                        .equals(getInstance().getInTipoDocumento()));
    }

    @Override
    public void newInstance() {
        setId(null);
        setInstance(new ClassificacaoDocumento());
        setTiposModeloDocumento(null);
    }

    public boolean canShowSelectTipoDocumento() {
        TipoDocumentoEnum tipoDocumento = getInstance().getInTipoDocumento();
        return TipoDocumentoEnum.T.equals(tipoDocumento) || TipoDocumentoEnum.P.equals(tipoDocumento);
    }

    @Override
    protected ClassificacaoDocumentoManager getManager() {
        return classificacaoDocumentoManager;
    }

    public void onClickClassificacaoPapel() {
        classificacaoDocumentoPapelCrudAction.setClassificacaoDocumento(getInstance());
        classificacaoDocumentoPapelList.getEntity().setClassificacaoDocumento(getInstance());
    }

    public void onClickExtensoes() {
        extensaoArquivoCrudAction.setClassificacaoDocumento(getInstance());
        extensaoArquivoList.getEntity().setClassificacaoDocumento(getInstance());
    }

    public boolean isPodeExibirNumeroPaginaAssinaturaEletronica() {
        return LocalizacaoAssinaturaEletronicaDocumentoEnum.PAGINA_UNICA.equals(getInstance().getLocalizacaoAssinaturaEletronicaDocumentoEnum());
    }

    public void onChangeLocalizacaoAssinaturaEletronica() {
        getInstance().setPaginaExibicaoAssinaturaEletronica(null);
    }

    @Override
    public String save() {
        Integer limitePagina = Parametros.LIMITE_PAGINA_ASSINATURA_ELETRONICA.getValue(Integer.class);
        if(limitePagina != null && !isPaginaExibicaoAssinaturaEletronicaValid(limitePagina)) {
            String message = InfoxMessages.getInstance().get("tipoProcessoDocumento.numeroPaginaAssinaturaEletronica.erro");
            getMessagesHandler().add(String.format(message, limitePagina));
            return null;
        }

        return super.save();
    }

    private boolean isPaginaExibicaoAssinaturaEletronicaValid(Integer limitePagina) {
        if(!isPodeExibirNumeroPaginaAssinaturaEletronica()) {
            return true;
        }

        return getInstance().getPaginaExibicaoAssinaturaEletronica() != null
                && getInstance().getPaginaExibicaoAssinaturaEletronica() <= limitePagina;
    }

}
