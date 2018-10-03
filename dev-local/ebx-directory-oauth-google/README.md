# ebx-directory-oauth-google

works with jdk 1.8

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
