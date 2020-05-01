package org.example.microservicecommon.http;

public class Sort {

    private String field;

    private Direction direction;

    public Sort(final String val) {
        final String[] splitVal = val.split(",");
        this.field = splitVal[0];

        if (splitVal.length == 2) {
            if (Direction.ASC.name().equalsIgnoreCase(splitVal[1])) {
                this.direction = Direction.ASC;
            }

            if (Direction.DESC.name().equalsIgnoreCase(splitVal[1])) {
                this.direction = Direction.DESC;
            }
        } else {
            this.direction = Direction.DESC;
        }
    }

    public String getField() {
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    public Boolean isAscending() {
        return direction.equals(Direction.ASC);
    }

    public enum Direction {
        ASC,
        DESC
    }

}
