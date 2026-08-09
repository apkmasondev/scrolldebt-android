package com.example.scrolldebt.utils

object Localization {
    private val pl = mapOf(
        "app_name" to "SCROLLDEBT",
        "tab_today" to "DZIŚ",
        "tab_lost" to "STRACONE",
        "tab_settings" to "USTAWIENIA",
        
        // Today Screen
        "today_wasted" to "DZIŚ STRACIŁEŚ",
        "brutal_truth" to "BRUTALNA PRAWDA",
        "refresh" to "[ ODŚWIEŻ ]",
        "thief_apps" to "TOP 5 APLIKACJI ZŁODZIEI",
        "no_data" to "Brak danych o użyciu.\nSkorzystaj z telefonu, aby zobaczyć statystyki.",
        
        // Life Lost Screen
        "life_lost_title" to "ŻYCIE STRACONE",
        "life_lost_subtitle" to "PRZELICZENIE TWOJEGO ZMARNOWANEGO POTENCJAŁU",
        "total_wasted" to "RAZEM ZMARNOWANE",
        "share_intent_text" to "Pobierz ScrollDebt i sprawdź ile czasu tracisz! #ScrollDebt",
        "weekly_chart" to "WYKRES TYGODNIOWY",
        "this_week_format" to "W tym tygodniu: %.1fh",
        "hour_suffix" to "h",
        "could_instead" to "MÓGŁBYŚ ZAMIAST TEGO:",
        "stracone_dni" to "STRACONE DNI",
        "lost_days_desc" to "pełnych 24-godzinnych dób wyjętych z życiorysu",
        "cykle_snu" to "CYKLE SNU",
        "sleep_cycles_desc" to "pełnych regenerujących cykli snu o długości 90 min",
        "przeczytane_ksiazki" to "PRZECZYTANE KSIĄŻKI",
        "books_read_desc" to "przeczytanych 250-stronicowych książek popularnonaukowych",
        "obejrzane_filmy" to "OBEJRZANE FILMY",
        "movies_watched_desc" to "klasyków kina kinowego, których nigdy nie nadrobiłeś",
        "opuszczone_treningi" to "OPUSZCZONE TRENINGI",
        "workouts_missed_desc" to "pełnych sesji na siłowni lub długich wybiegań",
        "przebiegniete_maratony" to "MARATONY",
        "marathons_run_desc" to "razy pokonałbyś dystans królewski w średnim tempie",
        "nowe_umiejetnosci" to "NOWE UMIEJĘTNOŚCI",
        "new_skills_desc" to "opanowanych do poziomu średniozaawansowanego",
        "wasted_money" to "PRZEPALONE PIENIĄDZE",
        "wasted_money_desc" to "tyle zarobiłbyś pracując w tym czasie za najniższą stawkę",
        
        // Settings Screen
        "settings_title" to "USTAWIENIA",
        "tracked_apps" to "ŚLEDZONE APLIKACJE",
        "tracked_apps_desc" to "Wybierz aplikacje do śledzenia czasu.",
        "add_other_app_btn" to "DODAJ INNĄ APLIKACJĘ",
        "select_app_title" to "Wybierz aplikację",
        "streak_label" to "🔥 Streak: %d dni",
        "warning_threshold" to "PRÓG OSTRZEŻENIA (MINUTY)",
        "brutal_truth_label" to "KOMUNIKATY BRUTAL TRUTH",
        "brutal_truth_desc" to "Wyświetlaj brutalne i sarkastyczne komentarze zamiast suchych statystyk.",
        "push_notifications_label" to "LIMIT POWIADOMIENIA",
        "push_notifications_desc" to "Wyślij powiadomienie Push, gdy przekroczysz ustalony próg czasu w śledzonych aplikacjach.",
        "streak_threshold" to "PRÓG DOBREJ PASSY",
        "streak_threshold_desc" to "Złamanie tego limitu przerywa Twoją passę.",
        "tracking_mode_title" to "TRYB MONITOROWANIA",
        "tracking_mode_desc" to "Wybierz precyzję monitorowania czasu w tle.",
        "sniper_mode_title" to "Tryb Snajperski (Na żywo)",
        "sniper_mode_desc" to "Idealna punktualność. Wymaga stałego powiadomienia i zużywa więcej baterii.",
        "battery_saver_title" to "Tryb Oszczędny (Opóźnienia do 30 min)",
        "battery_saver_desc" to "Powiadomienia spóźnią się. Oszczędza baterię i ukrywa górną ikonę.",
        "language_section" to "JĘZYK / LANGUAGE",
        "appearance_title" to "WYGLĄD",
        "theme_light" to "☀️ Jasny",
        "theme_dark" to "🌙 Ciemny",
        
        // Onboarding
        "onboarding_subtitle" to "CYFROWE LUSTRO TWOJEGO CZASU",
        "onboarding_desc1" to "Aplikacja nie blokuje Twoich aplikacji ani nie prawi kazań. Pokazuje tylko nagą prawde o tym, ile życia przecieka Ci przez palce podczas bezmyślnego scrollowania.",
        "onboarding_desc2" to "Aby zacząć odliczać dług, aplikacja wymaga jednego uprawnienia: 'Dostęp do danych użycia'. Wszystko odbywa się w 100% lokalnie i bezpiecznie na Twoim urządzeniu.",
        "grant_access_btn" to "PRZYZNAJ DOSTĘP",
        "check_permission_btn" to "SPRAWDŹ UPRAWNIENIE",
        "prominent_disclosure" to "Ważna informacja (Zasady Google Play):\nScrollDebt zbiera i przetwarza informacje o czasie używania przez Ciebie innych aplikacji (Usage Data), aby móc w czasie rzeczywistym obliczać Twój stracony czas. Aplikacja wykorzystuje również systemowe usługi pierwszoplanowe (Foreground Service), by dostarczać Ci powiadomienia Push po przekroczeniu ustalonego przez Ciebie progu, nawet gdy aplikacja działa w tle.",
        "accept_disclosure" to "Akceptuję zbieranie i przetwarzanie moich danych o użyciu (Usage Data) w wyżej wymienionym celu.",
        
        
        // Brutal truth helper
        "brutal_truth_disabled" to "Komunikaty Brutal Truth zostały wyłączone. Widzisz tylko suche liczby.",
        
        // Widget
        "widget_wasted_time" to "TEGO CZASU JUŻ NIE ODZYSKASZ",
        
        // Time Units
        "time_y_1" to "r",
        "time_y_many" to "l",
        "time_mo" to "msc",
        "time_d" to "d",
        "time_h" to "h",
        "time_m" to "m"
    )

