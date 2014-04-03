package br.com.infox.epp.documento.entity;

import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.LIST_TIPO_PROCESSO_DOCUMENTO;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.LIST_TIPO_PROCESSO_DOCUMENTO_QUERY;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_USEABLE;
import static br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_USEABLE_QUERY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Entity
@Table(name = "tb_tipo_processo_documento", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(name = LIST_TIPO_PROCESSO_DOCUMENTO, query = LIST_TIPO_PROCESSO_DOCUMENTO_QUERY),
    @NamedQuery(name = TIPO_PROCESSO_DOCUMENTO_USEABLE, query = TIPO_PROCESSO_DOCUMENTO_USEABLE_QUERY) })
public class TipoProcessoDocumento implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTipoProcessoDocumento;
    private String tipoProcessoDocumento;
    private String codigoDocumento;
    private String tipoProcessoDocumentoObservacao;
    private TipoDocumentoEnum inTipoDocumento;
    private TipoNumeracaoEnum tipoNumeracao;
    private VisibilidadeEnum visibilidade;
    private Boolean ativo;
    private Boolean sistema = Boolean.FALSE;

    private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);

    private Boolean publico;

    public TipoProcessoDocumento() {
        visibilidade = VisibilidadeEnum.A;
    }

    @SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tipo_processo_documento")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id_tipo_processo_documento", unique = true, nullable = false)
    public Integer getIdTipoProcessoDocumento() {
        return this.idTipoProcessoDocumento;
    }

    public void setIdTipoProcessoDocumento(Integer idTipoProcessoDocumento) {
        this.idTipoProcessoDocumento = idTipoProcessoDocumento;
    }

    @Column(name = "ds_tipo_processo_documento", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getTipoProcessoDocumento() {
        return this.tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(String tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }

    @Column(name = "cd_documento", length = LengthConstants.CODIGO_DOCUMENTO)
    @Size(max = LengthConstants.CODIGO_DOCUMENTO)
    public String getCodigoDocumento() {
        return this.codigoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    @Column(name = "in_publico", nullable = false)
    @NotNull
    public Boolean getPublico() {
        return this.publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

    @Column(name = "in_ativo", nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "tipoProcessoDocumento")
    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return this.processoDocumentoList;
    }

    public void setProcessoDocumentoList(
            List<ProcessoDocumento> processoDocumentoList) {
        this.processoDocumentoList = processoDocumentoList;
    }

    @Column(name = "in_tipo_documento")
    @Enumerated(EnumType.STRING)
    public TipoDocumentoEnum getInTipoDocumento() {
        return this.inTipoDocumento;
    }

    public void setInTipoDocumento(TipoDocumentoEnum inTipoDocumento) {
        this.inTipoDocumento = inTipoDocumento;
    }

    @Column(name = "tp_visibilidade", nullable = false)
    @Enumerated(EnumType.STRING)
    public VisibilidadeEnum getVisibilidade() {
        return this.visibilidade;
    }

    public void setVisibilidade(VisibilidadeEnum visibilidade) {
        this.visibilidade = visibilidade;
    }

    @Column(name = "ds_tipo_processo_documento_observacao", length = LengthConstants.DESCRICAO_PADRAO_DOBRO)
    @Size(max = LengthConstants.DESCRICAO_PADRAO_DOBRO)
    public String getTipoProcessoDocumentoObservacao() {
        return this.tipoProcessoDocumentoObservacao;
    }

    public void setTipoProcessoDocumentoObservacao(
            String tipoProcessoDocumentoObservacao) {
        this.tipoProcessoDocumentoObservacao = tipoProcessoDocumentoObservacao;
    }

    @Column(name = "tp_numeracao")
    @Enumerated(EnumType.STRING)
    public TipoNumeracaoEnum getTipoNumeracao() {
        return tipoNumeracao;
    }

    public void setTipoNumeracao(TipoNumeracaoEnum tipoNumeracao) {
        this.tipoNumeracao = tipoNumeracao;
    }

    @Column(name = "in_sistema")
    public Boolean getSistema() {
        return sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }

    @Override
    public String toString() {
        return tipoProcessoDocumento;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((idTipoProcessoDocumento == null) ? 0 : idTipoProcessoDocumento.hashCode());
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
        if (!(obj instanceof TipoProcessoDocumento)) {
            return false;
        }
        TipoProcessoDocumento other = (TipoProcessoDocumento) obj;
        if (idTipoProcessoDocumento == null) {
            if (other.getIdTipoProcessoDocumento() != null) {
                return false;
            }
        } else if (!idTipoProcessoDocumento.equals(other.getIdTipoProcessoDocumento())) {
            return false;
        }
        return true;
    }

}
