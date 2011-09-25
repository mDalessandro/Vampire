package com.massivecraft.vampire.listeners;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.massivecraft.vampire.P;
import com.massivecraft.vampire.VPlayer;
import com.massivecraft.vampire.VPlayers;
import com.massivecraft.vampire.config.Conf;
import com.massivecraft.vampire.config.Lang;
import com.massivecraft.vampire.zcore.util.TextUtil;


public class VampirePlayerListener extends PlayerListener
{
	public static P p = P.p; 
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		
		if ( ! (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) )
		{
			return;
		}
		
		VPlayer vplayer = VPlayers.i.get(event.getPlayer());
		Material itemMaterial = event.getMaterial();
		
		if(vplayer.isVampire())
		{
			if (Conf.canEat.containsKey(itemMaterial))
			{
				if ( ! Conf.canEat.get(itemMaterial))
				{
					vplayer.msg(p.txt.parse(Lang.vampiresCantEatThat, TextUtil.getMaterialName(itemMaterial)));
					event.setCancelled(true);
				}
			}
				
			if (Conf.jumpMaterials.contains(event.getMaterial())) 
			{
				vplayer.jump(Conf.jumpDeltaSpeed, false);
			}
		}
		
		if (vplayer.isInfected() && itemMaterial == Material.BREAD)
		{
			vplayer.infectionHeal(Conf.infectionBreadHealAmount);
		}		
		
		if ( action != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		
		Material blockMaterial = event.getClickedBlock().getType();
		
		if (blockMaterial == Conf.altarInfect.material)
		{
			vplayer.useAltarInfect(event.getClickedBlock());
		} 
		else if (blockMaterial == Conf.altarCure.material)
		{
			vplayer.useAltarCure(event.getClickedBlock());
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{		
		if (event.isCancelled()) return;
		
		Player me = event.getPlayer();
		VPlayer vme = VPlayers.i.get(me);
		
		if (Conf.nameColorize == false) return;
		if ( ! vme.isVampire()) return;
		
		me.setDisplayName(""+Conf.nameColor+ChatColor.stripColor(me.getDisplayName()));
		
		
		/*
		if (event.getMessage().startsWith("v ") || event.getMessage().equals("v"))
		{
			List<String> parameters = TextUtil.split(event.getMessage().trim());
			parameters.remove(0);
			CommandSender sender = event.getPlayer();
			P.p.handleCommand(sender, parameters);
			event.setCancelled(true);
		}
		*/
	}
	
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event)
	{
		VPlayer vplayer = VPlayers.i.get(event.getPlayer());
		if ( ! vplayer.isVampire()) return;
			
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING && Conf.jumpMaterials.contains(event.getPlayer().getItemInHand().getType()))
		{
			vplayer.jump(Conf.jumpDeltaSpeed, true);
		}
	}
	
	
}