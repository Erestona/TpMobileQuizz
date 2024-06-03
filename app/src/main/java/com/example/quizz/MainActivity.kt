package com.example.quizz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mButtonSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = (application as QuizApplication).database

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        editTextUsername.setText(username)

        mButtonSubmit = findViewById(R.id.buttonSubmit)

        mButtonSubmit.setOnClickListener {
            val usernameInput = editTextUsername.text.toString()

            val regex = "^[a-zA-Z0-9_-]*$".toRegex()

            if (usernameInput.isEmpty()) {
                Toast.makeText(this, "Entrer un pseudo valide", Toast.LENGTH_SHORT).show()
            } else if (!usernameInput.matches(regex)) {
                Toast.makeText(
                    this,
                    "Le pseudo peut uniquement contenir lettres, nombres, tirets et underscores",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val editor = sharedPreferences.edit()
                editor.putString("username", usernameInput)
                editor.apply()

                val intent = Intent(this@MainActivity, GameActivity::class.java).apply {
                    putExtra("username", usernameInput)
                }
                startActivity(intent)

                editor.remove("username")
                editor.apply()

                finish()
            }
        }

        lifecycleScope.launch {
            clearDatabase()
            initializeDatabase()
        }
    }

    private suspend fun clearDatabase() {
        database.categoryDao().nukeTable()
        database.questionDao().nukeTable()
        database.answersDao().nukeTable()
    }

    private suspend fun initializeDatabase() {

        if (database.categoryDao().getNumberCategory() == 0) {
            val categories = listOf(
                Category(1, "Cinéma"),
                Category(2, "Littérature"),
                Category(3, "Jeux Vidéo"),
                Category(4, "Culture Générale")
            )
            categories.forEach { category ->
                database.categoryDao().insert(category)
            }
        }

        if (database.questionDao().getNumberQuestions() == 0) {
            val questions = listOf(
                // Catégorie Cinéma
                Question(1, "Qui a réalisé le film \"Inception\" ?", 1),
                Question(
                    2,
                    "Quel acteur joue le rôle principal dans \"Pirates des Caraïbes\" ?",
                    1
                ),
                Question(3, "Quel film a remporté l'Oscar du meilleur film en 1994 ?", 1),
                Question(
                    4,
                    "Dans quel film trouve-t-on le personnage de \"Vito Corleone\" ?",
                    1
                ),
                Question(
                    5,
                    "Quel réalisateur est connu pour ses films de suspense comme \"Psycho\" et \"Vertigo\" ?",
                    1
                ),
                Question(
                    6,
                    "Quel film d'animation de Disney met en scène un lion nommé Simba ?",
                    1
                ),
                Question(
                    7,
                    "Dans quel film trouve-t-on le personnage de \"Forrest Gump\" ?",
                    1
                ),
                Question(
                    8,
                    "Quel film de science-fiction de 1999 met en scène un personnage nommé Neo ?",
                    1
                ),
                Question(9, "Quel film de 1977 a été le premier de la saga \"Star Wars\" ?", 1),
                Question(10, "Quel acteur a joué le rôle principal dans \"Gladiator\" ?", 1),

                // Catégorie Littérature
                Question(11, "Qui a écrit \"Les Misérables\" ?", 2),
                Question(12, "Quel est le titre du premier roman de J.K. Rowling ?", 2),
                Question(13, "Qui est l'auteur de \"1984\" ?", 2),
                Question(
                    14,
                    "Quel roman commence par \"Toutes les familles heureuses se ressemblent\" ?",
                    2
                ),
                Question(
                    15,
                    "Quel écrivain est connu pour ses romans policiers mettant en scène Hercule Poirot ?",
                    2
                ),
                Question(
                    16,
                    "Quel est le titre original du roman \"Le Seigneur des Anneaux\" ?",
                    2
                ),
                Question(17, "Qui a écrit \"Moby Dick\" ?", 2),
                Question(18, "Quel est le titre du roman de George Orwell publié en 1945 ?", 2),
                Question(19, "Qui a écrit \"Pride and Prejudice\" ?", 2),
                Question(
                    20,
                    "Quel est le titre du roman de Gabriel García Márquez publié en 1967 ?",
                    2
                ),

                // Catégorie Jeux Vidéo
                Question(
                    21,
                    "Quel est le personnage principal du jeu \"The Legend of Zelda\" ?",
                    3
                ),
                Question(
                    22,
                    "Dans quel jeu vidéo les joueurs peuvent-ils attraper et entraîner des créatures appelées Pokémon ?",
                    3
                ),
                Question(
                    23,
                    "Quel jeu est considéré comme ayant popularisé le genre \"battle royale\" ?",
                    3
                ),
                Question(
                    24,
                    "Quel studio de développement est à l'origine de la série \"The Witcher\" ?",
                    3
                ),
                Question(
                    25,
                    "Dans quel jeu d'aventure de 1995 les joueurs incarnent-ils un scientifique nommé Gordon Freeman ?",
                    3
                ),
                Question(
                    26,
                    "Quel est le nom de l'épée légendaire que porte le héros dans \"Final Fantasy VII\" ?",
                    3
                ),
                Question(27, "Quel est le studio derrière le célèbre jeu \"Minecraft\" ?", 3),
                Question(
                    28,
                    "Dans la série \"Metal Gear\", quel est le véritable nom de l'agent connu sous le nom de Solid Snake ?",
                    3
                ),
                Question(29, "Quel est le jeu vidéo le plus vendu de tous les temps ?", 3),
                Question(
                    30,
                    "Dans le jeu \"Super Mario Bros.\", quel est le principal objectif du joueur ?",
                    3
                ),

                // Catégorie Culture Générale
                Question(31, "Quelle est la capitale de l'Australie ?", 4),
                Question(32, "Qui a écrit la pièce \"Roméo et Juliette\" ?", 4),
                Question(33, "Quelle est la plus grande planète du système solaire ?", 4),
                Question(34, "Quel est le symbole chimique de l'eau ?", 4),
                Question(35, "Qui a peint la \"Mona Lisa\" ?", 4),
                Question(36, "Quelle est la langue officielle du Brésil ?", 4),
                Question(37, "Quel est le plus long fleuve du monde ?", 4),
                Question(38, "Qui a découvert la théorie de la relativité ?", 4),
                Question(39, "Quelle est la monnaie utilisée au Japon ?", 4),
                Question(40, "Quelle est la plus haute montagne du monde ?", 4)
            )
            questions.forEach { question ->
                database.questionDao().insert(question)
            }
        }

        if (database.answersDao().getNumberAnswers() == 0) {
            val answers = listOf(
                // Réponses pour les questions de la catégorie Cinéma
                Answers(1, 1, "Steven Spielberg", false),
                Answers(2, 1, "James Cameron", false),
                Answers(3, 1, "Christopher Nolan", true),
                Answers(4, 1, "Quentin Tarantino", false),

                Answers(5, 2, "Leonardo DiCaprio", false),
                Answers(6, 2, "Johnny Depp", true),
                Answers(7, 2, "Brad Pitt", false),
                Answers(8, 2, "Tom Cruise", false),

                Answers(9, 3, "Pulp Fiction", false),
                Answers(10, 3, "The Shawshank Redemption", false),
                Answers(11, 3, "Braveheart", false),
                Answers(12, 3, "Forrest Gump", true),

                Answers(13, 4, "Scarface", false),
                Answers(14, 4, "Le Parrain", true),
                Answers(15, 4, "Les Incorruptibles", false),
                Answers(16, 4, "Heat", false),

                Answers(17, 5, "Alfred Hitchcock", true),
                Answers(18, 5, "Stanley Kubrick", false),
                Answers(19, 5, "Martin Scorsese", false),
                Answers(20, 5, "Francis Ford Coppola", false),

                Answers(21, 6, "Le Roi Lion", true),
                Answers(22, 6, "Bambi", false),
                Answers(23, 6, "Aladdin", false),
                Answers(24, 6, "Tarzan", false),

                Answers(25, 7, "Rain Man", false),
                Answers(26, 7, "Big", false),
                Answers(27, 7, "Apollo 13", false),
                Answers(28, 7, "Forrest Gump", true),

                Answers(29, 8, "Blade Runner", false),
                Answers(30, 8, "Total Recall", false),
                Answers(31, 8, "The Matrix", true),
                Answers(32, 8, "Terminator 2", false),

                Answers(33, 9, "L'Empire contre-attaque", false),
                Answers(34, 9, "Le Retour du Jedi", false),
                Answers(35, 9, "Un nouvel espoir", true),
                Answers(36, 9, "La Menace fantôme", false),

                Answers(37, 10, "Mel Gibson", false),
                Answers(38, 10, "Tom Hanks", false),
                Answers(39, 10, "Russell Crowe", true),
                Answers(40, 10, "Brad Pitt", false),

                // Réponses pour les questions de la catégorie Littérature
                Answers(41, 11, "Émile Zola", false),
                Answers(42, 11, "Victor Hugo", true),
                Answers(43, 11, "Gustave Flaubert", false),
                Answers(44, 11, "Alexandre Dumas", false),

                Answers(45, 12, "Harry Potter et la Chambre des secrets", false),
                Answers(46, 12, "Harry Potter à l'école des sorciers", true),
                Answers(47, 12, "Harry Potter et le Prisonnier d'Azkaban", false),
                Answers(48, 12, "Harry Potter et la Coupe de feu", false),

                Answers(49, 13, "Aldous Huxley", false),
                Answers(50, 13, "Ray Bradbury", false),
                Answers(51, 13, "George Orwell", true),
                Answers(52, 13, "Arthur C. Clarke", false),

                Answers(53, 14, "Guerre et Paix", false),
                Answers(54, 14, "Les Frères Karamazov", false),
                Answers(55, 14, "Crime et Châtiment", false),
                Answers(56, 14, "Anna Karenine", true),

                Answers(57, 15, "Agatha Christie", true),
                Answers(58, 15, "Arthur Conan Doyle", false),
                Answers(59, 15, "Raymond Chandler", false),
                Answers(60, 15, "Dashiell Hammett", false),

                Answers(61, 16, "The Hobbit", false),
                Answers(62, 16, "The Lord of the Rings", true),
                Answers(63, 16, "The Silmarillion", false),
                Answers(64, 16, "Unfinished Tales", false),

                Answers(65, 17, "Mark Twain", false),
                Answers(66, 17, "Nathaniel Hawthorne", false),
                Answers(67, 17, "Herman Melville", true),
                Answers(68, 17, "Henry James", false),

                Answers(69, 18, "Animal Farm", true),
                Answers(70, 18, "Brave New World", false),
                Answers(71, 18, "Fahrenheit 451", false),
                Answers(72, 18, "We", false),

                Answers(73, 19, "Charlotte Brontë", false),
                Answers(74, 19, "Jane Austen", true),
                Answers(75, 19, "Emily Brontë", false),
                Answers(76, 19, "Mary Shelley", false),

                Answers(77, 20, "Love in the Time of Cholera", false),
                Answers(78, 20, "The Autumn of the Patriarch", false),
                Answers(79, 20, "One Hundred Years of Solitude", true),
                Answers(80, 20, "Chronicle of a Death Foretold", false),

                // Réponses pour les questions de la catégorie Jeux Vidéo
                Answers(81, 21, "Zelda", false),
                Answers(82, 21, "Link", true),
                Answers(83, 21, "Ganon", false),
                Answers(84, 21, "Navi", false),

                Answers(85, 22, "Digimon", false),
                Answers(86, 22, "Yu-Gi-Oh!", false),
                Answers(87, 22, "Pokémon", true),
                Answers(88, 22, "Monster Rancher", false),

                Answers(89, 23, "Fortnite", false),
                Answers(90, 23, "Call of Duty: Warzone", false),
                Answers(91, 23, "PlayerUnknown's Battlegrounds (PUBG)", true),
                Answers(92, 23, "Apex Legends", false),

                Answers(93, 24, "Bethesda Game Studios", false),
                Answers(94, 24, "CD Projekt Red", true),
                Answers(95, 24, "Bioware", false),
                Answers(96, 24, "Ubisoft", false),

                Answers(97, 25, "Doom", false),
                Answers(98, 25, "Quake", false),
                Answers(99, 25, "System Shock", false),
                Answers(100, 25, "Half-Life", true),

                Answers(101, 26, "Masamune", false),
                Answers(102, 26, "Ragnarok", false),
                Answers(103, 26, "Buster Sword", true),
                Answers(104, 26, "Excalibur", false),

                Answers(105, 27, "Valve", false),
                Answers(106, 27, "EA", false),
                Answers(107, 27, "Mojang", true),
                Answers(108, 27, "Bungie", false),

                Answers(109, 28, "Jack", false),
                Answers(110, 28, "David", true),
                Answers(111, 28, "John", false),
                Answers(112, 28, "Sam", false),

                Answers(113, 29, "Tetris", false),
                Answers(114, 29, "Grand Theft Auto V", false),
                Answers(115, 29, "Minecraft", true),
                Answers(116, 29, "Wii Sports", false),

                Answers(117, 30, "Ramasser des pièces", false),
                Answers(118, 30, "Sauver la princesse Peach", true),
                Answers(119, 30, "Battre Bowser", false),
                Answers(120, 30, "Atteindre le plus haut score", false),

                // Réponses pour les questions de la catégorie Culture Générale
                Answers(121, 31, "Sydney", false),
                Answers(122, 31, "Melbourne", false),
                Answers(123, 31, "Canberra", true),
                Answers(124, 31, "Brisbane", false),

                Answers(125, 32, "Molière", false),
                Answers(126, 32, "Victor Hugo", false),
                Answers(127, 32, "William Shakespeare", true),
                Answers(128, 32, "Anton Tchekhov", false),

                Answers(129, 33, "Saturne", false),
                Answers(130, 33, "Jupiter", true),
                Answers(131, 33, "Uranus", false),
                Answers(132, 33, "Neptune", false),

                Answers(133, 34, "H2O", true),
                Answers(134, 34, "O2", false),
                Answers(135, 34, "CO2", false),
                Answers(136, 34, "N2", false),

                Answers(137, 35, "Michel-Ange", false),
                Answers(138, 35, "Raphaël", false),
                Answers(139, 35, "Leonard de Vinci", true),
                Answers(140, 35, "Donatello", false),

                Answers(141, 36, "Espagnol", false),
                Answers(142, 36, "Portugais", true),
                Answers(143, 36, "Français", false),
                Answers(144, 36, "Anglais", false),

                Answers(145, 37, "Amazon", false),
                Answers(146, 37, "Nil", true),
                Answers(147, 37, "Yangtsé", false),
                Answers(148, 37, "Mississippi", false),

                Answers(149, 38, "Albert Einstein", true),
                Answers(150, 38, "Isaac Newton", false),
                Answers(151, 38, "Galilée", false),
                Answers(152, 38, "Nikola Tesla", false),

                Answers(153, 39, "Dollar", false),
                Answers(154, 39, "Won", false),
                Answers(155, 39, "Euro", false),
                Answers(156, 39, "Yen", true),

                Answers(157, 40, "K2", false),
                Answers(158, 40, "Kangchenjunga", false),
                Answers(159, 40, "Lhotse", false),
                Answers(160, 40, "Everest", true)
            )
            answers.forEach { answer ->
                database.answersDao().insert(answer)
            }
        }
    }
}
