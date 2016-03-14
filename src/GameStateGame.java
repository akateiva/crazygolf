/**
 * Created by akateiva on 13/03/16.
 */
public class GameStateGame extends GameState {

    GameStateGame(){

    }

    /**
     * Any GameState relating logic will be called from this method.
     * @param dt the time in milliseconds since last update call
     */
    @Override
    void update(long dt) {
        if(dt != 0)
            System.out.println(1000/dt + " fps");
    }

    /**
     * Any draw calls should be executed in this method
     */
    @Override
    void draw() {

    }
}
