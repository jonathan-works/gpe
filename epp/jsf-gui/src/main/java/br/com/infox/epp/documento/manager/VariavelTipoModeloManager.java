package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.VariavelTipoModeloDAO;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Name(VariavelTipoModeloManager.NAME)
@AutoCreate
public class VariavelTipoModeloManager extends Manager<VariavelTipoModeloDAO, VariavelTipoModelo> {
    private static final long serialVersionUID = 4455754174682600299L;
    public static final String NAME = "variavelTipoModeloManager";
}
