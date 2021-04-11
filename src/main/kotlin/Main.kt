import KotlinFunctionLibrary.getValidatedInput
import java.io.File

fun main() {
    try{
        val listFiles = File(System.getProperty("user.dir")).listFiles()
        val containsDirectory = listFiles.any { it.isDirectory }
        val listOfFiles_NotDirectories =
            if (containsDirectory) listFiles.filter { !it.isDirectory } else listFiles.toList()
        val containsExtensionFile = listOfFiles_NotDirectories.any { it.nameWithoutExtension.toLowerCase() == "extension" }
//        println("listFiles=$listFiles,containsDirectory=$containsDirectory,listOfFiles_NotDirectories=$listOfFiles_NotDirectories,wantsText=$wantsText")
        val extension = if(containsExtensionFile) listOfFiles_NotDirectories.find{it.nameWithoutExtension.toLowerCase() == "extension"}!!.readText() else getValidatedInput(
            "\\w+".toRegex(),
            "You can create a file in this folder called \"extension.txt\" and write in it the extension that you would like to diff and rerun this program, or enter the desired extension not preceeded by the character \".\" (e.g. txt) now",
            "That is not a valid extension, please try again"
        )
        val files =
            if (containsDirectory) File(System.getProperty("user.dir")).walk().filter { it.extension == extension }
                .toList() else listFiles.filter { it.extension == extension }.toList()
        println("Number of files found: ${files.size}")
        println("Filenames found: ${files.map { if(containsDirectory) it.absolutePath else it.name }}")
        val listOfFileStrings = mutableMapOf<File, List<String>>()
        for (file in files) listOfFileStrings[file] =
            file.readLines() //same as listOfFileStrings.put(file, file.readLines())

        var thereWasAMissingLine = false
        for ((file, lines) in listOfFileStrings) {
            for (line in lines) {
                for ((otherFile, otherLines) in listOfFileStrings) {
                    if (otherFile != file && line !in otherLines) {
                        thereWasAMissingLine = true
                        println("The line \"$line\" is in ${if(containsDirectory) file.absolutePath else file.name} and not in ${if(containsDirectory) otherFile.absolutePath else otherFile.name}")
                    }
                }
            }
        }
        if (!thereWasAMissingLine) println("No lines were missing between any of the files.")
        println("Done.")
    } catch(e:Throwable){
        println("When this program tried to list the files in this folder, the list returned was null, which may mean that this program needs administrator permissions and does not have it, or there are no files in this folder, or there was some other filesystem failure. Please fix the problem and run again. Shutting down.")
    }
}