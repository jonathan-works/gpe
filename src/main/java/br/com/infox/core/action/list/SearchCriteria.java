package br.com.infox.core.action.list;

public enum SearchCriteria {

	igual("o.{0} = #'{'{1}.entity.{0}}"),
	diferente("o.{0} <> #'{'{1}.entity.{0}}"),
	
	menor("o.{0} < #'{'{1}.entity.{0}}"),
	menorIgual("o.{0} <= #'{'{1}.entity.{0}}"),
	
	maior("o.{0} > #'{'{1}.entity.{0}}"),
	maiorIgual("o.{0} >= #'{'{1}.entity.{0}}"),
	
	contendo("lower(o.{0}) like concat('''%''', " +
			"lower(#'{'{1}.entity.{0}}), '''%''')"), 
	iniciando("lower(o.{0}) like concat(" +
			"lower(#'{'{1}.entity.{0}}), '''%''')"),
	
	dataIgual("to_char(cast(o.{0} as date), '''DD/MM/YYYY''') = to_char(cast(#'{'{1}.entity.{0}} as date), '''DD/MM/YYYY''')");
	
	private String pattern;
	
	/**
	 * Construtor do enum, que recebe o padrao
	 * @param pattern é o padrão para construir a expressão onde:
	 *  {0} = nome do campo
	 *  {1} = nome da entidade (primeira minúscula) 
	 */
	private SearchCriteria(String pattern) {
		this.setPattern(pattern);
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

}
