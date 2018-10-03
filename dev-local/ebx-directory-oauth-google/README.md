# ebx-directory-oauth-google

works with jdk 1.8 and ebx 5.8.1.1067

## build

```
mvn clean install
```

## EBX deployment

update your ebx.properties

```
ebx.directory.factory=com.orchestranetworks.ps.customDirectory.OauthGoogleDirectoryFactory
```

copy the jar to your tomcat/lib
