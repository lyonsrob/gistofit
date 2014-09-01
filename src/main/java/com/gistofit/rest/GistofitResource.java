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

import com.gistofit.domain.Comment;
import com.gistofit.domain.CommentListResponse;
import com.gistofit.domain.GistDomain;
import com.gistofit.domain.GistListResponse;
import com.gistofit.domain.GistResponse;
import com.gistofit.domain.ShardedCounter;
import com.gistofit.domain.UserServiceInfo;
import com.gistofit.model.URL;
import com.gistofit.model.Gist;
//import com.googlecode.objectify.Key;


import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

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

@Path("/old/gists")
public class GistofitResource {

  private static final int CACHE_PERIOD = 86400;
	
  private final MemcacheService mc = MemcacheServiceFactory
	           .getMemcacheService();
	
  private final Logger logger = Logger.getLogger(GistofitResource.class.getName());
  
  private GistListResponse getGists(String url, String cursor) {
    List<GistDomain> gists = new ArrayList<GistDomain>();
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    int pageSize = 5;
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
    
    if (cursor != null) {
        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
    }
    
    Key urlKey;
    Query query;
    
    if (url != null) {
    	urlKey = KeyFactory.createKey("URL", url);
    	query = new Query("Gist", urlKey).addSort("date", Query.SortDirection.DESCENDING);
    } else {
    	query = new Query("Gist").addSort("date", Query.SortDirection.DESCENDING);
    }
    
    PreparedQuery pq = datastoreService.prepare(query);
 
    QueryResultList<Entity> gistEntities = pq.asQueryResultList(fetchOptions);
    
    for (Entity gist : gistEntities) {
      gists.add(GistDomain.fromEntity(gist));
    }
    
    String nextCursor = gistEntities.getCursor().toWebSafeString();
    
    return new GistListResponse(url, gists, UserServiceInfo.get("/"), nextCursor);
  }

  private GistListResponse getTrendingGists(String cursor) {
	    List<GistDomain> gists = new ArrayList<GistDomain>();
	    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	    
	    int pageSize = 5;
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
	    
	    if (cursor != null) {
	        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
	    }

	    Query query = new Query("Gist").addSort("likes", Query.SortDirection.DESCENDING);
	    
	    PreparedQuery pq = datastoreService.prepare(query);
	 
	    QueryResultList<Entity> gistEntities = pq.asQueryResultList(fetchOptions);
	    
	    for (Entity gist : gistEntities) {
	      gists.add(GistDomain.fromEntity(gist));
	    }
	    
	    String cursorString = gistEntities.getCursor().toWebSafeString();
	    
	    return new GistListResponse(null, gists, UserServiceInfo.get("/"), cursorString);
	  }

  
  private GistResponse getGist(Key key) {
	    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	    Entity gistEntity = null;

	    try {
	    	gistEntity = datastoreService.get(key);
	    } catch (EntityNotFoundException e) {
	           logger.log(Level.WARNING, "Entity not found.");
	           logger.log(Level.WARNING, e.toString(), e);
	    } catch (Exception e) {
	           logger.log(Level.WARNING, e.toString(), e);
	    }
	    
	    return new GistResponse(GistDomain.fromEntity(gistEntity), UserServiceInfo.get("/"));
	  }
  
