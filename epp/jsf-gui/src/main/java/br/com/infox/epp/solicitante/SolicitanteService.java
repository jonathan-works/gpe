package br.com.infox.epp.solicitante;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SolicitanteService implements Serializable {

    private static final long serialVersionUID = 1L;

}
