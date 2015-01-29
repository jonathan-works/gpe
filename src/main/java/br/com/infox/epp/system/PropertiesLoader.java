package br.com.infox.epp.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * Classe que carrega páginas customizadas pelo cliente e as insere no ePP
 * dinamicamente, ou substitui as páginas padrão do ePP, caso essas existam.
 * 
 * @author avner
 */
@Singleton
@Startup
public class PropertiesLoader {

    public static final String JNDI_PORTABLE_NAME = "java:module/PropertiesLoader";

    private static final LogProvider LOG = Logging
            .getLogProvider(PropertiesLoader.class);

    private static final String PAGE_PROPERTIES = "/custom_pages.properties";
    private static final String MENU_PROPERTIES = "/menu.properties";
//    private static final String MESSAGES_PROPERTIES = "/extended_messages.properties";
//    private static final String ENTITY_MESSAGES_EPP_PATH = "/entity_messages_pt_BR.properties";
//    private static final String MESSAGES_EPP_PATH = "/messages_pt_BR.properties";
//    private static final String STANDARD_MESSAGES_EPP_PATH = "/standard_messages_pt_BR.properties";
//    private static final String PROCESS_DEFINITION_MESSAGES_EPP_PATH = "/process_definition_messages_pt_BR.properties";
//    private static final String VALIDATION_MESSAGES = "/ValidationMessages.properties";

    private Properties pageProperties;
    private Properties menuProperties;
    private List<String> menuItems;
    private Map<String, String> messages;

    @PostConstruct
    private void init() {
        loadPageProperties();
//        loadMessagesProperties();
        // Para o reCAPTCHA
        System.setProperty("networkaddress.cache.ttl", "30");
        System.setProperty("sun.net.inetaddr.ttl", "30");
    }

    private void loadPageProperties() {
        InputStream is = getClass().getResourceAsStream(PAGE_PROPERTIES);
        if (is != null) {
            try {
                String appPath = getAppPath();
                System.out.println(appPath);
                pageProperties = new Properties();
                pageProperties.load(is);

                Enumeration<Object> keys = pageProperties.keys();
                while (keys.hasMoreElements()) {
                    String key = (keys.nextElement().toString());
                    String value = pageProperties.getProperty(key);

                    performLoad(key, value, appPath);
                }
            } catch (IOException e) {
                LOG.error(
                        "Falha ao recuperar arquivos especificados no Properties Loader.",
                        e);
            }
        }
    }

    private void performLoad(String key, String path, String appPath)
            throws IOException {
        InputStream newInputStream = getClass().getResourceAsStream(key);
        if (newInputStream != null) {
            File file = new File(appPath + path);
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileOutputStream newOutputStream = new FileOutputStream(file);

            int length;
            byte[] data = new byte[1024];
            while ((length = newInputStream.read(data)) != -1) {
                newOutputStream.write(data, 0, length);
            }
            newInputStream.close();
            newOutputStream.close();
        }
    }

//    private void loadMessagesProperties() {
//        InputStream isEntityMessagesEpp = getClass().getResourceAsStream(
//                ENTITY_MESSAGES_EPP_PATH);
//        InputStream isMessagesEpp = getClass().getResourceAsStream(
//                MESSAGES_EPP_PATH);
//        InputStream isProcessDefinitionMessagesEpp = getClass()
//                .getResourceAsStream(PROCESS_DEFINITION_MESSAGES_EPP_PATH);
//        InputStream isStandardMessagesEpp = getClass().getResourceAsStream(
//                STANDARD_MESSAGES_EPP_PATH);
//        InputStream isValidationMessages = getClass().getResourceAsStream(
//                VALIDATION_MESSAGES);
//        InputStream isMessagesExt = getClass().getResourceAsStream(
//                MESSAGES_PROPERTIES);
//
//        try {
//            messages = new DefaultMap<>(new HashMap<String, String>());
//
//            Properties source = new Properties();
//            source.load(isEntityMessagesEpp);
//            copyProperties(source, messages);
//
//            source = new Properties();
//            source.load(isMessagesEpp);
//            copyProperties(source, messages);
//
//            source = new Properties();
//            source.load(isProcessDefinitionMessagesEpp);
//            copyProperties(source, messages);
//
//            source = new Properties();
//            source.load(isStandardMessagesEpp);
//            copyProperties(source, messages);
//
//            source = new Properties();
//            source.load(isValidationMessages);
//            copyProperties(source, messages);
//
//            if (isMessagesExt != null) {
//                source = new Properties();
//                source.load(isMessagesExt);
//                copyProperties(source, messages);
//            }
//            this.messages = Collections.unmodifiableMap(messages);
//        } catch (IOException e) {
//            // TODO logar este erro.
//            e.printStackTrace();
//        }
//    }

//    private void copyProperties(Properties source,
//            Map<String, String> destination) {
//        Enumeration<Object> srcKeys = source.keys();
//        while (srcKeys.hasMoreElements()) {
//            String key = srcKeys.nextElement().toString();
//            String value = source.getProperty(key);
//            destination.put(key, value);
//        }
//    }

    private String getAppPath() {
        URL thisPackage = getClass().getResource("");
        File file = new File(thisPackage.getFile());
        while (!file.toPath().endsWith("WEB-INF")) {
            file = file.getParentFile();
        }
        return file.getParent(); // o WAR
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<String> getMenuItems() {
        if (menuProperties == null) {
            menuProperties = new Properties();
            InputStream is = getClass().getResourceAsStream(MENU_PROPERTIES);
            if (is != null) {
                try {
                    menuProperties.load(is);
                    menuItems = Collections.unmodifiableList(new ArrayList(
                            menuProperties.values()));
                } catch (IOException e) {
                    LOG.error(
                            "Falha ao recuperar arquivos especificados no Properties Loader.",
                            e);
                }
            }
        }
        return (menuItems == null ? (menuItems = Collections
                .unmodifiableList(new ArrayList<String>())) : menuItems);
    }

    public Map<String, String> getMessages() {
        return this.messages;
    }
}