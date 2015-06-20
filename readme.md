# Publick Sling Blog

This project is intended to host my personal site and blog.

Publick is named after [Publick Occurrences Both Forreign and Domestick](https://en.wikipedia.org/wiki/Publick_Occurrences_Both_Forreign_and_Domestick), the first newspaper in the American colonies.

## Maven Archetypes

1. Create parent pom
2. Create sub projects using the following archetypes:

```
mvn archetype:generate \
    -DarchetypeGroupId=org.apache.sling \
    -DarchetypeArtifactId=sling-initial-content-archetype \
    -DgroupId=com.nateyolles.sling.publick \
    -DartifactId=ui \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.nateyolles.sling.publick.ui \
    -DappsFolderName=publick \
    -DartifactName="ui" \
    -DpackageGroup="ui"
```
```
mvn archetype:generate \
    -DarchetypeGroupId=org.apache.sling \
    -DarchetypeArtifactId=sling-bundle-archetype \
    -DgroupId=com.nateyolles.sling.publick \
    -DartifactId=core \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.nateyolles.sling.publick.core \
    -DappsFolderName=publick \
    -DartifactName="core" \
    -DpackageGroup="core"
```

## How to build

You can build and deploy to a running Sling instance with default values of port *8080*, user *admin* and password *admin* with:

    mvn clean install -PautoInstallBundle