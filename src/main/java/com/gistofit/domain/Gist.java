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
public class Gist {

  private final String url;
  
  private final String content;

  private final Date date;

  private final String author;

  public static Gist fromEntity(Entity gistEntity) {
    String author;
    User user = (User) gistEntity.getProperty("user");
    if (user == null) {
      author = "an anonymous user";
    } else {
      author = user.getEmail();
    }
    return new Gist((String) gistEntity.getKey().getParent().getName(), (String) gistEntity.getProperty("content"),
        (Date) gistEntity.getProperty("date"), author);
  }

  public Gist(String url, String content, Date date, String author) {
	this.url = url;
	this.content = content;
    this.date = date;
    this.author = author;
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
}
