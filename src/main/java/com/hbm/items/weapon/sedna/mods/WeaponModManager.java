package com.hbm.items.weapon.sedna.mods;

import net.minecraft.item.ItemStack;

public class WeaponModManager {

    public static final int ID_SILENCER = 201;
    public static final int ID_SCOPE = 202;
    public static final int ID_SAWED_OFF = 203;
    public static final int ID_NO_SHIELD = 204;
    public static final int ID_NO_STOCK = 205;
    public static final int ID_GREASEGUN_CLEAN = 206;
    public static final int ID_MINIGUN_SPEED = 208;
    public static final int ID_FURNITURE_GREEN = 211;
    public static final int ID_FURNITURE_BLACK = 212;
    public static final int ID_MAS_BAYONET = 213;
    public static final int ID_UZI_SATURN = 215;
    public static final int ID_LAS_SHOTGUN = 216;
    public static final int ID_LAS_CAPACITOR = 217;
    public static final int ID_LAS_AUTO = 218;
    public static final int ID_CARBINE_BAYONET = 219;
    public static final int ID_NI4NI_NICKEL = 220;
    public static final int ID_NI4NI_DOUBLOONS = 221;
    public static final int ID_DRILL_HSS = 222;
    public static final int ID_DRILL_WSTEEL = 223;
    public static final int ID_DRILL_TCALLOY = 224;
    public static final int ID_DRILL_SATURN = 225;

    public static void init() {
        XWeaponModManager.init();
    }

    public static ItemStack[] getUpgradeItems(ItemStack stack, int cfg) { return XWeaponModManager.getUpgradeItems(stack, cfg); }

    public static boolean hasUpgrade(ItemStack stack, int cfg, int id) { return XWeaponModManager.hasUpgrade(stack, cfg, id); }

    public static void install(ItemStack stack, int cfg, ItemStack... mods) { XWeaponModManager.install(stack, cfg, mods); }

    public static void uninstall(ItemStack stack, int cfg) { XWeaponModManager.uninstall(stack, cfg); }

    public static void onInstallStack(ItemStack gun, ItemStack mod, int cfg) { XWeaponModManager.onInstallStack(gun, mod, cfg); }

    public static void onUninstallStack(ItemStack gun, ItemStack mod, int cfg) { XWeaponModManager.onUninstallStack(gun, mod, cfg); }

    public static boolean isApplicable(ItemStack gun, ItemStack mod, int cfg, boolean checkMutex) { return XWeaponModManager.isApplicable(gun, mod, cfg, checkMutex); }

}
