package br.com.infox.epp.loglab.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ServidorService implements Serializable {

    private static final long serialVersionUID = 1L;

}
