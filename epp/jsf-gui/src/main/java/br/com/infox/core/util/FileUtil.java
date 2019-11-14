package br.com.infox.core.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public final class FileUtil {

    private static final LogProvider LOG = Logging.getLogProvider(FileUtil.class);

    private FileUtil() {
    }

    public static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.error(".close()", e);
            }
        }
    }

    public static String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret.toLowerCase();
    }
    
    public static File getTempFolder(){
        try {
            return getServletTempFolder();
        } catch (Exception e) {
            return getDefaultTempFolder();
        }
    }
    
    private static File getDefaultTempFolder(){
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    private static File getServletTempFolder(){
        File tempFolder = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = null;
        if (facesContext != null){
            request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        } else {
            request = Beans.getReference(HttpServletRequest.class);
        }
        tempFolder = (File)request.getServletContext().getAttribute(ServletContext.TEMPDIR);
        return tempFolder;
    }
    
    public static List<Path> find(String startingFrom, String pattern, boolean findFirst) {
        try {
            ResourceFrameFinder finder = new ResourceFrameFinder(pattern, findFirst);
            Files.walkFileTree(Paths.get(startingFrom), finder);
            return finder.getPathsMatched();
        } catch (IOException e) {
            LOG.error("", e);
            return Collections.emptyList();
        }
    }
    
    public static Path findFirst(String startingFrom, String pattern) {
        List<Path> findMatchedFiles = find(startingFrom, pattern, true);
        return findMatchedFiles.isEmpty() ? null : findMatchedFiles.get(0);
    }
    
    public static boolean isImagem(String extensao) {
        return "jpg".equalsIgnoreCase(extensao) || "jpeg".equalsIgnoreCase(extensao) || "png".equalsIgnoreCase(extensao) ||
                "gif".equalsIgnoreCase(extensao) || "bmp".equalsIgnoreCase(extensao); 
    }
    
    public static boolean isDocumento(String extensao){
        return "doc".equalsIgnoreCase(extensao) || "docx".equalsIgnoreCase(extensao) || "odt".equalsIgnoreCase(extensao);
    }
    
    public static boolean isPlanilha(String extensao){
        return "xls".equalsIgnoreCase(extensao) || "xlsx".equalsIgnoreCase(extensao) || "ods".equalsIgnoreCase(extensao);
    }
    
    public static boolean isVideo(String extensao) {
        return "avi".equalsIgnoreCase(extensao) || "mpeg".equalsIgnoreCase(extensao) || "mov".equalsIgnoreCase(extensao) ||
               "webm".equalsIgnoreCase(extensao) || "mkv".equalsIgnoreCase(extensao) || "mp4".equalsIgnoreCase(extensao)
               || "swf".equalsIgnoreCase(extensao);
    }
    
    public static class ResourceFrameFinder extends SimpleFileVisitor<Path> {

        private PathMatcher matcher;
        private boolean findFirst;
        private List<Path> fileMatches = new ArrayList<>();

        public ResourceFrameFinder(String pattern) {
            this(pattern, false);
        }
        
        public ResourceFrameFinder(String pattern, boolean findFirst) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            this.findFirst = findFirst;
        }
        
        public ResourceFrameFinder(PathMatcher matcher) {
            this.matcher = matcher;
        }

        public List<Path> getPathsMatched() {
            return fileMatches;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file != null && matcher.matches(file)) {
                fileMatches.add(file);
            } else if (file != null && file.toString().endsWith(".jar")) {
				URI jarFile = URI.create("jar:file:" + file.toUri().getPath());
				FileSystem jarFileS;
				try {
					jarFileS = getFileSystemFromJar(jarFile);
					ResourceFrameFinder finder = new ResourceFrameFinder(matcher);
					Path rootPath = jarFileS.getPath("/fragmentos");
					Files.walkFileTree(rootPath, finder);
					fileMatches.addAll(finder.getPathsMatched());
				} catch (IOException e) {
					LOG.info(e);
				}
            }
            if (!fileMatches.isEmpty() && findFirst) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        private FileSystem getFileSystemFromJar(URI jarFile) throws IOException{
        	Map<String, String> jarProperties = new HashMap<>();
        	jarProperties.put("create", "false");
        	jarProperties.put("encoding", "UTF-8");
        	FileSystem jarFileS;
        	try {
        		jarFileS = FileSystems.getFileSystem(jarFile);
        	} catch (FileSystemNotFoundException e) {
        		jarFileS = FileSystems.newFileSystem(jarFile, jarProperties);
        	}
        	return jarFileS;
        }
    }
    

}
