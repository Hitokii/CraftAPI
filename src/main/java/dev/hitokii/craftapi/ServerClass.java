package dev.hitokii.craftapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ServerClass {

  // Instance
  private static ServerClass instance;

  // Variables
  private HttpServer server;
  private int port = Main.getProvidingPlugin(getClass()).getConfig().getInt("port");

  private HttpHandler handler = new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      try {
        JsonObject jsonFile = new JsonObject();

        Logger.getLogger("Minecraft").info("Handler called");

        if (Main.getProvidingPlugin(getClass()).getConfig().getBoolean("data.players")) {
          Logger.getLogger("Minecraft").info("Fetching players");
          jsonFile.add("players", fetchUsers());
        }

        if (Main.getProvidingPlugin(getClass()).getConfig().getConfigurationSection("data.chests") != null) {
          for (String key : Main.getProvidingPlugin(getClass()).getConfig().getConfigurationSection("data.chests")
              .getKeys(false)) {
            List<Integer> coords = Main.getProvidingPlugin(getClass()).getConfig().getIntegerList("data.chests." + key);
            JsonObject chestData = fetchChest(coords.get(0), coords.get(1), coords.get(2));
            if (chestData != null)
              jsonFile.add(key, chestData);
          }
        }

        Logger.getLogger("Minecraft").info("Sending response");

        String response = new GsonBuilder().create().toJson(jsonFile);
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(response.getBytes());
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (Exception e) {
        Logger.getLogger("Minecraft").severe("Error in handler: " + e.getMessage());
        e.printStackTrace();
        exchange.sendResponseHeaders(500, -1); // Send a 500 Internal Server Error response
      }
    };
  };

  // Constructor
  private ServerClass() {
    instance = this;
  }

  public void startServer() {
    Logger.getLogger("Minecraft").info("Starting server on port " + port);
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", handler);
      server.setExecutor(null);
      server.start();
      Logger.getLogger("Minecraft").info("Server started successfully on port " + port);
    } catch (IOException e) {
      Logger.getLogger("Minecraft").severe("Failed to start server: " + e.getMessage());
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

  private JsonObject fetchUsers() {
    JsonObject players = new JsonObject();
    Logger.getLogger("Minecraft").info("Fetching players");

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

  private JsonObject fetchChest(int x, int y, int z) {
    Logger.getLogger("Minecraft").info("Fetching chest at " + x + ", " + y + ", " + z);

    String worldName = Main.getProvidingPlugin(getClass()).getConfig().getString("world_name");
    JsonObject chestData = new JsonObject();

    try {
        // Run the block access on the main server thread
        Main.getProvidingPlugin(getClass()).getServer().getScheduler().callSyncMethod(Main.getProvidingPlugin(getClass()), () -> {
            Block block = Main.getProvidingPlugin(getClass()).getServer().getWorlds().stream()
                .filter(world -> world.getName().equals(worldName)).findFirst().get().getBlockAt(x, y, z);

            if (block == null || !(block.getState() instanceof Chest)) {
                Logger.getLogger("Minecraft").warning("Block is not a chest or does not exist.");
                return null;
            }

            Chest chest = (Chest) block.getState();
            for (ItemStack item : chest.getBlockInventory().getContents()) {
                if (item != null) {
                    if (chestData.has(item.getType().name())) {
                        chestData.addProperty(item.getType().name(),
                            chestData.get(item.getType().name()).getAsInt() + item.getAmount());
                    } else {
                        chestData.addProperty(item.getType().name(), item.getAmount());
                    }
                }
            }
            return null;
        }).get(); // Wait for the task to complete
    } catch (Exception e) {
        Logger.getLogger("Minecraft").severe("Error fetching chest data: " + e.getMessage());
        e.printStackTrace();
    }

    return chestData;
}

  // Get the instance
  public static ServerClass getInstance() {
    if (instance == null) {
      instance = new ServerClass();
    }
    return instance;
  }

}
