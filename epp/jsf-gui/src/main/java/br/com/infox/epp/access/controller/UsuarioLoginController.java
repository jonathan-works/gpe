package br.com.infox.epp.access.controller;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.access.crud.BloqueioUsuarioCrudAction;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.crud.UsuarioPerfilCrudAction;
import br.com.infox.epp.access.crud.UsuarioPessoaFisicaCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.list.BloqueioUsuarioList;
import br.com.infox.epp.access.list.UsuarioPerfilEntityList;
import br.com.infox.epp.access.list.UsuarioPessoaFisicaList;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaController;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.meiocontato.list.MeioContatoList;
import br.com.infox.epp.pessoa.documento.list.PessoaDocumentoList;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoList;

@Named
@ViewScoped
public class UsuarioLoginController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginController";

    @Inject
    private UsuarioLoginCrudAction usuarioLoginCrudAction;
    @Inject
    private UsuarioPessoaFisicaCrudAction usuarioPessoaFisicaCrudAction;
    @Inject
    private UsuarioPessoaFisicaList usuarioPessoaFisicaList;
    @Inject
    private MeioContatoList meioContatoList;
    @Inject
    private PessoaDocumentoList pessoaDocumentoList;
    @Inject
    private UsuarioPerfilCrudAction usuarioPerfilCrudAction;
    @Inject
    private UsuarioPerfilEntityList usuarioPerfilEntityList;
    @Inject
    private BloqueioUsuarioCrudAction bloqueioUsuarioCrudAction;
    @Inject
    private BloqueioUsuarioList bloqueioUsuarioList;
    @Inject
    private AssinaturaDocumentoList assinaturaDocumentoList;
    @Inject
    private AssinaturaEletronicaController assinaturaEletronicaController;

    @Override
    public void setId(Object id) {
        super.setId(id);
        usuarioLoginCrudAction.setId(id);
    }

    @Override
    public void onClickSearchTab() {
        usuarioLoginCrudAction.onClickSearchTab();
        newInstance();
    }

    @Override
    public void onClickFormTab() {
        usuarioLoginCrudAction.onClickFormTab();
    }

    public void onClickPessoaFisicaTab() {
        usuarioPessoaFisicaCrudAction.newInstance();
        usuarioPessoaFisicaCrudAction.setUsuarioAssociado(getInstance());
        usuarioPessoaFisicaList.setUsuario(getInstance());
        if (getInstance().getPessoaFisica() != null) {
            meioContatoList.getEntity().setPessoa(getInstance().getPessoaFisica());
            pessoaDocumentoList.getEntity().setPessoa(getInstance().getPessoaFisica());
        }
    }

    public void onClickPerfilTab() {
        usuarioPerfilCrudAction.setUsuarioLogin(getInstance());
        usuarioPerfilEntityList.getEntity().setUsuarioLogin(getInstance());
    }

    public void onClickBloqueioTab() {
        bloqueioUsuarioCrudAction.setUsuarioAtual(getInstance());
        bloqueioUsuarioList.getEntity().setUsuario(getInstance());
    }

    public void onClickAssinaturaTab() {
        if (getInstance().getPessoaFisica() != null) {
            assinaturaDocumentoList.setFiltroPessoaFisica(getInstance().getPessoaFisica().getIdPessoa());
            assinaturaEletronicaController.carregarAssinaturaPessoaFisica(getInstance().getPessoaFisica().getIdPessoa());
        } else {
            assinaturaDocumentoList.setFiltroPessoaFisica(null);
        }
    }

    public UsuarioLogin getInstance() {
        return usuarioLoginCrudAction.getInstance();
    }

    public void newInstance() {
        setId(null);
        usuarioLoginCrudAction.newInstance();
        usuarioPessoaFisicaCrudAction.newInstance();
        usuarioPessoaFisicaList.newInstance();
        meioContatoList.newInstance();
        pessoaDocumentoList.newInstance();
        usuarioPerfilCrudAction.newInstance();
        usuarioPerfilEntityList.newInstance();
        bloqueioUsuarioCrudAction.newInstance();
        bloqueioUsuarioList.newInstance();
        assinaturaDocumentoList.newInstance();
        assinaturaEletronicaController.newInstance();
    }

    public void onGravarPessoaFisica() {
        meioContatoList.newInstance();
        pessoaDocumentoList.newInstance();
        meioContatoList.getEntity().setPessoa(getInstance().getPessoaFisica());
        pessoaDocumentoList.getEntity().setPessoa(getInstance().getPessoaFisica());
    }

}
