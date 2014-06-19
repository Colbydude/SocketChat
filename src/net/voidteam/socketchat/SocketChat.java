package net.voidteam.socketchat;

import net.voidteam.socketchat.events.MessageEvents;
import net.voidteam.socketchat.network.SocketListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.WebSocket;

/**
 * Created by Robby Duke on 6/19/14.
 * Copyright (c) 2014
 *
 * @project SocketChat
 * @time 12:44 AM
 */

public class SocketChat extends JavaPlugin {
    private SocketListener listener = null;

    @Override
    public void onDisable() {
        super.onDisable();

        for (WebSocket webSocket : SocketListener.activeSessions.keySet()) {
            if (webSocket.isOpen())
                webSocket.close();
        }

        SocketListener.activeSessions.clear();

        try {
            listener.stop();
        } catch (Exception ex) {
            Utilities.severe("Could not close the WebSocket Listener!");
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        /**
         * Create the SocketListener object
         * on port 1337. TODO - Add config option.
         */
        listener = new SocketListener(1337);
        listener.start();

        /**
         * Register the message event listener
         * so that the WebChat can receive messages.
         */
        Bukkit.getPluginManager().registerEvents(new MessageEvents(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "[SocketChat] Made by Robby Duke intended to bring a WebChat interface to Minecraft.");
            return true;
        }

        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("kick-all"))
                return false;

            for (WebSocket webSocket : SocketListener.activeSessions.keySet()) {
                if (webSocket.isOpen())
                    webSocket.close();
            }

            SocketListener.activeSessions.clear();
            return true;
        }

        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("kick"))
                return false;

            WebSocket clientSocket = null;

            for (WebSocket socket : SocketListener.activeSessions.keySet()) {
                String username = SocketListener.activeSessions.get(socket);

                if (username.equalsIgnoreCase(args[1])) {
                    if (socket.isOpen())
                        socket.close();

                    SocketListener.activeSessions.remove(socket);
                    sender.sendMessage(ChatColor.BLUE + "[SocketChat] " + username + " was successfully kicked.");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Quick and easy way to return the SocketChat plugin instance.
     *
     * @return SocketChat JavaPlugin instance
     */
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("SocketChat");
    }
}
