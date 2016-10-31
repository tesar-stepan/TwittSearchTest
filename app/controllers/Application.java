package controllers;

import models.SearchQuery;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by TesarStepan on 30. 10. 2016.
 *
 * Main application controller.
 */
public class Application extends Controller{
    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public Application(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result index() {
        return ok(views.html.index.render(this.getSearchQueryList()));
    }

    @Transactional
    public Result addSearchQuery() {
        System.out.println(formFactory.form().bindFromRequest().data());

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String text = requestData.get("text");
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.text = text;
        //TODO remove the above binding workaround
        //TODO automatic binding (commented out code below) from the request does not work for some reason - fix it.
        //SearchQuery searchQuery = formFactory.form(SearchQuery.class).bindFromRequest().get();
        //System.out.println(formFactory.form(SearchQuery.class).data());

        jpaApi.em().persist(searchQuery);
        return redirect(routes.Application.index()); //TODO redirect to search results page
    }

    @Transactional(readOnly = true)
    public Result getSearchQueries() {
        List<SearchQuery> persons = this.getSearchQueryList();
        return ok(toJson(persons));
    }

    @SuppressWarnings("unchecked")
    private List<SearchQuery> getSearchQueryList(){
        return jpaApi.em().createQuery("select p from SearchQuery p").getResultList();
    }
}

