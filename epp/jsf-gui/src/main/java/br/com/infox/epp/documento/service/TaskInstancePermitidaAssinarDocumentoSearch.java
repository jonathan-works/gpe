package br.com.infox.epp.documento.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskInstancePermitidaAssinarDocumentoSearch {

    public List<DocumentoVO> getListaDocumentosParaAssinar(Long idTaskInstance){
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<DocumentoVO> query = cb.createQuery(DocumentoVO.class);
        Root<TaskInstancePermitidaAssinarDocumento> taskInstancePermitidaAssinarDocumento = query.from(TaskInstancePermitidaAssinarDocumento.class);
        Join<TaskInstancePermitidaAssinarDocumento, Documento> documento = taskInstancePermitidaAssinarDocumento.join(TaskInstancePermitidaAssinarDocumento_.documento);
        Join<Documento, DocumentoBin> documentoBin = documento.join(Documento_.documentoBin);
        Join<Documento, UsuarioLogin> usuarioInclusao = documento.join(Documento_.usuarioInclusao);
        Join<Documento, ClassificacaoDocumento> classificacaoDocumento = documento.join(Documento_.classificacaoDocumento);

        query.where(cb.equal(taskInstancePermitidaAssinarDocumento.get(TaskInstancePermitidaAssinarDocumento_.idTaskInstance), idTaskInstance));

        query.select(cb.construct(DocumentoVO.class, documento.get(Documento_.id)
            , documentoBin.get(DocumentoBin_.id)
            , documento.get(Documento_.numeroDocumento)
            , classificacaoDocumento.get(ClassificacaoDocumento_.descricao)
            , documento.get(Documento_.descricao)
            , usuarioInclusao.get(UsuarioLogin_.nomeUsuario)
            , documento.get(Documento_.dataInclusao)
        ));

        return em.createQuery(query).getResultList();
    }

}
