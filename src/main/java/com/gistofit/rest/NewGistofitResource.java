/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gistofit.rest;

import static com.gistofit.model.OfyService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gistofit.domain.CommentListResponse;
import com.gistofit.domain.GistDomain;
import com.gistofit.domain.GistListResponse;
import com.gistofit.domain.GistResponse;
import com.gistofit.domain.ShardedCounter;
import com.gistofit.domain.UserServiceInfo;
import com.gistofit.model.Like;
import com.gistofit.model.URL;
import com.gistofit.model.Gist;
import com.gistofit.model.Comment;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.*;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo [tmatsuo@google.com]
 * Date: 4/4/13
 * Time: 11:58 PM
 */

@Path("/gists")
public class NewGistofitResource {

	private static final int CACHE_PERIOD = 86400;

	private final MemcacheService mc = MemcacheServiceFactory
			.getMemcacheService();

	private final Logger logger = Logger.getLogger(NewGistofitResource.class.getName());

	private GistListResponse getGists(String url, String cursor) {
		Query<Gist> query = ofy().load().type(Gist.class).limit(5);

		if (url != null) {
			Key<URL> urlKey = Key.create(URL.class, url);
			query = query.filter("url", urlKey);
		}
			
		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		query = query.order("-date");
		
		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = iterator.getCursor().toWebSafeString();
		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor);
	}

	@GET
	@Path("/recent")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getRecent(@QueryParam("cursor") final String cursor) throws
	Exception {
		return getGists(null, cursor);
	}

	private GistListResponse getTrendingGists(String cursor) {
		Query<Gist> query = ofy().load().type(Gist.class).limit(5);

		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = iterator.getCursor().toWebSafeString();
		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor);
	}

	@GET
	@Path("/trending")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getTrending(@QueryParam("cursor") final String cursor) throws
	Exception {
		return getTrendingGists(cursor);
	}

	  
	  @GET
	  @Path("/{url}")
	  @Produces(MediaType.APPLICATION_JSON)
	  public GistListResponse getGistsForURL(
	      @DefaultValue("default") @PathParam("url") final String url,
	      @QueryParam("cursor") final String cursor) throws
	      Exception {
		  	return getGists(url, cursor);
	  	}

	  @GET
	  @Path("/{url}/{id}")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Gist getSingleGist(
	      @DefaultValue("default") @PathParam("url") final String url,
	      @PathParam("id") final String id) throws
	      Exception {
		  	Long longId = Long.parseLong(id);
		  	Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
		  	
		    return ofy().load().key(gistKey).now();
	  	}
	  
	  
	@POST
	@Path("/{url}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Gist addGist(
			@DefaultValue("default") @PathParam("url") final String gistUrl,
			final Map<String, String> postData) {
		UserService userService = UserServiceFactory.getUserService();

		// We set the above parent key on each Greeting entity in order to make the queries strong
		// consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
		// rate more than 1 write/second.
		String content = postData.get("content");
		String genre = postData.get("genre");

		URL url = ofy().load().key(Key.create(URL.class, gistUrl)).now();

		if (url == null) {
			url = new URL(gistUrl);
		}

		ofy().save().entity(url).now();

		Key<Gist> gistKey = null; 

		if (content != null && content.length() > 0) {
			Date date = new Date();
			Gist gist = new Gist();
			gist.setUrl(url);;
			//     gist.setAuthor(userService.getCurrentUser().getEmail());
			gist.setDate(date);
			gist.setContent(content);
			gist.setGenre(genre);
			gistKey = ofy().save().entity(gist).now();
		}
		return ofy().load().key(gistKey).now();
	}
	
	  //TODO: Need to tell GsonMessageBody to ignore an object that is already JSON?
	  @GET
	  @Path("/{url}/extract")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getExtract(
	      @DefaultValue("default") @PathParam("url") final String url) throws
	    Exception {
		  String mcKey = url + "_extract";
		  return (String) mc.get(mcKey);
	  }
	  
	
	  //TODO: Need to tell GsonMessageBody to ignore an object that is already JSON?
	  @POST
	  @Path("/{url}/extract")
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String setExtract(
	      @DefaultValue("default") @PathParam("url") final String url,
	      final String extractData) throws
	    Exception {
		  String mcKey = url + "_extract";
		  
	      mc.put(mcKey, extractData, Expiration.byDeltaSeconds(CACHE_PERIOD),
	              SetPolicy.SET_ALWAYS);
	      
	      return extractData;
	  }
	  
	//  private List<Entity> getGistLikes(Key key, String cursor) {
	//	    List<Entity> likes = new ArrayList<Entity>();
	//	    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	//	    
	//	    int pageSize = 5;
	//	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
	//	    
	//	    if (cursor != null) {
	//	        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
	//	    }
	//
	//	    Query query = new Query("Like", key).addSort("date", Query.SortDirection.DESCENDING);
	//	    
	//	    PreparedQuery pq = datastoreService.prepare(query);
	//	 
	//	    QueryResultList<Entity> likeEntities = pq.asQueryResultList(fetchOptions);
	//	    
	//	    for (Entity like : likeEntities) {
	//	      likes.add(like);
	//	    }
	//	    
	//	    String cursorString = likeEntities.getCursor().toWebSafeString();
	//	    
	//	    return likeEntities; 
	//	  }
	
	  private List<Comment> getGistComments(Key<Gist> key, String cursor) {
		    List<Comment> comments = new ArrayList<Comment>();
		    Query<Comment> query = ofy().load().type(Comment.class).limit(5);
			if (key != null)
				query = query.filter("gist", key);
			
		    if (cursor != null)
		    	query = query.startAt(Cursor.fromWebSafeString(cursor));
		    
		    query = query.order("-created");
		    QueryResultIterator<Comment> iterator = query.iterator();
			while (iterator.hasNext()) {
				comments.add(iterator.next());
			}

			String nextCursor = iterator.getCursor().toWebSafeString();
			return comments;
	  }
	
	  
	  
	  @POST
	  @Path("/{url}/{id}/like")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Gist likeGist(
			  @PathParam("url") final String url, @PathParam("id") final String id,
			  final Map<String, String> postData) {
	    UserService userService = UserServiceFactory.getUserService();
		
		Long longId = Long.parseLong(id); 
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
	    	
		Date date = new Date();
		Like like = new Like();
		
		Gist gist = ofy().load().key(gistKey).now(); 
		
		like.setGist(gist);
		like.setCreated(date);
		
		ofy().save().entity(like).now();
		
	    return gist;
	  }
	  
	  
