package br.com.infox.epp.processo.comunicacao.crud;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;

@Name(TipoComunicacaoDAO.NAME)
@AutoCreate
public class TipoComunicacaoDAO extends DAO<TipoComunicacao> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoDAO";
}
