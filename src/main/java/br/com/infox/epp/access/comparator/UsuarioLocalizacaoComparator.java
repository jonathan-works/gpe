package br.com.infox.epp.access.comparator;

import java.util.Comparator;

import br.com.infox.epp.access.entity.UsuarioLocalizacao;

/**
 * Comparator usado para ordenação da Lista de Localizações do usuário interno pelos criterios:
 * 1º Administrador
 * 2º Se for primeiro grau numero da Vara
 * 3º toString do UsuarioLocalizacaoMagistradoServidor
 * @author rodrigo
 *
 */
public class UsuarioLocalizacaoComparator implements Comparator<UsuarioLocalizacao> {
	
	@Override
	public int compare(UsuarioLocalizacao o1, UsuarioLocalizacao o2) {
		if (isAdmin(o1)) {
			return Integer.MIN_VALUE;
		} else if (isAdmin(o2)) {
			return Integer.MAX_VALUE;
		}
		
		return o1.toString().compareTo(o2.toString());
	}
	
	private boolean isAdmin(UsuarioLocalizacao ul) {
		return "admin".equals(getPapelId(ul)) || "administrador".equals(getPapelId(ul));
	}

	private String getPapelId(UsuarioLocalizacao ul) {
		return ul.getPapel().getIdentificador();
	}
	
	
}