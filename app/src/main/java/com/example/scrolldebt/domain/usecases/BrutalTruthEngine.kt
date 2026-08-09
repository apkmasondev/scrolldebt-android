package com.example.scrolldebt.domain.usecases

import com.example.scrolldebt.utils.AppUsageInfo
import com.example.scrolldebt.utils.LocaleUtils
import com.example.scrolldebt.utils.TimeFormatUtils
import android.content.Context

data class TruthMessage(
    val text: String
)

class BrutalTruthEngine(private val context: Context) {

    /**
     * Quotes already shown, so the engine exhausts a pool before repeating itself.
     *
     * Guarded because this is a Hilt singleton reached concurrently from the ViewModel
     * (Dispatchers.IO), DoomTrackerService's polling loop and ThresholdWorker's worker
     * thread; an unsynchronised LinkedHashSet was one interleaving away from
     * ConcurrentModificationException.
     *
     * Deliberately in-memory only: it resets when the process dies, which is acceptable
     * for a "don't repeat yourself too soon" heuristic and avoids persisting several
     * hundred strings to disk on every roast.
     */
    private val seenQuotes = mutableSetOf<String>()
    private val seenQuotesLock = Any()

    // POLISH ROASTS
    private val generalShortTruthsPl = listOf(
        TruthMessage("Twoje palce zrobiły dziś więcej cardio niż ty sam."),
        TruthMessage("Mógłbyś w tym czasie przeczytać 20 stron książki."),
        TruthMessage("To wystarczająco dużo czasu, by ugotować porządny obiad."),
        TruthMessage("Właśnie oddałeś korporacjom reklamowym cząstkę swojego życia."),
        TruthMessage("Czy Twoje cele życiowe również czekają, aż skończysz scrollować?"),
        TruthMessage("Twoja uwaga ma wartość. Szkoda, że oddajesz ją za darmo."),
        TruthMessage("Dopamina z ekranu nie zastąpi prawdziwych osiągnięć."),
        TruthMessage("Gdybyś dostawał złotówkę za każdą minutę, miałbyś na dobrą kawę."),
        TruthMessage("Twoja produktywność właśnie popełniła seppuku."),
        TruthMessage("Krótkie formy wideo niszczą twoją pamięć. Widzisz to, prawda?"),
        TruthMessage("Właśnie przewinąłeś kolejny bezcenny moment swojego życia."),
        TruthMessage("Ktoś właśnie zarobił na tym, że Ty straciłeś kilka minut."),
        TruthMessage("Znowu uciekasz od rzeczywistości. Przed czym się chowasz?"),
        TruthMessage("Każde przesunięcie kciuka to krok dalej od twoich celów."),
        TruthMessage("Twoja uwaga znika szybciej niż te filmiki.")
    )

    private val generalMediumTruthsPl = listOf(
        TruthMessage("To 10% Twojego całego dnia. Zastanów się nad tym."),
        TruthMessage("Mógłbyś w tym czasie odbyć solidny trening na siłowni."),
        TruthMessage("Mógłbyś przespać pełny, zdrowy cykl snu (90 minut)."),
        TruthMessage("W tym czasie nauczyłbyś się podstawowych 15 słówek w nowym języku."),
        TruthMessage("Obejrzałeś dziś więcej shortów niż wypowiedziałeś słów do ludzi."),
        TruthMessage("Twoja zdolność skupienia właśnie drasticznie spadła. Czujesz to?"),
        TruthMessage("Darmowe aplikacje płacą Twoim czasem. Jesteś bardzo tanim towarem."),
        TruthMessage("Ten czas już nigdy, przenigdy nie wróci. Przepadł."),
        TruthMessage("Gratulacje, pomyślnie zignorowałeś dziś swoje prawdziwe życie."),
        TruthMessage("Zamiast budować swoją przyszłość, sponsorujesz nowy jacht deweloperom aplikacji."),
        TruthMessage("Twoi znajomi prawdopodobnie zrobili dziś coś ciekawego. Ty oglądałeś obcych w internecie."),
        TruthMessage("Właśnie sfinansowałeś wakacje jakiemuś influencerowi."),
        TruthMessage("Godzina przewijania... Nawet nie pamiętasz, co widziałeś 10 minut temu."),
        TruthMessage("Stajesz się ekspertem od bezużytecznych informacji."),
        TruthMessage("Każdy Twój wybór ma znaczenie. Dziś wybrałeś bycie cyfrowym zombie.")
    )

    private val generalLongTruthsPl = listOf(
        TruthMessage("To ponad 3 godziny. Prawie 2 pełne filmy kinowe. Warto było?"),
        TruthMessage("Przez rok w tym tempie spędzisz 45 PEŁNYCH DNI patrząc w ekran."),
        TruthMessage("Twój mózg wysyła sygnały ratunkowe. Odłóż ten telefon i wyjdź na spacer."),
        TruthMessage("Gdybyś poświęcił ten czas na naukę nowej umiejętności, byłbyś już ekspertem."),
        TruthMessage("Przewijanie ekranu to cyfrowy smoczek. Czas dorosnąć."),
        TruthMessage("Czy w wieku 80 lat będziesz wspominać ten dzień jako wspaniałą sesję scrollowania?"),
        TruthMessage("Twoje plecy i szyja właśnie błagają o fizjoterapię. Wyprostuj się!"),
        TruthMessage("Twoje oczy są czerwone, kciuk zmęczony, a mózg pusty. Idealny konsument."),
        TruthMessage("Zostaniesz zapamiętany jako człowiek, który perfekcyjnie opanował ruch kciuka z dołu na górę."),
        TruthMessage("Straciłeś dzisiaj ułamek swojego jedynego, niepowtarzalnego życia na oglądanie głupot."),
        TruthMessage("Pomyśl, ile mógłbyś zarobić, gdybyś poświęcił ten czas na pracę dodatkową."),
        TruthMessage("Twoja przyszłość właśnie dzwoniła, żeby powiedzieć, że jesteś spłukany."),
        TruthMessage("Większość ludzi sukcesu nie wie, co to doomscrolling. Zgadnij dlaczego."),
        TruthMessage("Zastanów się: co osiągnąłeś przez te ostatnie godziny? Kompletnie nic."),
        TruthMessage("Siedzisz przed ekranem tak długo, że zaczynasz zapominać, jak wygląda słońce.")
    )

    // ENGLISH ROASTS
    private val generalShortTruthsEn = listOf(
        TruthMessage("Your fingers did more cardio today than you did."),
        TruthMessage("You could have read 20 pages of a book in this time."),
        TruthMessage("That is enough time to cook a proper dinner."),
        TruthMessage("You just gave ad corporations a piece of your life."),
        TruthMessage("Are your life goals also waiting for you to finish scrolling?"),
        TruthMessage("Your attention has value. Too bad you give it away for free."),
        TruthMessage("Dopamine from a screen won't replace real achievements."),
        TruthMessage("If you got a dollar for every minute, you'd have enough for a nice coffee."),
        TruthMessage("Your productivity just committed seppuku."),
        TruthMessage("Short-form videos are destroying your memory. You can feel it, right?"),
        TruthMessage("You just scrolled past another priceless moment of your life."),
        TruthMessage("Someone just made money because you wasted a few minutes."),
        TruthMessage("Escaping reality again. What are you hiding from?"),
        TruthMessage("Every thumb swipe is a step away from your goals."),
        TruthMessage("Your attention span is disappearing faster than these videos.")
    )

    private val generalMediumTruthsEn = listOf(
        TruthMessage("That's 10% of your entire day. Think about it."),
        TruthMessage("You could have done a solid workout in this time."),
        TruthMessage("You could have slept a full, healthy sleep cycle (90 minutes)."),
        TruthMessage("In this time, you could have learned 15 basic words in a new language."),
        TruthMessage("You watched more shorts today than you spoke words to people."),
        TruthMessage("Your attention span just dropped drastically. Can you feel it?"),
        TruthMessage("Free apps pay with your time. You are a very cheap product."),
        TruthMessage("This time will never, ever come back. It's gone."),
        TruthMessage("Congratulations, you successfully ignored your real life today."),
        TruthMessage("Instead of building your future, you are funding an app developer's new yacht."),
        TruthMessage("Your friends probably did something interesting today. You watched strangers online."),
        TruthMessage("You just funded an influencer's vacation."),
        TruthMessage("An hour of scrolling... You don't even remember what you saw 10 minutes ago."),
        TruthMessage("You are becoming an expert in useless information."),
        TruthMessage("Every choice matters. Today you chose to be a digital zombie.")
    )

