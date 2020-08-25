package com.mithion.eternalstorms.damage;

import net.minecraft.util.DamageSource;

public class ColdDamage extends DamageSource{

	//easy getter for the type
	public static final ColdDamage INSTANCE = new ColdDamage();
	
	public ColdDamage() {
		super("eternalstorms_cold");
		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}

	@Override
	public boolean canHarmInCreative() {
		return false;
	}
}
