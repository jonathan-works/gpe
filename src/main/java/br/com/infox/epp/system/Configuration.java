package br.com.infox.epp.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import br.com.infox.core.persistence.NativeScanner;
import br.com.infox.core.server.ServerInfo;

public class Configuration {

    public static final String EPA_PERSISTENCE_UNIT_NAME = "EPAPersistenceUnit";
    public static final String EPA_BIN_PERSISTENCE_UNIT_NAME = "EPABinPersistenceUnit";

    private ApplicationServer applicationServer;
    private Database database;
    private boolean desenvolvimento;

    private static Configuration INSTANCE;

    private Configuration(ApplicationServer applicationServer, Database database, boolean desenvolvimento) {
        this.applicationServer = applicationServer;
        this.database = database;
        this.desenvolvimento = desenvolvimento;
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public synchronized static Configuration createInstance() {
        if (INSTANCE == null) {
            ApplicationServer applicationServer = ServerInfo.createApplicationServer();
            Database database = ServerInfo.createDatabase(applicationServer);
            boolean isDesenvolvimento = performAttributeDesenvolvimento();
            INSTANCE = new Configuration(applicationServer, database, isDesenvolvimento);
        }
        return INSTANCE;
    }

    private static boolean performAttributeDesenvolvimento() {
        try {
            InputStream resourceAsStream = Configuration.class.getClassLoader().getResourceAsStream("META-INF/persistence.xml");
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(resourceAsStream);
            XPath xpath = XPathFactory.newInstance().newXPath();
            Node property = (Node) xpath.compile("//property[@name='hibernate.show_sql'][1]").evaluate(document, XPathConstants.NODE);
            String textContent = property.getAttributes().getNamedItem("value").getTextContent();
            return "true".equals(textContent);
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void configureQuartz(Properties properties) {
        applicationServer.performQuartzProperties(properties);
        database.performQuartzProperties(properties);
    }

    public Properties configureJpa(Properties properties, String persistenceUnitName) {
        applicationServer.performJpaCustomProperties(properties);
        database.performJpaCustomProperties(properties);
        properties.put("hibernate.ejb.resource_scanner", NativeScanner.class.getName());
        return properties;
    }

    public ApplicationServer getApplicationServer() {
        return applicationServer;
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isDesenvolvimento() {
        return desenvolvimento;
    }

}
