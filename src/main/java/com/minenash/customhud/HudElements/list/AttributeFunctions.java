package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.FuncElements.Num;
import com.minenash.customhud.HudElements.FuncElements.Num.NumEntry;
import com.minenash.customhud.HudElements.FuncElements.Special.Entry;
import com.minenash.customhud.HudElements.FuncElements.SpecialText.TextEntry;
import com.minenash.customhud.HudElements.list.AttributeHelpers.ItemAttribute;
import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.minenash.customhud.ducks.ResourcePackProfileMetadataDuck;
import com.minenash.customhud.ducks.SubtitleEntryDuck;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.block.Block;
import net.minecraft.client.gui.hud.SubtitlesHud.SubtitleEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.scoreboard.*;
import net.minecraft.stat.StatFormatter;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.list.AttributeHelpers.getItemItems;

@SuppressWarnings("ALL")
public class AttributeFunctions {
    private static final StatFormatter HMS = ticks -> {
        if (ticks < 0) return "∞";
        int rawSeconds = ticks / 20;
        int seconds = rawSeconds % 60;
        int minutes = (rawSeconds / 60) % 60;
        int hours = (rawSeconds / 60 / 60);
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };


    public static final Function<?,?> DIRECT = (str) -> str;



    // STATUS EFFECTS
    public static final Function<StatusEffectInstance,String> STATUS_NAME = (status) -> status == null ? null : I18n.translate(status.getTranslationKey());
    public static final Function<StatusEffectInstance,Identifier> STATUS_ID = (status) -> status == null ? null : Registries.STATUS_EFFECT.getId(status.getEffectType());
    public static final NumEntry<StatusEffectInstance> STATUS_DURATION = Num.of(HMS, (status) -> status == null ? null : status.getDuration());
    public static final Function<StatusEffectInstance,Boolean> STATUS_INFINITE = (status) -> status == null ? null : status.getDuration() == -1;
    public static final Function<StatusEffectInstance,Number> STATUS_AMPLIFICATION = (status) -> status == null ? null : status.getAmplifier();
    public static final Function<StatusEffectInstance,Number> STATUS_LEVEL = (status) -> status == null ? null : status.getAmplifier() + (status.getAmplifier() >= 0 ? 1 : 256);
    public static final Function<StatusEffectInstance,Boolean> STATUS_AMBIENT = (status) -> status == null ? null : status.isAmbient();
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_PARTICLES = (status) -> status == null ? null : status.shouldShowParticles();
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_ICON = (status) -> status == null ? null : status.shouldShowIcon();
    public static final Function<StatusEffectInstance,Number> STATUS_COLOR = (status) -> status == null ? null : status.getEffectType().getColor();
    public static final Entry<StatusEffectInstance> STATUS_CATEGORY = new Entry<>(
            (status) -> status == null ? null : WordUtils.capitalize(status.getEffectType().getCategory().name().toLowerCase()),
            (status) -> status == null ? null : status.getEffectType().getCategory().ordinal(),
            (status) -> status == null ? null : status.getEffectType().getCategory().ordinal() != 1);


    // PLAYERS (From PlayerList)
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_NAME = (player) -> player.getProfile().getName();
    public static final Function<PlayerListEntry,Text> PLAYER_ENTRY_DISPLAY_NAME = (player) -> player.getDisplayName();
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_UUID = (player) -> player.getProfile().getId().toString();
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_TEAM = (player) -> player.getScoreboardTeam().getName(); //TODO: CHANGE TEAM VAR
    public static final Function<PlayerListEntry,Number> PLAYER_ENTRY_LATENCY = (player) -> player.getLatency();
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_SURVIVAL = (player) -> player.getGameMode() == GameMode.SURVIVAL;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_CREATIVE = (player) -> player.getGameMode() == GameMode.CREATIVE;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_ADVENTURE = (player) -> player.getGameMode() == GameMode.ADVENTURE;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_SPECTATOR = (player) -> player.getGameMode() == GameMode.SPECTATOR;
    public static final Entry<PlayerListEntry> PLAYER_ENTRY_GAMEMODE = new Entry<> (
            (player) -> player.getGameMode().getName(),
            (player) -> player.getGameMode().getId(),
            (player) -> true);
    public static final Function<PlayerListEntry,Number> PLAYER_ENTRY_LIST_SCORE = (player) -> {
        Scoreboard scoreboard = CLIENT.world.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
        return scoreboard.getScore(ScoreHolder.fromProfile(player.getProfile()), objective).getScore();
    };

