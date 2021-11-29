package utils

import org.gradle.api.Project

fun Project.getAppVersion(): String {
    val currentCommitTag = getCurrentCommitVersionTag()

    if (currentCommitTag != null)
        return tagToVersionName(currentCommitTag)
    else {
        val currentBranch = getGitBranch()
        val mostRecentTag = getMostRecentVersionTag()
        if (mostRecentTag == "") {
            return "0.0.1"
        }
        val mostRecentVersionArray = tagToVersionArray(mostRecentTag).toMutableList()

        when {
            currentBranch == "main" -> {
                mostRecentVersionArray[1]++
                mostRecentVersionArray[2] = 0
                return versionArrayToVersionName(mostRecentVersionArray) + "-snapshot"
            }
            currentBranch.matches("patch-.*\\..*".toRegex()) -> {
                val patchVersionArray = tagToVersionArray(currentBranch)
                val patchedVersion = "v-${patchVersionArray[0]}.${patchVersionArray[1]}"

                val highestRelevantTag = getMostRelevantVersionTag(patchedVersion)
                if (highestRelevantTag == "") {
                    return tagToVersionName(patchedVersion) + ".1"
                }

                val highestRelevantVersionArray = tagToVersionArray(highestRelevantTag).toMutableList()
                highestRelevantVersionArray[2] ++
                return versionArrayToVersionName(highestRelevantVersionArray)
            }
            else -> {
                return tagToVersionName(mostRecentTag) + "-" + currentBranch.replace(" ", "")
            }
        }
    }
}

fun String.toVersionCode(): Int {
    val parts = split('-')[0].split('.')
    return (parts[0].toInt() * 1000 + parts[1].toInt()) * 1000 + parts[2].toInt()
}

private fun tagToVersionName(versionTag: String): String = versionTag.split('-')[1]

private fun tagToVersionArray(versionTag: String): List<Int> =
    tagToVersionName(versionTag).split('.').map { it.toInt() }

private fun versionArrayToVersionName(versionArray: List<Int>): String = versionArray.joinToString(".")
