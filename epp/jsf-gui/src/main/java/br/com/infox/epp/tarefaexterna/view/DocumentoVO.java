package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DocumentoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer idDocumentoBin;
    private String descricao;
    private String pasta;
    private String classificacao;
    private Date dataInclusao;

    public DocumentoVO(Long id, Integer idDocumentoBin, String descricao, String pasta, String classificacao,
            Date dataInclusao) {
        super();
        this.id = id;
        this.idDocumentoBin = idDocumentoBin;
        this.descricao = descricao;
        this.pasta = pasta;
        this.classificacao = classificacao;
        this.dataInclusao = dataInclusao;
    }

}