package br.com.infox.epp.unidadedecisora.crud;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExtensaoCrudUDC {

    public List<String> getColumns() {
        return new ArrayList<>(0);
    }

    public List<String> getEditFields() {
        return new ArrayList<>(0);
    }
}