    private val generalLongTruthsEn = listOf(
        TruthMessage("That's over 3 hours. Almost 2 full movies. Was it worth it?"),
        TruthMessage("At this rate, you will spend 45 FULL DAYS looking at a screen in a year."),
        TruthMessage("Your brain is sending distress signals. Put down the phone and go for a walk."),
        TruthMessage("If you spent this time learning a new skill, you'd be an expert by now."),
        TruthMessage("Scrolling is a digital pacifier. Time to grow up."),
        TruthMessage("At age 80, will you remember today as a wonderful scrolling session?"),
        TruthMessage("Your back and neck are begging for physical therapy. Straighten up!"),
        TruthMessage("Your eyes are red, thumb is tired, and brain is empty. The perfect consumer."),
        TruthMessage("You will be remembered as the person who perfectly mastered the upward thumb swipe."),
        TruthMessage("You wasted a fraction of your only, unique life watching nonsense today."),
        TruthMessage("Think how much you could earn if you spent this time on a side hustle."),
        TruthMessage("Your future just called to say you're broke."),
        TruthMessage("Most successful people don't know what doomscrolling is. Guess why."),
        TruthMessage("Think about it: what did you achieve over these last few hours? Absolutely nothing."),
        TruthMessage("You've been in front of the screen so long you're forgetting what the sun looks like.")
    )

    // SPANISH ROASTS
    private val generalShortTruthsEs = listOf(
        TruthMessage("Tus dedos hicieron más cardio hoy que tú mismo."),
        TruthMessage("Podrías haber leído 20 páginas de un libro en este tiempo."),
        TruthMessage("Ese es tiempo suficiente para cocinar una buena cena."),
        TruthMessage("Le acabas de regalar a las corporaciones publicitarias un trozo de tu vida."),
        TruthMessage("¿Tus metas en la vida también están esperando a que termines de scrollear?"),
        TruthMessage("Tu atención tiene valor. Qué pena que la regales gratis."),
        TruthMessage("La dopamina de una pantalla no reemplazará los logros reales."),
        TruthMessage("Si te dieran un dólar por cada minuto, tendrías para un buen café."),
        TruthMessage("Tu productividad acaba de cometer seppuku."),
        TruthMessage("Los videos cortos están destruyendo tu memoria. Lo notas, ¿verdad?"),
        TruthMessage("Acabas de scrollear otro momento invaluable de tu vida."),
        TruthMessage("Alguien acaba de ganar dinero porque perdiste unos minutos."),
        TruthMessage("Escapando de la realidad de nuevo. ¿De qué te escondes?"),
        TruthMessage("Cada vez que deslizas el pulgar es un paso lejos de tus metas."),
        TruthMessage("Tu capacidad de atención desaparece más rápido que estos videos.")
    )

    private val generalMediumTruthsEs = listOf(
        TruthMessage("Eso es el 10% de todo tu día. Piénsalo."),
        TruthMessage("Podrías haber hecho un buen entrenamiento en este tiempo."),
        TruthMessage("Podrías haber dormido un ciclo de sueño completo y saludable (90 minutos)."),
        TruthMessage("En este tiempo podrías haber aprendido 15 palabras básicas de un nuevo idioma."),
        TruthMessage("Hoy viste más shorts de los que hablaste con personas."),
        TruthMessage("Tu capacidad de atención acaba de caer drásticamente. ¿Lo sientes?"),
        TruthMessage("Las apps gratuitas se pagan con tu tiempo. Eres un producto muy barato."),
        TruthMessage("Este tiempo nunca, jamás volverá. Se ha ido."),
        TruthMessage("Felicidades, ignoraste con éxito tu vida real hoy."),
        TruthMessage("En lugar de construir tu futuro, estás financiando el nuevo yate de un desarrollador."),
        TruthMessage("Tus amigos probablemente hicieron algo interesante hoy. Tú viste a extraños en internet."),
        TruthMessage("Acabas de financiar las vacaciones de un influencer."),
        TruthMessage("Una hora scrolleando... Ni siquiera recuerdas lo que viste hace 10 minutos."),
        TruthMessage("Te estás convirtiendo en un experto en información inútil."),
        TruthMessage("Cada elección importa. Hoy elegiste ser un zombi digital.")
    )

    private val generalLongTruthsEs = listOf(
        TruthMessage("Son más de 3 horas. Casi 2 películas completas. ¿Valió la pena?"),
        TruthMessage("A este ritmo, pasarás 45 DÍAS COMPLETOS mirando una pantalla en un año."),
        TruthMessage("Tu cerebro está enviando señales de auxilio. Deja el teléfono y sal a caminar."),
        TruthMessage("Si hubieras dedicado este tiempo a aprender algo nuevo, ya serías un experto."),
        TruthMessage("El scroll es un chupete digital. Es hora de madurar."),
        TruthMessage("A los 80 años, ¿recordarás hoy como una maravillosa sesión de scroll?"),
        TruthMessage("Tu espalda y tu cuello suplican fisioterapia. ¡Ponte derecho!"),
        TruthMessage("Tus ojos están rojos, tu pulgar cansado y tu cerebro vacío. El consumidor perfecto."),
        TruthMessage("Serás recordado como la persona que dominó perfectamente el deslizamiento del pulgar."),
        TruthMessage("Hoy desperdiciaste una fracción de tu única e irrepetible vida viendo tonterías."),
        TruthMessage("Piensa cuánto podrías ganar si dedicaras este tiempo a un trabajo extra."),
        TruthMessage("Tu futuro acaba de llamar para decir que estás en bancarrota."),
        TruthMessage("La mayoría de las personas exitosas no saben qué es el doomscrolling. Adivina por qué."),
        TruthMessage("Piénsalo: ¿qué lograste en estas últimas horas? Absolutamente nada."),
        TruthMessage("Llevas tanto tiempo frente a la pantalla que estás olvidando cómo es el sol.")
    )

    // FRENCH ROASTS
    private val generalShortTruthsFr = listOf(
        TruthMessage("Tes doigts ont fait plus de cardio aujourd'hui que toi-même."),
        TruthMessage("Tu aurais pu lire 20 pages d'un livre pendant ce temps."),
        TruthMessage("C'est assez de temps pour cuisiner un vrai dîner."),
        TruthMessage("Tu viens de donner une partie de ta vie aux agences de pub."),
        TruthMessage("Est-ce que tes objectifs de vie attendent aussi que tu finisses de scroller ?"),
        TruthMessage("Ton attention a de la valeur. Dommage que tu la donnes gratuitement."),
        TruthMessage("La dopamine d'un écran ne remplacera pas les vrais accomplissements."),
        TruthMessage("Si on te donnait un euro pour chaque minute, tu pourrais te payer un bon café."),
        TruthMessage("Ta productivité vient de commettre un seppuku."),
        TruthMessage("Les vidéos courtes détruisent ta mémoire. Tu le sens, n'est-ce pas ?"),
        TruthMessage("Tu viens de scroller un autre moment inestimable de ta vie."),
        TruthMessage("Quelqu'un vient de gagner de l'argent parce que tu as perdu quelques minutes."),
        TruthMessage("Tu fuis encore la réalité. De quoi te caches-tu ?"),
        TruthMessage("Chaque glissement de pouce t'éloigne un peu plus de tes objectifs."),
        TruthMessage("Ta capacité d'attention disparaît plus vite que ces vidéos.")
    )

