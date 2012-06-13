package br.com.infox.ibpm.home;

import java.util.Comparator;

import br.com.infox.ibpm.entity.UsuarioLocalizacao;

/**
 * Comparator usado para ordenação da Lista de Localizações do usuário interno pelos criterios:
 * 1º Administrador
 * 2º Se for primeiro grau numero da Vara
 * 3º toString do UsuarioLocalizacaoMagistradoServidor
 * @author rodrigo
 *
 */
public class UsuarioLocalizacaoComparator implements Comparator<UsuarioLocalizacao> {
	
	public int compare(UsuarioLocalizacao o1, UsuarioLocalizacao o2) {
		if (isAdmin(o1)) {
			return Integer.MIN_VALUE;
		} else if (isAdmin(o2)) {
			return Integer.MAX_VALUE;
		}
		
		return o1.toString().compareTo(o2.toString());
	}
	
	private boolean isAdmin(UsuarioLocalizacao ul) {
		return getPapelId(ul).equals("admin") || getPapelId(ul).equals("administrador");
	}

	private String getPapelId(UsuarioLocalizacao ul) {
		return ul.getPapel().getIdentificador();
	}
	
	
}