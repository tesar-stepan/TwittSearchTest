package models;

import play.data.validation.Constraints;
import javax.persistence.*;

/**
 * Created by TesarStepan on 30. 10. 2016.
 *
 * Class wrapping the queries users searched for.
 */
@Entity
public class SearchQuery {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;

    @Constraints.Required
    public String text;
}
