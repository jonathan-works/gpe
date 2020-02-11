package br.com.infox.epp.documento.manager;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
import br.com.infox.epp.documento.dao.VariavelDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.type.Expression;
import br.com.infox.epp.documento.type.ExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * Classe Manager para a entidade ModeloDocumento
 * 
 * @author erikliberal
 */
@Stateless
@Name(ModeloDocumentoManager.NAME)
@AutoCreate
public class ModeloDocumentoManager extends Manager<ModeloDocumentoDAO, ModeloDocumento> {
    
    private static final long serialVersionUID = 4455754174682600299L;
    private static final LogProvider LOG = Logging.getLogProvider(ModeloDocumentoManager.class);
    protected static final String NAME = "modeloDocumentoManager";
    
    @Inject
    private VariavelDAO variavelDAO;
    
    //TODO verificar se esse método ainda é utilizado, senão, remover
    public String getConteudoModeloDocumento(ModeloDocumento modeloDocumento) {
        if (modeloDocumento != null) {
            return evaluateModeloDocumento(modeloDocumento);
        } else {
            return "";
        }
    }

    /**
     * Retorna todos os Modelos de Documento ativos
     * 
     * @return lista de modelos de documento ativos
     */
    public List<ModeloDocumento> getModeloDocumentoList() {
        return getDao().getModeloDocumentoList();
    }

    public ModeloDocumento getModeloDocumentoByTitulo(String titulo) {
        return getDao().getModeloDocumentoByTitulo(titulo);
    }

    public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(
            GrupoModeloDocumento grupo, TipoModeloDocumento tipo) {
        return getDao().getModeloDocumentoByGrupoAndTipo(grupo, tipo);
    }

