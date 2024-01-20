package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
public class LngLatHandler implements LngLatHandling{
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double x1 = startPosition.lng(),  y1 = startPosition.lat();
        double x2 = endPosition.lng(), y2 = endPosition.lat();
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double dis = this.distanceTo(startPosition, otherPosition);
        return dis < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] points = region.vertices();
        double x0 = points[0].lng(), y0 = points[0].lat();
        double x1 = points[2].lng(), y1 = points[2].lat();
        double x = position.lng(), y = position.lat();
        return ((x > x0) && (x < x1)) && ((y1 < y) && (y < y0));
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        if (angle == 999) {
            return startPosition;
        }
        double delta_x = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angle);
        double delta_y = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angle);
        double x = startPosition.lng(), y = startPosition.lat();
        return new LngLat(x + delta_x, y + delta_y);
    }
}
