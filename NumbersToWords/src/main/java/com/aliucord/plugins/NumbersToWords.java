package com.aliucord.plugins;

import android.content.Context;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.widgets.chat.MessageContent;
import com.discord.widgets.chat.MessageManager;
import com.discord.widgets.chat.input.ChatInputViewModel;

import java.util.List;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class StupitCase extends Plugin {
    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(ChatInputViewModel.class.getDeclaredMethod("sendMessage", Context.class, MessageManager.class, MessageContent.class, List.class, boolean.class, Function1.class),
                new PreHook(cf -> {
                    var thisobj = (ChatInputViewModel) cf.thisObject;
                    var content = (MessageContent) cf.args[2];
                    try {
                        var mes = content.component1().trim();
                        String newmes = "";
                        for(var string : mes.split(" ")){
                            try {
                                int num = parseInt(string);

                            } catch(NumberFormatExepction e) {
                                e.printStackTrace();
                            }
                        }

                        
                    }
                        ReflectUtils.setField(content, "textContent", newmes.trim());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}