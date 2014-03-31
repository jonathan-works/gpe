package br.com.infox.epp.processo.documento.entity;

// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_QUERY;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL_QUERY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;

/**
 * ProcessoDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_processo_documento", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(name = LIST_ANEXOS_PUBLICOS, query = LIST_ANEXOS_PUBLICOS_QUERY),
    @NamedQuery(name = NEXT_SEQUENCIAL, query = NEXT_SEQUENCIAL_QUERY) })
@Analyzer(impl = BrazilianAnalyzer.class)
@Indexed(index="IndexProcessoDocumento")
public class ProcessoDocumento implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Long idJbpmTask;
    private int idProcessoDocumento;
    private TipoProcessoDocumento tipoProcessoDocumento;
    private ProcessoDocumentoBin processoDocumentoBin;
    private UsuarioLogin usuarioInclusao;
    private Processo processo;
    private String processoDocumento;
    private Date dataInclusao = new Date();
    private Integer numeroDocumento;
    private Boolean ativo = Boolean.TRUE;
    private Boolean documentoSigiloso = Boolean.FALSE;
    private Boolean anexo = Boolean.FALSE;
    private Papel papel;
    private UsuarioLogin usuarioAlteracao;
    private Localizacao localizacao;

    public ProcessoDocumento() {
    }

    @SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo_documento")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id_processo_documento", unique = true, nullable = false)
    @NotNull
    public int getIdProcessoDocumento() {
        return this.idProcessoDocumento;
    }

    public void setIdProcessoDocumento(int idProcessoDocumento) {
        this.idProcessoDocumento = idProcessoDocumento;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    @NotNull
    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return this.tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(
            TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }

    @Column(name = "nr_documento", nullable = true)
    public Integer getNumeroDocumento() {
        return this.numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_documento_bin", nullable = false)
    @NotNull
    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return this.processoDocumentoBin;
    }

    public void setProcessoDocumentoBin(
            ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_inclusao")
    public UsuarioLogin getUsuarioInclusao() {
        return this.usuarioInclusao;
    }

    public void setUsuarioInclusao(UsuarioLogin usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    @NotNull
    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Column(name = "ds_processo_documento", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getProcessoDocumento() {
        return this.processoDocumento;
    }

    public void setProcessoDocumento(String processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    @NotNull
    public Date getDataInclusao() {
        return this.dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    @Column(name = "in_ativo", nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Column(name = "in_documento_sigiloso", nullable = false)
    @NotNull
    public Boolean getDocumentoSigiloso() {
        return this.documentoSigiloso;
    }

    public void setDocumentoSigiloso(Boolean documentoSigiloso) {
        this.documentoSigiloso = documentoSigiloso;
    }

    @Column(name = "in_anexo", nullable = false)
    @NotNull
    public Boolean getAnexo() {
        return anexo;
    }

    public void setAnexo(Boolean anexo) {
        this.anexo = anexo;
    }

    @Override
    public String toString() {
        return processoDocumento;
    }

    @Column(name = "id_jbpm_task")
    public Long getIdJbpmTask() {
        return idJbpmTask;
    }

    public void setIdJbpmTask(Long idJbpmTask) {
        this.idJbpmTask = idJbpmTask;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_papel")
    public Papel getPapel() {
        return this.papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_alteracao")
    public UsuarioLogin getUsuarioAlteracao() {
        return this.usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao")
    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProcessoDocumento)) {
            return false;
        }
        ProcessoDocumento other = (ProcessoDocumento) obj;
        if (getIdProcessoDocumento() != other.getIdProcessoDocumento()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdProcessoDocumento();
        return result;
    }
    
    @Transient
    @Field(index = Index.YES, store = Store.NO, name = "texto")
    public String getTextoIndexavel() {
        return getProcessoDocumentoBin().getModeloDocumento();
    }

}
