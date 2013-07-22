/*
  IBPM - Ferramenta de produtividade Java
  Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.
 
  Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
  sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
  Free Software Foundation; vers�o 2 da Licen�a.
  Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
  NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
  ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
  
  Consulte a GNU GPL para mais detalhes.
  Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
  veja em http://www.gnu.org/licenses/  
 */
package br.com.infox.ibpm.jbpm.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.core.Expressions;

import br.com.infox.epp.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Variavel;
import br.com.infox.ibpm.jbpm.ActionTemplate;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.manager.VariavelManager;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;

@Name(ModeloDocumentoAction.NAME)
@Scope(ScopeType.SESSION)
@Startup
public class ModeloDocumentoAction extends ActionTemplate {
	
	public static final String NAME = "modeloDocumento";
	private static final long serialVersionUID = 1L;
	
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	
	@In private VariavelManager variavelManager;
	
	private ModeloDocumento modeloJbpm;
	
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoManager.getModeloDocumentoList();
	}

	public ModeloDocumento getModeloJbpm() {
		return modeloJbpm;
	}
	public void setModeloJbpm(ModeloDocumento modeloJbpm) {
		this.modeloJbpm = modeloJbpm;
	}

	@Override
	public String getExpression() {
		return "modeloDocumento.set";
	}
	
	@Override
	public String getFileName() {
		return "setModeloDocumento.xhtml";
	}
	
	@Override
	public String getLabel() {
		return "Atribuir modelo a uma vari�vel";
	}
	
	@Override
	public boolean isPublic() {
		return false;
	}
	
	@Override
	public void extractParameters(String expression) {
		if (expression == null || "".equals(expression)) {
			return;
		}
		parameters = getExpressionParameters(expression);
		if (parameters.length > 0) {
			ProcessBuilder.instance().getTaskFitter().getCurrentTask().setCurrentVariable(
					(String) parameters[0]);
		}
	}

	public void set(String variavel, int... idModeloDocumento) {
		variavel += "Modelo";
		StringBuilder s = new StringBuilder();
		for (int i : idModeloDocumento) {
			if (s.length() != 0) {
				s.append(",");
			}
			s.append(i);
		}
		Object valor = JbpmUtil.getProcessVariable(variavel);
		if (valor == null) {
			JbpmUtil.createProcessVariable(variavel, s.toString());
		} else {
			JbpmUtil.setProcessVariable(variavel, valor + "," + s.toString());
		}
	}

	private EntityManager getEntityManager() {
		EntityManager em = EntityUtil.getEntityManager();
		return em;
	}
	
	/**
	 * Recupera vari�veis atreladas a um tipo de documento.
	 * 
	 * @param tipo Tipo do Documento a que as vari�veis s�o atribu�das
	 * @return Mapa de Vari�veis em que o Nome � a chave de busca e os valores s�o os resultados
	 */
	private Map<String, String> getVariaveis(TipoModeloDocumento tipo)	{
		List<Variavel> list = new ArrayList<Variavel>();
		if (tipo != null) {
			list = variavelManager.getVariaveisByTipoModeloDocumento(tipo);
		}
		Map<String, String> map = new HashMap<String, String>();
		for (Variavel variavel : list) {
			map.put(variavel.getVariavel(), variavel.getValorVariavel());
		}
		return map;
	}
	
	public String getConteudo(int idModeloDocumento)	{
		return getConteudo(getModeloDocumento(idModeloDocumento));
	}
	
	public ModeloDocumento getModeloDocumento(int idModeloDocumento)	{
		return modeloDocumentoManager.find(ModeloDocumento.class, idModeloDocumento);
	}

	public String getConteudo(String tituloModeloDocumento) {
		return getConteudo(getModeloDocumento(tituloModeloDocumento));		
	}

	public ModeloDocumento getModeloDocumento(String tituloModeloDocumento)	{
		return modeloDocumentoManager.getModeloDocumentoByTitulo(tituloModeloDocumento);
	}
	
	/**
	 * Realiza convers�o de Modelo de Documento, para Documento final
	 * 
	 * Este m�todo busca linha a linha pelos nomes das vari�veis
	 * do sistema para substitui-las por seus respectivos valores 
	 * 
	 * @param modeloDocumento	Modelo de Documento n�o nulo a ser usado na tarefa
	 * @return					Documento contendo valores armazenados nas vari�veis inseridas no modelo
	 */
	public String getConteudo(ModeloDocumento modeloDocumento) {
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
				e.printStackTrace();
			}
		}
		return modeloProcessado.toString();	
	}
	
	@SuppressWarnings("unchecked")
	public List<ModeloDocumento> getModeloItems(String variavel) {
		String listaModelos = (String) new Util().eval(variavel);
		List<ModeloDocumento> list = getEntityManager()
			.createQuery("select o from ModeloDocumento o " +
					"where o.idModeloDocumento in (" +
					listaModelos + ") order by modeloDocumento")
			.getResultList();
		return list;
	}
	
	public static ModeloDocumentoAction instance()  {
		return (ModeloDocumentoAction) Component.getInstance(ModeloDocumentoAction.class);
	}


}