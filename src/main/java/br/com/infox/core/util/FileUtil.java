package br.com.infox.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
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
                Map<String, String> jarProperties = new HashMap<>();
                URI jarFile = URI.create("jar:file:" + file.toUri().getPath());
                jarProperties.put("create", "false");
                jarProperties.put("encoding", "UTF-8");
                ResourceFrameFinder finder = new ResourceFrameFinder(matcher);
                try (FileSystem jarFileS = FileSystems.newFileSystem(jarFile, jarProperties)) {
                    Path rootPath = jarFileS.getPath("/fragmentos");
                    Files.walkFileTree(rootPath, finder);
                } catch (IOException e) {
                    LOG.error(e);
                }
                fileMatches.addAll(finder.getPathsMatched());
            }
            if (!fileMatches.isEmpty() && findFirst) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

    }

}
