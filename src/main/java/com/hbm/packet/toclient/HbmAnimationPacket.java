package com.hbm.packet.toclient;

public class HbmAnimationPacket extends GunAnimationPacket {

    public HbmAnimationPacket() { super(); }

    public HbmAnimationPacket(int type) { super(type); }

    public HbmAnimationPacket(int type, int rec) { super(type, rec); }

    public HbmAnimationPacket(int type, int rec, int gun) { super(type, rec, gun); }
}
