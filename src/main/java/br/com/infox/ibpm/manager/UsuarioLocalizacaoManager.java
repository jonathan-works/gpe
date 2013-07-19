package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;

@Name(UsuarioLocalizacaoManager.NAME)
@AutoCreate
public class UsuarioLocalizacaoManager extends GenericManager {

	public static final String NAME = "usuarioLocalizacaoManager";
	private static final long serialVersionUID = 1L;
	
	@In private UsuarioLocalizacaoManager usuarioLocalizacaoManager; 

}
