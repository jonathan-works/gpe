package br.com.infox.epp.tce.prestacaocontas.modelo.dao;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;

@Name(ModeloPrestacaoContasDAO.NAME)
public class ModeloPrestacaoContasDAO extends DAO<ModeloPrestacaoContas> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasDAO";
}
