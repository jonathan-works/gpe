package br.com.infox.epp.manager;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.DAOException;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.CategoriaItemDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.home.CategoriaHome;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;

@Name(CategoriaItemManager.NAME)
@AutoCreate
public class CategoriaItemManager extends GenericManager{

	private static final long serialVersionUID = -3580636874720809514L;

	public static final String NAME = "categoriaItemManager";
	private static final Log LOG = Logging.getLog(CategoriaItemManager.class);

	@In
	private CategoriaItemDAO categoriaItemDAO;
	
	public List<CategoriaItem> listByCategoria(Categoria categoria) {
		return categoriaItemDAO.listByCategoria(categoria);
	}
	
	public Long countByCategoriaItem(Categoria categoria, Item item) {
	    return categoriaItemDAO.countByCategoriaItem(categoria, item);
	}
	
	public boolean containsCategoriaItem(CategoriaItem categoriaItem)  {
	    return categoriaItemDAO.countByCategoriaItem(categoriaItem.getCategoria(), categoriaItem.getItem()) > 0;
	}
	
	public Categoria getCategoriaAtual() {
	    CategoriaHome categoriaHome = ComponentUtil.getComponent(CategoriaHome.NAME);
	    return categoriaHome.getInstance();
	}
	
	public List<CategoriaItem> createCategoriaItemList(Categoria categoria, Set<Item> itens){
	    List<CategoriaItem> categoriaItemList = new ArrayList<CategoriaItem>();
        if (itens != null) {
            for (Item item : itens) {
                if (item.getAtivo()) {
                    persistCategoriaItem(categoria, item, categoriaItemList);
                }
            }
        }
        
        conclusionMessage("#{messages['entity_created']}","Falha ao inserir",categoriaItemList.size() > 0);
        return categoriaItemList;
	}
	
	@Override
	public <T> T remove(T o) {
	    T result;
		try {
			result = super.remove(o);
		} catch (DAOException e) {
			LOG.error(null, e);
			result = null;
		}
		
		conclusionMessage("#{messages['entity_deleted']}","Falha ao remover", result != null);
	    return result;
	}
    
    private void conclusionMessage(String successMessage, String errorMessage,boolean successful) {
        FacesMessages fm = instance();
        fm.clear();
        if (successful) {
            fm.add(successMessage);
        } else {
            fm.add(Severity.ERROR,errorMessage);
        }
    }
	
	/**
	 * @param categoria Categoria a ser associada ao Item
	 * @param item Item a ser associado à categoria
	 * @param categoriaItemList Lista resultante
	 * 
	 * Este método deve iniciar a transação antes de persistir e finalizar após persistir
	 * devido à necessidade de serem inserções atômicas, caso múltiplas entradas fossem
	 * permitidas em uma única transação e uma destas causasse uma exceção, então
	 * todas as outras falhariam
	 */
    private void persistCategoriaItem(Categoria categoria, Item item,
            List<CategoriaItem> categoriaItemList) {
        Util.beginTransaction();
        CategoriaItem ci;
		try {
			ci = persist(new CategoriaItem(categoria, item));
		} catch (DAOException e) {
			LOG.error(null, e);
			ci = null;
		}
        Util.commitTransction();
        if (ci!=null) {
            categoriaItemList.add(ci);
        }
    }
	
}