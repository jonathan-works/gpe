package br.com.infox.epp.documento.publicacao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name="tb_local_publicacao")
public class LocalPublicacao {
	
    private static final String GENERATOR_NAME = "LocalPublicacaoGenerator";
    private static final String SEQUENCE_NAME = "sq_local_publicacao";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = SEQUENCE_NAME, name = GENERATOR_NAME )
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_local_publicacao")
    private Long id;
    
	@NotNull
	@Size(max=LengthConstants.CODIGO_DOCUMENTO)
	@Column(name="cd_local_publicacao", unique = true)
	private String codigo;
	
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@Column(name="ds_local_publicacao", unique = true)
	private String descricao;

	public Long getId() {
		return id;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
