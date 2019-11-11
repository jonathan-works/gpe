package br.com.infox.epp.access.crud;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class BloqueioUsuarioCrudAction extends AbstractCrudAction<BloqueioUsuario, BloqueioUsuarioManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "bloqueioUsuarioCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(BloqueioUsuarioCrudAction.class);

    @Inject
    private BloqueioUsuarioManager bloqueioUsuarioManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;

    private UsuarioLogin usuarioAtual;

    public UsuarioLogin getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(final UsuarioLogin usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        if (existeBloqueioAtivo()) {
            final BloqueioUsuario ultimoBloqueio = getManager().getUltimoBloqueio(usuarioAtual);
            setInstanceId(ultimoBloqueio.getIdBloqueioUsuario());
        } else {
            newInstance();
            getInstance().setUsuario(this.usuarioAtual);
        }
    }

    @Override
    protected boolean isInstanceValid() {
        final BloqueioUsuario bloqueioUsuario = getInstance();
        if (!this.usuarioAtual.getBloqueio()
                && bloqueioUsuario.getDataBloqueio() != null) {
            bloqueioUsuario.setDataDesbloqueio(new Date());
        } else {
            bloqueioUsuario.setUsuario(this.usuarioAtual);
            bloqueioUsuario.setDataBloqueio(new Date());
        }
        return Boolean.TRUE;
    }

    private boolean existeBloqueioAtivo() {
        final BloqueioUsuario ultimoBloqueio = getManager().getUltimoBloqueio(this.usuarioAtual);
        return ultimoBloqueio != null
                && ultimoBloqueio.getDataDesbloqueio() == null;
    }

    public String desbloquear() {
        this.usuarioAtual.setBloqueio(Boolean.FALSE);
        return save();
    }

    public String bloquear() {
        this.usuarioAtual.setBloqueio(Boolean.TRUE);
        return save();
    }

    @Override
    protected void afterSave(final String ret) {
        if (UPDATED.equals(ret) || PERSISTED.equals(ret)) {
            try {
                usuarioLoginManager.update(this.usuarioAtual);
            } catch (final DAOException e) {
                LOG.error("Não foi possível atualizar as modificações em "
                        + usuarioAtual, e);
            }
        }
    }

    @Override
    protected BloqueioUsuarioManager getManager() {
        return bloqueioUsuarioManager;
    }
}
