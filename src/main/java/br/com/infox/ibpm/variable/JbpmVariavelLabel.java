package br.com.infox.ibpm.variable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = "tb_jbpm_variavel_label")
public class JbpmVariavelLabel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idJbpmVariavelLabel;
    private String nomeVariavel;
    private String labelVariavel;

    public JbpmVariavelLabel() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_jbpm_variavel_label")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_jbpm_variavel_label", unique = true, nullable = false)
    public int getIdJbpmVariavelLabel() {
        return this.idJbpmVariavelLabel;
    }

    public void setIdJbpmVariavelLabel(int idJbpmVariavelLabel) {
        this.idJbpmVariavelLabel = idJbpmVariavelLabel;
    }

    @Column(name = "nm_variavel", nullable = false, length = LengthConstants.NOME_PADRAO, unique = true)
    @Size(max = LengthConstants.NOME_PADRAO)
    @NotNull
    public String getNomeVariavel() {
        return this.nomeVariavel;
    }

    public void setNomeVariavel(String variavel) {
        this.nomeVariavel = variavel;
    }

    @Column(name = "ds_label_variavel", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    @Size(max = LengthConstants.NOME_PADRAO)
    @NotNull
    public String getLabelVariavel() {
        return this.labelVariavel;
    }

    public void setLabelVariavel(String valorVariavel) {
        this.labelVariavel = valorVariavel;
    }

    @Override
    public String toString() {
        return nomeVariavel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JbpmVariavelLabel)) {
            return false;
        }
        JbpmVariavelLabel other = (JbpmVariavelLabel) obj;
        if (getIdJbpmVariavelLabel() != other.getIdJbpmVariavelLabel()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdJbpmVariavelLabel();
        return result;
    }
}
