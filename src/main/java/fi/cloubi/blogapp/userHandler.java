package fi.cloubi.blogapp;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.io.BufferedReader;


	/**
	 * Jetty Handler to process ajax requests send by the frontend.
	 */
	public class userHandler extends AbstractHandler {

		// users stored in this json file
		public static final String dbFile = "./users.json";

		public void handle(String target, Request baseRequest, 
			HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {

			System.out.println(request.getMethod() + ": " + target);

			if ( "/users".equals(target) ) {

				if ( "GET".equals(request.getMethod()) ) {
					getUsers(baseRequest, response);
				}

				else if ( "POST".equals(request.getMethod()) ) {
					createUser(baseRequest, request, response);
				}

			} else if ("/users/login".equals(target)) {
				if("POST".equals(request.getMethod())) {
					login(baseRequest, request, response);
				}
			}
		}

		public void getUsers(Request baseRequest, HttpServletResponse response)
			 throws IOException, ServletException {

			JSONArray array = getUsers();

			writeJSONResponse(baseRequest, response, array.toString());

		}

		public void createUser(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {

			JSONObject data = readJSONObject(request);

			if ( data != null ) {

				JSONArray array = getUsers();

				// Generate new unique ID for the post using random UUID.
				UUID userId = UUID.randomUUID();
				Boolean isLoggedIn = false;
				data.put("userId", userId);
				data.put("isLoggedIn", isLoggedIn);
				
				System.out.println(data.getString("username"));
				System.out.println(data.getString("password"));

				array.put(data);

				System.out.println("Created user with id " + userId);

				writeUsers(array);

				writeJSONResponse(baseRequest, response, data.toString());

			}

		}

		private synchronized JSONArray getUsers() {

			try {
				String json = new String(Files.readAllBytes(Paths.get(dbFile)), "UTF-8");
				return new JSONArray(json);
			} catch ( Exception e ) {
			}

			return new JSONArray();

		}

		private synchronized void writeUsers(JSONArray array) {

			try {
				Files.write(Paths.get(dbFile), array.toString().getBytes("UTF-8"), 
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch ( Exception e ) {
			}

		}

		public JSONObject readJSONObject(HttpServletRequest request) {
	
			try {
	
				BufferedReader br = request.getReader();
				String line = null;
				StringBuilder sb = new StringBuilder();
	
				while ( (line = br.readLine()) != null ) {
					sb.append(line);
				}
	
				return new JSONObject(sb.toString());
		
			} catch ( Exception e ) {
			}
		
			return null;
		
		}

		private void writeJSONResponse(Request baseRequest, HttpServletResponse response,
			 String data)
			 throws IOException, ServletException {

			response.setContentType("application/json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.println(data);

			baseRequest.setHandled(true);

		}

		public void login(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {


			JSONArray array = getUsers();
			JSONArray filtered = new JSONArray();

			for ( int i=0; i<array.length(); i++ ) {
				JSONObject user = array.getJSONObject(i);
				if ( !user.getString("username").equals(request.getParameter("username"))  &&
				!user.getString("password").equals(request.getParameter("password"))) {
					user.put("isLoggedIn", true);
					filtered.put(user);
				} else {
					System.out.println("Username or password wrong, please try again!");
				}
			}
			writeUsers(filtered);
			

			writeJSONResponse(baseRequest, response, "{}");

		}

		

	}

	