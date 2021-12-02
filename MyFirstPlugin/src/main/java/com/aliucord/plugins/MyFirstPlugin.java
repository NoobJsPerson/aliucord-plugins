package com.aliucord.plugins;

// Import several packages such as Aliucord's CommandApi and the Plugin class

import android.content.Context;

import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;

import java.util.Collections;

// This class is never used so your IDE will likely complain. Let's make it shut up!
@SuppressWarnings("unused")
@AliucordPlugin
public class MyFirstPlugin extends Plugin {
    @Override
    // Called when your plugin is started. This is the place to register command, add patches, etc
    public void start(Context context) {
        // Registers a command with the name hello, the description "Say hello to the world" and no options
        var options = Collections.singletonList(
                Utils.createCommandOption(ApplicationCommandType.USER, "person", "The lucky person", null, true, false, null, null)
        );
        commands.registerCommand(
                "happybirthday",
                "Wish someone a happy birthday",
                options,
                // Return a command result with Hello World! as the content, no embeds and send set to false
                ctx -> {
                   var user = ctx.getUser("person");
                   String result = String.format("Happy Birthday <@%s>!",user.getId());
                    return new CommandsAPI.CommandResult(result, null, true);
                }
        );
    }

    @Override
    // Called when your plugin is stopped
    public void stop(Context context) {
        // Unregisters all commands
        commands.unregisterAll();
    }
}
