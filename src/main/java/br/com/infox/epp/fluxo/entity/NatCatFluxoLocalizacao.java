package br.com.infox.epp.fluxo.entity;

import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_LOCALIZACAO_AND_PAPEL;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_LOCALIZACAO_AND_PAPEL_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO_QUERY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name=NatCatFluxoLocalizacao.TABLE_NAME, schema="public",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"id_nat_cat_fluxo", "id_localizacao"})
})
@NamedQueries(value = {
    @NamedQuery(name = GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF, query = GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY),
    @NamedQuery(name = COUNT_NCF_LOCALIZACAO_BY_LOC_NCF, query = COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY),
    @NamedQuery(name = LIST_BY_LOCALIZACAO_AND_PAPEL, query = LIST_BY_LOCALIZACAO_AND_PAPEL_QUERY),
    @NamedQuery(name = LIST_BY_NAT_CAT_FLUXO, query = LIST_BY_NAT_CAT_FLUXO_QUERY) })
public class NatCatFluxoLocalizacao implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "tb_nat_cat_fluxo_localizacao";

	private int idNatCatFluxoLocalizacao;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;	
	private Localizacao localizacao;
	private boolean heranca = false;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_nat_cat_fluxo_localizacao")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_nat_cat_fluxo_localizacao", unique = true, nullable = false)
	public int getIdNatCatFluxoLocalizacao() {
		return idNatCatFluxoLocalizacao;
	}
	public void setIdNatCatFluxoLocalizacao(int idNatCatFluxoLocalizacao) {
		this.idNatCatFluxoLocalizacao = idNatCatFluxoLocalizacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nat_cat_fluxo", nullable=false)
	@NotNull(message = "#{messages['beanValidation.notNull']}")
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}
	
	public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable=false)
	@NotNull(message = "#{messages['beanValidation.notNull']}")
	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public void setHeranca(boolean heranca) {
		this.heranca = heranca;
	}
	
	@Column(name="in_heranca", nullable=false)
	public boolean getHeranca() {
		return heranca;
	}
	
	@Transient
	public boolean isAtivo(){
		return (naturezaCategoriaFluxo.isAtivo() && localizacao.getAtivo());
	}
	
}