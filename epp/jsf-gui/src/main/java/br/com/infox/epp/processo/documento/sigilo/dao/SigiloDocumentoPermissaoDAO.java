package br.com.infox.epp.processo.documento.sigilo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumentoPermissao;
import br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@AutoCreate
@Name(SigiloDocumentoPermissaoDAO.NAME)
public class SigiloDocumentoPermissaoDAO extends DAO<SigiloDocumentoPermissao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoPermissaoDAO";

    public boolean possuiPermissao(SigiloDocumento sigiloDocumento,
            UsuarioLogin usuario) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_USUARIO, usuario);
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_SIGILO_DOCUMENTO, sigiloDocumento);
        return getNamedSingleResult(SigiloDocumentoPermissaoQuery.NAMED_QUERY_USUARIO_POSSUI_PERMISSAO, params) != null;
    }

    public boolean possuiPermissao(Set<Integer> idsDocumentosSelecionados,
            UsuarioLogin usuario) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_USUARIO, usuario);
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_IDS_DOCUMENTO, idsDocumentosSelecionados);
        return getNamedSingleResult(SigiloDocumentoPermissaoQuery.NAMED_QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS, params) != null;
    }

    public void inativarPermissoes(SigiloDocumento sigiloDocumento) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_SIGILO_DOCUMENTO, sigiloDocumento);
        executeNamedQueryUpdate(SigiloDocumentoPermissaoQuery.NAMED_QUERY_INATIVAR_PERMISSOES, params);
    }

    public List<Documento> getDocumentosPermitidos(Processo processo, UsuarioLogin usuario) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_PROCESSO, processo);
        params.put(SigiloDocumentoPermissaoQuery.QUERY_PARAM_USUARIO, usuario);
        return getNamedResultList(SigiloDocumentoPermissaoQuery.NAMED_QUERY_DOCUMENTOS_PERMITIDOS, params);
    }
}
