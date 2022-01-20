package world.naturecraft.townymission.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.naturecraft.naturelib.utils.MultilineBuilder;
import world.naturecraft.townymission.TownyMissionBukkit;
import world.naturecraft.townymission.commands.templates.TownyMissionAdminCommand;
import world.naturecraft.townymission.components.enums.RankType;
import world.naturecraft.townymission.services.core.TimerService;
import world.naturecraft.townymission.utils.BukkitChecker;
import world.naturecraft.townymission.utils.TownyUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

public class TownyMissionAdminDump extends TownyMissionAdminCommand {

    public TownyMissionAdminDump(TownyMissionBukkit instance) {
        super(instance);
    }

    @Override
    public boolean playerSanityCheck(Player player, String[] strings) {
        BukkitChecker checker = new BukkitChecker(instance).target(player).silent(false)
                .hasPermission(new String[]{"townymission.admin.dump", "townymission.admin"});

        return checker.check();
    }

    @Override
    public boolean commonSanityCheck(CommandSender commandSender, String[] args) {
        BukkitChecker checker = new BukkitChecker(instance)
                .customCheck(() -> {
                    if (args.length != 1 || !args[0].equalsIgnoreCase("dump")) {
                        commandSender.sendMessage(instance.getLangEntry("universal.onCommandFormatError"));
                        return false;
                    }
                    return true;
                });

        return checker.check();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!sanityCheck(commandSender, args)) return;

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeFormat = format.format(new Date());
                List<String> builder = new LinkedList<>();

                String name = ("TownyMission Diagnostics Report - " + timeFormat);
                builder.add("---------Basic Info---------");
                builder.add("Software: " + instance.getServer().getName());
                builder.add("Version: " + instance.getServer().getVersion());
                builder.add("Plugin Version: " + instance.getDescription().getVersion());
                builder.add("");

                builder.add("---------Timer Data---------");
                builder.add("Current Season: " + instance.getStatsConfig().getString("season.current"));
                builder.add("Season Started Time(Raw): " + TimerService.getInstance().getStartTime(RankType.SEASON));
                builder.add("Season Started Time(Formatted): " + format.format(TimerService.getInstance().getStartTime(RankType.SEASON)));
                builder.add("Season Interval: " + TimerService.getInstance().isInInterval(RankType.SEASON));
                builder.add("--");
                builder.add("Current Sprint: " + instance.getStatsConfig().getString("sprint.current"));
                builder.add("Sprint Started Time(Raw): " + TimerService.getInstance().getStartTime(RankType.SPRINT));
                builder.add("Sprint Started Time(Formatted): " + format.format(TimerService.getInstance().getStartTime(RankType.SPRINT)));
                builder.add("Sprint Interval: " + TimerService.getInstance().isInInterval(RankType.SPRINT));
                builder.add("Ongoing: " + (!TimerService.getInstance().isInInterval(RankType.SPRINT) && !TimerService.getInstance().isInInterval(RankType.SEASON)));
                builder.add("");

                builder.add("---------Timer Config---------");
                builder.add("Sprint Duration(Days): " + instance.getInstanceConfig().getString("sprint.duration"));
                builder.add("Sprint Interval(Days): " + instance.getInstanceConfig().getString("sprint.interval"));
                builder.add("Season Duration(Sprints): " + instance.getInstanceConfig().getString("season.sprintPerSeason"));
                builder.add("Season Interval(Days): " + instance.getInstanceConfig().getString("season.interval"));
                builder.add("");

                builder.add("---------Participant Config---------");
                builder.add("sprintRewardBaseline: " + instance.getInstanceConfig().getString("participants.sprintRewardBaseline"));
                builder.add("sprintRewardMemberScale: " + instance.getInstanceConfig().getString("participants.sprintRewardMemberScale"));
                builder.add("sprintRewardBaselineCap: " + instance.getInstanceConfig().getString("participants.sprintRewardBaselineCap"));
                builder.add("sprintBaselineIncrement: " + instance.getInstanceConfig().getString("participants.sprintBaselineIncrement"));
                builder.add("");

                builder.add("---------Plugin List---------");
                for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                    builder.add(p.getName() + ": " + p.getDescription().getVersion());
                }

                StringBuilder sb = new StringBuilder();
                for (String s : builder) {
                    sb.append(s);
                    sb.append("\n");
                }
                send(commandSender, name, sb.toString());
            }
        };

        r.runTaskAsynchronously(instance);
        return true;
    }

    public void send(CommandSender sender, String name, String s) {
        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            Map<String,String> arguments = new HashMap<>();

            arguments.put("api_dev_key", "NUAVcH2lnklBHDzAlFrix7wimUqjs7Ey");
            arguments.put("api_option","paste");
            arguments.put("api_paste_name", name);
            arguments.put("api_paste_code", s);

            // Repeat this for for all required API arguments
            // ...

            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            InputStream is = http.getInputStream();
            String text = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

            sender.sendMessage(instance.getLangEntry("adminCommands.dump.onSuccess").replace("%link%", text));
        } catch (IOException urlException) {
            sender.sendMessage(instance.getLangEntry("adminCommands.dump.onFailure"));
            urlException.printStackTrace();
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
