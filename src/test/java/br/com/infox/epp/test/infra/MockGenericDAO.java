package br.com.infox.epp.test.infra;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;

public class MockGenericDAO extends GenericDAO {
    private static final long serialVersionUID = 1L;
    private SessionFactory sessionFactory;
    private Session session;

    @SuppressWarnings("deprecation")
    private SessionFactory createSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        Class<?>[] entities = getEntities();
        for (Class<?> class1 : entities) {
            configuration.addAnnotatedClass(class1);
        }
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.setProperty("hibernate.id.new_generator_mappings","false");
        //configuration.setProperty("hibernate.show_sql", "true");
        return configuration.buildSessionFactory();
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
        filesToProcess.addAll(visitFile(new File("src/main/java/")));
        
        for (File file : filesToProcess) {
            try {
                String path = file.getPath();
                if (path.contains(".java")) {
                    Class<?> loadedClass = loader.loadClass(path.substring(14, path.indexOf(".java")).replace("/", "."));
                    if (loadedClass.getAnnotation(Entity.class) != null) {
                        set.add(loadedClass);
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return set.toArray(new Class<?>[set.size()]);
    }
    
    public MockGenericDAO() {
        super();
        sessionFactory = createSessionFactory();
        session = sessionFactory.openSession();
    }
    
    @Override
    protected void finalize() throws Throwable {
        session.close();
        sessionFactory.close();
        super.finalize();
    }
    
    @Override
    public boolean contains(Object o) {
        return session.contains(o);
    }

    @Override
    @SuppressWarnings(UNCHECKED)
    public <T> T find(Class<T> c, Object id) {
        return (T) session.get(c, (Serializable) id);
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select o from ").append(clazz.getName()).append(" o");
        return session.createQuery(sb.toString()).list();
    }

    @Override
    public <T> List<T> getResultList(String query,
            Map<String, Object> parameters) {
        return super.getResultList(query, parameters);
    }
    
    @Override
    public <T> T getSingleResult(String query, Map<String, Object> parameters) {
        return super.getSingleResult(query, parameters);
    }

    @Override
    public <T> T merge(T object) throws DAOException {
        try {
            session.beginTransaction();
            session.update(object);
            return object;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    @Override
    public <T> T persist(T object) throws DAOException {
        try {
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            return object;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    @Override
    public <T> T remove(T object) throws DAOException {
        try {
            session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
        return object;
    }

    @Override
    public <T> T update(T object) throws DAOException {
        try {
            session.beginTransaction();
            session.update(object);
            session.getTransaction().commit();
            return object;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

//    private <T> Object getKey(T object) {
//        Object key=null;
//        Method[] methods = object.getClass().getMethods();
//        for (Method method : methods) {
//            if (method.isAnnotationPresent(Id.class)) {
//                try {
//                    key = method.invoke(object);
//                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//                }
//                break;
//            }
//        }
//        return key;
//    }

}