    private val generalMediumTruthsFr = listOf(
        TruthMessage("C'est 10% de toute ta journée. Penses-y."),
        TruthMessage("Tu aurais pu faire un bon entraînement de sport pendant ce temps."),
        TruthMessage("Tu aurais pu dormir un cycle de sommeil complet et sain (90 minutes)."),
        TruthMessage("Pendant ce temps, tu aurais pu apprendre 15 mots de base dans une nouvelle langue."),
        TruthMessage("Tu as regardé plus de shorts aujourd'hui que tu n'as dit de mots aux gens."),
        TruthMessage("Ta capacité d'attention vient de chuter drastiquement. Tu le sens ?"),
        TruthMessage("Les applis gratuites se paient avec ton temps. Tu es un produit très bon marché."),
        TruthMessage("Ce temps ne reviendra jamais, au grand jamais. Il est perdu."),
        TruthMessage("Félicitations, tu as réussi à ignorer ta vraie vie aujourd'hui."),
        TruthMessage("Au lieu de construire ton avenir, tu finances le nouveau yacht d'un développeur."),
        TruthMessage("Tes amis ont probablement fait quelque chose d'intéressant aujourd'hui. Toi, tu as regardé des inconnus sur Internet."),
        TruthMessage("Tu viens de financer les vacances d'un influenceur."),
        TruthMessage("Une heure de scroll... Tu ne te souviens même pas de ce que tu as vu il y a 10 minutes."),
        TruthMessage("Tu deviens un expert en informations inutiles."),
        TruthMessage("Chaque choix compte. Aujourd'hui, tu as choisi d'être un zombie numérique.")
    )

    private val generalLongTruthsFr = listOf(
        TruthMessage("Ça fait plus de 3 heures. Presque 2 films entiers. Ça en valait la peine ?"),
        TruthMessage("À ce rythme, tu passeras 45 JOURS COMPLETS à regarder un écran en un an."),
        TruthMessage("Ton cerveau envoie des signaux de détresse. Pose ce téléphone et va te promener."),
        TruthMessage("Si tu avais consacré ce temps à apprendre une nouvelle compétence, tu serais déjà un expert."),
        TruthMessage("Le scroll est une tétine numérique. Il est temps de grandir."),
        TruthMessage("À 80 ans, te souviendras-tu de cette journée comme d'une merveilleuse session de scroll ?"),
        TruthMessage("Ton dos et ton cou supplient pour de la kinésithérapie. Tiens-toi droit !"),
        TruthMessage("Tes yeux sont rouges, ton pouce est fatigué et ton cerveau est vide. Le consommateur parfait."),
        TruthMessage("On se souviendra de toi comme de la personne qui maîtrisait parfaitement le glissement du pouce vers le haut."),
        TruthMessage("Tu as gaspillé aujourd'hui une fraction de ta seule et unique vie à regarder des bêtises."),
        TruthMessage("Pense à combien tu pourrais gagner si tu consacrais ce temps à un travail d'appoint."),
        TruthMessage("Ton avenir vient d'appeler pour dire que tu es fauché."),
        TruthMessage("La plupart des gens qui réussissent ne savent pas ce qu'est le doomscrolling. Devine pourquoi."),
        TruthMessage("Réfléchis : qu'as-tu accompli ces dernières heures ? Absolument rien."),
        TruthMessage("Tu es resté si longtemps devant l'écran que tu commences à oublier à quoi ressemble le soleil.")
    )

    // GERMAN ROASTS
    private val generalShortTruthsDe = listOf(
        TruthMessage("Deine Finger haben heute mehr Cardio gemacht als du."),
        TruthMessage("Du hättest in dieser Zeit 20 Seiten eines Buches lesen können."),
        TruthMessage("Das ist genug Zeit, um ein ordentliches Abendessen zu kochen."),
        TruthMessage("Du hast Werbeunternehmen gerade ein Stück deines Lebens geschenkt."),
        TruthMessage("Warten deine Lebensziele auch darauf, dass du mit dem Scrollen fertig bist?"),
        TruthMessage("Deine Aufmerksamkeit hat Wert. Schade, dass du sie kostenlos weggibst."),
        TruthMessage("Dopamin von einem Bildschirm wird echte Erfolge nicht ersetzen."),
        TruthMessage("Wenn du für jede Minute einen Euro bekommst, hättest du genug für einen guten Kaffee."),
        TruthMessage("Deine Produktivität hat gerade Seppuku begangen."),
        TruthMessage("Kurzvideos zerstören dein Gedächtnis. Du spürst es, oder?"),
        TruthMessage("Du hast gerade an einem weiteren unbezahlbaren Moment deines Lebens vorbeigescrollt."),
        TruthMessage("Jemand hat gerade Geld verdient, weil du ein paar Minuten verschwendet hast."),
        TruthMessage("Schon wieder Flucht vor der Realität. Wovor versteckst du dich?"),
        TruthMessage("Jeder Wisch mit dem Daumen ist ein Schritt weg von deinen Zielen."),
        TruthMessage("Deine Aufmerksamkeitsspanne verschwindet schneller als diese Videos.")
    )

    private val generalMediumTruthsDe = listOf(
        TruthMessage("Das sind 10% deines gesamten Tages. Denk mal darüber nach."),
        TruthMessage("Du hättest in dieser Zeit ein solides Workout machen können."),
        TruthMessage("Du hättest einen vollen, gesunden Schlafzyklus (90 Minuten) schlafen können."),
        TruthMessage("In dieser Zeit hättest du 15 Grundwörter in einer neuen Sprache lernen können."),
        TruthMessage("Du hast heute mehr Shorts gesehen, als du mit echten Menschen geredet hast."),
        TruthMessage("Deine Aufmerksamkeitsspanne ist gerade drastisch gesunken. Spürst du das?"),
        TruthMessage("Kostenlose Apps werden mit deiner Zeit bezahlt. Du bist ein sehr billiges Produkt."),
        TruthMessage("Diese Zeit wird nie wieder zurückkommen. Sie ist weg."),
        TruthMessage("Herzlichen Glückwunsch, du hast dein echtes Leben heute erfolgreich ignoriert."),
        TruthMessage("Anstatt deine Zukunft aufzubauen, finanzierst du die neue Yacht eines App-Entwicklers."),
        TruthMessage("Deine Freunde haben heute wahrscheinlich etwas Interessantes gemacht. Du hast Fremden im Internet zugeschaut."),
        TruthMessage("Du hast gerade den Urlaub eines Influencers finanziert."),
        TruthMessage("Eine Stunde Scrollen... Du weißt nicht einmal mehr, was du vor 10 Minuten gesehen hast."),
        TruthMessage("Du wirst zu einem Experten für nutzlose Informationen."),
        TruthMessage("Jede Entscheidung zählt. Heute hast du dich entschieden, ein digitaler Zombie zu sein.")
    )

    private val generalLongTruthsDe = listOf(
        TruthMessage("Das sind über 3 Stunden. Fast 2 ganze Filme. War es das wert?"),
        TruthMessage("Bei diesem Tempo verbringst du 45 VOLLE TAGE im Jahr damit, auf einen Bildschirm zu schauen."),
        TruthMessage("Dein Gehirn sendet Notsignale. Leg das Telefon weg und geh spazieren."),
        TruthMessage("Hättest du diese Zeit damit verbracht, eine neue Fähigkeit zu lernen, wärst du jetzt ein Experte."),
        TruthMessage("Scrollen ist ein digitaler Schnuller. Zeit erwachsen zu werden."),
        TruthMessage("Wirst du dich mit 80 Jahren an heute als wunderbare Scroll-Session erinnern?"),
        TruthMessage("Dein Rücken und dein Nacken flehen um Physiotherapie. Richte dich auf!"),
        TruthMessage("Deine Augen sind rot, der Daumen ist müde und das Gehirn ist leer. Der perfekte Konsument."),
        TruthMessage("Du wirst als die Person in Erinnerung bleiben, die das Daumenwischen nach oben perfekt beherrscht hat."),
        TruthMessage("Du hast heute einen Bruchteil deines einzigen, einzigartigen Lebens damit verschwendet, Unsinn zu sehen."),
        TruthMessage("Denk mal darüber nach, wie viel du verdienen könntest, wenn du diese Zeit für einen Nebenjob nutzen würdest."),
        TruthMessage("Deine Zukunft hat gerade angerufen, um dir zu sagen, dass du pleite bist."),
        TruthMessage("Die meisten erfolgreichen Menschen wissen nicht, was Doomscrolling ist. Rate mal warum."),
        TruthMessage("Überleg mal: Was hast du in den letzten Stunden erreicht? Absolut gar nichts."),
        TruthMessage("Du warst so lange vor dem Bildschirm, dass du vergisst, wie die Sonne aussieht.")
    )

