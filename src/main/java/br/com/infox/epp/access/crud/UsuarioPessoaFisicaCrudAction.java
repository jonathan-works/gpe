package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaManager;

@Name(UsuarioPessoaFisicaCrudAction.NAME)
public class UsuarioPessoaFisicaCrudAction extends AbstractCrudAction<PessoaFisica> {
    
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioPessoaFisicaCrudAction.class);
    
    private static final String PESSOA_JA_ASSOCIADA = "#{messages['usuario.pessoaJaCadastrada']}";

    public static final String NAME = "usuarioPessoaFisicaCrudAction";
    
    private UsuarioLogin usuarioAssociado;

    @In private PessoaManager pessoaManager;
    
    public UsuarioLogin getUsuarioAssociado() {
        return usuarioAssociado;
    }

    public void setUsuarioAssociado(final UsuarioLogin usuarioAssociado) {
        this.usuarioAssociado = usuarioAssociado;
        final PessoaFisica pessoaFisica = usuarioAssociado.getPessoaFisica();
        final PessoaFisica pessoaFisicaAtual = getInstance();
        if (pessoaFisica != null && pessoaFisicaAtual != null && pessoaFisicaAtual.getNome() == null){
            setInstance(pessoaFisica);
        }
    }
    
    public void searchByCpf(final String cpf){
        newInstance();
        final PessoaFisica pf = pessoaManager.getPessoaFisicaByCpf(cpf);
        if (pf != null){
            setInstance(pf);
        } else {
            getInstance().setCpf(cpf);
        }
    }
    
    @Override
    protected boolean beforeSave() {
        final PessoaFisica entityInstance = getInstance();
        if (entityInstance != null && entityInstance.getAtivo() == null){
            entityInstance.setAtivo(Boolean.TRUE);
        }
        return Boolean.TRUE;
    }
    
    @Override
    public String save() {
        //TODO: Duas persistências em um mesmo método. Ao chegar aqui já deve existir um usuario obrigatoriamente
        String ret = super.save();
        if (PERSISTED.equals(ret) || UPDATED.equals(ret)){
            usuarioAssociado.setPessoaFisica(getInstance());
            try {
                getGenericManager().update(usuarioAssociado);
            } catch (DAOException e) {
                final String logMessagePattern = ".save()";
                if (e.getPostgreSQLErrorCode() == PostgreSQLErrorCode.UNIQUE_VIOLATION){
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
    
    @Override
    public String remove() {
        return null;
    }
    
    @Override
    public String remove(final PessoaFisica t) {
        String ret = null;
        if (t!= null && usuarioAssociado != null && t.equals(usuarioAssociado.getPessoaFisica())) {
            usuarioAssociado.setPessoaFisica(null);
            try {
                getGenericManager().update(usuarioAssociado);
                newInstance();
    			final StatusMessages messages = getMessagesHandler();
                messages.clear();
    			messages.add(MSG_REGISTRO_REMOVIDO);
                ret = REMOVED;
            } catch (DAOException e) {
                LOG.error(".remove()", e);
            }
        }
        return ret;
    }

}
