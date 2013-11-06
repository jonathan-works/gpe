package br.com.infox.epp.documento.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.documento.action.ModeloDocumentoAction;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

/**
 * Classe Manager para a entidade ModeloDocumento 
 * @author erikliberal
 */
@Name(ModeloDocumentoManager.NAME)
@AutoCreate
public class ModeloDocumentoManager extends GenericManager{
	private static final long serialVersionUID = 4455754174682600299L;
	private static final LogProvider LOG = Logging.getLogProvider(ModeloDocumentoManager.class);
	public static final String NAME = "modeloDocumentoManager";

	@In
	private ModeloDocumentoDAO modeloDocumentoDAO;
	
	@In private VariavelDAO variavelDAO;
	
	public String getConteudoModeloDocumento(ModeloDocumento modeloDocumento){
		if(modeloDocumento != null) {
		    return ModeloDocumentoAction.instance().getConteudo(modeloDocumento);
		} else {
		    return "";
		}
	}
	
	/**
	 * Retorna todos os Modelos de Documento ativos
	 * @return lista de modelos de documento ativos
	 */
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoDAO.getModeloDocumentoList();
	}
	
	public ModeloDocumento getModeloDocumentoByTitulo(String titulo){
		return modeloDocumentoDAO.getModeloDocumentoByTitulo(titulo);
	}
	
	public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(GrupoModeloDocumento grupo, TipoModeloDocumento tipo){
		return modeloDocumentoDAO.getModeloDocumentoByGrupoAndTipo(grupo, tipo);
	}
	
	public List<ModeloDocumento> getModelosDocumentoInListaModelo(String listaModelos){
		return modeloDocumentoDAO.getModelosDocumentoInListaModelos(listaModelos);
	}
	
	/**
	 * Realiza conversão de Modelo de Documento, para Documento final
	 * 
	 * Este método busca linha a linha pelos nomes das variáveis
	 * do sistema para substitui-las por seus respectivos valores 
	 * 
	 * @param modeloDocumento	Modelo de Documento não nulo a ser usado na tarefa
	 * @return					Documento contendo valores armazenados nas variáveis inseridas no modelo
	 */
	public String evaluateModeloDocumento(ModeloDocumento modeloDocumento) {
		if (modeloDocumento == null) {
			return null;
		}
		StringBuilder modeloProcessado = new StringBuilder();		
		String[] linhas = modeloDocumento.getModeloDocumento().split("\n");
		
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("#[{][^{}]+[}]");
		Map<String, String> map = getVariaveis(modeloDocumento.getTipoModeloDocumento());
		
		for (int i = 0; i < linhas.length; i++) {
			if (modeloProcessado.length() > 0) {
				modeloProcessado.append('\n');
				sb.delete(0, sb.length());
			}
			
			Matcher matcher = pattern.matcher(linhas[i]);
			
			while(matcher.find())	{
				String group = matcher.group();
				String variableName = group.substring(2, group.length()-1);
				String expression = map.get(variableName);
				if (expression == null) {
					matcher.appendReplacement(sb, group);
				} else {
					matcher.appendReplacement(sb, expression);
				}
			}
			matcher.appendTail(sb);
			
			try {
				String linha =(String) Expressions.instance()
					.createValueExpression(sb.toString()).getValue();
				modeloProcessado.append(linha);
			} catch (RuntimeException e) {
				modeloProcessado.append("Erro na linha: '" +linhas[i]);
				modeloProcessado.append("': " + e.getMessage());
				LOG.error(".appendTail()", e);
			}
		}
		return modeloProcessado.toString();
	}
	
	/**
	 * Recupera variáveis atreladas a um tipo de documento.
	 * 
	 * @param tipo Tipo do Documento a que as variáveis são atribuídas
	 * @return Mapa de Variáveis em que o Nome é a chave de busca e os valores são os resultados
	 */
	private Map<String, String> getVariaveis(TipoModeloDocumento tipo)	{
		List<Variavel> list = new ArrayList<Variavel>();
		if (tipo != null) {
			list = variavelDAO.getVariaveisByTipoModeloDocumento(tipo);
		}
		Map<String, String> map = new HashMap<String, String>();
		for (Variavel variavel : list) {
			map.put(variavel.getVariavel(), variavel.getValorVariavel());
		}
		return map;
	}

}