package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.epp.pessoa.type.TipoGeneroEnum;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import lombok.Getter;

@Named
@ViewScoped
public class CadastroTarefaExternaView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String TAREFA_EXTERNA_TIPOS_MANIFESTO = "tarefaExternaTiposManifesto";
    private static final String TAREFA_EXTERNA_MEIOS_RESPOSTA = "tarefaExternaMeiosResposta";
    private static final String TAREFA_EXTERNA_GRUPO_OUVIDORIAS = "tarefaExternaGrupoOuvidorias";

    @Getter
    private CadastroTarefaExternaVO vo;

    @Inject
    private EstadoSearch estadoSearch;
    @Inject
    private DominioVariavelTarefaSearch dominioVariavelTarefaSearch;

    @Getter
    private List<SelectItem> estados;
    @Getter
    private TipoGeneroEnum[] tiposGenero;
    @Getter
    private List<SelectItem> grupoOuvidorias;
    @Getter
    private List<SelectItem> meiosResposta;
    private List<SelectItem> meiosRespostaBase;
    @Getter
    private List<SelectItem> tiposManifesto;

    private String uuidView;

    @PostConstruct
    private void init() {
        uuidView = UUID.randomUUID().toString();
        this.vo = new CadastroTarefaExternaVO();
        this.estados = estadoSearch.findAll()
            .stream()
            .map(o -> new SelectItem(o.getCodigo(), o.getNome()))
            .collect(Collectors.toList());
        this.tiposGenero = TipoGeneroEnum.values();

        initListas();
        onChangeTipoManifestacao();
    }

    private void initListas() {
        this.grupoOuvidorias = getListaDados(TAREFA_EXTERNA_GRUPO_OUVIDORIAS);
        this.meiosRespostaBase = getListaDados(TAREFA_EXTERNA_MEIOS_RESPOSTA);
        this.tiposManifesto = getListaDados(TAREFA_EXTERNA_TIPOS_MANIFESTO);
    }

    public void onChangeDesejaResposta() {
        if(!getVo().getDesejaResposta()) {
            getVo().setMeioResposta(null);
            getVo().setEmail(null);
        }
    }

    public void onChangeTipoManifestacao() {
        if("A".equals(getVo().getTipoManifestacao())) {
            this.meiosResposta = this.meiosRespostaBase.stream()
                .filter(o -> Arrays.asList("EM","PE").contains(o.getValue()))
                .collect(Collectors.toList());
        } else {
            this.meiosResposta = this.meiosRespostaBase;
        }
    }

    private List<SelectItem> getListaDados(String codigo) {
        DominioVariavelTarefa dominioVarTarefa = dominioVariavelTarefaSearch
                .findByCodigo(codigo);
        if(dominioVarTarefa == null) {
            throw new EppConfigurationException(String.format("Domínio de dados não configurado: %s", codigo));
        } else if(dominioVarTarefa.isDominioSqlQuery()) {
            throw new EppConfigurationException(String.format("Domínio de dados não configurado corretamente: %s", codigo));
        }

        String[] itens = dominioVarTarefa.getDominio().split(";");
        List<SelectItem> resultado = new ArrayList<>();
        for (String item : itens) {
            String[] pair = item.split("=");
            resultado.add(new SelectItem(pair[0], pair[1]));
        }

        return resultado;
    }

    @ExceptionHandled(value = MethodType.PERSIST)
    public void cadastrar() {
        System.out.println(Contexts.getApplicationContext().get("f4666914-25ad-4860-8163-f5da1e93b1b1"));
    }


}