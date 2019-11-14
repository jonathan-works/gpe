package br.com.infox.epp.usuario;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExtensaoCrudUsuario {
	
	public List<String> getTabs() {
		return new ArrayList<>();
	}
}
