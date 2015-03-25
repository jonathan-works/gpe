package br.com.infox.epp.processo.prioridade.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_MEDIA;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.processo.prioridade.query.PrioridadeProcessoQuery;

@Entity
@Table(name = PrioridadeProcessoQuery.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = PrioridadeProcessoQuery.COLUMN_DESCRICAO))
@NamedQueries({ @NamedQuery(name = PrioridadeProcessoQuery.NAMED_QUERY_PRIORIDADES_ATIVAS, query = PrioridadeProcessoQuery.QUERY_PRIORIDADES_ATIVAS) })
public class PrioridadeProcesso implements Serializable, Comparable<PrioridadeProcesso> {


    private static final long serialVersionUID = 1L;

    private Integer idPrioridade;
    private String descricaoPrioridade;
    private Integer peso;
    private Boolean ativo;

    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_tb_prioridade_processo")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = PrioridadeProcessoQuery.COLUMN_ID)
    public Integer getIdPrioridade() {
        return idPrioridade;
    }

    public void setIdPrioridade(Integer idPrioridade) {
        this.idPrioridade = idPrioridade;
    }

    @Column(name = PrioridadeProcessoQuery.COLUMN_DESCRICAO, nullable = false)
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_MEDIA)
    public String getDescricaoPrioridade() {
        return descricaoPrioridade;
    }

    public void setDescricaoPrioridade(String descricaoPrioridade) {
        this.descricaoPrioridade = descricaoPrioridade;
    }

    @Column(name = PrioridadeProcessoQuery.COLUMN_NR_PESO, nullable = false)
    public Integer getPeso() {
        return peso;
    }

    public void setPeso(Integer peso) {
        this.peso = peso;
    }

    @Column(name = "in_ativo")
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricaoPrioridade;
    }

    @Override
    public int compareTo(PrioridadeProcesso o) {
        if ((this.peso != null) && (o != null) && (o.peso != null)) {
            return this.peso.compareTo(o.peso);
        } else if (this.peso != null) {
            // Este peso é maior que nulo
            return 1;
        } else if (o != null) {
            // Este peso é nulo e é menor que um peso não-nulo
            return -1;
        } else {
            // Dois pesos nulos são iguais
            return 0;
        }
    }
}
