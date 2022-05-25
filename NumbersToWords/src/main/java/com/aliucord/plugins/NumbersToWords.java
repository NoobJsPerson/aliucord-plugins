package com.aliucord.plugins;

import android.content.Context;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.widgets.chat.MessageContent;
import com.discord.widgets.chat.MessageManager;
import com.discord.widgets.chat.input.ChatInputViewModel;


import java.util.Arrays;
import java.util.List;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class NumbersToWords extends Plugin {
    List<String> Numbers = Arrays.asList("zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen");
    List<String> BiggerNumbers = Arrays.asList("twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety","hundred","thousand");
    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(ChatInputViewModel.class.getDeclaredMethod("sendMessage", Context.class, MessageManager.class, MessageContent.class, List.class, boolean.class, Function1.class),
                new PreHook(cf -> {
                    var content = (MessageContent) cf.args[2];
                    try {
                        var mes = content.component1().trim()+" ";
                        String newmes = "";
                        for(var string : mes.split(" ")){
                            try {
                                int num = Integer.parseInt(string);
                                if(num == 0){
                                    newmes += "0 ";
                                    continue;
                                }
                                String numstr = "";
                                int twodnum = num % 100;
                                if(twodnum == 0) continue;
                                if(twodnum <= 19) numstr = Numbers.get(num);
                                else {
                                    int bigNum = (int) Math.floor(num / 10);
                                    numstr = BiggerNumbers.get(bigNum - 2)+" ";
                                    if (twodnum != bigNum * 10)
                                        numstr += Numbers.get(twodnum - bigNum * 10);
                                }
                                newmes += numstr+" ";
                                continue;
                            } catch(NumberFormatException nfe){
                                System.out.println("NumberFormatException: " + nfe.getMessage());
                            }
                            newmes += string+" ";
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