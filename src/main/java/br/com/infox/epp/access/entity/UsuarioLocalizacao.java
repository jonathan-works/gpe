package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.CONTABILIZAR;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ESTRUTURA;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ID_USUARIO_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PAPEL;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.RESPONSAVEL_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.SEQUENCE_USUARIO_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.TABLE_USUARIO_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.USUARIO;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Entity
@Table(name = TABLE_USUARIO_LOCALIZACAO, uniqueConstraints = @UniqueConstraint(columnNames = {
    USUARIO, PAPEL, LOCALIZACAO, ESTRUTURA }))
@Inheritance(strategy = InheritanceType.JOINED)
public class UsuarioLocalizacao implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLocalizacao.class);

    private Integer idUsuarioLocalizacao;
    private Localizacao localizacao;
    private UsuarioLogin usuario;
    private Boolean responsavelLocalizacao;
    private Papel papel;
    private Localizacao estrutura;
    private String descricao;
    private Boolean contabilizar;

    public UsuarioLocalizacao() {
        this.contabilizar = Boolean.TRUE;
    }

    public UsuarioLocalizacao(final UsuarioLogin usuario,
            final Localizacao localizacao, final Localizacao estrutura,
            final Papel papel, final boolean contabilizar,
            final boolean responsavelLocalizacao) {
        this.usuario = usuario;
        this.localizacao = localizacao;
        this.estrutura = estrutura;
        this.papel = papel;
        this.contabilizar = Boolean.valueOf(contabilizar);
        this.responsavelLocalizacao = Boolean.valueOf(responsavelLocalizacao);
    }

    public UsuarioLocalizacao(final UsuarioLogin usuario,
            final Localizacao localizacao, final Localizacao estrutura,
            final Papel papel) {
        this(usuario, localizacao, estrutura, papel, Boolean.TRUE, Boolean.FALSE);
    }

    public UsuarioLocalizacao(final UsuarioLogin usuario,
            final Localizacao localizacao, final Localizacao estrutura,
            final Papel papel, final boolean responsavelLocalizacao) {
        this(usuario, localizacao, estrutura, papel, Boolean.TRUE, responsavelLocalizacao);
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_USUARIO_LOCALIZACAO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_USUARIO_LOCALIZACAO, unique = true, nullable = false)
    public Integer getIdUsuarioLocalizacao() {
        return this.idUsuarioLocalizacao;
    }

    public void setIdUsuarioLocalizacao(Integer idUsuarioLocalizacao) {
        this.idUsuarioLocalizacao = idUsuarioLocalizacao;
    }

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = LOCALIZACAO, nullable = false)
    @NotNull
    public Localizacao getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = PAPEL)
    @NotNull
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = USUARIO, nullable = false)
    @NotNull
    public UsuarioLogin getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @Column(name = RESPONSAVEL_LOCALIZACAO, nullable = false)
    @NotNull
    public Boolean getResponsavelLocalizacao() {
        return this.responsavelLocalizacao;
    }

    public void setResponsavelLocalizacao(Boolean responsavelLocalizacao) {
        this.responsavelLocalizacao = responsavelLocalizacao;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ESTRUTURA)
    public Localizacao getEstrutura() {
        return estrutura;
    }

    public void setEstrutura(Localizacao estrutura) {
        this.estrutura = estrutura;
    }

    @Override
    public String toString() {
        try {
            return (estrutura == null ? "" : estrutura + "/") + localizacao
                    + "/" + papel;
        } catch (Exception e) {
            LOG.warn(".toString()", e);
            return super.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((idUsuarioLocalizacao == null) ? 0 : idUsuarioLocalizacao.hashCode());
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
        if (!(obj instanceof UsuarioLocalizacao)) {
            return false;
        }
        UsuarioLocalizacao other = (UsuarioLocalizacao) obj;
        if (idUsuarioLocalizacao == null) {
            if (other.idUsuarioLocalizacao != null) {
                return false;
            }
        } else if (!idUsuarioLocalizacao.equals(other.idUsuarioLocalizacao)) {
            return false;
        }
        return true;
    }

    public void setContabilizar(Boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    @Column(name = CONTABILIZAR, nullable = false)
    public Boolean getContabilizar() {
        return contabilizar;
    }
    
    @Transient
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String nome) {
        this.descricao = nome;
    }

}