    // SUBTITLES TODO: ADD ALPHA COLOR
    public static final Function<SubtitleEntry,Identifier> SUBTITLE_ID = (subtitle) -> ((SubtitleEntryDuck)subtitle).customhud$getSoundID();
    public static final Function<SubtitleEntry,String> SUBTITLE_NAME = (subtitle) -> subtitle.getText().getString();
    public static final Function<SubtitleEntry,Number> SUBTITLE_AGE = (subtitle) -> (Util.getMeasuringTimeMs() - subtitle.getTime()) / 1000D;
    public static final Function<SubtitleEntry,Number> SUBTITLE_TIME = (subtitle) -> (3*CLIENT.options.getNotificationDisplayTime().getValue()) - (Util.getMeasuringTimeMs() - subtitle.getTime()) / 1000D;
    public static final Function<SubtitleEntry,Number> SUBTITLE_ALPHA = (subtitle) -> {
        double d = CLIENT.options.getNotificationDisplayTime().getValue();
        int p = MathHelper.floor(MathHelper.clampedLerp(255.0F, 75.0F, (float)(Util.getMeasuringTimeMs() - subtitle.getTime()) / (float)(3000.0 * d)));
        return  (p << 24);
    };
    public static final Function<SubtitleEntry,Number> SUBTITLE_DISTANCE = (subtitle) -> subtitle.getPosition().distanceTo(CLIENT.cameraEntity.getEyePos());
    public static final Function<SubtitleEntry,Number> SUBTITLE_X = (subtitle) -> subtitle.getPosition().getX();
    public static final Function<SubtitleEntry,Number> SUBTITLE_Y = (subtitle) -> subtitle.getPosition().getY();
    public static final Function<SubtitleEntry,Number> SUBTITLE_Z = (subtitle) -> subtitle.getPosition().getZ();
    public static final Function<SubtitleEntry,Boolean> SUBTITLE_LEFT = (subtitle) -> subtitle$getDirection(subtitle) == -1;
    public static final Function<SubtitleEntry,Boolean> SUBTITLE_RIGHT = (subtitle) -> subtitle$getDirection(subtitle) == 1;
    public static final Function<SubtitleEntry,String> SUBTITLE_DIRECTION = (subtitle) -> {
        int dir = subtitle$getDirection(subtitle);
        return dir == 0 ? "=" : dir == 1 ? ">" : "<";
    };
    public static final Function<SubtitleEntry,Number> SUBTITLE_DIRECTION_YAW = (subtitle) -> AttributeHelpers.getRelativeYaw(CLIENT.cameraEntity.getPos(), subtitle.getPosition());
    public static final Function<SubtitleEntry,Number> SUBTITLE_DIRECTION_PITCH = (subtitle) -> AttributeHelpers.getRelativePitch(CLIENT.cameraEntity.getEyePos(), subtitle.getPosition());;


    // BLOCK STATES
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_NAME = (property) -> property == null ? null : property.getKey().getName();
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_VALUE = (property) -> property == null ? null : property.getValue().toString();
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_FULL_TYPE = (property) -> property == null ? null : property.getKey().getType().getSimpleName();
    public static final Entry<Map.Entry<Property<?>,Comparable<?>> > BLOCK_STATE_TYPE = new Entry<> (
            (property) -> property == null ? null : switch (blockstate$getPropertyType(property.getKey().getType())) {
                case 1 -> "Boolean";
                case 2 -> "Number";
                case 3 -> "Enum";
                default -> "String"; },
            (property) -> property == null ? null : blockstate$getPropertyType(property.getKey().getType()),
            (property) -> property == null ? null : blockstate$getPropertyType(property.getKey().getType()) != 0);


