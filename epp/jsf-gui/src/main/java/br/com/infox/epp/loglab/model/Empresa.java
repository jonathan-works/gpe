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

import org.hibernate.annotations.Persister;

import br.com.infox.core.persistence.SchemaSingleTableEntityPersister;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_contato_loglab", nullable = false)
    @NotNull
    private ContatoLoglab contatoLoglab;


}
