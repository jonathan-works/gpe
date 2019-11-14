package br.com.infox.epp.processo.variavel.service;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(VariavelService.NAME)
@Scope(ScopeType.EVENT)
@Install(precedence=Install.APPLICATION)
/**
 * Classe extendida no tcepeextended, verificar outros projetos antes de modificar
 */
public class VariavelService implements Serializable {

	public static final String NAME = "variavelService";
	private static final long serialVersionUID = 1L;

	
}
