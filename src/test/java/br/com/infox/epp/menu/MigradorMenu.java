package br.com.infox.epp.menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import br.com.infox.core.util.XmlUtil;
import br.com.infox.epp.menu.api.Menu;

public class MigradorMenu {

    private String getFormatedKey(String key) {
        if (key.startsWith("/")) {
            return key.substring(1);
        } else {
            return key;
        }
    }
    
    private Menu buildItems(List<String> items){
        Menu menu = new MenuImpl();
        for (String key : items) {
            String[] split = key.split(":");
            key = split[0];
            String url = null;
            if (split.length > 1) {
                url = split[1];
            }
            buildItem(key, url, menu);
        }
        return menu;
    }
    
    private void buildItem(String key, String url, Menu menu) {
        String formatedKey = getFormatedKey(key);
        String[] groups = formatedKey.split("/");
        MenuImpl layoutMenu = new MenuImpl();
        Menu parent = layoutMenu;
        for (int i = 0; i < groups.length; i++) {
            String label = groups[i].trim();
            if (i < (groups.length - 1)) {
                SubmenuImpl item = new SubmenuImpl();
                item.setLabel(label);
                parent.add(item);
                parent = item;
            } else {
                MenuItemImpl item = new MenuItemImpl();
                item.setLabel(label);
                item.setUrl(url.replaceFirst("seam$", "xhtml"));
                parent.add(item);
            }
        }
        menu.addAll(layoutMenu.getItems());
    }
    private Document parseXmlInputStream(InputStream inputStream)
            throws ParserConfigurationException, SAXException, DocumentException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        Document document = new SAXReader(saxParser.getXMLReader()).read(inputStream);
        return document;
    }
    private Element getItemsElement(InputStream inputStream) throws DocumentException{
        try {
            Document document = parseXmlInputStream(inputStream);
            Element property = document.getRootElement();
            return property;
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("unchecked")
    private List<String> getItemsFromXml(InputStream inputStream) throws DocumentException {
        List<String> list = new ArrayList<>();
        Element property = getItemsElement(inputStream);
        for (Iterator<Element> iterator = property.elementIterator(); iterator.hasNext();) {
            Element element = iterator.next();
            list.add(element.getTextTrim());
        }
        return list;
    }
    private List<String> getItemsFromProperties(InputStream inputStream){
        List<String> list = new ArrayList<>();
        
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String key : p.stringPropertyNames()) {
            list.add(p.getProperty(key));
        }
        
        return list;
    }
    
    private void convertToNewXmlFormat(String fromPath, String toPath) throws FileNotFoundException {
        List<String> itemsFromXml;
        try {
            itemsFromXml = getItemsFromXml(new FileInputStream(fromPath));
        } catch (DocumentException e) {
            itemsFromXml = getItemsFromProperties(new FileInputStream(fromPath));
        }
        
        Menu menu = buildItems(itemsFromXml);
        if (toPath == null){
        XmlUtil.saveToXml(menu, System.out);
        } else {
            XmlUtil.saveToXml(menu, new FileOutputStream(toPath));
        }
    }

    /** EXECUTAR VIA MAVEN
     * Ex.:
     * Substituir arquivo xml por novo formato 
     * $ mvn exec:java@migrador-menu -Din="$(pwd)/src/main/resources/META-INF/menu/navigationMenu.xml" -Dout="$(pwd)/src/main/resources/META-INF/menu/navigationMenu.xml"
     * Escrever novo formato no stdout  
     * $ mvn exec:java@migrador-menu -Din="$(pwd)/src/main/resources/META-INF/menu/navigationMenu.xml"
     * Criar novo formato a partir de arquivo properties  
     * $ mvn -f epp/pom.xml exec:java@migrador-menu -Din="$(pwd)/tcepeextended/src/main/resources/menu.properties" -Dout="$(pwd)/tcepeextended/src/main/resources/menu.extension.xml"
     * Escrever novo formato no stdout  
     * $ mvn -f epp/pom.xml exec:java@migrador-menu -Din="$(pwd)/tcepeextended/src/main/resources/menu.properties"
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 1)
            new MigradorMenu().convertToNewXmlFormat(args[0], args[1]);
        else if (args.length > 0)
            new MigradorMenu().convertToNewXmlFormat(args[0], null);
    }
}
