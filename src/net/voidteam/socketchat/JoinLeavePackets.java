package net.voidteam.socketchat;

import net.ess3.api.IEssentials;
import net.voidteam.socketchat.network.SocketListener;
import net.voidteam.socketchat.network.events.SSOAuthorizeEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.java_websocket.WebSocket;


/**
 * Created by Jack Klink on 6/24/14.
 * Copyright (c) 2014
 *
 * @project SocketChat
 * @time 9:15 PM
 */
public class JoinLeavePackets {

    public static void joinServer(String username) {
    	boolean isHidden = false;
    	String displayName = username;
    	
    	try {
    		isHidden = ((IEssentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(username).isHidden();
    		displayName = ((IEssentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(username).getDisplayName().replaceAll("�", "&");
        }
        catch (NullPointerException ex) {
        	isHidden = false;
        	displayName = username;
        }
         
        for (WebSocket socket : SocketListener.activeSessions.keySet()) {
            if (socket.isOpen()) {
                if (!isHidden) {
                	socket.send(String.format("player.join=%s", username));
                	socket.send(String.format("online.list.join=%s", displayName));
                } else {
                	socket.send(String.format("player.join.vanished=%s", username));
                	socket.send(String.format("online.list.join.vanished=%s", displayName));
            	}
            }
        }
    }
    
    public static void leaveServer(String username) {
    	boolean isHidden = false;
    	String displayName = username;
    	
    	try {
    		isHidden = ((IEssentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(username).isHidden();
    		displayName = ((IEssentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(username).getDisplayName().replaceAll("�", "&");
        }
        catch (NullPointerException ex) {
        	isHidden = false;
        	displayName = username;
        }
         
        for (WebSocket socket : SocketListener.activeSessions.keySet()) {
            if (socket.isOpen()) {
                if (!isHidden) {
                	socket.send(String.format("player.leave=%s", username));
                	socket.send(String.format("online.list.leave=%s", displayName));
                } else {
                	socket.send(String.format("player.leave.vanished=%s", username));
                	socket.send(String.format("online.list.leave.vanished=%s", displayName));
            	}
            }
        }
    }
    
    public static void joinWebChat(String username) {
        for (WebSocket socket : SocketListener.activeSessions.keySet()) {
            if (socket.isOpen()) {
                if (!SSOAuthorizeEvent.spyList.contains(username)) {
                	socket.send(String.format("player.webchat.join=%s", username));
                	socket.send(String.format("online.list.webchat.join=%s", username));
                } else {
                	socket.send(String.format("player.webchat.join.spy=%s", username));
                	socket.send(String.format("online.list.webchat.join.spy=%s", username));
                }
            }
        }

        if (!SSOAuthorizeEvent.spyList.contains(username)) {
	        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + username + " joined the webchat.");
        	//VoidTab.add(ChatColor.YELLOW + username);
        }
    }
    
    public static void leaveWebChat(String username) {
        for (WebSocket socket : SocketListener.activeSessions.keySet()) {
            if (socket.isOpen()) {
                if (!SSOAuthorizeEvent.spyList.contains(username)) {
                	socket.send(String.format("player.webchat.leave=%s", username));
                	socket.send(String.format("online.list.webchat.leave=%s", username));
                } else {
                	socket.send(String.format("player.webchat.leave.spy=%s", username));
                	socket.send(String.format("online.list.webchat.leave.spy=%s", username));
                }
            }
        }

        if (!SSOAuthorizeEvent.spyList.contains(username)) {
        	Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + username + " left the webchat.");
            //VoidTab.remove(ChatColor.YELLOW + username);
        } else {
    		SSOAuthorizeEvent.spyList.remove(username);
        }
    }
}