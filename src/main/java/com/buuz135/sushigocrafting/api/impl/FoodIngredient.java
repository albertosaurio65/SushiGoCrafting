package com.buuz135.sushigocrafting.api.impl;

import com.buuz135.sushigocrafting.api.IFoodIngredient;
import com.buuz135.sushigocrafting.api.IIngredientConsumer;
import com.buuz135.sushigocrafting.api.IIngredientEffect;
import com.buuz135.sushigocrafting.api.impl.effect.AddIngredientEffect;
import com.buuz135.sushigocrafting.api.impl.effect.ModifyIngredientEffect;
import com.buuz135.sushigocrafting.proxy.SushiContent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effects;

import java.util.function.Supplier;

public class FoodIngredient implements IFoodIngredient {

    public static FoodIngredient EMPTY = new FoodIngredient("empty", null, 0, IIngredientConsumer.WEIGHT, null, null, 0, 0);
    public static FoodIngredient RICE = new FoodIngredient("rice", SushiContent.Items.COOKED_RICE, 30, IIngredientConsumer.WEIGHT, null, new ModifyIngredientEffect(1.75f, 0), 0, 2);
    public static FoodIngredient NORI = new FoodIngredient("nori", SushiContent.Items.NORI_SHEET, 1, IIngredientConsumer.STACK, null, null, 1, 1);
    public static FoodIngredient TUNA_FILLET = new FoodIngredient("tuna", SushiContent.Items.RAW_TUNA_FILLET, 30, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(SushiContent.Items.RAW_TUNA.get()), new AddIngredientEffect(SushiContent.Effects.ACQUIRED_TASTE, 30, 0), 4, 1);
    public static FoodIngredient SALMON_FILLET = new FoodIngredient("salmon", SushiContent.Items.RAW_SALMON_FILLET, 30, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(Items.SALMON), new AddIngredientEffect(SushiContent.Effects.SMALL_BITES, 30, 0), 4, 1);
    public static FoodIngredient AVOCADO = new FoodIngredient("avocado", SushiContent.Items.AVOCADO_SLICES, 45, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(SushiContent.Items.AVOCADO.get()), new ModifyIngredientEffect(1.25f, 1), 2, 2);
    public static FoodIngredient CUCUMBER = new FoodIngredient("cucumber", SushiContent.Items.CUCUMBER_SLICES, 15, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(SushiContent.Items.CUCUMBER.get()), new ModifyIngredientEffect(1.75f, 0), 2, 2);
    public static FoodIngredient SESAME = new FoodIngredient("sesame", SushiContent.Items.SESAME_SEED, 6, IIngredientConsumer.WEIGHT, null, new ModifyIngredientEffect(2, 0), 1, 1);
    public static FoodIngredient CRAB = new FoodIngredient("crab", SushiContent.Items.IMITATION_CRAB, 30, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(Items.COD), new ModifyIngredientEffect(1.25f, 0), 2, 1);
    public static FoodIngredient WAKAME = new FoodIngredient("wakame", SushiContent.Items.IMITATION_CRAB, 30, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(Items.KELP), null, 0, 1);
    public static FoodIngredient TOBIKO = new FoodIngredient("tobiko", SushiContent.Items.TOBIKO, 12, IIngredientConsumer.WEIGHT, null, new ModifyIngredientEffect(1.75f, 0), 1, 2);
    public static FoodIngredient CHEESE = new FoodIngredient("cheese", SushiContent.Items.CHEESE, 12, IIngredientConsumer.WEIGHT, null, new AddIngredientEffect(() -> Effects.SATURATION, 15, 0), 1, 2);
    public static FoodIngredient SHRIMP = new FoodIngredient("shrimp", SushiContent.Items.SHRIMP, 30, IIngredientConsumer.WEIGHT, () -> Ingredient.fromItems(SushiContent.Items.SHRIMP.get()), new AddIngredientEffect(SushiContent.Effects.STEADY_HANDS, 60, 0), 2, 1);
    public static FoodIngredient CHICKEN = new FoodIngredient("chicken", () -> Items.COOKED_CHICKEN, 30, IIngredientConsumer.WEIGHT, null, new AddIngredientEffect(() -> Effects.SLOW_FALLING, 10, 0), 2, 1);

    private final Supplier<? extends Item> item;
    private final String name;
    private final int defaultAmount;
    private final IIngredientConsumer ingredientConsumer;
    private final Supplier<Ingredient> ingredientSupplier;
    private final IIngredientEffect effect;
    private final int hungerValue;
    private final int saturationValue;

    public FoodIngredient(String name, Supplier<? extends Item> item, int defaultAmount, IIngredientConsumer ingredientConsumer, Supplier<Ingredient> ingredientSupplier, IIngredientEffect effect, int foodValue, int saturationValue) {
        this.name = name;
        this.item = item;
        this.defaultAmount = defaultAmount;
        this.ingredientConsumer = ingredientConsumer;
        this.ingredientSupplier = ingredientSupplier;
        this.effect = effect;
        this.hungerValue = foodValue;
        this.saturationValue = saturationValue;
        FoodAPI.get().addFoodIngredient(this);
    }

    public static void init() {
    }

    @Override
    public Item getItem() {
        return item.get();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean needsChoppingRecipe() {
        return ingredientSupplier != null;
    }

    public Supplier<Ingredient> getInput() {
        return ingredientSupplier;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    @Override
    public IIngredientEffect getEffect() {
        return effect;
    }

    @Override
    public int getHungerValue() {
        return hungerValue;
    }

    @Override
    public int getSaturationValue() {
        return saturationValue;
    }

    public IIngredientConsumer getIngredientConsumer() {
        return ingredientConsumer;
    }
}