    // BLOCK/ITEM TAGS
    public static final Function<TagKey<?>,Identifier> TAG_ID = (tag) -> tag.id();
    public static final Function<TagKey<?>,String> TAG_NAME = (tag) -> tag.id().getNamespace().equals("minecraft") ?
            tag.id().getPath() : tag.id().toString();

    // RECIEVED POWER
    public static final Function<AttributeHelpers.ReceivedPower,String> REC_DIRECTION = (rec) -> rec.direction().getName();
    public static final Function<AttributeHelpers.ReceivedPower,String> REC_OPOSITE_DIRECTION = (rec) -> rec.direction().getOpposite().getName();
    public static final Function<AttributeHelpers.ReceivedPower,Number> REC_POWER = (rec) -> rec.power();
    public static final Function<AttributeHelpers.ReceivedPower,Number> REC_STRONG_POWER = (rec) -> rec.strongPower();

    // ENCHANTMENTS
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_NAME = (enchant) -> enchant == null ? null : I18n.translate(enchant.getKey().getTranslationKey());
    public static final Function<Map.Entry<Enchantment,Integer>,Identifier> ENCHANT_ID = (enchant) -> enchant == null ? null : Registries.ENCHANTMENT.getId(enchant.getKey());
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_RARITY = (enchant) -> enchant == null ? null : enchant.getKey().getRarity().toString().toLowerCase();
    public static final Function<Map.Entry<Enchantment,Integer>,Number> ENCHANT_NUM = (enchant) -> enchant == null ? null : enchant.getValue();
    public static final Function<Map.Entry<Enchantment,Integer>,Number> ENCHANT_MAX_NUM = (enchant) -> enchant == null ? null : enchant.getKey().getMaxLevel();
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_FULL = (enchant) -> enchant == null ? null :
            enchant.getKey().getMaxLevel() == 1 ? I18n.translate(enchant.getKey().getTranslationKey())
                    : I18n.translate(enchant.getKey().getTranslationKey()) + " " + I18n.translate("enchantment.level." + enchant.getValue());
    public static final Entry<Map.Entry<Enchantment,Integer>> ENCHANT_LEVEL = new Entry<> (
            (enchant) -> enchant == null ? null : I18n.translate("enchantment.level." + enchant.getValue()),
            (enchant) -> enchant == null ? null : enchant.getValue(),
            (enchant) -> enchant != null);
    public static final Entry<Map.Entry<Enchantment,Integer>> ENCHANT_MAX_LEVEL = new Entry<> (
            (enchant) -> enchant == null ? null : I18n.translate("enchantment.level." + enchant.getKey().getMaxLevel()),
            (enchant) -> enchant == null ? null : enchant.getKey().getMaxLevel(),
            (enchant) -> enchant != null);


    // ITEMS
    public static final Function<ItemStack, Identifier> ITEM_ID = (stack) -> Registries.ITEM.getId(stack.getItem());
    public static final Function<ItemStack, String> ITEM_NAME = (stack) -> stack.getItem().getName().getString();
    public static final Function<ItemStack, Number> ITEM_RAW_ID = (stack) -> Item.getRawId(stack.getItem());
    public static final Function<ItemStack, Boolean> ITEM_IS_NOT_EMPTY = (stack) -> !stack.isEmpty();
    public static final TextEntry<ItemStack> ITEM_CUSTOM_NAME = new TextEntry<>(
            (stack) -> stack.getName(),
            (stack) -> stack.getName().getString().length(),
            (stack) -> !stack.getName().getString().equals(stack.getItem().getName().getString()));

