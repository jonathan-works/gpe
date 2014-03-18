package br.com.infox.epp.processo.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.TypeMismatchException;
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
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.service.IniciarProcessoService;

@Name(IniciarProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class IniciarProcessoAction {

    public static final String NAME = "iniciarProcessoAction";
    private static final LogProvider LOG = Logging.getLogProvider(IniciarProcessoAction.class);

    @In
    private IniciarProcessoService iniciarProcessoService;

    private boolean renderedByItem;
    private boolean renderizarCadastroPartes;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Item itemDoProcesso;
    private ProcessoEpa processoEpa;
    private List<ItemBean> itemList;

    private String viewId;

    public void iniciarProcesso() {
        newProcessoEpa();
        enviarProcessoParaJbpm();
    }

    public void iniciarProcesso(List<PessoaFisica> pessoasFisicas, List<PessoaJuridica> pessoasJuridicas) {
        newProcessoEpa();
        inserirPartes(pessoasFisicas, pessoasJuridicas);
        enviarProcessoParaJbpm();
    }

    private void inserirPartes(List<PessoaFisica> pessoasFisicas, List<PessoaJuridica> pessoasJuridicas) {
        if (necessitaPartes()) {
            for (PessoaFisica p : pessoasFisicas) {
                processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
            }
            for (PessoaJuridica p : pessoasJuridicas) {
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
            iniciarProcessoService.iniciarProcesso(processoEpa);
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

    private StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
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

    public Natureza getNatureza() {
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

    public boolean isRenderizarCadastroPartes() {
        return renderizarCadastroPartes;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

}
