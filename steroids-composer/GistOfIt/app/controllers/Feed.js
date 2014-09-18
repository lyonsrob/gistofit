'use strict'

function toArrayObj(array) {
    for (var i = 0; i < array.length; ++i)
        array[i] = {link: array[i]};
    return array;
}

angular.module('gistOfItApp').controller('FeedCtrl', ['$scope', '$q', 'GistofitService', 'embedlyService', 'FeedService', 
  function ($scope, $q, Gistofit, Embedly, Feed) {
    // Create a view
    var myView = new steroids.views.WebView("http://localhost/views/Article/article.html");
    myView.preload(); // Prelaod for faster view transitions
        
    var createGistView = new steroids.views.WebView("views/Gist/add.html");
    createGistView.preload(); // Prelaod for faster view transitions

    var feedURLs = [
        'http://feeds2.feedburner.com/Mashable',
        'http://www.tmz.com/rss.xml',
        'http://feeds.gawker.com/deadspin/full',
        'http://feeds.gawker.com/gizmodo/full',
        'http://feeds2.feedburner.com/businessinsider',
        'http://feeds.feedburner.com/TechCrunch',
        'http://rss.cnn.com/rss/cnn_topstories.rss',
        'http://sports.espn.go.com/espn/rss/news'
    ];

    $scope.setExtract = function(feed) {
        var url = feed.link;

        Gistofit.getExtract(url).then(function (response) {
            if (response.data == undefined || response.data == '') {
                Embedly.extract(url)
                .then(function(e){
                    Gistofit.setExtract(url, e.data);
                    feed.extract = e.data;
                },
                 function(error) {
                    console.log(error);
                 });
            } else {
                feed.extract = response.data;
            }
            
            feed.extract.video = feed.extract.embeds[0].html;
        });
    }

    $scope.loadFeed=function(e){        
        Feed.parseFeed($scope.feedSrc).then(function(res){
            $scope.loadButonText=angular.element(e.target).text();
            $scope.feeds=res.data.responseData.feed.entries;
        });
    }
    
    $scope.loadSearchFeed=function(){        
        Gistofit.searchTopUrls($scope.searchText).then(function(res){
            $scope.loadButonText=angular.element().text();
            $scope.feeds=toArrayObj(res.data);
        });
    }

    $scope.loadAllFeeds=function(e){      
        $scope.feeds = [];
        var promises = [];

        for (var i = 0, len = feedURLs.length; i < len; i++) {
            Feed.parseFeed(feedURLs[i]).then(function(res){
                //$scope.loadButonText=angular.element(e.target).text();
                $scope.feeds.push.apply($scope.feeds, res.data.responseData.feed.entries);
            
                angular.forEach($scope.feeds,function(feed){
                    promises.push($scope.setExtract(feed));
                });

                $q.all(promises).then(function success(data){
                    //console.log($scope.feeds); // Should all be here
                }, function failure(err){
                    // Can handle this is we want
                });
                });
        }
            //shuffle($scope.feeds);
    }

    $scope.showArticle = function(article) {
        var message = {
            recipient: "articleView",
            article: article,
        }
        window.postMessage(message);
        
        var fastSlide = new steroids.Animation({  transition: "slideFromRight",  duration: .2});

        // Navigate to your view
        steroids.layers.push(
        {
            view: myView,
            animation: fastSlide ,
        });
    }
    
    $scope.showGistPrompt = function(feed) {
        var message = {
            recipient: "gistModalView",
            feed: feed
        }
       
        window.postMessage(message);
        steroids.modal.show(createGistView);
    }
    
    $scope.loadAllFeeds();
    steroids.view.navigationBar.show("Feed");
}]);

angular.module('gistOfItApp').factory('FeedService',['$http',function($http){
    return {
        parseFeed : function(url){
            return $http.jsonp('https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=1&callback=JSON_CALLBACK&q=' + encodeURIComponent(url));
        }
    }
}]);