    private val en = mapOf(
        "app_name" to "SCROLLDEBT",
        "tab_today" to "TODAY",
        "tab_lost" to "LOST",
        "tab_settings" to "SETTINGS",
        
        // Today Screen
        "today_wasted" to "TODAY YOU WASTED",
        "brutal_truth" to "BRUTAL TRUTH",
        "refresh" to "[ REFRESH ]",
        "thief_apps" to "TOP 5 TIME THIEVES",
        "no_data" to "No usage data yet.\nUse your phone to see statistics.",
        
        // Life Lost Screen
        "life_lost_title" to "LIFE LOST",
        "life_lost_subtitle" to "CONVERSION OF YOUR WASTED POTENTIAL",
        "total_wasted" to "TOTAL WASTED",
        "share_intent_text" to "Download ScrollDebt and see how much time you are wasting! #ScrollDebt",
        "weekly_chart" to "WEEKLY CHART",
        "this_week_format" to "This week: %.1fh",
        "hour_suffix" to "h",
        "could_instead" to "YOU COULD HAVE INSTEAD:",
        "stracone_dni" to "DAYS WASTED",
        "lost_days_desc" to "full 24-hour days taken out of your life biography",
        "cykle_snu" to "SLEEP CYCLES",
        "sleep_cycles_desc" to "full restorative 90-minute sleep cycles",
        "przeczytane_ksiazki" to "BOOKS READ",
        "books_read_desc" to "read 250-page popular science books",
        "obejrzane_filmy" to "MOVIES WATCHED",
        "movies_watched_desc" to "cinema classics you never made time for",
        "opuszczone_treningi" to "MISSED WORKOUTS",
        "workouts_missed_desc" to "full gym sessions or long runs",
        "przebiegniete_maratony" to "MARATHONS",
        "marathons_run_desc" to "times you could have finished the marathon at average pace",
        "nowe_umiejetnosci" to "NEW SKILLS",
        "new_skills_desc" to "mastered to an intermediate level",
        "wasted_money" to "WASTED INCOME",
        "wasted_money_desc" to "money you could have earned working a minimum wage job",
        
        // Settings Screen
        "settings_title" to "SETTINGS",
        "tracked_apps" to "TRACKED APPLICATIONS",
        "tracked_apps_desc" to "Select apps to monitor their usage time.",
        "add_other_app_btn" to "ADD OTHER APP",
        "select_app_title" to "Select application",
        "streak_label" to "🔥 Streak: %d days",
        "warning_threshold" to "WARNING THRESHOLD (MINUTES)",
        "brutal_truth_label" to "BRUTAL TRUTH MESSAGES",
        "brutal_truth_desc" to "Show brutal and sarcastic comments instead of dry statistics.",
        "push_notifications_label" to "NOTIFICATION LIMIT",
        "push_notifications_desc" to "Send a Push notification when you exceed the time limit in tracked applications.",
        "streak_threshold" to "STREAK THRESHOLD",
        "streak_threshold_desc" to "Limit to keep your daily streak going.",
        "tracking_mode_title" to "TRACKING MODE",
        "tracking_mode_desc" to "Choose how aggressively the app monitors your time.",
        "sniper_mode_title" to "Sniper Mode (Real-time)",
        "sniper_mode_desc" to "Exact accuracy. Requires a permanent sticky notification and consumes more battery.",
        "battery_saver_title" to "Battery Saver (Max 30 min delay)",
        "battery_saver_desc" to "Notifications will arrive late. Saves battery and hides background icon.",
        "language_section" to "LANGUAGE / JĘZYK",
        "appearance_title" to "APPEARANCE",
        "theme_light" to "☀️ Light",
        "theme_dark" to "🌙 Dark",
        
        // Onboarding
        "onboarding_subtitle" to "DIGITAL MIRROR OF YOUR TIME",
        "onboarding_desc1" to "This app does not block your apps or lecture you. It only shows the naked truth about how much of your life slips through your fingers during mindless scrolling.",
        "onboarding_desc2" to "To start counting the debt, the app requires one permission: 'Usage Data Access'. Everything is processed 100% locally and safely on your device.",
        "grant_access_btn" to "GRANT ACCESS",
        "check_permission_btn" to "CHECK PERMISSION",
        "prominent_disclosure" to "Important Disclosure (Google Play Policy):\nScrollDebt collects and processes your app usage time data (Usage Data) to calculate your wasted time in real-time. The app also uses Foreground Services to deliver Push notifications when your configured time limit is exceeded, even when the app is running in the background.",
        "accept_disclosure" to "I accept the collection and processing of my App Usage Data for the purposes mentioned above.",
        
        
        // Brutal truth helper
        "brutal_truth_disabled" to "Brutal Truth messages are disabled. You only see dry numbers.",
        
        // Widget
        "widget_wasted_time" to "YOU'LL NEVER GET THIS TIME BACK",
        
        // Time Units
        "time_y_1" to "y",
        "time_y_many" to "y",
        "time_mo" to "mo",
        "time_d" to "d",
        "time_h" to "h",
        "time_m" to "m"
    )

