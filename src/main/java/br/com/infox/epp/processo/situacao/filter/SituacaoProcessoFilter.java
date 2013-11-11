/* $Id: SituacaoProcessoFilter.java 704 2010-08-12 23:21:10Z jplacerda $ */

package br.com.infox.epp.processo.situacao.filter;

import br.com.infox.cliente.entity.filters.Filter;


public interface SituacaoProcessoFilter extends Filter {

	String FILTER_PAPEL_LOCALIZACAO = "papelLocalizacaoSituacaoProcesso";
	String CONDITION_PAPEL_LOCALIZACAO = 
		"exists (select 1 from public.tb_processo_localizacao_ibpm tl " +
		"where tl.id_processo = id_processo " +
		"and tl.id_task_jbpm = id_task " +
		"and tl.id_localizacao = :idLocalizacao " +
		"and (tl.id_papel = :idPapel or tl.id_papel is null))";
	String FILTER_PARAM_ID_LOCALIZACAO = "idLocalizacao";
	String FILTER_PARAM_ID_PAPEL = "idPapel";
}
