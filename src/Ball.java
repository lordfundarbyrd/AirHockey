
import processing.core.PApplet;
import processing.core.PVector;

public class Ball extends PApplet {

    PVector position;
    PVector velocity;
    PVector acceleration;

    float radius;
    float mass;
    float c;

    public Ball(float x, float y, float r, float m, PVector vel) { // x coord, y coord, radius, mass, velocity
        position = new PVector (x,y);
        velocity = vel;
        acceleration = new PVector(random(0, 5), random(0, 5));
        radius = r;
        mass = m;
        c = 1;
    }

    void collision(Ball b) {
        Ball a = this;

        if (abs(a.position.dist(b.position)) > (a.radius + b.radius)) { // if balls are not touching don't do anything
            return;
        }

        // n=norm(a.position-b.position)
        PVector n = PVector.sub(a.position, b.position).normalize();

        // f=dot((a.m*a.velocity-b.m*b.velocity),n)
        float f = PVector.dot(PVector.mult(a.velocity, a.mass).sub(PVector.mult(b.velocity, b.mass)), n);
        n.mult(f * c);  // optimization: c*(f*n)

        // a.velocity=a.velocity-c*(f*n)/a.m
        a.velocity.sub(PVector.div(n, a.mass));

        // b.velocity=b.velocity+c*(f*n)/b.m
        b.velocity.add(PVector.div(n, b.mass));

        //eliminate the "tunneling" effect when the resulting positionitions are still within
        //the collision radius "after" the collision...
        float actual = abs(a.position.dist(b.position));
        float target = a.radius + b.radius;
        if (actual <= target) {
            n = PVector.sub(a.position, b.position).normalize();
            a.position.add(PVector.mult(n, (target - actual) / 2));
            b.position.sub(PVector.mult(n, (target - actual) / 2));
        }
    }
}