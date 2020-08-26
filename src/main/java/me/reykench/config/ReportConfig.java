package me.reykench.config;

import me.reykench.BungeeUtils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ReportConfig {

    private File file;
    private Configuration config;

    public int bugreportcount = 0;
    public int reportcount = 0;

    public ReportConfig() {

        if (!BungeeUtils.getInstance().getDataFolder().exists()) {
            BungeeUtils.getInstance().getDataFolder().mkdirs();
        }

        file = new File(BungeeUtils.getInstance().getDataFolder(), "reports.yml");

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

            config.set("bugreport_count", 0);
            config.set("report_count", 0);

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

        bugreportcount = config.getInt("bugreport_count");
        reportcount = config.getInt("report_count");

        save();
    }

    public void increaseReportCount() {
        reportcount++;
        config.set("report_count", reportcount);
        save();
    }

    public void increaseBugReportCount() {
        bugreportcount++;
        config.set("bugreport_count", bugreportcount);
        save();
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
