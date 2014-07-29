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

'use strict';

angular.module('guestbook').controller('GistCtrl', ['$scope', '$http', 'GistofitService', 
  function ($scope, $http, Gistofit) {

    $scope.loadRecentGists = function() {
        var url = 'http://localhost:8080/rest/gists/recent';

        Gistofit.getGists(url).then(function (response) {
            console.log(response);
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.cursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }
    
    $scope.loadTrendingGists = function() {
        var url = 'http://localhost:8080/rest/gists/trending';

        console.log("trending");

        Gistofit.getGists(url).then(function (response) {
            console.log(response);
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.cursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }

    $scope.submit_form = function () {
      $http.post(
          'http://localhost:8080/rest/gists/' + encodeURIComponent($scope.gistURL),
          {'content': $scope.content, 'genre': $scope.genre})
          .success(function (data) {
            $scope.content = '';
            $scope.genre = '';
            $scope.greetings = data.greetings;
            $scope.gistURL = data.gistURL;
            $scope.currentGuestbookName = data.gistURL;
          })
          .error(function(data, status) {
            console.log('Posting a message failed with the status code: ' + status);
            console.log(data);
          });
    };
    
    $scope.like_gist = function (url, id) {
        console.log(url);
        $http.post(
          'http://localhost:8080/rest/gists/' + encodeURIComponent(url) + "/" + id + "/like",{})
          .success(function (data) {
            $scope.content = '';
            $scope.genre = '';
            $scope.greetings = data.greetings;
            $scope.gistURL = data.gistURL;
            $scope.currentGuestbookName = data.gistURL;
          })
          .error(function(data, status) {
            console.log('Liking a gist failed with the status code: ' + status);
            console.log(data);
          });
    };

    $scope.loadRecentGists();
}    
]);

angular.module('guestbook').factory('GistofitService', ['$http', function ($http) {
    return {
        getGists: function (url) {
            return $http.get(url);
        }
    }
}]);



angular.module('guestbook').controller("FeedCtrl", ['$scope','FeedService', function ($scope,Feed) {    
    $scope.loadButonText="Load";
    $scope.loadFeed=function(e){        
        Feed.parseFeed($scope.feedSrc).then(function(res){
            console.log(res);
            $scope.loadButonText=angular.element(e.target).text();
            $scope.feeds=res.data.responseData.feed.entries;
        });
    }
}]);

angular.module('guestbook').factory('FeedService',['$http',function($http){
    return {
        parseFeed : function(url){
            return $http.jsonp('//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=50&callback=JSON_CALLBACK&q=' + encodeURIComponent(url));
        }
    }
}]);