    public List<ModeloDocumento> getModelosDocumentoInListaModelo(String listaModelos) {
        String[] tokens = listaModelos.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String token : tokens) {
            ids.add(Integer.valueOf(token));
        }
        return getDao().getModelosDocumentoInListaModelos(ids);
    }

    /**
     * Realiza conversão de Modelo de Documento, para Documento final
     * 
     * Este método busca linha a linha pelos nomes das variáveis do sistema para
     * substitui-las por seus respectivos valores
     * 
     * @param modeloDocumento Modelo de Documento não nulo a ser usado na tarefa
     * @param resolver 
     * @return Documento contendo valores armazenados nas variáveis inseridas no
     *         modelo
     */
    public String evaluateModeloDocumento(ModeloDocumento modeloDocumento) {
		SeamExpressionResolver seamExpressionResolver = new SeamExpressionResolver();
        return evaluateModeloDocumento(modeloDocumento, seamExpressionResolver);
    }
    
    /**
     * Realiza conversão de Modelo de Documento, para Documento final, verificando variáveis do jBPM
     * 
     * Este método busca linha a linha pelos nomes das variáveis do sistema para
     * substituí-las por seus respectivos valores. Caso a variável exista no mapa de variáveis, ela é
     * analisada de acordo com seu tipo e substituída por um valor formatado adequado
     * 
     * @param modeloDocumento Modelo de Documento não nulo a ser usado na tarefa
     * @param variableTypeMap Mapa de variáveis do jBPM e seus tipos
     * @return Documento contendo valores armazenados nas variáveis inseridas no
     *         modelo
     */
    public String evaluateModeloDocumento(ModeloDocumento modeloDocumento, ExpressionResolver resolver) {
    	if (modeloDocumento == null) {
    		return null;
    	}
        return evaluateModeloDocumento(modeloDocumento, modeloDocumento.getModeloDocumento(), resolver);
    }
    
    public String evaluateModeloDocumento(ModeloDocumento modeloDocumento, String texto, ExpressionResolver resolver) {
    	if (modeloDocumento == null) {
            return null;
        }
        StringBuilder modeloProcessado = new StringBuilder();
        String[] linhas = texto.split("\n");

        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("#[{][^{}]+[}]");
        Map<String, String> map = getVariaveis(modeloDocumento.getTipoModeloDocumento());

        for (int i = 0; i < linhas.length; i++) {
            if (modeloProcessado.length() > 0) {
                modeloProcessado.append('\n');
                sb.delete(0, sb.length());
            }

            Matcher matcher = pattern.matcher(linhas[i]);

            while (matcher.find()) {
                String group = matcher.group();
                String variableName = group.substring(2, group.length() - 1);
                String expression = map.get(variableName);
                if (expression == null) {
                	expression = group;
                }
                expression = unscapeHtmlFromExpression(expression);
                String value = "";
                if (!expression.startsWith("#{modelo:")) {
	            	Expression expr = new Expression(expression);
	            	if (resolver != null) {
	            		try {
	            			expr = resolver.resolve(expr);
	            		} catch (RuntimeException e) {
	            			modeloProcessado.append("Erro na linha: '" + linhas[i]);
	                        modeloProcessado.append("': " + e.getMessage());
	                        LOG.error(".appendTail()", e);
	            		}
	                }
	                // Os caracteres \ e $ devem ser escapados devido ao funcionamento do método
	                // Matcher#appendReplacement (ver o Javadoc correspondente).
	                // Importante manter a ordem dos replaces abaixo
	            	value = expr.isResolved() ? expr.getValue() : "";
	            	value = value == null ? "" : value;
	                value = value.replace("\\", "\\\\").replace("$", "\\$");
                } else {
                	String titulo = expression.substring("#{modelo:".length(), expression.length()-1);
                	ModeloDocumento modeloDocumentoInside = getModeloDocumentoByTitulo(titulo);
                	if (modeloDocumentoInside != null) {
                		value = evaluateModeloDocumento(modeloDocumentoInside, resolver);
                	} else {
                		value = value == null ? "" : value;
                		value = value.replace("\\", "\\\\").replace("$", "\\$");
                	}
                }
                matcher.appendReplacement(sb, value);
            }
            matcher.appendTail(sb);
            modeloProcessado.append(sb.toString());
        }
        return modeloProcessado.toString();
    }
    
    /**
     * Recupera variáveis atreladas a um tipo de documento.
     * 
     * @param tipo Tipo do Documento a que as variáveis são atribuídas
     * @return Mapa de Variáveis em que o Nome é a chave de busca e os valores
     *         são os resultados
     */
    private Map<String, String> getVariaveis(TipoModeloDocumento tipo) {
        List<Variavel> list = new ArrayList<Variavel>();
        if (tipo != null) {
            list = variavelDAO.getVariaveisAtivasByTipoModeloDocumento(tipo);
        }
        Map<String, String> map = new HashMap<>();
        int flag = 0x0;
        for (Variavel variavel : list) {
            String valorVariavel = variavel.getValorVariavel();
            if ("#{loginUsuarioRec}".equals(valorVariavel)) {
                valorVariavel="<loginUsuarioRec>";
                flag = flag | 0x1;
            } else if ("#{senhaUsuarioRec}".equals(valorVariavel)) {
                valorVariavel="<senhaUsuarioRec>";
                flag = flag | 0x2;
            } else if ("#{nomeUsuarioRec}".equals(valorVariavel)) {
                flag = flag | 0x4;
                valorVariavel="<nomeUsuarioRec>";
            }
            map.put(variavel.getVariavel(), valorVariavel);
        }
        if ((flag&0x1) != 0x1) {
            map.put("loginUsuarioRec", "<loginUsuarioRec>");
        }
        if ((flag&0x2) != 0x2) {
            map.put("senhaUsuarioRec", "<senhaUsuarioRec>");
        }
        if ((flag&0x4) != 0x4) {
            map.put("nomeUsuarioRec", "<nomeUsuarioRec>");
        }
        return map;
    }
    
    private static String unscapeHtmlFromExpression(String expression) {
        expression = StringEscapeUtils.unescapeHtml4(expression);
        expression = expression.replaceAll("\u00A0", "");
        return expression;
    }

    public String getConteudo(int idModeloDocumento) {
        final ModeloDocumento modeloDocumento = find(idModeloDocumento);
        return evaluateModeloDocumento(modeloDocumento);
    }
    
    public String getConteudo(String codigoModeloDocumento, ExpressionResolver resolver) {
    	CriteriaBuilder cb = getDao().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ModeloDocumento> cq = cb.createQuery(ModeloDocumento.class);
		Root<ModeloDocumento> fromModelo = cq.from(ModeloDocumento.class);
		cq.where(cb.equal(fromModelo.get(ModeloDocumento_.codigo), cb.literal(codigoModeloDocumento)));
    	ModeloDocumento modeloDocumento = getDao().getEntityManager().createQuery(cq).getSingleResult(); 
        return evaluateModeloDocumento(modeloDocumento, resolver);
    }
    
    public List<ModeloDocumento> getModeloDocumentoByTipo(TipoModeloDocumento tipoModeloDocumento) {
    	return getDao().getModeloDocumentoByTipo(tipoModeloDocumento);
    }
    
    public List<ModeloDocumento> getModeloDocumentoByPapel(Papel papel) {
        return getDao().getModeloDocumentoByPapel(papel);
    }
    
    public String resolverModeloComContexto(Integer idProcesso, String codigoModelo, Object contexto) {
        if (contexto == null)
            return "";
        StringBuilder sb = new StringBuilder();
        if (contexto instanceof Iterable) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Iterable<? extends Object> iterable = (Iterable)contexto;
            sb.append(resolveModelo(idProcesso, codigoModelo, iterable));
        } else {
            sb.append( resolveModelo(idProcesso, codigoModelo, contexto) );
        }
        return sb.toString();
    }
    private String resolveModelo(Integer idProcesso, String codigoModelo, Iterable<? extends Object> iterable) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<? extends Object> it = iterable.iterator(); it.hasNext();) {
            sb.append( resolveModelo(idProcesso, codigoModelo, it.next()) );
        }
        return sb.toString();
    }
    
    private String resolveModelo(Integer idProcesso, String codigoModelo, Object object) {
    	final ExecutionContext newExecContext = new ExecutionContext(ExecutionContext.currentExecutionContext());
    	try {
    		ExecutionContext.pushCurrentContext(newExecContext);
    		newExecContext.getContextInstance().addVariables(objectToMap(object));
    		ExpressionResolver resolver = ExpressionResolverChainBuilder.defaultExpressionResolverChain(idProcesso, newExecContext);
    		return getConteudo(codigoModelo, resolver);
    	} finally {
    		ExecutionContext.popCurrentContext(newExecContext);
    	}
    }
    
    private static Map<String, Object> objectToMap(Object object){
        if (object instanceof Map) {
            HashMap<String, Object> map = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<Object, Object> originalMap = (Map<Object,Object>)object;
            for (Entry<Object,Object> entry : originalMap.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                map.put(key, value);
                map.put(MessageFormat.format("#'{'{0}'}'", key), value);
            }
            return map;
        }
        try {
            HashMap<String, Object> map = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass(), Object.class)
                    .getPropertyDescriptors()) {
                String key = propertyDescriptor.getName();
                Object value = propertyDescriptor.getReadMethod().invoke(object);
                map.put(MessageFormat.format("#'{'{0}'}'", key), value);
                map.put(key, value);
            }
            return map;
        } catch (IntrospectionException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
    
}