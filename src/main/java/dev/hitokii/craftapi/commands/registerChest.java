package dev.hitokii.craftapi.commands;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import dev.hitokii.craftapi.ServerClass;

public class registerChest implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerClass server = ServerClass.getInstance();

        if (!(sender instanceof Player)) {
          Logger.getLogger("Minecraft").info("This command can only be run by a player.");
          return true;
        }

        Player player = (Player) sender;

        Block block = player.getTargetBlock((Set<Material>) null, 5);

        if (block == null || !(block.getState() instanceof Chest)) {
          player.sendMessage("Block not found or not a chest");
          return true;
        }

        Chest chest = (Chest) block.getState();
        JsonObject chestData = new JsonObject();

        for (ItemStack item : chest.getBlockInventory().getContents())
            if (item != null) 
            if (chestData.has(item.getType().name()))
                chestData.addProperty(item.getType().name(), chestData.get(item.getType().name()).getAsInt() + item.getAmount());
            else 
              chestData.addProperty(item.getType().name(), item.getAmount());

        server.addJsonData(args[0], chestData);

        return true;
    }
  
}
