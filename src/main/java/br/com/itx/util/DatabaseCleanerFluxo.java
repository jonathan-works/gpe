package br.com.itx.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Classe que efetua a limpeza da base de dados, respeitando os registros
 * referenciados pelos fluxos válidos do sistema.
 * 
 * Um fluxo é considerado válido se ele estiver associado a uma classe, 
 * ou for um subfluxo de um fluxo válido.
 * 
 * @author luiz
 *
 */
public class DatabaseCleanerFluxo {
	
	private final Connection connection;
	
	private static Set<Integer> modeloDocumentoList = new HashSet<Integer>();
	private static Set<Integer> localizacaoList = new HashSet<Integer>();
	
	/**
	 * Map que tem como chave o nome da tabela e como valor a coluna da chave primária.
	 */
	private static Map<String, String> primaryKeys = new HashMap<String, String>();
	
	public interface Tratador {
		void trataExpressao(String expressao, Map<String, Set<Integer>> registrosMap, Connection conn);
	}
	
	static class ModeloDocumento implements Tratador {
		
		/**
		 * Registra as tabelas com as chaves, que serão tratadas por essa classe
		 */
		static {
			primaryKeys.put("public.tb_modelo_documento", "id_modelo_documento");
		}
		
		public void trataExpressao(String expressao, Map<String, Set<Integer>> registrosMap, Connection conn) {
			Set<Integer> setModelos = registrosMap.get("public.tb_modelo_documento");
			if (setModelos == null) {
				setModelos = new HashSet<Integer>();
				registrosMap.put("public.tb_modelo_documento", setModelos);
			}
			List<String> params = parseParameters(expressao);
			params.remove(0);
			for (String s : params) {
				setModelos.add(Integer.parseInt(s));
				modeloDocumentoList.add(Integer.parseInt(s));
			}
			try {
				for (Integer idModelos : getModeloDocumentoParametro(conn)) {
					setModelos.add(idModelos);
					modeloDocumentoList.add(idModelos);
				};
			} catch (SQLException e) {
//				e.printStackTrace();
			}
		}
	}
	
	static class VerificaEvento implements Tratador{
		static {
			primaryKeys.put("public.tb_tarefa", "id_tarefa");
		}

		public void trataExpressao(String expressao, Map<String, Set<Integer>> registrosMap, Connection conn) {
			Set<Integer> setModelos = registrosMap.get("public.tb_tarefa");
			if (setModelos == null) {
				setModelos = new HashSet<Integer>();
				registrosMap.put("public.tb_tarefa", setModelos);
			}
			List<String> params = parseParameters(expressao);
			for (String s : params) {
				setModelos.add(Integer.parseInt(s));
			}
		}
	}
	
	static class PooledActor implements Tratador{

		static {
			primaryKeys.put("public.tb_papel", "id_papel");
			primaryKeys.put("public.tb_localizacao", "id_localizacao");
		}

		public void trataExpressao(String expressao, Map<String, Set<Integer>> registrosMap, Connection conn) {
			Set<Integer> setLocal = registrosMap.get("public.tb_localizacao");
			if (setLocal == null) {
				setLocal = new HashSet<Integer>();
				registrosMap.put("public.tb_localizacao", setLocal);
			}
			Set<Integer> setPapel = registrosMap.get("public.tb_papel");
			if (setPapel == null) {
				setPapel = new HashSet<Integer>();
				registrosMap.put("public.tb_papel", setPapel);
			}
			List<String> params = parseParameters(expressao);
			for (String s : params) {
				String local = s;
				if (s.contains(":")) {
					local = s.split(":")[0];
					setPapel.add(Integer.parseInt(s.split(":")[1]));
				}
				setLocal.add(Integer.parseInt(local));
				localizacaoList.add(Integer.parseInt(local));
			}
		}
	}
	
	public DatabaseCleanerFluxo(Connection connection) {
		this.connection = connection;
	}
	
