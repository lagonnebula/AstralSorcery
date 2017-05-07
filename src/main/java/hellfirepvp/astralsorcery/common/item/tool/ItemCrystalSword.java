/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalSword
 * Created by HellFirePvP
 * Date: 19.09.2016 / 15:52
 */
public class ItemCrystalSword extends ItemSword implements IGrindable {

    private static final Random rand = new Random();

    public ItemCrystalSword() {
        super(RegistryItems.crystalToolMaterial);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
        ItemStack stack = new ItemStack(itemIn);
        setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial));
        subItems.add(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        ToolCrystalProperties prop = getToolProperties(stack);
        CrystalProperties.addPropertyTooltip(prop, tooltip, CrystalProperties.MAX_SIZE_CELESTIAL * 2);
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        properties.writeToNBT(nbt);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
        damageProperties(stack, damage);
    }

    private void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if(prop == null) {
            stack.setItemDamage(stack.getMaxDamage());
            return;
        }
        if(prop.getSize() <= 0) {
            super.setDamage(stack, 11);
            return;
        }
        if(damage < 0) {
            return;
        }
        for (int i = 0; i < damage; i++) {
            double chance = Math.pow(((double) prop.getCollectiveCapability()) / 100D, 2);
            if(chance >= rand.nextFloat()) {
                if(rand.nextInt(3) == 0) prop.damageCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                for (int j = 0; j < 3; j++) {
                    if(purity <= rand.nextFloat()) {
                        if(rand.nextInt(15) == 0) prop.damageCutting();
                    }
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if(slot == EntityEquipmentSlot.MAINHAND) {
            ToolCrystalProperties prop = getToolProperties(stack);
            if(prop != null) {
                modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                        new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1F + (12F * prop.getEfficiencyMultiplier()), 0));
                modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                        new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1D, 0));
            }
        }
        return modifiers;
    }

    @Override
    public boolean canGrind(TileGrindstone grindstone, ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(TileGrindstone grindstone, ItemStack stack, Random rand) {
        ToolCrystalProperties prop = getToolProperties(stack);
        ToolCrystalProperties result = prop.grindCopy(rand);
        if(result == null) {
            return GrindResult.failBreakItem();
        }
        setToolProperties(stack, result);
        if(stack.getItemDamage() >= stack.getMaxDamage()) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }

}
