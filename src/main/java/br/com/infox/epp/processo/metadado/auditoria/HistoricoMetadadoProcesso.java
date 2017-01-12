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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name="tb_historico_metadado_processo")
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
	
	@Column(name = "in_visivel", nullable = true)
	private Boolean visivel;
	
	@Column(name = "dt_registro", nullable = true)
	private Date dataRegistro;
	
	@Column(name = "ds_valor_objeto", nullable = true)
    private String valorObjeto;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_logado", nullable = true)
    private UsuarioLogin usuarioLogin;
	
	@Column(name = "ds_acao", nullable = true, length=LengthConstants.CODIGO_DOCUMENTO)
	private String acao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdMetadadoProcesso() {
		return idMetadadoProcesso;
	}

	public void setIdMetadadoProcesso(Long idMetadadoProcesso) {
		this.idMetadadoProcesso = idMetadadoProcesso;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

	public Long getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Long idProcesso) {
		this.idProcesso = idProcesso;
	}

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

    public UsuarioLogin getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getValorObjeto() {
        return valorObjeto;
    }

    public void setValorObjeto(String valorObjeto) {
        this.valorObjeto = valorObjeto;
    }
    
	
}
