package br.com.infox.epp.fluxo.xpdl;

import java.io.InputStream;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * Deprecated : Utilizada apenas para o XPDL, cuja funcionalidade será
 * totalmente reimplementada
 * */
@Deprecated
public final class XmlUtil {

    private static final LogProvider LOG = Logging.getLogProvider(XmlUtil.class);

    private XmlUtil() {
    }

    public static Document readDocument(InputStream file) {
        Document doc = null;
        try {
            SAXBuilder builder = getSAXBuilder();
            if (file != null) {
                doc = builder.build(file);
            }
        } catch (Exception ex) {
            LOG.error(".readDocument()", ex);
        }
        return doc;
    }

    public static SAXBuilder getSAXBuilder() {
        SAXBuilder builder = new SAXBuilder();
        builder.setXMLReaderFactory(XMLReaders.NONVALIDATING);
        builder.setFeature("http://apache.org/xml/features/"
                + "nonvalidating/load-external-dtd", false);
        return builder;
    }

    public static List<Element> getChildren(Element element, String name) {
        return element.getChildren(name, element.getNamespace());
    }

    /**
     * Retorna torna o elemento filho na posição especificada no parâmetro index
     * 
     * @param element
     * @param name
     * @param index
     * @return
     */
    public static Element getChildByIndex(Element element, String name,
            int index) {
        return getChildren(element, name).get(index);
    }

    public static String getAttributeValue(Element element, String name) {
        String temp = element.getAttributeValue(name);
        if (temp == null) {
            temp = element.getAttributeValue(name, element.getNamespace());
        }
        return temp;
    }
}
