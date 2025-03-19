package dev.hitokii.craftapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.hitokii.craftapi.ServerClass;

public class StopServer implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Stop the server
        ServerClass server = ServerClass.getInstance();
        server.stopServer();
        
        return true;
    }
  
}
