package me.dynmie.aoc.yukino.utils;

import java.io.*;
import java.util.Properties;

public class Config extends Properties {

    private final File file;

    public Config(File file, Properties defaults) {
        super(defaults);

        this.file = file;

        if (!file.exists() && defaults != null) {
            saveProperties(defaults, file);
        }

        saveDefaults();
        load();
    }

    public void saveDefaults() {
        if (file.exists()) return;
        save();
    }

    public void save() {
        saveProperties(this, file);
    }

    public void load() {
        try (FileReader reader = new FileReader(file)) {
            load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String property) {
        return Integer.parseInt(this.getProperty(property));
    }

    public int getInt(String property, int def) {
        try {
            return Integer.parseInt(this.getProperty(property));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public double getDouble(String property) {
        return Double.parseDouble(this.getProperty(property));
    }

    public double getDouble(String property, double def) {
        try {
            return Double.parseDouble(this.getProperty(property));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public float getFloat(String property) {
        return Float.parseFloat(this.getProperty(property));
    }

    public float getFloat(String property, float def) {
        try {
            return Float.parseFloat(this.getProperty(property));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public long getLong(String property) {
        return Long.parseLong(this.getProperty(property));
    }

    public long getLong(String property, long def) {
        try {
            return Long.parseLong(this.getProperty(property));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean getBoolean(String property) {
        return "true".equalsIgnoreCase(this.getProperty(property));
    }

    public boolean getBoolean(String property, boolean def) {
        String value = this.getProperty(property);

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        return def;
    }

    public static Config getDefaultConfig(File file, String path) {
        Properties properties = new Properties();
        try (InputStream stream = Config.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return new Config(file, properties);
            }
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Config(file, properties);
    }

    public static void saveProperties(Properties properties, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, " Configuration");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
