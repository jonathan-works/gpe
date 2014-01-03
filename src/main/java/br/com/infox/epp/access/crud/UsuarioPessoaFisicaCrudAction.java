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

    public void setUsuarioAssociado(UsuarioLogin usuarioAssociado) {
        this.usuarioAssociado = usuarioAssociado;
        final PessoaFisica pessoaFisica = usuarioAssociado.getPessoaFisica();
        if (getInstance().getNome() == null && pessoaFisica != null){
            setInstance(pessoaFisica);
        }
    }
    
    public void searchByCpf(String cpf){
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
        if (entityInstance.getAtivo() == null){
            entityInstance.setAtivo(Boolean.TRUE);
        }
        return Boolean.TRUE;
    }
    
    @Override
    public String save() {
        String ret = super.save();
        if (PERSISTED.equals(ret) || UPDATED.equals(ret)){
            usuarioAssociado.setPessoaFisica(getInstance());
            try {
                getGenericManager().update(usuarioAssociado);
                return ret;
            } catch (DAOException e) {
                if (e.getPostgreSQLErrorCode() == PostgreSQLErrorCode.UNIQUE_VIOLATION){
                    final StatusMessages messagesHandler = getMessagesHandler();
                    messagesHandler.clear();
                    messagesHandler.add(PESSOA_JA_ASSOCIADA);
                    LOG.debug(".save()", e);
                } else {
                    LOG.error(".save()", e);
                }
                newInstance();
                usuarioAssociado.setPessoaFisica(null);
                return null;
            }
        }
        return null;
    }
    
    @Override
    public String remove(PessoaFisica t) {
        usuarioAssociado.setPessoaFisica(null);
        try {
            getGenericManager().update(usuarioAssociado);
            newInstance();
            return REMOVED;
        } catch (DAOException e) {
            LOG.error(".remove()", e);
            return null;
        }
    }

}
