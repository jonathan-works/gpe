package br.com.infox.epp.processo.partes.entity;

import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO;
import static br.com.infox.epp.processo.partes.query.ParticipanteProcessoQuery.PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Entity
@Table(name = ParticipanteProcesso.TABLE_NAME)
@NamedQueries(value={
		@NamedQuery(name=PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO, query=PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY)
})
public class ParticipanteProcesso implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_participante_processo";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "ParticipanteProcessoGenerator", sequenceName = "sq_participante_processo")
    @GeneratedValue(generator = "ParticipanteProcessoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_participante_processo", nullable = false)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private ProcessoEpa processo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;
    
    @NotNull
    @Column(name = "nm_participante", nullable = false)
    private String nome;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_participante", nullable = false)
    private TipoParte tipoParticipante;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante_pai", nullable = true)
    private ParticipanteProcesso participantePai;
    
    @Column(name = "in_ativo")
    private Boolean ativo = Boolean.TRUE;
    
    public ParticipanteProcesso() {
    }
    
    public ParticipanteProcesso(ProcessoEpa processo, Pessoa pessoa) {
    	this.processo = processo;
        this.pessoa = pessoa;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProcessoEpa getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoEpa processo) {
        this.processo = processo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
    
	public TipoParte getTipoParticipante() {
		return tipoParticipante;
	}

	public void setTipoParticipante(TipoParte tipoParticipante) {
		this.tipoParticipante = tipoParticipante;
	}

	public ParticipanteProcesso getParticipantePai() {
		return participantePai;
	}

	public void setParticipantePai(ParticipanteProcesso participantePai) {
		this.participantePai = participantePai;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParticipanteProcesso))
			return false;
		ParticipanteProcesso other = (ParticipanteProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
