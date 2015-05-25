package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.DELETE_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.DELETE_BY_PASTA_QUERY;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA_QUERY;

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
@Table(name = PastaRestricao.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = GET_BY_PASTA, query = GET_BY_PASTA_QUERY),
    @NamedQuery(name = DELETE_BY_PASTA, query = DELETE_BY_PASTA_QUERY)
})
public class PastaRestricao {
    protected static final String TABLE_NAME = "tb_pasta_restricao";
    private static final String GENERATOR_NAME = "PastaRestricaoGenerator";
    private static final String SEQUENCE_NAME = "sq_pasta_restricao";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_pasta_restricao", nullable = false, unique = true)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasta", nullable = false)
    private Pasta pasta;
    
    @Column(name = "id_alvo", nullable = true)
    private Integer alvo;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_restricao", nullable = false)
    private PastaRestricaoEnum tipoPastaRestricao;
    
    @NotNull
    @Column(name = "in_read", nullable = false)
    private Boolean read;
    
    @NotNull
    @Column(name = "in_write", nullable = false)
    private Boolean write;
    
    @NotNull
    @Column(name = "in_delete", nullable = false)
    private Boolean delete;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pasta getPasta() {
        return pasta;
    }

    public void setPasta(Pasta pasta) {
        this.pasta = pasta;
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

    public PastaRestricaoEnum getTipoPastaRestricao() {
        return tipoPastaRestricao;
    }

    public void setTipoPastaRestricao(PastaRestricaoEnum tipoPastaRestricao) {
        this.tipoPastaRestricao = tipoPastaRestricao;
    }
}
