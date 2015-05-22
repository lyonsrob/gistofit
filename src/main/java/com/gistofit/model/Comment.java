package com.gistofit.model;


import java.beans.Transient;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
public class Comment extends Jsonifiable {
	
  @Index
  @Load Ref<Gist> gist;
  
  @Index
  @Load Ref<User> user;
	  
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
  String content;
  
  @Index
  @Getter
  @Setter
  Date created = new Date();
  
  @Getter
  @Setter
  @Ignore String nextCursor; 
  
  public Gist getGist() { return gist.get(); }
  public void setGist(Gist value) { gist = Ref.create(value); }
  
  public User getUser() { return user.get(); }
  public void setUser(User value) { user = Ref.create(value); }
}