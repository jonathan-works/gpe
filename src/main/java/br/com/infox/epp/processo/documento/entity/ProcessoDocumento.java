package br.com.infox.epp.processo.documento.entity;

// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_QUERY;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_PROCESSO_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_PROCESSO_DOCUMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL_QUERY;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.entity.Processo;

/**
 * ProcessoDocumento generated by hbm2java
 */
@Entity
@Table(name = "tb_processo_documento")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(name = LIST_ANEXOS_PUBLICOS, query = LIST_ANEXOS_PUBLICOS_QUERY),
    @NamedQuery(name = NEXT_SEQUENCIAL, query = NEXT_SEQUENCIAL_QUERY),
    @NamedQuery(name = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO, query = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY),
    @NamedQuery(name = LIST_PROCESSO_DOCUMENTO_BY_PROCESSO, query = LIST_PROCESSO_DOCUMENTO_BY_PROCESSO_QUERY)
})
@Indexed(index="IndexProcessoDocumento")
public class ProcessoDocumento implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_processo_documento")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_documento", unique = true, nullable = false)
    @NotNull
    private int idProcessoDocumento;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    private TipoProcessoDocumento tipoProcessoDocumento;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_processo_documento_bin", nullable = false)
    private ProcessoDocumentoBin processoDocumentoBin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_inclusao")
    private UsuarioLogin usuarioInclusao;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo processo;
    
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    @Column(name = "ds_processo_documento", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    private String processoDocumento;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    private Date dataInclusao = new Date();
    
    @Column(name = "nr_documento", nullable = true)
    private Integer numeroDocumento;
    
    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;
    
    @NotNull
    @Column(name = "in_documento_sigiloso", nullable = false)
    private Boolean documentoSigiloso = Boolean.FALSE;
    
    @NotNull
    @Column(name = "in_anexo", nullable = false)
    private Boolean anexo = Boolean.FALSE;
    
    @Column(name = "id_jbpm_task")
    private Long idJbpmTask;
    
    @JoinColumn(name = "id_papel")
    @ManyToOne(fetch = FetchType.LAZY)
    private Papel papel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_alteracao")
    private UsuarioLogin usuarioAlteracao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;
    
    public ProcessoDocumento() {
    }

    public int getIdProcessoDocumento() {
        return this.idProcessoDocumento;
    }

    public void setIdProcessoDocumento(int idProcessoDocumento) {
        this.idProcessoDocumento = idProcessoDocumento;
    }

    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return this.tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(
            TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }

    public Integer getNumeroDocumento() {
        return this.numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return this.processoDocumentoBin;
    }

    public void setProcessoDocumentoBin(
            ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    public UsuarioLogin getUsuarioInclusao() {
        return this.usuarioInclusao;
    }

    public void setUsuarioInclusao(UsuarioLogin usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }

    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getProcessoDocumento() {
        return this.processoDocumento;
    }

    public void setProcessoDocumento(String processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    public Date getDataInclusao() {
        return this.dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDocumentoSigiloso() {
        return this.documentoSigiloso;
    }

    public void setDocumentoSigiloso(Boolean documentoSigiloso) {
        this.documentoSigiloso = documentoSigiloso;
    }

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

    public Long getIdJbpmTask() {
        return idJbpmTask;
    }

    public void setIdJbpmTask(Long idJbpmTask) {
        this.idJbpmTask = idJbpmTask;
    }

    public Papel getPapel() {
        return this.papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public UsuarioLogin getUsuarioAlteracao() {
        return this.usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
    
    public boolean isDocumentoAssinavel(Papel papel){
    	List<TipoProcessoDocumentoPapel> papeis = getTipoProcessoDocumento().getTipoProcessoDocumentoPapeis();
		for (TipoProcessoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getPapel().equals(papel) 
					&& tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinavel(){
    	List<TipoProcessoDocumentoPapel> papeis = getTipoProcessoDocumento().getTipoProcessoDocumentoPapeis();
		for (TipoProcessoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinado(Papel papel){
    	for(AssinaturaDocumento assinaturaDocumento : getProcessoDocumentoBin().getAssinaturas()){
    		if (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasAssinatura(){
    	return getProcessoDocumentoBin().getAssinaturas() != null && 
    			getProcessoDocumentoBin().getAssinaturas().size() > 0;
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
