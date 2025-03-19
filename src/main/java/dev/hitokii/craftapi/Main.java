package dev.hitokii.craftapi;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.hitokii.craftapi.commands.StartServer;
import dev.hitokii.craftapi.commands.StopServer;
import dev.hitokii.craftapi.commands.registerChest;
import dev.hitokii.craftapi.commands.showText;

public class Main extends JavaPlugin {
  // This code is called after the server starts and after the /reload command
  @Override
  public void onEnable() {

    getCommand("stopserver").setExecutor(new StopServer());
    getCommand("startserver").setExecutor(new StartServer());
    getCommand("showtext").setExecutor(new showText());
    getCommand("registerchest").setExecutor(new registerChest());

    ServerClass server = ServerClass.getInstance();
    server.startServer();

  }

  // This code is called before the server stops and after the /reload command
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
