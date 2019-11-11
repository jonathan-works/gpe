package br.com.infox.epp.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;

import br.com.infox.epp.system.Database.DatabaseType;

public final class DatabaseFactory {

    private static Properties databases;
    
    static {
        try {
            Enumeration<URL> databasePropertiesUrls = DatabaseFactory.class.getClassLoader().getResources("database.properties");
            TreeMap<Integer, URL> sortedProperties = new TreeMap<>();
            while (databasePropertiesUrls.hasMoreElements()) {
                URL databasePropertyUrl = databasePropertiesUrls.nextElement();
                Scanner scanner = new Scanner(databasePropertyUrl.openStream());
                Integer priority = Integer.valueOf(scanner.nextLine().split("=")[1]);
                sortedProperties.put(priority, databasePropertyUrl);
                scanner.close();
            }

            databases = new Properties();
            for (Integer priority : sortedProperties.keySet()) {
                InputStream is = sortedProperties.get(priority).openStream();
                databases.load(is);
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading database.properties", e);
        }
    }
    
    public static Database create(String productName) {
        DatabaseType databaseType = DatabaseType.fromProductName(productName);
        if (databaseType == null) {
            throw new IllegalArgumentException("Unknown database type '" + productName + "' ");
        }
     
        try {
            return (Database) Class.forName(databases.getProperty(databaseType.name())).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading database class '" + databases.getProperty(databaseType.name()) + "'", e);
        }
    }
}
