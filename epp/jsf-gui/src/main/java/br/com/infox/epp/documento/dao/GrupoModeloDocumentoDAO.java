package br.com.infox.epp.documento.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Stateless
@AutoCreate
@Name(GrupoModeloDocumentoDAO.NAME)
public class GrupoModeloDocumentoDAO extends DAO<GrupoModeloDocumento> {

    public static final String NAME = "grupoModeloDocumentoDAO";
    private static final long serialVersionUID = 1L;
}
