package br.com.infox.epp.tce.prestacaocontas.modelo.entity;

import java.io.Serializable;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_resp_mod_prest_contas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "id_modelo_prestacao_contas", "id_tipo_parte"
    })
})
public class ResponsavelModeloPrestacaoContas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ResponsavelModeloPrestacaoContasGenerator", allocationSize = 1, sequenceName = "sq_resp_mod_prest_contas")
    @GeneratedValue(generator = "ResponsavelModeloPrestacaoContasGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_resp_mod_prest_contas")
    private Long id;
    
    @NotNull
    @Column(name = "in_obrigatorio", nullable = false)
    private Boolean obrigatorio = false;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_modelo_prestacao_contas", nullable = false)
    private ModeloPrestacaoContas modeloPrestacaoContas;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_parte", nullable = false)
    private TipoParte tipoParte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public ModeloPrestacaoContas getModeloPrestacaoContas() {
        return modeloPrestacaoContas;
    }

    public void setModeloPrestacaoContas(ModeloPrestacaoContas modeloPrestacaoContas) {
        this.modeloPrestacaoContas = modeloPrestacaoContas;
    }

    public TipoParte getTipoParte() {
        return tipoParte;
    }

    public void setTipoParte(TipoParte tipoParte) {
        this.tipoParte = tipoParte;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime
                * result
                + ((getModeloPrestacaoContas() == null) ? 0 : getModeloPrestacaoContas()
                        .hashCode());
        result = prime * result
                + ((getTipoParte() == null) ? 0 : getTipoParte().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ResponsavelModeloPrestacaoContas))
            return false;
        ResponsavelModeloPrestacaoContas other = (ResponsavelModeloPrestacaoContas) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getModeloPrestacaoContas() == null) {
            if (other.getModeloPrestacaoContas() != null)
                return false;
        } else if (!getModeloPrestacaoContas().equals(other.getModeloPrestacaoContas()))
            return false;
        if (getTipoParte() == null) {
            if (other.getTipoParte() != null)
                return false;
        } else if (!getTipoParte().equals(other.getTipoParte()))
            return false;
        return true;
    }
}
