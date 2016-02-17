package barqsoft.footballscores;

/**
 * Created by abhijeet.burle on 2016/02/17.
 */
public class GlobalConstants {

    public enum League {
        SERIE_A(357,"Serie A"),
        SERIE_A1(401,"Serie A"),

        PREMIER_LEGAUE(354,"Premier League"),
        PREMIER_LEAGUE(398,"Premier League"),
        PRIMERA_LIGA(402,"Premier League"),

        CHAMPIONS_LEAGUE(362,"UEFA Champions League"),

        PRIMERA_DIVISION(358,"First Division"),
        PRIMERA_DIVISION1(399,"First Division"),

        SEGUNDA_DIVISION(400, "Second Division"),

        BUNDESLIGA(351,"Bundesliga"),
        BUNDESLIGA1(394,"Bundesliga"),
        BUNDESLIGA2(395,"Bundesliga"),
        BUNDESLIGA3(403,"Bundesliga"),

        LIGUE1(396,"League 1"),
        LIGUE2(397,"League 2"),

        EREDIVISIE(404,"Eredivisie")
        ;

        public final int leagueId;
        public final String leagueName;

        League(int id, String name) {
            leagueId=id;
            leagueName=name;
        }

        public boolean equalsId(int otherId) {
            return leagueId==otherId;
        }
        public static League getById(int id) {
            for(League e : values()) {
                if(e.equalsId(id))
                    return e;
            }
            return null;
        }

        public String toString() {
            return this.leagueId+"";
        }
    }
    
    public enum Team{
        arsenal("Arsenal London FC"),
        manchester_united( "Manchester United FC"),
        swansea_city_afc("Swansea City"),
        leicester_city_fc("Leicester City"),
        everton_fc("Everton FC"),
        west_ham("West Ham United FC"),
        tottenham_hotspur("Tottenham Hotspur FC"),
        west_bromwich_albion("West Bromwich Albion"),
        sunderland("Sunderland AFC"),
        stoke_city("Stoke City FC"),
        aston_villa("Aston Villa"),
        burney_fc("Burney FC"),
        chelsea("Chelsea"),
        crystal_palace_fc("Crystal Palace"),
        hull_city_afc("Hull City AFC"),
        liverpool("Liverpool"),
        manchester_city("Manchester City"),
        newcastle_united("Newcastle United"),
        queens_park_rangers("Queens Park Rangers"),
        southampton_fc("Southampton FC")
        ;
        private final String teamName;
        private Team(String s) {
            teamName = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : teamName.equals(otherName);
        }

        public String toString() {
            return this.teamName;
        }
    }

}
