package br.com.infox.epp.assinaturaeletronica;

import java.util.Date;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class AssinaturaEletronicaDTO {

    private Long id;
    private Integer idPessoa;
    private String nomeArquivo;
    private String extensao;
    private String contentType;
    private Date dataInclusao;
    private UUID uuid = UUID.randomUUID();
    private byte[] imagem;

    public AssinaturaEletronicaDTO(Long id, Integer idPessoa, String nomeArquivo, String extensao, String contentType, Date dataInclusao, UUID uuid) {
        this.id = id;
        this.idPessoa = idPessoa;
        this.nomeArquivo = nomeArquivo;
        this.extensao = extensao;
        this.contentType = contentType;
        this.dataInclusao = dataInclusao;
        this.uuid = uuid;
    }



}
