package uk.ac.ed.inf.Utils;

import uk.ac.ed.inf.ilp.data.LngLat;

public class NoFlyZone {
    private String name;
    private LngLat [] vertices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLat[] getVertices() {
        return vertices;
    }

    public void setVertices(LngLat[] vertices) {
        this.vertices = vertices;
    }
}
