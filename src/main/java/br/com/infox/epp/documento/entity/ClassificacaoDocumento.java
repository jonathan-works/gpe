package br.com.infox.epp.documento.entity;

import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO_QUERY;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO_QUERY;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery.LIST_CLASSIFICACAO_DOCUMENTO_QUERY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.processo.documento.entity.Documento;

@Entity
@Table(name = ClassificacaoDocumento.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = LIST_CLASSIFICACAO_DOCUMENTO, query = LIST_CLASSIFICACAO_DOCUMENTO_QUERY),
    @NamedQuery(name = FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO, query = FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO_QUERY),
    @NamedQuery(name = FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO, query = FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO_QUERY),
    @NamedQuery(name = LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO, query = LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO_QUERY)
})
public class ClassificacaoDocumento implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_classificacao_documento";
    private static final String COL_ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_classificacao_documento")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_classificacao_documento", unique = true, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = LengthConstants.FLAG, max = LengthConstants.DESCRICAO_PADRAO)
    @Column(name = "ds_classificacao_documento", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    private String descricao;

    @Size(min = LengthConstants.FLAG, max = LengthConstants.CODIGO_DOCUMENTO)
    @Column(name = "cd_documento", length = LengthConstants.CODIGO_DOCUMENTO)
    private String codigoDocumento;

    @Size(max = LengthConstants.TEXTO)
    @Column(name = "ds_observacao", length = LengthConstants.DESCRICAO_PADRAO_DOBRO)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_tipo_documento")
    private TipoDocumentoEnum inTipoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_numeracao")
    private TipoNumeracaoEnum tipoNumeracao = TipoNumeracaoEnum.S;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "classificacaoDocumento")
    private List<ExtensaoArquivo> extensaoArquivosList;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_visibilidade", nullable = false)
    private VisibilidadeEnum visibilidade;

    @NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "in_sistema")
    private Boolean sistema = Boolean.FALSE;

    @NotNull
    @Column(name = "in_publico", nullable = false)
    private Boolean publico;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classificacaoDocumento")
    private List<Documento> documentoList = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classificacaoDocumento")
    private List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapelList = new ArrayList<>(0);
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_ID_TIPO_MODELO_DOCUMENTO, nullable = false)
    private TipoModeloDocumento tipoModeloDocumento;
    
	public ClassificacaoDocumento() {
        visibilidade = VisibilidadeEnum.A;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoDocumentoEnum getInTipoDocumento() {
        return inTipoDocumento;
    }

    public void setInTipoDocumento(TipoDocumentoEnum inTipoDocumento) {
        this.inTipoDocumento = inTipoDocumento;
    }

    public TipoNumeracaoEnum getTipoNumeracao() {
        return tipoNumeracao;
    }

    public void setTipoNumeracao(TipoNumeracaoEnum tipoNumeracao) {
        this.tipoNumeracao = tipoNumeracao;
    }

    public List<ExtensaoArquivo> getExtensaoArquivosList() {
        return extensaoArquivosList;
    }

    public void setExtensaoArquivosList(List<ExtensaoArquivo> extensaoArquivosList) {
        this.extensaoArquivosList = extensaoArquivosList;
    }

    public VisibilidadeEnum getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(VisibilidadeEnum visibilidade) {
        this.visibilidade = visibilidade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getSistema() {
        return sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

    public List<Documento> getDocumentoList() {
        return documentoList;
    }

    public void setDocumentoList(List<Documento> documentoList) {
        this.documentoList = documentoList;
    }

    public List<ClassificacaoDocumentoPapel> getClassificacaoDocumentoPapelList() {
        return classificacaoDocumentoPapelList;
    }

    public void setClassificacaoDocumentoPapelList(List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapelList) {
        this.classificacaoDocumentoPapelList = classificacaoDocumentoPapelList;
    }

    public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
    @Override
    public String toString() {
        return descricao;
    }

    @Transient
    public String getAcceptedTypes() {
        String accepted = "";
        if (getExtensaoArquivosList().isEmpty()) {
            return accepted;
        }
        for (ExtensaoArquivo ea : getExtensaoArquivosList()) {
        	String extensao = ea.getExtensao();
        	if(!extensao.startsWith(".")) {
        		extensao = "." + extensao;
        	}
            accepted +=  extensao + ", ";
        }
        return accepted.substring(0, accepted.length() -2);
    }

    @Transient
    public List<String> getAcceptedTypesList(){
        if (getExtensaoArquivosList().isEmpty()){
            return new ArrayList<String>(1);
        }
        List<String> acceptedTypes = new ArrayList<>(getExtensaoArquivosList().size());
        StringBuilder stringBuilder = null;
        for (ExtensaoArquivo ea : getExtensaoArquivosList()){
            stringBuilder = new StringBuilder();
            stringBuilder.append(ea.getNomeExtensao().toUpperCase());
            stringBuilder.append("(").append(ea.getTamanho()).append(" Kb");
            if (ea.getPaginavel()){
                stringBuilder.append(" / ").append(ea.getTamanhoPorPagina()).append(" Kb");
                stringBuilder.append(" por Página");
            }
            stringBuilder.append(")");
            acceptedTypes.add(stringBuilder.toString());
        }
        return acceptedTypes;
    }

    public boolean canDoUpload() {
        return !(this.inTipoDocumento == TipoDocumentoEnum.P);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime
                * result)
                + ((getId() == null) ? 0 : getId().hashCode());
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
        if (!(obj instanceof ClassificacaoDocumento)) {
            return false;
        }
        ClassificacaoDocumento other = (ClassificacaoDocumento) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

}
