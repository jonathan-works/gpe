package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.DELETE_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.DELETE_BY_PASTA_QUERY;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA_ALVO_TIPO_RESTRICAO;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA_ALVO_TIPO_RESTRICAO_QUERY;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = PastaRestricao.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = GET_BY_PASTA, query = GET_BY_PASTA_QUERY),
    @NamedQuery(name = DELETE_BY_PASTA, query = DELETE_BY_PASTA_QUERY),
    @NamedQuery(name = GET_BY_PASTA_ALVO_TIPO_RESTRICAO, query = GET_BY_PASTA_ALVO_TIPO_RESTRICAO_QUERY)
})
@Getter
@Setter
@EqualsAndHashCode(of="id")
public class PastaRestricao implements br.com.infox.epp.documento.domain.Pasta.Permissao {
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
    
    @NotNull
    @Column(name = "in_logic_delete", nullable = false)
    private Boolean logicDelete;
    
    public PastaRestricao makeCopy() throws CloneNotSupportedException {
        PastaRestricao nova = new PastaRestricao();
        nova.setId(null);
        nova.setPasta(null);
        nova.setTipoPastaRestricao(this.getTipoPastaRestricao());
        nova.setAlvo(this.getAlvo());
        nova.setRead(this.getRead());
        nova.setWrite(this.getWrite());
        nova.setDelete(this.getDelete());
        nova.setLogicDelete(this.getLogicDelete());
        return nova;
    }
    
}
