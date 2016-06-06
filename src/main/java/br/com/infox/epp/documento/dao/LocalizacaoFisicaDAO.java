package br.com.infox.epp.documento.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;

@Stateless
@AutoCreate
@Name(LocalizacaoFisicaDAO.NAME)
public class LocalizacaoFisicaDAO extends DAO<LocalizacaoFisica> {

    public static final String NAME = "localizacaoFisicaDAO";
    private static final long serialVersionUID = 1L;
}
