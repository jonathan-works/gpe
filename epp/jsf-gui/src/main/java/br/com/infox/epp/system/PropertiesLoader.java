package br.com.infox.epp.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.infox.core.util.XmlUtil;
import br.com.infox.epp.menu.MenuImpl;
import br.com.infox.epp.menu.api.Menu;
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

    private static final LogProvider LOG = Logging.getLogProvider(PropertiesLoader.class);

    private static final String PAGE_PROPERTIES = "/custom_pages.properties";
    private static final String MENU_PROPERTIES = "menu.properties.xml";

    private Properties pageProperties;
    private Map<String, String> messages;

    @PostConstruct
    private void init() {
        loadPageProperties();
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

    private String getAppPath() {
        URL thisPackage = getClass().getResource("");
        File file = new File(thisPackage.getFile());
        while (!file.toPath().endsWith("WEB-INF")) {
            file = file.getParentFile();
        }
        return file.getParent(); // o WAR
    }

    public Menu getMenu(){
        Menu result = new MenuImpl();
        try {
            Enumeration<URL> menuUrls = getClass().getClassLoader().getResources(MENU_PROPERTIES);
            while (menuUrls.hasMoreElements()) {
                InputStream is = menuUrls.nextElement().openStream();
                Menu menu = XmlUtil.loadFromXml(MenuImpl.class, is);
                result.addAll(menu.getItems());
            }
        } catch (IOException e) {
            LOG.error("Falha ao recuperar arquivos especificados no Properties Loader.", e);
        }
        return result;
    }

    public Map<String, String> getMessages() {
        return this.messages;
    }
}