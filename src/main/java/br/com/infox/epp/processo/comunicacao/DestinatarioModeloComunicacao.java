package br.com.infox.epp.processo.comunicacao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Entity
@Table(name = "tb_destinatario_modelo_comunic", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_pessoa_fisica", "id_modelo_comunicacao"}),
})
public class DestinatarioModeloComunicacao implements Serializable, Cloneable {
    
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DestinatarioModeloComunicacaoGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_destinatario_modelo_comunic")
	@GeneratedValue(generator = "DestinatarioModeloComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_destinatario_modelo_comunic")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_fisica")
	private PessoaFisica destinatario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao")
	private Localizacao destino;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_perfil_destino")
	private PerfilTemplate perfilDestino;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_modelo_comunicacao", nullable = false)
	private ModeloComunicacao modeloComunicacao;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name = "tp_meio_expedicao", nullable = false)
	private MeioExpedicao meioExpedicao;
	
	@Min(0)
	@Column(name = "nr_prazo")
	private Integer prazo;
	
	@Column(name = "in_expedido", nullable = false)
	@NotNull
	private Boolean expedido = false;
	
	@JoinColumn(name = "id_documento")
	@ManyToOne(fetch = FetchType.LAZY)
	private Documento documentoComunicacao;

	@JoinColumn(name = "id_processo", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Processo processo;
	
	@Column(name = "in_individual", nullable = true)
	private Boolean individual = Boolean.TRUE;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_parte", nullable = true)
	private TipoParte tipoParte;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}

	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}

	public MeioExpedicao getMeioExpedicao() {
		return meioExpedicao;
	}

	public void setMeioExpedicao(MeioExpedicao meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
	}

	public Integer getPrazo() {
		return prazo;
	}

	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}

	public Localizacao getDestino() {
		return destino;
	}
	
	public void setDestino(Localizacao destino) {
		this.destino = destino;
	}
	
	public PessoaFisica getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(PessoaFisica destinatario) {
		this.destinatario = destinatario;
	}
	
	public Boolean getExpedido() {
		return expedido;
	}
	
	public void setExpedido(Boolean expedido) {
		this.expedido = expedido;
	}

	public Documento getDocumentoComunicacao() {
		return documentoComunicacao;
	}
	
	public void setDocumentoComunicacao(Documento documentoComunicacao) {
		this.documentoComunicacao = documentoComunicacao;
	}
	
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
    public Boolean getIndividual() {
        return individual;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }
    
    public TipoParte getTipoParte() {
		return tipoParte;
	}

	public void setTipoParte(TipoParte tipoParte) {
		this.tipoParte = tipoParte;
	}

	@Transient
	public String getNome() {
		if (destinatario != null) {
			return destinatario.getNome();
		} else if (perfilDestino != null) {
		    return (destino.getCaminhoCompletoFormatado() + " (" + perfilDestino.getPapel() + ")");
		} else if (destino != null) {
		    return destino.getCaminhoCompletoFormatado();
		}
		return null;
	}
	
	@Transient
	public String getNomeDestino() {
		if (destinatario != null) {
			return destinatario.getNome();
		} else if (perfilDestino != null) {
			return (destino.getPathDescriptor() + " - " + perfilDestino.getPapel());
		} else if (destino != null) {
			return destino.getPathDescriptor();
		}
		return null;
	}

    public PerfilTemplate getPerfilDestino() {
        return perfilDestino;
    }

    public void setPerfilDestino(PerfilTemplate perfilDestino) {
        this.perfilDestino = perfilDestino;
    }
    
    public DestinatarioModeloComunicacao makeCopy() throws CloneNotSupportedException {
    	DestinatarioModeloComunicacao novoDestinatario = (DestinatarioModeloComunicacao) clone();
    	novoDestinatario.setId(null);
    	return novoDestinatario;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DestinatarioModeloComunicacao)) {
			return false;
		}
		DestinatarioModeloComunicacao other = (DestinatarioModeloComunicacao) obj;
		if (getId() != null && other.getId() != null) {
			return getId().equals(other.getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return  getNomeDestino();
	}
	
	
}
