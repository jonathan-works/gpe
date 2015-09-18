package br.com.infox.epp.processo.documento.sigilo.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.dao.SigiloDocumentoDAO;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(SigiloDocumentoManager.NAME)
@AutoCreate
@Stateless
@Scope(ScopeType.EVENT)
public class SigiloDocumentoManager extends Manager<SigiloDocumentoDAO, SigiloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoManager";

    public SigiloDocumento getSigiloDocumentoAtivo(Documento documento) {
        return getDao().getSigiloDocumentoAtivo(documento);
    }
    
    public SigiloDocumento getSigiloDocumentoAtivoUsuarioLogin(Documento documento, UsuarioLogin login) {
    	return getDao().getSigiloDocumentoUsuarioLogin(documento, login);
    }

    public SigiloDocumento getSigiloDocumentoAtivo(Integer idDocumento) {
        return getDao().getSigiloDocumentoAtivo(idDocumento);
    }

    public boolean isSigiloso(Integer idDocumento) {
        return getDao().isSigiloso(idDocumento);
    }

    @Override
    public SigiloDocumento persist(SigiloDocumento o) throws DAOException {
        getDao().inativarSigilos(o.getDocumento());
        return super.persist(o);
    }

	public List<SigiloDocumento> getSigiloDocumentoAtivos(Localizacao localizacao, Processo processo) {
		return getDao().getSigiloDocumentoAtivosProcessoLocalizacao(localizacao, processo);
		
	}
}