    public static final Function<ItemStack, Number> ITEM_COUNT = (stack) -> stack.getCount();
    public static final Function<ItemStack, Number> ITEM_MAX_COUNT = (stack) -> stack.getMaxCount();
    public static final Function<ItemStack, Boolean> ITEM_IS_STACKABLE = (stack) -> stack.getMaxCount() > 1;
    public static final Function<ItemStack, Boolean> ITEM_HAS_DURABILITY = (stack) -> stack.getItem().getMaxDamage() - CLIENT.player.getMainHandStack().getDamage() > 0;
    public static final Function<ItemStack, Boolean> ITEM_HAS_MAX_DURABILITY = (stack) -> stack.getItem().getMaxDamage() > 0;
    public static final Function<ItemStack, Number> ITEM_DURABILITY = (stack) -> stack.getMaxDamage() - stack.getDamage();
    public static final Function<ItemStack, Number> ITEM_MAX_DURABILITY = (stack) -> stack.getMaxDamage();
    public static final Function<ItemStack, Number> ITEM_DURABILITY_PERCENT = (stack) -> 100 - stack.getDamage() / (float) stack.getMaxDamage() * 100;
    public static final Function<ItemStack, Number> ITEM_DURABILITY_COLOR = (stack) ->  stack.getItem().getMaxDamage() > 0 ? stack.getItemBarColor() : null;
    public static final Function<ItemStack, Boolean> ITEM_UNBREAKABLE = (stack) -> stack.hasNbt() && stack.getNbt().getBoolean("Unbreakable");
    public static final Function<ItemStack, Number> ITEM_REPAIR_COST = (stack) -> stack.getRepairCost();
    public static final Function<ItemStack, Number> ITEM_HIDE_FLAGS_NUM = (stack) -> stack.getHideFlags();
    public static final Entry<ItemStack> ITEM_RARITY = new Entry<>(
            (stack) -> stack.getItem().getRarity(stack).name(),
            (stack) -> stack.getItem().getRarity(stack).formatting.getColorValue(),
            (stack) -> stack.getItem().getRarity(stack) != Rarity.COMMON
    );
    public static final Entry<ItemStack> ITEM_ARMOR_SLOT = new Entry<>(
            (stack) -> {
                if ( !(stack.getItem() instanceof Equipment eq && eq.getSlotType().isArmorSlot())) return "None";
                String name = eq.getSlotType().getName();
                return name.substring(0,1).toUpperCase() + name.substring(1);
            },
            (stack) -> stack.getItem() instanceof Equipment eq && eq.getSlotType().isArmorSlot() ? 5 - ((eq.getSlotType().getArmorStandSlotId()-1) % 4) : 0,
            (stack) -> stack.getItem() instanceof Equipment eq && eq.getSlotType().isArmorSlot()
    );


    // CAN X
    public static final Function<Block, Identifier> BLOCK_ID = (block) -> Registries.BLOCK.getId(block);
    public static final Function<Block, String> BLOCK_NAME = (block) -> I18n.translate(block.getTranslationKey());

    // ATTRIBUTES
    public static final Function<EntityAttributeInstance,String> ATTRIBUTE_NAME = (attr) -> I18n.translate(attr.getAttribute().getTranslationKey());
    public static final Function<EntityAttributeInstance,Identifier> ATTRIBUTE_ID = (attr) -> Registries.ATTRIBUTE.getId(attr.getAttribute());
    public static final Function<EntityAttributeInstance,Boolean> ATTRIBUTE_TRACKED = (attr) -> attr.getAttribute().isTracked();
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE_DEFAULT = (attr) -> attr.getAttribute().getDefaultValue();
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE_BASE = EntityAttributeInstance::getBaseValue;
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE = EntityAttributeInstance::getValue;


