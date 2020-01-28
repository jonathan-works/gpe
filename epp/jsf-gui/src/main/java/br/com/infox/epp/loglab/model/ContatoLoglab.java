package br.com.infox.epp.loglab.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Persister;

import br.com.infox.core.persistence.SchemaSingleTableEntityPersister;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ContatoLoglab.TABLE_NAME)
@Persister(impl = SchemaSingleTableEntityPersister.class)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class ContatoLoglab implements Serializable {

    public static final String TABLE_NAME = "tb_contato_loglab";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "ContatoLoglabGenerator";

    @Id
    @SequenceGenerator(name = GENERATOR, sequenceName = "sq_contato_loglab", allocationSize = 1, initialValue=1)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_contato_loglab", nullable = false, unique = true)
    private Long id;

	@NotNull
    @Column(name = "ds_email", nullable = false)
    private String email;

    @Column(name = "nr_telefone", nullable = true)
    private String telefone;

    @Column(name = "nr_celular", nullable = true)
    private String celular;

    @Column(name = "ds_cidade", nullable = true)
	private String cidade;

    @Column(name = "ds_logradouro", nullable = true)
	private String logradouro;

    @Column(name = "ds_bairro", nullable = true)
    private String bairro;

    @Column(name = "ds_complemento", nullable = true)
    private String complemento;

    @Column(name = "nr_residencia", nullable = true)
	private String numero;

    @Column(name = "nr_cep", nullable = true)
	private String cep;

    @Override
    public String toString() {
        return getEmail();
    }

}
