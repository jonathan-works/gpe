package br.com.infox.epp.documento.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Name(VariavelManager.NAME)
@AutoCreate
@Stateless
public class VariavelManager extends Manager<VariavelDAO, Variavel> {

    public static final String NAME = "variavelManager";
    private static final long serialVersionUID = 1L;
    private static final String NOME_VARIAVEL_NUM_DOCUMENTO = "Numero_Documento";

    public List<Variavel> getVariaveisByTipoModeloDocumento(
            TipoModeloDocumento tipoModeloDocumento) {
        return getDao().getVariaveisByTipoModeloDocumento(tipoModeloDocumento);
    }
    
    public Variavel getVariavelByNome(String nomeVariavel) {
    	return getDao().getVariavelAtivaByNome(nomeVariavel);
    }

    public Variavel getVariavelNumDocumento() {
    	return getVariavelByNome(NOME_VARIAVEL_NUM_DOCUMENTO);
    }
}
