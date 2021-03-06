package game;

import achievement.Achievement;
import achievement.AchievementSetFactory;
import statistic.StatSet;
import statistic.constants.StatKey;

import java.util.ArrayList;
import java.util.HashSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Map.Entry;
import static statistic.constants.StatAttribute.LIFETIME;

public class Player {
    private final String name;
    private final StatSet lifetimeStats;
    private StatSet lastGameStats;
    private final HashSet<Achievement> possibleAwards;
    private final HashSet<Achievement> awardsEarned;

    public Player(String name, StatSet statSet, HashSet<Achievement> achievementSet) {
        this.name = name;
        this.lifetimeStats = statSet;
        this.lastGameStats = null;
        this.possibleAwards = achievementSet;
        this.awardsEarned = newHashSet();
    }

    // factory method for creating new player quickly
    public static Player createPlayer(String name) {
        final AchievementSetFactory achievementSetFactory = new AchievementSetFactory();
        return new Player(name, new StatSet(LIFETIME), achievementSetFactory.createFullAchievementSet());
    }

    public void updateStats(StatSet gameStats) {
        lastGameStats = gameStats;
        for (Entry stat : gameStats) {
            lifetimeStats.update((StatKey) stat.getKey(), (Integer) stat.getValue());
        }
    }

    public void evaluateAchievements() throws Exception {
        if (lastGameStats == null) {
            throw new Exception("Error: you haven't played a game yet!");
        }

        final ArrayList<Achievement> newlyEarnedAwards = newArrayList();

        for (Achievement award : possibleAwards) {
            if (award.evaluate(lifetimeStats)) {
                newlyEarnedAwards.add(award);
            }
            if (award.evaluate(lastGameStats)) {
                newlyEarnedAwards.add(award);
            }
        }

        for (Achievement award : newlyEarnedAwards) {
            possibleAwards.remove(award);
            awardsEarned.add(award);
            System.out.print(name + ": ");
            award.claim();
        }
    }

    public String getName() {
        return name;
    }

    public int getStat(StatKey key) {
        return lifetimeStats.getStat(key);
    }
}
