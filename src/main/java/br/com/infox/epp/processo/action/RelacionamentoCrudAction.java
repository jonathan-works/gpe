package br.com.infox.epp.processo.action;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.manager.RelacionamentoManager;
import br.com.infox.epp.processo.manager.RelacionamentoProcessoManager;
import br.com.infox.epp.processo.manager.TipoRelacionamentoProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;

@Name(RelacionamentoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class RelacionamentoCrudAction extends AbstractCrudAction<Relacionamento, RelacionamentoManager> {

	public static final String NAME = "relacionamentoCrudAction";
	public static final String PROCESSO_RELAC_MSG = "Processo já está relacionado.";
	private static final long serialVersionUID = 1L;

	@In
	private RelacionamentoProcessoManager relacionamentoProcessoManager;
	@In
	private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;
	@In
	private ProcessoManager processoManager;
	@In
	private InfoxMessages infoxMessages;

	private String processo;
	private String processoRelacionado;
	private List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList;

	RelacionamentoProcessoInterno rp1;
	RelacionamentoProcessoInterno rp2;

	public String getProcesso() {
		return processo;
	}

	public void setEditMode(String numeroProcesso, Object id) {
		setId(id);
		this.processoRelacionado = numeroProcesso;
	}

	@Override
	public String inactive(Relacionamento t) {
		return super.inactive((Relacionamento) HibernateUtil.removeProxy(t));
	}

	@Override
	protected boolean isInstanceValid() {
		boolean result = true;
		if (getInstance().getMotivo().trim().length() > 0 && !isManaged()) {
			Relacionamento relacionamento;
			try {
				relacionamento = getManager().find(EntityUtil.getIdValue(getInstance()));
				Processo p1 = processoManager.getProcessoEpaByNumeroProcesso(processo);
				Processo p2 = processoManager.getProcessoEpaByNumeroProcesso(processoRelacionado);
				if (p2 == null) {
					FacesMessages.instance().add(Severity.ERROR,
					        "Processo eletrônico com número " + processoRelacionado + " não encontrado");
				}
				rp1 = new RelacionamentoProcessoInterno(relacionamento, p1);
				rp2 = new RelacionamentoProcessoInterno(relacionamento, p2);
				if (relacionamentoProcessoManager.existeRelacionamento(rp1, rp2)) {
					getMessagesHandler().add(PROCESSO_RELAC_MSG);
					return result = false;
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	@Override
	protected void beforeSave() {
		final Relacionamento relacionamento = getInstance();
		if (relacionamento.getAtivo() == null) {
			relacionamento.setAtivo(Boolean.TRUE);
		}
		relacionamento.setDataRelacionamento(new Date());
		relacionamento.setNomeUsuario(Authenticator.getUsuarioLogado().getNomeUsuario());
	}

	@Override
	protected void afterSave(String ret) {
		if (AbstractAction.PERSISTED.equals(ret)) {
			relacionamentoProcessoManager.persist(rp1);
			relacionamentoProcessoManager.persist(rp2);
			newInstance();
		}
	}

	@Override
	public void newInstance() {
		super.newInstance();
		processoRelacionado = "";
	}

	public void setProcesso(final String processo) {
		this.processo = processo;
	}

	public List<TipoRelacionamentoProcesso> getTipoRelacionamentoProcessoList() {
		if (tipoRelacionamentoProcessoList == null) {
			tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoManager.findAll();
		}
		return tipoRelacionamentoProcessoList;
	}

	public void setTipoRelacionamentoProcessoList(List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessoList) {
		this.tipoRelacionamentoProcessoList = tipoRelacionamentoProcessoList;
	}

	public String getProcessoRelacionado() {
		return processoRelacionado;
	}

	public void setProcessoRelacionado(String processoRelacionado) {
		this.processoRelacionado = processoRelacionado;
	}

}
