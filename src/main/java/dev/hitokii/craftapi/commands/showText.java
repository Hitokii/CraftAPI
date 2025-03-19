package dev.hitokii.craftapi.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.hitokii.craftapi.ServerClass;

public class showText implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerClass server = ServerClass.getInstance();
        ArrayList<String> data = new ArrayList<String>();
        for (String arg : args) {
            data.add(arg);
        }

        server.setDisplayData(data.stream().collect(Collectors.joining(" ")));
        return true;
    }
        
  
}
