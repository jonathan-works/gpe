package br.com.infox.core.list;

public enum RestrictionType {

	igual("{0} = #'{'{1}.{2}}"),
	
	diferente("{0} <> #'{'{1}.{2}}"),
	
	menor("{0} < #'{'{1}.{2}}"),
	
	menorIgual("{0} <= #'{'{1}.{2}}"),
	
	maior("{0} > #'{'{1}.{2}}"),
	
	maiorIgual("{0} >= #'{'{1}.{2}}"),
	
	contendo("{0} like ('''%''' || #'{'{1}.{2}} || '''%''')"), 
	
	iniciando("{0} like (#'{'{1}.{2}} || '''%''')"),
	
	contendoLower("lower({0}) like ('''%''' || lower(#'{'{1}.{2}}) || '''%''')"),
	
	iniciandoLower("lower({0}) like (lower(#'{'{1}.{2}}) || '''%''')"),
	
	dataIgual("cast({0} as date) = cast(#'{'{1}.{2}} as date)");
	
	private String pattern;

	private RestrictionType(String pattern) {
		this.setPattern(pattern);
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

}

