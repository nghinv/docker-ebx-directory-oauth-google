# ebx docker wrapper around ebx-directory-oauth-google

works with jre 10

## maven build

```
mkdir bin
rm bin/*.jar && cd dev-local/ebx-directory-oauth-google && mvn clean install && cp target/ebx-directory-oauth-google-1.0-SNAPSHOT.jar ../../bin && cp target/deps/*.jar ../../bin && cd ../..
```

## EBX deployment

update your ebx.properties

```
ebx.directory.factory=com.orchestranetworks.ps.customDirectory.OauthGoogleDirectoryFactory
```

copy the jar to your tomcat/lib OR see docker build/run

## Docker build

```
put your ebxLicense in ~/.profile
export EBXLICENSE=XXXXX-XXXXX-XXXXX-XXXXX
source ~/.profile
docker build -t ebx5.8.1-oauth-google .
```

## Docker run

```
docker run --rm -p 9090:8080 --mount type=volume,src=ebx-repository-h2-oauth2google,dst=/ebx-repository-h2 -e "CATALINA_OPTS=-DebxLicense=$EBXLICENSE" --name ebx581_oauth_google ebx5.8.1-oauth-google
```

open your browser at ```http://localhost:9090/ebx```

## connect to running container

```
docker exec -it ebx581_oauth_google /bin/bash
```

## POSTMAN test REST

open EBX and go to Administration > Directory > dataset Permission, and give read-only access to [all profiles]

oauth2 google accesstoken

URL ```http://localhost:9090/ebx-dataservices/rest/data/v1/Bebx-directory/ebx-directory//directory/users```

use header key ```CUSTOM_TOKEN```

use header value similar to ```ya29.Gl0rBjDkUJNwgFxT1_HOkJCAjj-5B0NDt197V-4-JeladX8EZFkttpTNDgxxSQTJD3Ug-3nN6IArpNEAeWCJGDamKzSU_LmbVwQtFc1UldceNQMQwfVM-KXPltrQ190```
