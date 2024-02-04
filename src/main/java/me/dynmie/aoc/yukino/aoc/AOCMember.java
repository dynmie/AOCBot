package me.dynmie.aoc.yukino.aoc;

import me.dynmie.aoc.yukino.aoc.strikes.Strike;
import me.dynmie.aoc.yukino.database.DBSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author dynmie
 */
public class AOCMember implements DBSerializable {

    private final UUID uniqueId;
    private String discordId;
    private final long joined;
    private String firstName;
    private String lastName;
    private final List<Strike> strikes;
    private long hours;

    public AOCMember(
            UUID uniqueId,
            String discordId,
            long joined,
            String firstName,
            String lastName,
            List<Strike> strikes,
            long hours
    ) {
        this.uniqueId = uniqueId;
        this.discordId = discordId;
        this.joined = joined;
        this.firstName = firstName;
        this.lastName = lastName;
        this.strikes = strikes;
        this.hours = hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getHours() {
        return hours;
    }

    public String getDiscordId() {
        return discordId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();

        ret.put("uniqueId", uniqueId.toString());
        ret.put("discordId", discordId);
        ret.put("joined", joined);
        ret.put("firstName", firstName);
        ret.put("lastName", lastName);
        ret.put("strikes", strikes.stream().map(Strike::serialize).toList());
        ret.put("hours", hours);

        return ret;
    }

    @SuppressWarnings("unchecked")
    public static AOCMember deserialize(Map<String, Object> map) {
        List<Map<String, Object>> strikesMapList = (List<Map<String, Object>>) map.get("strikes");
        List<Strike> strikes = new ArrayList<>();
        for (Map<String, Object> strikesMap : strikesMapList) {
            Strike strike = DBSerializable.deserialize(strikesMap, Strike.class);
            strikes.add(strike);
        }

        return new AOCMember(
                UUID.fromString((String) map.get("uniqueId")),
                (String) map.get("discordId"),
                (Long) map.get("joined"),
                (String) map.get("firstName"),
                (String) map.get("lastName"),
                strikes,
                (long) map.getOrDefault("hours", 0L)
        );
    }

    public long getJoined() {
        return joined;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Strike> getStrikes() {
        return strikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AOCMember aocMember = (AOCMember) o;
        return Objects.equals(uniqueId, aocMember.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

}
