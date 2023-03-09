import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
	//On suppose que les noms des fichiers qui contiennent les photos terminent par l'année de la photo
	public static void main(String[] args) throws IOException {
		System.out.println("\n   //////////////////////////////////////////////////////////////////////" + "\n  /// Projet de déplacement des fichiers (photos/vidéos) téléchargés ///" + "\n /// depuis Google Takeout d'un dossier à un autre -- VERSION 1.0   ///" + "\n//////////////////////////////////////////////////////////////////////\n");

		File rootDirectoryPath = getRootDirectoryPathFromUser();
		File destinationDirectoryPath = getDestinationDirectoryPathFromUser();

		List<File> pictureDirectoriesList = getAllPictureDirectories(rootDirectoryPath, new ArrayList<>());
		int totalPictureNumber = 0;

		for (File pictureDirectory : pictureDirectoriesList) {
			deleteJsonFiles(pictureDirectory);
			int pictureNumber = changeFilePath(destinationDirectoryPath, pictureDirectory);
			totalPictureNumber += pictureNumber;
			System.out.println("Déplacement de " + pictureNumber + " fichiers.");
		}

		System.out.println("\nFin du traitement des fichiers (" + totalPictureNumber + " fichiers au total).");
	}

	/**
	 * Demande à l'utilisateur le dossier dans quel dossier il faut aller chercher toutes les photos
	 *
	 * @return le chemin du dossier
	 */
	private static File getRootDirectoryPathFromUser() {
		boolean rootDirectoryPathOk = false;
		File rootDirectoryPath = null;

		while (! rootDirectoryPathOk) {
			Scanner sc = new Scanner(System.in);

			System.out.print("Entrez le chemin absolut du dossier contenant les photos a traiter : ");
			String input = sc.nextLine();
			rootDirectoryPath = new File(input);
			System.out.print("Seuls les photos contenues dans le dossier \"" + input + "\" seront traitées. Continuer" + " ? (Y/N) ");
			input = sc.next();

			rootDirectoryPathOk = ("Y".equals(input) || "y".equals(input));

			if (rootDirectoryPathOk && ! rootDirectoryPath.exists()) {
				System.out.println("Erreur ! Le dossier renseigné n'existe pas. Veuillez choisir un chemin correct.");
				rootDirectoryPathOk = false;
			}
			System.out.print("\n");
		}
		return rootDirectoryPath;
	}

	/**
	 * Demande à l'utilisateur le dossier dans quel dossier il faut transférer et trier les photos
	 *
	 * @return le chemin du dossier
	 */
	private static File getDestinationDirectoryPathFromUser() {
		boolean destinationDirectoryPathOk = false;
		File destinationDirectoryPath = null;

		while (! destinationDirectoryPathOk) {
			Scanner sc = new Scanner(System.in);

			System.out.print("Entrez le dossier dans lequel les photos seront transférées et triées : ");
			String input = sc.nextLine();
			destinationDirectoryPath = new File(input);
			System.out.print("Seuls les photos contenues dans les sous dossiers de  \"" + input + "\" seront transférées et triées. Continuer ? (Y/N) ");
			input = sc.next();

			destinationDirectoryPathOk = ("Y".equals(input) || "y".equals(input));

			if (destinationDirectoryPathOk && ! destinationDirectoryPath.exists()) {
				System.out.println("Erreur ! Le dossier renseigné n'existe pas. Veuillez choisir un chemin correct.");
				destinationDirectoryPathOk = false;
			}
			System.out.print("\n");
		}

		return destinationDirectoryPath;
	}

	/**
	 * Parcours les fichiers du dossier pour vérifier l'existence d'images
	 *
	 * @param directory dossier à vérifier
	 *
	 * @return true si le dossier contient au moins une image, false sinon
	 */
	private static boolean directoryWithPicture(File directory) {
		String[] listDirectoryName = directory.list();
		boolean pictureExiste = false;
		int i = 0;
		while (! pictureExiste && i < Objects.requireNonNull(listDirectoryName).length) {
			pictureExiste = listDirectoryName[i].endsWith(".jpg") || listDirectoryName[i].endsWith(".jpeg") || listDirectoryName[i].endsWith(".png") || listDirectoryName[i].endsWith(".mp4");
			i++;
		}
		return pictureExiste;
	}

	/**
	 * Parcours l'entièreté des sous dossiers de {@param rootDirectoryPath} pour retourner ceux contenant au moins
	 * une photo
	 *
	 * @param rootDirectoryPath Chemin de début de recherche
	 * @param pictureDirectoriesList permet de stocker la liste des dossiers contenant des photos
	 *
	 * @return une liste de {@link File} qui contient des photos
	 */
	private static List<File> getAllPictureDirectories(File rootDirectoryPath, List<File> pictureDirectoriesList) {

		//On filtre la liste pour qu'elle ne retourne que des dossiers
		FileFilter filter = File::isDirectory;

		if (rootDirectoryPath.exists()) {
			File[] subFolders = rootDirectoryPath.listFiles(filter);
			assert subFolders != null : "Erreur ! Le dossier " + rootDirectoryPath + " n'existe pas.";

			for (File subFolder : subFolders) {
				if (directoryWithPicture(subFolder)) {
					pictureDirectoriesList.add(subFolder);
				} else {
					getAllPictureDirectories(subFolder, pictureDirectoriesList);
				}
			}
		}
		return pictureDirectoriesList;
	}

	/**
	 * Supprime tous les fichiers .json (métaData des photos) du dossier (et aussi les .gif)
	 *
	 * @param directory dossier qui contient les fichiers .json parmi les photos à supprimer
	 *
	 * @throws IOException lorsque la suppression ne s'est pas correctement déroulée
	 */
	private static void deleteJsonFiles(File directory) throws IOException {
		//On filtre la liste pour qu'elle ne retourne que des fichiers
		FileFilter filter = File::isFile;
		File[] listFiles = directory.listFiles(filter);

		assert listFiles != null : "Erreur ! Le dossier " + directory + " n'existe pas.";
		for (File file : listFiles) {
			if (file.getName()
			        .endsWith(".json") || file.getName()
			                                  .endsWith(".gif")) {
				Files.delete(file.toPath());
			}
		}
	}

	/**
	 * Change le nom d'un fichier en ajoutant une année juste avec le format
	 * (test.jpg --> test - 2023.jpg)
	 *
	 * @param file Fichier dont il faut changer le nom
	 * @param year Année à ajouter
	 *
	 * @return Le nouveau nom du fichier
	 */
	private static String addYearAtEndOfFileName(File file, String year) {
		String fileName = file.getName();
		int indexOfFormatFile = fileName.lastIndexOf('.');
		String newEndFileName = "_" + year + fileName.substring(indexOfFormatFile);
		return fileName.replaceFirst("\\..{3,4}$", newEndFileName);
	}

	/**
	 * Change de chemin absolut les fichiers contenant les photos
	 *
	 * @param destinationDirectoryPath dossier de destination donné par l'utilisateur
	 * @param directory dossier contenant les photos
	 *
	 * @return un entier correspondant au nombre de photos traitées
	 */
	private static int changeFilePath(File destinationDirectoryPath, File directory) {
		String currentDirectoryName = directory.getName();
		String newDirectoryName = "";
		//On part du principe que tous les dossiers contenant les photos se terminent par l'année de la photo
		if (currentDirectoryName.length() < 4) {
			newDirectoryName = "unknown year";
		} else {
			newDirectoryName = currentDirectoryName.substring(currentDirectoryName.length() - 4);
		}

		String[] destinationDirectoryExisting = destinationDirectoryPath.list();
		assert destinationDirectoryExisting != null : "Erreur ! Le dossier " + destinationDirectoryPath + " n'existe pas.";

		boolean isNewDirectoryNameAlreadyExist = false;
		int i = 0;

		while (! isNewDirectoryNameAlreadyExist && i < destinationDirectoryExisting.length) {
			isNewDirectoryNameAlreadyExist = destinationDirectoryExisting[i].equals(newDirectoryName);
			i++;
		}

		File newDirectoryPath = new File(destinationDirectoryPath.getAbsolutePath() + File.separator + newDirectoryName);

		if (! isNewDirectoryNameAlreadyExist) {
			boolean mkdirWorked = newDirectoryPath.mkdir();
			assert mkdirWorked : "Erreur ! La création du dossier  " + newDirectoryPath.getName() + " n'a pas fonctionnée.";
		}

		//On enlève les doublons (fichiers determinants par (1).XXX par exemple)
		FileFilter filter = pathname -> ! pathname.getName()
		                                          .matches(".*\\(\\d\\)\\..*$");
		File[] pictureList = directory.listFiles(filter);
		assert pictureList != null : "Erreur ! Le dossier " + directory + " n'existe pas.";

		for (File picture : pictureList) {
			String newPathName = newDirectoryPath.getAbsolutePath() + File.separator + addYearAtEndOfFileName(picture, newDirectoryName);
			boolean renamingWorked = picture.renameTo(new File(newPathName));
			assert renamingWorked : "Erreur ! Le renommage du dossier  " + newDirectoryPath.getName() + " n'a pas fonctionné.";
		}
		return pictureList.length;
	}
}