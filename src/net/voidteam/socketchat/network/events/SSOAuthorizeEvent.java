package net.voidteam.socketchat.network.events;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.minecraft.util.com.google.gson.internal.LinkedTreeMap;
import net.voidteam.socketchat.Utilities;
import net.voidteam.socketchat.network.SocketListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Robby Duke on 6/19/14.
 * Copyright (c) 2014
 *
 * @project SocketChat
 * @time 10:55 AM
 */
public class SSOAuthorizeEvent extends iEvent {
    /**
     * Default constructor for the iEvent class.
     *
     * @param payload The SSO ticket being authorized.
     */
    public SSOAuthorizeEvent(WebSocket client, String payload) {
        super(client, payload);
    }


    @Override
    public void run() {
        if (getPayload().length() != 50) {
            throw new IllegalArgumentException("Invalid SSO Ticket.");
        }

        String json = null;

        try {
            json = getText("http://voidteam.net/minecraft/api/check_webchat_ticket/".concat(getPayload()));
        } catch (Exception ex) {
            Utilities.severe("Couldn't fetch JSON!");
            ex.printStackTrace();

            throw new IllegalArgumentException("SSO Server Down.");
        }

        /**
         * Create the GSON object and turn the JSON
         * into a LinkedTreeMap for ease of use.
         */
        Gson gson = new Gson();
        LinkedTreeMap jsonValues = gson.fromJson(json, LinkedTreeMap.class);

        if (jsonValues.containsKey("profile")) {
            throw new IllegalArgumentException("Invalid SSO Ticket.");
        }

        String username = (String) jsonValues.get("username");
        Utilities.debug(String.format("Validated SSO Ticket [%s] [username=%s]", getPayload(), username));

        SocketListener.activeSessions.put(getClient(), username);

        if (getClient().isOpen())
            getClient().send("SSO Ticket Authorized");

        /**
         * Check if the user is vanished, and then
         * display a join message.
         */

        IUser iUser = ((IEssentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(username);

        // TODO - Add localization.
        if(!iUser.isVanished()) {
            Bukkit.getServer().broadcastMessage(String.format(ChatColor.YELLOW + "%s joined the webchat.", username));
        }
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}