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

package com.gistofit.model;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
@EqualsAndHashCode(of="id", callSuper=false)
public class Gist {

  @Expose
  public static String kind = "Gist";

  @Index
  @Load Ref<URL> url;
  
  public static Key<Gist> key(long id) {
	    return Key.create(Gist.class, id);
  }
  
  /**
   * Primary identifier of this Gist.
   */
  @Id
  @Getter
  @Expose
  public Long id;
  
  @Getter
  @Setter
  @Expose
  public String content;

  @Getter
  @Setter
  @Index
  @Expose
  public Date date = new Date();

  @Getter
  @Setter
  @Index
  @Expose
  public String author;
  
  @Getter
  @Setter
  @Index
  @Expose
  public String genre;
  
  public URL getUrl() { return url.get(); }
  public void setUrl(URL value) { url = Ref.create(value); }
}
