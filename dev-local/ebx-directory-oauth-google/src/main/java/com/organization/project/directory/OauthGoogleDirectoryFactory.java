package com.organization.project.directory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import com.mickaelgermemont.example.GoogleOauth2Client;
import com.mickaelgermemont.example.User;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import com.microsoft.aad.adal4jsample.HttpClientHelper;
//import com.microsoft.aad.adal4jsample.JSONHelper;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.AuthenticationException;
import com.orchestranetworks.service.directory.Directory;
import com.orchestranetworks.service.directory.DirectoryDefault;
import com.orchestranetworks.service.directory.DirectoryDefaultFactory;
import com.orchestranetworks.service.directory.DirectoryDefaultHelper;

public final class OauthGoogleDirectoryFactory extends DirectoryDefaultFactory {
	
	@Override
	public final Directory createDirectory(AdaptationHome aHome) throws Exception {
		final Directory dir = new CustomDirectory(aHome, new CustomTokenHelperImpl());
		return dir;
	}
	
	private final static class CustomDirectory extends DirectoryDefault {

		private final CustomTokenHelper customTokenHelper;
		
		protected CustomDirectory(final AdaptationHome arg0, final CustomTokenHelper customTokenHelper) {
			super(arg0);
			this.customTokenHelper = customTokenHelper;
		}
		
		@Override
		public UserReference authenticateUserFromHttpRequest(final HttpServletRequest request) throws AuthenticationException {

			final Optional<String> tokenOpt = customTokenHelper.getAccessToken(request);
			if(tokenOpt.isPresent()) {
				final String token = tokenOpt.get();
				
				final com.mickaelgermemont.example.User user;
				try {
					user = customTokenHelper.getUserFromGooglePlus(token);
				} catch (final Exception e) {
					new Exception("cant get usernames from google+", e).printStackTrace();
					return null;
				}
				
				if(user != null) {
					final String userId = user.getId();
					final UserReference ebxId = UserReference.forUser(userId);
					upsertUser(user, ebxId);
					return ebxId;
				} else {
					new Exception("username from google+ is null/empty").printStackTrace();
					return null;
				}
			}
			
			// EBX default Directory 
			return super.authenticateUserFromHttpRequest(request);
			
		}
		
		@Override
		public UserReference authenticateUserFromLoginPassword(final String aLogin, final String aPassword) {
			// EBX default Directory 
			return super.authenticateUserFromLoginPassword(aLogin, aPassword);
		}
		
		@Override
		protected UserReference authenticateUserFromLoginPassword(final String aLogin, final String aPassword, final HttpServletRequest aRequest) {
			// EBX default Directory
			return super.authenticateUserFromLoginPassword(aLogin, aPassword, aRequest);
		}
		
		private void upsertUser(final User user, final UserReference ebxId) {
			final UserEntity ebxUser;
			
			if(this.isUserDefined(ebxId)) {
				// update
				ebxUser = DirectoryDefaultHelper.findUser(ebxId, this);
			} else {
				// insert
				ebxUser = DirectoryDefaultHelper.newUser(ebxId, this);
			}
			
			ebxUser.setFirstName(user.getDisplayName());
			ebxUser.setLastName(user.getGender());
			DirectoryDefaultHelper.saveUser(ebxUser, "DirectoryOauthGoogleTokenUpdate", this);
		}
	}
	
	private static interface CustomTokenHelper {
		public Optional<String> getAccessToken(final HttpServletRequest request);
		public User getUserFromGooglePlus(String accessToken) throws Exception;
	}
	
	private static final class CustomTokenHelperImpl implements CustomTokenHelper {
//		private final static String IDENTIFIER = "##OPENIDCONNECT_TOKEN##";
//		private final static String HEADER_KEY = "CUSTOM_TOKEN";
		private final static String HEADER_KEY = "Authorization";
		
		@Override
		public Optional<String> getAccessToken(final HttpServletRequest request) {
//			System.out.print("request.getHeader('"+HEADER_KEY+"')=");
//			System.out.println(request.getHeader(HEADER_KEY));
			
			final String headerAuth = request.getHeader("Authorization");
			if(headerAuth!=null && headerAuth.startsWith("Bearer ")) {
				return Optional.of(headerAuth.substring(7));
			}
			
			return Optional.empty();
		}
		
		public User getUserFromGooglePlus(final String accessToken) throws Exception {
			final User data = GoogleOauth2Client.INSTANCE.getUserInfoJson(accessToken);
			return data;
		}
			
	}
	
