package br.com.infox.epp.processo.sigilo.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoManager;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;

@Name(SigiloProcessoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
@Stateless
public class SigiloProcessoService implements Serializable {
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoService";

    @In
    private SigiloProcessoManager sigiloProcessoManager;

    @In
    private SigiloProcessoPermissaoManager sigiloProcessoPermissaoManager;

    public boolean usuarioPossuiPermissao(UsuarioLogin usuario, Processo processo) {
        if (sigiloProcessoManager.isSigiloso(processo)) {
            return sigiloProcessoPermissaoManager.usuarioPossuiPermissao(usuario, sigiloProcessoManager.getSigiloProcessoAtivo(processo));
        }
        return true;
    }

    public void inserirSigilo(SigiloProcesso sigiloProcesso) throws DAOException {
        SigiloProcesso sigiloProcessoAtivo = sigiloProcessoManager.getSigiloProcessoAtivo(sigiloProcesso.getProcesso());
        if (sigiloProcessoAtivo != null) {
            sigiloProcessoPermissaoManager.inativarPermissoes(sigiloProcessoAtivo);
            sigiloProcessoAtivo.setAtivo(false);
            sigiloProcessoManager.update(sigiloProcessoAtivo);
        }
        sigiloProcessoManager.persist(sigiloProcesso);
        SigiloProcessoPermissao permissaoPadrao = new SigiloProcessoPermissao();
        permissaoPadrao.setAtivo(true);
        permissaoPadrao.setSigiloProcesso(sigiloProcesso);
        permissaoPadrao.setUsuario(Authenticator.getUsuarioLogado());
        gravarPermissoes(sigiloProcesso.getProcesso(), Arrays.asList(permissaoPadrao));
    }

    public void gravarPermissoes(Processo processo, List<SigiloProcessoPermissao> permissoes) throws DAOException {
        SigiloProcesso sigiloProcesso = sigiloProcessoManager.getSigiloProcessoAtivo(processo);
        if (sigiloProcesso != null) {
            sigiloProcessoPermissaoManager.gravarPermissoes(permissoes, sigiloProcesso);
        }
    }
}
