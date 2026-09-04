package de.unileipzig.dbpraktikum.cli_interface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.ui.TuiApplication;

public class MediaStoreCliInterface {
    private static final String DEFAULT_PROPERTIES_FILE = "hibernate.properties";
    private static final String DB_INTERFACE_CLASS_PROPERTY = "db.interface.class";

    public static void main(String[] args) {
        DBInterface db = null;
        
        try {
            Properties properties = loadProperties(args);
            db = createDBInterface(properties);

            Properties databaseProperties = new Properties();
            databaseProperties.putAll(properties);
            databaseProperties.remove(DB_INTERFACE_CLASS_PROPERTY);
            db.init(databaseProperties);

            TuiApplication tui = new TuiApplication(db);
            tui.run();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (db != null) {
                db.finish();
            }
        }
    }

    private static Properties loadProperties(String[] args) throws IOException {
        Properties properties = new Properties();

        if (args != null && args.length > 0 && !args[0].trim().isEmpty()) {
            try (InputStream input = new FileInputStream(args[0])) {
                properties.load(input);
            }
            return properties;
        }

        try (InputStream input = MediaStoreCliInterface.class.getClassLoader()
                .getResourceAsStream(DEFAULT_PROPERTIES_FILE)) {
            if (input == null) {
                throw new IOException("Could not find " + DEFAULT_PROPERTIES_FILE + ".");
            }
            properties.load(input);
        }

        return properties;
    }

    private static DBInterface createDBInterface(Properties properties) throws ReflectiveOperationException {
        String className = properties.getProperty(DB_INTERFACE_CLASS_PROPERTY);
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Missing property: " + DB_INTERFACE_CLASS_PROPERTY
            );
        }

        Class<?> implementationClass = Class.forName(className.trim());
        if (!DBInterface.class.isAssignableFrom(implementationClass)) {
            throw new IllegalArgumentException(
                className + " does not implement " + DBInterface.class.getName()
            );
        }

        return DBInterface.class.cast(implementationClass.getDeclaredConstructor().newInstance());
    }
}