	public static void main(final String[] args) throws Exception {
		final HttpServletRequest request = new HttpServletRequest() {
			
			@Override
			public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
					throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AsyncContext startAsync() throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAttribute(String name, Object o) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeAttribute(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isAsyncSupported() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isAsyncStarted() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getServerPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getServerName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getScheme() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RequestDispatcher getRequestDispatcher(String path) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRemotePort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getRemoteHost() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRemoteAddr() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRealPath(String path) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public BufferedReader getReader() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getParameterValues(String name) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration<String> getParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, String[]> getParameterMap() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getParameter(String name) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration<Locale> getLocales() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getLocalPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getLocalAddr() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ServletInputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DispatcherType getDispatcherType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getContentLengthLong() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration<String> getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getAttribute(String name) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AsyncContext getAsyncContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void logout() throws ServletException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void login(String username, String password) throws ServletException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isUserInRole(String role) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdValid() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromUrl() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromURL() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRequestedSessionIdFromCookie() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpSession getSession(boolean create) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpSession getSession() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getServletPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRequestedSessionId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public StringBuffer getRequestURL() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRequestURI() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRemoteUser() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getQueryString() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPathTranslated() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPathInfo() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Part> getParts() throws IOException, ServletException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Part getPart(String name) throws IOException, ServletException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getMethod() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getIntHeader(String name) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Enumeration<String> getHeaders(String name) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration<String> getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getHeader(String name) {
				
				if(name!=null && name.equals(CustomTokenHelperImpl.HEADER_KEY)) {
					return "abc";
				}
				
				return null;
			}
			
			@Override
			public long getDateHeader(String name) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Cookie[] getCookies() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAuthType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String changeSessionId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
				// TODO Auto-generated method stub
				return false;
			}
		};
		
		System.out.println(new CustomTokenHelperImpl().getAccessToken(request));
		System.out.println("Bearer ya29.Gl0rBjDkUJNwgFxT1_HOkJCAjj".substring(7));
		
		final String token = "eyJ0eXAiOiJKV1QiLCJub25jZSI6IkFRQUJBQUFBQUFEWHpaM2lmci1HUmJEVDQ1ek5TRUZFdUk3U2hnc2F5azZ3Z0I4RnFwUUJVMVQ3RF95eXctSXpYN1M1U3F1b2M5MXYxclJwUWhHWUVDd3duR2RnVWtZX1NRSTZXbXA0TzhiaE5qV2FnUHE5NGlBQSIsImFsZyI6IlJTMjU2IiwieDV0IjoiaTZsR2szRlp6eFJjVWIyQzNuRVE3c3lISmxZIiwia2lkIjoiaTZsR2szRlp6eFJjVWIyQzNuRVE3c3lISmxZIn0.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC85MWU2ZDgzYi04YmIzLTQzNDgtOGVhNC1jY2ZlZjQzMzcxOTQvIiwiaWF0IjoxNTM4NDI4NDY4LCJuYmYiOjE1Mzg0Mjg0NjgsImV4cCI6MTUzODQzMjM2OCwiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IkFTUUEyLzhJQUFBQUl4SGcyT0ErR0Ixa3p0eTZtMzBOZ1FUTDNMN0JOZS9BL203QnEvRGk1VU09IiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDYwMDAwRUI2NTIwM0QiLCJhbXIiOlsicHdkIl0sImFwcF9kaXNwbGF5bmFtZSI6IldlYmFwcC1PcGVuaWRjb25uZWN0IiwiYXBwaWQiOiJiYjUzNTZiZC1kYjY2LTQxYmQtODkyYy1mZjRhMTRjMmQ4YWQiLCJhcHBpZGFjciI6IjEiLCJlX2V4cCI6MjYyODAwLCJlbWFpbCI6Im1pY2thZWxnZXJtZW1vbnRAaG90bWFpbC5jb20iLCJmYW1pbHlfbmFtZSI6IkdFUk1FTU9OVCIsImdpdmVuX25hbWUiOiJtaWNrYWVsZ2VybWVtb250QGhvdG1haWwuY29tIiwiaWRwIjoibGl2ZS5jb20iLCJpcGFkZHIiOiIxNzQuMjI1LjEyOC41OSIsIm5hbWUiOiJtaWNrYWVsZ2VybWVtb250QGhvdG1haWwuY29tIEdFUk1FTU9OVCIsIm9pZCI6ImVhN2UzMjc5LThlMjAtNDE0NC05NmY4LTgyZDkxNTI3MThmNiIsInBsYXRmIjoiNSIsInB1aWQiOiIxMDAzQkZGREFFM0E2Mzk3Iiwic2NwIjoiVXNlci5SZWFkIFVzZXIuUmVhZEJhc2ljLkFsbCIsInNpZ25pbl9zdGF0ZSI6WyJrbXNpIl0sInN1YiI6Im9VdUU2YzFnTndEdWpHNXhTd2t3V09ITlNzOHlhcE9KNzNPLWJhMExOcmciLCJ0aWQiOiI5MWU2ZDgzYi04YmIzLTQzNDgtOGVhNC1jY2ZlZjQzMzcxOTQiLCJ1bmlxdWVfbmFtZSI6ImxpdmUuY29tI21pY2thZWxnZXJtZW1vbnRAaG90bWFpbC5jb20iLCJ1dGkiOiI2SHpmaklDQUZrbXh3OTJlSmd1bUFRIiwidmVyIjoiMS4wIiwid2lkcyI6WyI2MmU5MDM5NC02OWY1LTQyMzctOTE5MC0wMTIxNzcxNDVlMTAiXSwieG1zX3RjZHQiOiIxNTM3OTcwMTExIn0.nh6SBO6-eX1DAc9M2elf2-Un1lfC71fj_Hc-xVbDDEQ9Y4L8r6ccahgXXGAW6CPHIrFJfxzQ3jbvbnBLftvOFOr0XXQuJLtMzBg1jaWNINO08O9QNfNtnuFsgJp-N_N6jjDkKWdGM43V1T9DB2mPp1SouADuUzlOPk5vt5WESS45V7p7o3OImA9vhcn5NxhTkzcKCxnCi2hg-_iSNrB2pfnJIy1PPZhTYl4GbHAzvyvk-M5Xr5eRLIr-wQSS944NLVLv_hdbMNikZoWaUsi1YtXA6YMkeb3OLwu5UcR7NGpa9-B0rANWS_B6Kzik3TFQIb1GFR60TVIocM2TTWqWBQ";

		final User user = new CustomTokenHelperImpl().getUserFromGooglePlus(token);
		System.out.print("user=");
		System.out.println(user);
		
		
	}
}
