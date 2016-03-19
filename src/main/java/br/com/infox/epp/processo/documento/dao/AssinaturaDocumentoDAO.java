package br.com.infox.epp.processo.documento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery;
import br.com.infox.hibernate.util.HibernateUtil;

@Name(AssinaturaDocumentoDAO.NAME)
@Stateless
@AutoCreate
public class AssinaturaDocumentoDAO extends DAO<AssinaturaDocumento> {
    
    public static final String NAME = "assinaturaDocumentoDAO";
    private static final long serialVersionUID = 1L;
    
    public List<AssinaturaDocumento> listAssinaturaDocumentoByDocumento(Documento documento) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AssinaturaDocumentoQuery.PARAM_DOCUMENTO, documento);
        return getNamedResultList(AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_DOCUMENTO, hashMap);
    }

    //TODO colocar o atributo nomePerfil na entidade AssinaturaDocumento e remover isso daqui
    public String getNomePerfil(String nomeLocalizacao, String nomePapel) {
        Map<String, Object> params = new HashMap<>();
        String localizacaoQuery = "select o from Localizacao o where o.localizacao = :localizacao";
        params.put("localizacao", nomeLocalizacao);
        Localizacao idLocalizacao = getSingleResult(localizacaoQuery, params);
        
        params = new HashMap<>();
        String papelQuery = "select o from Papel o where o.nome = :papel";
        params.put("papel", nomePapel);
        Papel idPapel = getSingleResult(papelQuery, params);
        
        params = new HashMap<>();
        String perfilQuery = "select o.descricao from Perfil o where o.localizacao = :idLocalizacao and o.papel = :idPapel";
        params.put("idLocalizacao", idLocalizacao);
        params.put("idPapel", idPapel);
        return getSingleResult(perfilQuery, params);
    }

    public List<AssinaturaDocumento> listAssinaturaByDocumentoBin(DocumentoBin documentoBin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AssinaturaDocumento> cq = cb.createQuery(AssinaturaDocumento.class);
        Root<AssinaturaDocumento> from = cq.from(AssinaturaDocumento.class);
        cq.select(from);
        cq.where(cb.equal(from.get(AssinaturaDocumento_.documentoBin), documentoBin));
        
        TypedQuery<AssinaturaDocumento> query = getEntityManager().createQuery(cq);
        HibernateUtil.enableCache(query);
		return query.getResultList();
    }
}
