package com.alexanthony.olibro.Content;

public class Podcast extends Track {
    public Podcast() {
        super();

    }

    public Podcast(long podID, String podTitle, String podArtist, int podDuration) {
        super(podID, podTitle, podArtist, podDuration);

    }

    public Podcast(long podID, String podTitle, String podArtist, int podDuration, int podLastPlayed) {
        super(podID, podTitle, podArtist, podDuration, podLastPlayed);

    }

    public Podcast(long podID, String podTitle, String podArtist, int podDuration, int podLastPlayed, long podCompID) {
        super(podID, podTitle, podArtist, podDuration, podLastPlayed, podCompID);

    }

    public void setSubscriptionID(long subId) {
        setCompID(subId);
    }
}
