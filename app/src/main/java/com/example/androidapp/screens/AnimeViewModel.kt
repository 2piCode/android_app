package com.example.androidapp.screens

import androidx.lifecycle.ViewModel

class AnimeViewModel : ViewModel() {
    private val _anime = mutableListOf<Anime>()

    val anime: List<Anime>
        get() = _anime

    init {
        _anime.addAll(
            listOf(
                Anime(
                    1,
                    "Sousou no Frieren",
                    "During their decade-long quest to defeat the Demon King, the members of the hero's party—Himmel himself, the priest Heiter, the dwarf warrior Eisen, and the elven mage Frieren—forge bonds through adventures and battles, creating unforgettable precious memories for most of them.\\n\\nHowever, the time that Frieren spends with her comrades is equivalent to merely a fraction of her life, which has lasted over a thousand years. When the party disbands after their victory, Frieren casually returns to her \"usual\" routine of collecting spells across the continent. Due to her different sense of time, she seemingly holds no strong feelings toward the experiences she went through.\\n\\nAs the years pass, Frieren gradually realizes how her days in the hero's party truly impacted her. Witnessing the deaths of two of her former companions, Frieren begins to regret having taken their presence for granted; she vows to better understand humans and create real personal connections. Although the story of that once memorable journey has long ended, a new tale is about to begin.\\n\\n",
                    "https://cdn.myanimelist.net/images/anime/1015/138006.jpg",
                    "https://cdn.myanimelist.net/images/anime/1015/138006t.jpg",
                    1111,
                    listOf(
                        Genre.ADVENTURE,
                        Genre.FANTASY,
                        Genre.ACTION
                    ),
                    listOf(
                        "https://google.com",
                        "https://google.com",
                        "https://google.com",
                        "https://google.com"
                    )
                ), Anime(
                    2,
                    "One Piece Fan Letter",
                    "Two years after the Summit War in which Straw Hat pirate Luffy lost his brother Ace, the story takes place on the Sabaody Archipelago. The protagonist, a young girl who has a strong admiration for Nami, sets off on a small adventure. This is an ensemble drama that focuses on people who do not \"pursue\" ONE PIECE, depicting the reunion of the Straw Hat Pirates from their perspective.\\n\\n",
                    "https://cdn.myanimelist.net/images/anime/1675/145881.jpg",
                    "https://cdn.myanimelist.net/images/anime/1675/145881t.jpg",
                    1111,
                    listOf(
                        Genre.ADVENTURE,
                        Genre.COMEDY,
                        Genre.ACTION
                    ),
                    listOf(
                        "https://google.com"
                    )
                ), Anime(
                    3,
                    "Fullmetal Alchemist: Brotherhood",
                    "After a horrific alchemy experiment goes wrong in the Elric household, brothers Edward and Alphonse are left in a catastrophic new reality. Ignoring the alchemical principle banning human transmutation, the boys attempted to bring their recently deceased mother back to life. Instead, they suffered brutal personal loss: Alphonse's body disintegrated while Edward lost a leg and then sacrificed an arm to keep Alphonse's soul in the physical realm by binding it to a hulking suit of armor.\\n\\nThe brothers are rescued by their neighbor Pinako Rockbell and her granddaughter Winry. Known as a bio-mechanical engineering prodigy, Winry creates prosthetic limbs for Edward by utilizing \"automail,\" a tough, versatile metal used in robots and combat armor. After years of training, the Elric brothers set off on a quest to restore their bodies by locating the Philosopher's Stone—a powerful gem that allows an alchemist to defy the traditional laws of Equivalent Exchange.\\n\\nAs Edward becomes an infamous alchemist and gains the nickname \"Fullmetal,\" the boys' journey embroils them in a growing conspiracy that threatens the fate of the world.\\n\\n",
                    "https://cdn.myanimelist.net/images/anime/1208/94745.jpg",
                    "https://cdn.myanimelist.net/images/anime/1208/94745t.jpg",
                    1111,
                    listOf(
                        Genre.ACTION,
                        Genre.ADVENTURE,
                        Genre.DRAMA,
                        Genre.FANTASY
                    ),
                    listOf(
                        "https://google.com"
                    )
                ), Anime(
                    4,
                    "Steins;Gate",
                    "Eccentric scientist Rintarou Okabe has a never-ending thirst for scientific exploration. Together with his ditzy but well-meaning friend Mayuri Shiina and his roommate Itaru Hashida, Okabe founds the Future Gadget Laboratory in the hopes of creating technological innovations that baffle the human psyche. Despite claims of grandeur, the only notable \"gadget\" the trio have created is a microwave that has the mystifying power to turn bananas into green goo.\\n\\nHowever, when Okabe attends a conference on time travel, he experiences a series of strange events that lead him to believe that there is more to the \"Phone Microwave\" gadget than meets the eye. Apparently able to send text messages into the past using the microwave, Okabe dabbles further with the \"time machine,\" attracting the ire and attention of the mysterious organization SERN.\\n\\nDue to the novel discovery, Okabe and his friends find themselves in an ever-present danger. As he works to mitigate the damage his invention has caused to the timeline, Okabe fights a battle to not only save his loved ones but also to preserve his degrading sanity.\\n\\n",
                    "https://cdn.myanimelist.net/images/anime/1935/127974.jpg",
                    "https://cdn.myanimelist.net/images/anime/1935/127974t.jpg",
                    1111,
                    listOf(
                        Genre.THRILLER,
                        Genre.SCIENCE_FICTION,
                        Genre.PSYCHOLOGICAL
                    ),
                    listOf(
                        "https://google.com"
                    )
                ), Anime(
                    5,
                    "Attack on Titan Season 3 Part 2",
                    "Seeking to restore humanity's diminishing hope, the Survey Corps embark on a mission to retake Wall Maria, where the battle against the merciless \"Titans\" takes the stage once again.\\n\\nReturning to the tattered Shiganshina District that was once his home, Eren Yeager and the Corps find the town oddly unoccupied by Titans. Even after the outer gate is plugged, they strangely encounter no opposition. The mission progresses smoothly until Armin Arlert, highly suspicious of the enemy's absence, discovers distressing signs of a potential scheme against them. \\n\\nShingeki no Kyojin Season 3 Part 2 follows Eren as he vows to take back everything that was once his. Alongside him, the Survey Corps strive—through countless sacrifices—to carve a path towards victory and uncover the secrets locked away in the Yeager family's basement.\\n\\n",
                    "https://cdn.myanimelist.net/images/anime/1517/100633.jpg",
                    "https://cdn.myanimelist.net/images/anime/1517/100633t.jpg",
                    1111,
                    listOf(
                        Genre.ACTION,
                        Genre.DRAMA,
                        Genre.FANTASY
                    ),
                    listOf(
                        "https://google.com"
                    )
                )
            )
        )
    }
}