# Publick Apache Sling + Sightly Blog

This project is intended to host my personal site and blog.

Publick is named after [Publick Occurrences Both Forreign and Domestick](https://en.wikipedia.org/wiki/Publick_Occurrences_Both_Forreign_and_Domestick), the first newspaper in the American colonies.

View a custom implementation of Publick with custom styling and components at [https://github.com/nateyolles/publick-nateyolles](https://github.com/nateyolles/publick-nateyolles).

## Download and start Apache Sling

This project requires Apache Sling 7.

1. [Download](http://sling.apache.org/downloads.cgi) the Apache Sling self runnable jar.
2. Start the Apache Sling instance by running the following from the command line:

```
java -jar org.apache.sling.launchpad-7-standalone.jar
```

## Install Sightly

Install external dependencies to a running Sling instance with default values of port *8080*, user *admin* and password *admin*:

    mvn clean install -PautoInstallDependencies

## Build

Build and deploy to a running Sling instance with default values of port *8080*, user *admin* and password *admin*:

    mvn clean install -PautoInstallBundle

## Login

Navigate to [http://localhost:8080/admin/login.html](http://localhost:8080/admin/login.html). The default credentials are *admin*/*admin*.

## Setup

Create user

1. Navigate to [http://localhost:8080/admin/users.html](http://localhost:8080/admin/users.html)
2. Change admin password
3. Create an Author account

Setup reCAPTCHA

1. Sign up at [https://www.google.com/recaptcha](https://www.google.com/recaptcha)
2. Navigate to [http://localhost:8080/admin/config.html](http://localhost:8080/admin/config.html)
3. Insert site key and secret key

Setup your SMTP server

1. Setup your email server using something like [Amazon Simple Email Service (SES)](https://aws.amazon.com/ses/), [Postfix](http://www.postfix.org/) or [Gmail](https://mail.google.com)
2. Navigation to [http://localhost:8080/admin/config/email.html](http://localhost:8080/admin/config/email.html)
3. Insert your server information

## Debugging

Attach a debugger to the Apache Sling instance by running the following from the command line:

```
java -Xmx2048M \
     -agentlib:jdwp=transport=dt_socket,address=30303,server=y,suspend=n \
     -jar org.apache.sling.launchpad-7-standalone.jar
```

## Further information

### Fix a Sling bug

If you start getting errors in the log about `/var/discovery` or the `org.apache.sling.discovey.impl` framework, it's because of a bug in Sling. You can fix the problem by changing the `jcr:primaryType` of the `/var` node from `nt:unstructured` to `sling:Folder`. Run the following cURL command:

```
curl -u admin:admin -F"jcr:primaryType=sling:Folder" http://localhost:8080/var
```

### Maven Archetypes

Two Maven Archetypes where used to begin the project, one for the core Java bundle and one for the UI bundle. This is purely informational; you do not need to run the following commands.

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