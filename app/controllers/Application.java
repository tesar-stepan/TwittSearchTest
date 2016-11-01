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
        if(foundTweets != null) {
            int from = ((page - 1) * count);
            int to = Math.min( (page * count) - 1, foundTweets.size()-1);
            pageTweets = foundTweets.subList(from, to);
            maxPage = foundTweets.size()/count;
        }
        return ok(views.html.index.render(this.getSearchQueryList(), search, pageTweets, null, count, page, maxPage));
    }

    @Transactional
    public Result doSearch() {
        String search = this.persistQueryAndGetText();
        int count = this.getCount();

        if(search != null){
            try {
                foundTweets = TwitterHandler.getInstance().Search(search);
            } catch (TwitterException e) {
                return ok(views.html.index.render(this.getSearchQueryList(), search, null, e.getMessage(), count, 1, 1));
            }
        }
        return redirect(routes.Application.index(search, count, 1));
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
        return Integer.parseInt(count);
    }
}

