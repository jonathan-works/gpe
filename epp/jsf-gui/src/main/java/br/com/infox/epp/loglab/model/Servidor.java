package br.com.infox.epp.loglab.model;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Persister;

import br.com.infox.core.persistence.SchemaSingleTableEntityPersister;
import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Servidor.TABLE_NAME)
@Persister(impl = SchemaSingleTableEntityPersister.class)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class Servidor implements Serializable {

    public static final String TABLE_NAME = "tb_servidor";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "ServidorGenerator";

    @Id
    @SequenceGenerator(name = GENERATOR, sequenceName = "sq_servidor", allocationSize = 1, initialValue=1)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_servidor", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_pessoa_fisica", nullable = false)
    @NotNull
    private PessoaFisica pessoaFisica;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_contato_loglab", nullable = false)
    @NotNull
    private ContatoLoglab contatoLoglab;

	@NotNull
	@Cpf
    @Column(name = "nr_cpf", nullable = false)
	private String cpf;

	@NotNull
	@Size(min = 6, max = 256)
    @Column(name = "nm_servidor", nullable = false)
	private String nomeCompleto;

	@NotNull
	@Size(min = 3, max = 256)
    @Column(name = "ds_cargo_funcao", nullable = false)
	private String cargoFuncao;

	@NotNull
    @Column(name = "ds_email", nullable = false)
    private String email;

    @Column(name = "nr_telefone", nullable = true)
    private String telefone;

    @Column(name = "ds_secretaria", nullable = true)
	private String secretaria;

    @Column(name = "ds_departamento", nullable = true)
	private String departamento;

    @Override
    public String toString() {
        return getCpf() + " - " + getNomeCompleto();
    }

}
