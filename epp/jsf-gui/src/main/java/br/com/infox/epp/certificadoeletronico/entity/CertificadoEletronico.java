package br.com.infox.epp.certificadoeletronico.entity;

import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = CertificadoEletronico.TABLE_NAME)
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class CertificadoEletronico implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_certificado_eletronico";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "CertificadoEletronicoGenerator", sequenceName = "sq_certificado_eletronico")
	@GeneratedValue(generator = "CertificadoEletronicoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_certificado_eletronico", nullable = false, unique = true)
	@Getter @Setter
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_certificado_eletronico_pai")
    @Getter @Setter
	private CertificadoEletronico certificadoEletronicoPai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa_fisica")
    @Getter @Setter
    private PessoaFisica pessoaFisica;

    @Column(name = "ds_senha")
    @Getter @Setter
    @NotNull
    private String senha;

    @Column(name = "dt_cadastro")
    @Getter @Setter
    @NotNull
    private Date dataCadastro;

	@Column(name = "dt_inicio")
	@Getter @Setter
	@NotNull
	private Date dataInicio;

	@Column(name = "dt_fim")
	@Getter @Setter
	@NotNull
	private Date dataFim;

	@Transient
	public boolean isAtivo() {
	    Date now = Calendar.getInstance().getTime();
	    return now.after(getDataInicio()) && now.before(getDataFim());
	}

}