    private val es = mapOf(
        "app_name" to "SCROLLDEBT",
        "tab_today" to "HOY",
        "tab_lost" to "PERDIDO",
        "tab_settings" to "AJUSTES",
        
        // Today Screen
        "today_wasted" to "HOY PERDISTE",
        "brutal_truth" to "VERDAD BRUTAL",
        "refresh" to "[ ACTUALIZAR ]",
        "thief_apps" to "TOP 5 LADRONES DE TIEMPO",
        "no_data" to "No hay datos de uso.\nUsa tu teléfono para ver estadísticas.",
        
        // Life Lost Screen
        "life_lost_title" to "VIDA PERDIDA",
        "life_lost_subtitle" to "CONVERSIÓN DE TU POTENCIAL DESPERDICIADO",
        "total_wasted" to "TOTAL DESPERDICIADO",
        "share_intent_text" to "¡Descarga ScrollDebt y mira cuánto tiempo estás perdiendo! #ScrollDebt",
        "weekly_chart" to "GRÁFICO SEMANAL",
        "this_week_format" to "Esta semana: %.1fh",
        "hour_suffix" to "h",
        "could_instead" to "PODRÍAS HABERLO USADO PARA:",
        "stracone_dni" to "DÍAS PERDIDOS",
        "lost_days_desc" to "días completos de 24 horas eliminados de tu vida",
        "cykle_snu" to "CICLOS DE SUEÑO",
        "sleep_cycles_desc" to "ciclos completos de sueño reparador de 90 min",
        "przeczytane_ksiazki" to "LIBROS LEÍDOS",
        "books_read_desc" to "libros de divulgación científica de 250 páginas",
        "obejrzane_filmy" to "PELÍCULAS VISTAS",
        "movies_watched_desc" to "clásicos del cine que nunca tuviste tiempo de ver",
        "opuszczone_treningi" to "ENTRENAMIENTOS PERDIDOS",
        "workouts_missed_desc" to "sesiones completas de gimnasio o carreras largas",
        "przebiegniete_maratony" to "MARATONES",
        "marathons_run_desc" to "veces que podrías haber terminado el maratón a un ritmo promedio",
        "nowe_umiejetnosci" to "NUEVAS HABILIDADES",
        "new_skills_desc" to "dominadas hasta un nivel intermedio",
        "wasted_money" to "INGRESOS PERDIDOS",
        "wasted_money_desc" to "dinero que podrías haber ganado con un salario mínimo",
        
        // Settings Screen
        "settings_title" to "AJUSTES",
        "tracked_apps" to "APLICACIONES RASTREADAS",
        "tracked_apps_desc" to "Selecciona aplicaciones para monitorear su tiempo de uso.",
        "add_other_app_btn" to "AÑADIR OTRA APLICACIÓN",
        "select_app_title" to "Seleccionar aplicación",
        "streak_label" to "🔥 Racha: %d días",
        "warning_threshold" to "UMBRAL DE ADVERTENCIA (MINUTOS)",
        "brutal_truth_label" to "MENSAJES DE VERDAD BRUTAL",
        "brutal_truth_desc" to "Muestra comentarios brutales y sarcásticos en lugar de estadísticas secas.",
        "push_notifications_label" to "LÍMITE DE NOTIFICACIÓN",
        "push_notifications_desc" to "Envía una notificación Push cuando superes el límite de tiempo en las aplicaciones rastreadas.",
        "streak_threshold" to "LÍMITE DE RACHA",
        "streak_threshold_desc" to "Límite para mantener tu racha diaria.",
        "tracking_mode_title" to "MODO DE RASTREO",
        "tracking_mode_desc" to "Elige qué tan agresivamente monitorea.",
        "sniper_mode_title" to "Modo Francotirador (Tiempo real)",
        "sniper_mode_desc" to "Precisión exacta. Requiere notificación permanente y gasta más batería.",
        "battery_saver_title" to "Ahorro Batería (Retraso max 30 min)",
        "battery_saver_desc" to "Las notificaciones llegarán tarde. Ahorra batería y oculta el icono.",
        "language_section" to "IDIOMA / LANGUAGE",
        "appearance_title" to "APARIENCIA",
        "theme_light" to "☀️ Claro",
        "theme_dark" to "🌙 Oscuro",
        
        // Onboarding
        "onboarding_subtitle" to "ESPEJO DIGITAL DE TU TIEMPO",
        "onboarding_desc1" to "Esta app no bloquea tus apps ni te da sermones. Solo muestra la cruda verdad de cuánto de tu vida se escurre entre tus dedos desplazando la pantalla.",
        "onboarding_desc2" to "Para empezar a calcular la deuda, la app requiere un permiso: 'Acceso a datos de uso'. Todo se procesa 100% de forma local y segura en tu dispositivo.",
        "grant_access_btn" to "CONCEDER ACCESO",
        "check_permission_btn" to "COMPROBAR PERMISO",
        "prominent_disclosure" to "Aviso importante (Política de Google Play):\nScrollDebt recopila y procesa datos sobre el tiempo de uso de tus aplicaciones (Usage Data) para calcular tu tiempo perdido en tiempo real. La aplicación también utiliza servicios en primer plano (Foreground Service) para enviar notificaciones Push cuando superas el límite de tiempo, incluso en segundo plano.",
        "accept_disclosure" to "Acepto la recopilación y el procesamiento de mis datos de uso (Usage Data) para los fines mencionados.",
        
        
        // Brutal truth helper
        "brutal_truth_disabled" to "Los mensajes de Verdad Brutal están desactivados. Solo ves números secos.",
        
        // Widget
        "widget_wasted_time" to "NUNCA RECUPERARÁS ESTE TIEMPO",
        
        // Time Units
        "time_y_1" to "a",
        "time_y_many" to "a",
        "time_mo" to "m",
        "time_d" to "d",
        "time_h" to "h",
        "time_m" to "m"
    )

