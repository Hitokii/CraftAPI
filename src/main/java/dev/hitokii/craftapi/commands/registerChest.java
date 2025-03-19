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

import dev.hitokii.craftapi.Main;
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

        Logger.getLogger("Minecraft").info("Registering chest: " + args[0] + " at " + block.getLocation().toString());
        Main.getProvidingPlugin(getClass()).getConfig().set("data/chests/" + args[0], block.getLocation());

        return true;
    }
  
}
