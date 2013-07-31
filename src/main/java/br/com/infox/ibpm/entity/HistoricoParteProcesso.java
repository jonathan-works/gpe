package br.com.infox.ibpm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name=HistoricoParteProcesso.TABLE_NAME, schema="public")
public class HistoricoParteProcesso {
	
	public static final String TABLE_NAME = "tb_historico_parte_processo";
	
	private Integer idHistoricoParteProcesso;
	private UsuarioLogin responsavelPorModificacao;
	private Date dataModificacao;
	private String motivoModificacao;
	private ParteProcesso parteModificada;
	//Atributos da Entidade ParteProcesso que podem ser modificados
	//Em caso de novas regras/atributos, favor inserir aqui abaixo
	private boolean ativo;
	
	public HistoricoParteProcesso(){
		super();
	}
	
	public HistoricoParteProcesso(ParteProcesso parteProcessoAtual, String motivoModificacao){
		responsavelPorModificacao = Authenticator.getUsuarioLogado();
		dataModificacao = new Date();
		this.motivoModificacao = motivoModificacao;
		parteModificada = parteProcessoAtual;
		ativo = parteProcessoAtual.getAtivo();
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_historico_parte_processo")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_historico_parte_processo", unique=true, nullable=false)
	public Integer getIdHistoricoParteProcesso() {
		return idHistoricoParteProcesso;
	}

	public void setIdHistoricoParteProcesso(Integer idHistoricoParteProcesso) {
		this.idHistoricoParteProcesso = idHistoricoParteProcesso;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_responsavel_modificacao")
	public UsuarioLogin getResponsavelPorModificacao() {
		return responsavelPorModificacao;
	}

	public void setResponsavelPorModificacao(UsuarioLogin responsavelPorModificacao) {
		this.responsavelPorModificacao = responsavelPorModificacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_alteracao", nullable=false)
	public Date getDataModificacao() {
		return dataModificacao;
	}

	public void setDataModificacao(Date dataModificacao) {
		this.dataModificacao = dataModificacao;	
	}

	@Column(name="ds_motivo_modificacao", nullable=false, length=LengthConstants.DESCRICAO_MEDIA)
	public String getMotivoModificacao() {
		return motivoModificacao;
	}

	public void setMotivoModificacao(String motivoModificacao) {
		this.motivoModificacao = motivoModificacao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_parte_processo")
	public ParteProcesso getParteModificada() {
		return parteModificada;
	}

	public void setParteModificada(ParteProcesso parte) {
		this.parteModificada = parte;
	}

	@Column(name="is_ativo", nullable=false)
	public boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
