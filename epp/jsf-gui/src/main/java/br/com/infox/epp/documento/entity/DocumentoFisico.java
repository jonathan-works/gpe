package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.DOCUMENTO_FISICO;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.ID_DOCUMENTO_FISICO;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.ID_LOCALIZACAO_FISICA;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.ID_PROCESSO;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.LIST_BY_PROCESSO;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.LIST_BY_PROCESSO_QUERY;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.SEQUENCE_DOCUMENTO_FISICO;
import static br.com.infox.epp.documento.query.DocumentoFisicoQuery.TABLE_DOCUMENTO_FISICO;

import java.io.Serializable;

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
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = TABLE_DOCUMENTO_FISICO)
@NamedQueries(value = { @NamedQuery(name = LIST_BY_PROCESSO, query = LIST_BY_PROCESSO_QUERY) })
public class DocumentoFisico implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idDocumentoFisico;
    private LocalizacaoFisica localizacaoFisica;
    private Processo processo;
    private String descricaoDocumentoFisico;
    private Boolean ativo = true;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_DOCUMENTO_FISICO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_DOCUMENTO_FISICO, unique = true, nullable = false)
    public Integer getIdDocumentoFisico() {
        return idDocumentoFisico;
    }

    public void setIdDocumentoFisico(Integer idDocumentoFisico) {
        this.idDocumentoFisico = idDocumentoFisico;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_LOCALIZACAO_FISICA)
    public LocalizacaoFisica getLocalizacaoFisica() {
        return localizacaoFisica;
    }

    public void setLocalizacaoFisica(LocalizacaoFisica localizacaoFisica) {
        this.localizacaoFisica = localizacaoFisica;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_PROCESSO)
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Column(name = DOCUMENTO_FISICO, nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getDescricaoDocumentoFisico() {
        return descricaoDocumentoFisico;
    }

    public void setDescricaoDocumentoFisico(String descricaoDocumentoFisico) {
        this.descricaoDocumentoFisico = descricaoDocumentoFisico;
    }

    @Column(name = ATIVO, nullable = false)
    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
