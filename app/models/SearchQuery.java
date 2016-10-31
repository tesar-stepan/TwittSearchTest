package models;

import javax.persistence.*;

/**
 * Created by TesarStepan on 30. 10. 2016.
 *
 * Class used to save history of user search queries
 */
@Entity
public class SearchQuery {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;

    public String text;
}
