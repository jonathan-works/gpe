package br.com.infox.epp.relacionamentoprocessos;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.type.Displayable;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoExterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Named
@ViewScoped
public class RelacionamentoProcessoEdit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProcessoManager processoManager;
	@Inject
	private RelacionamentoManager relacionamentoManager;
	@Inject
	private RelacionamentoProcessoManager relacionamentoProcessoManager;
	@Inject
	private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;
	
	

	private Processo processo;
	private String numeroProcessoRelacionado = null;
	private TipoProcesso tipoProcessoRelacionado;
	
	private Relacionamento instance = new Relacionamento();
	
	private SaveListener saveListener;
	
	private List<TipoRelacionamentoProcesso> tipoRelacionamentoProcessos;
	
	private enum TipoProcesso implements Displayable {
		INTERNO("Interno"), EXTERNO("Externo");

		private String label;

		private TipoProcesso(String label) {
			this.label = label;
		}

		@Override
		public String getLabel() {
			return label;
		}
	}
	
    public void save() {
    	if (instance.getMotivo().trim().length() > 0 && !isManaged()) {
    		Relacionamento relacionamento = instance;
			if (relacionamento.getAtivo() == null) {
				relacionamento.setAtivo(Boolean.TRUE);
			}
    		relacionamento.setDataRelacionamento(new Date());
    		relacionamento.setNomeUsuario(Authenticator.getUsuarioLogado().getNomeUsuario());
    		
			RelacionamentoProcessoInterno rp1 = new RelacionamentoProcessoInterno(relacionamento, processo);
			
			RelacionamentoProcesso rp2 = null;
    		if(tipoProcessoRelacionado == TipoProcesso.INTERNO) {
    			Processo p2 = processoManager.getProcessoByNumero(numeroProcessoRelacionado);
    			if (p2 == null) {
    				FacesMessages.instance().add(Severity.ERROR,
    				        "Processo eletrônico com número '" + numeroProcessoRelacionado + "' não encontrado");
    				return;
    			}
    			else if(p2.equals(processo)) {
    				FacesMessages.instance().add(Severity.ERROR,
    				        "Processo não pode se relacionar com ele mesmo");
    				return;
    			}
    			rp2 = new RelacionamentoProcessoInterno(relacionamento, p2);
    		}
    		else if(tipoProcessoRelacionado == TipoProcesso.EXTERNO)
    		{
    			rp2 = new RelacionamentoProcessoExterno(relacionamento, numeroProcessoRelacionado);
    		}
    		else
    		{
    			throw new UnsupportedOperationException("Tipo de processo não suportado: " + tipoProcessoRelacionado);
    		}
    		
			if (relacionamentoProcessoManager.existeRelacionamento(rp1, rp2)) {
				FacesMessages.instance().add("Processo já relacionado");
				return ;
			}
			
    		relacionamentoManager.persist(relacionamento);    		
			
			relacionamentoProcessoManager.persist(rp1);
			relacionamentoProcessoManager.persist(rp2);
			newInstance();
			if(saveListener != null) {
				saveListener.afterSave(rp2);
			}
		}		
    }
    
	public boolean isManaged() {
		return instance != null && instance.getIdRelacionamento() != null;
	}
	
	public void newInstance() {
		instance = new Relacionamento();
		numeroProcessoRelacionado = null;
		tipoProcessoRelacionado = null;
	}
	
	public String getProcessoRelacionado() {
		return numeroProcessoRelacionado;
	}

	public void setProcessoRelacionado(String processoRelacionado) {
		this.numeroProcessoRelacionado = processoRelacionado;
	}
	
	public List<TipoProcesso> getTipoProcessoList() {
		return Arrays.asList(TipoProcesso.values());
	}
	
	public void initView(Processo processo, boolean readOnly){
		setProcesso(processo);
		this.newInstance();
	}
	
	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	public static interface SaveListener {
		public void afterSave(RelacionamentoProcesso relacionamentoProcesso);
	}

	public void setSaveListener(SaveListener saveListener) {
		this.saveListener = saveListener;
	}

	public String getNumeroProcessoRelacionado() {
		return numeroProcessoRelacionado;
	}

	public void setNumeroProcessoRelacionado(String numeroProcessoRelacionado) {
		this.numeroProcessoRelacionado = numeroProcessoRelacionado;
	}

	public TipoProcesso getTipoProcessoRelacionado() {
		return tipoProcessoRelacionado;
	}

	public void setTipoProcessoRelacionado(TipoProcesso tipoProcessoRelacionado) {
		this.tipoProcessoRelacionado = tipoProcessoRelacionado;
	}

	public Relacionamento getInstance() {
		return instance;
	}

	public void setInstance(Relacionamento instance) {
		this.instance = instance;
	}
	
	public List<TipoRelacionamentoProcesso> getTipoRelacionamentoProcessos() {
		if (tipoRelacionamentoProcessos == null) {
			tipoRelacionamentoProcessos = tipoRelacionamentoProcessoManager.findAll();
		}
		return tipoRelacionamentoProcessos;
	}
	
	    

}
