package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CadastroTarefaExternaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private CadastroTarefaExternaPessoaVO dadosPessoais = new CadastroTarefaExternaPessoaVO();
    @Pattern(regexp = "A|I", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotBlank
    private String codTipoManifestacao = "A";
    private String tipoManifestacao;
    @NotBlank
    private String codGrupoOuvidoria;
    @NotBlank
    private String grupoOuvidoria;
    @NotNull
    private Boolean desejaResposta = Boolean.FALSE;
    private String codMeioResposta;
    private String meioResposta;
    private String email;
    @NotNull
    private Date dataAbertura;
    @NotBlank
    private String codTipoManifesto;
    @NotBlank
    private String tipoManifesto;
    @NotBlank
    private String tituloManifesto;
    @NotBlank
    private String descricaoManifesto;

}
