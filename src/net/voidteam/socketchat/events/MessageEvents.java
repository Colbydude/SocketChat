package net.voidteam.socketchat.events;

import net.voidteam.socketchat.SocketChat;
import net.voidteam.socketchat.network.SocketListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robby Duke on 6/19/14.
 * Copyright (c) 2014
 *
 * @project SocketChat
 * @time 12:38 PM
 */
public class MessageEvents implements Listener {
    public static List<String> cachedMessages = new ArrayList<String>();

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        final String formattedMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

        /**
         * Add the message to the message cache
         * Jack requested this function.
         */
        cachedMessages.add(0, formattedMessage);

        /**
         * If the size of the arraylist meets or exceeds 50 messages,
         * sublist it so we don't rape the webchat use with messages.
         */
        if (cachedMessages.size() >= 50)
            cachedMessages = cachedMessages.subList(0, 49);

        /**
         * Broadcast the message to the WebChat users.
         */
        Bukkit.getScheduler().runTaskAsynchronously(SocketChat.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (WebSocket socket : SocketListener.activeSessions.keySet()) {
                    if (socket.isOpen()) {
                        socket.send(String.format("chat.receive=%s", formattedMessage.replace(ChatColor.COLOR_CHAR, '&')));
                    }
                }
            }
        });

    }
}