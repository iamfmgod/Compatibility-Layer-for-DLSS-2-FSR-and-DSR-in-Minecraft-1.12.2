package com.fattie.compatlayer.command;

import com.fattie.compatlayer.client.gui.GuiDLSSConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class DLSSCommand extends CommandBase {
    @Override
    public String getName() {
        return "dlssconfig";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dlssconfig";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiDLSSConfigScreen());
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("dlss");
    }
}