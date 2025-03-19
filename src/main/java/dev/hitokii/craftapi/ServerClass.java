package dev.hitokii.craftapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ServerClass {
  
  // Instance
  private static ServerClass instance;
  private GsonBuilder builder = new GsonBuilder();
  private Gson gson = builder.create();

  // Variables
  private HttpServer server;
  private int port = 8080;
  private JsonObject jsonData = null;


  private HttpHandler handler = new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addJsonData("players", fetchUsers());
        // Fetch the latest data dynamically
        JsonObject latestData = getLatestData(); // Method to fetch the latest data
        String response = gson.toJson(latestData);

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
};
  

  // Constructor
  private ServerClass() {
    // Initialize the instance
    Logger.getLogger("Minecraft").info("Initializing server class");
    instance = this;
  }

  public void startServer() {
    Logger.getLogger("Minecraft").info("Starting server on port " + port);
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", handler);
      server.setExecutor(null);
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stopServer() {
    Logger.getLogger("Minecraft").info("Stopping server");
    server.stop(0);
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setDisplayData(String data) {
    if (handler != null) 
      server.removeContext("/");
    
    // Display the data
    handler = new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        // Handle the request

        String response = data;
        exchange.sendResponseHeaders(200, response.length());
                  try (OutputStream os = exchange.getResponseBody()) {
                      os.write(response.getBytes());
                  }

      }
    };

    server.createContext("/", handler);
  }

  public void clearJson() {
    jsonData = new JsonObject();
  }

  public void addJsonData(String name, JsonObject data) {
    if (jsonData == null)
      jsonData = new JsonObject();
      
    jsonData.add(name, data);
  }

  private JsonObject fetchUsers() {
    JsonObject players = new JsonObject();

    for (Player player : Main.getProvidingPlugin(getClass()).getServer().getOnlinePlayers()) {
      JsonObject playerData = new JsonObject();
      playerData.addProperty("name", player.getName());
      playerData.addProperty("health", player.getHealth());
      playerData.addProperty("food", player.getFoodLevel());
      playerData.addProperty("x", player.getLocation().getX());
      playerData.addProperty("y", player.getLocation().getY());
      playerData.addProperty("z", player.getLocation().getZ());
      playerData.addProperty("world", player.getWorld().getName());
      players.add(player.getDisplayName(), playerData);
    }

    return players;
  }

  private JsonObject getLatestData() {
    // You can modify this to fetch or compute the latest data dynamically
    return jsonData;
}

  // Get the instance
  public static ServerClass getInstance()
  {
    if (instance == null) {
      instance = new ServerClass();
    }
    return instance;
  }



}
