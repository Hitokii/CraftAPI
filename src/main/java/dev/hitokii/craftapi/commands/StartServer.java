package dev.hitokii.craftapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.hitokii.craftapi.ServerClass;

public class StartServer implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Start the server
        ServerClass server = ServerClass.getInstance();
        server.startServer();
        return true;
    }
  
}
