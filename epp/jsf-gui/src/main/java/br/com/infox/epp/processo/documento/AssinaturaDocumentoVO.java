package br.com.infox.epp.processo.documento;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of="idAssinatura")
public class AssinaturaDocumentoVO {

    private Integer idAssinatura;
    private Integer idDocumentoBin;
    private String dsExtensao;
    private String dsDocumento;
    private String nrProcesso;
    private Date dtAssinatura;

    public AssinaturaDocumentoVO(Integer idAssinatura, Integer idDocumentoBin, String dsExtensao,
            String dsDocumento, String nrProcesso, Date dtAssinatura) {
        this.idAssinatura = idAssinatura;
        this.idDocumentoBin = idDocumentoBin;
        this.dsExtensao = dsExtensao;
        this.dsDocumento = dsDocumento;
        this.nrProcesso = nrProcesso;
        this.dtAssinatura = dtAssinatura;
    }

}
