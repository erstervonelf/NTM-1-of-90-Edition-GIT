package com.hbm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandCustomize extends CommandBase {

    @Override
    public String getCommandName() {
        return "customize";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/customize";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("Customize command placeholder (not implemented in this fork)."));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // op by default
    }
}
