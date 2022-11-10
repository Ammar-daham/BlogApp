package fi.cloubi.blogapp;


import java.io.IOException;
import java.io.PrintWriter;

import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.util.UUID;

import org.eclipse.jetty.server.Server;

/**
 * This is a simple single-user blogging app using Jetty.
 */
class BlogApp {

	public static void main(String[] args) throws Exception {

		Server server = new Server(8080);

		// The frontend of the app is just a static single-page web app
		ResourceHandler staticHandler = new ResourceHandler();
		staticHandler.setBaseResource(Resource.newClassPathResource("/META-INF/resources/client/"));

		HandlerList handlers = new HandlerList();
		
		// This will serve the static frontend files
		handlers.addHandler(staticHandler);
		
		// This will handle the backend
		handlers.addHandler(new BlogHandler());

		handlers.addHandler(new UserHandler());

		server.setHandler(handlers);

		server.start();
		server.join();

	}

	/**
	 * Jetty Handler to process ajax requests send by the frontend.
	 */
	public static class BlogHandler extends AbstractHandler {

		// This is the file were the posts are saved
		public static final String dbFile = "./posts.json";

		public void handle(String target, Request baseRequest, 
			HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {

			System.out.println(request.getMethod() + ": " + target);

			if (request.getMethod().equals("GET") && target.equals("/posts") ) {
				getPosts(baseRequest, response);
			}
			if (request.getMethod().equals("POST") && target.equals("/posts") ) {
				createPost(baseRequest, request, response);
			}
			if (request.getMethod().equals("DELETE") && target.contains("/posts") ) {
					//String postId = target.substring(7);
					deletePost(baseRequest, request, response);
			}

		}

		/**
		 * Reads all current blog posts from the "database".
		 * @return An JSON array containing all the blog posts.
		 */
		private synchronized JSONArray getPosts() {

			try {
				String json = new String(Files.readAllBytes(Paths.get(dbFile)), "UTF-8");
				return new JSONArray(json);
			} catch ( Exception e ) {
			}

			return new JSONArray();

		}

		/**
		 * Writes blog posts to the "database", completely replacing the current posts.
		 * @param array An JSON array containgin all the blog posts.
		 */
		private synchronized void writePosts(JSONArray array) {

			try {
				Files.write(Paths.get(dbFile), array.toString().getBytes("UTF-8"), 
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch ( Exception e ) {
			}

		}
		
		/**
		 * Handles 'GET /posts' request. Just returns all posts from the database as they are.
		 * 
		 * @param baseRequest Jetty Request object.
		 * @param response The HttpServletResponse object to write response to.
		 * @throws IOException
		 * @throws ServletException
		 */
		public void getPosts(Request baseRequest, HttpServletResponse response)
			 throws IOException, ServletException {

			JSONArray array = getPosts();

			writeJSONResponse(baseRequest, response, array.toString());

		}

		/**
		 * Returns given String data back to client and marks it as a JSON.
		 * 
		 * @param baseRequest Jetty Request object.
		 * @param response The HttpServletResponse object to write response to.
		 * @param data The data to return. Should be a JSON object or JSON array.
		 * @throws IOException
		 * @throws ServletException
		 */
		private void writeJSONResponse(Request baseRequest, HttpServletResponse response,
			 String data)
			 throws IOException, ServletException {

			response.setContentType("application/json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.println(data);

			baseRequest.setHandled(true);

		}

		/**
		 *  Handles 'POST /posts' request. Adds new post to the database.
		 * 
		 * 
		 * @param baseRequest Jetty Request object.
		 * @param request The HttpServletRequest object to read the request data from.
		 * @param response The HttpServletResponse object to write response to.
		 * @throws IOException
		 * @throws ServletException
		 */
		public void createPost(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {

			JSONObject data = readPostJSONObject(request);

			if ( data != null ) {

				JSONArray array = getPosts();
				UUID id = UUID.randomUUID();
				data.put("id", id);
				array.put(data);
				System.out.println("Created post with id " + id);
				writePosts(array);
				writeJSONResponse(baseRequest, response, data.toString());

			}

		}

		/**
		 * Handles 'DELETE /posts/id' request. Removes a post with given ID from the database.
		 * 
		 * @param baseRequest Jetty Request object.
		 * @param request The HttpServletRequest object to read the request data from.
		 * @param response The HttpServletResponse object to write response to.
		 * @throws IOException
		 * @throws ServletException
		 */
		public void deletePost(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {

			//String postId = request.getParameter("id");
			//String username = request.getParameter("username");

			JSONObject data = readPostJSONObject(request);
			String username = data.getString("username");
			String id = data.getString("id");
			System.out.println("username " + username);

			//int id = Integer.parseInt(postId);

			JSONArray posts = getPosts();
			JSONArray filtered = new JSONArray();
			System.out.println("postsId " + id);
			//System.out.println("username " + username);

			for ( int i=0; i<posts.length(); i++ ) {
				JSONObject post = posts.getJSONObject(i);
				if (post.getString("id").equals(id) && post.getString("username").equals(username)) {
					System.out.println("Deleted post with id " + id);
				} else {
					filtered.put(post);

				}
			}
			writePosts(filtered);
			writeJSONResponse(baseRequest, response, "{}");
		}

		/**
		 * Reads JSONObject from the body of the http request.
		 * @param request The HttpServletRequest object to read the request data from.
		 * @return JSON object read from the body.
		 */
		public JSONObject readPostJSONObject(HttpServletRequest request) {
	
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

	}

	
}