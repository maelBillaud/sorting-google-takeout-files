# Programme de tri des fichiers Google Takeout
Application qui permet de trier et déplacer des fichiers téléchargés depuis Google Takeout dans un dossier souhaité

## Problématique
On peut utiliser Google Takeout pour télécharger un toutes ses images/ vidéos stockées sur Google Photo.
Cependant, il extrait toutes les metadatas de ces fichiers dans de nouveaux fichiers .json.

Trier tous ces fichiers et tous les déplacer dans le dossier souhaité à la main prend beaucoup de temps.

## Solution

Ce programme prend en saisi utilisateur le fichier qui contient tous les fichiers téléchargés depuis Google Takeout ainsi que le fichier de destination souhaité.

Il va chercher dans tous les sous-dossiers de celui contenant les fichiers à déplacer, chaque dossier contenant des médias (.jpg, .jpeg, .png, .mp4).

**Note : Google takeout nomme les dossiers contenant des images avec un nom contenant l'année de création du média. 

Pour chacun de ces dossiers, il va : 
- Supprimer les métadatas extraites par Google.
- Ajouter l'année de création du fichier à la fin de son nom.
- Créer dans le dossier de destination un sous dossier par année trouvée et y déplacer les fichiers ayant l'année correspondante
