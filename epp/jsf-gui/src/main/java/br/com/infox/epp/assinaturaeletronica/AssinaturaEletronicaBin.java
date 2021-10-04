package br.com.infox.epp.assinaturaeletronica;

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
@Table(name = "tb_assinatura_eletronica_bin")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class AssinaturaEletronicaBin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_assinatura_eletronica_bin", nullable = false, unique = true)
    private Long id;

    @Column(name = "ob_imagem")
    @NotNull
    private byte[] imagem;

}
