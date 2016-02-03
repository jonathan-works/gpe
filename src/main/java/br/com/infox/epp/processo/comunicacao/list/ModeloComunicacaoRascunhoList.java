package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@Named(ModeloComunicacaoRascunhoList.NAME)
@ViewScoped
public class ModeloComunicacaoRascunhoList extends DataList<ModeloComunicacao> {
    
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoRascunhoList";
	
	private static final String DEFAULT_JPQL = "select m from ModeloComunicacao m ";
	
	private static final String DEFAULT_WHERE = "where m.processo.idProcesso = #{modeloComunicacaoRascunhoList.processo.idProcesso} "
	        + "and not exists (select 1 from org.jbpm.context.exe.variableinstance.LongInstance li where li.name = 'idModeloComunicacao' "
	        + "                and li.value = m.id) "
	        + "and (m.localizacaoResponsavelAssinatura is null or "
	        + "        (m.localizacaoResponsavelAssinatura.idLocalizacao = #{authenticator.getUsuarioPerfilAtual().localizacao.idLocalizacao} "
	        + "         and ( m.perfilResponsavelAssinatura is null or m.perfilResponsavelAssinatura.id =  #{authenticator.getUsuarioPerfilAtual().perfilTemplate.id} ) ) ) "
	        + "and exists (select 1 from DestinatarioModeloComunicacao dm where dm.modeloComunicacao.id = m.id and dm.expedido = false)";
	
	private static final String DEFAULT_ORDER = "m.id";

	private Processo processo;
	
	@Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
	
	@Override
	protected String getDefaultWhere() {
	    return DEFAULT_WHERE;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

}
