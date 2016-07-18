package br.com.infox.epp.menu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.component.menu.RecursoCreator;
import br.com.infox.epp.system.PropertiesLoader;
import br.com.infox.jsf.function.ElFunctions;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.security.SecurityUtil;

@Stateless
public class MenuService {

    @Inject private PathResolver pathResolver;
    @Inject private RecursoCreator roleCreator;
    @Inject private PropertiesLoader propertiesLoader;
    @Inject private SecurityUtil securityUtil;

    public List<MenuItemDTO> getMenuItemList() {
        List<String> items = getItemsFromNavigationMenuXml();
        List<MenuItemDTO> dropMenus = new ArrayList<>();
        
        for (String key : items) {
            String[] split = key.split(":");
            key = split[0];
            String url = null;
            if (split.length > 1) {
                url = split[1];
            }
            String pageRole = SecurityUtil.PAGES_PREFIX + url;
            if (securityUtil.checkPage(pageRole)) {
                buildItem(key, url, dropMenus);
            }
        }
        return dropMenus;
    }

    public void discoverAndCreateRoles() {
        try {
            Files.walkFileTree(new File(pathResolver.getContextRealPath()).toPath(), roleCreator);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    List<String> getItemsFromMainMenuComponentXml() {
        List<String> list = new ArrayList<>();
        InputStream inputStream = getResource("/META-INF/menu/mainMenu.component.xml");

        Element property = getPropertyElement(inputStream);
        for (Iterator<Element> iterator = property.elementIterator(); iterator.hasNext();) {
            Element element = iterator.next();
            list.add(element.getTextTrim());
        }
        list.addAll(propertiesLoader.getMenuItems());
        return list;
    }

    @SuppressWarnings("unchecked")
    List<String> getItemsFromNavigationMenuXml() {
        List<String> list = new ArrayList<>();
        InputStream inputStream = getResource("/META-INF/menu/navigationMenu.xml");

        Element property = getItemsElement(inputStream);
        for (Iterator<Element> iterator = property.elementIterator(); iterator.hasNext();) {
            Element element = iterator.next();
            list.add(element.getTextTrim());
        }
        list.addAll(propertiesLoader.getMenuItems());
        return list;
    }
    
    private Element getItemsElement(InputStream inputStream){
        try {
            Document document = parseXmlInputStream(inputStream);
            Element property = document.getRootElement();
            return property;
        } catch (ParserConfigurationException | SAXException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Element getPropertyElement(InputStream inputStream) {
        try {
            Document document = parseXmlInputStream(inputStream);
            Element property = document.getRootElement().element("component").element("property");
            return property;
        } catch (ParserConfigurationException | SAXException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlInputStream(InputStream inputStream)
            throws ParserConfigurationException, SAXException, DocumentException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        Document document = new SAXReader(saxParser.getXMLReader()).read(inputStream);
        return document;
    }

    private InputStream getResource(String relativePath) {
        return MenuNavigation.class.getResourceAsStream(relativePath);
    }

    String resolveLabel(String label) {
        String result = label.trim();
        if (result.startsWith("#{") && result.endsWith("}")) {
            result = ElFunctions.evaluateExpression(result, String.class);
        } else {
            result = InfoxMessages.getInstance().get(result);
        }
        return result;
    }

    void buildItem(String key, String url, List<MenuItemDTO> dropMenus) {
        String formatedKey = getFormatedKey(key);
        String[] groups = formatedKey.split("/");
        MenuItemDTO parent = new MenuItemDTO(null);
        for (int i = 0; i < groups.length; i++) {
            String label = groups[i].trim();
            label = resolveLabel(label);
            MenuItemDTO item = new MenuItemDTO(label);
            if (i == 0) {
                int j = dropMenus.indexOf(item);
                if (j != -1) {
                    parent = dropMenus.get(j);
                } else {
                    parent = item;
                    if (groups.length == 1) {
                        parent.setUrl(pathResolver.getContextPath(url));
                    }
                    dropMenus.add(parent);
                }
            } else if (i < (groups.length - 1)) {
                parent = parent.add(item);
            } else {
                item.setUrl(pathResolver.getContextPath(url));
                parent.add(item);
            }
        }
    }

    private String getFormatedKey(String key) {
        if (key.startsWith("/")) {
            return key.substring(1);
        } else {
            return key;
        }
    }

}
