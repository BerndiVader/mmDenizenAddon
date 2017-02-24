package com.gmail.berndivader.mmDenizenAddon.plugins;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mmDenizenAddon.MythicDenizenPlugin;
import com.gmail.berndivader.mmDenizenAddon.Support;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.aufdemrand.denizen.objects.dEntity;

public class MythicMobsAddon extends Support {
	
	@SuppressWarnings("unchecked")
	public MythicMobsAddon() {
		registerObjects(dActiveMob.class);
		registerProperty(dActiveMobExt.class, dEntity.class);
	}

	public static boolean isActiveMob(UUID uuid) {
		return MythicMobs.inst().getMobManager().isActiveMob(uuid);
	}

	public static boolean isActiveMob(Entity e) {
		return MythicMobs.inst().getMobManager().isActiveMob(BukkitAdapter.adapt(e));
	}

	public static ActiveMob getActiveMob(Entity e) {
		return MythicMobs.inst().getMobManager().getMythicMobInstance(e);
	}
	
	public static boolean removeSelf(ActiveMob am) {
		if (!am.isUsingDamageSkill()) {
			am.setDead();
			am.getEntity().remove();
		} else {
	    	new BukkitRunnable() {
	            ActiveMob ram = am;
	    		public void run() {
	    			if (ram.isDead() || !ram.isUsingDamageSkill()) {
	    				ram.setDead();
	    				ram.getEntity().remove();
	    				this.cancel();
	    			}
	            }
	        }.runTaskTimer(MythicDenizenPlugin.inst(), 1, 1);
		}
		return true;
	}

	public static boolean isDead(Entity e) {
		ActiveMob am;
		if ((am=MythicMobs.inst().getAPIHelper().getMythicMobInstance(e))!=null) {
			return am.isDead();
		}
		return false;
	}

	public static boolean hasThreatTable(Entity e) {
		ActiveMob am;
		if ((am=MythicMobs.inst().getAPIHelper().getMythicMobInstance(e))!=null) {
			return am.hasThreatTable();
		}
		return false;
	}

	public static boolean hasMythicSpawner(Entity e) {
		ActiveMob am;
		if ((am=MythicMobs.inst().getAPIHelper().getMythicMobInstance(e))!=null) {
			return am.getSpawner()!=null;
		}
		return false;
	}

	public static Entity getOwner(ActiveMob am) {
		if (am.getOwner().isPresent()) {
			UUID uuid = am.getOwner().get();
			return dEntity.getEntityForID(uuid);
		}
		return null;
	}

	public static Entity getLastAggro(ActiveMob am) {
		if (am.getLastAggroCause() != null) {
			return am.getLastAggroCause().getBukkitEntity();
		};
		return null;
	}

	public static Entity getTopTarget(ActiveMob am) {
		if (am.hasThreatTable()) {
			return am.getThreatTable().getTopThreatHolder().getBukkitEntity();
		} else if (am.hasTarget()) {
			return am.getEntity().getTarget().getBukkitEntity();
		}
		return null;
	}

	public static boolean hasTarget(ActiveMob am) {
		return (am.hasThreatTable() || am.hasTarget())?true:false;
	}

	public static void setCustomName(ActiveMob am, String name) {
		am.getEntity().getBukkitEntity().setCustomName(name);
	}

	public static void setTarget(ActiveMob am, Entity target) {
		if (am.hasThreatTable() && (target instanceof LivingEntity)) {
			double h = am.getThreatTable().getTopTargetThreat();
			MythicMobs.inst().getAPIHelper().addThreat(am.getEntity().getBukkitEntity(), (LivingEntity)target, h+1);
		} else {
			am.setTarget(BukkitAdapter.adapt(target));
		}
	}
}