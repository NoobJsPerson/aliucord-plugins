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
import java.util.Objects;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class NumbersToWords extends Plugin {
    List<String> Numbers = Arrays.asList("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen");
    List<String> BiggerNumbers = Arrays.asList("twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety", "hundred", "thousand", "million", "billion");
    private String specialBuild(int num){
        int twodnum = num % 100;
        if(twodnum == 0) return "";
        String numstr = "";
        if(twodnum <= 19) numstr = Numbers.get(twodnum);
        else {
            int bigNum = (int) Math.floor(twodnum / 10);
            numstr = BiggerNumbers.get(bigNum - 2);
            if (twodnum != bigNum * 10)
                numstr += " "+Numbers.get(twodnum - bigNum * 10);
        }
        return numstr;
    }
    private String buildHundreds(int checkWith, String numstr){
        String returnstr = numstr;
        if(checkWith % 1000 >= 100) {
            int test = (int) Math.floor((checkWith % 1000) / 100);
            returnstr = Numbers.get(test) + " " + BiggerNumbers.get(8) + (!Objects.equals(numstr, "") ? " and "+numstr:"");
        }
        return returnstr;
    }
    private String complexBuild(int checkFor, int checkWith, int index, String numstr){
        String returnstr = numstr;
        int rest = checkFor != 1000000000 ? checkWith % (checkFor*1000) : checkWith;
        if(rest >= checkFor) {
            int smallerNum = (int) Math.floor(rest / checkFor);
            returnstr = buildHundreds(smallerNum,specialBuild(smallerNum))+" "+ BiggerNumbers.get(index) + (!Objects.equals(numstr, "") ? " and "+numstr:"");
        }
        return returnstr;
    }
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
                                    newmes += "zero ";
                                    continue;
                                }
                                String numstr = "";
                                numstr = specialBuild(num);
                                numstr = buildHundreds(num,numstr);
                                numstr = complexBuild(1000,num,9,numstr);
                                numstr = complexBuild(1000000,num,10,numstr);
                                numstr = complexBuild(1000000000,num,11,numstr);
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