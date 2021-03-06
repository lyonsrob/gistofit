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

package com.gistofit.domain;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo <tmatsuo@google.com>
 * Date: 4/5/13
 * Time: 1:35 AM
 */
public class GistDomain {

  private final String url;
  
  private final String content;

  private final Date date;

  private final String author;
  
  private final String genre;
  
  private final Long id;
  
  private final Long likes;

  public static GistDomain fromEntity(Entity gistEntity) {
    String author;
    User user = (User) gistEntity.getProperty("user");
    if (user == null) {
      author = "an anonymous user";
    } else {
      author = user.getEmail();
    }
    return new GistDomain((String) gistEntity.getKey().getParent().getName(), (String) gistEntity.getProperty("content"),
        (Date) gistEntity.getProperty("date"), author, gistEntity.getKey().getId(), (String) gistEntity.getProperty("genre"), (Long) gistEntity.getProperty("likes"));
  }

  public GistDomain(String url, String content, Date date, String author, Long id, String genre, Long likes) {
	this.url = url;
	this.content = content;
    this.date = date;
    this.author = author;
    this.id = id;
    this.genre = genre;
    this.likes = likes;
}

  public String getUrl() {
	    return url;
	  }
  
  public String getContent() {
    return content;
  }

  public Date getDate() {
    return date;
  }

  public String getAuthor() {
    return author;
  }
  
  public Long getId() {
	    return id;
	  }
  
  public String getGenre() {
	    return genre;
	  }
  
  public Long getLikes() {
	    return likes;
	  }
}
