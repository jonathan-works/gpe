package br.com.infox.epp.security;

import br.com.infox.core.type.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString(of="label")
public enum ControleAcessoEnum implements Displayable {

	GOOGLE("Google");

    @Getter
	private String label;

}