    private val appSpecificTruthsPl = mapOf(
        "com.zhiliaoapp.musically" to listOf(
            TruthMessage("TikTok rozpuścił dziś Twój mózg na 15-sekundowe kawałki."),
            TruthMessage("Obejrzałeś dziś dziesiątki ludzi robiących z siebie głupka. A Ty co zrobiłeś?"),
            TruthMessage("Algorytm TikToka zna Twoje słabości lepiej niż Ty sam. I właśnie na nich zarabia."),
            TruthMessage("15 sekund, 15 sekund, 15 sekund... Gratulacje, Twoje skupienie przestało istnieć."),
            TruthMessage("Przewijasz algorytm zaprojektowany przez geniuszy, by okraść cię z czasu.")
        ),
        "com.instagram.android" to listOf(
            TruthMessage("Oglądasz idealne, wyreżyserowane życie innych, podczas gdy Twoje własne ucieka."),
            TruthMessage("Instagram żywi się Twoją zazdrością i niepewnością. Odłóż to."),
            TruthMessage("Serduszka pod zdjęciami nie zapłacą Twoich rachunków ani nie zbudują relacji."),
            TruthMessage("Kolejne story o niczym. Twoja ciekawość została nakarmiona cyfrowym fast foodem."),
            TruthMessage("Zamiast kreować własne wspomnienia, karmisz się cudzymi.")
        ),
        "com.google.android.youtube" to listOf(
            TruthMessage("Kolejny filmik z serii 'zaraz wyłączę'. Wiemy obaj, że to kłamstwo."),
            TruthMessage("YouTube wciągnął Cię w spiralę algorytmu. Jak głęboko już jesteś?"),
            TruthMessage("Obejrzałeś dziś więcej reklam niż minut wartościowej treści."),
            TruthMessage("Miniaturki i clickbaity wygrały dzisiejszą batalię z Twoją silną wolą."),
            TruthMessage("Próbujesz znaleźć edukacyjny filmik, a kończysz na kompilacji memów.")
        ),
        "com.facebook.katana" to listOf(
            TruthMessage("Facebook w 2026 roku? Naprawdę nie masz nic lepszego do roboty?"),
            TruthMessage("Kłótnie w komentarzach pod postami obcych ludzi na pewno zmienią świat."),
            TruthMessage("Karmisz się dramami ludzi, z którymi nawet nie utrzymujesz kontaktu.")
        ),
        "com.twitter.android" to listOf(
            TruthMessage("Przeczytałeś dziś wystarczająco dużo toksycznych opinii na X, by zepsuć sobie humor."),
            TruthMessage("Doomscroll na X to najkrótsza droga do cyfrowej depresji."),
            TruthMessage("Uzależniłeś się od gniewu i oburzenia. Gratulacje.")
        ),
        "com.reddit.frontpage" to listOf(
            TruthMessage("Reddit dał Ci dziś złudne poczucie wiedzy i dyskusji. Czas wyjść do ludzi."),
            TruthMessage("Przewijanie wątków r/all nie naprawi Twojego prawdziwego życia."),
            TruthMessage("Kolejna anegdota, której nikt nie potrzebował i nikt nie poprosił.")
        ),
        "com.snapchat.android" to listOf(
            TruthMessage("Utrzymywanie 'streaków' na Snapie to Twoje największe dzisiejsze osiągnięcie?"),
            TruthMessage("Zdjęcia, które znikają po 10 sekundach. Zupełnie jak czas, który na nie straciłeś."),
            TruthMessage("Sztuczne filtry nie zakryją tego, jak marnujesz swój potencjał.")
        )
    )

    private val appSpecificTruthsEn = mapOf(
        "com.zhiliaoapp.musically" to listOf(
            TruthMessage("TikTok melted your brain into 15-second pieces today."),
            TruthMessage("You watched dozens of people making fools of themselves today. What did you do?"),
            TruthMessage("The TikTok algorithm knows your weaknesses better than you do. And it's cashing in on them."),
            TruthMessage("15 seconds, 15 seconds, 15 seconds... Congrats, your attention span is gone."),
            TruthMessage("You're scrolling an algorithm designed by geniuses to rob you of your time.")
        ),
        "com.instagram.android" to listOf(
            TruthMessage("You watch the perfect, staged lives of others while your own slips away."),
            TruthMessage("Instagram feeds on your envy and insecurity. Put it down."),
            TruthMessage("Hearts under photos won't pay your bills or build relationships."),
            TruthMessage("Another story about nothing. Your curiosity was fed with digital fast food."),
            TruthMessage("Instead of creating your own memories, you feed on others'.")
        ),
        "com.google.android.youtube" to listOf(
            TruthMessage("Another video from the 'I'll turn it off in a second' series. We both know that's a lie."),
            TruthMessage("YouTube dragged you into an algorithmic spiral. How deep are you now?"),
            TruthMessage("You watched more ads today than minutes of actual valuable content."),
            TruthMessage("Thumbnails and clickbait won today's battle against your willpower."),
            TruthMessage("You try to find an educational video and end up on a meme compilation.")
        ),
        "com.facebook.katana" to listOf(
            TruthMessage("Facebook in 2026? Do you really have nothing better to do?"),
            TruthMessage("Arguing in the comments under posts of strangers will definitely change the world."),
            TruthMessage("You feed on the dramas of people you don't even keep in touch with.")
        ),
        "com.twitter.android" to listOf(
            TruthMessage("You read enough toxic opinions on X today to completely ruin your mood."),
            TruthMessage("Doomscrolling on X is the shortest path to digital depression."),
            TruthMessage("You've become addicted to anger and outrage. Congratulations.")
        ),
        "com.reddit.frontpage" to listOf(
            TruthMessage("Reddit gave you a false sense of knowledge and discussion today. Time to go out."),
            TruthMessage("Scrolling r/all threads won't fix your real life."),
            TruthMessage("Another anecdote nobody needed and nobody asked for.")
        ),
        "com.snapchat.android" to listOf(
            TruthMessage("Keeping streaks on Snap is your biggest achievement today?"),
            TruthMessage("Photos that disappear after 10 seconds. Just like the time you wasted on them."),
            TruthMessage("Artificial filters won't cover up how you're wasting your potential.")
        )
    )

