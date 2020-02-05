package br.com.infox.epp.loglab.model;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.Date;

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

import org.hibernate.annotations.Persister;

import br.com.infox.core.persistence.SchemaSingleTableEntityPersister;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Empresa.TABLE_NAME)
@Persister(impl = SchemaSingleTableEntityPersister.class)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
public class Empresa implements Serializable {

    public static final String TABLE_NAME = "tb_empresa";

    private static final long serialVersionUID = 1L;
    private static final String GENERATOR = "EmpresaGenerator";

    @Id
    @SequenceGenerator(name = GENERATOR, sequenceName = "sq_empresa", allocationSize = 1, initialValue=1)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_empresa", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_pessoa_juridica", nullable = false)
    @NotNull
    private PessoaJuridica pessoaJuridica;

    @NotNull
    @Column(name = "nr_cnpj", nullable = false)
    private String cnpj;

    @Column(name = "tp_empresa", nullable = true)
    private String tipoEmpresa;

    @NotNull
    @Column(name = "ds_razao_social", nullable = false)
    private String razaoSocial;

    @NotNull
    @Column(name = "ds_nome_fantasia", nullable = false)
    private String nomeFantasia;

    @NotNull
    @Column(name = "dt_abertura", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dataAbertura;

    @NotNull
    @Column(name = "ds_email", nullable = false)
    private String email;

    @Column(name = "nr_telefone_fixo", nullable = true)
    private String telefoneFixo;

    @NotNull
    @Column(name = "nr_telefone_celular", nullable = false)
    private String telefoneCelular;

    @Column(name = "ds_cidade", nullable = true)
    private String cidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "ds_logradouro", nullable = true)
    private String logradouro;

    @Column(name = "ds_bairro", nullable = true)
    private String bairro;

    @Column(name = "ds_complemento", nullable = true)
    private String complemento;

    @Column(name = "nr_residencia", nullable = true)
    private String numeroResidencia;

    @Column(name = "nr_cep", nullable = true)
    private String cep;

    @Override
    public String toString() {
        return getCnpj();
    }

}
