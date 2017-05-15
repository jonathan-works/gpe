package br.com.infox.epp.redistribuicao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Named
@ViewScoped
public class RedistribuicaoList extends DataList<Processo> {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Processo o, PessoaFisica pf "
            + "\ninner join o.naturezaCategoriaFluxo ncf" + "\ninner join ncf.natureza nat"
            + "\ninner join ncf.categoria cat" + "\ninner join ncf.fluxo fluxo"
            + "\ninner join o.metadadoProcessoList relator";
    private static final String DEFAULT_WHERE = "\nwhere nat.primaria = true"
            + String.format("\nand relator.metadadoType = '%s'", EppMetadadoProvider.RELATOR.getMetadadoType())
            + "\nand relator is not null" + "\nand cast(pf.idPessoa as string) = relator.valor";

    private static final String DEFAULT_ORDER = "o.dataInicio";

    // Filters controll
    private Natureza natureza;
    private Categoria categoria;
    private Fluxo tipoProcesso;
    private PessoaFisica relatorAtual;
    private PessoaFisica novoRelator;
    private UnidadeDecisoraMonocratica novaUdm;

    private List<Natureza> naturezas;
    private List<Categoria> categorias;
    private List<Fluxo> tiposProcessos;
    private List<UnidadeDecisoraMonocratica> udms;
    private List<PessoaFisica> relatores;
    private List<TipoRedistribuicao> tiposRedistribuicao;

    private TipoRedistribuicao tipoRedistribuicao;
    private String motivoRedistribuicao;

    private UnidadeDecisoraMonocratica udmFiltro;
    private List<UnidadeDecisoraMonocratica> udmsFiltro;

    @Inject
    private NaturezaManager naturezaManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private CategoriaDAO categoriaDAO;
    @Inject
    private UnidadeDecisoraMonocraticaDAO unidadeDecisoraMonocraticaDAO;
    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private PapelManager papelManager;
    @Inject
    private TipoRedistribuicaoService tipoRedistribuicaoService;
    @Inject
    private RedistribuicaoService redistribuicaoService;

    private Map<Processo, Boolean> processosSelecionados = new LinkedHashMap<>();

    @Override
    protected String getDefaultEjbql() {
        String retorno = DEFAULT_EJBQL;
        if (getUdmFiltro() != null) {
            retorno += "\ninner join o.metadadoProcessoList udm";
        }
        return retorno;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> orderMap = new HashMap<>();
        orderMap.put("relator", "pf.nome");
        orderMap.put("natureza", "nat.natureza");
        orderMap.put("categoria", "cat.categoria");
        orderMap.put("tipoProcesso", "fluxo.fluxo");
        return orderMap;
    }

