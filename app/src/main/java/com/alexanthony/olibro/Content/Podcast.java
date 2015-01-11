package com.alexanthony.olibro.Content;

public class Podcast extends Track {
    public Podcast() {
        super();

    }

    public Podcast(long podID, String podTitle, long podDuration) {
        super(podID, podTitle, podDuration);

    }

    public Podcast(long podID, String podTitle, long podDuration, long podLastPlayed) {
        super(podID, podTitle, podDuration, podLastPlayed);

    }

    public Podcast(long podID, String podTitle, long podDuration, long podLastPlayed, long podCompID) {
        super(podID, podTitle, podDuration, podLastPlayed, podCompID);

    }

    public void setSubscriptionID(long subId) {
        setCompID(subId);
    }
}
