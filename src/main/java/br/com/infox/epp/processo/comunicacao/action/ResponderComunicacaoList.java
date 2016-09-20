package br.com.infox.epp.processo.comunicacao.action;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.action.ResponderComunicacaoList.ResponderComunicacaoBean;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoSearch;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoUsoComunicacaoEnum;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Named
@ViewScoped
public class ResponderComunicacaoList extends DataList<ResponderComunicacaoBean> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoComunicacaoSearch tipoComunicacaoSearch;
    
    private final String DEFAULT_JPQL = "select new br.com.infox.epp.processo.comunicacao.action.ResponderComunicacaoList$ResponderComunicacaoBean( "
            + "o.idProcesso, pr.numeroProcesso, o.numeroProcesso, "
            + " (case when destModelo.destinatario is not null then "
            + "       pf.nome "
            + "  else "
            + "       case when perf.descricao is null then "
            + "           loc.localizacao "
            + "       else "
            + "           concat(loc.localizacao, ': ', perf.descricao) "
            + "       end "
            + "  end), "
            + " mpDataCiencia.valor, mpDataResposta.valor "
            + ") " 
            + "from Processo o, DestinatarioModeloComunicacao destModelo "
            + "inner join o.processoRoot pr "
            + "inner join destModelo.modeloComunicacao modCom "
            + "inner join o.metadadoProcessoList mpDestinatario "
            + "inner join o.metadadoProcessoList mpDataCiencia "
            + "inner join o.metadadoProcessoList mpDataResposta "
            + "left join destModelo.destinatario pf "
            + "left join destModelo.destino loc "
            + "left join destModelo.perfilDestino perf ";
    
    private final String DEFAULT_WHERE = "where exists ( select 1 from MetadadoProcesso mp where mp.processo.idProcesso = o.idProcesso " +
                                                "                and mp.metadadoType = '" + EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType() + "' " +
                                                "                and mp.valor = '" + TipoProcesso.COMUNICACAO.value() + "' ) " + 
                                                "and mpDestinatario.metadadoType =  '" + ComunicacaoMetadadoProvider.DESTINATARIO.getMetadadoType() + "' " +
                                                "and mpDataCiencia.metadadoType = '" + ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType()  + "' " +
                                                "and mpDataResposta.metadadoType = '" + ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO.getMetadadoType()  + "' " +
                                                "and cast(destModelo.id as string) = mpDestinatario.valor " +
                                                "and o.dataFim is null and o.idJbpm is not null " +
                                                "and #{currentDate} <= to_date(mpDataResposta.valor) ";
    
    private final String FILTER_DESTINATARIO = " 'true' = "
            + "( case when (destModelo.destinatario is not null) then "
            + "    case when pf.idPessoa = {idPessoaLogada} then 'true' else 'false' end "
            + "else "
            + "    case when (destModelo.perfilDestino is not null) then "
            + "        case when (perf.id = {idPerfilLogado} and loc.idLocalizacao = {idLocalizacaoLogado} ) then 'true' else 'false' end "
            + "    else "
            + "        case when (loc.idLocalizacao = {idLocalizacaoLogado}) then 'true' else 'false' end "
            + "    end "
            + "end ) ";
    
    private final String FILTER_DESTINATARIO_LIBERADO = " exists (select 1 from PessoaRespostaComunicacao prc "
            + "where prc.comunicacao.idProcesso = o.idProcesso and prc.pessoaFisica.idPessoa = {idPessoaLogada} ) ";
    
    private String numeroProcessoRoot;
    private String numeroProcesso;
    private TipoComunicacao tipoComunicacao;
    private Date dataCienciaFrom;
    private Date dataCienciaTo;
    private Date dataRespostaFrom;
    private Date dataRespostaTo;
    
    private List<TipoComunicacao> tipoComunicacaoList;
    
    @Override
    protected void postInit() {
        tipoComunicacaoList = tipoComunicacaoSearch.getTiposComunicacaoAtivosByUso(TipoUsoComunicacaoEnum.E);
    }
    
    @Override
    protected String getDefaultOrder() {
        return "o.dataInicio";
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
    
    @Override
    protected String getDefaultWhere() {
        return DEFAULT_WHERE;
    }
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("numeroProcessoRoot", "pr.numeroProcesso like concat('%', #{responderComunicacaoList.numeroProcessoRoot}, '%')");
        addRestrictionField("numeroProcesso", "o.numeroProcesso like concat('%', #{responderComunicacaoList.numeroProcesso}, '%')");
        addRestrictionField("tipoComunicacao", "modCom.tipoComunicacao.id = #{responderComunicacaoList.tipoComunicacao.id}");
        addRestrictionField("dataCienciaFrom", "to_date(mpDataCiencia.valor) >= #{responderComunicacaoList.dataCienciaFrom}");
        addRestrictionField("dataCienciaTo", "to_date(mpDataCiencia.valor) <= #{responderComunicacaoList.dataCienciaTo}");
        addRestrictionField("dataRespostaFrom", "to_date(mpDataResposta.valor) <= #{responderComunicacaoList.dataRespostaFrom}");
        addRestrictionField("dataRespostaTo", "to_date(mpDataResposta.valor) <= #{responderComunicacaoList.dataRespostaTo}");
    }
    
    @Override
    protected void addAdditionalClauses(StringBuilder sb) {
        PessoaFisica pessoaFisica = Authenticator.getUsuarioLogado().getPessoaFisica();
        Integer idPessoa = pessoaFisica == null ? -1 : pessoaFisica.getIdPessoa();
        PerfilTemplate perfilTemplate = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
        Localizacao localizacaoAtual = Authenticator.getLocalizacaoAtual();
        String filterDestinatario = FILTER_DESTINATARIO.replace("{idPessoaLogada}", idPessoa.toString())
                .replace("{idPerfilLogado}", perfilTemplate.getId().toString())
                .replace("{idLocalizacaoLogado}", localizacaoAtual.getIdLocalizacao().toString());
        String filterDestinatarioLiberado = FILTER_DESTINATARIO_LIBERADO.replace("{idPessoaLogada}", idPessoa.toString());
        sb.append(" and ( ").append(filterDestinatario).append(" or ").append(filterDestinatarioLiberado).append(" ) ") ;
    }
    
    public List<TipoComunicacao> getTipoComunicacaoList() {
        return tipoComunicacaoList;
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public void setNumeroProcessoRoot(String numeroProcessoRoot) {
        this.numeroProcessoRoot = numeroProcessoRoot;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoComunicacao getTipoComunicacao() {
        return tipoComunicacao;
    }

    public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
        this.tipoComunicacao = tipoComunicacao;
    }

    public Date getDataCienciaFrom() {
        return dataCienciaFrom;
    }

    public void setDataCienciaFrom(Date dataCienciaFrom) {
        this.dataCienciaFrom = dataCienciaFrom;
    }

    public Date getDataCienciaTo() {
        return dataCienciaTo;
    }

    public void setDataCienciaTo(Date dataCienciaTo) {
        this.dataCienciaTo = dataCienciaTo;
    }

    public Date getDataRespostaFrom() {
        return dataRespostaFrom;
    }

    public void setDataRespostaFrom(Date dataRespostaFrom) {
        this.dataRespostaFrom = dataRespostaFrom;
    }

    public Date getDataRespostaTo() {
        return dataRespostaTo;
    }

    public void setDataRespostaTo(Date dataRespostaTo) {
        this.dataRespostaTo = dataRespostaTo;
    }
    
    public static class ResponderComunicacaoBean {

        private Integer idComunicacao;
        private String numeroProcessoRoot;
        private String numeroProcesso;
        private String destinatario;
        private Date dataCiencia;
        private Date prazoResposta;
        
        public ResponderComunicacaoBean(Integer idComunicacao, String numeroProcessoRoot, String numeroProcesso, Object destinatario,
                String dataCiencia, String prazoResposta) {
            this.idComunicacao = idComunicacao;
            this.numeroProcessoRoot = numeroProcessoRoot;
            this.numeroProcesso = numeroProcesso;
            this.destinatario = (String) destinatario;
            this.dataCiencia = convertToDate(dataCiencia);
            this.prazoResposta = convertToDate(prazoResposta);
        }

        private Date convertToDate(String dataCiencia) {
            if (dataCiencia != null) {
                return DateTime.parse(dataCiencia, DateTimeFormat.forPattern(MetadadoProcesso.DATE_PATTERN)).toDate();
            }
            return null;
        }

        public Integer getIdComunicacao() {
            return idComunicacao;
        }

        public String getNumeroProcessoRoot() {
            return numeroProcessoRoot;
        }

        public String getNumeroProcesso() {
            return numeroProcesso;
        }

        public String getDestinatario() {
            return destinatario;
        }

        public Date getDataCiencia() {
            return dataCiencia;
        }

        public Date getPrazoResposta() {
            return prazoResposta;
        }

    }

}
