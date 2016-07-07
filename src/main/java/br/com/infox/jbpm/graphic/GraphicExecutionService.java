package br.com.infox.jbpm.graphic;

import java.awt.Point;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GraphicExecutionService {
    
    @Inject
    private FluxoManager fluxoManager;
    
    public String performGraphicExecution(Token token, Map<String, GraphImageBean> graphImageBeans) throws TransformerFactoryConfigurationError {
        String svg = fluxoManager.getFluxoByDescricao(token.getProcessInstance().getProcessDefinition().getName()).getSvgExecucao();
        try {
            Document document = createDocument(svg);
            
            XPath xPath =  XPathFactory.newInstance().newXPath();
            
            Set<Long> tokens = getTokens(token);
            
            List<GraphImageBean> graphImageBeanList = getGraphImageBeanList(tokens);
            
            NodeLog nodeLog = new NodeLog(token.getNode(), token.getNodeEnter(), DateTime.now().toDate());
            nodeLog.setToken(token);
            
            graphImageBeanList.add(new NodeGraphImage(nodeLog, true));
            
            for (GraphImageBean graphElementBean : graphImageBeanList) {
                
                Node graphNode = (Node) xPath.compile("//g[@data-element-id='" + graphElementBean.getKey() + "']/g[@class='djs-visual']/*[1]").evaluate(document, XPathConstants.NODE);
                
                if (graphNode != null) {
                    graphImageBeans.put(graphElementBean.getKey(), graphElementBean);
                    Node node = (Node) xPath.compile("//g[@data-element-id='" + graphElementBean.getKey() + "']").evaluate(document, XPathConstants.NODE);
                    Element newChild = null;
                    if (token.getNode().getKey().equals(graphElementBean.getKey())) {
                        newChild = createCurrentNodeElement(graphNode, document, 6);
                        newChild.setAttribute("style", "stroke-width: 2; fill: #ff0000; fill-opacity: 0.18; stroke: #ff0000;");
                        node.appendChild(newChild);
                    } else {
                        Node attrStyle = graphNode.getAttributes().getNamedItem("stroke");
                        attrStyle.setTextContent("#FF0000");
                    }
                    if (!"path".equals(graphNode.getNodeName())) {
                        Node styleNode = node.getAttributes().getNamedItem("style");
                        styleNode.setTextContent(styleNode.getTextContent() + " cursor: pointer;");
                        ((Element) node).setAttribute("onclick", "onSelectNodeElement(" + getParameters(graphElementBean) + ")");
                    }
                }
            }
            
            return writeDocumentToString(document);
        } catch (Exception e) {
            return "Erro ao renderizar gráfico: " + e.getMessage();
        }
    }

    private String writeDocumentToString(Document document)
            throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        return stringWriter.toString();
    }

    private Document createDocument(String svg) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setValidating(false);
        docFactory.setNamespaceAware(true);
        docFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        docFactory.setFeature("http://xml.org/sax/features/validation", false);
        docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(IOUtils.toInputStream(svg));
        return document;
    }
    
    public List<GraphImageBean> getGraphImageBeanList(Collection<Long> tokensId) {
        List<GraphImageBean> graphImageBeans = new ArrayList<>();
        List<TransitionLog> transitionLogs = listTransitionsLogged(tokensId);
        for (TransitionLog transitionLog : transitionLogs) {
            graphImageBeans.add(new TransitionGraphImage(transitionLog));
        }
        List<NodeLog> nodesLog = listNodesLogged(tokensId);
        for (NodeLog nodeLog : nodesLog) {
            graphImageBeans.add(new NodeGraphImage(nodeLog));
        }
        return graphImageBeans;
    }

    private List<TransitionLog> listTransitionsLogged(Collection<Long> tokensId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TransitionLog> cq = cb.createQuery(TransitionLog.class);
        Root<TransitionLog> transitionLog = cq.from(TransitionLog.class);
        transitionLog.fetch("token");
        transitionLog.fetch("transition");
        cq.select(transitionLog);
        cq.where(
            transitionLog.<Token>get("token").<String>get("id").in(tokensId)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    private List<NodeLog> listNodesLogged(Collection<Long> tokensId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<NodeLog> cq = cb.createQuery(NodeLog.class);
        Root<NodeLog> nodeLog = cq.from(NodeLog.class);
        nodeLog.fetch("token");
        nodeLog.fetch("node");
        cq.select(nodeLog);
        cq.where(
            nodeLog.<Token>get("token").<String>get("id").in(tokensId)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
    
    private Set<Long> getTokens(Token token) {
        Set<Long> tokens = new HashSet<>();
        tokens.add(token.getId());
        if (token.getChildren() != null) {
            Collection<Token> children = token.getChildren().values();
            for (Token child : children) {
                tokens.add(child.getId());
            }
        }
        while (token.getParent() != null) {
            token = token.getParent();
            tokens.add(token.getId());
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
    
    public String getUsuariosExecutaramNo(NodeGraphImage nodeGraphImage) {
        TaskNode taskNode = (TaskNode) HibernateUtil.removeProxy(nodeGraphImage.getNode());
        Token token = nodeGraphImage.getToken();
        Set<Task> tasks = taskNode.getTasks();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<UsuarioLogin> usuarioLogin = cq.from(UsuarioLogin.class);
        cq.select(usuarioLogin.get(UsuarioLogin_.nomeUsuario));
        cq.where(
            cb.equal(taskInstance.<Token>get("token").<Long>get("id"), cb.literal(token.getId())),
            taskInstance.<Task>get("task").in(tasks),
            cb.equal(usuarioLogin.get(UsuarioLogin_.login), taskInstance.<String>get("assignee"))
        );
        List<String> resultList = getEntityManager().createQuery(cq).getResultList();
        return StringUtil.concatList(resultList, ", ");
    }
    
    private String getParameters(GraphImageBean graphImageBean) {
        return "[{name: 'key', value:'" + graphImageBean.getKey() + "'}]";
    }

}
