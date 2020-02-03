package br.com.infox.epp.certificadoeletronico.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = CertificadoEletronicoBin.TABLE_NAME)
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class CertificadoEletronicoBin implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_certificado_eletronico_bin";

	@Id
	@Column(name = "id_certificado_eletronico_bin", nullable = false, unique = true)
	@Getter @Setter
	private Long id;

    @NotNull
    @Column(name = "ob_crt", nullable = false)
    @Getter @Setter
    private byte[] crt;

    @NotNull
    @Column(name = "ob_key", nullable = false)
    @Getter @Setter
    private byte[] key;

}