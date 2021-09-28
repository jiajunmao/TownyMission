/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.commands;

import net.mmogroup.mmolib.api.item.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.api.events.DoMissionEvent;
import world.naturecraft.townymission.api.exceptions.NoStartedException;
import world.naturecraft.townymission.commands.templates.TownyMissionCommand;
import world.naturecraft.townymission.components.PluginMessage;
import world.naturecraft.townymission.components.entity.MissionEntry;
import world.naturecraft.townymission.components.enums.MissionType;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.components.json.mission.ResourceMissionJson;
import world.naturecraft.townymission.data.dao.MissionDao;
import world.naturecraft.townymission.services.ChatService;
import world.naturecraft.townymission.services.MMOService;
import world.naturecraft.townymission.services.PluginMessagingService;
import world.naturecraft.townymission.services.core.TimerService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;
import world.naturecraft.townymission.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Towny mission deposit.
 */
public class TownyMissionDeposit extends TownyMissionCommand {
    /**
     * Instantiates a new Towny mission command.
     *
     * @param instance the instance
     */
    public TownyMissionDeposit(TownyMissionBukkit instance) {
        super(instance);
    }

    public int getAmountCompletable(Player player, ResourceMissionJson resourceMissionJson, boolean isAll) {
        int number;
        int diff = resourceMissionJson.getAmount() - resourceMissionJson.getCompleted();
        if (isAll) {
            if (resourceMissionJson.isMi()) {
                number = MMOService.getInstance().getAmountAndSetNull(player.getUniqueId(), resourceMissionJson.getType(), resourceMissionJson.getMiID(), diff);
            } else {
                number = getAmountAndSetNull(player, Material.valueOf(resourceMissionJson.getType()), diff);
            }
        } else {
            int tempAmount = player.getItemInHand().getAmount();
            if (tempAmount < diff) {
                player.setItemInHand(null);
                number = tempAmount;
            } else {
                ItemStack stack = player.getItemInHand();
                stack.setAmount(stack.getAmount() - diff);
                player.setItemInHand(null);
                player.setItemInHand(stack);
                number = diff;
            }
        }
        return number;
    }
    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /tms deposit
        // /tms deposit all
        if (sender instanceof Player) {
            Player player = (Player) sender;

            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {

                    TownyMissionBukkit instance = TownyMissionInstance.getInstance();
                    MissionEntry resourceEntry = MissionDao.getInstance().getTownMissions(TownyUtil.residentOf(player).getUUID(), missionEntry -> missionEntry.isStarted() && missionEntry.getMissionType().equals(MissionType.RESOURCE)).get(0);
                    ResourceMissionJson resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();

                    if (instance.isMainServer()) {
                        // This means that this is the main server, directly interact with the services
                        if (!sanityCheck(player, args)) return;

                        int number = getAmountCompletable(player, resourceMissionJson, args.length == 2);

                        resourceMissionJson.addContribution(player.getUniqueId().toString(), number);
                        resourceMissionJson.addCompleted(number);
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onSuccess")
                                .replace("%number%", String.valueOf(number)).replace("%type%", resourceMissionJson.getType().toLowerCase(Locale.ROOT)));

                        resourceEntry.setMissionJson(resourceMissionJson);

                        DoMissionEvent missionEvent = new DoMissionEvent(player, resourceEntry, true);
                        Bukkit.getPluginManager().callEvent(missionEvent);

                        if (!missionEvent.isCanceled()) {
                            MissionDao.getInstance().update(resourceEntry);
                        }
                    } else {
                        // Non main, send PMC instead
                        int number = getAmountCompletable(player, resourceMissionJson, args[1].equalsIgnoreCase("all"));
                        Material itemInHand = player.getItemInHand().getType();

                        PluginMessage mainSrvRequest = new PluginMessage()
                                .origin(instance.getInstanceConfig().getString("bungeecord.server-name"))
                                .destination(instance.getInstanceConfig().getString("bungeecord.server-name"))
                                .channel("config:request")
                                .messageUUID(UUID.randomUUID())
                                .dataSize(1)
                                .data(new String[]{"main-server"});


                        String mainServer = null;

                        try {
                            PluginMessage mainSrvResponse = PluginMessagingService.getInstance().sendAndWait(mainSrvRequest, 3, TimeUnit.SECONDS);
                            mainServer = mainSrvResponse.getData()[0];

                            PluginMessage request = new PluginMessage()
                                    .origin(instance.getInstanceConfig().getString("bungeecord.server-name"))
                                    .destination(mainServer)
                                    .channel("mission:request")
                                    .messageUUID(UUID.randomUUID())
                                    .dataSize(5)
                                    .data(new String[]{"doMission", player.getUniqueId().toString(), MissionType.RESOURCE.toString(), player.getItemInHand().getType().name(), String.valueOf(number)});

                            PluginMessage response = PluginMessagingService.getInstance().sendAndWait(request, 5, TimeUnit.SECONDS);
                            if (response.getData()[0].equalsIgnoreCase("false")) {
                                giveBackDueToError(player, number, itemInHand);
                            } else {
                                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onSuccess")
                                        .replace("%number%", String.valueOf(number)).replace("%type%", resourceMissionJson.getType().toLowerCase(Locale.ROOT)));
                            }
                        } catch (TimeoutException | InterruptedException | ExecutionException e) {
                            giveBackDueToError(player, number, itemInHand);
                        }
                    }
                }
            };

            r.runTaskAsynchronously(instance);
        }
        return true;
    }

    public void giveBackDueToError(Player player, int number, Material itemInHand) {
        while (number > 64) {
            ItemStack itemStack = new ItemStack(itemInHand, 64);
            player.getInventory().addItem(itemStack);
            number -= 64;
        }
        ItemStack itemStack = new ItemStack(itemInHand, number);
        player.getInventory().addItem(itemStack);
        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onNonMainServerFail"));
    }

    /**
     * Sanity check boolean.
     *
     * @param player the player
     * @param args   the args
     * @return the boolean
     */
    public boolean playerSanityCheck(@NotNull Player player, String[] args) {
        MissionDao missionDao = MissionDao.getInstance();
        return new BukkitChecker(instance)
                .target(player)
                .hasPermission(new String[]{"townymission.player.deposit", "townymission.player"})
                .hasTown()
                .hasStarted()
                .isMissionType(MissionType.RESOURCE)
                .customCheck(() -> {
                    // Recess check
                    if (TimerService.getInstance().isInInterval(RankType.SEASON) || TimerService.getInstance().isInInterval(RankType.SPRINT)) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onClickDuringRecess"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    // Command format error
                    if (args.length == 1)
                        return true;
                    if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                        return true;
                    } else {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                })
                .customCheck(() -> {
                    List<MissionEntry> resouceEntries = MissionDao.getInstance().getTownMissions(TownyUtil.residentOf(player).getUUID(), missionEntry -> missionEntry.isStarted() && missionEntry.getMissionType().equals(MissionType.RESOURCE));
                    if (resouceEntries.isEmpty()) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onNoMission", true));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> {
                    // Mission timeout
                    MissionEntry resourceEntry = missionDao.getTownMissions(TownyUtil.residentOf(player).getUUID(), missionEntry -> missionEntry.isStarted() && missionEntry.getMissionType().equals(MissionType.RESOURCE)).get(0);

                    if (resourceEntry.isTimedout()) {
                        ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onMissionTimedOut"));
                        return false;
                    }
                    return true;
                })
                .customCheck(() -> checkItemMismatch(player)).check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) return true;

        commandSender.sendMessage(instance.getLangEntry("universal.onPlayerCommandInConsole"));
        return false;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabList = new ArrayList<>();
        if (args.length == 2) {
            tabList.add("all");
        }
        return tabList;
    }

    private int getAmountAndSetNull(Player player, Material material, int amount) {
        int total = 0;
        int index = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().equals(material)) {
                int tempAmount = itemStack.getAmount();

                // If temp amount is lesser than the required amount, set to null and keep the loop running
                if (tempAmount <= amount) {
                    player.getInventory().setItem(index, null);
                    amount -= tempAmount;
                    total += tempAmount;
                } else {
                    // If temp amount is greater than the required amount, only take the required amount
                    int diff = tempAmount - amount;
                    ItemStack tempStack = new ItemStack(material, diff);
                    player.getInventory().setItem(index, null);
                    player.getInventory().setItem(index, tempStack);
                    total += amount;
                    return total;
                }
            }
            index++;
        }
        return total;
    }

    private boolean checkItemMismatch(Player player) {
        // Holding item in hand mismatch
        MissionEntry resourceEntry = MissionDao.getInstance().getTownMissions(TownyUtil.residentOf(player).getUUID(), missionEntry -> missionEntry.isStarted() && missionEntry.getMissionType().equals(MissionType.RESOURCE)).get(0);
        ResourceMissionJson resourceMissionJson = (ResourceMissionJson) resourceEntry.getMissionJson();

        ItemStack inHand = player.getItemInHand();
        boolean mismatch = true;
        if (resourceMissionJson.isMi()) {
            // This means that this mission is MI
            NBTItem miItem = NBTItem.get(inHand);
            if (miItem.hasType()) {
                // This means that this item is indeed MI
                String miType = miItem.getType();
                String ID = miItem.getString("MMOITEMS_ITEM_ID");
                if (miType.equalsIgnoreCase(resourceMissionJson.getType())
                        && ID.equalsIgnoreCase(resourceMissionJson.getMiID())) {
                    mismatch = false;
                }
            }
        } else {
            if (inHand.getType().equals(Material.valueOf(resourceMissionJson.getType()))) {
                mismatch = false;
            }
        }

        if (mismatch) {
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.onNotMatch"));
            if (resourceMissionJson.isMi()) {
                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.requiredItem").replace("%item%", "&e" + Util.capitalizeFirst(resourceMissionJson.getMiID())));
            } else {
                ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.requiredItem").replace("%item%", "&e" + Util.capitalizeFirst(resourceMissionJson.getType())));
            }
            ChatService.getInstance().sendMsg(player.getUniqueId(), instance.getLangEntry("commands.deposit.inHandItem").replace("%item%", "&e" + Util.capitalizeFirst(player.getItemInHand().getType().name())));
            return false;
        } else {
            return true;
        }
    }
}
