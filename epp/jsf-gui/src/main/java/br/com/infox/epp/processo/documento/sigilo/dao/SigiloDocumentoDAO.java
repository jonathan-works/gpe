package br.com.infox.epp.processo.documento.sigilo.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento_;
import br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@AutoCreate
@Name(SigiloDocumentoDAO.NAME)
public class SigiloDocumentoDAO extends DAO<SigiloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoDAO";

    public SigiloDocumento getSigiloDocumentoAtivo(Documento documento) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoQuery.QUERY_PARAM_DOCUMENTO, documento);
        return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO, params);
    }
    
    public SigiloDocumento getSigiloDocumentoUsuarioLogin(Documento documento, UsuarioLogin usuarioLogin) {
    	Map<String, Object> params = new HashMap<>();
    	params.put(SigiloDocumentoQuery.QUERY_PARAM_DOCUMENTO, documento);
    	params.put(SigiloDocumentoQuery.QUERY_PARAM_USUARIO_LOGIN, documento);
    	return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_USUARIO_LOGIN_ATIVO, params);
    }
    
    
    public List<SigiloDocumento> getSigiloDocumentoAtivosProcessoLocalizacao(Localizacao localizacao, Processo processo){
    	Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoQuery.QUERY_PARAM_LOCALIZACAO,localizacao);
        params.put(SigiloDocumentoQuery.QUERY_PARAM_PROCESSO,processo);
        return getNamedResultList(SigiloDocumentoQuery.NAMED_QUERY_DOCUMENTOS_ATIVO_PESSOA, params);
    }

    public SigiloDocumento getSigiloDocumentoAtivo(Integer idDocumento) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoQuery.QUERY_PARAM_ID_DOCUMENTO, idDocumento);
        return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO, params);
    }

    public boolean isSigiloso(Integer idDocumento) {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoQuery.QUERY_PARAM_ID_DOCUMENTO, idDocumento);
        return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO, params) != null;
    }
    
    public List<Integer> getSigilosos(List<Integer> idsDocumentos) {
    	if(idsDocumentos.isEmpty()) {
    		return Collections.emptyList();
    	}
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
    	Root<SigiloDocumento> sigilo = cq.from(SigiloDocumento.class);
    	Path<Documento> documento = sigilo.join(SigiloDocumento_.documento);
    	cq.select(documento.get(Documento_.id));
    	cq.distinct(true);
    	cq.where(documento.get(Documento_.id).in(idsDocumentos), cb.isTrue(sigilo.get(SigiloDocumento_.ativo)));
    	
    	return getEntityManager().createQuery(cq).getResultList();
    }
    

    public void inativarSigilos(Documento documento) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(SigiloDocumentoQuery.QUERY_PARAM_DOCUMENTO, documento);
        executeNamedQueryUpdate(SigiloDocumentoQuery.NAMED_QUERY_INATIVAR_SIGILOS, params);
    }
}
