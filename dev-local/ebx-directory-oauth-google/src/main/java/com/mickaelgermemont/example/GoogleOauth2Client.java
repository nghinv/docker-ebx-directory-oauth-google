package com.mickaelgermemont.example;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 */
public final class GoogleOauth2Client {
	
	private static final String USER_INFO_URL = "https://www.googleapis.com/plus/v1/people/me";
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	public static final GoogleOauth2Client INSTANCE = new GoogleOauth2Client();
	
	private GoogleOauth2Client() {}

	/**
	 * Expects an accessToken, and makes an authenticated request for the user's profile information
	 * @return JSON formatted user profile information
	 * @param accessToken provided by google
	 */
	public User getUserInfoJson(final String accessToken) throws IOException {
		final GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(JSON_FACTORY).build()
				.setAccessToken(accessToken);
		
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
//		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> {
//			request.setParser(new JsonObjectParser(JSON_FACTORY));
//		});

		// Make an authenticated request - to google+ API user profile
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		
		request.getHeaders().setContentType("application/json");
		final String rawResponse = request.execute().parseAsString();
//		System.out.println(rawResponse);
		
	    //final User user = (User) request.execute().parseAs(User.class);
		
		return JSON_FACTORY.fromString(rawResponse, User.class);
	}
	
	public static void main(String[] args) throws IOException {
//		final String info = INSTANCE.getUserInfoJson("ya29.Gl0qBn9ZmpYUAVhviopO1YoSLKjeLDR9X8fduafhvauo0TPgwJVS7myozJWJPO3aLtjkV0g-QOp844Sj8eaX-UsofzGznIhpCIJ6a0XCf1oJqxDSofMrmyrSqWcmnWw");
//		System.out.println(info);
		
		final User user = INSTANCE.getUserInfoJson("ya29.Gl0rBjDkUJNwgFxT1_HOkJCAjj-5B0NDt197V-4-JeladX8EZFkttpTNDgxxSQTJD3Ug-3nN6IArpNEAeWCJGDamKzSU_LmbVwQtFc1UldceNQMQwfVM-KXPltrQ190");
		System.out.println(user);
	}

}
