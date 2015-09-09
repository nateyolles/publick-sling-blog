# Publick Apache Sling + Sightly Blog

Publick is named after [Publick Occurrences Both Forreign and Domestick](https://en.wikipedia.org/wiki/Publick_Occurrences_Both_Forreign_and_Domestick), the first newspaper in the American colonies.

View a custom implementation of Publick with custom styling and components at [https://github.com/nateyolles/publick-nateyolles](https://github.com/nateyolles/publick-nateyolles) and the live implementation at [nateyolles.com](http://www.nateyolles.com). You can log into [nateyolles.com/admin](http://www.nateyolles.com/admin) with user *demo* and password *demo*.

View the [Trello Board](https://trello.com/b/2zcNr5qV/publick-sling-sightly-blog-engine).

## Download and start Apache Sling

This project requires Apache Sling 7.

1. [Download](http://sling.apache.org/downloads.cgi) the Apache Sling self runnable jar.
2. Start the Apache Sling instance by running the following from the command line:

```
java -jar org.apache.sling.launchpad-7-standalone.jar
```

## Install Apache Maven

[Apache Maven](https://maven.apache.org/) is used to build the project.

## Install Sightly

Install external dependencies to a running Sling instance with default values of port *8080*, user *admin* and password *admin*:

    mvn clean install -PautoInstallDependencies

## Build

Build and deploy to a running Sling instance with default values of port *8080*, user *admin* and password *admin*:

    mvn clean install -PautoInstallBundle

## Login

Navigate to [http://localhost:8080/admin/login.html](http://localhost:8080/admin/login.html). The default credentials are *admin*/*admin*.

## Create Users

1. Navigate to [http://localhost:8080/admin/users.html](http://localhost:8080/admin/users.html)
2. Change admin password
3. Create an Author account

## Configuration

Configurations can be set in any of three ways:

1. The Publick dashboard [http://localhost:8080/admin.html](http://localhost:8080/admin.html)
2. The Apache Felix (OSGi) console [http://localhost:8080/system/console/configMgr](http://localhost:8080/system/console/configMgr)
3. Create preconfigured sling:OsgiConfig nodes. View examples under [/publick/ui/src/main/resources/jcr_root/libs/publick/install](https://github.com/nateyolles/publick-sling-blog/tree/master/ui/src/main/resources/jcr_root/libs/publick/install)

Setup reCAPTCHA

1. Sign up at [https://www.google.com/recaptcha](https://www.google.com/recaptcha)
2. Navigate to [http://localhost:8080/admin/config.html](http://localhost:8080/admin/config.html)
3. Insert site key and secret key

Setup your SMTP server

1. Setup your email server using something like [Amazon Simple Email Service (SES)](https://aws.amazon.com/ses/), [Postfix](http://www.postfix.org/) or [Gmail](https://mail.google.com)
2. Navigate to [http://localhost:8080/admin/config/email.html](http://localhost:8080/admin/config/email.html)
3. Insert your server information

Setup your System Settings

1. Navigate to [http://localhost:8080/admin/config/system.html](http://localhost:8080/admin/config/system.html)
2. Insert your blog name.
3. Turn extensionless URLs on/off and setup your web server rewrites accordingly.

## Debugging

Attach a debugger to the Apache Sling instance by running the following from the command line:

```
java -Xmx2048M \
     -agentlib:jdwp=transport=dt_socket,address=30303,server=y,suspend=n \
     -jar org.apache.sling.launchpad-7-standalone.jar
```

## Apache Web Server setup

  1. Serve your address on port 80 and proxy to Apache Sling on port 8080.
  2. Redirect paths to remove "/content".
  3. Redirect for extentionless URLs.

```
<VirtualHost *:80>
    ProxyPreserveHost On
    ProxyPass / http://localhost:8080/
    ProxyPassReverse / http://localhost:8080/
    ServerName www.yourdomain.com
</VirtualHost>
```

```
<IfModule mod_dir.c>
    DirectorySlash Off
</IfModule>

<IfModule mod_rewrite.c>
    RewriteEngine On

    # Always use www
    RewriteCond %{HTTP_HOST} !^www\.
    RewriteRule ^ http://www.%{HTTP_HOST}%{REQUEST_URI} [R=301,L]

    # Step 1: Redirect all paths that end in .html or slash.
    # Redirect to remove index.html and /content. Hopefully
    # you've provided the correct links so that you don't
    # have to do any of these redirects.

    # remove trailing slash
    RewriteRule     ^(.+)/$ $1 [R=301,L,NC,QSA]

    # remove content
    RewriteRule     ^/content/(.*)$ /$1 [R=301,L,NC,QSA]

    # Remove .html
    # Condition needed for a bug in Sling 7. Updating a user group
    # doesn't work when posting to JSON. While fixed in Sling 8, the
    # admin JavaScript UserService#PATH_UPDATE_GROUP would need to be
    # updated as well if you were going to use it. See readme.md.
    RewriteCond     %{REQUEST_URI} !^/system/userManager/group/.+\.update.html [NC]
    RewriteRule     (.*).html$ $1 [R=301,L,NC,QSA]

    # remove /index
    RewriteRule     (.*)/index $1 [R=301,L,NC,QSA]

    # Step 2: Use a path through to do an internal rewrite rather
    # than a 301 or 302 redirect. Add the .html extension back on
    # so that Sling can resolve the resource with the correct
    # renderer.

    # Ending without a slash or extension, pass through to *.html
    RewriteCond     %{REQUEST_URI} !.*/j_security_check [NC]
    RewriteCond     %{REQUEST_URI} !^/bin [NC]
    RewriteCond     %{REQUEST_URI} !^/etc [NC]
    RewriteCond     %{REQUEST_URI} !^/assets [NC]
    RewriteCond     %{REQUEST_URI} !.*\..*/?$ [NC]
    RewriteCond     %{REQUEST_URI} !.*/$ [NC]
    RewriteRule     (.*)$ $1.html [PT,L,NC,QSA]

    # Ending with slash, pass through to index.html
    RewriteCond     %{REQUEST_URI} .*/$ [NC]
    RewriteRule     (.*)$ $1/index.html [PT,L,NC,QSA]
</IfModule>
```

## Further information

### Dealing with Sling bugs

#### /var node is incorrect jcr:primaryType

If you start getting errors in the log about `/var/discovery` or the `org.apache.sling.discovey.impl` framework, it's because of a bug in Sling. You can fix the problem by changing the `jcr:primaryType` of the `/var` node from `nt:unstructured` to `sling:Folder`. Run the following cURL command:

```
curl -u admin:admin -F"jcr:primaryType=sling:Folder" http://localhost:8080/var
```

#### Updating groups doesn't work with JSON

The post servlet that handles updating user groups doesn't work when posting to JSON in Sling 7. It has been fixed in Sling 8, however, this project is using the latest version of Sling's downloadable JAR, which is 7. The workaround is for the UserService in admin.js to post to HTML rather than JSON to update groups. The side effect is that in order to use extensionless URLs, you need to account for this in your web server redirects. See above.

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