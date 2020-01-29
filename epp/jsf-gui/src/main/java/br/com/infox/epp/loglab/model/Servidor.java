package br.com.infox.epp.loglab.model;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @NotNull
    @Cpf
    @Column(name = "nr_cpf", nullable = false)
    private String cpf;

    @NotNull
    @Size(min = 6, max = 256)
    @Column(name = "nm_servidor", nullable = false)
    private String nomeCompleto;

    @Size(min = 3, max = 256)
    @Column(name = "ds_cargo_funcao", nullable = false)
    private String cargoFuncao;

    @NotNull
    @Column(name = "ds_email", nullable = false)
    private String email;

    @Column(name = "nr_telefone", nullable = true)
    private String telefone;

    //Orgao
    @Column(name = "ds_secretaria", nullable = true)
    private String secretaria;

    //LocalTrabalho
    @Column(name = "ds_departamento", nullable = true)
    private String departamento;

    @Column(name = "nr_matricula", nullable = true)
    private String matricula;

    @Column(name = "nr_celular", nullable = true)
    private String celular;

    @Column(name = "dt_nascimento", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(name = "dt_nomeacao_contratacao", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date DataNomeacaoContratacao;

    @Column(name = "dt_posse", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date DataPosse;

    @Column(name = "dt_exercicio", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date DataExercicio;

    @Column(name = "ds_subfolha", nullable = true)
    private String subFolha;

    @Column(name = "ds_jornada", nullable = true)
    private String Jornada;

    @Column(name = "ds_ocupacao_carreira", nullable = true)
    private String OcupacaoCarreira;

    @Column(name = "ds_cargo_carreira", nullable = true)
    private String CargoCarreira;

    @Column(name = "ds_ocupacao_comissao", nullable = true)
    private String OcupacaoComissao;

    @Column(name = "ds_cargo_comissao", nullable = true)
    private String cargoComissao;

    @Column(name = "nm_pai", nullable = true)
    private String pai;

    @Column(name = "nm_mae", nullable = true)
    private String mae;

    @Column(name = "nr_rg", nullable = true)
    private String numeroRg;

    @Column(name = "dt_emissao_rg", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dataEmissaoRg;

    @Column(name = "ds_orgao_rg", nullable = true)
    private String orgaoEmissoRG;

    @Override
    public String toString() {
        return getCpf() + " - " + getNomeCompleto();
    }

}
