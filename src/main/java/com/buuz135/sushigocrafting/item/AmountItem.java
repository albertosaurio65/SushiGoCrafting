package com.buuz135.sushigocrafting.item;

import com.buuz135.sushigocrafting.api.IFoodIngredient;
import com.buuz135.sushigocrafting.api.impl.FoodAPI;
import com.buuz135.sushigocrafting.proxy.SushiContent;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class AmountItem extends SushiItem {

    public static final String NBT_AMOUNT = "Amount";

    private final int minAmount;
    private final int maxAmount;
    private final int maxCombineAmount;

    public AmountItem(Properties properties, String category, int minAmount, int maxAmount, int maxCombineAmount, boolean foodHurts) {
        super(properties.food((new Food.Builder()).hunger(2).saturation(0.3F).effect(new EffectInstance(Effects.POISON, 100, 0), foodHurts ? 0.6f : 0.01f).build()), category);
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.maxCombineAmount = maxCombineAmount;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        if (!stack.hasTag())
            stack.getOrCreateTag().putInt(NBT_AMOUNT, worldIn.rand.nextInt(maxAmount - minAmount) + minAmount);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_AMOUNT);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - stack.getOrCreateTag().getInt(NBT_AMOUNT) / (double) maxCombineAmount;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Color.YELLOW.getRGB();
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Amount: " + stack.getTag().getInt(NBT_AMOUNT) + "/" + maxCombineAmount + " gr.")); //TODO Localize
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt(NBT_AMOUNT, maxAmount / 2);
            items.add(stack);
        }
    }

    public int getCurrentAmount(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_AMOUNT);
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getMaxCombineAmount() {
        return maxCombineAmount;
    }

    public ItemStack withAmount(int amount) {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt(NBT_AMOUNT, amount);
        return stack;
    }

    public ItemStack random(PlayerEntity entity, World world) {
        int extra = 0;
        if (entity != null && entity.isPotionActive(SushiContent.Effects.STEADY_HANDS.get())) {
            extra += (entity.getActivePotionEffect(SushiContent.Effects.STEADY_HANDS.get()).getAmplifier() + 1) * getMinAmount();
        }
        return withAmount(Math.min(getMaxCombineAmount(), extra + world.rand.nextInt(getMaxAmount() - getMinAmount()) + getMinAmount()));
    }

    public void consume(IFoodIngredient ingredient, ItemStack stack, int amountLevel) {
        int amount = (int) (stack.getOrCreateTag().getInt(NBT_AMOUNT) - ingredient.getDefaultAmount() * (amountLevel + 1) / 5D);
        stack.getOrCreateTag().putInt(NBT_AMOUNT, amount);
        if (amount <= 0) {
            stack.shrink(1);
        }
    }

    public boolean canConsume(IFoodIngredient ingredient, ItemStack stack, int amountLevel) {
        int amount = (int) (ingredient.getDefaultAmount() * (amountLevel + 1) / 5D);
        return !stack.isEmpty() && stack.getOrCreateTag().getInt(NBT_AMOUNT) >= amount;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            worldIn.playSound((PlayerEntity) null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), this.getEatSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.4F);
            ((PlayerEntity) entityLiving).getFoodStats().addStats(stack.getItem().getFood().getHealing(), stack.getItem().getFood().getSaturation());
            for (Pair<EffectInstance, Float> pair : stack.getItem().getFood().getEffects()) {
                if (!worldIn.isRemote && pair.getFirst() != null && worldIn.rand.nextFloat() < pair.getSecond()) {
                    entityLiving.addPotionEffect(new EffectInstance(pair.getFirst()));
                }
            }
            if (!((PlayerEntity) entityLiving).abilities.isCreativeMode) {
                consume(FoodAPI.get().getIngredientFromItem(this), stack, 6);
            }
        }
        return stack;
    }

}
