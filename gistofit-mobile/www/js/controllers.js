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

        Gistofit.getRecent().then(function (response) {
            console.log(response);
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.cursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }
    
    $scope.loadTrendingGists = function() {
        Gistofit.getTrending().then(function (response) {
            console.log(response);
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.cursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }
    
    $scope.likeGist = function(url, id) {
        Gistofit.likeGist(url, id).then(function (response) {
            console.log(response);
          });
    }

    $scope.load_extract_content = function (content) {
        console.log(content);
        $scope.extract_content = content;
    };
    
    $scope.detectViewport = function() {
		$scope.screenWidth = window.innerWidth,
		$scope.screenHeight = window.innerHeight;
		
		// Retina detect
		if(window.devicePixelRatio >= 2) {
			$scope.retina = 1;
		} else {
			$scope.retina = 0;
		}
    }

    $scope.detectViewport();
    $scope.loadRecentGists();
}    
]);

angular.module('guestbook').factory('GistofitService', ['$http', function ($http) {
    var domain = 'http://localhost:8080/';

    function buildURL (method) {
        return domain + method;
    }

    return {
        getRecent: function () {
            var url = buildURL('rest/gists/recent');
            return $http({method: 'GET', url: url});
        },
        getTrending: function () {
            var url = buildURL('rest/gists/trending');
            return $http({method: 'GET', url: url});
        },
        getExtract: function (inputUrl, width, height, retina) {
            var escapedUrl = encodeURIComponent(inputUrl);
            var url = buildURL('rest/gists/'+ escapedUrl + '/extract'); 
            return $http({method: 'GET', url: url});
        },
        addGist: function (url, content) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url));
            var data = {'content': content};
            return $http.post(url, data);
        },
        likeGist: function (url, id) {
            url = buildURL ('rest/gists/' + encodeURIComponent(url) + "/" + id + "/like");
            return $http.post(url, {});
        },
    }
}]);

angular.module('guestbook').controller("FeedCtrl", ['$scope','FeedService', function ($scope,Feed) {    
    var feedURLs = [
        'http://feeds2.feedburner.com/Mashable',
        'http://www.tmz.com/rss.xml',
        'http://feeds.gawker.com/deadspin/full',
        'http://feeds.gawker.com/gizmodo/full',
        'http://feeds2.feedburner.com/businessinsider',
        'http://feeds.feedburner.com/TechCrunch',
        'http://rss.cnn.com/rss/cnn_topstories.rss',
        'http://sports.espn.go.com/espn/rss/news'
    ]


    $scope.loadButonText="Load";
    $scope.loadFeed=function(e){        
        Feed.parseFeed($scope.feedSrc).then(function(res){
            console.log(res);
            $scope.loadButonText=angular.element(e.target).text();
            $scope.feeds=res.data.responseData.feed.entries;
        });
    }

function shuffle(array) {
  var currentIndex = array.length
    , temporaryValue
    , randomIndex
    ;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
} 

    $scope.loadAllFeeds=function(e){      
        $scope.feeds = [];
        for (var i = 0, len = feedURLs.length; i < len; i++) {
            Feed.parseFeed(feedURLs[i]).then(function(res){
                console.log(res);
                //$scope.loadButonText=angular.element(e.target).text();
                if (i>0) {
                    $scope.feeds.push.apply($scope.feeds, res.data.responseData.feed.entries);
                }
            });
        }
            shuffle($scope.feeds);
    }

    $scope.loadAllFeeds();
}]);

angular.module('guestbook').factory('FeedService',['$http',function($http){
    return {
        parseFeed : function(url){
            return $http.jsonp('https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=2&callback=JSON_CALLBACK&q=' + encodeURIComponent(url));
        }
    }
}]);

angular.module('guestbook').controller("PageCtrl", ['$scope', function ($scope) {    
    ons.ready(function() {
      console.log("ONS READY");
      //Check if it already exists or not
      console.log(show_welcome);
      
      if(show_welcome){
        var options = {
            animation: 'lift'
        };
        navigator1.pushPage('welcome.html', options); 
        console.log("Launch Count!");
        console.log($scope.tabs);
        tabs.setTabbarVisibility(false);
          //This is a second time launch, and count = applaunchCount
      }
    });
}]);
