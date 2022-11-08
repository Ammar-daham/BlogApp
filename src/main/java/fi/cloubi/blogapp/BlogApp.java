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

		handlers.addHandler(new userHandler());

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

			if ( "/posts".equals(target) ) {

				if ( "GET".equals(request.getMethod()) ) {
					getPosts(baseRequest, response);
				}

				else if ( "POST".equals(request.getMethod()) ) {
					createPost(baseRequest, request, response);
				}

			} 

			else if ( target.startsWith("/posts/") ) {

				if ( "DELETE".equals(request.getMethod()) ) {
					String postId = target.substring(7);
					deletePost(baseRequest, request, response, postId);
				}

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

				// Generate new unique ID for the post.
				// It is one higher than the highest ID currently in the database.
				int id = 1;
				for ( int i=0; i<array.length(); i++ ) {
					int postId = array.getJSONObject(i).getInt("id");
					if ( postId >= id ) {
						id = postId + 1;
					}
				}

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
		 * @param postId The ID of the post to remove.
		 * @throws IOException
		 * @throws ServletException
		 */
		public void deletePost(Request baseRequest, HttpServletRequest request, HttpServletResponse response,
			String postId)
			 throws IOException, ServletException {

			int id = Integer.parseInt(postId);

			JSONArray array = getPosts();
			JSONArray filtered = new JSONArray();

			for ( int i=0; i<array.length(); i++ ) {
				JSONObject post = array.getJSONObject(i);
				if ( post.getInt("id") != id ) {
					filtered.put(post);
				} else {
					System.out.println("Deleted post with id " + id);
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