    private val fr = mapOf(
        "app_name" to "SCROLLDEBT",
        "tab_today" to "AUJOURD'HUI",
        "tab_lost" to "PERDU",
        "tab_settings" to "PARAMÈTRES",
        
        // Today Screen
        "today_wasted" to "AUJOURD'HUI VOUS AVEZ PERDU",
        "brutal_truth" to "VÉRITÉ BRUTALE",
        "refresh" to "[ ACTUALISER ]",
        "thief_apps" to "TOP 5 VOLEURS DE TEMPS",
        "no_data" to "Aucune donnée d'utilisation.\nUtilisez votre téléphone pour voir les statistiques.",
        
        // Life Lost Screen
        "life_lost_title" to "VIE PERDUE",
        "life_lost_subtitle" to "CONVERSION DE VOTRE POTENTIEL GÂCHÉ",
        "total_wasted" to "TOTAL GÂCHÉ",
        "share_intent_text" to "Téléchargez ScrollDebt et découvrez combien de temps vous perdez ! #ScrollDebt",
        "weekly_chart" to "GRAPHIQUE HEBDOMADAIRE",
        "this_week_format" to "Cette semaine : %.1fh",
        "hour_suffix" to "h",
        "could_instead" to "VOUS AURIEZ PU À LA PLACE :",
        "stracone_dni" to "JOURS PERDUS",
        "lost_days_desc" to "jours complets de 24 heures retirés de votre vie",
        "cykle_snu" to "CYCLES DE SOMMEIL",
        "sleep_cycles_desc" to "cycles complets de sommeil réparateur de 90 min",
        "przeczytane_ksiazki" to "LIVRES LUS",
        "books_read_desc" to "livres de vulgarisation scientifique de 250 pages",
        "obejrzane_filmy" to "FILMS VUS",
        "movies_watched_desc" to "classiques du cinéma que vous n'avez jamais pris le temps de voir",
        "opuszczone_treningi" to "ENTRAÎNEMENTS MANQUÉS",
        "workouts_missed_desc" to "séances de sport complètes ou courses à pied",
        "przebiegniete_maratony" to "MARATHONS",
        "marathons_run_desc" to "fois où vous auriez pu finir un marathon à un rythme moyen",
        "nowe_umiejetnosci" to "NOUVELLES COMPÉTENCES",
        "new_skills_desc" to "maîtrisées à un niveau intermédiaire",
        "wasted_money" to "REVENUS PERDUS",
        "wasted_money_desc" to "l'argent que vous auriez pu gagner au salaire minimum",
        
        // Settings Screen
        "settings_title" to "PARAMÈTRES",
        "tracked_apps" to "APPLICATIONS SUIVIES",
        "tracked_apps_desc" to "Sélectionnez les applications à surveiller.",
        "add_other_app_btn" to "AJOUTER UNE AUTRE APP",
        "select_app_title" to "Sélectionner une application",
        "streak_label" to "🔥 Série : %d jours",
        "warning_threshold" to "SEUIL D'AVERTISSEMENT (MINUTES)",
        "brutal_truth_label" to "MESSAGES DE VÉRITÉ BRUTALE",
        "brutal_truth_desc" to "Affiche des commentaires brutaux et sarcastiques au lieu de statistiques sèches.",
        "push_notifications_label" to "LIMITE DE NOTIFICATION",
        "push_notifications_desc" to "Envoyer une notification Push lorsque vous dépassez la limite de temps dans les applications suivies.",
        "streak_threshold" to "LIMITE DE SÉRIE",
        "streak_threshold_desc" to "Limite pour garder votre série quotidienne.",
        "tracking_mode_title" to "MODE DE SUIVI",
        "tracking_mode_desc" to "Choisissez avec quelle agressivité l'application surveille votre temps.",
        "sniper_mode_title" to "Mode Sniper (Temps réel)",
        "sniper_mode_desc" to "Précision exacte. Nécessite une notification permanente et consomme plus de batterie.",
        "battery_saver_title" to "Économie de batterie (Délai max 30 min)",
        "battery_saver_desc" to "Les notifications arriveront en retard. Économise la batterie et masque l'icône d'arrière-plan.",
        "language_section" to "LANGUE / LANGUAGE",
        "appearance_title" to "APPARENCE",
        "theme_light" to "☀️ Clair",
        "theme_dark" to "🌙 Sombre",
        
        // Onboarding
        "onboarding_subtitle" to "MIROIR NUMÉRIQUE DE VOTRE TEMPS",
        "onboarding_desc1" to "Cette application ne bloque pas vos applications et ne vous fait pas la morale. Elle montre seulement la vérité crue sur la quantité de votre vie qui vous glisse entre les doigts pendant que vous scrollez machinalement.",
        "onboarding_desc2" to "Pour commencer à calculer votre dette, l'application nécessite une autorisation : 'Accès aux données d'utilisation'. Tout est traité à 100 % localement et en toute sécurité sur votre appareil.",
        "grant_access_btn" to "ACCORDER L'ACCÈS",
        "check_permission_btn" to "VÉRIFIER L'AUTORISATION",
        "prominent_disclosure" to "Avis important (Politique Google Play):\nScrollDebt collecte et traite les données relatives au temps d'utilisation de vos applications (Usage Data) pour calculer votre temps perdu en temps réel. L'application utilise également des services au premier plan (Foreground Service) pour envoyer des notifications Push lorsque vous dépassez la limite, même en arrière-plan.",
        "accept_disclosure" to "J'accepte la collecte et le traitement de mes données d'utilisation (Usage Data) aux fins mentionnées ci-dessus.",
        
        
        // Brutal truth helper
        "brutal_truth_disabled" to "Les messages de Vérité Brutale sont désactivés. Vous ne voyez que des chiffres secs.",
        
        // Widget
        "widget_wasted_time" to "VOUS NE RÉCUPÉREREZ JAMAIS CE TEMPS",
        
        // Time Units
        "time_y_1" to "an",
        "time_y_many" to "ans",
        "time_mo" to "m",
        "time_d" to "j",
        "time_h" to "h",
        "time_m" to "m"
    )