  @GET
  @Path("/recent")
  @Produces(MediaType.APPLICATION_JSON)
  public GistListResponse getRecent(@QueryParam("cursor") final String cursor) throws
      Exception {
	  	return getGists(null, cursor);
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
  public GistResponse getSingleGist(
      @DefaultValue("default") @PathParam("url") final String url,
      @PathParam("id") final String id) throws
      Exception {
	  	Long longId = Long.parseLong(id); 
		Key urlKey = KeyFactory.createKey("URL", url);
	    Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
	    return getGist(idKey);
  	}
  
  @POST
  @Path("/{url}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GistListResponse addGist(
      @DefaultValue("default") @PathParam("url") final String gistUrl,
      final Map<String, String> postData) {
    UserService userService = UserServiceFactory.getUserService();
    
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Key urlKey = KeyFactory.createKey("URL", gistUrl);
    // We set the above parent key on each Greeting entity in order to make the queries strong
    // consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
    // rate more than 1 write/second.
    String content = postData.get("content");
    String genre = postData.get("genre");
    
//    URL url = new URL(gistUrl);
//    ofy().save().entity(url).now();
//    
//    Key<URL> parent = Key.create(URL.class, url);
    
    if (content != null && content.length() > 0) {
      Date date = new Date();
      Entity gist = new Entity("Gist", urlKey);
      gist.setProperty("user", userService.getCurrentUser());
      gist.setProperty("date", date);
      gist.setProperty("content", content);
      gist.setProperty("genre", genre);
      gist.setProperty("likes", 0);
      datastoreService.put(gist);
    }
    return getGists(gistUrl, null);
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
  
  private List<Entity> getGistLikes(Key key, String cursor) {
	    List<Entity> likes = new ArrayList<Entity>();
	    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	    
	    int pageSize = 5;
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
	    
	    if (cursor != null) {
	        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
	    }

	    Query query = new Query("Like", key).addSort("date", Query.SortDirection.DESCENDING);
	    
	    PreparedQuery pq = datastoreService.prepare(query);
	 
	    QueryResultList<Entity> likeEntities = pq.asQueryResultList(fetchOptions);
	    
	    for (Entity like : likeEntities) {
	      likes.add(like);
	    }
	    
	    String cursorString = likeEntities.getCursor().toWebSafeString();
	    
	    return likeEntities; 
	  }

  private List<Entity> getGistComments(Key key, String cursor) {
	    List<Entity> comments = new ArrayList<Entity>();
	    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
	    
	    int pageSize = 5;
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
	    
	    if (cursor != null) {
	        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
	    }

	    Query query = new Query("Comment", key).addSort("date", Query.SortDirection.DESCENDING);
	    
	    PreparedQuery pq = datastoreService.prepare(query);
	 
	    QueryResultList<Entity> commentEntities = pq.asQueryResultList(fetchOptions);
	    
	    for (Entity comment : commentEntities) {
	      comments.add(comment);
	    }
	    
	    String cursorString = commentEntities.getCursor().toWebSafeString();
	    
	    return commentEntities; 
	  }
  
  @POST
  @Path("/{url}/{id}/like")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GistResponse likeGist(
		  @PathParam("url") final String url, @PathParam("id") final String id,
		  final Map<String, String> postData) {
    UserService userService = UserServiceFactory.getUserService();
	DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

	// We set the above parent key on each Greeting entity in order to make the queries strong
	// consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
	// rate more than 1 write/second. Date date = new Date();

	Long longId = Long.parseLong(id); 
	Key urlKey = KeyFactory.createKey("URL", url);
	
    ShardedCounter likeCounter = new ShardedCounter("Likes");
    Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
    

	Date date = new Date();
	Entity like = new Entity("Like", idKey);
	like.setProperty("user", userService.getCurrentUser());
	like.setProperty("date", date);
	datastoreService.put(like);
	
    likeCounter.incrementPropertyTx(idKey, "likes", 1, 1);
    
    return getGist(idKey);
  }
  
  @GET
  @Path("/{url}/{id}/likes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Entity> getLikes(@PathParam("url") final String url, @PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
      Exception {
		  Long longId = Long.parseLong(id); 
		  Key urlKey = KeyFactory.createKey("URL", url);
		  Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
		  return getGistLikes(idKey, cursor);
  }
  
  @POST
  @Path("/{url}/{id}/comment")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String commentGist(
		  @PathParam("url") final String url, @PathParam("id") final String id,
		  final Map<String, String> postData) {
    UserService userService = UserServiceFactory.getUserService();
	DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

	// We set the above parent key on each Greeting entity in order to make the queries strong
	// consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
	// rate more than 1 write/second. Date date = new Date();

	Long longId = Long.parseLong(id); 
	Key urlKey = KeyFactory.createKey("URL", url);
    Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
    
    String content = postData.get("comment");
    
	Date date = new Date();
	Entity comment = new Entity("Comment", idKey);
	comment.setProperty("user", userService.getCurrentUser());
	comment.setProperty("comment", content);
	comment.setProperty("date", date);
	datastoreService.put(comment);

	   
	return content;
  }
  


@GET
@Path("/{url}/{id}/comments")
@Produces(MediaType.APPLICATION_JSON)
public CommentListResponse getComments(@PathParam("url") final String url, @PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
    Exception {
		  List<Comment> comments = new ArrayList<Comment>();
		  List<Entity> commentEntities = new ArrayList<Entity>();
		  Long longId = Long.parseLong(id); 
		  Key urlKey = KeyFactory.createKey("URL", url);
		  Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
		  
		  commentEntities = getGistComments(idKey, cursor);
		  
		  for (Entity comment : commentEntities) {
		      comments.add(Comment.fromEntity(comment));
		    }
		  
		  return new CommentListResponse(null, comments, null, null);
}
}
