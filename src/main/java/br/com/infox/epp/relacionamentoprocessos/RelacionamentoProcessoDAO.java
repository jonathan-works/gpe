package br.com.infox.epp.relacionamentoprocessos;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.entity.PastaRestricao_;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento_;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoExterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno_;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso_;
import br.com.infox.epp.processo.entity.Relacionamento_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;

@Stateless
@AutoCreate
@Name(RelacionamentoProcessoDAO.NAME)
public class RelacionamentoProcessoDAO extends DAO<RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoDAO";
    
    public boolean existeRelacionamento(RelacionamentoProcessoInterno rel1, RelacionamentoProcesso rel2) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("processo1", rel1.getProcesso());
        
        String query = "select r.idRelacionamento from RelacionamentoProcessoInterno rp inner join rp.relacionamento r, "
        		+ rel2.getClass().getSimpleName() + " rp2 inner join rp2.relacionamento r2 "
        		+ "where r.idRelacionamento = r2.idRelacionamento "        		
        		+ "and rp.processo=:processo1 ";
        		if(rel2 instanceof RelacionamentoProcessoInterno) {
        			query += "and rp2.processo=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoInterno) rel2).getProcesso());
        		}
        		else if(rel2 instanceof RelacionamentoProcessoExterno)
        		{
        			query += "and rp2.numeroProcesso=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoExterno) rel2).getNumeroProcesso());        			
        		}
        		else
        		{
        			throw new UnsupportedOperationException();
        		}
        		query += "group by r having count(r)>0";
        final Integer result = getSingleResult(query, parameters);
        return result != null;
    }
    
    @SuppressWarnings("unchecked")
	public <T extends RelacionamentoProcesso> List<T> getListProcessosRelacionados(Processo processo, Class<T> classe) {
    	EntityManager entityManager = getEntityManager();
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(classe == null ? RelacionamentoProcesso.class : classe);
    	Root<RelacionamentoProcessoInterno> root = query.from(RelacionamentoProcessoInterno.class);
    	Join<RelacionamentoProcessoInterno, Relacionamento> relacionamento = root.join(RelacionamentoProcesso_.relacionamento);
		Join<Relacionamento, T> processosRelacionados = (Join<Relacionamento, T>)relacionamento.join(Relacionamento_.relacionamentosProcessos);
    	
    	query.where(
				cb.equal(root.get(RelacionamentoProcessoInterno_.processo), processo),
				cb.notEqual(root.get(RelacionamentoProcesso_.idRelacionamentoProcesso), processosRelacionados.get(RelacionamentoProcesso_.idRelacionamentoProcesso)),
				cb.equal(processosRelacionados.type(), classe)
    	);
    	
    	query.select(processosRelacionados);
    	return entityManager.createQuery(query).getResultList();
    }
    
    public List<RelacionamentoProcessoInterno> getListProcessosEletronicosRelacionados(Processo processo) {
    	return getListProcessosRelacionados(processo, RelacionamentoProcessoInterno.class);    	
    }
    
    public RelacionamentoProcessoInterno getRelacionamentoEletronico(Processo processo, Processo processoRelacionado) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RelacionamentoProcessoInterno> query = cb.createQuery(RelacionamentoProcessoInterno.class);
        Root<RelacionamentoProcessoInterno> root = query.from(RelacionamentoProcessoInterno.class);
        Root<RelacionamentoProcessoInterno> processosRelacionados = query.from(RelacionamentoProcessoInterno.class);
        query.select(processosRelacionados);
        
        query.where(
                cb.equal(processosRelacionados.get(RelacionamentoProcessoInterno_.relacionamento), root.get(RelacionamentoProcessoInterno_.relacionamento)),
                cb.equal(root.get(RelacionamentoProcessoInterno_.processo), processo),
                cb.equal(processosRelacionados.get(RelacionamentoProcessoInterno_.processo), processoRelacionado)
        );
        
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<RelacionamentoProcessoInterno> getProcessosEletronicosPublicosRelacionadosComDocumentosPublicos(Processo processo) {
        EntityManager entityManager = getEntityManager();
        
        // Processos Internos Relacionados
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RelacionamentoProcessoInterno> query = cb.createQuery(RelacionamentoProcessoInterno.class);
        Root<RelacionamentoProcessoInterno> root = query.from(RelacionamentoProcessoInterno.class);
        Root<RelacionamentoProcessoInterno> processosRelacionados = query.from(RelacionamentoProcessoInterno.class);
        query.select(processosRelacionados);
        Join<RelacionamentoProcessoInterno, Processo> processoRelacionado = processosRelacionados.join(RelacionamentoProcessoInterno_.processo, JoinType.INNER);
        
        // Sigilo do processo relacionado
        Subquery<Integer> subquerySigiloProcesso = query.subquery(Integer.class);
        subquerySigiloProcesso.select(cb.literal(1));
        Root<SigiloProcesso> sigiloProcesso = subquerySigiloProcesso.from(SigiloProcesso.class);
        subquerySigiloProcesso.where(
                cb.equal(sigiloProcesso.get(SigiloProcesso_.processo), processoRelacionado),
                cb.isTrue(sigiloProcesso.get(SigiloProcesso_.ativo)),
                cb.isTrue(sigiloProcesso.get(SigiloProcesso_.sigiloso))
        );
        
        // Subquery documentos públicos
        Subquery<Integer> subqueryDocumentos = query.subquery(Integer.class);
        Root<Documento> documento = subqueryDocumentos.from(Documento.class);
        Join<Documento, Pasta> pasta = documento.join(Documento_.pasta, JoinType.INNER);
        Join<Documento, DocumentoBin> documentoBin = documento.join(Documento_.documentoBin, JoinType.INNER);
        subqueryDocumentos.select(cb.literal(1));

        // Subquery pastas públicas
        Subquery<Integer> subqueryPastasPublicas = query.subquery(Integer.class);
        Root<PastaRestricao> restricao = subqueryPastasPublicas.from(PastaRestricao.class);
        subqueryPastasPublicas.where(cb.equal(restricao.get(PastaRestricao_.pasta), pasta), 
                cb.equal(restricao.get(PastaRestricao_.tipoPastaRestricao), PastaRestricaoEnum.D),
                cb.isTrue(restricao.get(PastaRestricao_.read)));
        subqueryPastasPublicas.select(cb.literal(1));
        
        // Documentos não sigilosos das pastas públicas do processo relacionado
        Subquery<Integer> subquerySigiloDocumento = subqueryDocumentos.subquery(Integer.class);
        subquerySigiloDocumento.select(cb.literal(1));
        Root<SigiloDocumento> sigiloDocumento = subquerySigiloDocumento.from(SigiloDocumento.class);
        subquerySigiloDocumento.where(cb.equal(sigiloDocumento.get(SigiloDocumento_.documento), documento), cb.isTrue(sigiloDocumento.get(SigiloDocumento_.ativo)));
        
        // Documentos das pastas públicas que não são de comunicação, são binários ou já estão com ciência
        Subquery<Integer> subqueryComunicacao = subqueryDocumentos.subquery(Integer.class);
        subqueryComunicacao.select(cb.literal(1));
        Root<DestinatarioModeloComunicacao> destinatario = subqueryComunicacao.from(DestinatarioModeloComunicacao.class);
        subqueryComunicacao.where(cb.equal(destinatario.get(DestinatarioModeloComunicacao_.documentoComunicacao), documento));
        
        Subquery<Integer> subqueryDestinatarioCiencia = subqueryDocumentos.subquery(Integer.class);
        subqueryDestinatarioCiencia.select(cb.literal(1));
        Root<DestinatarioModeloComunicacao> destinatarioCiencia = subqueryDestinatarioCiencia.from(DestinatarioModeloComunicacao.class);
        Join<Processo, MetadadoProcesso> metadados = destinatarioCiencia.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER)
                .join(Processo_.metadadoProcessoList, JoinType.INNER);
        subqueryDestinatarioCiencia.where(
                cb.equal(destinatarioCiencia.get(DestinatarioModeloComunicacao_.documentoComunicacao), documento),
                cb.equal(metadados.get(MetadadoProcesso_.metadadoType), ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType())
        );
        
        // Documentos suficientemente assinados, das pastas públicas, que são binários ou não são de comunicação ou o destinatário
        // correspondente já deu ciência
        subqueryDocumentos.where(cb.equal(pasta.get(Pasta_.processo), processoRelacionado), cb.exists(subquerySigiloDocumento).not(),
                cb.exists(subqueryPastasPublicas),
                cb.isTrue(documentoBin.get(DocumentoBin_.suficientementeAssinado)),
                cb.or(
                    cb.isNotNull(documentoBin.get(DocumentoBin_.extensao)),
                    cb.exists(subqueryComunicacao).not(),
                    cb.exists(subqueryDestinatarioCiencia)
                )
        );
        
        query.where(
                cb.equal(processosRelacionados.get(RelacionamentoProcessoInterno_.relacionamento), root.get(RelacionamentoProcessoInterno_.relacionamento)),
                cb.equal(root.get(RelacionamentoProcessoInterno_.processo), processo),
                cb.notEqual(root, processosRelacionados),
                cb.exists(subquerySigiloProcesso).not(),
                cb.exists(subqueryDocumentos)
        );
        
        return entityManager.createQuery(query).getResultList();
    }
}
