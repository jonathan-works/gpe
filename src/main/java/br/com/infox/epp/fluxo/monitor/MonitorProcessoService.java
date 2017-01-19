package br.com.infox.epp.fluxo.monitor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jbpm.graph.def.ProcessDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
public class MonitorProcessoService {

    @Inject
    private MonitorProcessoSearch monitorProcessoSearch;

    public MonitorProcessoDTO createSvgMonitoramentoProcesso(Fluxo fluxo, String key) throws TransformerException,
            ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document svgDocument = createDocument(fluxo.getDefinicaoProcesso().getSvgExecucao());
        ProcessDefinition processDefinition = monitorProcessoSearch.getProcessDefinitionByFluxo(fluxo);
        List<MonitorTarefaDTO> humanTaskList = monitorProcessoSearch.listTarefaHumanaByProcessDefinition(processDefinition.getId(), key);
        List<MonitorTarefaDTO> automaticNodeList = monitorProcessoSearch.listNosAutomaticosByProcessDefinition(processDefinition.getId(), key);
        adicionaInformacoesTarefaHumana(svgDocument, humanTaskList);
        adicionaInformacoesNosAutomaticos(svgDocument, automaticNodeList);
        String svg = documentToString(svgDocument);
        return new MonitorProcessoDTO(fluxo, processDefinition, humanTaskList, automaticNodeList, svg);
    }

    private void adicionaInformacoesTarefaHumana(Document doc, List<MonitorTarefaDTO> monitorProcessoList) throws XPathExpressionException {
        for (MonitorTarefaDTO mpDTO: monitorProcessoList) {
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String elementId = mpDTO.getKey();
            NodeList nodeList = (NodeList) xPath.compile("//g[@data-element-id='" + elementId + "']/g").evaluate(doc, XPathConstants.NODESET);
            Node item = nodeList.item(0);

            ((Element) item).setAttribute("onclick", "filterElement([{name:'elementId', value:'" + elementId + "'}])");

            Element circle = createCircleElement(doc, "15", "60", "21", "blue", "2", "white");
            item.appendChild(circle);

            Element text = createTextElement(doc, "66", "14", "blue", "style=\"font-size: 13px;\"", mpDTO.getQuantidade().toString());
            item.appendChild(text);
        }
    }

    private void adicionaInformacoesNosAutomaticos(Document doc, List<MonitorTarefaDTO> monitorProcessoList) throws XPathExpressionException {
        for (MonitorTarefaDTO mpDTO : monitorProcessoList) {
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String elementId = mpDTO.getKey();
            NodeList nodeList = (NodeList) xPath.compile("//g[@data-element-id='" + elementId + "']/g").evaluate(doc, XPathConstants.NODESET);
            Node item = nodeList.item(0);

            ((Element) item).setAttribute("onclick", "filterElement([{name:'elementId', value:'" + elementId + "'}])");

            Element circle = createCircleElement(doc, "15", "60", "21", "red", "2", "white");
            item.appendChild(circle);

            Element text = createTextElement(doc, "66", "14", "red", "style=\"font-size: 13px;\"", mpDTO.getQuantidade().toString());
            item.appendChild(text);
        }
    }

    private Document createDocument(String svgString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        f.setNamespaceAware(true);
        f.setFeature("http://xml.org/sax/features/namespaces", false);
        f.setFeature("http://xml.org/sax/features/validation", false);
        f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder documentBuilder = f.newDocumentBuilder();
        ByteArrayInputStream bais = new ByteArrayInputStream(svgString.getBytes());
        return documentBuilder.parse(bais);
    }

    private Element createCircleElement(Document doc, String r, String cy, String cx, String stroke, String strokeWidth, String fill) {
        Element circle = doc.createElement("circle");
        circle.setAttribute("r", r);
        circle.setAttribute("cy", cy);
        circle.setAttribute("cx", cx);
        circle.setAttribute("stroke", stroke);
        circle.setAttribute("stroke-width", strokeWidth);
        circle.setAttribute("fill", fill);
        return circle;
    }

    private Element createTextElement(Document doc, String y, String x, String fill, String style, String textContent) {
        Element text = doc.createElement("text");
        text.setAttribute("y", y);
        text.setAttribute("x", x);
        text.setAttribute("fill", fill);
        text.setAttribute("style", style);
        text.setTextContent(textContent);
        return text;
    }

    private String documentToString(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource,  result);
        return writer.toString();
    }
}
