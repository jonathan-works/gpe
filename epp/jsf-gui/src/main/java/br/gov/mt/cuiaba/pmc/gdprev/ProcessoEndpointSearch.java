package br.gov.mt.cuiaba.pmc.gdprev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
public class ProcessoEndpointSearch extends PersistenceController {

    private static final String CODIGO_STATUS_ARQUIVADO = "arquivado";
    private static final String CODIGO_STATUS_DESARQUIVADO = "desarquivado";
    private static final String CODIGO_TIPO_PARTE_INTERESSADO = "interessadoServidor";

    public List<DocumentoBin> getListaDocumentoBinByNrProcesso(String numeroDoProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DocumentoBin> documentosQuery = cb.createQuery(DocumentoBin.class);
        Root<DocumentoBin> docBin = documentosQuery.from(DocumentoBin.class);
        Join<?, Pasta> pasta = docBin.join(DocumentoBin_.documentoList, JoinType.INNER)
            .join(Documento_.pasta, JoinType.INNER);
        documentosQuery.select(docBin);
        documentosQuery.groupBy(docBin);
        documentosQuery.where(
            cb.equal(pasta.join(Pasta_.processo, JoinType.INNER).get(Processo_.numeroProcesso), numeroDoProcesso)
        );
        List<DocumentoBin> documentos = getEntityManager().createQuery(documentosQuery).getResultList();
        return documentos;
    }

    public List<Processo> getListaProcesso(){
        return getListaProcesso(null);
    }

    public List<Processo> getListaProcesso(Date dataAlteracao){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<br.com.infox.epp.processo.entity.Processo> prc = cq.from(br.com.infox.epp.processo.entity.Processo.class);
        Join<?, NaturezaCategoriaFluxo> ncf = prc.join(Processo_.naturezaCategoriaFluxo, JoinType.LEFT);
        Join<?, Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);
        Join<?, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria);
        Join<?, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo);
        Expression<Integer> idProcesso = prc.get(Processo_.idProcesso);
        Expression<String> nrProcesso = prc.get(Processo_.numeroProcesso);

        Expression<String> nmNatureza = natureza.get(Natureza_.natureza);
        Path<String> nmFluxo = fluxo.get(Fluxo_.fluxo);
        Path<String> nmCategoria = categoria.get(Categoria_.categoria);

        Expression<Date> dtCriacao = prc.get(Processo_.dataInicio);
        //TODO: A data de encerramento deve ser igual à data de arquivamento quando houver, senão deve ser a data de fim
        Expression<Date> dtEncerramento = prc.get(Processo_.dataFim);
        cq.multiselect(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento);
        //TODO: Adicionar filtro para recuperar apenas processos que foram arquivados, ou desarquivados na data da alteração




        cq.groupBy(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento);
        List<Tuple> resultList = getEntityManager().createQuery(cq).getResultList();
        List<Processo> processos = new ArrayList<>();
        for (Tuple tuple : resultList) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Integer id = tuple.get(idProcesso);
            String numero = tuple.get(nrProcesso);
            String nomeNatureza = tuple.get(nmNatureza);
            String nomeCategoria = tuple.get(nmCategoria);
            String nomeFluxo = tuple.get(nmFluxo);
            String dataCriacao = Optional.ofNullable(tuple.get(dtCriacao)).map(sdf::format).orElse(null);
            String dataEncerramento = Optional.ofNullable(tuple.get(dtEncerramento)).map(sdf::format).orElse(null);
            List<Interessado> servidores = recuperarInteressados(id);
            String situacao = recuperarSituacaoProcesso(id);
            processos.add(new Processo(numero, nomeNatureza, nomeCategoria, nomeFluxo, situacao, servidores, dataCriacao, dataEncerramento));
        }

        return processos;
    }

    private String recuperarSituacaoProcesso(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Situacao> cq = cb.createQuery(Situacao.class);
        Root<MetadadoProcesso> mp = cq.from(MetadadoProcesso.class);
        Root<StatusProcesso> sp = cq.from(StatusProcesso.class);
        cq.where(
            cb.equal(mp.get(MetadadoProcesso_.metadadoType), cb.literal(EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType())),
            cb.equal(sp.get(StatusProcesso_.idStatusProcesso).as(String.class), mp.get(MetadadoProcesso_.valor)),
            cb.equal(mp.get(MetadadoProcesso_.processo).get(Processo_.idProcesso), idProcesso),
            sp.get(StatusProcesso_.nome).in(CODIGO_STATUS_ARQUIVADO, CODIGO_STATUS_DESARQUIVADO)
        );
        cq.select(cb.construct(Situacao.class, sp.get(StatusProcesso_.nome), sp.get(StatusProcesso_.descricao)));
        getEntityManager().createQuery(cq).getResultList().stream().findFirst().orElse(null);
        //TODO: Utilizar lógica que discrimina o estado de um processo como arquivado ou desarquivado.
        // Aguardando definição da Loglab
        return "ARQUIVADO"; // ou "DESARQUIVADO";
    }

    private List<Interessado> recuperarInteressados(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Interessado> cq = cb.createQuery(Interessado.class);
        Root<ParticipanteProcessoConsulta> part = cq.from(ParticipanteProcessoConsulta.class);
        Join<?, PessoaFisica> pessoaFisica = part.join(ParticipanteProcessoConsulta_.pessoaFisica, JoinType.INNER);
        Join<?, ParticipanteProcesso> partProcesso = part.join(ParticipanteProcessoConsulta_.participanteProcesso);
        Join<?, TipoParte> tipoParte = partProcesso.join(ParticipanteProcesso_.tipoParte);
        cq.select(cb.construct(Interessado.class, pessoaFisica.get(PessoaFisica_.cpf), cb.literal(""),
                partProcesso.get(ParticipanteProcesso_.nome)));
        cq.where(cb.equal(partProcesso.get(ParticipanteProcesso_.processo).get(Processo_.idProcesso), idProcesso),
                cb.equal(tipoParte.get(TipoParte_.identificador), CODIGO_TIPO_PARTE_INTERESSADO));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
