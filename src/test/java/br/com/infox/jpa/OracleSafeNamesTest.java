package br.com.infox.jpa;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class OracleSafeNamesTest {
    
    private static final String ROOT_FOLDER = "src/main/java";
    private static final String JAVA_SUFFIX = ".java";
    
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
                if (path.endsWith(JAVA_SUFFIX)) {
                    String className=path.substring(ROOT_FOLDER.length()+1, path.indexOf(JAVA_SUFFIX)).replace('/', '.');
                    Class<?> loadedClass = loader.loadClass(className);
                    if (loadedClass.getAnnotation(Table.class) != null) {
                        set.add(loadedClass);
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        
        return set.toArray(new Class<?>[set.size()]);
    }
    
    public List<String> getProblemsFor(Class<?> type) {
        List<String> problems = new ArrayList<>();
        Table table = type.getAnnotation(Table.class);
        String tableName=ObjectUtils.defaultIfNull(table.name(), Introspector.decapitalize(type.getClass().getSimpleName()));
        if (tableName.length() > 30) {
            problems.add(MessageFormat.format("Table name {0} for class {1} is more than 30 characters long", tableName, type.getClass()));
        }
//        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);
            if (col != null){
                String columnName = ObjectUtils.defaultIfNull(col.name(),Introspector.decapitalize(field.getName()));
                if (columnName.length() > 30){
                    problems.add(MessageFormat.format("Column name {0} for field {1} in class {2} is more than 30 characters long", columnName, field.getName(), type.getSimpleName()));
                }
            }
        }
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                Method method = ObjectUtils.firstNonNull(propertyDescriptor.getWriteMethod(), propertyDescriptor.getReadMethod());
                if (method==null){
                    continue;
                }
                Column col = method.getAnnotation(Column.class);
                if (col != null){
                    String columnName = ObjectUtils.defaultIfNull(col.name(),Introspector.decapitalize(propertyDescriptor.getName()));
                    if (columnName.length() > 30){
                        problems.add(MessageFormat.format("Column name {0} for property {1} in class {2} is more than 30 characters long", columnName, propertyDescriptor.getName(), type.getSimpleName()));
                    }
                }
            }
        } catch (IntrospectionException e){
            Logger.getGlobal().log(Level.FINEST, e.getMessage(), e);
        }
        
        return problems;
    }
    
    @Test
    public void testaNomesOracleSafe(){
        List<String> problems=new ArrayList<>();
        OracleSafeNamesTest safeNamesTest = new OracleSafeNamesTest();
        for (Class<?> type : safeNamesTest.getEntities()) {
            problems.addAll(safeNamesTest.getProblemsFor(type));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Falhas encontradas:\n");
        for (String string : problems) {
            sb.append(string).append("\n");
        }
        Assert.assertTrue(sb.toString(), problems.isEmpty());
    }
    
    public static void main(String[] args) {
        
    }
    
}