    private val appSpecificTruthsEs = mapOf(
        "com.zhiliaoapp.musically" to listOf(
            TruthMessage("TikTok derritió tu cerebro en pedazos de 15 segundos hoy."),
            TruthMessage("Hoy viste a decenas de personas hacer el ridículo. ¿Tú qué hiciste?"),
            TruthMessage("El algoritmo de TikTok conoce tus debilidades mejor que tú. Y saca provecho de ellas."),
            TruthMessage("15 segundos, 15 segundos, 15 segundos... Felicidades, tu capacidad de atención ya no existe."),
            TruthMessage("Estás scrolleando un algoritmo diseñado por genios para robarte tu tiempo.")
        ),
        "com.instagram.android" to listOf(
            TruthMessage("Miras las vidas perfectas y escenificadas de otros mientras la tuya se escapa."),
            TruthMessage("Instagram se alimenta de tu envidia e inseguridad. Déjalo ya."),
            TruthMessage("Los corazones bajo las fotos no pagarán tus facturas ni construirán relaciones."),
            TruthMessage("Otra historia sobre nada. Tu curiosidad fue alimentada con comida rápida digital."),
            TruthMessage("En lugar de crear tus propios recuerdos, te alimentas de los ajenos.")
        ),
        "com.google.android.youtube" to listOf(
            TruthMessage("Otro video de la serie 'lo apago en un segundo'. Ambos sabemos que es mentira."),
            TruthMessage("YouTube te arrastró a una espiral algorítmica. ¿Qué tan profundo estás ahora?"),
            TruthMessage("Hoy viste más anuncios que minutos de contenido valioso real."),
            TruthMessage("Las miniaturas y el clickbait ganaron hoy la batalla contra tu fuerza de voluntad."),
            TruthMessage("Intentas encontrar un video educativo y terminas en una recopilación de memes.")
        ),
        "com.facebook.katana" to listOf(
            TruthMessage("¿Facebook en 2026? ¿De verdad no tienes nada mejor que hacer?"),
            TruthMessage("Discutir en los comentarios bajo publicaciones de extraños definitivamente cambiará el mundo."),
            TruthMessage("Te alimentas de los dramas de personas con las que ni siquiera mantienes contacto.")
        ),
        "com.twitter.android" to listOf(
            TruthMessage("Hoy leíste suficientes opiniones tóxicas en X como para arruinar tu estado de ánimo por completo."),
            TruthMessage("El Doomscrolling en X es el camino más corto hacia la depresión digital."),
            TruthMessage("Te has vuelto adicto a la ira y la indignación. Felicidades.")
        ),
        "com.reddit.frontpage" to listOf(
            TruthMessage("Reddit te dio un falso sentido de conocimiento y debate hoy. Es hora de salir."),
            TruthMessage("Desplazarse por los hilos de r/all no arreglará tu vida real."),
            TruthMessage("Otra anécdota que nadie necesitaba y nadie pidió.")
        ),
        "com.snapchat.android" to listOf(
            TruthMessage("¿Mantener rachas en Snap es tu mayor logro de hoy?"),
            TruthMessage("Fotos que desaparecen después de 10 segundos. Igual que el tiempo que perdiste en ellas."),
            TruthMessage("Los filtros artificiales no ocultarán cómo estás desperdiciando tu potencial.")
        )
    )

    private val appSpecificTruthsFr = mapOf(
        "com.zhiliaoapp.musically" to listOf(
            TruthMessage("TikTok a fait fondre ton cerveau en morceaux de 15 secondes aujourd'hui."),
            TruthMessage("Tu as regardé des dizaines de personnes se ridiculiser aujourd'hui. Et toi, qu'as-tu fait ?"),
            TruthMessage("L'algorithme de TikTok connaît tes faiblesses mieux que toi. Et il en profite."),
            TruthMessage("15 secondes, 15 secondes, 15 secondes... Bravo, ton attention n'existe plus."),
            TruthMessage("Tu scrolles un algorithme conçu par des génies pour te voler ton temps.")
        ),
        "com.instagram.android" to listOf(
            TruthMessage("Tu regardes la vie parfaite et mise en scène des autres pendant que la tienne t'échappe."),
            TruthMessage("Instagram se nourrit de ton envie et de ton insécurité. Pose-le."),
            TruthMessage("Les cœurs sous les photos ne paieront pas tes factures ni ne construiront tes relations."),
            TruthMessage("Encore une story sur rien. Ta curiosité a été nourrie au fast-food numérique."),
            TruthMessage("Au lieu de créer tes propres souvenirs, tu te nourris de ceux des autres.")
        ),
        "com.google.android.youtube" to listOf(
            TruthMessage("Encore une vidéo de la série 'j'éteins dans une seconde'. On sait tous les deux que c'est faux."),
            TruthMessage("YouTube t'a entraîné dans une spirale algorithmique. À quelle profondeur es-tu maintenant ?"),
            TruthMessage("Tu as regardé plus de publicités aujourd'hui que de minutes de vrai contenu de valeur."),
            TruthMessage("Les miniatures et le putaclic ont gagné la bataille d'aujourd'hui contre ta volonté."),
            TruthMessage("Tu essaies de trouver une vidéo éducative et tu finis sur une compilation de mèmes.")
        ),
        "com.facebook.katana" to listOf(
            TruthMessage("Facebook en 2026 ? Tu n'as vraiment rien de mieux à faire ?"),
            TruthMessage("Se disputer dans les commentaires sous les posts d'inconnus va certainement changer le monde."),
            TruthMessage("Tu te nourris des drames de personnes avec qui tu n'es même plus en contact.")
        ),
        "com.twitter.android" to listOf(
            TruthMessage("Tu as lu assez d'opinions toxiques sur X aujourd'hui pour ruiner complètement ton humeur."),
            TruthMessage("Le doomscrolling sur X est le chemin le plus court vers la dépression numérique."),
            TruthMessage("Tu es devenu accro à la colère et à l'indignation. Félicitations.")
        ),
        "com.reddit.frontpage" to listOf(
            TruthMessage("Reddit t'a donné une fausse impression de connaissances et de débats aujourd'hui. Il est temps de sortir."),
            TruthMessage("Scroller les fils r/all ne réparera pas ta vraie vie."),
            TruthMessage("Encore une anecdote dont personne n'avait besoin et que personne n'a demandée.")
        ),
        "com.snapchat.android" to listOf(
            TruthMessage("Maintenir des flammes sur Snap est ton plus grand accomplissement aujourd'hui ?"),
            TruthMessage("Des photos qui disparaissent après 10 secondes. Tout comme le temps que tu y as perdu."),
            TruthMessage("Les filtres artificiels ne cacheront pas à quel point tu gâches ton potentiel.")
        )
    )

    private val appSpecificTruthsDe = mapOf(
        "com.zhiliaoapp.musically" to listOf(
            TruthMessage("TikTok hat dein Gehirn heute in 15-Sekunden-Stücke geschmolzen."),
            TruthMessage("Du hast heute Dutzenden von Leuten zugesehen, wie sie sich zum Narren machen. Was hast du gemacht?"),
            TruthMessage("Der TikTok-Algorithmus kennt deine Schwächen besser als du selbst. Und er profitiert davon."),
            TruthMessage("15 Sekunden, 15 Sekunden, 15 Sekunden... Glückwunsch, deine Aufmerksamkeitsspanne ist weg."),
            TruthMessage("Du scrollst durch einen Algorithmus, der von Genies entwickelt wurde, um dich deiner Zeit zu berauben.")
        ),
        "com.instagram.android" to listOf(
            TruthMessage("Du schaust dir das perfekte, inszenierte Leben anderer an, während dein eigenes entgleitet."),
            TruthMessage("Instagram ernährt sich von deinem Neid und deiner Unsicherheit. Leg es weg."),
            TruthMessage("Herzen unter Fotos werden deine Rechnungen nicht bezahlen oder Beziehungen aufbauen."),
            TruthMessage("Noch eine Story über nichts. Deine Neugier wurde mit digitalem Fast Food gefüttert."),
            TruthMessage("Anstatt eigene Erinnerungen zu schaffen, ernährst du dich von denen anderer.")
        ),
        "com.google.android.youtube" to listOf(
            TruthMessage("Noch ein Video aus der Serie 'Ich schalte gleich aus'. Wir wissen beide, dass das eine Lüge ist."),
            TruthMessage("YouTube hat dich in eine algorithmische Spirale gezogen. Wie tief bist du schon?"),
            TruthMessage("Du hast heute mehr Werbung gesehen als Minuten mit wertvollen Inhalten."),
            TruthMessage("Thumbnails und Clickbait haben die heutige Schlacht gegen deine Willenskraft gewonnen."),
            TruthMessage("Du versuchst, ein lehrreiches Video zu finden, und landest bei einer Meme-Kompilation.")
        ),
        "com.facebook.katana" to listOf(
            TruthMessage("Facebook im Jahr 2026? Hast du wirklich nichts Besseres zu tun?"),
            TruthMessage("Streiten in den Kommentaren unter Beiträgen von Fremden wird die Welt bestimmt verändern."),
            TruthMessage("Du ernährst dich von den Dramen von Menschen, mit denen du nicht einmal mehr in Kontakt stehst.")
        ),
        "com.twitter.android" to listOf(
            TruthMessage("Du hast heute genug giftige Meinungen auf X gelesen, um dir die Laune komplett zu verderben."),
            TruthMessage("Doomscrolling auf X ist der kürzeste Weg zur digitalen Depression."),
            TruthMessage("Du bist süchtig nach Wut und Empörung geworden. Herzlichen Glückwunsch.")
        ),
        "com.reddit.frontpage" to listOf(
            TruthMessage("Reddit hat dir heute ein falsches Gefühl von Wissen und Diskussion gegeben. Zeit, rauszugehen."),
            TruthMessage("Das Scrollen durch r/all wird dein echtes Leben nicht reparieren."),
            TruthMessage("Noch eine Anekdote, die niemand brauchte und nach der niemand gefragt hat.")
        ),
        "com.snapchat.android" to listOf(
            TruthMessage("Das Aufrechterhalten von 'Streaks' auf Snap ist heute deine größte Leistung?"),
            TruthMessage("Fotos, die nach 10 Sekunden verschwinden. Genau wie die Zeit, die du damit verschwendet hast."),
            TruthMessage("Künstliche Filter werden nicht verbergen, wie du dein Potenzial verschwendest.")
        )
    )


