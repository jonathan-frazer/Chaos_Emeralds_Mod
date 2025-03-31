package net.sonicrushxii.chaos_emerald.capabilities;

public enum EmeraldAbility
{
    CHAOS_CONTROL(null),
    AQUA_EMERALD(0x00FFFF),
    BLUE_EMERALD(0x0000FF),
    GREEN_EMERALD(0x00FF00),
    GREY_EMERALD(0xEEEEEE),
    PURPLE_EMERALD(0xCC00FF),
    RED_EMERALD(0xFF0000),
    YELLOW_EMERALD(0xFFFF00);

    private final Integer color;

    EmeraldAbility(Integer color) {
        this.color = color;
    }

    public Integer color()
    {
        return this.color;
    }
}
