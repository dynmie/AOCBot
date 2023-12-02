package me.dynmie.aoc.yukino.aoc.strikes;

import me.dynmie.aoc.yukino.database.DBSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dynmie
 */
public class Strike implements DBSerializable {

    private final long when;
    private String reason;

    public Strike(long when, String reason) {
        this.when = when;
        this.reason = reason;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();

        ret.put("when", when);
        ret.put("reason", reason);

        return ret;
    }

    public static Strike deserialize(Map<String, Object> map) {
        return new Strike(
                (long) map.get("when"),
                (String) map.get("reason")
        );
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getWhen() {
        return when;
    }

}