    // ATTRIBUTE MODIFIERS
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_NAME = (modifier) -> modifier.name;
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_ID = (modifier) -> modifier.getId().toString();
    public static final Function<EntityAttributeModifier,Number> ATTRIBUTE_MODIFIER_VALUE = (modifier) -> modifier.getValue();
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_OPERATION_NAME = (modifier) -> switch (modifier.getOperation()) {
        case ADDITION -> "Addition";
        case MULTIPLY_BASE -> "Multiplication Base";
        case MULTIPLY_TOTAL -> "Multiplication Total"; };
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_OPERATION = (modifier) -> switch (modifier.getOperation()) {
        case ADDITION -> "+";
        case MULTIPLY_BASE -> "☒";
        case MULTIPLY_TOTAL -> "×"; };


    // ITEM ATTRIBUTE MODIFIERS
    public static final Function<ItemAttribute,String> ITEM_ATTR_SLOT = (attr) -> attr.slot();
    public static final Function<ItemAttribute,String> ITEM_ATTR_NAME = (attr) -> I18n.translate(attr.attribute().getTranslationKey());
    public static final Function<ItemAttribute,Identifier> ITEM_ATTR_ID = (attr) -> Registries.ATTRIBUTE.getId(attr.attribute());
    public static final Function<ItemAttribute,Boolean> ITEM_ATTR_TRACKED = (attr) -> attr.attribute().isTracked();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE_DEFAULT = (attr) -> attr.attribute().getDefaultValue();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE_BASE = (attr) -> CLIENT.player.getAttributeBaseValue(attr.attribute());
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE = (attr) -> CLIENT.player.getAttributeValue(attr.attribute());
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_NAME = (attr) -> {
        String key = "attribute.name." + attr.modifier().name;
        return I18n.hasTranslation(key) ? I18n.translate(key) : attr.modifier().name; };
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_ID = (attr) -> attr.modifier().getId().toString();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_MODIFIER_VALUE = (attr) -> attr.modifier().getValue();
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_OPERATION_NAME = (attr) -> switch (attr.modifier().getOperation()) {
        case ADDITION -> "Addition";
        case MULTIPLY_BASE -> "Multiplication Base";
        case MULTIPLY_TOTAL -> "Multiplication Total"; };
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_OPERATION = (attr) -> switch (attr.modifier().getOperation()) {
        case ADDITION -> "+";
        case MULTIPLY_BASE -> "☒";
        case MULTIPLY_TOTAL -> "×"; };


    // TEAM
    public static final Function<Team,Text> TEAM_NAME = (team) -> team.getDisplayName();
    public static final Function<Team,String> TEAM_ID = Team::getName;
    public static final Function<Team,Boolean> TEAM_FRIENDLY_FIRE = Team::isFriendlyFireAllowed;
    public static final Function<Team,Boolean> TEAM_FRIENDLY_INVIS = Team::shouldShowFriendlyInvisibles;
    public static final Entry<Team> TEAM_NAME_TAG_VISIBILITY = new Entry<>(
            (team) -> team.getNameTagVisibilityRule().getDisplayName().getString(),
            (team) -> team.getNameTagVisibilityRule().value,
            (team) -> team$visibleToPlayer(team, team.getNameTagVisibilityRule()));
    public static final Entry<Team> TEAM_DEATH_MGS_VISIBILITY = new Entry<>(
            (team) -> team.getNameTagVisibilityRule().getDisplayName().getString(),
            (team) -> team.getNameTagVisibilityRule().value,
            (team) -> team$visibleToPlayer(team, team.getDeathMessageVisibilityRule()));
    public static final Entry<Team> TEAM_COLLISION = new Entry<>(
            (team) -> team.getCollisionRule().getDisplayName().getString(),
            (team) -> team.getCollisionRule().value,
            (team) -> team.getCollisionRule() != AbstractTeam.CollisionRule.NEVER);
    public static final Entry<Team> TEAM_COLOR = new Entry<>(
            (team) -> team.getColor().getName(),
            (team) -> team.getColor().getColorValue(),
            (team) -> team.getColor() != Formatting.RESET
    );


