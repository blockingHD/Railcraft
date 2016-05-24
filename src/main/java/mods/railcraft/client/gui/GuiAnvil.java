/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import io.netty.buffer.Unpooled;
import mods.railcraft.client.render.OpenGL;
import mods.railcraft.common.gui.containers.ContainerAnvil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAnvil extends GuiContainer implements ICrafting {

    private static final ResourceLocation anvilGuiTextures = new ResourceLocation("textures/gui/container/anvil.png");
    private final ContainerRepair repairContainer;
    private GuiTextField itemNameField;
    private final InventoryPlayer playerInv;

    public GuiAnvil(InventoryPlayer playerInv, World world, BlockPos pos) {
        super(new ContainerAnvil(playerInv, world, pos, Minecraft.getMinecraft().thePlayer));
        this.playerInv = playerInv;
        this.repairContainer = (ContainerRepair) inventorySlots;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        this.itemNameField = new GuiTextField(0, fontRendererObj, i + 62, j + 24, 103, 12);
        itemNameField.setTextColor(-1);
        itemNameField.setDisabledTextColour(-1);
        itemNameField.setEnableBackgroundDrawing(false);
        itemNameField.setMaxStringLength(40);
        inventorySlots.removeCraftingFromCrafters(this);
        inventorySlots.onCraftGuiOpened(this);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        inventorySlots.removeCraftingFromCrafters(this);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        OpenGL.glDisable(GL11.GL_LIGHTING);
        fontRendererObj.drawString(I18n.format("container.repair"), 60, 6, 4210752);

        if (repairContainer.maximumCost > 0) {
            int k = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", repairContainer.maximumCost);

            if (repairContainer.maximumCost >= 40 && !mc.thePlayer.capabilities.isCreativeMode) {
                s = I18n.format("container.repair.expensive");
                k = 16736352;
            } else if (!repairContainer.getSlot(2).getHasStack())
                flag = false;
            else if (!repairContainer.getSlot(2).canTakeStack(playerInv.player))
                k = 16736352;

            if (flag) {
                int l = -16777216 | (k & 16579836) >> 2 | k & -16777216;
                int i1 = xSize - 8 - fontRendererObj.getStringWidth(s);
                byte b0 = 67;

                if (fontRendererObj.getUnicodeFlag()) {
                    drawRect(i1 - 3, b0 - 2, xSize - 7, b0 + 10, -16777216);
                    drawRect(i1 - 2, b0 - 1, xSize - 8, b0 + 9, -12895429);
                } else {
                    fontRendererObj.drawString(s, i1, b0 + 1, l);
                    fontRendererObj.drawString(s, i1 + 1, b0, l);
                    fontRendererObj.drawString(s, i1 + 1, b0 + 1, l);
                }

                fontRendererObj.drawString(s, i1, b0, k);
            }
        }

        OpenGL.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (itemNameField.textboxKeyTyped(par1, par2))
            func_135015_g();
        else
            super.keyTyped(par1, par2);
    }

    private void func_135015_g() {
        String s = itemNameField.getText();
        Slot slot = repairContainer.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
            s = "";

        repairContainer.updateItemName(s);
        mc.thePlayer.sendQueue.addToSendQueue(new CPacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).writeString(s)));
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        itemNameField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        itemNameField.drawTextBox();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(anvilGuiTextures);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        drawTexturedModalRect(k + 59, l + 20, 0, ySize + (repairContainer.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((repairContainer.getSlot(0).getHasStack() || repairContainer.getSlot(1).getHasStack()) && !repairContainer.getSlot(2).getHasStack())
            drawTexturedModalRect(k + 99, l + 45, xSize, 0, 28, 21);
    }

    @Override
    public void updateCraftingInventory(Container container, List<ItemStack> itemStackList) {
        sendSlotContents(container, 0, container.getSlot(0).getStack());
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container.
     * This doesn't have to match the actual contents of that slot. Args:
     * Container, slot number, slot contents
     */
    @Override
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack) {
        if (par2 == 0) {
            itemNameField.setText(par3ItemStack == null ? "" : par3ItemStack.getDisplayName());
            itemNameField.setEnabled(par3ItemStack != null);

            if (par3ItemStack != null)
                func_135015_g();
        }
    }

    /**
     * Sends two integers to the client-side Container. Used for furnace burning
     * time, smelting progress, brewing progress, and enchanting level. Normally
     * the first int identifies which variable to update, and the second
     * contains the new value. Both are truncated to shorts in non-local SMP.
     */
    @Override
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {
    }

    @Override
    public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_) {
    }
}
