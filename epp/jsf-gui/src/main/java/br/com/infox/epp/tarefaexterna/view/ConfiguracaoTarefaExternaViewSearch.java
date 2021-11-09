package br.com.infox.epp.tarefaexterna.view;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.tarefaexterna.ClassificacaoDocumentoUploadTarefaExterna;
import br.com.infox.epp.tarefaexterna.ClassificacaoDocumentoUploadTarefaExterna_;
import br.com.infox.epp.tarefaexterna.PastaUploadTarefaExterna;
import br.com.infox.epp.tarefaexterna.PastaUploadTarefaExterna_;
import br.com.infox.ibpm.sinal.Signal;
import br.com.infox.ibpm.sinal.Signal_;

@Stateless
public class ConfiguracaoTarefaExternaViewSearch {

    public List<PastaVO> getPastas() {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<PastaVO> query = cb.createQuery(PastaVO.class);
        Root<PastaUploadTarefaExterna> pastaUploadTarefaExterna = query.from(PastaUploadTarefaExterna.class);

        query.select(
            cb.construct(PastaVO.class,
                pastaUploadTarefaExterna.get(PastaUploadTarefaExterna_.id),
                pastaUploadTarefaExterna.get(PastaUploadTarefaExterna_.codigo),
                pastaUploadTarefaExterna.get(PastaUploadTarefaExterna_.nome)
            )
        );

        query.orderBy(cb.asc(pastaUploadTarefaExterna.get(PastaUploadTarefaExterna_.nome)));

        return em.createQuery(query).getResultList();
    }

    public List<SelectItem> getModelos() {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<SelectItem> query = cb.createQuery(SelectItem.class);
        Root<ModeloDocumento> modeloDocumento = query.from(ModeloDocumento.class);

        query.select(
            cb.construct(SelectItem.class,
                modeloDocumento.get(ModeloDocumento_.codigo),
                modeloDocumento.get(ModeloDocumento_.tituloModeloDocumento)
            )
        );

        query.where(
            cb.isTrue(modeloDocumento.get(ModeloDocumento_.ativo))
        );

        query.orderBy(cb.asc(modeloDocumento.get(ModeloDocumento_.tituloModeloDocumento)));

        return em.createQuery(query).getResultList();
    }

    public List<SelectItem> getSignais() {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<SelectItem> query = cb.createQuery(SelectItem.class);
        Root<Signal> signal = query.from(Signal.class);

        query.select(
            cb.construct(SelectItem.class,
                signal.get(Signal_.codigo),
                signal.get(Signal_.nome)
            )
        );

        query.where(
            cb.isTrue(signal.get(Signal_.ativo))
        );

        query.orderBy(cb.asc(signal.get(Signal_.nome)));

        return em.createQuery(query).getResultList();
    }

    public List<ClassificacaoDocumentoVO> getClassificacoesDisponiveis() {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ClassificacaoDocumentoVO> query = cb.createQuery(ClassificacaoDocumentoVO.class);
        Root<ClassificacaoDocumento> classificacaoDocumento = query.from(ClassificacaoDocumento.class);

        query.select(
            cb.construct(ClassificacaoDocumentoVO.class,
                classificacaoDocumento.get(ClassificacaoDocumento_.id),
                classificacaoDocumento.get(ClassificacaoDocumento_.codigoDocumento),
                classificacaoDocumento.get(ClassificacaoDocumento_.descricao)
            )
        );

        query.orderBy(cb.asc(classificacaoDocumento.get(ClassificacaoDocumento_.descricao)));

        Subquery<Integer> sqClassificacaoDocumentoUploadTarefaExterna = query.subquery(Integer.class);
        sqClassificacaoDocumentoUploadTarefaExterna.select(cb.literal(1));
        Root<ClassificacaoDocumentoUploadTarefaExterna> classificacaoDocumentoUploadTarefaExterna = sqClassificacaoDocumentoUploadTarefaExterna.from(ClassificacaoDocumentoUploadTarefaExterna.class);
        sqClassificacaoDocumentoUploadTarefaExterna.where(
            cb.equal(classificacaoDocumentoUploadTarefaExterna, classificacaoDocumento)
        );

        query.where(cb.not(cb.exists(sqClassificacaoDocumentoUploadTarefaExterna)));

        return em.createQuery(query).getResultList();
    }

    public List<ClassificacaoDocumentoVO> getClassificacoes() {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<ClassificacaoDocumentoVO> query = cb.createQuery(ClassificacaoDocumentoVO.class);
        Root<ClassificacaoDocumentoUploadTarefaExterna> classificacaoDocumentoUploadTarefaExterna = query.from(ClassificacaoDocumentoUploadTarefaExterna.class);
        Join<ClassificacaoDocumentoUploadTarefaExterna, ClassificacaoDocumento> classificacaoDocumento = classificacaoDocumentoUploadTarefaExterna.join(ClassificacaoDocumentoUploadTarefaExterna_.classificacaoDocumento);

        query.select(
            cb.construct(ClassificacaoDocumentoVO.class,
                classificacaoDocumento.get(ClassificacaoDocumento_.id),
                classificacaoDocumento.get(ClassificacaoDocumento_.codigoDocumento),
                classificacaoDocumento.get(ClassificacaoDocumento_.descricao)
            )
        );

        query.where(
            classificacaoDocumento.get(ClassificacaoDocumento_.inTipoDocumento).in(TipoDocumentoEnum.T, TipoDocumentoEnum.D),
            cb.isTrue(classificacaoDocumento.get(ClassificacaoDocumento_.ativo))
        );

        query.orderBy(cb.asc(classificacaoDocumento.get(ClassificacaoDocumento_.descricao)));

        return em.createQuery(query).getResultList();
    }


}