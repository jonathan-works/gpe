package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.CONTEUDO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.DATA_ALTERACAO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.ID_HISTORICO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.ID_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.ID_USUARIO_ALTERACAO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_MODELO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_MODELO_QUERY;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_USUARIO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_USUARIO_QUERY;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.SEQUENCE_HISTORICO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.TABLE_HISTORICO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.TITULO_MODELO_DOCUMENTO;

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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = TABLE_HISTORICO_MODELO_DOCUMENTO)
@NamedQueries(value = {
    @NamedQuery(name = LIST_MODELO, query = LIST_MODELO_QUERY),
    @NamedQuery(name = LIST_USUARIO, query = LIST_USUARIO_QUERY) })
public class HistoricoModeloDocumento implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_HISTORICO_MODELO_DOCUMENTO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_HISTORICO_MODELO_DOCUMENTO, unique = true, nullable = false)
    private int idHistoricoModeloDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_MODELO_DOCUMENTO)
    private ModeloDocumento modeloDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_USUARIO_ALTERACAO)
    private UsuarioLogin usuarioAlteracao;
    
    @Column(name = DATA_ALTERACAO, nullable = false)
    private Date dataAlteracao;
    
    @Column(name = TITULO_MODELO_DOCUMENTO, nullable = false, length = 250)
    @Size(max = 250)
    private String tituloModeloDocumento;
    
    @Column(name = CONTEUDO_MODELO_DOCUMENTO, nullable = false)
    private String descricaoModeloDocumento;
    
    @Column(name = ATIVO, nullable = false)
    private Boolean ativo;

    public int getIdHistoricoModeloDocumento() {
        return idHistoricoModeloDocumento;
    }

    public void setIdHistoricoModeloDocumento(int idHistoricoModeloDocumento) {
        this.idHistoricoModeloDocumento = idHistoricoModeloDocumento;
    }

    public ModeloDocumento getModeloDocumento() {
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public UsuarioLogin getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Transient
    public String getDataAlteracaoFormatada() {
        if (dataAlteracao == null) {
            return "";
        }
        return DateFormat.getDateInstance().format(dataAlteracao);
    }

    public String getTituloModeloDocumento() {
        return tituloModeloDocumento;
    }

    public void setTituloModeloDocumento(String tituloModeloDocumento) {
        this.tituloModeloDocumento = tituloModeloDocumento;
    }

    public String getDescricaoModeloDocumento() {
        return descricaoModeloDocumento;
    }

    public void setDescricaoModeloDocumento(String descricaoModeloDocumento) {
        this.descricaoModeloDocumento = descricaoModeloDocumento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return this.tituloModeloDocumento;
    }

}
