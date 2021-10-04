package br.com.infox.epp.assinaturaeletronica;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.hibernate.UUIDGenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_assinatura_eletronica")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class AssinaturaEletronica implements Serializable {

    private static final String GENERATOR = "AssinaturaEletronicaGenerator";

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = "sq_assinatura_eletronica")
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_assinatura_eletronica", nullable = false, unique = true)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pessoa_fisica", unique = true, nullable = false)
    private PessoaFisica pessoaFisica;

    @NotNull
    @Column(name = "ds_extensao")
    private String extensao;

    @NotNull
    @Column(name = "nm_arquivo")
    private String nomeArquivo;

    @NotNull
    @Column(name = "ds_content_type")
    private String contentType;

    @NotNull
    @Column(name = "dt_inclusao")
    private Date dataInclusao;

    @NotNull
    @Column(name = "ds_uuid", unique = true, nullable = false)
    @Type(type = UUIDGenericType.TYPE_NAME)
    private UUID uuid = UUID.randomUUID();

}