    fun getRoastMessage(totalTimeMs: Long, breakdown: List<AppUsageInfo>, language: String): String {
        val totalMinutes = totalTimeMs / 1000 / 60
        val lang = language.lowercase()

        val candidateList = mutableListOf<String>()

        // 1. App-specific roast if there is a dominant app with high usage
        val dominantApp = breakdown.firstOrNull { it.timeSpentMs > 10 * 60 * 1000 } // > 10 mins
        if (dominantApp != null && Math.random() < 0.4) {
            val appList = when (lang) {
                "en" -> appSpecificTruthsEn[dominantApp.packageName]
                "es" -> appSpecificTruthsEs[dominantApp.packageName]
                "fr" -> appSpecificTruthsFr[dominantApp.packageName]
                "de" -> appSpecificTruthsDe[dominantApp.packageName]
                else -> appSpecificTruthsPl[dominantApp.packageName]
            }
            if (!appList.isNullOrEmpty()) {
                candidateList.addAll(appList.map { it.text })
            }
        }

        // If we didn't pick app-specific, or we did but we want a fallback
        if (candidateList.isEmpty()) {
            val pool = when {
                totalMinutes < 30 -> when (lang) {
                    "en" -> generalShortTruthsEn
                    "es" -> generalShortTruthsEs
                    "fr" -> generalShortTruthsFr
                    "de" -> generalShortTruthsDe
                    else -> generalShortTruthsPl
                }
                totalMinutes < 120 -> when (lang) {
                    "en" -> generalMediumTruthsEn
                    "es" -> generalMediumTruthsEs
                    "fr" -> generalMediumTruthsFr
                    "de" -> generalMediumTruthsDe
                    else -> generalMediumTruthsPl
                }
                else -> when (lang) {
                    "en" -> generalLongTruthsEn
                    "es" -> generalLongTruthsEs
                    "fr" -> generalLongTruthsFr
                    "de" -> generalLongTruthsDe
                    else -> generalLongTruthsPl
                }
            }
            candidateList.addAll(pool.map { it.text })
        }

        if (candidateList.isEmpty()) {
            return fallbackMessage(lang)
        }

        synchronized(seenQuotesLock) {
            // Show every quote in the pool before any of them comes round again.
            var choices = candidateList.filter { !seenQuotes.contains(it) }

            // Pool exhausted: forget only this pool, so other pools keep their history.
            if (choices.isEmpty()) {
                candidateList.forEach { seenQuotes.remove(it) }
                choices = candidateList
            }

            val chosen = choices.random()
            seenQuotes.add(chosen)
            return chosen
        }
    }

    private fun fallbackMessage(lang: String): String {
        return when (lang) {
            "en" -> "Your doomscrolling counter is ticking. Time to start living in the real world."
            "es" -> "Tu contador de doomscrolling está corriendo. Es hora de empezar a vivir en el mundo real."
            "fr" -> "Ton compteur de doomscrolling tourne. Il est temps de commencer à vivre dans le monde réel."
            "de" -> "Dein Doomscrolling-Zähler tickt. Zeit, im echten Leben anzukommen."
            else -> "Twój licznik doomscrollingu bije. Czas zacząć żyć w realnym świecie."
        }
    }

