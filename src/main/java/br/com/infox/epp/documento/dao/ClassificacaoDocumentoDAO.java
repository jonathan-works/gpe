package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.ASSINATURA_OBRIGATORIA;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CLASSIFICACAO_DOCUMENTO_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.CODIGO_DOCUMENTO_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PARAM_DESCRICAO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.PARAM_PROCESSO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel_;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(ClassificacaoDocumentoDAO.NAME)
public class ClassificacaoDocumentoDAO extends DAO<ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoDAO";

    @Override
    public List<ClassificacaoDocumento> findAll() {
        String hql = "select o from ClassificacaoDocumento o order by o.descricao";
        return getResultList(hql, null);
    }

    public List<ClassificacaoDocumento> getClassificacaoDocumentoListByProcesso(Processo processo) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(PARAM_PROCESSO, processo);
        return getNamedResultList(LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO, params);
    }

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, Papel papel) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryUseableClassificacaoDocumento(isModelo, papel);
        return getEntityManager().createQuery(query).getResultList();
    }

    protected CriteriaQuery<ClassificacaoDocumento> createQueryUseableClassificacaoDocumento(boolean isModelo,
            Papel papel) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryClassificacoesDocumento();
        addRedatorFilter(query, papel);
        addTipoDocumentoEnumFilter(query, isModelo ? TipoDocumentoEnum.P : TipoDocumentoEnum.D);
        return query;
    }

    public boolean isAssinaturaObrigatoria(ClassificacaoDocumento classificacaoDocumento, Papel papel) {
        HashMap<String, Object> params = new HashMap<String, Object>(0);
        params.put(CLASSIFICACAO_DOCUMENTO_PARAM, classificacaoDocumento);
        params.put(PAPEL_PARAM, papel);
        ClassificacaoDocumentoPapel tpdp = getNamedSingleResult(ASSINATURA_OBRIGATORIA, params);
        if (tpdp != null) {
            return tpdp.getTipoAssinatura() != TipoAssinaturaEnum.F;
        }
        return false;
    }

    public ClassificacaoDocumento findByCodigo(String codigo) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CODIGO_DOCUMENTO_PARAM, codigo);
        return getNamedSingleResult(FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO, parameters);
    }

    public ClassificacaoDocumento findByDescricao(String descricao) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(PARAM_DESCRICAO, descricao);
        return getNamedSingleResult(FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO, params);
    }

    public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao, boolean isModelo, Papel papel) {
		return getUseableClassificacaoDocumento(isModelo, papel);
	}

    @SuppressWarnings("unchecked")
    protected void addTipoDocumentoEnumFilter(AbstractQuery<?> query, TipoDocumentoEnum tipoDocumento) {
        Root<ClassificacaoDocumento> from = (Root<ClassificacaoDocumento>) query.getRoots().iterator().next();
        Predicate predicate = query.getRestriction();

        List<TipoDocumentoEnum> tiposDocumento = new ArrayList<>();
        tiposDocumento.add(TipoDocumentoEnum.T);
        switch (tipoDocumento) {
        case T:
            tiposDocumento.add(TipoDocumentoEnum.P);
            tiposDocumento.add(TipoDocumentoEnum.D);
            break;

        default:
            tiposDocumento.add(tipoDocumento);
            break;
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        predicate = cb.and(predicate, from.get(ClassificacaoDocumento_.inTipoDocumento).in(tiposDocumento));
        query.where(predicate);
    }

    @SuppressWarnings("unchecked")
    protected void addRedatorFilter(AbstractQuery<?> query, Papel papel) {
        Root<ClassificacaoDocumento> from = (Root<ClassificacaoDocumento>) query.getRoots().iterator().next();
        Join<ClassificacaoDocumento, ClassificacaoDocumentoPapel> join = from
                .join(ClassificacaoDocumento_.classificacaoDocumentoPapelList);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        Predicate predicate = query.getRestriction();
        predicate = cb.and(predicate, cb.equal(join.get(ClassificacaoDocumentoPapel_.papel), papel));
        predicate = cb.and(predicate, cb.isTrue(join.get(ClassificacaoDocumentoPapel_.podeRedigir)));
        query.where(predicate);
    }

    protected CriteriaQuery<ClassificacaoDocumento> createQueryClassificacoesDocumento() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ClassificacaoDocumento> query = cb.createQuery(ClassificacaoDocumento.class);
        Root<ClassificacaoDocumento> from = query.from(ClassificacaoDocumento.class);

        Predicate predicate = cb.and(cb.isFalse(from.get(ClassificacaoDocumento_.sistema)));
        predicate = cb.and(predicate, cb.isTrue(from.get(ClassificacaoDocumento_.ativo)));

        query.where(predicate);
        query.orderBy(cb.asc(from.get(ClassificacaoDocumento_.descricao)));
        return query;
    }

    public List<ClassificacaoDocumento> getClassificacoesDocumentoCruds(TipoDocumentoEnum tipoDocumento) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryClassificacoesDocumento();
        addTipoDocumentoEnumFilter(query, tipoDocumento);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<ClassificacaoDocumento> getClassificacoesDocumentoAnexarDocumento(TipoDocumentoEnum tipoDocumento) {
        CriteriaQuery<ClassificacaoDocumento> query = createQueryClassificacoesDocumento();
        addTipoDocumentoEnumFilter(query, tipoDocumento);
        addRedatorFilter(query, Authenticator.getPapelAtual());
        return getEntityManager().createQuery(query).getResultList();
    }
}
