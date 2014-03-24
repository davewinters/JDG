package uk.co.c2b2.jdg.hotrod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Winters
 */
public class Cinema implements Serializable {

    private static final long serialVersionUID = -181403229462007401L;

    private String cinemaName;
    private List<String> shows;

    public Cinema(String cinemaName) {
        this.cinemaName = cinemaName;
        shows = new ArrayList<String>();
    }

    public void addShow(String name) {
        shows.add(name);
    }

    public void removeShow(String name) {
        shows.remove(name);
    }

    public List<String> getShows() {
        return shows;
    }

    public String getName() {
        return cinemaName;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("=== Cinema: " + cinemaName + " ===\n");
        b.append("Show:\n");
        for (String show : shows) {
            b.append("- " + show + "\n");
        }
        return b.toString();
    }
}
