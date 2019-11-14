package br.com.infox.epp.relacionamentoprocessos;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.relacionamentoprocessos.RelacionamentoProcessoEdit.SaveListener;

@Named
@ViewScoped
public class RelacionamentoProcessoView implements SaveListener, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private RelacionamentoProcessoInternoList relacionamentoProcessoList;
	@Inject
	private RelacionamentoProcessoExternoList relacionamentoProcessoExternoList;
	@Inject
	private RelacionamentoProcessoEdit relacionamentoProcessoEdit;
	
	@Inject
	private RelacionamentoProcessoManager relacionamentoProcessoManager;
	
	private boolean readOnly;
	
	public void initView(Processo processo, boolean readonly) {
		readonly = readonly || !relacionamentoProcessoManager.isPermittedModificar(); 
		
		this.readOnly = readonly;
		
		relacionamentoProcessoList.setProcesso(processo);
		relacionamentoProcessoList.setReadOnly(readonly);
		relacionamentoProcessoExternoList.setProcesso(processo);
		relacionamentoProcessoExternoList.setReadOnly(readonly);
		
		relacionamentoProcessoEdit.setProcesso(processo);
		relacionamentoProcessoEdit.setSaveListener(this);
	}

	@Override
	public void afterSave(RelacionamentoProcesso relacionamentoProcesso) {
		if(relacionamentoProcesso instanceof RelacionamentoProcessoInterno) {
			relacionamentoProcessoList.refresh();			
		}
		else {
			relacionamentoProcessoExternoList.refresh();			
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Verifica se o usuário logado tem permissão para visualizar processos relacionados
	 * @return
	 */
	public boolean podeVisualizar() {
		return relacionamentoProcessoManager.isPermittedVisualizar();
	}

}
