package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;

@Entity
@Table(name="tb_historico_status_documento")
public class HistoricoStatusDocumento implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="HistoricoStatusDocumentoGen", sequenceName="sq_historico_status_documento")
	@GeneratedValue(generator="HistoricoStatusDocumentoGen", strategy=GenerationType.SEQUENCE)
	@Column(name="id_historico_status_documento", nullable=false, unique=true)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_processo_documento", nullable=false)
	private ProcessoDocumento processoDocumento;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="tp_alteracao_documento", nullable=false)
	private TipoAlteracaoDocumento tipoAlteracaoDocumento;
	
	@NotNull
	@Column(name="ds_motivo", nullable=false)
	private String motivo;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_alteracao", nullable=false)
	private Date dataAlteracao;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_alteracao", nullable=false)
	private UsuarioLogin usuarioAlteracao;
	
	@PrePersist
	private void prePersist(){
		setUsuarioAlteracao(Authenticator.getUsuarioLogado());
		setDataAlteracao(new Date());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProcessoDocumento getProcessoDocumento() {
		return processoDocumento;
	}

	public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
		this.processoDocumento = processoDocumento;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public TipoAlteracaoDocumento getTipoAlteracaoDocumento() {
		return tipoAlteracaoDocumento;
	}

	public void setTipoAlteracaoDocumento(
			TipoAlteracaoDocumento tipoAlteracaoDocumento) {
		this.tipoAlteracaoDocumento = tipoAlteracaoDocumento;
	}

	public UsuarioLogin getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
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
		if (!(obj instanceof HistoricoStatusDocumento))
			return false;
		HistoricoStatusDocumento other = (HistoricoStatusDocumento) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
