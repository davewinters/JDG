package uk.co.c2b2.jdg.hotrod;

import java.io.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

/**
 * @author David Winters
 */
public class CinemaDirectory {

    private static final String cinemaNotListed = "The specified cinema \"%s\" does not exist in the database, choose next operation\n";
    private static final String msgEnterCinemaName = "Enter cinema name: ";
    private static final String startupPrompt = "Choose one of the following actions:\n" + "============= \n" + "ac  -  add a cinema\n"
            + "as  -  add a show to a cinema\n" + "rs  -  remove a show from a cinema\n"
            + "pr   -  print all cinemas and shows\n" + "q   -  quit\n";
    private static final String cinemaKey = "directory-dist-cache";

    private Console con;
    private RemoteCacheManager cacheManager;
    private RemoteCache<String, Object> cache;

    public CinemaDirectory(Console con) {
        this.con = con;
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SERVER_LIST, "127.0.0.1:11222;127.0.0.1:11322;127.0.0.1:11422");


        cacheManager = new RemoteCacheManager(properties);
        cache = cacheManager.getCache("directory-dist-cache");
        if(!cache.containsKey(cinemaKey)) {
            List<String> teams = new ArrayList<String>();
            Cinema cinema = new Cinema("Vue Bristol");
            cinema.addShow("the wolf of wall street");
            cinema.addShow("12 years a slave");
            cinema.addShow("big lebowski");
            cache.put(cinema.getName(), cinema);
            teams.add(cinema.getName());
            // use the same key which will be shared for all cinema ojbects
            cache.put(cinemaKey, teams);
        }
    }

    public void addCinema() {
        String cinemaName = con.readLine(msgEnterCinemaName);
        @SuppressWarnings("unchecked")
        List<String> cinemalist = (List<String>) cache.get(cinemaKey);
        if (cinemalist == null) {
        	cinemalist = new ArrayList<String>();
        }
        Cinema cinema = new Cinema(cinemaName);
        cache.put(cinemaName, cinema);
        cinemalist.add(cinemaName);
        // use the same key which will be shared for all cinema ojbects
        cache.put(cinemaKey, cinemalist);
    }

    public void addShow() {
        String cinemaName = con.readLine(msgEnterCinemaName);
        String showName = null;
        Cinema cinema = (Cinema) cache.get(cinemaName);
        if (cinema != null) {
            while (!(showName = con.readLine("Enter the show name (to stop adding, type \"q\"): ")).equals("q")) {
            	cinema.addShow(showName);
            }
            // use the same key which will be shared for all cinema ojbects
            cache.put(cinemaName,cinema);
        } else {
            con.printf(cinemaNotListed, cinemaName);
        }
    }

    public void removeShow() {
        String showName = con.readLine("Enter the show name: ");
        String cinemaName = con.readLine("Enter the cinema where the show is located: ");
        Cinema cinema = (Cinema) cache.get(cinemaName);
        if (cinema != null) {
        	cinema.removeShow(showName);
            cache.put(cinemaName, cinema);
        } else {
            con.printf(cinemaNotListed, cinemaName);
        }
    }



    public void printCinemasShows() {
        @SuppressWarnings("unchecked")
        List<String> cinemalist = (List<String>) cache.get(cinemaKey);
        if (cinemalist != null) {
            for (String cinemaName : cinemalist) {
                con.printf(cache.get(cinemaName).toString());
            }
        }
    }

    public void stop() {
        cacheManager.stop();
    }

    public static void main(String[] args) {
        Console con = System.console();
        CinemaDirectory manager = new CinemaDirectory(con);
        con.printf(startupPrompt);

        while (true) {
            String action = con.readLine(">");
            if ("ac".equals(action)) {
                manager.addCinema();
            } else if ("as".equals(action)) {
                manager.addShow();
            } else if ("rs".equals(action)) {
                manager.removeShow();
            } else if ("pr".equals(action)) {
                manager.printCinemasShows();
            } else if ("q".equals(action)) {
                manager.stop();
                break;
            }
        }
    }


}
