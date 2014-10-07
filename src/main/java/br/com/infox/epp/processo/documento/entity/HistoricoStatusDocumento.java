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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;

import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO_QUERY;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.LIST_HISTORICO_BY_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.LIST_HISTORICO_BY_DOCUMENTO_QUERY;

@Entity
@Table(name = HistoricoStatusDocumento.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO, query = EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO_QUERY),
		@NamedQuery(name = LIST_HISTORICO_BY_DOCUMENTO, query = LIST_HISTORICO_BY_DOCUMENTO_QUERY)
})
public class HistoricoStatusDocumento implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_historico_status_documento";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="HistoricoStatusDocumentoGen", sequenceName="sq_historico_status_documento")
	@GeneratedValue(generator="HistoricoStatusDocumentoGen", strategy=GenerationType.SEQUENCE)
	@Column(name="id_historico_status_documento", nullable=false, unique=true)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_documento", nullable=false)
	private Documento documento;
	
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

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
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
