pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
<<<<<<< HEAD
        maven(url = "https://jitpack.io")
=======
>>>>>>> 61caeb2 (Initial commit)
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
<<<<<<< HEAD
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "giaodien"
=======
    }
}

rootProject.name = "Expense Management"
>>>>>>> 61caeb2 (Initial commit)
include(":app")
 