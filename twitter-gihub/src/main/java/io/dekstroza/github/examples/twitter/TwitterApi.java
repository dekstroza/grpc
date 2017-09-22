package io.dekstroza.github.examples.twitter;

import com.jayway.jsonpath.ReadContext;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.jayway.jsonpath.JsonPath.parse;
import static java.lang.String.format;
import static java.util.Base64.getEncoder;

/**
 * Representation of Twitter API
 */
public class TwitterApi {

    private static final String ENCODING = "UTF-8";
    private static final MediaType TWITTER_MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
    private static final String TWITER_OAUTH_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWITTER_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static TwitterApi apiInstance;
    private static final Logger log = LoggerFactory.getLogger(TwitterApi.class);
    private static AtomicInteger invocationCount = new AtomicInteger(0);
    private static AtomicLong invocationDuration = new AtomicLong(0);
    private String consumerKey;
    private String consumerSecret;
    private String bearerToken;

    /**
     * Private constructor, as this is singleton
     */
    private TwitterApi() {
        loadCredentialProperties();
    }

    /**
     * Load twitter consumer key and secret key from twitter.properties, located on root of the classpath.
     */
    private void loadCredentialProperties() {
        final Properties properties = new Properties();
        try (final InputStream stream = this.getClass().getResourceAsStream("/twitter.properties")) {
            properties.load(stream);
            this.consumerKey = properties.getProperty("consumerKey");
            this.consumerSecret = properties.getProperty("consumerSecret");
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to load twitter credentials from file.");
        }
    }

    /**
     * Encode consumer key and consumer secret, as per Twitter's algorithm.
     * This method will prepare value for Authorization header, in order to retrieve bearer token from twitter o-auth.
     *
     * @param consumerKey
     *            Consumer key provided by Twitter
     * @param consumerSecret
     *            Secret key provided by Twitter
     * @return Will return Base64 encoded string, made up from url encoded consumer key, concatenated to : and concatenated to url encoded consumer secret.
     * @throws UnsupportedEncodingException
     *            In case UTF-8 is not supported as encoding.
     */
    private String encodeConsumerKeyAndSecret(final String consumerKey, final String consumerSecret) throws UnsupportedEncodingException {
        String encodedConsumerKey = URLEncoder.encode(consumerKey, ENCODING);
        String encodedConsumerSecret = URLEncoder.encode(consumerSecret, ENCODING);
        return getEncoder().encodeToString(format("%s:%s", encodedConsumerKey, encodedConsumerSecret).getBytes());
    }

    /**
     * Retrieve bearer token from Twitter. This method will post required body and headers to Twitter url,
     * in order to retrieve bearer token from Twitter.
     *
     * @param encodedConsumerKeyAndSecret
     *            Encoded consumer key and secret as per Twitter algorithm.
     * @return Bearer token, which should be used to access Twitter api's.
     * @throws IOException
     *            is thrown in case of communication problems with Twitter oauth-api.
     */
    private String obtainBearerToken(final String encodedConsumerKeyAndSecret) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(TWITTER_MEDIA_TYPE, "grant_type=client_credentials");
        Request request = new Builder().url(TWITER_OAUTH_URL).post(body)

                   .addHeader("Authorization", format("Basic %s", encodedConsumerKeyAndSecret)).build();
        Response response = client.newCall(request).execute();
        return parse(response.body().string()).read("$.access_token").toString();
    }

    /**
     * Obtain bearer token or return existing one.
     * This method will return cashed bearer talken in order to improve performance.
     *
     * @return Returns or creates bearer token, if one does not exist already.
     */
    private String getTwitterBearerToken() {
        try {
            return bearerToken == null ? (bearerToken = obtainBearerToken(encodeConsumerKeyAndSecret(consumerKey, consumerSecret))) : bearerToken;
        } catch (UnsupportedEncodingException unse) {
            log.error("Unable to encode twitter credentials to UTF-8", unse);
            throw new IllegalStateException("Unable to encode twitter credentials to UTF-8");
        } catch (IOException ioe) {
            log.error("Unable to obtain twitter bearer token", ioe);
            throw new IllegalStateException(format("Unable to obtain twitter bearer token: %s", ioe.getMessage()));
        }
    }

    /**
     * Create instance of twitter api. Twitter api is singleton.
     *
     * @return TwitterApi instance, existing or will create new.
     */
    public static TwitterApi getTwitterApiInstance() {
        return (apiInstance == null ? (apiInstance = new TwitterApi()) : apiInstance);
    }

    /**
     * Find all tweets mentioning given keyword.
     * Twitter will return json, query the json using JsonPath, extracting from statuses, attribute screen_name from users.
     * In second query extract from statuses, text of all tweets.
     * Json is read and parsed only once, having it parsed and read we can query it multiple times.
     *
     * @param keyword
     *            to look in all tweets
     * @return List of immutable tweets, containing requested keyword.
     */
    public List<Tweet> searchTwitter(final String keyword) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Builder().url(format(TWITTER_SEARCH_URL, URLEncoder.encode("#" + keyword, "UTF-8"))).addHeader("Authorization",
                       format("Bearer %s", getTwitterBearerToken())).build();
            long start = System.nanoTime();
            final ReadContext ctx = parse(client.newCall(request).execute().body().byteStream());
            start = System.nanoTime() - start;
            invocationCount.getAndIncrement();
            invocationDuration.getAndAdd(start);
            final List<String> screenNames = ctx.read("$.statuses[*].user.screen_name", List.class);
            final List<String> texts = ctx.read("$.statuses[*].text", List.class);
            final List<Tweet> tweets = new ArrayList<>();
            for (int i = 0; i < screenNames.size(); i++) {
                tweets.add(new Tweet(format("@%s", screenNames.get(i)), texts.get(i)));
            }
            return tweets;
        } catch (Exception exception) {
            throw new RuntimeException(format("Unable to fetch tweets for user:%s, error:%s", keyword, exception.getMessage()));
        }
    }

    /**
     * Return average duration of twitter search. Divide total search time with number of queries made to twitter api.
     *
     * @return average search duration in milliseconds.
     */
    public double getAverageDurationTimeMs() {
        return (invocationDuration.get() / invocationCount.get()) / 1000000;
    }

}
