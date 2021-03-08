package br.com.infox.epp.access;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class DeveAssinarTermoAdesaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