    fun getWeeklyRoast(weekHours: Double, language: String): String {
        val totalMinutes = (weekHours * 60).toLong()
        // The engine holds the application context, which tracks the *system* locale.
        // Format the duration against the in-app language so "2h 15m" matches the roast around it.
        val formattedTime = TimeFormatUtils.formatSmartTime(
            LocaleUtils.withAppLocale(context, language),
            totalMinutes
        )
        val lang = language.lowercase()

        val plLevel1 = listOf(
            "W tym tygodniu oddałem %s na doomscrolling. Mógłbym w tym czasie przeczytać świetną książkę. Zobaczmy czy przebijesz mój wynik!",
            "Straciłem %s życia w tym tygodniu na bezmyślne przewijanie. To mój najsłabszy wynik od lat, spróbuj mnie pokonać.",
            "Tylko %s zmarnowane na doomscrolling w tym tygodniu. Chyba powoli odzyskuję kontrolę nad życiem, ale i tak jestem od Was gorszy.",
            "Mój wynik to zaledwie %s w social mediach. Amator. Muszę poćwiczyć machanie kciukiem, żeby Wam dorównać.",
            "%s stracone w tydzień na przewijanie apek. Mogłem pójść na krótki kurs, ale memy wciągnęły mnie bardziej. Przebij to."
        )
        val plLevel2 = listOf(
            "W tym tygodniu oddałem %s na doomscrolling. Mógłbym w tym czasie obejrzeć całą trylogię Władcy Pierścieni w wersji reżyserskiej.",
            "Moje kciuki wykręciły w tym tygodniu %s na Instagramie i TikToku. Ktoś zapłaci mi za to ubezpieczenie zdrowotne?",
            "%s stracone bezpowrotnie w internecie. Mogłem iść na 10 treningów, ale wolałem oglądać obce życia na ekranie.",
            "%s z głowy przez scrollowanie. Przespałem ten czas z otwartymi oczami. Może Ty masz gorszy wynik?",
            "Zamiast posprzątać mieszkanie, scrolowałem przez %s. Wynik wstydu do pobicia."
        )
        val plLevel3 = listOf(
            "W tym tygodniu oddałem %s na doomscrolling. To prawie praca na pełen etat. Potrzebuję cyfrowego odwyku.",
            "%s w kosz. Zamiast budować swoją przyszłość, oglądałem cudzą przez ekran telefonu. Kto mnie przebije?",
            "Scrollowałem przez %s w tym tygodniu. To już nie jest nawyk, to mój styl życia. Pomocy.",
            "Ponad doba mojego tygodnia wyparowała. Równe %s gapienia się w social media. Kto jest gorszy?",
            "%s gapienia się w ścianę ze śmiesznymi filmikami. Moja uwaga oficjalnie nie istnieje. Ktoś rzuci wyzwanie?"
        )
        val plLevel4 = listOf(
            "W tym tygodniu straciłem %s na doomscrolling. Oficjalnie straciłem kontrolę nad własnym życiem. Wyślijcie pomoc.",
            "Złoty medal w marnowaniu życia na social mediach! %s z głowy. Słońce? A co to takiego? Spróbujcie pobić ten wynik, jeśli macie odwagę.",
            "%s przed ekranem w 7 dni. Kiedyś byłem człowiekiem, teraz jestem po prostu baterią dla algorytmów.",
            "Dwa pełne dni z tego tygodnia po prostu usunąłem na scrollowanie. %s. Mój terapeuta płacze.",
            "%s doomscrollingu. Tak, dobrze widzisz. Nie mam życia, nie mam znajomych, mam tylko algorytm. Czy ktoś ma gorzej?"
        )
        val plLevel5 = listOf(
            "Wykręciłem %s w social mediach. Mój mózg jest gładki jak lustro. To nie jest gra, to wołanie o pomoc.",
            "%s stracone w tydzień na scrollowaniu. W tym czasie mógłbym na piechotę przejść do sąsiedniego województwa. Jesteście w stanie to pobić?",
            "Absolutny rekord patologii. %s doomscrollingu w jednym tygodniu. Oddział zamknięty dzwoni po moje kciuki.",
            "Dzień i noc przed ekranem. %s na social mediach. Jestem żywym dowodem na ewolucję wsteczną ludzkości.",
            "%s w ciągu tygodnia. Korporacje zarobiły na mnie tyle, że powinienem dostać akcje TikToka."
        )

        val enLevel1 = listOf(
            "This week I wasted %s doomscrolling. I could have read a decent book instead. Try to beat my score!",
            "I lost %s of my life this week mindless scrolling. That's my weakest score in years, try to beat me.",
            "Only %s wasted on social media this week. I think I'm slowly regaining control of my life, but I'm still worse than you.",
            "My score is merely %s on social media. Amateur. I need to practice my thumb swipes to match you guys.",
            "%s lost to doomscrolling in a week. I could have taken a short course, but memes pulled me in. Beat that."
        )
        val enLevel2 = listOf(
            "This week I wasted %s doomscrolling. I could have watched the entire Lord of the Rings trilogy instead.",
            "My thumbs cranked out %s on social media this week. Who's paying my medical bills?",
            "%s lost to the internet forever. I could have gone to the gym 10 times, but I preferred watching cats online.",
            "%s gone to doomscrolling. I slept through this time with my eyes open. Maybe you have a worse score?",
            "Instead of cleaning my apartment, I scrolled for %s. A score of shame to beat."
        )
        val enLevel3 = listOf(
            "This week I wasted %s doomscrolling. That's almost a full-time job. I need an intervention.",
            "%s down the drain watching a screen. Instead of building my future, I watched someone else's. Who can beat this?",
            "I scrolled for %s this week. It's not a habit anymore, it's a lifestyle. Help.",
            "Over a day of my week evaporated. Exactly %s staring at pixels on social media. Who is worse?",
            "%s staring at a wall of funny videos. My attention span officially doesn't exist. Anyone challenge me?"
        )
        val enLevel4 = listOf(
            "This week I wasted %s doomscrolling. I've officially lost control of my life. Send help.",
            "Gold medal in wasting life on social media! %s gone. The sun? What's that? Try to beat this score if you dare.",
            "%s in front of a screen in 7 days. I used to be human, now I'm just a battery for algorithms.",
            "I just deleted two full days from this week doomscrolling. %s. My therapist is crying.",
            "%s scrolling. Yes, you see correctly. I have no life, no friends, I only have the algorithm. Does anyone have it worse?"
        )
        val enLevel5 = listOf(
            "I clocked %s on social media. My brain is as smooth as a mirror. This is not a game, it's a cry for help.",
            "%s scrolling in a week. In this time I could have walked to the next state. Can you beat this?",
            "Absolute record of pathology. %s of doomscrolling in a single week. The psych ward is calling for my thumbs.",
            "Day and night in front of the screen. %s of doomscrolling. I am living proof of human devolution.",
            "%s scrolling in a week. Corporations made so much money off me I should get TikTok stock."
        )

        val esLevel1 = listOf(
            "Esta semana perdí %s haciendo doomscrolling. Podría haber leído un buen libro en su lugar. ¡Intenta superar mi puntuación!",
            "Perdí %s de mi vida esta semana scrolleando sin pensar. Es mi puntaje más bajo en años, intenta superarme.",
            "Solo %s perdidas en redes sociales esta semana. Creo que estoy recuperando el control de mi vida, pero sigo siendo peor que tú.",
            "Mi puntuación es de apenas %s scrolleando. Aficionado. Necesito practicar mis movimientos de pulgar para igualarlos.",
            "%s perdidas haciendo doomscrolling en una semana. Podría haber tomado un curso corto, pero los memes me atraparon. Supera eso."
        )
        val esLevel2 = listOf(
            "Esta semana perdí %s haciendo doomscrolling. Podría haber visto toda la trilogía de El Señor de los Anillos.",
            "Mis pulgares trabajaron %s en redes sociales esta semana. ¿Quién pagará mi seguro médico?",
            "%s perdidas en internet para siempre. Podría haber ido al gimnasio 10 veces, pero preferí ver gatos en internet.",
            "%s perdidas scrolleando. Dormí durante este tiempo con los ojos abiertos. ¿Tal vez tú tienes un puntaje peor?",
            "En lugar de limpiar mi departamento, scrolleé por %s. Un puntaje de vergüenza para superar."
        )
        val esLevel3 = listOf(
            "Esta semana perdí %s haciendo doomscrolling. Es casi un trabajo de tiempo completo. Necesito una intervención.",
            "%s a la basura mirando una pantalla. En lugar de construir mi futuro, vi el de alguien más a través de una pantalla. ¿Quién me supera?",
            "Scrolleé por %s esta semana. Ya no es un hábito, es un estilo de vida. Ayuda.",
            "Más de un día de mi semana se evaporó. Exactamente %s mirando píxeles en redes sociales. ¿Quién es peor?",
            "%s mirando una pared de videos divertidos. Mi capacidad de atención ya no existe. ¿Alguien me desafía?"
        )
        val esLevel4 = listOf(
            "Esta semana perdí %s haciendo doomscrolling. Oficialmente he perdido el control de mi vida. Envíen ayuda.",
            "¡Medalla de oro en desperdiciar la vida en redes sociales! %s perdidas. ¿El sol? ¿Qué es eso? Intenta superar este puntaje si te atreves.",
            "%s frente a una pantalla en 7 días. Solía ser humano, ahora solo soy una batería para algoritmos.",
            "Acabo de eliminar dos días completos de esta semana scrolleando. %s. Mi terapeuta está llorando.",
            "%s de doomscrolling. Sí, ves correctamente. No tengo vida, no tengo amigos, solo tengo el algoritmo. ¿Alguien lo tiene peor?"
        )
        val esLevel5 = listOf(
            "Marqué %s en redes sociales. Mi cerebro es tan liso como un espejo. Esto no es un juego, es un grito de auxilio.",
            "%s scrolleando en una semana. En este tiempo podría haber caminado hasta la siguiente ciudad. ¿Puedes superar esto?",
            "Récord absoluto de patología. %s de doomscrolling en una sola semana. El manicomio llama por mis pulgares.",
            "Día y noche frente a la pantalla. %s haciendo scroll. Soy la prueba viviente de la involución humana.",
            "%s scrolleando en una semana. Las corporaciones ganaron tanto dinero conmigo que debería recibir acciones de TikTok."
        )

        val frLevel1 = listOf(
            "Cette semaine, j'ai perdu %s à faire du doomscrolling. J'aurais pu lire un bon livre à la place. Essaie de battre mon score !",
            "J'ai perdu %s de ma vie cette semaine à scroller sans réfléchir. C'est mon pire score depuis des années, essaie de me battre.",
            "Seulement %s perdues sur les réseaux sociaux cette semaine. Je crois que je reprends doucement le contrôle de ma vie, mais je suis toujours pire que toi.",
            "Mon score n'est que de %s sur les réseaux sociaux. Amateur. Je dois m'entraîner à balayer du pouce pour t'égaler.",
            "%s perdues en doomscrolling en une semaine. J'aurais pu suivre une petite formation, mais les mèmes m'ont happé. Bats ça."
        )
        val frLevel2 = listOf(
            "Cette semaine, j'ai perdu %s à faire du doomscrolling. J'aurais pu regarder toute la trilogie du Seigneur des Anneaux à la place.",
            "Mes pouces ont tourné pendant %s sur les réseaux sociaux cette semaine. Qui va payer mes frais médicaux ?",
            "%s perdues à jamais sur Internet. J'aurais pu aller à la salle de sport 10 fois, mais j'ai préféré regarder des vidéos en ligne.",
            "%s disparues à cause du doomscrolling. J'ai dormi pendant tout ce temps les yeux ouverts. Peut-être que tu as un pire score ?",
            "Au lieu de nettoyer mon appartement, j'ai scrollé pendant %s. Un score de honte à battre."
        )
        val frLevel3 = listOf(
            "Cette semaine, j'ai perdu %s à faire du doomscrolling. C'est presque un travail à temps plein. J'ai besoin d'une cure de désintox.",
            "%s à la poubelle en regardant un écran. Au lieu de construire mon avenir, j'ai regardé celui de quelqu'un d'autre. Qui peut battre ça ?",
            "J'ai scrollé pendant %s cette semaine. Ce n'est plus une habitude, c'est un mode de vie. À l'aide.",
            "Plus d'une journée de ma semaine s'est évaporée. Exactement %s à fixer des pixels sur les réseaux sociaux. Qui dit pire ?",
            "%s à fixer un mur de vidéos drôles. Ma capacité d'attention n'existe officiellement plus. Quelqu'un veut me défier ?"
        )
        val frLevel4 = listOf(
            "Cette semaine, j'ai perdu %s à faire du doomscrolling. J'ai officiellement perdu le contrôle de ma vie. Envoyez de l'aide.",
            "Médaille d'or de la perte de temps sur les réseaux sociaux ! %s envolées. Le soleil ? C'est quoi ça ? Essaie de battre ce score si tu l'oses.",
            "%s devant un écran en 7 jours. Avant, j'étais humain, maintenant je suis juste une batterie pour algorithmes.",
            "Je viens d'effacer deux jours complets de cette semaine à scroller. %s. Mon psy est en larmes.",
            "%s de scroll. Oui, tu as bien lu. Je n'ai pas de vie, pas d'amis, je n'ai que l'algorithme. Quelqu'un a pire ?"
        )
        val frLevel5 = listOf(
            "J'ai fait %s sur les réseaux sociaux. Mon cerveau est aussi lisse qu'un miroir. Ce n'est pas un jeu, c'est un appel à l'aide.",
            "%s de scroll en une semaine. Pendant ce temps, j'aurais pu traverser la région à pied. Tu peux battre ça ?",
            "Record absolu de pathologie. %s de doomscrolling en une seule semaine. L'hôpital psychiatrique réclame mes pouces.",
            "Jour et nuit devant l'écran. %s de doomscrolling. Je suis la preuve vivante de la dévolution humaine.",
            "%s de scroll en une semaine. Les entreprises ont tellement gagné d'argent grâce à moi que je devrais recevoir des actions TikTok."
        )

        val deLevel1 = listOf(
            "Diese Woche habe ich %s beim Doomscrolling verloren. Ich hätte stattdessen ein gutes Buch lesen können. Versuch mal, meinen Rekord zu brechen!",
            "Ich habe diese Woche %s meines Lebens beim sinnlosen Scrollen verloren. Das ist mein schwächster Wert seit Jahren, versuch mich zu schlagen.",
            "Nur %s auf Social Media diese Woche verschwendet. Ich glaube, ich bekomme langsam die Kontrolle über mein Leben zurück, bin aber immer noch schlechter als ihr.",
            "Mein Wert liegt bei mageren %s auf Social Media. Anfänger. Ich muss mein Daumenwischen üben, um mit euch mitzuhalten.",
            "%s durch Doomscrolling in einer Woche verloren. Ich hätte einen kurzen Kurs machen können, aber Memes haben mich mehr gefesselt. Überbiete das."
        )
        val deLevel2 = listOf(
            "Diese Woche habe ich %s beim Doomscrolling verloren. Ich hätte stattdessen die gesamte Herr der Ringe-Trilogie ansehen können.",
            "Meine Daumen haben diese Woche %s auf Social Media geleistet. Wer bezahlt meine Arztrechnungen?",
            "%s unwiederbringlich im Internet verloren. Ich hätte 10 Mal ins Fitnessstudio gehen können, aber ich habe lieber Katzen online zugesehen.",
            "%s fürs Scrollen draufgegangen. Ich habe diese Zeit mit offenen Augen verschlafen. Hast du vielleicht einen schlechteren Wert?",
            "Anstatt meine Wohnung aufzuräumen, habe ich %s gescrollt. Ein peinlicher Wert, den es zu überbieten gilt."
        )
        val deLevel3 = listOf(
            "Diese Woche habe ich %s beim Doomscrolling verloren. Das ist fast ein Vollzeitjob. Ich brauche einen digitalen Entzug.",
            "%s in den Müll geworfen. Anstatt meine Zukunft aufzubauen, habe ich die von jemand anderem auf einem Bildschirm beobachtet. Wer kann mich schlagen?",
            "Ich habe diese Woche für %s gescrollt. Das ist keine Gewohnheit mehr, das ist mein Lebensstil. Hilfe.",
            "Mehr als ein Tag meiner Woche ist verdampft. Genau %s habe ich auf Social Media gestarrt. Wer ist schlimmer?",
            "%s lang auf eine Wand voller lustiger Videos gestarrt. Meine Aufmerksamkeitsspanne existiert offiziell nicht mehr. Irgendjemand, der mich herausfordert?"
        )
        val deLevel4 = listOf(
            "Diese Woche habe ich %s beim Doomscrolling verloren. Ich habe offiziell die Kontrolle über mein Leben verloren. Schickt Hilfe.",
            "Goldmedaille im Zeitverschwenden auf Social Media! %s weg. Die Sonne? Was ist das? Versucht diesen Rekord zu brechen, wenn ihr euch traut.",
            "%s vor einem Bildschirm in 7 Tagen. Früher war ich ein Mensch, jetzt bin ich nur noch eine Batterie für Algorithmen.",
            "Ich habe gerade zwei volle Tage aus dieser Woche gestrichen, nur um zu scrollen. %s. Mein Therapeut weint.",
            "%s Doomscrolling. Ja, du siehst richtig. Ich habe kein Leben, keine Freunde, ich habe nur den Algorithmus. Hat es jemand noch schlimmer erwischt?"
        )
        val deLevel5 = listOf(
            "Ich habe %s auf Social Media verbracht. Mein Gehirn ist so glatt wie ein Spiegel. Das ist kein Spiel, das ist ein Hilferuf.",
            "%s Scrollen in einer Woche. In dieser Zeit hätte ich zu Fuß ins nächste Bundesland laufen können. Könnt ihr das überbieten?",
            "Absoluter Rekord der Pathologie. %s Doomscrolling in einer einzigen Woche. Die Psychiatrie ruft nach meinen Daumen.",
            "Tag und Nacht vor dem Bildschirm. %s Doomscrolling. Ich bin der lebende Beweis für die Rückentwicklung der Menschheit.",
            "%s Scrollen in einer Woche. Die Konzerne haben so viel Geld an mir verdient, dass ich TikTok-Aktien bekommen sollte."
        )

        val selectedPool = when {
            weekHours < 7 -> when (lang) { "en" -> enLevel1; "es" -> esLevel1; "fr" -> frLevel1; "de" -> deLevel1; else -> plLevel1 }
            weekHours < 15 -> when (lang) { "en" -> enLevel2; "es" -> esLevel2; "fr" -> frLevel2; "de" -> deLevel2; else -> plLevel2 }
            weekHours < 30 -> when (lang) { "en" -> enLevel3; "es" -> esLevel3; "fr" -> frLevel3; "de" -> deLevel3; else -> plLevel3 }
            weekHours < 50 -> when (lang) { "en" -> enLevel4; "es" -> esLevel4; "fr" -> frLevel4; "de" -> deLevel4; else -> plLevel4 }
            else -> when (lang) { "en" -> enLevel5; "es" -> esLevel5; "fr" -> frLevel5; "de" -> deLevel5; else -> plLevel5 }
        }

        return selectedPool.random().replace("%s", formattedTime)
    }
}
