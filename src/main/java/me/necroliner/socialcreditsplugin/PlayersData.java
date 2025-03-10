package me.necroliner.socialcreditsplugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.necroliner.socialcreditsplugin.data.Datasets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class PlayersData{

    private final EnumMap<Material, HashMap<UUID, Integer>> materialsCounter;
    private final HashMap<String, Integer> socialCreditCounter;
    private final ArrayList<UUID> ignoreBoard = new ArrayList<>();

    private long updateTime;

    public PlayersData(){
        materialsCounter = new EnumMap<>(Material.class);
        Datasets data = Datasets.getDataset();
        data.cropThresholds.forEach((k, v) -> materialsCounter.put(k, new HashMap<>()));
        data.oreThresholds.forEach((k, v) -> materialsCounter.put(k, new HashMap<>()));
        socialCreditCounter = getSocialCreditMapFromJSON();
        this.startLoop();
    }

    public EnumMap<Material, HashMap<UUID, Integer>> getMaterialsCounterMap(){
        return materialsCounter;
    }

    public void saveSocialCredits() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("socialCreditsMap.json"), this.socialCreditCounter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String, Integer> getSocialCreditMapFromJSON() {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Integer> map = new HashMap<>();
        try {
            map = mapper.readValue(new File("socialCreditsMap.json"), HashMap.class);
        }catch (Exception e){
            saveSocialCredits();
            e.printStackTrace();
        }
        return map;
    }

    public HashMap<String, Integer> getSocialCreditsCounter() {
        return socialCreditCounter;
    }

    public void addSocialCredit(Player player, int points){
        if(!socialCreditCounter.containsKey(player.getName())){
            socialCreditCounter.put(player.getName(), points);
        }else{
            int currentScore = this.getScore(player);
            socialCreditCounter.replace(player.getName(), currentScore + points);
        }

        if(points<2) {
            Datasets.sendActionBar(player,ChatColor.GREEN + "+" + points + " Social Credit !" );
        }else{
            Datasets.sendActionBar(player, ChatColor.GREEN + "+" + points + " Social Credits !");
        }
    }

    public void removeSocialCredit(Player player, int points){

        if(!socialCreditCounter.containsKey(player.getName())){
            socialCreditCounter.put(player.getName(), points);
        }else{
            int currentScore = this.getScore(player);
            socialCreditCounter.replace(player.getName(), currentScore - points);
        }
        if(points<2) {
            Datasets.sendActionBar(player,ChatColor.RED + "-" + points + " Social Credit !");
        }else{
            Datasets.sendActionBar(player,ChatColor.RED + "-" + points + " Social Credits !");
        }
    }

    public void setSocialCredit(Player player, int points){
        socialCreditCounter.put(player.getName(), points);
    }

    public int getScore(Player player){
        if(!socialCreditCounter.containsKey(player.getName())){
            socialCreditCounter.put(player.getName(), 0);
        }
        return socialCreditCounter.get(player.getName());
    }

    private void startLoop(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SocialCreditSystem.getInstance(), this::tick, 10L, 2L);
    }

    private void tick() {
        long current = System.currentTimeMillis();
        if (updateTime <= current) {
            saveSocialCredits();
            SocialCreditSystem.LOGGER.log(Level.INFO,"saved social credits.");
            updateTime = current + 300000L;
        }
    }

    public ArrayList<UUID> getIgnoreBoard() {
        return ignoreBoard;
    }

    public void removePlayerHiddenStats(Player player) {
            ignoreBoard.remove(player.getUniqueId());
    }

    public void addPlayerHiddenStats(Player player) {
            ignoreBoard.add(player.getUniqueId());
    }

    public void sortHashMap(){

    }


}
/*
class Sort
{
    public static void main (String[] args) throws java.lang.Exception
    {
        // Creating an array of superHero names and their strengths.
        String[] superHeroes = new String[] { "CaptainAmerica" , "Thanos" , "Thor" , "IronMan" };
        Integer[] strength = new Integer[] { 50 , 100 , 75 , 50 };

        // Insert the superHero name and its strength as a key-value pair inside a HashMap.
        Map hashmap = new HashMap<>();
        for (int i=0 ; i < superHeroes.length ; i++) {
            hashmap.put( superHeroes[i] , strength[i] );
        }


        Map sortedMap = sortedHashMapByValues(hashmap);

    }

    private static Map sortedHashMapByValues(Map<String, Integer> hashmap) {
        // Create an ArrayList and insert all hashmap key-value pairs.
        ArrayList<Map.Entry<String, Integer>> arrayList = new ArrayList<>();
        arrayList.addAll(hashmap.entrySet());

        // Sort the Arraylist using a custom comparator.
        Collections.sort(arrayList, new Comparator() {
            @Override
            public int compare(Map.Entry o1, Map.Entry o2) {
                if ( o1.getValue() == o2.getValue() )
                    return o1.getKey().compareTo(o2.getKey());

                return Integer.compare(o1.getValue() , o2.getValue());
            }
        });

        // Create a LinkedHashMap.
        Map sortedMap = new LinkedHashMap<>();

        // Iterate over the ArrayList and insert the key-value pairs into LinkedHashMap.
        for (int i=0 ; i hashmap) {
            for( Map.Entry entry : hashmap.entrySet() ) {
                System.out.println( "Key : " + entry.getKey()+ " \t  value :  " + entry.getValue() );
            }
        }
    }

}
*/