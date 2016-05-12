package br.com.infox.epp.processo.action;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.service.IniciarProcessoService;

@Named
@ViewScoped
public class IniciarProcessoView implements Serializable {

	private static final long serialVersionUID = 1L;

    @Inject
    private IniciarProcessoService iniciarProcessoService;
    @Inject
    private NatCatFluxoLocalizacaoDAO natCatFluxoLocalizacaoDAO;

    private List<NaturezaCategoriaFluxoItem> naturezaCategoriaFluxoItemList;
    
    private NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem;
    private Processo processo;
    private TipoPessoaEnum tipoPessoa = TipoPessoaEnum.F;
    private ParticipanteProcesso participanteProcesso;
    private List<ParticipanteProcesso> participanteProcessoList;
    
    @PostConstruct
    private void init() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        Papel papel = Authenticator.getPapelAtual();
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        createProcesso(localizacao, usuarioLogin);
        naturezaCategoriaFluxoItemList = natCatFluxoLocalizacaoDAO.listByLocalizacaoAndPapel(localizacao, papel);
        createParticipanteProcesso();
    }

    private void createProcesso(Localizacao localizacao, UsuarioLogin usuarioLogin) {
        processo = new Processo();
        processo.setLocalizacao(localizacao);
        processo.setUsuarioCadastro(usuarioLogin);
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoRoot(processo);
    }
    
    public void onSelectNaturezaCategoriaFluxoItem() {
        if (naturezaCategoriaFluxoItem != null) {
            processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxoItem.getNaturezaCategoriaFluxo());
        }
    }
    
    public void onChangeTipoPessoa() {
        createParticipanteProcesso();
    }

    private void createParticipanteProcesso() {
        participanteProcesso = new ParticipanteProcesso();
        if (TipoPessoaEnum.F.equals(tipoPessoa)) {
            participanteProcesso.setPessoa(new PessoaFisica());
        } else if (TipoPessoaEnum.J.equals(tipoPessoa)) {
            participanteProcesso.setPessoa(new PessoaJuridica());
        }
    }
    
    public void adicionarParticipante() {
        
    }
    
    public List<NaturezaCategoriaFluxoItem> getNaturezaCategoriaFluxoItemList() {
        return naturezaCategoriaFluxoItemList;
    }
    
    public List<ParticipanteProcesso> getParticipanteProcessoList() {
        return participanteProcessoList;
    }
    
    public TipoPessoaEnum getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public ParticipanteProcesso getParticipanteProcesso() {
        return participanteProcesso;
    }

    public void setParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
        this.participanteProcesso = participanteProcesso;
    }

    public NaturezaCategoriaFluxoItem getNaturezaCategoriaFluxoItem() {
        return naturezaCategoriaFluxoItem;
    }

    public void setNaturezaCategoriaFluxoItem(NaturezaCategoriaFluxoItem naturezaCategoriaFluxoItem) {
        this.naturezaCategoriaFluxoItem = naturezaCategoriaFluxoItem;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
    
//    private void addItemDoProcesso(Processo processo) {
//    	MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
//		MetadadoProcesso metadadoProcesso = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.ITEM_DO_PROCESSO, itemDoProcesso.getIdItem().toString());
//		processo.getMetadadoProcessoList().add(metadadoProcesso);
//	}
//
//	public void iniciarProcesso(Processo processo) {
//    	setProcesso(processo);
//    	enviarProcessoParaJbpm();
//    }
//    
//    public void newProcessoEpa() {
//        processo = new Processo();
//        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
//        processo.setNumeroProcesso("");
//        processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
//        processo.setLocalizacao(Authenticator.getLocalizacaoAtual());
//        processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
//        processo.setDataInicio(new Date());
//    }
//
//    private void enviarProcessoParaJbpm() {
//        try {
//            iniciarProcessoService.iniciarProcesso(processo);
//            TaskInstance taskInstance = taskInstanceManager.getTaskInstanceOpen(processo);
//            getMessagesHandler().add("Processo inserido com sucesso!");
//            if (taskInstance == null) {
//            	throw new BusinessException("Processo não está em tarefa humana");
//            }
//            idTaskInstance = taskInstance.getId();
//        } catch (TypeMismatchException tme) {
////            sendIniciarProcessoErrorMessage(IniciarProcessoService.TYPE_MISMATCH_EXCEPTION, tme);
//        } catch (NullPointerException npe) {
//            sendIniciarProcessoErrorMessage("Nenhum processo informado.", npe);
//        } catch (DAOException e) {
//            sendIniciarProcessoErrorMessage("Erro ao inserir o processo: "
//                    + e.getMessage(), e);
//        } catch (Exception e) {
//        	if (e.getCause() instanceof BusinessException) {
//        		try {
//        			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//        			ec.getFlash().put("message", e.getCause().getLocalizedMessage());
//        			ec.redirect("listView.seam");
//				} catch (IOException e1) {
//					LOG.warn(e1.getMessage(), e1);
//				}
//        	}
//        }
//    }
//
//    private void sendIniciarProcessoErrorMessage(String message, Exception exception) {
//        LOG.error(".iniciarProcesso()", exception);
//        getMessagesHandler().add(Severity.ERROR, infoxMessages.get(message));
//    }
//
//    public void onSelectNatCatFluxo(final NaturezaCategoriaFluxo ncf) {
//        naturezaCategoriaFluxo = ncf;
//        itemList = new ArrayList<ItemBean>();
//        final Categoria categoria = naturezaCategoriaFluxo.getCategoria();
//        if (categoria != null) {
//            for (CategoriaItem ca : categoria.getCategoriaItemList()) {
//                itemList.add(new ItemBean(ca.getItem()));
//            }
//            if (itemList.isEmpty()) {
//                if (!necessitaPartes()) {
//                    iniciarProcesso();
//                    redirectIfExternalUser();
//                } else {
//                    renderizarCadastroPartes = Boolean.TRUE;
//                    renderedByItem = Boolean.FALSE;
//                    ParticipantesController.instance().setNaturezaCategoriaFluxo(ncf);
//                }
//            } else {
//                setRenderedByItem(Boolean.TRUE);
//            }
//        }
//    }
//
//    public void onSelectItem(final ItemBean bean) {
//        itemDoProcesso = bean.getItem();
//        renderedByItem = hasSelectedItem();
//        if (!necessitaPartes()) {
//            iniciarProcesso();
//            redirectIfExternalUser();
//        } else {
//            renderizarCadastroPartes = Boolean.TRUE;
//            renderedByItem = Boolean.FALSE;
//            ParticipantesController.instance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
//        }
//    }
//
//    private void redirectIfExternalUser() {
//        if (Authenticator.instance().isUsuarioExterno()) {
//            final Redirect redirect = Redirect.instance();
//            redirect.setViewId("/Processo/movimentar.seam");
//            redirect.setParameter("scid", Conversation.instance().getId());
//            redirect.setParameter("idProcesso", getProcesso().getIdProcesso());
//            redirect.execute();
//        }
//    }
//
//    private boolean hasSelectedItem() {
//        for (ItemBean ib : itemList) {
//            if (ib.isChecked()) {
//                return Boolean.TRUE;
//            }
//        }
//        return Boolean.FALSE;
//    }
//
//    private StatusMessages getMessagesHandler() {
//        return FacesMessages.instance();
//    }
//
//    public boolean isRenderedByItem() {
//        return renderedByItem;
//    }
//
//    public void setRenderedByItem(final boolean renderedByItem) {
//        this.renderedByItem = renderedByItem;
//    }
//
//    public Processo getProcesso() {
//		return processo;
//	}
//
//	public void setProcesso(Processo processo) {
//		this.processo = processo;
//	}
//
//	public List<ItemBean> getItemList() {
//        return itemList;
//    }
//
//    public void setItemList(final List<ItemBean> itemList) {
//        this.itemList = itemList;
//    }
//
//    public Natureza getNatureza() {
//        return naturezaCategoriaFluxo != null ? naturezaCategoriaFluxo.getNatureza() : null;
//    }
//
//    public boolean necessitaPartes() {
//        return getNatureza() != null && getNatureza().getHasPartes();
//    }
//
//    public boolean isRenderizarCadastroPartes() {
//        return renderizarCadastroPartes;
//    }
//
//    public String getViewId() {
//        return viewId;
//    }
//
//    public void setViewId(String viewId) {
//        this.viewId = viewId;
//    }
//
//    public Long getIdTaskInstance() {
//        return idTaskInstance;
//    }
    
}
