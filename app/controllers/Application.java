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

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public Application(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result index(String search){
        List<Status> foundTweets = null;
        if(search != null){
            try {
                foundTweets = TwitterHandler.getInstance().Search(search);
            } catch (TwitterException e) {
                return ok(views.html.index.render(this.getSearchQueryList(), search, null, e.getMessage()));
            }
        }
        return ok(views.html.index.render(this.getSearchQueryList(), search, foundTweets, null));
    }

    @Transactional
    public Result doSearch() {
        String search = this.persistQueryAndGetText();
        return redirect(routes.Application.index(search));
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
        //TODO remove the above binding workaround
        //TODO automatic binding (commented out code below) from the request does not work for some reason - fix it.
        //SearchQuery searchQuery = formFactory.form(SearchQuery.class).bindFromRequest().get();
        //System.out.println(formFactory.form(SearchQuery.class).data());

        jpaApi.em().persist(searchQuery);
        return text;
    }
}

