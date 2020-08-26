package br.gov.mt.cuiaba.pmc.gdprev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.loglab.model.Servidor;
import br.com.infox.epp.loglab.model.Servidor_;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.ProcessoJbpm;
import br.com.infox.epp.processo.entity.ProcessoJbpm_;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoEndpointSearch extends PersistenceController {

    //TODO: verificar com a Loglab quais os códigos
    private static final Object[] CODIGO_STATUS_ARQUIVADO = {"Processo Arquivado"};
    private static final Object[] CODIGO_STATUS_DESARQUIVADO = {"Processo Desarquivado"};
	private static final Object[] CODIGOS_FLUXOS_CONSULTA = { "P01-SME", "P01-SMS", "P02-SME", "P02-SMGE", "P02-SMS",
			"P03-SME", "P03-SMS", "P04-SME", "P04-SMS", "P05-SME", "P05-SMS", "P06-SME", "P06-SMS", "P07-SME",
			"P07-SMGE", "P07-SMS", "P08-SME", "P08-SMS", "P09-SME", "P09-SMS", "P10-SME", "P10-SMGE", "P10-SMS",
			"P11-SME", "P11-SMS", "P12-SME", "P12-SMGE", "P12-SMS", "P13-SME", "P13-SMGE", "P13-SMS", "P14-SME",
			"P14-SMGE", "P14-SMS", "P15-SME", "P15-SMGE", "P15-SMS", "P16-SME", "P16-SMGE", "P16-SMS", "P17-SME",
			"P17-SMGE", "P17-SMS", "P18-SMGE", "P18-SMS", "P19-SME", "P19-SMS", "P20-SME", "P20-SMS", "P21-SME",
			"P21-SMS", "P22-SME", "P22-SMGE", "P22-SMS", "P23-SME", "P23-SMGE", "P23-SMS", "P24-SME", "P24-SMGE",
			"P24-SMS", "P25-SME", "P25-SMGE", "P25-SMS", "P26-SME", "P26-SMS", "P27-SME", "P27-SMS", "P28-SME",
			"P28-SMS", "P29-SME", "P29-SMGE", "P29-SMS", "P30-SME", "P30-SMGE", "P30-SMS", "P31-SME", "P31-SMGE",
			"P31-SMS", "P32-SME", "P32-SMGE", "P32-SMS", "P33-SMS", "P34-SMGE", "P34-SMS", "P35-SMGE", "P35-SMS",
			"P36-SMGE", "P36-SMS", "P37-SMGE", "P37-SMS", "P38-SMGE", "P38-SMS", "P39-SMGE", "P39-SMS", "P40-SMGE",
			"P40-SMS", "P41-SMGE", "P41-SMS", "P42-SMGE", "P42-SMS", "P43-SMGE", "P43-SMS", "P44-SMGE", "P44-SMS",
			"P45-SMS", "P46-SMGE", "P46-SMS", "P47-SMS", "P48-SMGE", "P48-SMS", "P49-SMS", "P50-SMGE", "P50-SMS",
			"P51-SMGE", "P51-SMS", "P52-SMGE", "P52-SMS", "P53-SMGE", "P53-SMS", "P54-SMGE", "P54-SMS", "P55-SMGE",
			"P55-SMS", "P56-SMGE", "P56-SMS", "P57-SMGE", "P57-SMS", "P58-SMGE", "P58-SMS", "P59-SMGE", "P59-SMS",
			"P60-SMGE", "P60-SMS", "P61-SMGE", "P61-SMS", "P62-SMGE", "P62-SMS", "P63-SMGE", "P63-SMS", "P64-SMGE",
			"P64-SMS", "P65-SMS", "P66-SMGE", "P66-SMS", "P67-SMS", "P68-SMGE", "P68-SMS", "P69-SMS", "P70-SMGE",
			"P70-SMS", "P71-SMGE", "P71-SMS", "P74-SMGE", "P75-SMGE", "P76-SMGE", "P77-SMGE", "P78-SMGE", "P79-SMGE",
			"P80-SMGE", "P81-SMGE", "P82-SMGE", "P83-SMGE", "P84-SMGE", "P85-SMGE", "P88-SMGE", "P89-SMGE", "P90-SMGE",
			"P91-SMGE", "P92-SMGE", "P93-SMGE", "P94-SMGE" }; 
    private static final Object[] CODIGO_TIPO_PARTE_INTERESSADO = {"interessadoServidor"};

    public List<DocumentoBin> getListaDocumentoBinByNrProcesso(String numeroDoProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DocumentoBin> documentosQuery = cb.createQuery(DocumentoBin.class);
        Root<DocumentoBin> docBin = documentosQuery.from(DocumentoBin.class);
        ListJoin<?, br.com.infox.epp.processo.documento.entity.Documento> doc = docBin.join(DocumentoBin_.documentoList, JoinType.INNER);
        Join<?, Pasta> pasta = doc.join(Documento_.pasta, JoinType.INNER);
        documentosQuery.select(docBin);
        documentosQuery.groupBy(docBin);
        documentosQuery.where(
            cb.equal(pasta.join(Pasta_.processo, JoinType.INNER).get(Processo_.numeroProcesso), numeroDoProcesso),
            cb.isFalse(doc.get(Documento_.excluido))
        );
        List<DocumentoBin> documentos = getEntityManager().createQuery(documentosQuery).getResultList();
        return documentos;
    }

    public List<Processo> getListaProcesso(Date dataAlteracao){
        Set<Processo> processos = new HashSet<>();
        processos.addAll(getListaProcessoByDataAlteracao(dataAlteracao));
        processos.addAll(getListaProcessoByDataEncerramento(dataAlteracao));
        return new ArrayList<>(processos);
    }

    private List<Processo> getListaProcessoByDataAlteracao(Date dataAlteracao){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<br.com.infox.epp.processo.entity.Processo> prc = cq.from(br.com.infox.epp.processo.entity.Processo.class);
        Join<?, NaturezaCategoriaFluxo> ncf = prc.join(Processo_.naturezaCategoriaFluxo, JoinType.LEFT);
        Join<?, Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);
        Join<?, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria);
        Join<?, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo);
        ListJoin<?, HistoricoMetadadoProcesso> hist = prc.join(Processo_.historicoMetadadoProcessoList, JoinType.LEFT);
        Root<StatusProcesso> sp = cq.from(StatusProcesso.class);
        Expression<Integer> idProcesso = prc.get(Processo_.idProcesso);
        Expression<String> nrProcesso = prc.get(Processo_.numeroProcesso);

        Expression<String> nmNatureza = natureza.get(Natureza_.natureza);
        Path<String> nmFluxo = fluxo.get(Fluxo_.fluxo);
        Path<String> nmCategoria = categoria.get(Categoria_.categoria);

        Expression<Date> dtCriacao = prc.get(Processo_.dataInicio);
        Expression<Date> dtEncerramento = hist.get(HistoricoMetadadoProcesso_.dataRegistro);
        Expression<String> situacaoExp = hist.get(HistoricoMetadadoProcesso_.descricao);
        cq.multiselect(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento, situacaoExp).distinct(true);

        Predicate historico = pradicateHistoricoMetadadoProcesso(dataAlteracao, cb, hist, sp);

        cq.where(historico, fluxo.get(Fluxo_.codFluxo).in(CODIGOS_FLUXOS_CONSULTA));

        cq.groupBy(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento, situacaoExp);
        cq.orderBy(cb.desc(dtEncerramento));
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
            String situacao = Optional.ofNullable(tuple.get(situacaoExp)).orElse("");
            if (Stream.of(CODIGO_STATUS_ARQUIVADO).anyMatch(java.util.function.Predicate.isEqual(situacao))) {
            	situacao = "ARQUIVADO";
            } else if (Stream.of(CODIGO_STATUS_DESARQUIVADO).anyMatch(java.util.function.Predicate.isEqual(situacao))) {
            	situacao = "DESARQUIVADO";
            }
            processos.add(new Processo(numero, nomeNatureza, nomeCategoria, nomeFluxo, situacao, servidores, dataCriacao, dataEncerramento));
        }

        return processos;
    }

    private List<Processo> getListaProcessoByDataEncerramento(Date dataAlteracao){
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
        Expression<Date> dtEncerramento = prc.get(Processo_.dataFim);
        cq.multiselect(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento).distinct(true);

        cq.where(cb.between(prc.get(Processo_.dataFim), DateUtil.getBeginningOfDay(dataAlteracao),
                DateUtil.getEndOfDay(dataAlteracao)));

        cq.groupBy(idProcesso,nrProcesso, nmNatureza, nmCategoria, nmFluxo,dtCriacao,dtEncerramento);
        cq.orderBy(cb.desc(dtEncerramento));
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
            String situacao = "Encerrado";
            processos.add(new Processo(numero, nomeNatureza, nomeCategoria, nomeFluxo, situacao, servidores, dataCriacao, dataEncerramento));
        }

        return processos;
    }

    private Predicate pradicateHistoricoMetadadoProcesso(Date dataAlteracao, CriteriaBuilder cb,
            ListJoin<?, HistoricoMetadadoProcesso> hist, Root<StatusProcesso> sp) {
    	;
        Predicate historico = cb.and(
                cb.equal(hist.get(HistoricoMetadadoProcesso_.nome),
                        cb.literal(EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType())),
                cb.equal(sp.get(StatusProcesso_.idStatusProcesso).as(String.class),
                        hist.get(HistoricoMetadadoProcesso_.valor)),
                
                sp.get(StatusProcesso_.nome).in(Stream.of(
                	Stream.of(CODIGO_STATUS_ARQUIVADO),
                    Stream.of(CODIGO_STATUS_DESARQUIVADO)
                ).flatMap(Function.identity()).toArray(Object[]::new)),
                hist.get(HistoricoMetadadoProcesso_.acao).in("Insert", "Update"),
                cb.between(hist.get(HistoricoMetadadoProcesso_.dataRegistro), DateUtil.getBeginningOfDay(dataAlteracao),
                        DateUtil.getEndOfDay(dataAlteracao)));
        return historico;
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
                tipoParte.get(TipoParte_.identificador).in(CODIGO_TIPO_PARTE_INTERESSADO));
        List<Interessado> resultList = getEntityManager().createQuery(cq).getResultList();
        for (Interessado interessado : resultList) {
            interessado.setMatricula(getMatriculaServidorByCpf(interessado.getCpf()));
        }
        return resultList;
    }

    private String getMatriculaServidorByCpf(String cpf) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Servidor> servidor = cq.from(Servidor.class);
        cq.select(cb.coalesce(servidor.get(Servidor_.matricula), cb.literal("")));
        cq.where(cb.equal(servidor.get(Servidor_.cpf), cpf));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        }catch (NoResultException e) {
            return "";
        }
    }

    public List<MovimentacaoGroup> getListaMovimentacaoGroupByIdProcesso(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Movimentacao> cq = cb.createQuery(Movimentacao.class);

        Root<ProcessoJbpm> prcJbpm = cq.from(ProcessoJbpm.class);
        Root<TaskInstance> ti = cq.from(TaskInstance.class);
        Root<UsuarioTaskInstance> uti = cq.from(UsuarioTaskInstance.class);
        Join<?, br.com.infox.epp.processo.entity.Processo> prc = prcJbpm.join(ProcessoJbpm_.processo);
        Join<?, Localizacao> local = uti.join(UsuarioTaskInstance_.localizacaoExterna);
        Join<?, UsuarioLogin> usuLogin = uti.join(UsuarioTaskInstance_.usuario);
        Join<?, Papel> papel = uti.join(UsuarioTaskInstance_.papel);
//        Join<?, br.com.infox.epp.processo.entity.Processo> prcRoot = prc.join(Processo_.processoRoot);
        cq.select(cb.construct(cq.getResultType(), ti.get("id"), ti.get("name"), ti.get("create"), ti.get("start"),
                ti.get("end"), local.get(Localizacao_.idLocalizacao), local.get(Localizacao_.localizacao),
                usuLogin.get(UsuarioLogin_.idUsuarioLogin), usuLogin.get(UsuarioLogin_.nomeUsuario),
                papel.get(Papel_.idPapel), papel.get(Papel_.nome)));
        cq.where(cb.equal(prc.get(Processo_.processoRoot).get(Processo_.idProcesso), idProcesso),
                cb.equal(prcJbpm.get(ProcessoJbpm_.processInstance), ti.get("processInstance")),
                cb.equal(uti.get(UsuarioTaskInstance_.idTaskInstance), ti.get("id")));
        cq.orderBy(cb.asc(ti.get("create")));
        List<Movimentacao> resultList = getEntityManager().createQuery(cq).getResultList();

        List<MovimentacaoGroup> groups = new ArrayList<>();
        Integer idCurrLog = null;
        MovimentacaoGroup currGroup = new MovimentacaoGroup();

        for (Movimentacao mov : resultList) {
            if(!mov.getIdLocalizacao().equals(idCurrLog)) {
                currGroup = new MovimentacaoGroup();
                idCurrLog = mov.getIdLocalizacao();
                groups.add(currGroup);
            }
            currGroup.add(mov);
        }

        groups.sort(Comparator.comparing(MovimentacaoGroup::getStart));
        return groups;
    }

    public List<DocumentoBin> getListaDocumentoBinByIdProcessoAndLocalizacaoAndDtInclusao(Integer idProcesso,
            Integer idLocalizacao, Date dtStart, Date dtEnd) {
      CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
      CriteriaQuery<DocumentoBin> documentosQuery = cb.createQuery(DocumentoBin.class);
      Root<DocumentoBin> docBin = documentosQuery.from(DocumentoBin.class);
      
      Subquery<Integer> subqueryDocumentos = documentosQuery.subquery(Integer.class);
      Root<DocumentoBin> subDocBin = subqueryDocumentos.from(DocumentoBin.class);
      ListJoin<?, br.com.infox.epp.processo.documento.entity.Documento> doc = subDocBin.join(DocumentoBin_.documentoList,
              JoinType.INNER);
      Join<?, Pasta> pasta = doc.join(Documento_.pasta, JoinType.INNER);
      subqueryDocumentos.select(subDocBin.get(DocumentoBin_.id));
      subqueryDocumentos.groupBy(subDocBin.get(DocumentoBin_.id));
      subqueryDocumentos.where(
              cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), idProcesso),
              cb.isFalse(doc.get(Documento_.excluido)), cb.between(doc.get(Documento_.dataInclusao), dtStart, dtEnd),
              cb.equal(doc.get(Documento_.localizacao).get(Localizacao_.idLocalizacao), idLocalizacao),
              cb.isTrue(subDocBin.get(DocumentoBin_.suficientementeAssinado))
              );
      
      documentosQuery.select(docBin);
      documentosQuery.where(docBin.get(DocumentoBin_.id).in(subqueryDocumentos));
      
      List<DocumentoBin> documentos = getEntityManager().createQuery(documentosQuery).getResultList();
      return documentos;
    }

    public ProcessoDTO getProcessoDTOByNrProcesso(String nrProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProcessoDTO> cq = cb.createQuery(ProcessoDTO.class);
        Root<br.com.infox.epp.processo.entity.Processo> processo = cq
                .from(br.com.infox.epp.processo.entity.Processo.class);
        cq.select(cb.construct(cq.getResultType(), processo.get(Processo_.idProcesso),
                processo.get(Processo_.numeroProcesso)));
        cq.where(cb.equal(processo.get(Processo_.numeroProcesso), nrProcesso));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
