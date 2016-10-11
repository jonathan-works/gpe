package br.com.infox.epp.processo.metadado.manager;

import static br.com.infox.epp.processo.metadado.entity.MetadadoProcesso.DATE_PATTERN;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.dao.MetadadoProcessoDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@AutoCreate
@Name(MetadadoProcessoManager.NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MetadadoProcessoManager extends Manager<MetadadoProcessoDAO, MetadadoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "metadadoProcessoManager";
	
	@Any
    @Inject
    private Instance<MetadadoProcessoProvider> metadadoProviderInstances;
    
	public List<MetadadoProcesso> getListMetadadoVisivelByProcesso(Processo processo) {
		return getDao().getListMetadadoVisivelByProcesso(processo);
	}
	
	public MetadadoProcesso getMetadado(MetadadoProcessoDefinition definition, Processo processo) {
		return getDao().getMetadado(definition, processo);
	}
	public void addMetadadoProcesso(Processo processo, MetadadoProcessoDefinition definition, String valor) throws DAOException {
		MetadadoProcessoProvider provider = new MetadadoProcessoProvider(processo);        
		MetadadoProcesso metadadoProcesso = provider.gerarMetadado(definition, valor);
        processo.getMetadadoProcessoList().add(metadadoProcesso);
        persist(metadadoProcesso);
    }
	
	public List<MetadadoProcesso> getMetadadoProcessoByType(Processo processo, String metadadoType) {
		return getDao().getMetadadoProcessoByType(processo, metadadoType);
	}
	
	public void removerMetadado(MetadadoProcessoDefinition definition, Processo processo) throws DAOException {
	    processo.removerMetadado(definition);
	    getDao().removerMetadado(definition, processo);
	}

	public void persistMetadados(MetadadoProcessoProvider metadadoProcessoProvider, List<MetadadoProcesso> metadados) throws DAOException {
		Processo processo = metadadoProcessoProvider.getProcesso();
	    for (MetadadoProcesso metadadoProcesso : metadados) {
			persist(metadadoProcesso);
			processo.getMetadadoProcessoList().add(metadadoProcesso);
		}
	}
	
	public void removerMetadadoProcesso(Integer idProcesso, MetadadoProcessoDefinition metadadoProcessoDefinition) throws DAOException {
	    Processo processo = ComponentUtil.<ProcessoManager>getComponent(ProcessoManager.NAME).find(idProcesso);
	    MetadadoProcesso metadadoProcesso = getMetadado(metadadoProcessoDefinition, processo);
	    if (metadadoProcesso != null) {
	        remove(metadadoProcesso);
	    }
	}
	
	public MetadadoProcessoDefinition getMetadadoProcessoDefinition(String nomeMetadado) {
		Iterator<MetadadoProcessoProvider> metadadosProviders = metadadoProviderInstances.iterator();
		while (metadadosProviders.hasNext()) {
			MetadadoProcessoProvider provider = metadadosProviders.next();
			MetadadoProcessoDefinition retorno = provider.getDefinicoesMetadados().get(nomeMetadado);
			if (retorno != null) {
				return retorno;
			}
		}
		return null;
	}
	
    public void setMetadado(MetadadoProcessoDefinition definition, Processo processo, Object objeto) {
        MetadadoProcesso metadadoExistente = processo.getMetadado(definition);
        String valor;
        
        if (EntityUtil.isEntity(definition.getClassType())){
            try {
                valor=EntityUtil.getId(definition.getClassType()).getReadMethod().invoke(objeto).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BusinessException("Não foi possível extrair o valor do id do objeto");
            }
        } else if (Date.class.isInstance(definition.getClassType())) {
            valor = new SimpleDateFormat(DATE_PATTERN).format(objeto);
        } else {
            valor = objeto.toString();
        }
        
        if (metadadoExistente != null) {
            metadadoExistente.setValor(valor);
            update(metadadoExistente);
        } else {
            addMetadadoProcesso(processo, definition, valor);
        }
    }

    public void removeAll(List<MetadadoProcesso> metadadoList) {
        getDao().removeAll(metadadoList);
    }
}
