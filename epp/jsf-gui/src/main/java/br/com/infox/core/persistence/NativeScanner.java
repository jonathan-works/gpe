package br.com.infox.core.persistence;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.AssertionFailure;
import org.hibernate.ejb.packaging.ClassFilter;
import org.hibernate.ejb.packaging.Entry;
import org.hibernate.ejb.packaging.ExplodedJarVisitor;
import org.hibernate.ejb.packaging.FileFilter;
import org.hibernate.ejb.packaging.FileZippedJarVisitor;
import org.hibernate.ejb.packaging.Filter;
import org.hibernate.ejb.packaging.InputStreamZippedJarVisitor;
import org.hibernate.ejb.packaging.JarProtocolVisitor;
import org.hibernate.ejb.packaging.JarVisitor;
import org.hibernate.ejb.packaging.NamedInputStream;
import org.hibernate.ejb.packaging.PackageFilter;
import org.hibernate.ejb.packaging.Scanner;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;

import br.com.infox.epp.system.Configuration;

public class NativeScanner implements Scanner {

    private static final String META_INF_ORM_XML = "META-INF/orm.xml";

    private Map<URL, StateJarVisitor> visitors = new HashMap<URL, StateJarVisitor>();
    private static final int PACKAGE_FILTER_INDEX = 0;
    private static final int CLASS_FILTER_INDEX = 1;
    private static final int FILE_FILTER_INDEX = 2;
    private static final int FILE_EXTENDED_FILTER_INDEX = 3;

    private String persistenceUnitName;

