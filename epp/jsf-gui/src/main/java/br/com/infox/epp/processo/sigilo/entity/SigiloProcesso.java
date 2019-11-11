package br.com.infox.epp.processo.sigilo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_ATIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_DATA_INCLUSAO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_ID;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_ID_PROCESSO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_ID_USUARIO_LOGIN;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_MOTIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.COLUMN_SIGILOSO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.NAMED_QUERY_SIGILO_PROCESSO_ATIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.NAMED_QUERY_SIGILO_PROCESSO_USUARIO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.QUERY_SIGILO_PROCESSO_ATIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.QUERY_SIGILO_PROCESSO_USUARIO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.TABLE_NAME;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
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
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries({ 
	@NamedQuery(name = NAMED_QUERY_SIGILO_PROCESSO_USUARIO, query = QUERY_SIGILO_PROCESSO_USUARIO),
	@NamedQuery(name = NAMED_QUERY_SIGILO_PROCESSO_ATIVO, query = QUERY_SIGILO_PROCESSO_ATIVO,
				hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
			 			 @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.processo.sigilo.entity.SigiloProcesso")}) 
})
@Cacheable
public class SigiloProcesso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = COLUMN_ID)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_ID_PROCESSO, nullable = false)
    private Processo processo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_ID_USUARIO_LOGIN, nullable = false)
    private UsuarioLogin usuario;

    @Column(name = COLUMN_SIGILOSO, nullable = false)
    private Boolean sigiloso;

    @Column(name = COLUMN_MOTIVO, nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = COLUMN_DATA_INCLUSAO, nullable = false)
    private Date dataInclusao;

    @Column(name = COLUMN_ATIVO, nullable = false)
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public Boolean getSigiloso() {
        return sigiloso;
    }

    public void setSigiloso(Boolean sigiloso) {
        this.sigiloso = sigiloso;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
