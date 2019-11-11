package br.com.infox.epp.system.entity;

import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_DATA_ATUALIZACAO;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_DESCRICAO;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_ID;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_IN_ATIVO;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_IN_SISTEMA;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_NOME;
import static br.com.infox.epp.system.query.ParametroQuery.COLUMN_VALOR;
import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO;
import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO_QUERY;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS_QUERY;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_NOME;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_NOME_QUERY;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_VALOR;
import static br.com.infox.epp.system.query.ParametroQuery.PARAMETRO_BY_VALOR_QUERY;
import static br.com.infox.epp.system.query.ParametroQuery.TABLE_NAME;

import java.io.Serializable;
import java.text.DateFormat;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = COLUMN_NOME))
@NamedQueries({
    @NamedQuery(name = LIST_PARAMETROS_ATIVOS, query = LIST_PARAMETROS_ATIVOS_QUERY),
    @NamedQuery(name = EXISTE_PARAMETRO, query = EXISTE_PARAMETRO_QUERY),
    @NamedQuery(name = PARAMETRO_BY_NOME, query = PARAMETRO_BY_NOME_QUERY,
    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
            @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.system.entity.Parametro")}),
            @NamedQuery(name = PARAMETRO_BY_VALOR, query = PARAMETRO_BY_VALOR_QUERY,
            hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
                    @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.system.entity.Parametro")})
})
@Cacheable
public class Parametro implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idParametro;
    private String nomeVariavel;
    private String descricaoVariavel;
    private String valorVariavel;
    private Date dataAtualizacao = new Date();
    private Boolean sistema;
    private UsuarioLogin usuarioModificacao;
    private Boolean ativo;

    public Parametro() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_parametro")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = COLUMN_ID, unique = true, nullable = false)
    public Integer getIdParametro() {
        return this.idParametro;
    }

    public void setIdParametro(Integer idParametro) {
        this.idParametro = idParametro;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_modificacao", nullable = true)
    public UsuarioLogin getUsuarioModificacao() {
        return this.usuarioModificacao;
    }

    public void setUsuarioModificacao(UsuarioLogin usuarioModificacao) {
        this.usuarioModificacao = usuarioModificacao;
    }

    @Column(name = COLUMN_NOME, nullable = false, length = LengthConstants.NOME_PADRAO, unique = true)
    @NotNull
    @Size(min = LengthConstants.FLAG, max = LengthConstants.NOME_PADRAO)
    public String getNomeVariavel() {
        return this.nomeVariavel;
    }

    public void setNomeVariavel(String nomeVariavel) {
        this.nomeVariavel = nomeVariavel;
    }

    @Column(name = COLUMN_DESCRICAO, nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @NotNull
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_PADRAO)
    public String getDescricaoVariavel() {
        return this.descricaoVariavel;
    }

    public void setDescricaoVariavel(String descricaoVariavel) {
        this.descricaoVariavel = descricaoVariavel;
    }

    @Column(name = COLUMN_VALOR)
    public String getValorVariavel() {
        return this.valorVariavel;
    }

    public void setValorVariavel(String valorVariavel) {
        this.valorVariavel = valorVariavel;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = COLUMN_DATA_ATUALIZACAO)
    public Date getDataAtualizacao() {
        return this.dataAtualizacao;
    }

    @Transient
    public String getDataAtualizacaoFormatada() {
        return DateFormat.getDateInstance().format(dataAtualizacao);
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Column(name = COLUMN_IN_SISTEMA, nullable = false)
    @NotNull
    public Boolean getSistema() {
        return this.sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }

    @Column(name = COLUMN_IN_ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return nomeVariavel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((idParametro == null) ? 0 : idParametro.hashCode());
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
        if (!(obj instanceof Parametro)) {
            return false;
        }
        Parametro other = (Parametro) obj;
        if (idParametro == null) {
            if (other.idParametro != null) {
                return false;
            }
        } else if (!idParametro.equals(other.idParametro)) {
            return false;
        }
        return true;
    }

}
