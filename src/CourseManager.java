import java.util.HashMap;

/**
 * Created by akateiva on 16/03/16.
 */
public class CourseManager {
    HashMap<String, Entity> entities;

    CourseManager(){

    }
    Entity getEntity(String entityName){
        return entities.get(entityName);
    }


}