    public NativeScanner(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * This implementation does not honor the list of annotations and return
     * everything. Must strictly be used by HEM
     */
    @SuppressWarnings("unchecked")
    public Set<Package> getPackagesInJar(URL jarToScan, Set<Class<? extends Annotation>> annotationsToLookFor) {
        if (annotationsToLookFor.size() > 0) {
            throw new AssertionFailure("Improper use of NativeScanner: must not filter packages");
        }

        JarVisitor jarVisitor = getVisitor(jarToScan);
        final Set<Entry> packageEntries;
        try {
            packageEntries = (Set<Entry>) jarVisitor.getMatchingEntries()[PACKAGE_FILTER_INDEX];
        } catch (IOException e) {
            throw new RuntimeException("Error while reading " + jarToScan.toString(), e);
        }
        Set<Package> packages = new HashSet<Package>(packageEntries.size());
        for (Entry entry : packageEntries) {
            try {
                packages.add(ReflectHelper.classForName(entry.getName() + ".package-info").getPackage());
            } catch (ClassNotFoundException e) {
                // should never happen, if it happens, simply ignore the flawed
                // package
            }
        }
        return packages;
    }

    /**
     * Build a JarVisitor with some assumptions wrt the scanning This helps do
     * one scan instead of several
     */
    private JarVisitor getVisitor(URL jar) {
        StateJarVisitor stateJarVisitor = visitors.get(jar);

        if (stateJarVisitor == null) {
            
            Filter[] filters = new Filter[3];
            filters[PACKAGE_FILTER_INDEX] = new PackageFilter(false, null) {
                public boolean accept(String javaElementName) {
                    return true;
                }
            };
            filters[CLASS_FILTER_INDEX] = new ClassFilter(false, new Class[] { Entity.class, MappedSuperclass.class, Embeddable.class }) {
                public boolean accept(String javaElementName) {
                    return true;
                }
            };
            filters[FILE_FILTER_INDEX] = new FileFilter(true) {
                public boolean accept(String javaElementName) {
                    return javaElementName.endsWith("hbm.xml") || javaElementName.endsWith(META_INF_ORM_XML);
                }
            };

            stateJarVisitor = new StateJarVisitor(getVisitor(jar, filters, ""));
            visitors.put(jar, stateJarVisitor);
        }
        return stateJarVisitor.visitor;
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> getClassesInJar(URL jarToScan, Set<Class<? extends Annotation>> annotationsToLookFor) {
        if (isValidForClasses(annotationsToLookFor)) {
            throw new AssertionFailure(
                    "Improper use of NativeScanner: " + "must not filter classes by other annotations than Entity, MappedSuperclass, embeddable");
        }
        JarVisitor jarVisitor = getVisitor(jarToScan);
        final Set<Entry> classesEntry;
        try {
            classesEntry = (Set<Entry>) jarVisitor.getMatchingEntries()[CLASS_FILTER_INDEX];
        } catch (IOException e) {
            throw new RuntimeException("Error while reading " + jarToScan.toString(), e);
        }
        Set<Class<?>> classes = new HashSet<Class<?>>(classesEntry.size());
        for (Entry entry : classesEntry) {
            try {
                classes.add(ReflectHelper.classForName(entry.getName()));
            } catch (ClassNotFoundException e) {
                // should never happen, if it happens, simply ignore the flawed
                // package
            }
        }
        return classes;
    }

    private boolean isValidForClasses(Set<Class<? extends Annotation>> annotationsToLookFor) {
        return annotationsToLookFor.size() != 3 || !annotationsToLookFor.contains(Entity.class)
                || !annotationsToLookFor.contains(MappedSuperclass.class) || !annotationsToLookFor.contains(Embeddable.class);
    }

    /**
     * support for patterns is primitive: - **\/*.hbm.xml Other patterns will
     * not be found
     */
    @SuppressWarnings("unchecked")
    public Set<NamedInputStream> getFilesInJar(URL jarToScan, Set<String> filePatterns) {
        StringBuilder sb = new StringBuilder("URL: ").append(jarToScan).append("\n");
        for (String pattern : filePatterns) {
            sb.append("  ").append(pattern).append("\n");
        }
        JarVisitor jarVisitor = getVisitor(jarToScan);

        // state visitor available
        final StateJarVisitor stateVisitor = visitors.get(jarToScan);
        if (stateVisitor.hasReadFiles) {
            throw new AssertionFailure("Cannot read files twice on NativeScanner");
        }
        stateVisitor.hasReadFiles = true;

        Set<String> endWiths = new HashSet<String>();
        Set<String> exacts = new HashSet<String>();
        for (String pattern : filePatterns) {
            if (pattern.startsWith("**/*")) {
                final String patternTail = pattern.substring(4, pattern.length());
                if (!patternTail.equals(".hbm.xml")) {
                    throw new AssertionFailure("Improper use of NativeScanner: " + "must not filter files via pattern other than .hbm.xml");
                }
                endWiths.add(patternTail);
            } else {
                exacts.add(pattern);
            }
        }

        final Set<Entry> fileEntries;
        try {
            fileEntries = (Set<Entry>) jarVisitor.getMatchingEntries()[FILE_FILTER_INDEX];
        } catch (IOException e) {
            throw new RuntimeException("Error while reading " + jarToScan.toString(), e);
        }
        Set<NamedInputStream> files = new HashSet<NamedInputStream>(fileEntries.size());
        Set<Entry> leftOver = new HashSet<Entry>(fileEntries);
        for (Entry entry : fileEntries) {
            boolean done = false;
            for (String exact : exacts) {
                if (entry.getName().equals(exact)) {
                    files.add(new NamedInputStream(entry.getName(), entry.getInputStream()));
                    leftOver.remove(entry);
                    done = true;
                }
            }
            if (done)
                continue;
            for (String endWithPattern : endWiths) {
                if (entry.getName().endsWith(endWithPattern)) {
                    files.add(new NamedInputStream(entry.getName(), entry.getInputStream()));
                    leftOver.remove(entry);
                }
            }

        }
        for (Entry entry : leftOver) {
            try {
                entry.getInputStream().close();
            } catch (IOException e) {
                // swallow as we don't care about these files
            }
        }
        loadExtendedMappingClasses(files);
        return files;
    }

    private void loadExtendedMappingClasses(Set<NamedInputStream> files) {
        if (Configuration.EPA_PERSISTENCE_UNIT_NAME.equals(persistenceUnitName)) {
            try {
                Enumeration<URL> extendedMappings = getClass().getClassLoader().getResources("META-INF/extended-mappings.xml");
                while (extendedMappings.hasMoreElements()) {
                    URL file = extendedMappings.nextElement();
                    files.add(new NamedInputStream(file.getPath(), file.openStream()));
                }
                extendedMappings = getClass().getClassLoader().getResources("META-INF/extended-named-queries.xml");
                while (extendedMappings.hasMoreElements()) {
                    URL file = extendedMappings.nextElement();
                    files.add(new NamedInputStream(file.getPath(), file.openStream()));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Fail to read files extended-mappings.xml", e);
            }
        }
    }

    public JarVisitor getVisitor(URL jarUrl, Filter[] filters, String entry) throws IllegalArgumentException {
        String protocol = jarUrl.getProtocol();
        if ("jar".equals(protocol)) {
            return new JarProtocolVisitor(jarUrl, filters, entry);
        } else if (StringHelper.isEmpty(protocol) || "file".equals(protocol) || "vfs".equals(protocol) || "vfszip".equals(protocol)
                || "vfsfile".equals(protocol)) {
            File file;
            try {
                final String filePart = jarUrl.getFile();
                if (filePart != null && filePart.indexOf(' ') != -1) {
                    // unescaped (from the container), keep as is
                    file = new File(jarUrl.getFile());
                } else {
                    file = new File(jarUrl.toURI().getSchemeSpecificPart());
                }
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Unable to visit JAR " + jarUrl + ". Cause: " + e.getMessage(), e);
            }

            if (file.isDirectory()) {
                return new ExplodedJarVisitor(jarUrl, filters, entry);
            } else {
                return new FileZippedJarVisitor(jarUrl, filters, entry);
            }
        } else {
            // let's assume the url can return the jar as a zip stream
            return new InputStreamZippedJarVisitor(jarUrl, filters, entry);
        }
    }

    public Set<NamedInputStream> getFilesInClasspath(Set<String> filePatterns) {
        throw new AssertionFailure("Not implemented");
    }

    public String getUnqualifiedJarName(URL jarToScan) {
        JarVisitor jarVisitor = getVisitor(jarToScan);
        return jarVisitor.getUnqualifiedJarName();
    }

    private static class StateJarVisitor {
        StateJarVisitor(JarVisitor visitor) {
            this.visitor = visitor;
        }

        JarVisitor visitor;
        boolean hasReadFiles = false;
    }

}
