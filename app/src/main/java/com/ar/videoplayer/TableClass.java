package com.ar.videoplayer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

public class TableClass {
    @Entity(tableName = "history")
    public static class HistoryItem {
        @PrimaryKey @NotNull
        private String videoPath;
        @ColumnInfo(name = "videoTitle")
        private String videoTitle;
        @ColumnInfo(name = "videoLength")
        private String videoLength;
        @ColumnInfo (name = "videoWatchedTime")
        private long videoWatchedTime;
        @ColumnInfo(name = "size")
        private String size;
        @ColumnInfo(name = "resolution")
        private String resolution;
        @ColumnInfo(name = "subText")
        private String subText;
        @ColumnInfo(name = "rating")
        private Float rating;


        public HistoryItem(@NotNull String videoPath, String videoTitle, String videoLength, long videoWatchedTime, String size, String resolution,String subText, Float rating) {
            this.videoPath = videoPath;
            this.videoTitle = videoTitle;
            this.videoLength = videoLength;
            this.videoWatchedTime = videoWatchedTime;
            this.size = size;
            this.resolution = resolution;
            this.subText = subText;
            this.rating = rating;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public String getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(String videoLength) {
            this.videoLength = videoLength;
        }

        public long getVideoWatchedTime() {
            return videoWatchedTime;
        }

        public void setVideoWatchedTime(long videoWatchedTime) {
            this.videoWatchedTime = videoWatchedTime;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
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

        public Float getRating() {
            return rating;
        }

        public void setRating(Float rating) {
            this.rating = rating;
        }
    }

    @Entity(tableName = "watchlist")
    public static class WatchlistItem {

        @PrimaryKey @NotNull
        private String videoPath;

        @ColumnInfo(name = "videoTitle")
        private String videoTitle;
        @ColumnInfo(name = "videoLength")
        private String videoLength;
        @ColumnInfo (name = "videoWatchedTime")
        private long videoWatchedTime;

        @ColumnInfo(name = "size")
        private String size;
        @ColumnInfo(name = "resolution")
        private String resolution;
        @ColumnInfo(name = "subText")
        private String subText;
        @ColumnInfo(name = "rating")
        private Float rating;



        public WatchlistItem(@NotNull String videoPath, String videoTitle, String videoLength, long videoWatchedTime, String size, String resolution,String subText, Float rating) {
            this.videoPath = videoPath;
            this.videoTitle = videoTitle;
            this.videoLength = videoLength;
            this.videoWatchedTime = videoWatchedTime;
            this.size = size;
            this.resolution = resolution;
            this.subText = subText;
            this.rating = rating;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public String getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(String videoLength) {
            this.videoLength = videoLength;
        }

        public long getVideoWatchedTime() {
            return videoWatchedTime;
        }

        public void setVideoWatchedTime(long videoWatchedTime) {
            this.videoWatchedTime = videoWatchedTime;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
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

        public Float getRating() {
            return rating;
        }

        public void setRating(Float rating) {
            this.rating = rating;
        }
    }
}