//	  @GET
//	  @Path("/{url}/{id}/likes")
//	  @Produces(MediaType.APPLICATION_JSON)
//	  public List<Like> getLikes(@PathParam("url") final String url, @PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
//	      Exception {
//			  Long longId = Long.parseLong(id);
//			  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
//			  Query<Gist> query = ofy().load().type(Like.class).limit(5);
//
//				if (url != null) {
//					Key<URL> urlKey = Key.create(URL.class, url);
//					query = query.filter("url", urlKey);
//				}
//				
//			  return ofy().load().key(Key.create(L.class, gistUrl)).now();
//	  }
	  
	  @POST
	  @Path("/{url}/{id}/comment")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Key<Comment> commentGist(
			  @PathParam("url") final String url, @PathParam("id") final String id,
			  final Map<String, String> postData) {
	    UserService userService = UserServiceFactory.getUserService();

		Long longId = Long.parseLong(id); 
		Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
	    	
		Date date = new Date();
		Comment comment = new Comment();
		
		Gist gist = ofy().load().key(gistKey).now(); 
		
		comment.setGist(gist);
		comment.setUser("user");
		comment.setComment(postData.get("comment"));
		comment.setCreated(date);
	    
		return ofy().save().entity(comment).now();
	  }
	  
	  @GET
	  @Path("/{url}/{id}/comments")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Comment> getComments(@PathParam("url") final String url, @PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
	  Exception {
		  Long longId = Long.parseLong(id);
		  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());

		  return getGistComments(gistKey, cursor);
	  }	
}
