package com.group9.crazygolf.entities;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.StateComponent;

/**
 * Calculates how objects move
 */
public class RK4Integrator {

    static void forces(StateComponent state, float dt, Vector3 force, Vector3 torque) {
        force.set(0, -state.mass * 9.81f, 0); //gravity
    }

    class Derivative {
        Vector3 velocity;
        Vector3 force;
        Quaternion spin;
        Vector3 torque;

        Derivative evaluate(StateComponent state, float t, float dt) {
            state.position.mulAdd(this.velocity, dt);
            state.momentum.mulAdd(this.force, dt);
            //state.orientation.mulAdd(this.spin, dt); TODO:fix this
            state.angularMomentum.mulAdd(this.torque, dt);
            state.update();


            Derivative output = new Derivative();
            output.velocity = state.velocity;
            output.spin = state.spin;
            forces(state, t + dt, output.force, output.torque);
            return output;
        }
    }
}