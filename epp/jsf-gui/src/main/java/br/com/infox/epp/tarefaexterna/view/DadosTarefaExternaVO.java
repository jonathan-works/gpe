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
public class DadosTarefaExternaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String tipoManifestacao;
    private String tituloManifestacao;
    private Date dataInico;
    private String numeroManifestacao;
    private String conteudo;
    private String conteudoPDF;

}