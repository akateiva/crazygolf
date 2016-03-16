/**
 * An abstract GameState class.
 *
 * New GameStates such as GameStateMenu or GameStateEditor should be built ontop of this abstract class.
 */
public abstract class GameState {
    abstract void update(long dt);
    abstract void draw();
    abstract void keyEvent(int key, int scancode, int action, int mods);
}
