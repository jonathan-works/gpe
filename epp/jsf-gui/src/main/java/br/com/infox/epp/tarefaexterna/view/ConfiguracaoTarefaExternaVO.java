package br.com.infox.epp.tarefaexterna.view;

import java.util.List;

import javax.faces.model.SelectItem;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

public class ConfiguracaoTarefaExternaVO {

    @NotNull
    @Getter @Setter
    private Integer classificacaoParaInserir;
    @Getter @Setter
    private List<SelectItem> modelos;
    @Getter @Setter
    private List<SelectItem> signais;
    @Getter @Setter
    private List<ClassificacaoDocumentoVO> classificacoes;
    @Getter @Setter
    private List<ClassificacaoDocumentoVO> classificacoesDisponiveis;
    @Getter @Setter
    @NotNull @NotEmpty
    private String codPastaParaInserir;
    @Getter @Setter
    @NotNull @NotEmpty
    private String nomePastaParaInserir;
    @Getter @Setter
    private List<PastaVO> pastas;

    @Getter @Setter
    @NotNull @NotEmpty
    private String signalTarefaExterna;
    @Getter @Setter
    @NotNull @NotEmpty
    private String modeloDocumentoChaveConsulta;
    @Getter @Setter
    @NotNull @NotEmpty
    private String modeloDocumentoDownloadPDF;

}
