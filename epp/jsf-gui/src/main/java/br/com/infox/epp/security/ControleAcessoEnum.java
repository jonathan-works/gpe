package br.com.infox.epp.security;

import br.com.infox.core.type.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ControleAcessoEnum implements Displayable {

    DESATIVADO("Desativado"),
	GOOGLE("Google");

    @Getter
	private String label;

    @Override
    public String toString() {
        return super.name();
    }

}
