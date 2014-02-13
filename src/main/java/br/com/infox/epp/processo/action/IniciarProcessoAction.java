package br.com.infox.epp.processo.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.TypeMismatchException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.bean.ItemBean;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.home.PessoaFisicaHome;
import br.com.infox.epp.pessoa.home.PessoaJuridicaHome;
import br.com.infox.epp.pessoa.manager.PessoaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.itx.component.AbstractHome;

@Name(IniciarProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class IniciarProcessoAction {

    public static final String NAME = "iniciarProcessoAction";
    private static final LogProvider LOG = Logging.getLogProvider(IniciarProcessoAction.class);

    @In
    private IniciarProcessoService iniciarProcessoService;
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private PessoaManager pessoaManager;

    private boolean renderedByItem;
    private boolean renderizarCadastroPartes;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Item itemDoProcesso;
    private ProcessoEpa processoEpa;
    private List<ItemBean> itemList;
    private List<PessoaFisica> pessoaFisicaList = new ArrayList<PessoaFisica>();
    private List<PessoaJuridica> pessoaJuridicaList = new ArrayList<PessoaJuridica>();

    public void iniciarProcesso() {
        newProcessoEpa();
        enviarProcessoParaJbpm();
    }
    
    public void iniciarProcesso(List<Pessoa> pessoas){
        newProcessoEpa();
        inserirPartes(pessoas);
        enviarProcessoParaJbpm();
    }

    private void inserirPartes(List<Pessoa> pessoas) {
        if (necessitaPartes()) {
            for (Pessoa p : pessoas) {
                processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
            }
        }
    }

    private void newProcessoEpa() {
        final UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        final Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        processoEpa = new ProcessoEpa(SituacaoPrazoEnum.SAT, new Date(), "", usuarioLogado, naturezaCategoriaFluxo, localizacao, itemDoProcesso);
    }

    private void enviarProcessoParaJbpm() {
        try {
            processoEpaManager.persist(processoEpa);
            iniciarProcessoService.iniciarProcesso(processoEpa, naturezaCategoriaFluxo.getFluxo());
            getMessagesHandler().add("Processo inserido com sucesso!");
        } catch (TypeMismatchException tme) {
            sendIniciarProcessoErrorMessage(IniciarProcessoService.TYPE_MISMATCH_EXCEPTION, tme);
        } catch (NullPointerException npe) {
            sendIniciarProcessoErrorMessage("Nenhum processo informado.", npe);
        } catch (DAOException e) {
            sendIniciarProcessoErrorMessage("Erro ao inserir o processo: "
                    + e.getMessage(), e);
        }
    }

    private void sendIniciarProcessoErrorMessage(final String message,
            final Exception exception) {
        LOG.error(".iniciarProcesso()", exception);
        getMessagesHandler().add(Severity.ERROR, message);
    }

    public void onSelectNatCatFluxo(final NaturezaCategoriaFluxo ncf) {
        naturezaCategoriaFluxo = ncf;
        itemList = new ArrayList<ItemBean>();
        final Categoria categoria = naturezaCategoriaFluxo.getCategoria();
        if (categoria != null) {
            for (CategoriaItem ca : categoria.getCategoriaItemList()) {
                itemList.add(new ItemBean(ca.getItem()));
            }
            if (itemList.isEmpty()) {
                getMessagesHandler().add(Severity.ERROR, "Não há itens cadastrados para a categoria escolhida");
            } else {
                setRenderedByItem(Boolean.TRUE);
            }
        }
    }

    public void onSelectItem(final ItemBean bean) {
        itemDoProcesso = bean.getItem();
        renderedByItem = hasSelectedItem();
        if (!necessitaPartes()) {
            iniciarProcesso();
            if (Authenticator.instance().isUsuarioExterno()) {
                final Redirect redirect = Redirect.instance();
                redirect.setViewId("/Processo/movimentar.seam");
                redirect.setParameter("cid", Conversation.instance().getId());
                redirect.setParameter("idProcesso", getProcessoEpa().getIdProcesso());
                redirect.execute();
            }
        } else {
            renderizarCadastroPartes = Boolean.TRUE;
            renderedByItem = Boolean.FALSE;
        }
    }

    private boolean hasSelectedItem() {
        for (ItemBean ib : itemList) {
            if (ib.isChecked()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private TipoPessoaEnum convertTipoPessoaEnum(final String tipoPessoa) {
        if ("F".equals(tipoPessoa) || "f".equals(tipoPessoa)) {
            return TipoPessoaEnum.F;
        } else if ("J".equals(tipoPessoa) || "j".equals(tipoPessoa)) {
            return TipoPessoaEnum.J;
        }
        return null;
    }

    public void carregaPessoa(String tipoPessoa, String codigo) {
        pessoaManager.carregaPessoa(convertTipoPessoaEnum(tipoPessoa), codigo);
    }

    private <P extends Pessoa> void include(final AbstractHome<P> home,
            final TipoPessoaEnum tipoPessoa, final List<P> list) {
        final StatusMessages messagesHandler = getMessagesHandler();
        final P pessoa = home.getInstance();
        if (pessoa.getAtivo() == null) {
            pessoa.setAtivo(Boolean.TRUE);
            pessoa.setTipoPessoa(tipoPessoa);
            try {
                pessoaManager.persist(pessoa);
            } catch (DAOException e) {
                messagesHandler.add(Severity.ERROR, "Falha ao tentar gravar pessoa", e);
            }
        }
        if (list.contains(pessoa)) {
            messagesHandler.add(Severity.ERROR, "Parte já cadastrada no processo");
        } else {
            list.add(pessoa);
        }
        home.setInstance(null);
    }

    public void incluir(String tipoPessoa) {
        final TipoPessoaEnum tipoPessoaEnum = convertTipoPessoaEnum(tipoPessoa);
        if (TipoPessoaEnum.F.equals(tipoPessoaEnum)) {
            include((PessoaFisicaHome) Component.getInstance(PessoaFisicaHome.NAME), tipoPessoaEnum, pessoaFisicaList);
        } else if (TipoPessoaEnum.J.equals(tipoPessoaEnum)) {
            include((PessoaJuridicaHome) Component.getInstance(PessoaJuridicaHome.NAME), tipoPessoaEnum, pessoaJuridicaList);
        }
    }

    private StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
    }

    public void removePessoaFisica(final PessoaFisica obj) {
        pessoaFisicaList.remove(obj);
    }

    public void removePessoaJuridica(final PessoaJuridica obj) {
        pessoaJuridicaList.remove(obj);
    }

    public boolean isRenderedByItem() {
        return renderedByItem;
    }

    public void setRenderedByItem(final boolean renderedByItem) {
        this.renderedByItem = renderedByItem;
    }

    public void setProcessoEpa(final ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public List<ItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(final List<ItemBean> itemList) {
        this.itemList = itemList;
    }

    private Natureza getNatureza() {
        if (naturezaCategoriaFluxo != null) {
            return naturezaCategoriaFluxo.getNatureza();
        }
        return null;
    }

    public boolean necessitaPartes() {
        if (getNatureza() != null) {
            return getNatureza().getHasPartes();
        }
        return Boolean.FALSE;
    }

    public ParteProcessoEnum tipoPartes() {
        if (getNatureza() != null) {
            return getNatureza().getTipoPartes();
        }
        return null;
    }

    public boolean isRenderizarCadastroPartes() {
        return renderizarCadastroPartes;
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        return pessoaFisicaList;
    }

    public void setPessoaFisicaList(final List<PessoaFisica> pessoaFisicaList) {
        this.pessoaFisicaList = pessoaFisicaList;
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        return pessoaJuridicaList;
    }

    public void setPessoaJuridicaList(
            final List<PessoaJuridica> pessoaJuridicaList) {
        this.pessoaJuridicaList = pessoaJuridicaList;
    }
}
