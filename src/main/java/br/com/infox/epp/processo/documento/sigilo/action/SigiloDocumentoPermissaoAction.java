package br.com.infox.epp.processo.documento.sigilo.action;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.util.collection.Factory;
import br.com.infox.util.collection.LazyMap;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(SigiloDocumentoPermissaoAction.NAME)
@Transactional
@ContextDependency
public class SigiloDocumentoPermissaoAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoPermissaoAction";
    private static final LogProvider LOG = Logging.getLogProvider(SigiloDocumentoPermissaoAction.class);

    @In
    private SigiloDocumentoController sigiloDocumentoController;
    @In
    private SigiloDocumentoManager sigiloDocumentoManager;
    @In
    private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private ActionMessagesService actionMessagesService;

    private Map<Integer, Boolean> permissoesMap;
    private Map<Integer, Boolean> usuariosMap;
    private Set<Integer> idsDocumentosSelecionados;

    @Create
    public void init() {
        this.permissoesMap = new LazyMap<>(new Factory<Integer, Boolean>() {
            @Override
            public Boolean create(Integer idDocumento) {
                return false;
            }
        });

        this.usuariosMap = new LazyMap<>(new Factory<Integer, Boolean>() {
            @Override
            public Boolean create(Integer key) {
                inicializarDocumentosSelecionados();
                SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager = (SigiloDocumentoPermissaoManager) Component.getInstance(SigiloDocumentoPermissaoManager.NAME);
                UsuarioLoginManager usuarioLoginManager = BeanManager.INSTANCE.getReference(UsuarioLoginManager.class);
                return sigiloDocumentoPermissaoManager.possuiPermissao(idsDocumentosSelecionados, usuarioLoginManager.find(key));
            }

            private void inicializarDocumentosSelecionados() {
                if (idsDocumentosSelecionados == null) {
                    idsDocumentosSelecionados = new HashSet<>();
                    for (Integer idDocumento : permissoesMap.keySet()) {
                        if (permissoesMap.get(idDocumento)) {
                            idsDocumentosSelecionados.add(idDocumento);
                        }
                    }
                }
            }
        });
    }

    public Map<Integer, Boolean> getPermissoesMap() {
        return permissoesMap;
    }

    public Map<Integer, Boolean> getUsuariosMap() {
        return usuariosMap;
    }

    public void gravarPermissoes() {
        try {
            if (idsDocumentosSelecionados != null) {
                for (Integer idDocumento : idsDocumentosSelecionados) {
                    SigiloDocumento sigiloDocumento = sigiloDocumentoManager.getSigiloDocumentoAtivo(idDocumento);
                    sigiloDocumentoPermissaoManager.inativarPermissoes(sigiloDocumento);
                    for (Integer idUsuario : usuariosMap.keySet()) {
                        if (usuariosMap.get(idUsuario)) {
                            UsuarioLogin usuario = usuarioLoginManager.find(idUsuario);
                            SigiloDocumentoPermissao permissao = new SigiloDocumentoPermissao();
                            permissao.setAtivo(true);
                            permissao.setSigiloDocumento(sigiloDocumento);
                            permissao.setUsuario(usuario);
                            sigiloDocumentoPermissaoManager.persist(permissao);
                        }
                    }
                }
                resetarPermissoes();
                FacesMessages.instance().add(SigiloDocumentoController.MSG_REGISTRO_ALTERADO);
            }
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

    public void resetarPermissoes() {
        sigiloDocumentoController.setFragmentoARenderizar(null);
        init();
    }

    public boolean canManagePermissions() {
        for (Boolean b : permissoesMap.values()) {
            if (b) {
                return true;
            }
        }
        return false;
    }
}
