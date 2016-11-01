package controllers;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.List;

/**
 * Created by TesarStepan on 31. 10. 2016.
 *
 * Singleton class handling access to Twitter4j.
 */
public class TwitterHandler {
    private static final int TWEET_COUNT = 100;
    private static final String CONSUMER_KEY = "8tzMzg6JR3WVJqxWZqIiisQQj";
    private static final String CONSUMER_SECRET = "nOfUn4VlYN9v8reORaJXBoV6zxg7BcTyw1jo1k9Ip2qxid9Cgp";
    private static final String ACCESS_TOKEN = "193733891-2APgh8RKtfQqSm8VODAkfDZcM40g5zyicQUleAKj";
    private static final String ACCESS_TOKEN_SECRET = "Ll1Y195sFix6euVZB4T5xHFXSBzxZsVF2quDCyKNwTxdo";

    private static TwitterHandler instance = null;

    private final Twitter twitter = new TwitterFactory().getInstance();

    /**
     * Private constructor
     */
    private TwitterHandler(){
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET));
    }

    /**
     *
     * @return always the same instance of the TwitterHandler.
     */
    static TwitterHandler getInstance(){
        if(instance == null){
            instance = new TwitterHandler();
        }
        return instance;
    }

    /**
     * Searches twitter through twitter4j.
     * @param query the string to search for.
     * @return List of tweets matching the search query.
     * @throws TwitterException in case there was a problem during the search process.
     */
    public List<Status> Search(String query) throws TwitterException {
        Query twitterQuery = new Query(query);
        twitterQuery.setCount(TWEET_COUNT);
        QueryResult result =  twitter.search(twitterQuery);
        return result.getTweets();
    }
}

