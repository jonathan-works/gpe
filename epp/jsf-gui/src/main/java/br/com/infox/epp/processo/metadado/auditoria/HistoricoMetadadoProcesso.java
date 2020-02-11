package br.com.infox.epp.processo.metadado.auditoria;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.annotation.Ignore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Ignore
@Table(name="tb_historico_metadado_processo") @Data @EqualsAndHashCode(of="id")
public class HistoricoMetadadoProcesso implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(allocationSize=1, initialValue=1, name="HistoricoMetadadoProcessoGenerator", sequenceName="sq_historico_metadado_processo")
	@GeneratedValue(generator = "HistoricoMetadadoProcessoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_historico_metadado_processo", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "id_metadado_processo", nullable = true)
	private Long idMetadadoProcesso;
	
	@Column(name = "nm_metadado_processo", nullable = true, length=LengthConstants.DESCRICAO_MEDIA)
	private String nome;
	
	@Column(name = "vl_metadado_processo", nullable = true, length=LengthConstants.DESCRICAO_MEDIA)
	private String valor;
	
	@Column(name = "ds_tipo", nullable = false)
    private Class<?> classType;
	
	@Column(name = "id_processo", nullable = true)
	private Long idProcesso;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo", nullable = false, updatable=false, insertable=false)
	private Processo processo;
	
	@Column(name = "in_visivel", nullable = true)
	private Boolean visivel;
	
	@Column(name = "dt_registro", nullable = true)
	private Date dataRegistro;
	
	@Column(name = "ds_objeto", nullable = true)
    private String descricao;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_logado", nullable = true)
    private UsuarioLogin usuarioLogin;
	
	@Column(name = "ds_acao", nullable = true, length = LengthConstants.DESCRICAO_GRANDE)
	private String acao;

	@Override
	public String toString() {
		return "HistoricoMetadadoProcesso [nome=" + nome + ", valor=" + valor + "]";
	}
    
    

}
