package me.reykench.config;

import me.reykench.BungeeUtils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MySQLConfig {

    public String hostname, database, username, password;
    public int port;

    private File file;
    private Configuration config;

    public MySQLConfig() {

        if (!BungeeUtils.getInstance().getDataFolder().exists()) {
            BungeeUtils.getInstance().getDataFolder().mkdirs();
        }

        file = new File(BungeeUtils.getInstance().getDataFolder(), "mysql.yml");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            config.set("hostname", "127.0.0.1");
            config.set("database", "bungeecore_data");
            config.set("username", "admin");
            config.set("password", "123");
            config.set("port", 3306);

            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        hostname = config.getString("hostname");
        database = config.getString("database");
        username = config.getString("username");
        password = config.getString("password");
        port = config.getInt("port");

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
