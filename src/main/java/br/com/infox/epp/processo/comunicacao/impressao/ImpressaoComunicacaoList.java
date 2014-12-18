package br.com.infox.epp.processo.comunicacao.impressao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;
import br.com.infox.epp.processo.type.TipoProcesso;
import edu.emory.mathcs.backport.java.util.Collections;

@Scope(ScopeType.PAGE)
@Name(ImpressaoComunicacaoList.NAME)
public class ImpressaoComunicacaoList extends EntityList<Processo> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "impressaoComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from Processo o " +
												"where exists (select 1 from MetadadoProcesso mp " +
												"				where mp.metadadoType = '" + MetadadoProcessoType.TIPO_PROCESSO + "' " +
												"				and mp.valor = '" + TipoProcesso.COMUNICACAO + "' " +
												"				and mp.processo = o) " +
												"and exists (select 1 from MetadadoProcesso mp " +
												"			  where mp.metadadoType = '" + ComunicacaoService.MEIO_EXPEDICAO + "' " + 
												"			  and (mp.valor = '" + MeioExpedicao.DO + "' or mp.valor = '" + MeioExpedicao.IM + "' ) " +
												"			  and mp.processo = o) ";
	
	private static final String DEFAULT_ORDER = "idProcesso";
	
	private static final String R1 = "exists (select 1 from MetadadoProcesso mp where mp.processo = o " +
									 "			and mp.metadadoType = '" + ComunicacaoService.MEIO_EXPEDICAO + "' " +
									 "			and mp.valor = #{impressaoComunicacaoList.meioExpedicao.name()} ) ";
	
	private static final String R2 = "exists (select 1 from DestinatarioModeloComunicacao dmc " +
									 " 		  inner join dmc.comunicacao c " +
									 "		  inner join c.assinaturas a " +	
									 " 	      where cast(a.dataAssinatura as date) = cast(#{impressaoComunicacaoList.dataAssinatura} as date) " +
									 "		  and dmc.id = (select cast(mp.valor as integer) from MetadadoProcesso mp " + 
									 "						where mp.metadadoType = '" + ComunicacaoService.DESTINATARIO + "' " +
									 "						and mp.processo = o ) )";	
	
	@In
	private ImpressaoComunicacaoService impressaoComunicacaoService;
	
	private List<MeioExpedicao> meiosExpedicao;
	private MeioExpedicao meioExpedicao;
	private Date dataAssinatura;
	
	{
		meioExpedicao = MeioExpedicao.IM;
		meiosExpedicao = new ArrayList<>(2);
		meiosExpedicao.add(MeioExpedicao.IM);
		meiosExpedicao.add(MeioExpedicao.DO);
	}
	
	private Comparator<Processo> comparator = new Comparator<Processo>() {
		@Override
		public int compare(Processo o1, Processo o2) {
			Boolean o1Impresso = impressaoComunicacaoService.getImpresso(o1);
			Boolean o2Impresso = impressaoComunicacaoService.getImpresso(o2);
			Date o1DataAssinatura = impressaoComunicacaoService.getDataAssinatura(o1);
			Date o2DataAssinatura = impressaoComunicacaoService.getDataAssinatura(o2);
			int compare = o1Impresso.compareTo(o2Impresso);
			return compare == 0 ? o2DataAssinatura.compareTo(o1DataAssinatura): compare;
		}
	};
	
	@Override
	public List<Processo> getResultList() {
		List<Processo> resultList = super.getResultList();
		Collections.sort(resultList, comparator);
		return resultList;
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		this.meioExpedicao = MeioExpedicao.IM;
	}

	@Override
	protected void addSearchFields() {
		addSearchField("meioExpedicao", SearchCriteria.IGUAL, R1);
		addSearchField("dataAssinatura", SearchCriteria.DATA_IGUAL, R2);
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

	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}
	
}
