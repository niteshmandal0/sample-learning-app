package packaging

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Creates the learning package and places it at build/learning-package.zip. Relies on a previous release build.
 */
fun generateLearningPackage() {
    val sourceDirectory = File("learning-package")
    val sourceApk = File("sample-learning-app/build/outputs/apk/release/sample-learning-app-release.apk")
    val targetZipFile = File("build/learning-package.zip")

    try {
        ZipOutputStream(FileOutputStream(targetZipFile)).use { zip ->
            sourceDirectory.walk()
                .filter { it.isFile }
                .forEach { zip.putEntry(it.toRelativeString(sourceDirectory), it) }
            zip.putEntry("app.apk", sourceApk)
        }
    } catch (e: Exception) {
        targetZipFile.delete()
        throw e
    }
}

private fun ZipOutputStream.putEntry(path: String, file: File) {
    putNextEntry(ZipEntry(path))
    file.inputStream().use { it.copyTo(this) }
}
