package base;

/**
 * MI => minutes
 * H => hours
 * D => days
 * MO => months
 * Y => years
 */
public enum ExpirationTime {
    MI_10,
    MI_30,
    H_1,
    H_10,
    D_1,
    D_5,
    D_10,
    MO_1,
    MO_6,
    Y_1;

    public String getTimeUnit() {
        String name = this.name().split("_")[0];
        String unit = null;

        switch (name) {
            case "MI":
                unit = "minutes";
                break;
            case "H":
                unit = "hours";
                break;
            case "D":
                unit = "days";
                break;
            case "MO":
                unit = "months";
                break;
            case "Y":
                unit = "years";
                break;
        }

        return unit;
    }

    public int getTimeValue() {
        return Integer.parseInt(this.name().split("_")[1]);
    }
}
