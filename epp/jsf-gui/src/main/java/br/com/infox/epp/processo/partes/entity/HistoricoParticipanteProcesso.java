package br.com.infox.epp.processo.partes.entity;

import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.HAS_HISTORICO_BY_PARTICIPANTE;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.HAS_HISTORICO_BY_PARTICIPANTE_QUERY;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.LIST_BY_PARTICIPANTE_PROCESSO;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.LIST_BY_PARTICIPANTE_PROCESSO_QUERY;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = HistoricoParticipanteProcesso.TABLE_NAME)
@NamedQueries(value = {
        @NamedQuery(name = LIST_BY_PARTICIPANTE_PROCESSO, query = LIST_BY_PARTICIPANTE_PROCESSO_QUERY),
        @NamedQuery(name = HAS_HISTORICO_BY_PARTICIPANTE, query = HAS_HISTORICO_BY_PARTICIPANTE_QUERY)
})
public class HistoricoParticipanteProcesso implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_hist_participante_processo";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "HistParticipanteGenerator", sequenceName = "sq_hist_participante_processo")
    @GeneratedValue(generator = "HistParticipanteGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_hist_participante_processo", unique = true, nullable = false)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsavel_modificacao")
    private UsuarioLogin responsavelPorModificacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_alteracao", nullable = false)
    private Date dataModificacao;
    
    @Column(name = "ds_motivo_modificacao", nullable = false, length = LengthConstants.DESCRICAO_MEDIA)
    @Size(max = LengthConstants.DESCRICAO_MEDIA)
    private String motivoModificacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante_processo")
    private ParticipanteProcesso participanteModificado;
   
    @Column(name = "is_ativo", nullable = false)
    private boolean ativo;
    
    @PrePersist
    private void prePersist() {
    	setResponsavelPorModificacao(Authenticator.getUsuarioLogado());
    	setDataModificacao(Calendar.getInstance().getTime());
    }

    public HistoricoParticipanteProcesso() {
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UsuarioLogin getResponsavelPorModificacao() {
        return responsavelPorModificacao;
    }

    public void setResponsavelPorModificacao(
            UsuarioLogin responsavelPorModificacao) {
        this.responsavelPorModificacao = responsavelPorModificacao;
    }

    public Date getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(Date dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    public String getMotivoModificacao() {
        return motivoModificacao;
    }

    public void setMotivoModificacao(String motivoModificacao) {
        this.motivoModificacao = motivoModificacao;
    }

    public ParticipanteProcesso getParticipanteModificado() {
		return participanteModificado;
	}

	public void setParticipanteModificado(ParticipanteProcesso participanteModificado) {
		this.participanteModificado = participanteModificado;
	}

	public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
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
		if (!(obj instanceof HistoricoParticipanteProcesso))
			return false;
		HistoricoParticipanteProcesso other = (HistoricoParticipanteProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
	public HistoricoParticipanteProcesso makeCopy() throws CloneNotSupportedException {
		HistoricoParticipanteProcesso cHistorico = (HistoricoParticipanteProcesso) clone();
		cHistorico.setId(null);
		cHistorico.setParticipanteModificado(null);
		return cHistorico;
	}
    
}
