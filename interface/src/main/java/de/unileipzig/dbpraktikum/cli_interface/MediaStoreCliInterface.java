package de.unileipzig.dbpraktikum.cli_interface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.ui.TuiApplication;

/** Starts the CLI with a configurable database interface. */
public class MediaStoreCliInterface {
    // Default hibernate properties file
    private static final String DEFAULT_PROPERTIES_FILE = "hibernate.properties";

    // Default name of DB Interface class
    private static final String DB_INTERFACE_CLASS_PROPERTY = "db.interface.class";

    public static void main(String[] args) {
        DBInterface db = null;
        
        try {
            Properties properties = loadProperties(args);
            db = createDBInterface(properties);

            // The implementation setting is only used by the CLI, not by Hibernate.
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

    /** Loads an explicitly supplied configuration or the bundled default. */
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

    /** Creates the configured DBInterface implementation via reflection. */
    private static DBInterface createDBInterface(Properties properties) throws ReflectiveOperationException {
        String className = properties.getProperty(DB_INTERFACE_CLASS_PROPERTY);
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing property: " + DB_INTERFACE_CLASS_PROPERTY);
        }

        Class<?> implementationClass = Class.forName(className.trim());
        if (!DBInterface.class.isAssignableFrom(implementationClass)) {
            throw new IllegalArgumentException(className + " does not implement CBInterface");
        }

        return DBInterface.class.cast(implementationClass.getDeclaredConstructor().newInstance());
    }
}
