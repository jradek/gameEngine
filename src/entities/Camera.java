package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private float distanceFromPlayer = 80;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 20; // up/down
    private float yaw; // left/right
    private float roll; // tilt

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (Math.sin(Math.toRadians(theta)) * horizontalDistance);
        float offsetZ = (float) (Math.cos(Math.toRadians(theta)) * horizontalDistance);
        position.x = player.getPosition().x - offsetX;
        position.y = player.getPosition().y + verticalDistance;
        position.z = player.getPosition().z - offsetZ;

    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;

        distanceFromPlayer = Math.max(distanceFromPlayer, 10f);
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
        
        pitch = Math.max(Math.min(pitch, 90f), 5f);
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
}
