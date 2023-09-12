//package xyz.dysaido.pvpevent.scoreboard.impl;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import xyz.dysaido.pvpevent.match.AbstractMatch;
//import xyz.dysaido.pvpevent.match.MatchState;
//import xyz.dysaido.pvpevent.match.Participant;
//import xyz.dysaido.pvpevent.scoreboard.DynamicBoard;
//
//import xyz.dysaido.pvpevent.scoreboard.SidebarProvider;
//
//public class MatchSidebar implements SidebarProvider {
//    public static final String LINE = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-------------------------";
//
//    @Override
//    public void updateEntries(DynamicBoard sidebar) {
//        AbstractMatch match = sidebar.getMatch();
//        if (match.getState() == MatchState.ACTIVE) {
//            onFight(sidebar, match);
//        } else {
//            onQueue(sidebar, match);
//        }
//    }
//
//    private void onQueue(DynamicBoard sidebar, AbstractMatch match) {
//        sidebar.addLine(LINE);
//        sidebar.addLine("Online: &e%d", Bukkit.getServer().getOnlinePlayers().size());
//        sidebar.addLine("InEvent: &e%d", match.getParticipantsByUUD().size());
//
//        sidebar.addLine("&f");
//        sidebar.addLine("Event: ");
//        sidebar.addLine("  Arena: &e%s", match.getArena().getIdentifier());
//        sidebar.addLine("  Players: &e%d&7(%d)", match.getParticipantsByUUD().size(), match.getArena().getCapacity());
//        sidebar.addLine("&f");
//        sidebar.addLine("&6play.pvpevent.xyz");
//        sidebar.addLine(LINE);
//    }
//
//    private void onFight(DynamicBoard sidebar, AbstractMatch match) {
//        if (match.getFighting().size() != 2) return;
//        sidebar.addLine(LINE);
//        sidebar.addLine("Fighting: ");
//
//        Participant p1 = match.getFighting().get(0);
//        Participant p2 = match.getFighting().get(1);
//        sidebar.addLine(" %s &e(%.2f ❤)", p1.getName(), roundToHalves(p1.getPlayer().getHealth() / 2.0D));
//
//        sidebar.addLine(" &6vs");
//
//        sidebar.addLine(" %s &e(%.2f ❤)", p2.getName(), roundToHalves(p2.getPlayer().getHealth() / 2.0D));
//
//        sidebar.addLine("");
//        sidebar.addLine("&6play.pvpevent.xyz | play.pvpevent.xyz");
//        sidebar.addLine(LINE);
//    }
//
//    public static double roundToHalves(double d) {
//        return Math.round(d * 2.0D) / 2.0D;
//    }
//}
