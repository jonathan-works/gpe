package br.com.infox.core.util;

import java.io.Closeable;
import java.io.IOException;
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
import java.util.List;

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
        return ret;
    }
    
    public static List<Path> find(String startingFrom, String pattern) {
        try {
            Finder finder = new Finder(pattern);
            Files.walkFileTree(Paths.get(startingFrom), finder);
            return finder.getPathsMatched();
        } catch (IOException e) {
            LOG.error("", e);
            return Collections.emptyList();
        }
    }
    
    public static Path findFirst(String startingFrom, String pattern) {
        try {
            Finder finder = new Finder(pattern);
            Files.walkFileTree(Paths.get(startingFrom), finder);
            return finder.getPathsMatched().isEmpty() ? null : finder.getPathsMatched().get(0);
        } catch (IOException e) {
            LOG.error("", e);
            return null;
        }
    }
    
    public static class Finder extends SimpleFileVisitor<Path> {

        private PathMatcher matcher;
        private boolean findFirst;
        private List<Path> fileMatches = new ArrayList<>();

        public Finder(String pattern) {
            this(pattern, false);
        }
        
        public Finder(String pattern, boolean findFirst) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            this.findFirst = findFirst;
        }

        public void find(Path file) {
            if (file != null && matcher.matches(file)) {
                fileMatches.add(file);
            }
        }

        public List<Path> getPathsMatched() {
            return fileMatches;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            if (!fileMatches.isEmpty() && findFirst) return FileVisitResult.TERMINATE;
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            find(dir);
            if (!fileMatches.isEmpty() && findFirst) return FileVisitResult.TERMINATE;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            //do nothing
            return FileVisitResult.CONTINUE;
        }
    }

}
