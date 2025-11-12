package com.rainhockey.apps.mtavz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rainhockey.apps.mtavz.data.database.HockeyDatabase
import com.rainhockey.apps.mtavz.data.models.Match
import com.rainhockey.apps.mtavz.data.models.Team
import com.rainhockey.apps.mtavz.data.models.TeamStats
import com.rainhockey.apps.mtavz.data.repository.HockeyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HockeyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HockeyDatabase.getDatabase(application)
    private val repository = HockeyRepository(database.teamDao(), database.matchDao())
    
    private val _selectedLeague = MutableStateFlow("NHL")
    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()
    
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()
    
    private val _teamStats = MutableStateFlow<List<TeamStats>>(emptyList())
    val teamStats: StateFlow<List<TeamStats>> = _teamStats.asStateFlow()
    
    private val _isDataInitialized = MutableStateFlow(false)
    val isDataInitialized: StateFlow<Boolean> = _isDataInitialized.asStateFlow()
    
    init {
        initializeData()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            val count = repository.getTeamsCount()
            if (count == 0) {
                repository.insertTeams(getAllLeaguesTeams())
            }
            _isDataInitialized.value = true
            loadTeams()
            loadTeamStats()
        }
    }
    
    fun setSelectedLeague(league: String) {
        _selectedLeague.value = league
        loadTeams()
        loadTeamStats()
    }
    
    private fun loadTeams() {
        viewModelScope.launch {
            repository.getTeamsByLeague(_selectedLeague.value).collect { teamList ->
                _teams.value = teamList
            }
        }
    }
    
    private fun loadTeamStats() {
        viewModelScope.launch {
            repository.getTeamStats(_selectedLeague.value).collect { stats ->
                _teamStats.value = stats
            }
        }
    }
    
    fun saveMatch(match: Match) {
        viewModelScope.launch {
            repository.insertMatch(match)
            loadTeamStats()
        }
    }
    
    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            repository.insertTeams(getAllLeaguesTeams())
            loadTeams()
            loadTeamStats()
        }
    }
    
    private fun getAllLeaguesTeams(): List<Team> {
        val teams = mutableListOf<Team>()
        
        teams.addAll(listOf(
            Team(name = "Bruins", city = "Boston", league = "NHL", logoColor = "#FFD700", logoResName = "team_nhl_bruins"),
            Team(name = "Maple Leafs", city = "Toronto", league = "NHL", logoColor = "#00205B", logoResName = "team_nhl_maple_leafs"),
            Team(name = "Canadiens", city = "Montreal", league = "NHL", logoColor = "#AF1E2D", logoResName = "team_nhl_canadiens"),
            Team(name = "Rangers", city = "New York", league = "NHL", logoColor = "#0038A8", logoResName = "team_nhl_rangers"),
            Team(name = "Penguins", city = "Pittsburgh", league = "NHL", logoColor = "#FCB514", logoResName = "team_nhl_penguins"),
            Team(name = "Blackhawks", city = "Chicago", league = "NHL", logoColor = "#CF0A2C", logoResName = "team_nhl_blackhawks"),
            Team(name = "Red Wings", city = "Detroit", league = "NHL", logoColor = "#CE1126", logoResName = "team_nhl_red_wings"),
            Team(name = "Flyers", city = "Philadelphia", league = "NHL", logoColor = "#F74902", logoResName = "team_nhl_flyers"),
            Team(name = "Capitals", city = "Washington", league = "NHL", logoColor = "#C8102E", logoResName = "team_nhl_capitals"),
            Team(name = "Lightning", city = "Tampa Bay", league = "NHL", logoColor = "#002868", logoResName = "team_nhl_lightning"),
            Team(name = "Avalanche", city = "Colorado", league = "NHL", logoColor = "#6F263D", logoResName = "team_nhl_avalanche"),
            Team(name = "Oilers", city = "Edmonton", league = "NHL", logoColor = "#FF4C00", logoResName = "team_nhl_oilers"),
            Team(name = "Kings", city = "Los Angeles", league = "NHL", logoColor = "#111111", logoResName = "team_nhl_kings"),
            Team(name = "Golden Knights", city = "Vegas", league = "NHL", logoColor = "#B4975A", logoResName = "team_nhl_golden_knights"),
            Team(name = "Hurricanes", city = "Carolina", league = "NHL", logoColor = "#CC0000", logoResName = "team_nhl_hurricanes"),
            Team(name = "Panthers", city = "Florida", league = "NHL", logoColor = "#041E42", logoResName = "team_nhl_panthers"),
            Team(name = "Devils", city = "New Jersey", league = "NHL", logoColor = "#CE1126", logoResName = "team_nhl_devils"),
            Team(name = "Islanders", city = "New York", league = "NHL", logoColor = "#00539B", logoResName = "team_nhl_islanders"),
            Team(name = "Blue Jackets", city = "Columbus", league = "NHL", logoColor = "#002654", logoResName = "team_nhl_blue_jackets"),
            Team(name = "Predators", city = "Nashville", league = "NHL", logoColor = "#FFB81C", logoResName = "team_nhl_predators"),
            Team(name = "Stars", city = "Dallas", league = "NHL", logoColor = "#006847", logoResName = "team_nhl_stars"),
            Team(name = "Wild", city = "Minnesota", league = "NHL", logoColor = "#154734", logoResName = "team_nhl_wild"),
            Team(name = "Blues", city = "St. Louis", league = "NHL", logoColor = "#002F87", logoResName = "team_nhl_blues"),
            Team(name = "Canucks", city = "Vancouver", league = "NHL", logoColor = "#00205B", logoResName = "team_nhl_canucks"),
            Team(name = "Flames", city = "Calgary", league = "NHL", logoColor = "#C8102E", logoResName = "team_nhl_flames"),
            Team(name = "Jets", city = "Winnipeg", league = "NHL", logoColor = "#041E42", logoResName = "team_nhl_jets"),
            Team(name = "Ducks", city = "Anaheim", league = "NHL", logoColor = "#F47A38", logoResName = "team_nhl_ducks"),
            Team(name = "Sharks", city = "San Jose", league = "NHL", logoColor = "#006D75", logoResName = "team_nhl_sharks"),
            Team(name = "Sabres", city = "Buffalo", league = "NHL", logoColor = "#002654", logoResName = "team_nhl_sabres"),
            Team(name = "Senators", city = "Ottawa", league = "NHL", logoColor = "#C52032", logoResName = "team_nhl_senators"),
            Team(name = "Coyotes", city = "Arizona", league = "NHL", logoColor = "#8C2633", logoResName = "team_nhl_coyotes"),
            Team(name = "Kraken", city = "Seattle", league = "NHL", logoColor = "#001628", logoResName = "team_nhl_kraken")
        ))
        
        teams.addAll(listOf(
            Team(name = "CSKA Moscow", city = "Moscow", league = "KHL", logoColor = "#D52B1E", logoResName = "team_khl_cska_moscow"),
            Team(name = "SKA Saint Petersburg", city = "Saint Petersburg", league = "KHL", logoColor = "#0032A0", logoResName = "team_khl_ska_saint_petersburg"),
            Team(name = "Ak Bars Kazan", city = "Kazan", league = "KHL", logoColor = "#00A94F", logoResName = "team_khl_ak_bars_kazan"),
            Team(name = "Avangard Omsk", city = "Omsk", league = "KHL", logoColor = "#ED1C24", logoResName = "team_khl_avangard_omsk"),
            Team(name = "Metallurg Magnitogorsk", city = "Magnitogorsk", league = "KHL", logoColor = "#000000", logoResName = "team_khl_metallurg_magnitogorsk"),
            Team(name = "Dynamo Moscow", city = "Moscow", league = "KHL", logoColor = "#0066CC", logoResName = "team_khl_dynamo_moscow"),
            Team(name = "Lokomotiv Yaroslavl", city = "Yaroslavl", league = "KHL", logoColor = "#ED1B24", logoResName = "team_khl_lokomotiv_yaroslavl"),
            Team(name = "Traktor Chelyabinsk", city = "Chelyabinsk", league = "KHL", logoColor = "#F47920", logoResName = "team_khl_traktor_chelyabinsk"),
            Team(name = "Salavat Yulaev Ufa", city = "Ufa", league = "KHL", logoColor = "#FDB913", logoResName = "team_khl_salavat_yulaev_ufa"),
            Team(name = "Spartak Moscow", city = "Moscow", league = "KHL", logoColor = "#ED1C24", logoResName = "team_khl_spartak_moscow"),
            Team(name = "Torpedo Nizhny Novgorod", city = "Nizhny Novgorod", league = "KHL", logoColor = "#0066B3", logoResName = "team_khl_torpedo_nizhny_novgorod"),
            Team(name = "Severstal Cherepovets", city = "Cherepovets", league = "KHL", logoColor = "#C41E3A", logoResName = "team_khl_severstal_cherepovets"),
            Team(name = "Vityaz Moscow", city = "Moscow", league = "KHL", logoColor = "#ED174C", logoResName = "team_khl_vityaz_moscow"),
            Team(name = "Sibir Novosibirsk", city = "Novosibirsk", league = "KHL", logoColor = "#009FDA", logoResName = "team_khl_sibir_novosibirsk"),
            Team(name = "Amur Khabarovsk", city = "Khabarovsk", league = "KHL", logoColor = "#F47920", logoResName = "team_khl_amur_khabarovsk"),
            Team(name = "Admiral Vladivostok", city = "Vladivostok", league = "KHL", logoColor = "#003DA5", logoResName = "team_khl_admiral_vladivostok")
        ))
        
        teams.addAll(listOf(
            Team(name = "Frolunda HC", city = "Gothenburg", league = "SHL", logoColor = "#DC143C", logoResName = "team_shl_frolunda_hc"),
            Team(name = "Vaxjo Lakers", city = "Vaxjo", league = "SHL", logoColor = "#FFD700", logoResName = "team_shl_vaxjo_lakers"),
            Team(name = "Farjestad BK", city = "Karlstad", league = "SHL", logoColor = "#FFFFFF", logoResName = "team_shl_farjestad_bk"),
            Team(name = "Lulea HF", city = "Lulea", league = "SHL", logoColor = "#FF6600", logoResName = "team_shl_lulea_hf"),
            Team(name = "Skelleftea AIK", city = "Skelleftea", league = "SHL", logoColor = "#FFD700", logoResName = "team_shl_skelleftea_aik"),
            Team(name = "HV71", city = "Jonkoping", league = "SHL", logoColor = "#FFFF00", logoResName = "team_shl_hv71"),
            Team(name = "Linkoping HC", city = "Linkoping", league = "SHL", logoColor = "#0000FF", logoResName = "team_shl_linkoping_hc"),
            Team(name = "Orebro HK", city = "Orebro", league = "SHL", logoColor = "#FF0000", logoResName = "team_shl_orebro_hk"),
            Team(name = "Rogle BK", city = "Angelholm", league = "SHL", logoColor = "#FFFF00", logoResName = "team_shl_rogle_bk"),
            Team(name = "Djurgarden", city = "Stockholm", league = "SHL", logoColor = "#0000FF", logoResName = "team_shl_djurgarden"),
            Team(name = "Brynas IF", city = "Gavle", league = "SHL", logoColor = "#0000FF", logoResName = "team_shl_brynas_if"),
            Team(name = "IK Oskarshamn", city = "Oskarshamn", league = "SHL", logoColor = "#FF6600", logoResName = "team_shl_ik_oskarshamn")
        ))
        
        teams.addAll(listOf(
            Team(name = "Eisbaren Berlin", city = "Berlin", league = "DEL", logoColor = "#0066B3", logoResName = "team_del_eisbaren_berlin"),
            Team(name = "Adler Mannheim", city = "Mannheim", league = "DEL", logoColor = "#003DA5", logoResName = "team_del_adler_mannheim"),
            Team(name = "Red Bull Munich", city = "Munich", league = "DEL", logoColor = "#DC0714", logoResName = "team_del_red_bull_munich"),
            Team(name = "Kolner Haie", city = "Cologne", league = "DEL", logoColor = "#ED1C24", logoResName = "team_del_kolner_haie"),
            Team(name = "Grizzlys Wolfsburg", city = "Wolfsburg", league = "DEL", logoColor = "#97D700", logoResName = "team_del_grizzlys_wolfsburg"),
            Team(name = "Straubing Tigers", city = "Straubing", league = "DEL", logoColor = "#FFC845", logoResName = "team_del_straubing_tigers"),
            Team(name = "Dusseldorfer EG", city = "Dusseldorf", league = "DEL", logoColor = "#ED1C24", logoResName = "team_del_dusseldorfer_eg"),
            Team(name = "Nurnberg Ice Tigers", city = "Nurnberg", league = "DEL", logoColor = "#003DA5", logoResName = "team_del_nurnberg_ice_tigers"),
            Team(name = "Augsburger Panther", city = "Augsburg", league = "DEL", logoColor = "#006F3C", logoResName = "team_del_augsburger_panther"),
            Team(name = "Krefeld Pinguine", city = "Krefeld", league = "DEL", logoColor = "#FFED00", logoResName = "team_del_krefeld_pinguine"),
            Team(name = "Schwenninger Wild Wings", city = "Schwenningen", league = "DEL", logoColor = "#FFED00", logoResName = "team_del_schwenninger_wild_wings"),
            Team(name = "Iserlohn Roosters", city = "Iserlohn", league = "DEL", logoColor = "#0066B3", logoResName = "team_del_iserlohn_roosters")
        ))
        
        teams.addAll(listOf(
            Team(name = "HC Ocelari Trinec", city = "Trinec", league = "CZECH", logoColor = "#003DA5", logoResName = "team_czech_hc_ocelari_trinec"),
            Team(name = "HC Sparta Praha", city = "Prague", league = "CZECH", logoColor = "#8B0000", logoResName = "team_czech_hc_sparta_praha"),
            Team(name = "HC Kometa Brno", city = "Brno", league = "CZECH", logoColor = "#0066CC", logoResName = "team_czech_hc_kometa_brno"),
            Team(name = "Bily Tygri Liberec", city = "Liberec", league = "CZECH", logoColor = "#003DA5", logoResName = "team_czech_bily_tygri_liberec"),
            Team(name = "HC Vitkovice Steel", city = "Ostrava", league = "CZECH", logoColor = "#003DA5", logoResName = "team_czech_hc_vitkovice_steel"),
            Team(name = "Mountfield HK", city = "Hradec Kralove", league = "CZECH", logoColor = "#ED1C24", logoResName = "team_czech_mountfield_hk"),
            Team(name = "HC Skoda Plzen", city = "Plzen", league = "CZECH", logoColor = "#DC143C", logoResName = "team_czech_hc_skoda_plzen"),
            Team(name = "HC Dynamo Pardubice", city = "Pardubice", league = "CZECH", logoColor = "#DC143C", logoResName = "team_czech_hc_dynamo_pardubice"),
            Team(name = "HC Olomouc", city = "Olomouc", league = "CZECH", logoColor = "#DC143C", logoResName = "team_czech_hc_olomouc"),
            Team(name = "HC Energie Karlovy Vary", city = "Karlovy Vary", league = "CZECH", logoColor = "#FFD700", logoResName = "team_czech_hc_energie_karlovy_vary")
        ))
        
        teams.addAll(listOf(
            Team(name = "HC Davos", city = "Davos", league = "NL", logoColor = "#DC143C", logoResName = "team_nl_hc_davos"),
            Team(name = "ZSC Lions", city = "Zurich", league = "NL", logoColor = "#003DA5", logoResName = "team_nl_zsc_lions"),
            Team(name = "SC Bern", city = "Bern", league = "NL", logoColor = "#DC143C", logoResName = "team_nl_sc_bern"),
            Team(name = "EV Zug", city = "Zug", league = "NL", logoColor = "#003DA5", logoResName = "team_nl_ev_zug"),
            Team(name = "HC Lugano", city = "Lugano", league = "NL", logoColor = "#FFFFFF", logoResName = "team_nl_hc_lugano"),
            Team(name = "Geneve-Servette HC", city = "Geneva", league = "NL", logoColor = "#8B0000", logoResName = "team_nl_geneve_servette_hc"),
            Team(name = "Lausanne HC", city = "Lausanne", league = "NL", logoColor = "#003DA5", logoResName = "team_nl_lausanne_hc"),
            Team(name = "HC Fribourg-Gotteron", city = "Fribourg", league = "NL", logoColor = "#003DA5", logoResName = "team_nl_hc_fribourg_gotteron"),
            Team(name = "EHC Biel", city = "Biel", league = "NL", logoColor = "#DC143C", logoResName = "team_nl_ehc_biel"),
            Team(name = "SCL Tigers", city = "Langnau", league = "NL", logoColor = "#FFD700", logoResName = "team_nl_scl_tigers"),
            Team(name = "Rapperswil-Jona Lakers", city = "Rapperswil", league = "NL", logoColor = "#DC143C", logoResName = "team_nl_rapperswil_jona_lakers"),
            Team(name = "HC Ambri-Piotta", city = "Ambri", league = "NL", logoColor = "#003DA5", logoResName = "team_nl_hc_ambri_piotta")
        ))
        
        return teams
    }
}

