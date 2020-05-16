package com.kwpugh.simple_backpack.util;

import com.kwpugh.simple_backpack.Backpack;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

public class BackpackBlockEntity extends LootableContainerBlockEntity implements BlockEntityClientSerializable, BackpackInventoryImpl
{
    private DefaultedList<ItemStack> inventory;

    public int inventory_width = 9;
    public int inventory_height = 6;

    public int players_using = 0;
    public int players_using_old = 0;
    public boolean is_open = false;

    public float lid_openness = 0f;
    public float last_lid_openness = 0f;

    public BackpackBlockEntity()
    {
        super(Backpack.BACKPACK_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(inventory_width * inventory_height, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName()
    {
        return new TranslatableText("container.chest");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory)
    {
        return new BackpackContainer(syncId, playerInventory, (Inventory) this, inventory_width, inventory_height, null);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList()
    {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list)
    {
        this.inventory = list;
    }

    @Override
    public int getInvSize()
    {
        return inventory_width * inventory_height;
    }

    @Override
    public void onInvOpen(PlayerEntity player)
    {
        super.onInvOpen(player);

        if(!player.isSpectator())
        {
            players_using++;
        }
    }

    @Override
    public void onInvClose(PlayerEntity player)
    {
        super.onInvClose(player);

        if(!player.isSpectator())
        {
            players_using--;
        }
    }

    public void resizeInventory(boolean copy_contents)
    {
        DefaultedList<ItemStack> new_inventory = DefaultedList.ofSize(inventory_width * inventory_height, ItemStack.EMPTY);
        
        if(copy_contents)
        {
            DefaultedList<ItemStack> list = this.inventory;

            for(int i = 0; i < list.size(); i++)
            {
                new_inventory.set(i, list.get(i));
            }
        }

        this.inventory = new_inventory;
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);

        inventory_width = tag.contains("inventory_width") ? tag.getInt("inventory_width") : 9;
        inventory_height = tag.contains("inventory_height") ? tag.getInt("inventory_height") : 6;

        this.inventory = DefaultedList.ofSize(inventory_width * inventory_height, ItemStack.EMPTY);
        readItemsFromTag(this.inventory, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);

        writeItemsToTag(inventory, tag);

        tag.putInt("inventory_width", inventory_width);
        tag.putInt("inventory_height", inventory_height);

        return tag;
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag)
    {
        return toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag)
    {
        fromTag(tag);
    }

    @Override
    public int getInventoryWidth()
    {
        return inventory_width;
    }

    @Override
    public int getInventoryHeight()
    {
        return inventory_height;
    }
}
