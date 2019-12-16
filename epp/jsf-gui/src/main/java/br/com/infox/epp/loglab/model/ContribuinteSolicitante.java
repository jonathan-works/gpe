package br.com.infox.epp.loglab.model;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Persister;

import br.com.infox.core.persistence.SchemaSingleTableEntityPersister;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.util.time.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ContribuinteSolicitante.TABLE_NAME)
@Persister(impl = SchemaSingleTableEntityPersister.class)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class ContribuinteSolicitante implements Serializable {

    public static final String TABLE_NAME = "tb_contribuinte";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "ContribuinteGenerator";

    @Id
    @SequenceGenerator(name = GENERATOR, sequenceName = "sq_contribuinte", allocationSize = 1, initialValue=1)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_contribuinte", nullable = false, unique = true)
    private Long id;

	@NotNull
	@Cpf
    @Column(name = "nr_cpf", nullable = false)
	private String cpf;

    @Column(name = "nr_matricula", nullable = true)
	private String matricula;

	@NotNull
	@Size(min = 6, max = 256)
    @Column(name = "nm_contribuinte", nullable = false)
	private String nomeCompleto;

	@NotNull
    @Column(name = "tp_sexo", nullable = false)
	private Character sexo;

	@NotNull
    @Column(name = "dt_nascimento", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataNascimento;

	@NotNull
	@Size(min = 3, max = 20)
    @Column(name = "nr_rg", nullable = false)
    private String numeroRg;

	@NotNull
	@Size(min = 3, max = 256)
    @Column(name = "ds_emissor_rg", nullable = false)
    private String emissorRg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado idEstagoRg;

	@NotNull
	@Size(min = 6, max = 256)
    @Column(name = "nm_mae_contribuinte", nullable = false)
	private String nomeMae;

	@NotNull
    @Column(name = "ds_email", nullable = false)
    private String email;

    @Column(name = "nr_telefone_celular", nullable = true)
    private String telefone;

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
        return getCpf() + " - " + getNomeCompleto();
    }

}
