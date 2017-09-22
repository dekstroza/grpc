package io.dekstroza.github.examples.twitter;

/**
 * Represents single immutable tweet
 */
public class Tweet {

    private final String handle;
    private final String text;

    /**
     * Full arg constructor
     *
     * @param handle
     *            Twitter handle of the user.
     * @param text
     *            Text of the tweet itself.
     */
    public Tweet(String handle, String text) {
        this.handle = handle;
        this.text = text;
    }

    /**
     * Returns twitter handle for this tweet.
     *
     * @return Twitter handle of the user who tweeted this.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Returns text of the tweet.
     *
     * @return Text of this tweet.
     */
    public String getText() {
        return text;
    }

}
