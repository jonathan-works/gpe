package org.hibernate.ejb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

public class HibernatePersistenceImpl extends HibernatePersistence {
    
    @SuppressWarnings({ "rawtypes" })
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        loadExtendedMappingFiles(info);
       return super.createContainerEntityManagerFactory(info, properties);
    }
    
    private void loadExtendedMappingFiles(PersistenceUnitInfo info) {
        List<String> mappingFiles = info.getMappingFileNames();
        try {
            Enumeration<URL> files = getClass().getClassLoader().getResources("META-INF/extended-mappings.xml");
            while (files.hasMoreElements()) {
                URL file = files.nextElement();
                mappingFiles.add(file.toURI().toASCIIString());
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Fail to read files extended-mappings.xml", e);
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(HibernatePersistenceImpl.class.getName());
}
