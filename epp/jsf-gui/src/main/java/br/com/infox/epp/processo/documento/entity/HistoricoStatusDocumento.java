package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO_QUERY;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.LIST_HISTORICO_BY_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.LIST_HISTORICO_BY_DOCUMENTO_QUERY;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = HistoricoStatusDocumento.TABLE_NAME)
@NamedQueries(value = {
		@NamedQuery(name = EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO, query = EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO_QUERY),
		@NamedQuery(name = LIST_HISTORICO_BY_DOCUMENTO, query = LIST_HISTORICO_BY_DOCUMENTO_QUERY)
})
@EqualsAndHashCode(of = "id")
public class HistoricoStatusDocumento implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_historico_status_documento";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="HistoricoStatusDocumentoGen", sequenceName="sq_historico_status_documento")
	@GeneratedValue(generator="HistoricoStatusDocumentoGen", strategy=GenerationType.SEQUENCE)
	@Column(name="id_historico_status_documento", nullable=false, unique=true)
	@Getter @Setter
	private Long id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_documento", nullable=false)
	@Getter @Setter
	private Documento documento;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="tp_alteracao_documento", nullable=false)
	@Getter @Setter
	private TipoAlteracaoDocumento tipoAlteracaoDocumento;
	
	@NotNull
	@Column(name="ds_motivo", nullable=false)
	@Getter @Setter
	private String motivo;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_alteracao", nullable=false)
	@Getter @Setter
	private Date dataAlteracao;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_alteracao", nullable=false)
	@Getter @Setter
	private UsuarioLogin usuarioAlteracao;
	
	@PrePersist
	private void prePersist(){
		setUsuarioAlteracao(Authenticator.getUsuarioLogado());
		setDataAlteracao(new Date());
	}

	public HistoricoStatusDocumento makeCopy() throws CloneNotSupportedException {
		HistoricoStatusDocumento clone = (HistoricoStatusDocumento) clone();
		clone.setId(null);
		clone.setDocumento(null);
		return clone;
	}
	
}
