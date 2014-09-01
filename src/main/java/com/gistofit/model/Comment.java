package com.gistofit.model;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
public class Comment extends Jsonifiable {

  @Index
  @Load Ref<Gist> gist;
	  
  public static Key<Comment> key(long id) {
	    return Key.create(Comment.class, id);
  }
  
  /**
   * Primary identifier of this Comment.  Specific to GistOfIt.
   */
  @Id
  @Getter
  @Expose
  public Long id;

  @Getter
  @Setter
  String comment;
  
  @Index
  @Getter
  @Setter
  String user;
  
  @Index
  @Getter
  @Setter
  Date created;
  
  public Gist getGist() { return gist.get(); }
  public void setGist(Gist value) { gist = Ref.create(value); }
}