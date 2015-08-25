package br.com.infox.epp.fluxo.entity;

import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.DELETE_BY_MODELO_PASTA;
import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.DELETE_BY_MODELO_PASTA_QUERY;
import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.GET_BY_MODELO_PASTA;
import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.GET_BY_MODELO_PASTA_QUERY;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

@Entity
@Table(name = ModeloPastaRestricao.TABLE_NAME)
@NamedQueries({
	@NamedQuery(name = GET_BY_MODELO_PASTA, query = GET_BY_MODELO_PASTA_QUERY),
	@NamedQuery(name = DELETE_BY_MODELO_PASTA, query = DELETE_BY_MODELO_PASTA_QUERY)
})
public class ModeloPastaRestricao {
    protected static final String TABLE_NAME = "tb_modelo_pasta_restricao";
    private static final String GENERATOR_NAME = "ModeloPastaRestricaoGenerator";
    private static final String SEQUENCE_NAME = "sq_modelo_pasta_restricao";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_modelo_pasta_restricao", nullable = false, unique = true)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modelo_pasta", nullable = false)
    private ModeloPasta modeloPasta;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_pasta_restricao", nullable = false)
    private PastaRestricaoEnum tipoPastaRestricao;
    
    @Column(name = "id_alvo", nullable = true)
    private Integer alvo;
    
    @NotNull
    @Column(name = "in_read", nullable = false)
    private Boolean read;
    
    @NotNull
    @Column(name = "in_write", nullable = false)
    private Boolean write;
    
    @NotNull
    @Column(name = "in_delete", nullable = false)
    private Boolean delete;
    
    @NotNull
    @Column(name = "in_logic_delete", nullable = false)
    private Boolean logicDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModeloPasta getModeloPasta() {
        return modeloPasta;
    }

    public void setModeloPasta(ModeloPasta modeloPasta) {
        this.modeloPasta = modeloPasta;
    }

    public PastaRestricaoEnum getTipoPastaRestricao() {
        return tipoPastaRestricao;
    }

    public void setTipoPastaRestricao(PastaRestricaoEnum tipoPastaRestricao) {
        this.tipoPastaRestricao = tipoPastaRestricao;
    }

    public Integer getAlvo() {
        return alvo;
    }

    public void setAlvo(Integer alvo) {
        this.alvo = alvo;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }

    public void setWrite(Boolean write) {
        this.write = write;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        this.logicDelete = logicDelete;
    }
}