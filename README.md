#jvcAPI Client Program

Client program for the jeuxvideo.com API, thanks to [gromez](http://wiki.gromez.fr/dev/api/jeuxvideo.com).

##Example

When one game match the pattern:

    :Java Okami
    Synopsis:
    Okami est un jeu d'aventure et d'action sur Wii. Le titre dispose d'un aspect
    graphique très particulier, et vous propose d'incarner la déesse Amaterasu
    réincarnée en un magnifique loup blanc, dans une quête pour redonner de la
    vie et de la couleur à notre monde, terrorisé par de nombreux ennemis qui
    font régner les ténèbres. Battez-vous au moyen d'un pinceau, véritable
    prolongement du personnage qu'on incarne, et utilisez-le également pour
    avancer dans l'histoire.
    Title: Okami
    Release date: 12 juin 2008
    Editor: Capcom
    Developer: Ready At Dawn Studios
    Type: Aventure / Action

When multiple games match the pattern:

    :Java de\ Blob
    Result: 2
    19199 : de Blob
    37132 : de Blob 2
    Enter the ID:
    37132
    Synopsis:
    de Blob 2 est un jeu de plates-formes sur Wii. Vous devez sauver la mégapole
    de Prisma City des sinistres dictateurs aux envies monochromatiques.
    L'existence de nouveaux pouvoirs pour Blob viendra aiguiller et pigmenter
    votre aventure à travers une centaine de niveaux, et un mode coopératif
    qui permet à un second joueur de contrôler un robot volant.
    Title: de Blob 2
    Release date: 25 février 2011
    Editor: THQ
    Developer: Blue Tongue Entertainment
    Type: Plates-formes
