package br.com.infox.bpm.cdi.qualifier;

import java.awt.Point;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ObjectUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.jsf.util.JsfUtil;

@Named
@ViewScoped
public class GraphicExecutionView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private JsfUtil jsfUtil;
    
    private Map<String, GraphImageBean> graphImageBeans;
    private TaskInstance taskInstance;
    private String svg;
    
    public void loadSvg() {
        graphImageBeans = new HashMap<>();
        String svg = fluxoManager.getFluxoByDescricao(taskInstance.getProcessInstance().getProcessDefinition().getName()).getSvgExecucao();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setValidating(false);
            docFactory.setNamespaceAware(true);
            docFactory.setFeature("http://xml.org/sax/features/namespaces", false);
            docFactory.setFeature("http://xml.org/sax/features/validation", false);
            docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(IOUtils.toInputStream(svg));
            
            XPath xPath =  XPathFactory.newInstance().newXPath();
            
            Token token = taskInstance.getToken();
            
            Set<Long> tokens = getTokens(token);
            
            List<GraphImageBean> graphImageBeanList = getGraphImageBeanList(tokens);
            
            graphImageBeanList.add(new GraphImageBean(token.getNode().getKey(), GraphImageBean.Type.NODE, true, token));
            
            for (GraphImageBean graphElementBean : graphImageBeanList) {
                
                Node graphNode = (Node) xPath.compile("//g[@data-element-id='" + graphElementBean.getKey() + "']/g[@class='djs-visual']/*[1]").evaluate(document, XPathConstants.NODE);
                
                if (graphNode != null) {
                    graphImageBeans.put(graphElementBean.getKey(), graphElementBean);
                    Node node = (Node) xPath.compile("//g[@data-element-id='" + graphElementBean.getKey() + "']").evaluate(document, XPathConstants.NODE);
                    Element newChild = null;
                    if (token.getNode().getKey().equals(graphElementBean.getKey())) {
                        newChild = createCurrentNodeElement(graphNode, document, 6);
                        newChild.setAttribute("style", "stroke-width: 2; fill: #ff0000; fill-opacity: 0.18; stroke: #ff0000; cursor: pointer;");
                    } else {
                        newChild = createCurrentNodeElement(graphNode, document, 0);
                        newChild.setAttribute("style", "fill: transparent; cursor: pointer;");
                        Node attrStyle = graphNode.getAttributes().getNamedItem("stroke");
                        attrStyle.setTextContent("#FF0000");
                    }
                    newChild.setAttribute("onclick", "onSelectGraphElement(" + getParameters(graphElementBean) + ")");
                    node.appendChild(newChild);
                }
            }
            
            StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(stringWriter);
            transformer.transform(source, result);
            this.svg = stringWriter.toString();
        } catch (Exception e) {
            this.svg = e.getMessage();
        }
    }
    
    public List<GraphImageBean> getGraphImageBeanList(Collection<Long> tokensId) {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        
        Root<TransitionLog> transitionLog = cq.from(TransitionLog.class);
        Join<TransitionLog, Token> token = transitionLog.join("token", JoinType.INNER);
        Join<TransitionLog, Transition> transition = transitionLog.join("transition", JoinType.INNER);
        Join<TransitionLog, org.jbpm.graph.def.Node> sourceNode = transitionLog.join("sourceNode", JoinType.INNER);
        
        Selection<String> sourceNodeKeySelection = sourceNode.<String>get("key").alias("sourceNodeKey");
        Selection<String> transitionKeySelection = transition.<String>get("key").alias("transitionKey");
        Selection<Token> tokenSelection = token.alias("token");
        
        cq.select(cb.tuple(sourceNodeKeySelection, transitionKeySelection, tokenSelection));
        
        cq.where(
              transitionLog.<Token>get("token").<String>get("id").in(tokensId)
        );
        
        cq.orderBy(cb.asc(transitionLog.get("date")));
        
        List<Tuple> keys = entityManager.createQuery(cq).getResultList();
        List<GraphImageBean> graphImageBeans = new ArrayList<>();
        
        for (Tuple key : keys) {
            String sourceNodeKey= key.get("sourceNodeKey", String.class);
            String transitionKey = key.get("transitionKey", String.class);
            Token tokenKey = key.get("token", Token.class);
            
            graphImageBeans.add(new GraphImageBean(sourceNodeKey, GraphImageBean.Type.NODE, tokenKey));
            graphImageBeans.add(new GraphImageBean(transitionKey, GraphImageBean.Type.TRANSITION, tokenKey));
        }
        
        return graphImageBeans;
    }
    
    private Set<Long> getTokens(Token token) {
        Set<Long> tokens = new HashSet<>();
        tokens.add(token.getId());
        while (token.getParent() != null) {
            token = token.getParent();
            tokens.add(token.getId());
        }
        if (token.getChildren() != null) {
            Collection<Token> children = token.getChildren().values();
            for (Token child : children) {
                tokens.add(child.getId());
            }
        }
        return tokens;
    }
    
    private Element createCurrentNodeElement(Node graphNode, Document document, int margin) {
        Element newChild = null;
        String name = graphNode.getNodeName();
        if ("polygon".equals(name)) {
            newChild = document.createElement("polygon");
            String[] points = graphNode.getAttributes().getNamedItem("points").getTextContent().split(",");
            Point point1 = new Point(Integer.valueOf(points[0]), Integer.valueOf(points[1]) - margin);
            Point point2 = new Point(Integer.valueOf(points[2]) + margin, Integer.valueOf(points[3]));
            Point point3 = new Point(Integer.valueOf(points[4]), Integer.valueOf(points[5]) + margin);
            Point point4 = new Point(Integer.valueOf(points[6]) - margin, Integer.valueOf(points[7]));
            newChild.setAttribute("points", point1.getX() + "," + point1.getY() + "," + point2.getX() + "," + point2.getY() + "," + point3.getX() + "," + point3.getY() + "," + point4.getX() + "," + point4.getY());
        } else if ("circle".equals(name)) {
            Integer radio = Integer.valueOf(graphNode.getAttributes().getNamedItem("r").getTextContent()); 
            newChild = document.createElement("circle");
            newChild.setAttribute("cx", graphNode.getAttributes().getNamedItem("cx").getTextContent());
            newChild.setAttribute("cy", graphNode.getAttributes().getNamedItem("cy").getTextContent());
            newChild.setAttribute("r", String.valueOf(radio + margin));
        } else if ("rect".equals(name)) {
            Integer height = Integer.valueOf(graphNode.getAttributes().getNamedItem("height").getTextContent());
            Integer width = Integer.valueOf(graphNode.getAttributes().getNamedItem("width").getTextContent());
            Integer x = Integer.valueOf(graphNode.getAttributes().getNamedItem("x").getTextContent());
            Integer y = Integer.valueOf(graphNode.getAttributes().getNamedItem("y").getTextContent());
            newChild = document.createElement("rect");
            newChild.setAttribute("ry", graphNode.getAttributes().getNamedItem("ry").getTextContent());
            newChild.setAttribute("rx", graphNode.getAttributes().getNamedItem("rx").getTextContent());
            newChild.setAttribute("height", String.valueOf(height + (2 * margin)));
            newChild.setAttribute("width", String.valueOf(width + (2 * margin)));
            newChild.setAttribute("y", String.valueOf(y - margin));
            newChild.setAttribute("x", String.valueOf(x - margin));
        } else if ("path".equals(name)) {
            newChild = document.createElement("path");
            newChild.setAttribute("d", graphNode.getAttributes().getNamedItem("d").getTextContent());
        }
        return newChild;
    }
    
    public void onSelectGraphElement() {
        String key = jsfUtil.getRequestParameter("key");
        GraphImageBean graphImageBean = graphImageBeans.get(key);
        System.out.println(key);
    }
    
    public void onCloseInformacoes() {
        
    }
    
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(TaskInstance taskInstance) {
        if (!ObjectUtil.equals(this.taskInstance, taskInstance)) {
            this.taskInstance = taskInstance;
            loadSvg();
        }
    }
    
    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
    
    private String getParameters(GraphImageBean graphImageBean) {
        return "[{name: 'key', value:'" + graphImageBean.getKey() + "'}]";
    }

    private static class GraphImageBean {
        
        private String key;
        private Token token;
        private Type type;
        private Boolean isCurrent;
        
        public GraphImageBean(String key, Type type, Token token) {
            this(key, type, false, token);
        }
        
        public GraphImageBean(String key, Type type, Boolean isCurrent, Token token) {
            this.key = key;
            this.type = type;
            this.isCurrent = isCurrent;
            this.token = token;
        }
        
        public String getKey() {
            return key;
        }
        
        public Type getType() {
            return type;
        }

        public Boolean isCurrent() {
            return isCurrent;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof GraphImageBean))
                return false;
            GraphImageBean other = (GraphImageBean) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }

        private enum Type {
            NODE, TRANSITION;
        }
    }

}
