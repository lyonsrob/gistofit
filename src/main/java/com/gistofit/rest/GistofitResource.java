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

import com.gistofit.domain.Gist;
import com.gistofit.domain.GistListResponse;
import com.gistofit.domain.GistResponse;
import com.gistofit.domain.ShardedCounter;
import com.gistofit.domain.UserServiceInfo;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONArray;

import com.embedly.api.Api;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo [tmatsuo@google.com]
 * Date: 4/4/13
 * Time: 11:58 PM
 */

@Path("/gists")
public class GistofitResource {

  private static final int CACHE_PERIOD = 86400;
	
  private final MemcacheService mc = MemcacheServiceFactory
	           .getMemcacheService();
	
  private final Logger logger = Logger.getLogger(GistofitResource.class.getName());
  
  private GistListResponse getGists(String url, String cursor) {
    List<Gist> gists = new ArrayList<Gist>();
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    int pageSize = 5;
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
    
    if (cursor != null) {
        fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
    }
    
    Key urlKey;
    Query query;
    
    if (url != null) {
    	urlKey = KeyFactory.createKey("Gist", url);
    	query = new Query("Gist", urlKey).addSort("date", Query.SortDirection.DESCENDING);
    } else {
    	query = new Query("Gist").addSort("date", Query.SortDirection.DESCENDING);
    }
    
    PreparedQuery pq = datastoreService.prepare(query);
 
    QueryResultList<Entity> gistEntities = pq.asQueryResultList(fetchOptions);
    
    for (Entity gist : gistEntities) {
      gists.add(Gist.fromEntity(gist));
    }
    
    String cursorString = gistEntities.getCursor().toWebSafeString();
    
    return new GistListResponse(url, gists, UserServiceInfo.get("/"), cursorString);
  }

  private GistListResponse getTrendingGists(String cursor) {
	    List<Gist> gists = new ArrayList<Gist>();
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
	      gists.add(Gist.fromEntity(gist));
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
	    
	    String content= (String) gistEntity.getProperty("content");
	    
	    return new GistResponse(Gist.fromEntity(gistEntity), UserServiceInfo.get("/"));
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
		Key urlKey = KeyFactory.createKey("Gist", url);
	    Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
	    return getGist(idKey);
  	}
  
  @POST
  @Path("/{url}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GistListResponse addGist(
      @DefaultValue("default") @PathParam("url") final String url,
      final Map<String, String> postData) {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Key urlKey = KeyFactory.createKey("Gist", url);
    // We set the above parent key on each Greeting entity in order to make the queries strong
    // consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
    // rate more than 1 write/second.
    String content = postData.get("content");
    String genre = postData.get("genre");
    
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
    return getGists(url, null);
  }

  //TODO: Need to tell GsonMessageBody to ignore an object that is already JSON?
  @GET
  @Path("/{url}/extract")
  @Produces(MediaType.TEXT_PLAIN)
  public String getExtract(
      @DefaultValue("default") @PathParam("url") final String url, 
      	@QueryParam("width") final String width, 
      	@QueryParam("height") final String height, 
      	@QueryParam("retina") final String retina) throws
    Exception {
	  String mcKey = url + "_extract";
	  String cachedExtract = (String) mc.get(mcKey);
      if (cachedExtract != null) {
          return cachedExtract;
      }
	  
	  Api api = new Api("Mozilla/5.0 (compatible; gistofit/1.0; lyonsrobp@gmail.com)", "42f4925174814d68b90d0758d932fe14");
	  HashMap<String, Object> params = new HashMap<String, Object>();
      params.put("url", url);
/*
      params.put("image_error_url", "http%3A%2F%2Fmedia.tumblr.com%2Ftumblr_m9e0vfpA7K1qkbsaa.jpg");
      
      String device = postData.get("device");
      
      if (device == "mobile") {
    	  params.put("image_height", "300");
      	  params.put("image_width", "300");
      	  params.put("image_method", "fill");
      }
*/
      
      JSONObject extractJSON = api.extract(params).getJSONObject(0);
      
      String embedlyExtract = extractJSON.toString();
      
      //Cache both the original URL that the RSS feed or user entered, plus the normalized URL from embed.ly
      mc.put(mcKey, embedlyExtract, Expiration.byDeltaSeconds(CACHE_PERIOD),
              SetPolicy.SET_ALWAYS);
      mc.put(extractJSON.get("url").toString() + "_extract", embedlyExtract, Expiration.byDeltaSeconds(CACHE_PERIOD),
              SetPolicy.SET_ALWAYS);
      
      return embedlyExtract;
  }

  
  @POST
  @Path("/{url}/{id}/like")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GistResponse likeGist(
		  @PathParam("url") final String url, @PathParam("id") final String id) {
//    UserService userService = UserServiceFactory.getUserService();

    // We set the above parent key on each Greeting entity in order to make the queries strong
    // consistent. Please Note that as a trade off, we can not write to a single #gistofit at a
    // rate more than 1 write/second. Date date = new Date();
    
	Long longId = Long.parseLong(id); 
	Key urlKey = KeyFactory.createKey("Gist", url);
	
    ShardedCounter likeCounter = new ShardedCounter("Likes");
    Key idKey = KeyFactory.createKey(urlKey, "Gist", longId);
    likeCounter.incrementPropertyTx(idKey, "likes", 1, 1);
    
    return getGist(idKey);
  }
}
