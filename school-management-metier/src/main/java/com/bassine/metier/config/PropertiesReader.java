package com.bassine.metier.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String propertyFileName) throws IOException {
        InputStream is = getClass().getClassLoader()
            .getResourceAsStream(propertyFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }

    public Object getUrl() {
        return this.properties.getProperty("db.url");
    }

    public Object getUsername() {
        return this.properties.getProperty("db.username");
    }

    public Object getPassword() {
        return this.properties.getProperty("db.password");
    }
}