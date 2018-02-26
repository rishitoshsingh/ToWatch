package com.example.rishi.towatch

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by rishi on 25/2/18.
 */
class QueryMovie {

    private val SAMPLE_JSON_RESPONSE = "{\"page\":1,\"total_results\":378467,\"total_pages\":18924,\"results\":[{\"vote_count\":7007,\"id\":198663,\"video\":false,\"vote_average\":7,\"title\":\"The Maze Runner\",\"popularity\":456.70938,\"poster_path\":\"/coss7RgL0NH6g4fC2s5atvf3dFO.jpg\",\"original_language\":\"en\",\"original_title\":\"The Maze Runner\",\"genre_ids\":[28,9648,878,53],\"backdrop_path\":\"/lkOZcsXcOLZYeJ2YxJd3vSldvU4.jpg\",\"adult\":false,\"overview\":\"Set in a post-apocalyptic world, young Thomas is deposited in a community of boys after his memory is erased, soon learning they're all trapped in a maze that will require him to join forces with fellow “runners” for a shot at escape.\",\"release_date\":\"2014-09-10\"},{\"vote_count\":125,\"id\":441614,\"video\":false,\"vote_average\":4.7,\"title\":\"Loving\",\"popularity\":432.74585,\"poster_path\":\"/6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg\",\"original_language\":\"es\",\"original_title\":\"Amar\",\"genre_ids\":[10749],\"backdrop_path\":\"/iBM6zvlOmcfodGhUa36sy7pM2Er.jpg\",\"adult\":false,\"overview\":\"Laura and Carlos love each other as if every day was the last, and perhaps that first love intensity is what will tear them apart a year later.\",\"release_date\":\"2017-04-21\"},{\"vote_count\":520,\"id\":337167,\"video\":false,\"vote_average\":6.5,\"title\":\"Fifty Shades Freed\",\"popularity\":429.5454,\"poster_path\":\"/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg\",\"original_language\":\"en\",\"original_title\":\"Fifty Shades Freed\",\"genre_ids\":[18,10749],\"backdrop_path\":\"/9ywA15OAiwjSTvg3cBs9B7kOCBF.jpg\",\"adult\":false,\"overview\":\"Believing they have left behind shadowy figures from their past, newlyweds Christian and Ana fully embrace an inextricable connection and shared life of luxury. But just as she steps into her role as Mrs. Grey and he relaxes into an unfamiliar stability, new threats could jeopardize their happy ending before it even begins.\",\"release_date\":\"2018-02-07\"},{\"vote_count\":4248,\"id\":284053,\"video\":false,\"vote_average\":7.4,\"title\":\"Thor: Ragnarok\",\"popularity\":318.10056,\"poster_path\":\"/oSLd5GYGsiGgzDPKTwQh7wamO8t.jpg\",\"original_language\":\"en\",\"original_title\":\"Thor: Ragnarok\",\"genre_ids\":[878,28,12,35,14],\"backdrop_path\":\"/5wNUJs23rT5rTBacNyf5h83AynM.jpg\",\"adult\":false,\"overview\":\"Thor is imprisoned on the other side of the universe and finds himself in a race against time to get back to Asgard to stop Ragnarok, the prophecy of destruction to his homeworld and the end of Asgardian civilization, at the hands of an all-powerful new threat, the ruthless Hela.\",\"release_date\":\"2017-10-25\"},{\"vote_count\":1473,\"id\":284054,\"video\":false,\"vote_average\":7.4,\"title\":\"Black Panther\",\"popularity\":290.14264,\"poster_path\":\"/bLBUCtMQGJclH36clliPLmljMys.jpg\",\"original_language\":\"en\",\"original_title\":\"Black Panther\",\"genre_ids\":[28,12,14,878],\"backdrop_path\":\"/b6ZJZHUdMEFECvGiDpJjlfUWela.jpg\",\"adult\":false,\"overview\":\"After the events of Captain America: Civil War, King T'Challa returns home to the reclusive, technologically advanced African nation of Wakanda to serve as his country's new leader. However, T'Challa soon finds that he is challenged for the throne from factions within his own country. When two foes conspire to destroy Wakanda, the hero known as Black Panther must team up with C.I.A. agent Everett K. Ross and members of the Dora Milaje, Wakandan special forces, to prevent Wakanda from being dragged into a world war.\",\"release_date\":\"2018-02-13\"},{\"vote_count\":3389,\"id\":335984,\"video\":false,\"vote_average\":7.4,\"title\":\"Blade Runner 2049\",\"popularity\":286.84583,\"poster_path\":\"/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg\",\"original_language\":\"en\",\"original_title\":\"Blade Runner 2049\",\"genre_ids\":[9648,878,53],\"backdrop_path\":\"/mVr0UiqyltcfqxbAUcLl9zWL8ah.jpg\",\"adult\":false,\"overview\":\"Thirty years after the events of the first film, a new blade runner, LAPD Officer K, unearths a long-buried secret that has the potential to plunge what's left of society into chaos. K's discovery leads him on a quest to find Rick Deckard, a former LAPD blade runner who has been missing for 30 years.\",\"release_date\":\"2017-10-04\"},{\"vote_count\":6261,\"id\":269149,\"video\":false,\"vote_average\":7.7,\"title\":\"Zootopia\",\"popularity\":258.9728,\"poster_path\":\"/sM33SANp9z6rXW8Itn7NnG1GOEs.jpg\",\"original_language\":\"en\",\"original_title\":\"Zootopia\",\"genre_ids\":[16,12,10751,35],\"backdrop_path\":\"/mhdeE1yShHTaDbJVdWyTlzFvNkr.jpg\",\"adult\":false,\"overview\":\"Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, jumps at the chance to crack her first case - even if it means partnering with scam-artist fox Nick Wilde to solve the mystery.\",\"release_date\":\"2016-02-11\"},{\"vote_count\":7284,\"id\":321612,\"video\":false,\"vote_average\":6.8,\"title\":\"Beauty and the Beast\",\"popularity\":250.54068,\"poster_path\":\"/tWqifoYuwLETmmasnGHO7xBjEtt.jpg\",\"original_language\":\"en\",\"original_title\":\"Beauty and the Beast\",\"genre_ids\":[10751,14,10749],\"backdrop_path\":\"/6aUWe0GSl69wMTSWWexsorMIvwU.jpg\",\"adult\":false,\"overview\":\"A live-action adaptation of Disney's version of the classic tale of a cursed prince and a beautiful young woman who helps him break the spell.\",\"release_date\":\"2017-03-16\"},{\"vote_count\":5019,\"id\":374720,\"video\":false,\"vote_average\":7.4,\"title\":\"Dunkirk\",\"popularity\":241.20842,\"poster_path\":\"/ebSnODDg9lbsMIaWg2uAbjn7TO5.jpg\",\"original_language\":\"en\",\"original_title\":\"Dunkirk\",\"genre_ids\":[28,18,36,53,10752],\"backdrop_path\":\"/4yjJNAgXBmzxpS6sogj4ftwd270.jpg\",\"adult\":false,\"overview\":\"The story of the miraculous evacuation of Allied soldiers from Belgium, Britain, Canada and France, who were cut off and surrounded by the German army from the beaches and harbour of Dunkirk between May 26th and June 4th 1940 during World War II.\",\"release_date\":\"2017-07-19\"},{\"vote_count\":3507,\"id\":254128,\"video\":false,\"vote_average\":6,\"title\":\"San Andreas\",\"popularity\":216.13129,\"poster_path\":\"/qey0tdcOp9kCDdEZuJ87yE3crSe.jpg\",\"original_language\":\"en\",\"original_title\":\"San Andreas\",\"genre_ids\":[28,18,53],\"backdrop_path\":\"/cUfGqafAVQkatQ7N4y08RNV3bgu.jpg\",\"adult\":false,\"overview\":\"In the aftermath of a massive earthquake in California, a rescue-chopper pilot makes a dangerous journey across the state in order to rescue his estranged daughter.\",\"release_date\":\"2015-05-27\"},{\"vote_count\":4217,\"id\":216015,\"video\":false,\"vote_average\":5.3,\"title\":\"Fifty Shades of Grey\",\"popularity\":210.49261,\"poster_path\":\"/jV8wnk3Jgz6f7degmT3lHNGI2tK.jpg\",\"original_language\":\"en\",\"original_title\":\"Fifty Shades of Grey\",\"genre_ids\":[18,10749,53],\"backdrop_path\":\"/zAd0MjURkOvJynIsqmLMBcICxUt.jpg\",\"adult\":false,\"overview\":\"When college senior Anastasia Steele steps in for her sick roommate to interview prominent businessman Christian Grey for their campus paper, little does she realize the path her life will take. Christian, as enigmatic as he is rich and powerful, finds himself strangely drawn to Ana, and she to him. Though sexually inexperienced, Ana plunges headlong into an affair -- and learns that Christian's true sexual proclivities push the boundaries of pain and pleasure.\",\"release_date\":\"2015-02-11\"},{\"vote_count\":2752,\"id\":343668,\"video\":false,\"vote_average\":7,\"title\":\"Kingsman: The Golden Circle\",\"popularity\":208.5258,\"poster_path\":\"/34xBL6BXNYFqtHO9zhcgoakS4aP.jpg\",\"original_language\":\"en\",\"original_title\":\"Kingsman: The Golden Circle\",\"genre_ids\":[28,12,35],\"backdrop_path\":\"/uExPmkOHJySrbJyJDJylHDqaT58.jpg\",\"adult\":false,\"overview\":\"When an attack on the Kingsman headquarters takes place and a new villain rises, Eggsy and Merlin are forced to work together with the American agency known as the Statesman to save the world.\",\"release_date\":\"2017-09-20\"},{\"vote_count\":252,\"id\":347882,\"video\":false,\"vote_average\":5.2,\"title\":\"Sleight\",\"popularity\":198.39545,\"poster_path\":\"/wridRvGxDqGldhzAIh3IcZhHT5F.jpg\",\"original_language\":\"en\",\"original_title\":\"Sleight\",\"genre_ids\":[18,53,28,878],\"backdrop_path\":\"/2SEgJ0mHJ7TSdVDbkGU061tR33K.jpg\",\"adult\":false,\"overview\":\"A young street magician is left to take care of his little sister after his mother's passing and turns to drug dealing in the Los Angeles party scene to keep a roof over their heads. When he gets into trouble with his supplier, his sister is kidnapped and he is forced to rely on both his sleight of hand and brilliant mind to save her.\",\"release_date\":\"2017-04-28\"},{\"vote_count\":8199,\"id\":99861,\"video\":false,\"vote_average\":7.3,\"title\":\"Avengers: Age of Ultron\",\"popularity\":177.0963,\"poster_path\":\"/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg\",\"original_language\":\"en\",\"original_title\":\"Avengers: Age of Ultron\",\"genre_ids\":[28,12,878],\"backdrop_path\":\"/vXIrvKadue7GdySiVh3huoQZiMi.jpg\",\"adult\":false,\"overview\":\"When Tony Stark tries to jumpstart a dormant peacekeeping program, things go awry and Earth’s Mightiest Heroes are put to the ultimate test as the fate of the planet hangs in the balance. As the villainous Ultron emerges, it is up to The Avengers to stop him from enacting his terrible plans, and soon uneasy alliances and unexpected action pave the way for an epic and unique global adventure.\",\"release_date\":\"2015-04-22\"},{\"vote_count\":124,\"id\":335777,\"video\":false,\"vote_average\":5.8,\"title\":\"The Nut Job 2: Nutty by Nature\",\"popularity\":162.1201,\"poster_path\":\"/xOfdQHNF9TlrdujyAjiKfUhxSXy.jpg\",\"original_language\":\"en\",\"original_title\":\"The Nut Job 2: Nutty by Nature\",\"genre_ids\":[10751,16,12,35],\"backdrop_path\":\"/bd1X5nNrrAHVGG0MxsAeCOPPh1w.jpg\",\"adult\":false,\"overview\":\"When the evil mayor of Oakton decides to bulldoze Liberty Park and build a dangerous amusement park in its place, Surly Squirrel and his ragtag group of animal friends need to band together to save their home, defeat the mayor, and take back the park.\",\"release_date\":\"2017-08-11\"},{\"vote_count\":5827,\"id\":211672,\"video\":false,\"vote_average\":6.4,\"title\":\"Minions\",\"popularity\":155.51913,\"poster_path\":\"/q0R4crx2SehcEEQEkYObktdeFy.jpg\",\"original_language\":\"en\",\"original_title\":\"Minions\",\"genre_ids\":[10751,16,12,35],\"backdrop_path\":\"/qLmdjn2fv0FV2Mh4NBzMArdA0Uu.jpg\",\"adult\":false,\"overview\":\"Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.\",\"release_date\":\"2015-06-17\"},{\"vote_count\":2960,\"id\":214756,\"video\":false,\"vote_average\":6.2,\"title\":\"Ted 2\",\"popularity\":155.26021,\"poster_path\":\"/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg\",\"original_language\":\"en\",\"original_title\":\"Ted 2\",\"genre_ids\":[35],\"backdrop_path\":\"/nkwoiSVJLeK0NI8kTqioBna61bm.jpg\",\"adult\":false,\"overview\":\"Newlywed couple Ted and Tami-Lynn want to have a baby, but in order to qualify to be a parent, Ted will have to prove he's a person in a court of law.\",\"release_date\":\"2015-06-25\"},{\"vote_count\":3015,\"id\":258489,\"video\":false,\"vote_average\":5.6,\"title\":\"The Legend of Tarzan\",\"popularity\":148.8348,\"poster_path\":\"/6FxOPJ9Ysilpq0IgkrMJ7PubFhq.jpg\",\"original_language\":\"en\",\"original_title\":\"The Legend of Tarzan\",\"genre_ids\":[28,12],\"backdrop_path\":\"/pWNBPN8ghaKtGLcQBMwNyM32Wbm.jpg\",\"adult\":false,\"overview\":\"Tarzan, having acclimated to life in London, is called back to his former home in the jungle to investigate the activities at a mining encampment.\",\"release_date\":\"2016-06-29\"},{\"vote_count\":2165,\"id\":339964,\"video\":false,\"vote_average\":6.6,\"title\":\"Valerian and the City of a Thousand Planets\",\"popularity\":148.42685,\"poster_path\":\"/jfIpMh79fGRqYJ6PwZLCntzgxlF.jpg\",\"original_language\":\"en\",\"original_title\":\"Valerian and the City of a Thousand Planets\",\"genre_ids\":[12,878,28],\"backdrop_path\":\"/7WjMTRF6LDa4latRUIDM25xnDO0.jpg\",\"adult\":false,\"overview\":\"In the 28th century, Valerian and Laureline are special operatives charged with keeping order throughout the human territories. On assignment from the Minister of Defense, the two undertake a mission to Alpha, an ever-expanding metropolis where species from across the universe have converged over centuries to share knowledge, intelligence, and cultures. At the center of Alpha is a mysterious dark force which threatens the peaceful existence of the City of a Thousand Planets, and Valerian and Laureline must race to identify the menace and safeguard not just Alpha, but the future of the universe.\",\"release_date\":\"2017-07-20\"},{\"vote_count\":3384,\"id\":315837,\"video\":false,\"vote_average\":5.9,\"title\":\"Ghost in the Shell\",\"popularity\":146.41776,\"poster_path\":\"/myRzRzCxdfUWjkJWgpHHZ1oGkJd.jpg\",\"original_language\":\"en\",\"original_title\":\"Ghost in the Shell\",\"genre_ids\":[28,878,53],\"backdrop_path\":\"/jGPSVArC0GS2VVc0aGAqGTjfFOG.jpg\",\"adult\":false,\"overview\":\"In the near future, Major is the first of her kind: a human saved from a terrible crash, then cyber-enhanced to be a perfect soldier devoted to stopping the world's most dangerous criminals.\",\"release_date\":\"2017-03-29\"}]}"


    fun extractMovies(): ArrayList<Movie> {
        var movies = ArrayList<Movie>()

        try {
            val root = JSONObject(SAMPLE_JSON_RESPONSE)
            val results = root.getJSONArray("results")
            for (index in 0 until results.length()) {
                val movieObject = results.getJSONObject(index)
                val title = movieObject.getString("title")
                val releaseDate = movieObject.getString("release_date")
                val language = movieObject.getString("original_language")
                val imdbId = movieObject.getInt("id")
                val overview = movieObject.getString("overview")
                val adult = movieObject.getBoolean("adult")
                val genreIdsJson = movieObject.getJSONArray("genre_ids")
                var genreIds = ArrayList<Int>()
                for (id in 0 until genreIdsJson.length()) {
                    genreIds.add(genreIdsJson.getInt(id))
                }
                val posterPath = movieObject.getString("poster_path")
                val backdropPath = movieObject.getString("backdrop_path")
                movies.add(Movie(title, releaseDate, language, imdbId, overview, adult, genreIds, posterPath, backdropPath))
            }
        } catch (ex: JSONException) {
            Log.v("Movie Extractor", ex.toString())
        }
        return movies
    }


}