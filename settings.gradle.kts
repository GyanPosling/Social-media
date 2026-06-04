pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
	}
}

rootProject.name = "social-media"

include(
	"apigateway",
	"authservice",
	"notificationservice",
	"postservice",
	"userservice",
)
