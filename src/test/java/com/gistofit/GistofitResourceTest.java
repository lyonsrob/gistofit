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

package com.gistofit;

import static com.gistofit.TestUtil.getMockedJsonRequest;
import static com.gistofit.TestUtil.getMockedServletResponse;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import com.gistofit.config.Resources;
import com.gistofit.domain.Gist;
import com.gistofit.domain.GistListResponse;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Takashi Matsuo <tmatsuo@google.com>
 * Date: 4/7/13
 * Time: 9:36 AM
 */

public class GistofitResourceTest {

  private static final LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
      new LocalUserServiceTestConfig())
      .setEnvIsLoggedIn(true)
      .setEnvAuthDomain("example.com")
      .setEnvEmail("test@example.com");

  private ServletContainer servletContainer;

  private final Logger logger = Logger.getLogger(GistofitResourceTest.class.getName());

  @Before
  public void setUp() throws ServletException {
    helper.setUp();
    // Create a jersey ServletContainer object with mocked ServletConfig and ServletContext.
    servletContainer = new ServletContainer(Resources.class);
    ServletConfig servletConfig = mock(ServletConfig.class);
    ServletContext servletContext = mock(ServletContext.class);
    when(servletConfig.getServletContext()).thenReturn(servletContext);
    servletContainer.init(servletConfig);
  }

  @After
  public void tearDownDatastoreHelper() {
    helper.tearDown();
  }

/*
  @Test
  public void testGetRecentGists() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    final StringBuffer resultBuffer = new StringBuffer();
    HttpServletResponse response = getMockedServletResponse(resultBuffer);

    // Since jersey looks up the HTTP method and headers from the request, we mock those 2 calls.
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());

    servletContainer.service(URI.create("http://localhost/"), URI.create("/default"),
        request, response);
    logger.fine(resultBuffer.toString().trim());
    Gson gson = new GsonBuilder().create();
    GistListResponse result = gson.fromJson(resultBuffer.toString().trim(),
        GistListResponse.class);
    
    assertThat(result.getGists().getGists().size(), is(0));
    assertThat(result.getUserServiceInfo().getCurrentUser().getEmail(), is("test@example.com"));
    assertThat(result.getUserServiceInfo().getLoginUrl(), is("/_ah/login?continue=%2F"));
    assertThat(result.getUserServiceInfo().getLogoutUrl(), is("/_ah/logout?continue=%2F"));
  }
  
  @Test
  public void testGetGistOfItData() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    final StringBuffer resultBuffer = new StringBuffer();
    HttpServletResponse response = getMockedServletResponse(resultBuffer);

    // Since jersey looks up the HTTP method and headers from the request, we mock those 2 calls.
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());

    servletContainer.service(URI.create("http://localhost/"), URI.create("/default"),
        request, response);
    logger.fine(resultBuffer.toString().trim());
    Gson gson = new GsonBuilder().create();
    GistListResponse result = gson.fromJson(resultBuffer.toString().trim(),
        GistListResponse.class);
    assertThat(result.getURL(), is("default"));
    assertThat(result.getGists().getGists().size(), is(0));
    assertThat(result.getUserServiceInfo().getCurrentUser().getEmail(), is("test@example.com"));
    assertThat(result.getUserServiceInfo().getLoginUrl(), is("/_ah/login?continue=%2F"));
    assertThat(result.getUserServiceInfo().getLogoutUrl(), is("/_ah/logout?continue=%2F"));
  }

  @Test
  public void testAddGist() throws ServletException, IOException {
    HttpServletRequest request = getMockedJsonRequest("{content:\"Test message\"}");
    final StringBuffer resultBuffer = new StringBuffer();
    HttpServletResponse response = getMockedServletResponse(resultBuffer);

    servletContainer.service(URI.create("http://localhost/"), URI.create("/default"),
        request, response);
    logger.fine(resultBuffer.toString().trim());
    Gson gson = new GsonBuilder().create();
    GistListResponse result = gson.fromJson(resultBuffer.toString().trim(),
        GistListResponse.class);
    assertThat(result.getURL(), is("default"));
    assertThat(result.getGists().getGists().size(), is(1));
    Gist gist = result.getGists().getGists().get(0);
    assertThat(gist.getContent(), is("Test message"));
    assertThat(gist.getAuthor(), is("test@example.com"));
    assertThat(gist.getDate(), instanceOf(Date.class));
    assertThat(result.getUserServiceInfo(), nullValue());
  }
*/
}
