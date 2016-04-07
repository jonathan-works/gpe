package br.com.infox.jpa.packaging;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.annotations.common.util.ReflectHelper;
import org.hibernate.ejb.packaging.NamedInputStream;
import org.hibernate.ejb.packaging.NativeScanner;
import org.hibernate.ejb.packaging.Scanner;

public class EntityScanner implements Scanner {
    
    private static final Logger LOGGER = Logger.getLogger(EntityScanner.class.getName()); 
    private Scanner scanner;
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public EntityScanner () {
        try {
            Class<? extends Scanner> scannerClass = ReflectHelper.classForName("org.jboss.as.jpa.hibernate4.HibernateAnnotationScanner", org.hibernate.ejb.Ejb3Configuration.class);
            scanner = scannerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            scanner = new NativeScanner(); // TOMCAT
        }
    }

    @Override
    public Set<Package> getPackagesInJar(URL jartoScan, Set<Class<? extends Annotation>> annotationsToLookFor) {
        return scanner.getPackagesInJar(jartoScan, annotationsToLookFor);
    }

    @Override
    public Set<Class<?>> getClassesInJar(URL jartoScan, Set<Class<? extends Annotation>> annotationsToLookFor) {
        return scanner.getClassesInJar(jartoScan, annotationsToLookFor);
    }

    @Override
    public Set<NamedInputStream> getFilesInJar(URL jartoScan, Set<String> filePatterns) {
        Set<NamedInputStream> filesInJar = scanner.getFilesInJar(jartoScan, filePatterns);
        readExtendedMappings(jartoScan, filesInJar);
        return filesInJar;
    }

    @Override
    public Set<NamedInputStream> getFilesInClasspath(Set<String> filePatterns) {
        return scanner.getFilesInClasspath(filePatterns); //not Implemented
    }

    @Override
    public String getUnqualifiedJarName(URL jarUrl) {
        return scanner.getUnqualifiedJarName(jarUrl);
    }
    
    private void readExtendedMappings(URL jartoScan, Set<NamedInputStream> filesInJar) {
        try {
            Enumeration<URL> files = getClass().getClassLoader().getResources("META-INF/extended-mappings.xml");
            while (files.hasMoreElements()) {
                URL file = files.nextElement();
                filesInJar.add(new NamedInputStream(file.getPath(), file.openStream()));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fail to read files extended-mappings.xml", e);
        }
    }

}
