package barqsoft.footballscores;

import android.content.res.Resources;

import static barqsoft.footballscores.GlobalConstants.League.SERIE_A;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities
{
    public static String getLeague(int league_num)
    {

        GlobalConstants.League league= GlobalConstants.League.getById(league_num);
        if(league==null)
            return "Not known League Please report";
        else
            return league.leagueName;
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if(league_num == GlobalConstants.League.CHAMPIONS_LEAGUE.leagueId)
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        if(GlobalConstants.Team.arsenal.equalsName(teamname)) {
            return R.drawable.arsenal;
        }
        if(GlobalConstants.Team.manchester_united.equalsName(teamname)) {
            return R.drawable.manchester_united;
        }
        if(GlobalConstants.Team.swansea_city_afc.equalsName(teamname)) {
            return R.drawable.swansea_city_afc;
        }
        if(GlobalConstants.Team.leicester_city_fc.equalsName(teamname)) {
            return R.drawable.leicester_city_fc_hd_logo;
        }
        if(GlobalConstants.Team.everton_fc.equalsName(teamname)) {
            return R.drawable.everton_fc_logo1;
        }
        if(GlobalConstants.Team.west_ham.equalsName(teamname)) {
            return R.drawable.west_ham;
        }
        if(GlobalConstants.Team.tottenham_hotspur.equalsName(teamname)) {
            return R.drawable.tottenham_hotspur;
        }
        if(GlobalConstants.Team.west_bromwich_albion.equalsName(teamname)) {
            return R.drawable.west_bromwich_albion_hd_logo;
        }
        if(GlobalConstants.Team.sunderland.equalsName(teamname)) {
            return R.drawable.sunderland;
        }
        if(GlobalConstants.Team.stoke_city.equalsName(teamname)) {
            return R.drawable.stoke_city;
        }
        if(GlobalConstants.Team.aston_villa.equalsName(teamname)) {
            return R.drawable.aston_villa;
        }
        if(GlobalConstants.Team.burney_fc.equalsName(teamname)) {
            return R.drawable.burney_fc_hd_logo;
        }
        if(GlobalConstants.Team.chelsea.equalsName(teamname)) {
            return R.drawable.chelsea;
        }
        if(GlobalConstants.Team.crystal_palace_fc.equalsName(teamname)) {
            return R.drawable.crystal_palace_fc;
        }
        if(GlobalConstants.Team.hull_city_afc.equalsName(teamname)) {
            return R.drawable.hull_city_afc_hd_logo;
        }
        if(GlobalConstants.Team.liverpool.equalsName(teamname)) {
            return R.drawable.liverpool;
        }
        if(GlobalConstants.Team.manchester_city.equalsName(teamname)) {
            return R.drawable.manchester_city;
        }
        if(GlobalConstants.Team.newcastle_united.equalsName(teamname)) {
            return R.drawable.newcastle_united;
        }
        if(GlobalConstants.Team.queens_park_rangers.equalsName(teamname)) {
            return R.drawable.queens_park_rangers_hd_logo;
        }
        if(GlobalConstants.Team.southampton_fc.equalsName(teamname)) {
            return R.drawable.southampton_fc;
        }
        return R.drawable.no_icon;

    }
}
