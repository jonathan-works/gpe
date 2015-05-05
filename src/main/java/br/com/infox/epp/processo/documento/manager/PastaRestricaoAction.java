package br.com.infox.epp.processo.documento.manager;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(PastaRestricaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class PastaRestricaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	static final String NAME = "pastaRestricaoAction";

	@In
	private ProcessoManager processoManager;
	@In
	private PastaManager pastaManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private DocumentoList documentoList;
	@In
	private PastaRestricaoManager pastaRestricaoManager;

	private Pasta instance;
	private List<Pasta> pastaList;
	private Processo processo;
	private Integer id;
	private List<PastaRestricao> restricoes;
	private Boolean pastaSelecionada = false;

	@Create
	public void create() {
		newInstance();
	}

	public void newInstance() {
		setInstance(new Pasta());
		setRemovivel(true);
		setSistema(false);
	}

	public void persist() {
		try {
			String nome = getNome();
			setPastaList(processo.getPastaList());
			for (Pasta pasta : getPastaList()) {
				if (nome.equals(pasta.getNome())) {
					FacesMessages.instance().add(Severity.INFO, "Já existe uma pasta com este nome.");
					return;
				}
			}
			getInstance().setProcesso(processo);
			setSistema(false);
			Boolean editavel = (getEditavel() == null) ? Boolean.TRUE : getEditavel();
			setEditavel(editavel);
			Boolean removivel = (getRemovivel() == null) ? Boolean.TRUE : getRemovivel();
			setRemovivel(removivel);
			pastaManager.persistWithDefault(getInstance());
			setPastaList(pastaManager.getByProcesso(processo));
			newInstance();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Pasta adicionada com sucesso.");
		} catch (DAOException e) {
			e.printStackTrace();
			actionMessagesService.handleDAOException(e);
		}
	}

	public void update() {
		try {
			Pasta pasta = pastaManager.find(getId());
			pasta.setNome(getNome());
			pasta.setDescricao(getDescricao());
			pastaManager.update(pasta);
			newInstance();
			FacesMessages.instance().add(Severity.INFO, "Pasta atualizada com sucesso.");
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
		}
	}

	public void remove(Pasta pasta) {
		try {
			if (pastaManager == null) {
				pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
			}
			documentoList.checkPastaToRemove(pasta);
			pastaManager.remove(pasta);
			newInstance();
			setPastaList(pastaManager.getByProcesso(processo.getProcessoRoot()));
			FacesMessages.instance().add(Severity.INFO, "Pasta removida com sucesso.");
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
		}
	}

	public Boolean canRemove(Pasta pasta) {
		if (pasta.getRemovivel()) {
			List<Documento> documentoList = pasta.getDocumentosList();
			return (documentoList == null || documentoList.isEmpty());
		}
		return false;
	}

	public Boolean canEdit(Pasta pasta) {
		return pasta.getEditavel();
	}

	public Pasta getInstance() {
		return instance;
	}

	public void setInstance(Pasta pasta) {
		this.instance = pasta;
	}

	public String getNome() {
		return getInstance().getNome();
	}

	public void setNome(String nome) {
		this.getInstance().setNome(nome);
	}

	public Boolean getSistema() {
		return getInstance().getSistema();
	}

	public void setSistema(Boolean sistema) {
		getInstance().setSistema(sistema);
	}

	public List<Pasta> getPastaList() {
		return pastaList;
	}

	public void setPastaList(List<Pasta> pastaList) {
		this.pastaList = pastaList;
	}

	public void setEditavel(Boolean editavel) {
		getInstance().setEditavel(editavel);
	}

	public Boolean getEditavel() {
		return getInstance().getEditavel();
	}

	public void setRemovivel(Boolean removivel) {
		getInstance().setRemovivel(removivel);
	}

	public Boolean getRemovivel() {
		return getInstance().getRemovivel();
	}

	public void setProcesso(Processo processo) {
        this.processo = processo.getProcessoRoot();
        try {
            this.pastaList = pastaManager.getByProcesso(processo.getProcessoRoot());
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
	}

	public Processo getProcesso() {
		return getInstance().getProcesso();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		setInstance(pastaManager.find(id));
	}

	public String getDescricao() {
		return getInstance().getDescricao();
	}

	public void setDescricao(String descricao) {
		getInstance().setDescricao(descricao);
	}

	public List<PastaRestricao> getRestricoes() {
		return restricoes;
	}

	private void setRestricoes(Pasta pasta) {
		this.restricoes = pastaRestricaoManager.getByPasta(pasta);
	}
	
	public void selectPasta(Pasta pasta){
		setInstance(pasta);
		setRestricoes(pasta);
		setPastaSelecionada(true);
	}

	public Boolean getPastaSelecionada() {
		return pastaSelecionada;
	}

	public void setPastaSelecionada(Boolean pastaSelecionada) {
		this.pastaSelecionada = pastaSelecionada;
	}
	
	
	

}
