package com.gistofit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
@EqualsAndHashCode(of="url", callSuper=false)
public class URL extends Jsonifiable {

  public static Key<URL> key(String url) {
	    return Key.create(URL.class, url);
  }
  
  /**
   * Primary identifier of this URL.  Specific to GistOfIt.
   */
  @Id
  @Getter
  @Expose
  public String url;
  
  @Getter
  @Setter
  @Expose
  public String mainEntity;

  @Getter
  @Setter
  @Index
  @Expose
  public Date mainKeyword;

  @Getter
  @Setter
  @Index
  @Expose
  public String provider;
  
  @Getter
  @Setter
  @Index
  @Expose
  public String genre;

  @Getter
  @Setter
  @Expose
  public String articleAuthor;
  
  @Index
  @Getter
  Date created;
  
  List<Key<Gist>> gists = new ArrayList<Key<Gist>>();
  
  public URL(String url) {
	this.url = url;
	this.created = new Date();
  }
  
  public URL() {
  }
  
}