package br.com.infox.epa.type;

import br.com.itx.type.EnumType;

public class PessoaType extends EnumType<TipoPessoaEnum> {

	protected PessoaType(Enum<TipoPessoaEnum> type) {
		super(type);
	}

	private static final long serialVersionUID = 1L;

	protected final String getPessoaFisicaString() {
		return "F";
	}

	protected final String getPessoaJuridicaString() {
		return "J";
	}

	public String getName() {
		return "fisica_juridica";
	}
}
