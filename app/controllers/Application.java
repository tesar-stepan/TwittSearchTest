package controllers;

import models.SearchQuery;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by TesarStepan on 30. 10. 2016.
 *
 * Main application controller.
 */
public class Application extends Controller{
    private final static int HISTORY_DISPLAY_SIZE = 20;
    private final static int DEFAULT_TWEET_PPG = 15;
    private final static String NO_TWEETS_MSG = "No matching tweets found.";

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    private List<Status> foundTweets = null;

    @Inject
    public Application(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result index(String search, int count, int page){
        List<Status> pageTweets = null;
        int maxPage = 1;

        if(foundTweets == null){
            try {
                this.searchTwitter(search);
            } catch (TwitterException e) {
                ok(views.html.index.render(this.getSearchQueryList(), search, null, e.getMessage(), count, 1, 1));
            }
        }

        if(foundTweets != null && foundTweets.size() > 0) {
            maxPage = Math.max((foundTweets.size())/count, 1);

            page = Math.min(page, maxPage);

            int from = ((page - 1) * count);
            int to = Math.min( (page * count), foundTweets.size());
            System.out.println("Displaying tweets from:"+from+" to:"+to);
            pageTweets = foundTweets.subList(from, to);
        }else{
            return ok(views.html.index.render(this.getSearchQueryList(), search, null, NO_TWEETS_MSG, count, 1, 1));
        }

        return ok(views.html.index.render(this.getSearchQueryList(), search, pageTweets, null, count, page, maxPage));
    }

    @Transactional
    public Result doSearch() {
        String search = this.persistQueryAndGetText();
        int count = this.getCount();
        try{
            this.searchTwitter(search);
        }catch (TwitterException e) {
            ok(views.html.index.render(this.getSearchQueryList(), search, null, e.getMessage(), count, 1, 1));
        }
        return redirect(routes.Application.index(search, count, 1));
    }

    private void searchTwitter(String search) throws TwitterException {
        if(search != null) {
            foundTweets = TwitterHandler.getInstance().Search(search);
        }
    }

    @SuppressWarnings("unchecked")
    private List<SearchQuery> getSearchQueryList(){
        return jpaApi.em()
                .createQuery("select p from SearchQuery p order by id desc")
                .setMaxResults(HISTORY_DISPLAY_SIZE).getResultList();
    }

    private String persistQueryAndGetText(){
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String text = requestData.get("text");
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.text = text;

        jpaApi.em().persist(searchQuery);
        return text;
    }

    private int getCount(){
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String count = requestData.get("count");

        try {
            return Integer.parseInt(count);
        }catch(NumberFormatException e){
            return DEFAULT_TWEET_PPG;
        }
    }
}

