package br.com.infox.epp.test.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Entity;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

public class ArquillianSeamTestSetup {

    private static final String DOT = ".";
    private static final String SLASH = "/";
    private static final String JAVA_SUFFIX = ".java";
    private static final String ROOT_FOLDER = "src/main/java/";
    private ArrayList<String> packages;
    private ArrayList<Class<?>> classes;
    private String archiveName;
    private String mockWebXMLPath;
    private String mockComponentsXMLPath;
    private String mockPersistenceXMLPath;
    private String pomPath;

    public ArquillianSeamTestSetup() {
        this.packages = new ArrayList<>();
        addPackages("br.com.infox.core", "br.com.infox.seam");
        this.classes = new ArrayList<>();
        this.archiveName = "epp-test.war";
        this.mockComponentsXMLPath = "src/test/resources/mock-components.xml";
        this.mockPersistenceXMLPath = "src/test/resources/mock-persistence.xml";
        this.mockWebXMLPath = "src/test/resources/mock-web.xml";
        this.pomPath = "pom.xml";
        addClasses(br.com.infox.epp.test.crud.AbstractCrudTest.class,br.com.infox.epp.test.crud.CrudActions.class,
                br.com.infox.epp.test.crud.AbstractCrudActions.class,br.com.infox.epp.test.crud.RunnableTest.class,
                br.com.infox.epp.test.crud.PersistSuccessTest.class);
    }
    
    public ArquillianSeamTestSetup addPackages(final String... packages) {
        for (String packageName : packages) {
            this.packages.add(packageName);
        }
        return this;
    }
    
    public ArquillianSeamTestSetup addPackage(final String packageName) {
        this.packages.add(packageName);
        return this;
    }
    
    public ArquillianSeamTestSetup addClass(final Class<?> clazz) {
        this.classes.add(clazz);
        return this;
    }
    
    public ArquillianSeamTestSetup addClasses(final Class<?>... classes) {
        for (Class<?> clazz : classes) {
            this.classes.add(clazz);
        }
        return this;
    }
    
    public ArquillianSeamTestSetup setArchiveName(final String archiveName) {
        this.archiveName = archiveName;
        return this;
    }
    
    public ArquillianSeamTestSetup setMockComponentsXMLPath(final String path) {
        this.mockComponentsXMLPath = path;
        return this;
    }
    
    public ArquillianSeamTestSetup setMockPersistenceXMLPath(final String path) {
        this.mockPersistenceXMLPath = path;
        return this;
    }
    
    public ArquillianSeamTestSetup setPomPath(final String pomPath) {
        this.pomPath = pomPath;
        return this;
    }
    
    public ArquillianSeamTestSetup setMockWebXMLPath(final String path) {
        this.mockWebXMLPath = path;
        return this;
    }
    
    private List<File> visitFile(final File file) {
        final ArrayList<File> filesToProcess = new ArrayList<>();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                filesToProcess.addAll(visitFile(child));
            }
        } else {
            filesToProcess.add(file);
        }
        
        return filesToProcess;
    }
    
    private Class<?>[] getEntities() {
        final HashSet<Class<?>> set = new HashSet<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final ArrayList<File> filesToProcess = new ArrayList<>();
        filesToProcess.addAll(visitFile(new File(ROOT_FOLDER)));
        
        for (File file : filesToProcess) {
            try {
                String path = file.getPath();
                if (path.contains(JAVA_SUFFIX)) {
                    Class<?> loadedClass = loader.loadClass(path.substring(ROOT_FOLDER.length(), path.indexOf(JAVA_SUFFIX)).replace(SLASH, DOT));
                    if (loadedClass.getAnnotation(Entity.class) != null) {
                        set.add(loadedClass);
                    } else if (loadedClass.isEnum()) {
                        set.add(loadedClass);
                    } else if (loadedClass.isInterface()) {
                        set.add(loadedClass);
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return set.toArray(new Class<?>[set.size()]);
    }
    
    private Class<?>[] getPackages(String... packages) {
        final HashSet<Class<?>> set = new HashSet<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final ArrayList<File> filesToProcess = new ArrayList<>();
        for (String packageName : packages) {
            final String treatedPackageName = packageName.replace(DOT, SLASH);
            final StringBuilder fileNameSB = new StringBuilder(ROOT_FOLDER).append(treatedPackageName);
            filesToProcess.addAll(visitFile(new File(fileNameSB.toString())));
        }
        
        for (File file : filesToProcess) {
            try {
                String path = file.getPath();
                if (path.contains(JAVA_SUFFIX)) {
                    Class<?> loadedClass = loader.loadClass(path.substring(14, path.indexOf(JAVA_SUFFIX)).replace(SLASH, DOT));
                    set.add(loadedClass);
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return set.toArray(new Class<?>[set.size()]);
    }
    
    private File[] getFilesFromPom(String pom) {
        final ArrayList<File> resultFiles = new ArrayList<>();
        final MavenResolverSystem resolver = Maven.resolver();
        final PomEquippedResolveStage loadPomFromFile = resolver.loadPomFromFile(new File(pom))
                                                        .importDependencies(ScopeType.RUNTIME,ScopeType.TEST,ScopeType.COMPILE);
        
        final File[] files = loadPomFromFile.resolve().withTransitivity().asFile();

        for (File file : files) {
            if (file.getName().matches("antlr.*.jar")) {
                continue;
            } else {
                resultFiles.add(file);
            }
        }
        
        return resultFiles.toArray(new File[resultFiles.size()]);
    }
    
    public WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, archiveName);
        if (packages.size() > 0) {
            webArchive.addClasses(getPackages(packages.toArray(new String[packages.size()])));
        }
        if (classes.size()>0) {
            webArchive.addClasses(classes.toArray(new Class<?>[classes.size()]));
        }
        if (this.mockWebXMLPath != null) {
            webArchive.addAsWebInfResource(new File(mockWebXMLPath),"web.xml");
        }
        if (this.mockComponentsXMLPath != null) {
            webArchive.addAsWebInfResource(new File(mockComponentsXMLPath),"components.xml");
        }
        if (this.pomPath != null) {
            webArchive.addAsLibraries(getFilesFromPom(pomPath));
        }
        if (this.mockPersistenceXMLPath != null) {
            webArchive.add(new FileAsset(new File(mockPersistenceXMLPath)),"WEB-INF/classes/META-INF/persistence.xml");
        }
        webArchive.addClasses(getEntities());
        webArchive.addAsResource(EmptyAsset.INSTANCE, "seam.properties");
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return webArchive;
    }

}
