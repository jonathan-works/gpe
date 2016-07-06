package br.com.infox.epp.fluxo.definicaovariavel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoVariavelProcessoRecursos {

	public List<RecursoVariavel> getRecursosDisponiveis() {
		List<RecursoVariavel> recursos = new ArrayList<>();
		recursos.add(CONSULTA_PROCESSOS);
		recursos.add(DETALHE_PROCESSO);
		recursos.add(MOVIMENTAR);
		recursos.add(PAINEL_INTERNO);
		
		Collections.sort(recursos, new Comparator<RecursoVariavel>() {
			@Override
			public int compare(RecursoVariavel o1, RecursoVariavel o2) {
				return o1.getIdentificador().compareToIgnoreCase(o2.getIdentificador());
			}
		});
		
		return recursos;
	}
	
	public static final RecursoVariavel PAINEL_INTERNO = new RecursoVariavel("painelInterno", "Painel do Usuário Interno");
	public static final RecursoVariavel CONSULTA_PROCESSOS = new RecursoVariavel("consultaProcessos", "Consulta Interna de Processos");
	public static final RecursoVariavel MOVIMENTAR = new RecursoVariavel("movimentar", "Aba de Variáveis da tela de Movimentação de Processos");
	public static final RecursoVariavel DETALHE_PROCESSO = new RecursoVariavel("detalheProcesso", "Detalhe do Processo");
	
	public static class RecursoVariavel {
		private String identificador;
		private String nome;
		
		public RecursoVariavel(String identificador, String nome) {
			this.identificador = identificador;
			this.nome = nome;
		}
		
		public String getIdentificador() {
			return identificador;
		}
		
		public String getNome() {
			return nome;
		}
	}
}
