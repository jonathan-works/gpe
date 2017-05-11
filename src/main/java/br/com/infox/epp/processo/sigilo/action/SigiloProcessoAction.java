package br.com.infox.epp.processo.sigilo.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoManager;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;

@Name(SigiloProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
@ContextDependency
public class SigiloProcessoAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoAction";
    private static final Logger LOG = Logger.getLogger(SigiloProcessoAction.class);
    public static final String GERENCIAR_PERMISSOES = "P";
    public static final String DETALHES_DO_SIGILO = "D";
    private static final String MSG_REGISTRO_ALTERADO = "#{infoxMessages['sigiloProcesso.registroAlterado']}";

    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private SigiloProcessoService sigiloProcessoService;
    @Inject
    private SigiloProcessoManager sigiloProcessoManager;
    @Inject
    private SigiloProcessoPermissaoManager sigiloProcessoPermissaoManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    private SigiloProcesso sigiloProcesso = new SigiloProcesso();
    private boolean modoInclusao = false;
    private String informacaoTelaSigilo;
    private Processo processo;
    private Map<Integer, Boolean> usuariosPermissao;
    private boolean sigiloso = false;

    public SigiloProcesso getSigiloProcesso() {
        return sigiloProcesso;
    }

    public boolean isModoInclusao() {
        return modoInclusao;
    }

    public void setModoInclusao(boolean modoInclusao) {
        this.modoInclusao = modoInclusao;
    }

    public String getInformacaoTelaSigilo() {
        return informacaoTelaSigilo;
    }

    public void setInformacaoTelaSigilo(String informacaoTelaSigilo) {
        this.informacaoTelaSigilo = informacaoTelaSigilo;
    }

    public void gravarSigiloProcesso() {
        this.sigiloProcesso.setUsuario(Authenticator.getUsuarioLogado());
        this.sigiloProcesso.setDataInclusao(new Date());
        this.sigiloProcesso.setProcesso(this.processo);
        this.sigiloProcesso.setAtivo(true);
        try {
            sigiloProcessoService.inserirSigilo(sigiloProcesso);
            this.sigiloso = !this.sigiloso;
            resetSigiloProcesso();
            resetPermissoes();
            FacesMessages.instance().add(MSG_REGISTRO_ALTERADO);
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

    public void resetSigiloProcesso() {
        SigiloProcesso novoSigiloProcesso = new SigiloProcesso();
        novoSigiloProcesso.setSigiloso(isSigiloso());
        this.sigiloProcesso = novoSigiloProcesso;

        this.modoInclusao = false;
    }

    public boolean isSigiloso() {
        return this.sigiloso;
    }

    public void gravarPermissoes() {
        List<SigiloProcessoPermissao> permissoes = new ArrayList<>();
        for (Integer idUsuario : this.usuariosPermissao.keySet()) {
            SigiloProcessoPermissao permissao = new SigiloProcessoPermissao();
            permissao.setUsuario(usuarioLoginManager.find(idUsuario));
            permissao.setAtivo(this.usuariosPermissao.get(idUsuario));
            permissoes.add(permissao);
        }
        try {
            sigiloProcessoService.gravarPermissoes(processo, permissoes);
            FacesMessages.instance().add(MSG_REGISTRO_ALTERADO);
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

    public void resetPermissoes() {
        this.buildUsuariosPermissao();
    }
    
    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
    	this.processo = processo;
        this.sigiloso = sigiloProcessoManager.isSigiloso(processo);
        this.sigiloProcesso.setSigiloso(isSigiloso());
        buildUsuariosPermissao();
    }

    public Map<Integer, Boolean> getUsuariosPermissao() {
        return usuariosPermissao;
    }

    private void buildUsuariosPermissao() {
        this.usuariosPermissao = new HashMap<>();
        if (isSigiloso()) {
            SigiloProcesso sigiloProcessoAtivo = sigiloProcessoManager.getSigiloProcessoAtivo(processo);
            List<SigiloProcessoPermissao> permissoes = sigiloProcessoPermissaoManager.getPermissoes(sigiloProcessoAtivo);
            for (SigiloProcessoPermissao permissao : permissoes) {
                this.usuariosPermissao.put(permissao.getUsuario().getIdUsuarioLogin(), true);
            }
        }
    }
}