    // SCOREBOARD OBJECTIVES
    public static final Function<ScoreboardObjective,Text> OBJECTIVE_NAME = (obj) -> obj.getDisplayName();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_ID = (obj) -> obj.getName();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_CRITIERIA = (obj) -> CLIENT.getServer() == null ? "unknown" : obj.getCriterion().getName();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_DISPLAY_SLOT = (obj) -> {
        Scoreboard scoreboard = AttributeHelpers.scoreboard();
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values())
            if (scoreboard.getObjectiveForSlot(slot) == obj)
                return slot.name().toLowerCase();
        return "none";
    };


    // SCOREBOARD OBJECTIVE SCORE
    public static final Function<ScoreboardEntry,String> OBJECTIVE_SCORE_HOLDER_OWNER = (score) -> score.owner();
    public static final Function<ScoreboardEntry,Text> OBJECTIVE_SCORE_HOLDER_DISPLAY = (score) -> score.display();
    public static final Function<ScoreboardEntry,Number> OBJECTIVE_SCORE_VALUE = (score) -> score.value();


    // SCOREBOARD SCORE
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,Text> SCORES_OBJECTIVE_NAME = (entry) -> entry.getKey().getDisplayName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,String> SCORES_OBJECTIVE_ID = (entry) -> entry.getKey().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,String> SCORES_OBJECTIVE = (entry) -> CLIENT.getServer() == null ? "unknown" : entry.getKey().getCriterion().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,Number> SCORES_VALUE = (entry) -> entry.getValue().getScore();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,String> SCORES_OBJECTIVE_CRITIERIA = (entry) -> CLIENT.getServer() == null ? "unknown" : entry.getKey().getCriterion().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardScore>,String> SCORES_OBJECTIVE_DISPLAY_SLOT = (entry) -> {
        Scoreboard scoreboard = AttributeHelpers.scoreboard();
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values())
            if (scoreboard.getObjectiveForSlot(slot) == entry.getKey())
                return slot.name().toLowerCase();
        return "none";
    };


    // BOSSBARS
    public static final Function<BossBar,Text> BOSSBAR_NAME = (bar) -> bar.getName();
    public static final Function<BossBar,String> BOSSBAR_UUID = (bar) -> bar.getUuid().toString();
    public static final Function<BossBar,Number> BOSSBAR_PERCENT = (bar) -> bar.getPercent();
    public static final Function<BossBar,Boolean> BOSSBAR_DARKEN_SKY = (bar) -> bar.shouldDarkenSky();
    public static final Function<BossBar,Boolean> BOSSBAR_DRAGON_MUSIC = (bar) -> bar.hasDragonMusic();
    public static final Function<BossBar,Boolean> BOSSBAR_THICKENS_FOG = (bar) -> bar.shouldThickenFog();
    public static final Entry<BossBar> BOSSBAR_COLOR = new Entry<>(
            (bar) -> WordUtils.capitalize(bar.getColor().getName().toLowerCase()),
            (bar) -> AttributeHelpers.getBossBarColor(bar),
            (bar) -> bar.getColor() != BossBar.Color.WHITE
    );
    public static final Entry<BossBar> BOSSBAR_TEXT_COLOR = new Entry<>(
            (bar) -> WordUtils.capitalize(bar.getColor().getTextFormat().getName().toLowerCase()),
            (bar) -> bar.getColor().getTextFormat().getColorValue(),
            (bar) -> bar.getColor() != BossBar.Color.WHITE
    );
    public static final Entry<BossBar> BOSSBAR_STYLE = new Entry<>(
            (bar) -> switch (bar.getStyle()) {
                case PROGRESS -> "Progress";
                case NOTCHED_6 -> "Notched 6";
                case NOTCHED_10 -> "Notched 10";
                case NOTCHED_12 -> "Notched 12";
                case NOTCHED_20 -> "Notched 20";
            },
            (bar) -> bar.getStyle().ordinal(),
            (bar) -> bar.getStyle().ordinal() != 0
    );
    public static final Function<BossBar,Identifier> BOSSBAR_ID = (bar) -> {
        if (CLIENT.getServer() == null) return null;
        for (var entry : CLIENT.getServer().getBossBarManager().commandBossBars.entrySet())
            if (entry.getValue() == bar) return entry.getKey();
        return null;
    };
    public static final Function<BossBar,Boolean> BOSSBAR_IS_VISIBLE = (bar) -> bar instanceof CommandBossBar cbb ? cbb.isVisible() : null;


    // MODS
    public static final Function<?,String> MOD_NAME = (mod) -> ((Mod)mod).getName();
    public static final Function<?,String> MOD_ID = (mod) -> ((Mod)mod).getId();
    public static final Function<?,String> MOD_SUMMARY = (mod) -> ((Mod)mod).getSummary();
    public static final Function<?,String> MOD_DESCRIPTION = (mod) -> ((Mod)mod).getTranslatedDescription();
    public static final Function<?,String> MOD_VERSION = (mod) -> ((Mod)mod).getVersion();
    public static final Function<?,String> MOD_PREFIXED_VERSION = (mod) -> ((Mod)mod).getPrefixedVersion();
    public static final Function<?,String> MOD_HASH = (mod) -> {
        try { return ((Mod)mod).getSha512Hash(); }
        catch (IOException e) { return null; }
    };
    public static final Function<?,Boolean> MOD_IS_LIBRARY = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.LIBRARY);
    public static final Function<?,Boolean> MOD_IS_CLIENT = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.CLIENT);
    public static final Function<?,Boolean> MOD_IS_DEPRECATED = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.DEPRECATED);
    public static final Function<?,Boolean> MOD_IS_PATCHWORK = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.PATCHWORK_FORGE);
    public static final Function<?,Boolean> MOD_IS_FROM_MODPACK = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.MODPACK);
    public static final Function<?,Boolean> MOD_IS_MINECRAFT = (mod) -> ((Mod)mod).getBadges().contains(Mod.Badge.MINECRAFT);

    public static final Function<?,String> BADGE_NAME = (badge) -> ((Mod.Badge)badge).getText().getString();
    public static final Function<?,Number> BADGE_OUTLINE_COLOR = (badge) -> ((Mod.Badge)badge).getOutlineColor();
    public static final Function<?,Number> BADGE_FILL_COLOR = (badge) -> ((Mod.Badge)badge).getFillColor();


    // PACKS
    public static final Function<ResourcePackProfile,Text> PACK_NAME = (pack) -> pack.getDisplayName();
    public static final Function<ResourcePackProfile,String> PACK_ID = (pack) -> pack.getName();
    public static final Function<ResourcePackProfile,Text> PACK_DESCRIPTION = (pack) -> pack.getDescription();
    public static final Function<ResourcePackProfile,Number> PACK_VERSION = (pack) -> ((ResourcePackProfileMetadataDuck)(Object)pack.metadata).customhud$getPackVersion();

    public static final Function<ResourcePackProfile,Boolean> PACK_ALWAYS_ENABLED = (pack) -> pack.isAlwaysEnabled();
    public static final Function<ResourcePackProfile,Boolean> PACK_IS_PINNED = (pack) -> pack.isPinned();
    public static final Function<ResourcePackProfile,Boolean> PACK_IS_COMPATIBLE = (pack) -> pack.getCompatibility().isCompatible();


    // RECORDS
    public static final Function<MusicAndRecordTracker.RecordInstance,Text> RECORD_NAME = (rec) -> rec.name;
    public static final Function<MusicAndRecordTracker.RecordInstance, Identifier> RECORD_ID = (rec) -> rec.id;
    public static final Function<MusicAndRecordTracker.RecordInstance,Number> RECORD_LENGTH = (rec) -> rec.length / 20F;
    public static final Function<MusicAndRecordTracker.RecordInstance,Number> RECORD_ELAPSED = (rec) -> rec.elapsed / 20F;
    public static final Function<MusicAndRecordTracker.RecordInstance,Number> RECORD_REMAINING = (rec) -> (rec.length - rec.elapsed) / 20F;
    public static final Function<MusicAndRecordTracker.RecordInstance,Number> RECORD_ELAPSED_PER = (rec) -> 100F * rec.elapsed / rec.length;


    // OFFERS
    public static final Function<TradeOffer,Number> OFFER_USES = (offer) -> offer.getUses();
    public static final Function<TradeOffer,Number> OFFER_MAX_USES = (offer) -> offer.getMaxUses();
    public static final Function<TradeOffer,Number> OFFER_SPECIAL_PRICE = (offer) -> offer.getSpecialPrice();
    public static final Function<TradeOffer,Number> OFFER_DEMAND_BONUS = (offer) -> offer.getDemandBonus();
    public static final Function<TradeOffer,Number> OFFER_PRICE_MULTIPLIER = (offer) -> offer.getPriceMultiplier();
    public static final Function<TradeOffer,Boolean> OFFER_DISABLED = (offer) -> offer.isDisabled();
    public static final Function<TradeOffer,Boolean> OFFER_CAN_AFFORD = (offer) -> {
        if (offer.isDisabled() || CLIENT.player == null) return false;
        ItemStack first = offer.getAdjustedFirstBuyItem();
        ItemStack second = offer.getSecondBuyItem();

        int amountOfFirst = 0;
        int amountOfSecond = 0;

        List<ItemStack> items = new ArrayList<>(37);
        items.add(CLIENT.player.getInventory().player.getOffHandStack());
        for (ItemStack stack : CLIENT.player.getInventory().main)
            items.addAll(getItemItems(stack, true));

        for (ItemStack stack : items) {
            if (ItemStack.canCombine(first, stack))
                amountOfFirst += stack.getCount();
            else if (ItemStack.canCombine(second, stack))
                amountOfSecond += stack.getCount();

            if (amountOfFirst >= first.getCount() && amountOfSecond >= second.getCount())
                return true;
        }
        return false;

    };

    public static final Function<ItemConvertible,Text> TAG_ENTRY_NAME = (convertible) -> convertible.asItem().getName();
    public static final Function<ItemConvertible,Identifier> TAG_ENTRY_ID = (convertible) -> Registries.ITEM.getId(convertible.asItem());




    // HELPER METHODS


    public static int subtitle$getDirection(SubtitleEntry subtitle) {
        float xRotation = -CLIENT.cameraEntity.getPitch() * ((float)Math.PI / 180);
        float yRotation = -CLIENT.cameraEntity.getYaw() * ((float)Math.PI / 180);

        Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0).rotateX(xRotation).rotateY(yRotation);
        Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0).rotateX(xRotation).rotateY(yRotation);
        Vec3d vec3d5 = subtitle.getPosition().subtract(CLIENT.cameraEntity.getEyePos()).normalize();
        double e = vec3d2.crossProduct(vec3d3).dotProduct(vec3d5);

        return -vec3d2.dotProduct(vec3d5) > 0.5 || e == 0? 0 : e < 0 ? 1 : -1;
    }

    public static int blockstate$getPropertyType(Class<?> type) {
        return type == Boolean.class ? 1 : Number.class.isAssignableFrom(type) ? 2 : type.isEnum() ? 3 : 0;
    }

    public static boolean team$visibleToPlayer(Team team, AbstractTeam.VisibilityRule rule) {
        return rule == AbstractTeam.VisibilityRule.ALWAYS
                || (rule == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS && CLIENT.player.isTeamPlayer(team))
                || (rule == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM && !CLIENT.player.isTeamPlayer(team));
    }


}
