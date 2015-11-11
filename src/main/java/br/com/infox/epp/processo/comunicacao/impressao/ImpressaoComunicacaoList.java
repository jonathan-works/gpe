package br.com.infox.epp.processo.comunicacao.impressao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.entity.Processo;

@Scope(ScopeType.PAGE)
@Name(ImpressaoComunicacaoList.NAME)
public class ImpressaoComunicacaoList extends EntityList<Processo> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "impressaoComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from DestinatarioModeloComunicacao dmc " +
												"inner join dmc.processo o " +
												"inner join dmc.documentoComunicacao doc " +
												"inner join doc.documentoBin bin " +
												"left join bin.assinaturas a " +
												"where exists (select 1 from MetadadoProcesso mp " +
												"			  where mp.metadadoType = '" + ComunicacaoMetadadoProvider.MEIO_EXPEDICAO.getMetadadoType() + "' " + 
												"			  and (mp.valor = '" + MeioExpedicao.DO.name() + "' or mp.valor = '" + MeioExpedicao.IM.name() + "' ) " +
												"			  and mp.processo = o) " +
												"and o.localizacao = #{usuarioLogadoPerfilAtual.localizacao} ";
	
	private static final String DEFAULT_ORDER = "a.dataAssinatura desc";
	
	private static final String CONDICAO_MEIO_IMPRESSAO =
	        "and exists (select 1 from MetadadoProcesso mp where "
	                + "mp.metadadoType = '" + ComunicacaoMetadadoProvider.MEIO_EXPEDICAO.getMetadadoType() + "' "
                    + "and mp.valor = #{impressaoComunicacaoList.meioExpedicao.name()} "
                    + "and mp.processo = o) ";
	//Essa query pode trazer resultados incorretos quando houver mais de uma assinatura
	private static final String CONDICAO_DATA_ASSINATURA_PREFIX =
	        "and exists (select 1 from DestinatarioModeloComunicacao dmc "
	        + "inner join dmc.documentoComunicacao c "
	        + "inner join c.documentoBin bin "
	        + "inner join bin.assinaturas a "
	        + "where a.dataAssinatura is not null ";
	private static final String CONDICAO_DATA_ASSINATURA_SUFIX =
	        "and dmc.id = (select cast(mp.valor as integer) from MetadadoProcesso mp "
	                + "where mp.metadadoType = '" + ComunicacaoMetadadoProvider.DESTINATARIO.getMetadadoType() + "' "
	                + "and mp.processo = o))";
	private static final String CONDICAO_DATA_INICIO = "and cast(a.dataAssinatura as date) >= cast(#{impressaoComunicacaoList.dataInicio} as date) ";
	private static final String CONDICAO_DATA_FIM = "and cast(a.dataAssinatura as date) <= cast(#{impressaoComunicacaoList.dataFim} as date) ";
	private static final String CONDICAO_IMPRESSO =
	        "and exists (select 1 from MetadadoProcesso mp where "
	                + "mp.metadadoType = '" + ComunicacaoMetadadoProvider.IMPRESSA.getMetadadoType() + "' "
	                + "and cast(mp.valor as boolean) = #{impressaoComunicacaoList.impresso} "
	                + "and mp.processo = o)";
	private static final String CONDICAO_NAO_IMPRESSO = "and "
	        + "("
                + "not exists (select 1 from MetadadoProcesso mp where "
                    + "mp.metadadoType = '" + ComunicacaoMetadadoProvider.IMPRESSA.getMetadadoType() + "' "
                    + "and mp.processo = o) "
                + "or exists (select 1 from MetadadoProcesso mp where "
                    + "mp.metadadoType = '" + ComunicacaoMetadadoProvider.IMPRESSA.getMetadadoType() + "' "
                    + "and cast(mp.valor as boolean) = #{impressaoComunicacaoList.impresso} "
                    + "and mp.processo = o)"
            + ")";
	
	@In
	private ImpressaoComunicacaoService impressaoComunicacaoService;
	
	private List<MeioExpedicao> meiosExpedicao;
	private MeioExpedicao meioExpedicao;
	private Boolean showDataTable = false;
	private Date dataInicio;
	private Date dataFim;
	private Boolean impresso = false;
	
	{
		meioExpedicao = MeioExpedicao.IM;
		meiosExpedicao = new ArrayList<>(2);
		meiosExpedicao.add(MeioExpedicao.IM);
		meiosExpedicao.add(MeioExpedicao.DO);
	}
	
	private String getEjbqlRestrictedByFilters() {
        StringBuilder sb = new StringBuilder(DEFAULT_EJBQL);
        if (meioExpedicao != null) {
            sb.append(CONDICAO_MEIO_IMPRESSAO);
        }
        if (impresso != null) {
            sb.append(impresso ? CONDICAO_IMPRESSO : CONDICAO_NAO_IMPRESSO);
        }
        if (dataInicio == null && dataFim == null) {
            return sb.toString();
        } else {
            sb.append(CONDICAO_DATA_ASSINATURA_PREFIX);
            if (dataInicio != null) {
                sb.append(CONDICAO_DATA_INICIO);
            }
            if (dataFim != null) {
                sb.append(CONDICAO_DATA_FIM);
            }
            sb.append(CONDICAO_DATA_ASSINATURA_SUFIX);
        }
        return sb.toString();
    }

	@Override
	public List<Processo> getResultList() {
	    setEjbql(getEjbqlRestrictedByFilters());
		List<Processo> resultList = super.getResultList();
		this.showDataTable = true;
		return resultList;
	}
	
    @Override
    public void newInstance() {
        super.newInstance();
        this.meioExpedicao = MeioExpedicao.IM;
        this.dataInicio = null;
        this.dataFim = null;
        this.impresso = false;
    }

	@Override
	protected void addSearchFields() {
	}

    public void showDataTable() {
        setShowDataTable(true);
    }

    public void hideDataTable() {
        setShowDataTable(false);
    }

    public List<MeioExpedicao> getMeiosExpedicao() {
		return meiosExpedicao;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	public MeioExpedicao getMeioExpedicao() {
		return meioExpedicao;
	}

	public void setMeioExpedicao(MeioExpedicao meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
	}

    public Boolean getShowDataTable() {
        return showDataTable;
    }

    public void setShowDataTable(Boolean showDataTable) {
        this.showDataTable = showDataTable;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Boolean getImpresso() {
        return impresso;
    }

    public void setImpresso(Boolean impresso) {
        this.impresso = impresso;
    }
	
}
