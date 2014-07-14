package br.com.infox.epp.documento.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="tb_extensao_arquivo")
public class ExtensaoArquivo {
    
    private Integer idExtensaoArquivo;
    private TipoProcessoDocumento tipoProcessoDocumento;
    private String nomeExtensao;
    private String extensao;
    private Integer tamanho;
    private Boolean paginavel;
    private Integer tamanhoPorPagina;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_tb_extensao_arquivo")
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_extensao_arquivo", unique = true, nullable = false)
    public Integer getIdExtensaoArquivo() {
        return idExtensaoArquivo;
    }
    
    public void setIdExtensaoArquivo(Integer idExtensaoArquivo) {
        this.idExtensaoArquivo = idExtensaoArquivo;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    @NotNull
    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }

    @Column(name = "nm_extensao", nullable = false, length = DESCRICAO_PEQUENA, unique = true)
    @Size(min = FLAG, max = DESCRICAO_PEQUENA)
    @NotNull
    public String getNomeExtensao() {
        return nomeExtensao;
    }
    
    public void setNomeExtensao(String nomeExtensao) {
        this.nomeExtensao = nomeExtensao;
    }

    @Column(name = "ds_extensao", nullable = false, length = DESCRICAO_PEQUENA, unique = true)
    @Size(min = FLAG, max = DESCRICAO_PEQUENA)
    @NotNull
    public String getExtensao() {
        return extensao;
    }

    public void setExtensao(String extensao) {
        this.extensao = extensao;
    }
    
    @Column(name = "nr_tamanho", nullable = false)
    @NotNull
    public Integer getTamanho() {
        return this.tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    @Column(name = "in_paginavel", nullable = false)
    @NotNull
    public Boolean getPaginavel() {
        return paginavel;
    }

    public void setPaginavel(Boolean paginavel) {
        this.paginavel = paginavel;
    }

    @Column(name = "nr_tamanho_pagina", nullable = true)
    public Integer getTamanhoPorPagina() {
        return tamanhoPorPagina;
    }

    public void setTamanhoPorPagina(Integer tamanhoPorPagina) {
        this.tamanhoPorPagina = tamanhoPorPagina;
    }

}
