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

import java.util.List;

import com.gistofit.model.Comment;

public class CommentListResponse {

  private final List<Comment> comments;
  
  private final String nextCursor;
  
  private final Long lastSeen;

public CommentListResponse(List<Comment> comments,
                           String nextCursor, Long lastSeen) {
    this.comments = comments;
    this.nextCursor = nextCursor;
    this.lastSeen = lastSeen;
  }

  public List<Comment> getGists() {
    return comments;
  }
  
  public String getNextCursor() {
	  return nextCursor;
  }
  
  public Long getLastSeen() {
	return lastSeen;
}

}