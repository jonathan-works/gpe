package br.com.infox.epp.access.crud;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class UsuarioPessoaFisicaCrudAction extends AbstractCrudAction<PessoaFisica, PessoaFisicaManager> {
    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(UsuarioPessoaFisicaCrudAction.class);

    private static final String PESSOA_JA_ASSOCIADA = "#{infoxMessages['usuario.pessoaJaCadastrada']}";

    private UsuarioLogin usuarioAssociado;

    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private PessoaFisicaManager pessoaFisicaManager;

    public UsuarioLogin getUsuarioAssociado() {
        return usuarioAssociado;
    }

    public void setUsuarioAssociado(final UsuarioLogin usuarioAssociado) {
        this.usuarioAssociado = usuarioAssociado;
        final PessoaFisica pessoaFisica = usuarioAssociado.getPessoaFisica();
        final PessoaFisica pessoaFisicaAtual = getInstance();
        if (pessoaFisica != null && pessoaFisicaAtual != null
                && pessoaFisicaAtual.getNome() == null) {
            setInstance(pessoaFisica);
        }
    }

    public void searchByCpf(final String cpf) {
        newInstance();
        final PessoaFisica pf = getManager().getByCpf(cpf);
        if (pf != null) {
            setInstance(pf);
        } else {
            getInstance().setCpf(cpf);
        }
    }

    @Override
    protected boolean isInstanceValid() {
        final PessoaFisica entityInstance = getInstance();
        if (entityInstance != null && entityInstance.getAtivo() == null) {
            entityInstance.setAtivo(Boolean.TRUE);
        }
        return Boolean.TRUE;
    }

    @Override
    public String save() {
        // TODO: Duas persistências em um mesmo método. Ao chegar aqui já deve
        // existir um usuario obrigatoriamente
        String ret = super.save();
        if (PERSISTED.equals(ret) || UPDATED.equals(ret)) {
            usuarioAssociado.setPessoaFisica(getInstance());
            try {
                usuarioLoginManager.update(usuarioAssociado);
            } catch (final DAOException e) {
                final String logMessagePattern = ".save()";
                if (e.getDatabaseErrorCode() == GenericDatabaseErrorCode.UNIQUE_VIOLATION) {
                    final StatusMessages messagesHandler = getMessagesHandler();
                    messagesHandler.clear();
                    messagesHandler.add(PESSOA_JA_ASSOCIADA);
                    LOG.debug(logMessagePattern, e);
                } else {
                    LOG.error(logMessagePattern, e);
                }
                newInstance();
                usuarioAssociado.setPessoaFisica(null);
                ret = null;
            }
        }
        return ret;
    }
    
    public void gravarUsuarioPessoaFisica() {
    	save();
    	newInstance();
    }

    @Override
    public String remove() {
        return null;
    }

    @Override
    public String remove(final PessoaFisica t) {
        String ret = null;
        if (t != null && usuarioAssociado != null
                && t.equals(usuarioAssociado.getPessoaFisica())) {
            usuarioAssociado.setPessoaFisica(null);
            try {
                usuarioLoginManager.update(usuarioAssociado);
                newInstance();
                final StatusMessages messages = getMessagesHandler();
                messages.clear();
                messages.add(MSG_REGISTRO_REMOVIDO);
                ret = REMOVED;
            } catch (final DAOException e) {
                LOG.error(".remove()", e);
            }
        }
        return ret;
    }

    @Override
    protected PessoaFisicaManager getManager() {
        return pessoaFisicaManager;
    }

}
