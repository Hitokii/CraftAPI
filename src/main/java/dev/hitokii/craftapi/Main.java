package dev.hitokii.craftapi;

import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dev.hitokii.craftapi.commands.StartServer;
import dev.hitokii.craftapi.commands.StopServer;
import dev.hitokii.craftapi.commands.registerChest;

public class Main extends JavaPlugin {
  @Override
  public void onEnable() {

    getCommand("stopserver").setExecutor(new StopServer());
    getCommand("startserver").setExecutor(new StartServer());
    getCommand("registerchest").setExecutor(new registerChest());

    ServerClass server = ServerClass.getInstance();
    server.startServer();

    // check if config exists
    if (!this.getConfig().isSet("port"))
      this.saveDefaultConfig();
    
  }

  @Override
  public void onDisable() {
    ServerClass server = ServerClass.getInstance();
    server.stopServer();
    getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
  }

  public Plugin getDPlugin() {
    return this;
  }
}
