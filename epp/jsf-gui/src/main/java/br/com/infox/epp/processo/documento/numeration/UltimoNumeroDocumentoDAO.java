package br.com.infox.epp.processo.documento.numeration;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.LockModeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Stateless
@AutoCreate
@Name(UltimoNumeroDocumentoDAO.NAME)
public class UltimoNumeroDocumentoDAO extends DAO<UltimoNumeroDocumento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "ultimoNumeroDocumentoDAO";
	
	public Long getNextNumeroDocumento(TipoModeloDocumento tipoModeloDocumento, Integer ano) throws DAOException {
	    Map<String, Object> parameters = new HashMap<>();
        parameters.put("tipoModeloDocumento", tipoModeloDocumento);
        UltimoNumeroDocumento next = null;
        if (tipoModeloDocumento.getReiniciaNumeracaoAnual().equals(Boolean.TRUE)) {
        	parameters.put("ano", ano);
        	next = getNamedSingleResult(UltimoNumeroDocumento.GET_NEXT_VALUE_BY_ANO, parameters);
        } else {
        	next = getNamedSingleResult(UltimoNumeroDocumento.GET_NEXT_VALUE, parameters);
        }
        if (next == null) {
            next = new UltimoNumeroDocumento();
            next.setTipoModeloDocumento(tipoModeloDocumento);
            if (tipoModeloDocumento.getReiniciaNumeracaoAnual().equals(Boolean.TRUE)) {
            	next.setAno(ano);
            }
            next.setUltimoNumero(tipoModeloDocumento.getNumeroDocumentoInicial());
            persist(next);
            return next.getUltimoNumero();
        } else {
            detach(next);
            if (tipoModeloDocumento.getReiniciaNumeracaoAnual().equals(Boolean.TRUE)) {
            	next = getNamedSingleResult(UltimoNumeroDocumento.GET_NEXT_VALUE_BY_ANO, parameters);
            } else {
            	next = getNamedSingleResult(UltimoNumeroDocumento.GET_NEXT_VALUE, parameters);
            }
            lock(next, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            next.setUltimoNumero(next.getUltimoNumero() + 1);
            update(next);
            return next.getUltimoNumero();
        }
	}
	
	public void createUltimoNumero(TipoModeloDocumento tipoModeloDocumento, Integer ano) {
		UltimoNumeroDocumento ultimoNumero = new UltimoNumeroDocumento();
		ultimoNumero.setTipoModeloDocumento(tipoModeloDocumento);
		if (tipoModeloDocumento.getReiniciaNumeracaoAnual().equals(Boolean.TRUE)) {
			ultimoNumero.setAno(ano);
        }
		ultimoNumero.setUltimoNumero(tipoModeloDocumento.getNumeroDocumentoInicial());
		persist(ultimoNumero);
	}
}
