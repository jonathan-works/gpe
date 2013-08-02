package br.com.infox.core.action.list;

public enum SearchCriteria {

	IGUAL("o.{0} = #'{'{1}.entity.{0}}"),
	DIFERENTE("o.{0} <> #'{'{1}.entity.{0}}"),
	
	MENOR("o.{0} < #'{'{1}.entity.{0}}"),
	MENOR_IGUAL("o.{0} <= #'{'{1}.entity.{0}}"),
	
	MAIOR("o.{0} > #'{'{1}.entity.{0}}"),
	MAIOR_IGUAL("o.{0} >= #'{'{1}.entity.{0}}"),
	
	CONTENDO("lower(o.{0}) like concat('''%''', " +
			"lower(#'{'{1}.entity.{0}}), '''%''')"), 
	INICIANDO("lower(o.{0}) like concat(" +
			"lower(#'{'{1}.entity.{0}}), '''%''')"),
	
	DATA_IGUAL("to_char(cast(o.{0} as date), '''DD/MM/YYYY''') = to_char(cast(#'{'{1}.entity.{0}} as date), '''DD/MM/YYYY''')");
	
	private String pattern;
	
	/**
	 * Construtor do enum, que recebe o padrao
	 * @param pattern � o padr�o para construir a express�o onde:
	 *  {0} = nome do campo
	 *  {1} = nome da entidade (primeira min�scula) 
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
