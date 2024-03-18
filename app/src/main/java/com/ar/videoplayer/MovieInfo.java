package com.ar.videoplayer;

import java.io.Serializable;
import java.util.List;

public class MovieInfo implements Serializable {
    private String id;
    private String size;
    private String path;
    private String duration;
    private String Artists;
    private String videoPath;
    private String title;
    private String releaseDate;
    private String resolution;
    private List<String> cast;
    private String imageUrl;
    private String slideImageUrl;
    private String displayName;
    private String videoLength;
    private long tillWatched;
    private String storyLines;
    private String[] genre;
    private Float rating;
    private String subText;
    public MovieInfo(){

    }


    public String getSlideImageUrl() {
        return slideImageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSlideImageUrl(String slideImageUrl) {
        this.slideImageUrl = slideImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<String> getCast() {
        return cast;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    public long getTillWatched() {
        return tillWatched;
    }

    public void setTillWatched(long tillWatched) {
        this.tillWatched = tillWatched;
    }


    public String getArtists() {
        return Artists;
    }

    public void setArtists(String artists) {
        Artists = artists;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public String getStoryLines() {
        return storyLines;
    }

    public void setStoryLines(String storyLines) {
        this.storyLines = storyLines;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }
}
