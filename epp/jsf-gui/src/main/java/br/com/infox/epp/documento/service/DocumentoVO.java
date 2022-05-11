package br.com.infox.epp.documento.service;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class DocumentoVO {

    private Integer idDocumento;
    private Integer idDocumentoBin;
    private Integer numeroSequencialDocumento;
    private String classificacaoDocumento;
    private String descricao;
    private String usuario;
    private Date dataInclusao;

    public DocumentoVO(Integer idDocumento, Integer idDocumentoBin, Integer numeroSequencialDocumento
        , String classificacaoDocumento, String descricao
        , String usuario, Date dataInclusao
    ){
        super();
        this.idDocumento = idDocumento;
        this.idDocumentoBin = idDocumentoBin;
        this.numeroSequencialDocumento = numeroSequencialDocumento;
        this.classificacaoDocumento = classificacaoDocumento;
        this.descricao = descricao;
        this.usuario = usuario;
        this.dataInclusao = dataInclusao;
    }

}
