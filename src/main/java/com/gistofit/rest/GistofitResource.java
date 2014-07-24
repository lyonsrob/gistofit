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
import com.gistofit.domain.UserServiceInfo;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.*;
import java.util.logging.Logger;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo [tmatsuo@google.com]
 * Date: 4/4/13
 * Time: 11:58 PM
 */

@Path("/")
public class GistofitResource {

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
    	urlKey = KeyFactory.createKey("URL", url);
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

  @GET
  @Path("/recent")
  @Produces(MediaType.APPLICATION_JSON)
  public GistListResponse getRecentGists(@QueryParam("cursor") final String cursor) throws
      Exception {
	  	return getGists(null, cursor);
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

  @POST
  @Path("/{url}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GistListResponse addGist(
      @DefaultValue("default") @PathParam("url") final String url,
      final Map<String, String> postData) {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Key urlKey = KeyFactory.createKey("URL", url);
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
}
