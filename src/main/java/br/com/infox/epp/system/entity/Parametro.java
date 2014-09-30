package br.com.infox.epp.system.entity;

import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO;
import static br.com.infox.epp.system.query.ParametroQuery.EXISTE_PARAMETRO_QUERY;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS;
import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS_QUERY;

import java.text.DateFormat;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = "tb_parametro")
@NamedQueries({ 
    @NamedQuery(name = LIST_PARAMETROS_ATIVOS, query = LIST_PARAMETROS_ATIVOS_QUERY),
    @NamedQuery(name = EXISTE_PARAMETRO, query = EXISTE_PARAMETRO_QUERY)
})
public class Parametro implements java.io.Serializable {

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
    @Column(name = "id_parametro", unique = true, nullable = false)
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

    @Column(name = "nm_variavel", nullable = false, length = LengthConstants.NOME_PADRAO, unique = true)
    @NotNull
    @Size(max = LengthConstants.NOME_PADRAO)
    public String getNomeVariavel() {
        return this.nomeVariavel;
    }

    public void setNomeVariavel(String nomeVariavel) {
        this.nomeVariavel = nomeVariavel;
    }

    @Column(name = "ds_variavel", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getDescricaoVariavel() {
        return this.descricaoVariavel;
    }

    public void setDescricaoVariavel(String descricaoVariavel) {
        this.descricaoVariavel = descricaoVariavel;
    }

    @Column(name = "vl_variavel", nullable = false)
    @NotNull
    public String getValorVariavel() {
        return this.valorVariavel;
    }

    public void setValorVariavel(String valorVariavel) {
        this.valorVariavel = valorVariavel;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_atualizacao")
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

    @Column(name = "in_sistema", nullable = false)
    @NotNull
    public Boolean getSistema() {
        return this.sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }

    @Column(name = "in_ativo", nullable = false)
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
        result = prime * result
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