    private val de = mapOf(
        "app_name" to "SCROLLDEBT",
        "tab_today" to "HEUTE",
        "tab_lost" to "VERLOREN",
        "tab_settings" to "EINSTELLUNGEN",
        
        // Today Screen
        "today_wasted" to "HEUTE VERLOREN",
        "brutal_truth" to "BRUTALE WAHRHEIT",
        "refresh" to "[ AKTUALISIEREN ]",
        "thief_apps" to "TOP 5 ZEITDIEBE",
        "no_data" to "Noch keine Nutzungsdaten.\nNutze dein Telefon, um Statistiken zu sehen.",
        
        // Life Lost Screen
        "life_lost_title" to "VERLORENES LEBEN",
        "life_lost_subtitle" to "UMRECHNUNG DEINES VERSCHWENDETEN POTENZIALS",
        "total_wasted" to "GESAMT VERLOREN",
        "share_intent_text" to "Lade ScrollDebt herunter und sieh, wie viel Zeit du verschwendest! #ScrollDebt",
        "weekly_chart" to "WOCHENDIAGRAMM",
        "this_week_format" to "Diese Woche: %.1fh",
        "hour_suffix" to "h",
        "could_instead" to "DU HÄTTEST STATTDESSEN TUN KÖNNEN:",
        "stracone_dni" to "VERLORENE TAGE",
        "lost_days_desc" to "volle 24-Stunden-Tage aus deinem Leben gestrichen",
        "cykle_snu" to "SCHLAFZYKLEN",
        "sleep_cycles_desc" to "volle erholsame 90-Minuten-Schlafzyklen",
        "przeczytane_ksiazki" to "GELESENE BÜCHER",
        "books_read_desc" to "gelesene 250-seitige Sachbücher",
        "obejrzane_filmy" to "GESEHENE FILME",
        "movies_watched_desc" to "Kinoklassiker, für die du nie Zeit hattest",
        "opuszczone_treningi" to "VERPASSTE WORKOUTS",
        "workouts_missed_desc" to "komplette Trainingseinheiten im Fitnessstudio",
        "przebiegniete_maratony" to "MARATHONS",
        "marathons_run_desc" to "Mal, die du den Marathon im Durchschnittstempo beendet hättest",
        "nowe_umiejetnosci" to "NEUE FÄHIGKEITEN",
        "new_skills_desc" to "bis zu einem mittleren Niveau gemeistert",
        "wasted_money" to "VERSCHWENDETES EINKOMMEN",
        "wasted_money_desc" to "Geld, das du mit einem Mindestlohnjob verdienen könntest",
        
        // Settings Screen
        "settings_title" to "EINSTELLUNGEN",
        "tracked_apps" to "VERFOLGTE APPS",
        "tracked_apps_desc" to "Wähle Apps aus, deren Nutzungszeit überwacht werden soll.",
        "add_other_app_btn" to "ANDERE APP HINZUFÜGEN",
        "select_app_title" to "App auswählen",
        "streak_label" to "🔥 Streak: %d Tage",
        "warning_threshold" to "WARNUNGSSCHWELLE (MINUTEN)",
        "brutal_truth_label" to "BRUTALE WAHRHEIT NACHRICHTEN",
        "brutal_truth_desc" to "Zeige brutale und sarkastische Kommentare statt trockener Statistiken.",
        "push_notifications_label" to "BENACHRICHTIGUNGS-LIMIT",
        "push_notifications_desc" to "Sende eine Push-Benachrichtigung, wenn du das Zeitlimit überschreitest.",
        "streak_threshold" to "STREAK-SCHWELLE",
        "streak_threshold_desc" to "Limit, um deinen täglichen Streak aufrechtzuerhalten.",
        "tracking_mode_title" to "TRACKING-MODUS",
        "tracking_mode_desc" to "Wähle, wie aggressiv die App deine Zeit überwacht.",
        "sniper_mode_title" to "Sniper-Modus (Echtzeit)",
        "sniper_mode_desc" to "Exakte Genauigkeit. Erfordert eine permanente Benachrichtigung und verbraucht mehr Akku.",
        "battery_saver_title" to "Energiesparmodus (Max. 30 Min. Verzögerung)",
        "battery_saver_desc" to "Benachrichtigungen kommen spät. Spart Akku und versteckt das Symbol.",
        "language_section" to "SPRACHE / LANGUAGE",
        "appearance_title" to "ERSCHEINUNGSBILD",
        "theme_light" to "☀️ Hell",
        "theme_dark" to "🌙 Dunkel",
        
        // Onboarding
        "onboarding_subtitle" to "DIGITALER SPIEGEL DEINER ZEIT",
        "onboarding_desc1" to "Diese App blockiert deine Apps nicht und belehrt dich nicht. Sie zeigt nur die nackte Wahrheit darüber, wie viel von deinem Leben beim Scrollen durch deine Finger rinnt.",
        "onboarding_desc2" to "Um deine Schuld zu berechnen, benötigt die App eine Berechtigung: 'Nutzungsdatenzugriff'. Alles wird zu 100 % lokal und sicher auf deinem Gerät verarbeitet.",
        "grant_access_btn" to "ZUGRIFF GEWÄHREN",
        "check_permission_btn" to "BERECHTIGUNG PRÜFEN",
        "prominent_disclosure" to "Wichtiger Hinweis (Google Play Richtlinie):\nScrollDebt sammelt und verarbeitet Daten über deine App-Nutzungszeit (Usage Data), um deine verschwendete Zeit in Echtzeit zu berechnen. Die App verwendet auch Vordergrunddienste, um Push-Benachrichtigungen zu senden, selbst im Hintergrund.",
        "accept_disclosure" to "Ich akzeptiere die Erhebung und Verarbeitung meiner App-Nutzungsdaten für die oben genannten Zwecke.",
        
        // Brutal truth helper
        "brutal_truth_disabled" to "Brutale Wahrheit Nachrichten sind deaktiviert. Du siehst nur trockene Zahlen.",
        
        // Widget
        "widget_wasted_time" to "DIESE ZEIT BEKOMMST DU NIE ZURÜCK",
        
        // Time Units
        "time_y_1" to "j",
        "time_y_many" to "j",
        "time_mo" to "m",
        "time_d" to "t",
        "time_h" to "h",
        "time_m" to "m"
    )

    fun get(key: String, lang: String): String {
        return when (lang.lowercase()) {
            "en" -> en[key] ?: pl[key] ?: key
            "es" -> es[key] ?: pl[key] ?: key
            "fr" -> fr[key] ?: pl[key] ?: key
            "de" -> de[key] ?: pl[key] ?: key
            else -> pl[key] ?: key
        }
    }
}
