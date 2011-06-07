/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.main;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anindya
 */
public class Movie {
    public enum Genre {
        CRIME,
        THRILLER,
        DRAMA,
        ROMANTIC,
        HORROR
    }
    private String title = null;
    private boolean isEng = true;
    private boolean inTop250 = false;
    private List<Genre> genres = null;
    private int year = 0;
    private double rating = 0.0;
    private int votes = 0;
    public Movie(String title) {
        this.title = title;
    }
    public void setIsEng(boolean v) {
        isEng = v;
    }
    public void setInTop250(boolean v) {
        inTop250 = v;
    }
    public void setYear(int yr) {
        year = yr;
    }
    public void setRating(double r) {
        rating = r;
    }
    public void setVotes(int v) {
        votes = v;
    }
    public void addGenre(Genre g) {
        if (genres == null) {
            genres = new ArrayList<Genre>();
        }
        genres.add(g);
    }
    public boolean getInTop250() {
        return inTop250;
    }
    public boolean getIsEng() {
        return isEng;
    }
    public double getRating() {
        return rating;
    }
    public int getVotes() {
        return votes;
    }
    public int getYear() {
        return year;
    }
    public String getTitle() {
        return title;
    }
    public List<Genre> getGenres() {
        return genres;
    }
}
