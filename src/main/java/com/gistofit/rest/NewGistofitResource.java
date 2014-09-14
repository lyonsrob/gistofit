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
import java.util.LinkedHashSet;
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
import com.gistofit.model.EmbedlyExtract;
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
import com.google.appengine.api.search.Index;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gistofit.domain.GistSearch;

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

	public GistListResponse getGists(String url, String cursor) {
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
		String lastSeen = gists.get(0).id.toString();
		
		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Gist> searchGists(@QueryParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> urls = gistSearch.getUrls(keyword, 15);
		List<Gist> gists = new ArrayList<Gist>();
		
		for (String url : urls) {
			gists.addAll(getGists(url, null).getGists());
		}
		
		return gists;
	}
	
	@GET
	@Path("/search/top/keywords")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashSet<String> searchKeywords(@QueryParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> top = gistSearch.getKeywords(keyword, 15);
		return top;
	}
	
	@GET
	@Path("/search/top/urls")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashSet<String> searchTopUrls(@QueryParam("keyword") final String keyword) throws
	Exception {
		GistSearch gistSearch = new GistSearch();
		LinkedHashSet<String> top = gistSearch.getUrls(keyword, 15);
		return top;
	}
	
	@GET
	@Path("/newest")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getNewest(@QueryParam("last_seen") final String lastSeen) throws
	Exception {
		return getNewestGists(lastSeen);
	}
	
	private GistListResponse getNewestGists(String lastSeen) {
		if (lastSeen == null)
			return new GistListResponse(null, null, null, null);

		Query<Gist> query = ofy().load().type(Gist.class).filterKey(">", Gist.key(Long.parseLong(lastSeen))).limit(5).order("-__key__");
		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}
		
		String nextCursor = null;
		
		if (gists.size() > 0) {
			nextCursor = iterator.getCursor().toWebSafeString();
			lastSeen = gists.get(0).id.toString();
		}
		
		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
	}

	
	@GET
	@Path("/recent")
	@Produces(MediaType.APPLICATION_JSON)
	public GistListResponse getRecent(@QueryParam("cursor") final String cursor) throws
	Exception {
		return getGists(null, cursor);
	}

	private GistListResponse getTrendingGists(String cursor) {
		Query<Gist> query = ofy().load().type(Gist.class).limit(15);

		if (cursor != null)
			query = query.startAt(Cursor.fromWebSafeString(cursor));

		QueryResultIterator<Gist> iterator = query.iterator();
		List<Gist> gists = new ArrayList<Gist>();
		while (iterator.hasNext()) {
			gists.add(iterator.next());
		}

		String nextCursor = iterator.getCursor().toWebSafeString();
		String lastSeen = gists.get(0).id.toString();
		
		return new GistListResponse(gists, UserServiceInfo.get("/"), nextCursor, lastSeen);
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

		URL url = ofy().load().key(Key.create(URL.class, gistUrl)).now();

		if (url == null) {
			url = new URL(gistUrl);
			ofy().save().entity(url).now();
		}
		
		Key<Gist> gistKey = null; 

		if (content != null && content.length() > 0) {
			Date date = new Date();
			Gist gist = new Gist();
			gist.setUrl(url);;
			//     gist.setAuthor(userService.getCurrentUser().getEmail());
			gist.setDate(date);
			gist.setContent(content);
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
	      @DefaultValue("default") @PathParam("url") final String urlString,
	      final String extractData) throws
	    Exception {
			JsonParser parser = new JsonParser();
			JsonObject obj = (JsonObject)parser.parse(extractData);
			Gson gson = new GsonBuilder()
		    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		    .create();

			EmbedlyExtract extract = gson.fromJson(obj, EmbedlyExtract.class);
			
			String cleanUrl = extract.getUrl();
			URL url = ofy().load().key(Key.create(URL.class, cleanUrl)).now();
			
			if (url == null) {
				url = new URL(cleanUrl);
				ofy().save().entity(url).now();
				
				GistSearch search = new GistSearch();
				Index index = GistSearch.getIndex();
					
				for (EmbedlyExtract.Keyword keyword : extract.getKeywords()) {
					index.put(search.buildDocument(keyword.getName(), cleanUrl, keyword.getScore()));
				}
				
				for (EmbedlyExtract.Entity entity : extract.getEntities()) {
					index.put(search.buildDocument(entity.getName(), cleanUrl, entity.getCount()));		
				}			
			}

		  
		  String mcKey = cleanUrl + "_extract";
	      mc.put(mcKey, extractData, Expiration.byDeltaSeconds(CACHE_PERIOD),
	              SetPolicy.SET_ALWAYS);
	      mcKey = extract.getOriginalUrl() + "_extract";
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
	  @Path("/{id}/like")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Gist likeGist(
			  @PathParam("id") final String id,
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
	  
	  
	  @GET
	  @Path("/{id}/likes")
	  @Produces(MediaType.APPLICATION_JSON)
	  public List<Like> getLikes(@PathParam("id") final String id, @QueryParam("cursor") final String cursor) throws
	      Exception {
			  Long longId = Long.parseLong(id);
			  Key<Gist> gistKey = Key.create(Gist.class, longId.longValue());
			  Query<Like> query = ofy().load().type(Like.class).filter("gist", gistKey).order("-created");
			  
			  return query.list();  
	  }
	  
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