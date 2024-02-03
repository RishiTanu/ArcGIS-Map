pluginManagement {
    repositories {
        gradlePluginPortal()

        google()
        mavenCentral()
        maven(url = "https://esri.jfrog.io/artifactory/arcgis")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://esri.jfrog.io/artifactory/arcgis")
    }

}

rootProject.name = "AarcGis"
include(":app")