    @Override
    protected String getDefaultWhere() {
        String retorno = DEFAULT_WHERE;
        if (getUdmFiltro() != null) {
            retorno += String.format("\nand udm.metadadoType = '%s'",
                    EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA.getMetadadoType());
        }
        return retorno;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    public String getValorMetadadoRelator() {
        if (relatorAtual == null) {
            return null;
        }
        return Long.toString(relatorAtual.getIdPessoa());
    }

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("natureza", "nat", RestrictionType.igual);
        addRestrictionField("categoria", "cat", RestrictionType.igual);
        addRestrictionField("tipoProcesso", "fluxo", RestrictionType.igual);
        addRestrictionField("relatorAtual", "relator.valor = #{redistribuicaoList.valorMetadadoRelator}");
        addRestrictionField("udmFiltro",
                "udm.valor = cast(#{redistribuicaoList.udmFiltro.idUnidadeDecisoraMonocratica} as string)");
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Fluxo getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(Fluxo tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public PessoaFisica getRelatorAtual() {
        return relatorAtual;
    }

    public void setRelatorAtual(PessoaFisica relatorAtual) {
        this.relatorAtual = relatorAtual;
    }

    public List<Natureza> getNaturezas() {
        if (naturezas == null) {
            naturezas = naturezaManager.getNaturezasPrimarias();
        }
        return naturezas;
    }

    public List<Categoria> getCategorias() {
        if (categoria == null) {
            categorias = categoriaDAO.getCategoriasPrimariasAtivas();
        }
        return categorias;
    }

    public List<Fluxo> getTiposProcessos() {
        if (tiposProcessos == null) {
            tiposProcessos = fluxoManager.getFluxosPrimariosAtivos();
        }
        return tiposProcessos;
    }

    public List<UnidadeDecisoraMonocratica> getUdms() {
        if (udms == null) {
            udms = unidadeDecisoraMonocraticaDAO.searchUnidadeDecisoraMonocratica(true, true);
        }
        return udms;
    }

    public List<PessoaFisica> getRelatores() {
        if (relatores == null) {
            relatores = unidadeDecisoraMonocraticaDAO.getRelatores();
        }
        return relatores;
    }

    public List<TipoRedistribuicao> getTiposRedistribuicao() {
        if (tiposRedistribuicao == null) {
            tiposRedistribuicao = tipoRedistribuicaoService.listAtivos();
        }
        return tiposRedistribuicao;
    }

    public List<VariavelProcesso> getVariaveis(Processo processo) {
        if (processo == null) {
            return null;
        }
        return variavelProcessoService.getVariaveis(processo,
                DefinicaoVariavelProcessoRecursos.REDISTRIBUICAO.getIdentificador(),
                papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()));
    }

    public Map<Processo, Boolean> getProcessosSelecionados() {
        return processosSelecionados;
    }

    public List<Processo> getProcessosSelecionadosList() {
        List<Processo> retorno = new ArrayList<>();

        for (Processo processo : processosSelecionados.keySet()) {
            if (processosSelecionados.get(processo)) {
                retorno.add(processo);
            }
        }

        return retorno;
    }

    @Override
    public List<Processo> getResultList() {
        List<Processo> retorno = super.getResultList();

        return retorno;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        limparSelecionados();
        limparCamposRedistribuir();
    }

    public void limparCamposRedistribuir() {
        novoRelator = null;
        tipoRedistribuicao = null;
        novaUdm = null;
        motivoRedistribuicao = null;
    }

    public void limparSelecionados() {
        processosSelecionados.clear();
    }

    @ExceptionHandled(successMessage = "Processos redistribuídos com sucesso")
    public void redistribuir() {
        List<Processo> processosSelecionadosList = getProcessosSelecionadosList();

        if (processosSelecionadosList.isEmpty()) {
            throw new ValidationException("Selecione processos para redistribuir");
        }

        redistribuicaoService.redistribuir(processosSelecionadosList, novaUdm, novoRelator, tipoRedistribuicao,
                motivoRedistribuicao);
        limparSelecionados();
        limparCamposRedistribuir();
        refresh();
    }


    @ExceptionHandled(successMessage = "Processos redistribuídos com sucesso")
    public void redistribuirTodos() {
        int firstResult = getFirstResult();
        int maxResult = getMaxResults();
        setFirstResult(null);
        setMaxResults(null);
        this.resultList = null;
        List<Processo> processosSelecionadosList = getResultList();
        setFirstResult(firstResult);
        setMaxResults(maxResult);

        if (processosSelecionadosList.isEmpty()) {
            throw new ValidationException("Não foram encontrados processos para redistribuir");
        }

        redistribuicaoService.redistribuir(processosSelecionadosList, novaUdm, novoRelator, tipoRedistribuicao,
                motivoRedistribuicao);

        limparSelecionados();
        limparCamposRedistribuir();
        refresh();
    }

    public TipoRedistribuicao getTipoRedistribuicao() {
        return tipoRedistribuicao;
    }

    public void setTipoRedistribuicao(TipoRedistribuicao tipoRedistribuicao) {
        this.tipoRedistribuicao = tipoRedistribuicao;
    }

    public String getMotivoRedistribuicao() {
        return motivoRedistribuicao;
    }

    public void setMotivoRedistribuicao(String motivoRedistribuicao) {
        this.motivoRedistribuicao = motivoRedistribuicao;
    }

    public PessoaFisica getNovoRelator() {
        return novoRelator;
    }

    public void setNovoRelator(PessoaFisica novoRelator) {
        this.novoRelator = novoRelator;
    }

    public UnidadeDecisoraMonocratica getNovaUdm() {
        return novaUdm;
    }

    public void setNovaUdm(UnidadeDecisoraMonocratica novaUdm) {
        this.novaUdm = novaUdm;
    }

    public UnidadeDecisoraMonocratica getUdmFiltro() {
        if (udmFiltro == null && Authenticator.instance().isUsuarioLogadoInMonocratica()) {
            udmFiltro = Authenticator.instance().getMonocraticaLogada();
        }
        return udmFiltro;
    }

    public void setUdmFiltro(UnidadeDecisoraMonocratica udmFiltro) {
        this.udmFiltro = udmFiltro;
    }

    public List<UnidadeDecisoraMonocratica> getUdmsFiltro() {
        if (udmsFiltro == null) {
            udmsFiltro = unidadeDecisoraMonocraticaDAO.searchUnidadeDecisoraMonocratica(true, true);
        }
        return udmsFiltro;
    }
    
    public List<String> getExtensaoFiltrosRedistribuicao(){
        return Collections.emptyList();
    }
}
