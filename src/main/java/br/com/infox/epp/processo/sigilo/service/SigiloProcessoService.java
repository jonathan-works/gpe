package br.com.infox.epp.processo.sigilo.service;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoManager;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;

@Name(SigiloProcessoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class SigiloProcessoService implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "sigiloProcessoService";
	
	@In
	private SigiloProcessoManager sigiloProcessoManager;
	
	@In
	private SigiloProcessoPermissaoManager sigiloProcessoPermissaoManager;
	
	public boolean usuarioPossuiPermissao(UsuarioLogin usuario, ProcessoEpa processo) {
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
	}
	
	public void gravarPermissoes(ProcessoEpa processo, List<SigiloProcessoPermissao> permissoes) throws DAOException {
		SigiloProcesso sigiloProcesso = sigiloProcessoManager.getSigiloProcessoAtivo(processo);
		if (sigiloProcesso != null) {
			sigiloProcessoPermissaoManager.gravarPermissoes(permissoes, sigiloProcesso);
		}
	}
}
