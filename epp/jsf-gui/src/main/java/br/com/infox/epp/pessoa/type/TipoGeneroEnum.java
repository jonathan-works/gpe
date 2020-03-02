package br.com.infox.epp.pessoa.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoGeneroEnum {

	F("Feminino"), M("Masculino");

	private String label;

    public static TipoGeneroEnum safeValueOf(String codigo) {
    	if ("M".equalsIgnoreCase(codigo) || "Masculino".equalsIgnoreCase(codigo))
    		return TipoGeneroEnum.M;
    	if ("F".equalsIgnoreCase(codigo) || "Feminino".equalsIgnoreCase(codigo))
    		return TipoGeneroEnum.F;
    	return null;
    }

}