	public void clearBin() throws SQLException {
		System.out.println("Limpando Bin...");
		PreparedStatement ps = null;
		try {
			String query =
				"delete from public.tb_processo_documento_bin";
		ps = connection.prepareStatement(query);
		ps.executeUpdate();
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		System.out.println("Limpeza do Bin efetuada com Sucesso.");
	}
	
	public void clearDatabase() throws SQLException {
		removerFluxo();
		addUsuarioLocalizacao();
		
	}
	
	public static List<String> parseParameters(String expression) {
		List<String> ret = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(expression, "(',)");
		st.nextToken();
		while (st.hasMoreTokens()) {
			ret.add(st.nextToken().trim());
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		Connection conn = new DatabaseUtil().getConnection("jdbc:postgresql://localhost:5432/desbd_clean");
		
		DatabaseCleanerFluxo dc = new DatabaseCleanerFluxo(conn);
		dc.clearDatabase();
		
		Connection connBin = new DatabaseUtil().getConnection("jdbc:postgresql://localhost:5432/desbd_clean_bin");
		
		DatabaseCleanerFluxo dcBin = new DatabaseCleanerFluxo(connBin);
		dcBin.clearBin();
		System.out.println("Limpeza do Banco Concluida com Sucesso.");
	}
	
	//Busca os modelos de documento que são referenciados nos parâmetros
	public static List<Integer> getModeloDocumentoParametro(Connection connection) throws SQLException{
		PreparedStatement ps = null;
		String query = 
			"select vl_variavel from public.tb_parametro " +
			"where ds_esquema_tabela_id like 'public.tb_modelo_documento'";
		ps = connection.prepareStatement(query);
		ResultSet resultSet = ps.executeQuery();
		List<Integer> listaModelos = new ArrayList<Integer>(0);
		while (resultSet.next()){
			Integer idModelo = resultSet.getInt(1);
			listaModelos.add(idModelo);
		}
		return listaModelos;
	}
	
	//Limpa a tabela tb_fluxo e tb_localizacao deixando apenas os atributos id_localização_pai.
	public void removerFluxo() throws SQLException{
		PreparedStatement ps = null;
		try {
			String query = "delete from client.tb_tipo_parte_classe_judicial;"+
						   "delete from client.tb_competencia_classe_assunto;"+
						   "delete from client.tb_classe_aplicacao;"+
						   "delete from client.tb_classe_judicial;"+
						   "delete from public.tb_tarefa_transicao_evento_agrupamento;"+
						   "delete from public.tb_tarefa_transicao_evento;"+ 
						   "delete from public.tb_tarefa_jbpm;"+
						   "delete from public.tb_caixa;"+
						   "delete from public.tb_tarefa_evento_agrupamento;"+
						   "delete from public.tb_tarefa_evento;"+
						   "delete from public.tb_tarefa;"+
						   "delete from public.tb_fluxo;" +
						   "delete from client.tb_orgao_julgador_competencia;"+
						   "delete from client.tb_orgao_julgador;"+
						   "delete from public.tb_usuario_localizacao;"+
						   "delete from public.tb_localizacao where id_localizacao_pai is not null;"+
						   "delete from public.tb_usuario_localizacao;";
			ps = connection.prepareStatement(query);	
			ps.executeUpdate();
			System.out.println("A tabela tb_fluxo está limpa com sucesso!!!");
		} finally {
			if (ps != null){
				ps.close();
			}
		}
	}
	
	
	//Inseri na tabela tb_usuario_localizacao um Usuário adimnistrador
	public void addUsuarioLocalizacao() throws SQLException{
		PreparedStatement ps = null;
		try {
			String query = "insert into public.tb_usuario_localizacao (id_usuario, " +
						   "in_responsavel_localizacao, id_localizacao, id_papel) " +
						   "values ( (select id_usuario from acl.tb_usuario_login where ds_nome =  'Administrador'), " +
						   "'S', (select id_localizacao from public.tb_localizacao where id_localizacao_pai is null limit 1), "+
						   "(select id_papel from acl.tb_papel where ds_identificador = 'Administrador'));";
			ps = connection.prepareStatement(query);	
			ps.executeUpdate();
			System.out.println("Foi inserido dados com sucesso na tb_usuario_localizacao!!!");
		} finally {
			if (ps != null){
				ps.close();
			}
		}
	}
}