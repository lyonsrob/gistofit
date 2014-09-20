'use strict'

angular.module('gistOfItApp').controller('CurrentCtrl', ['$scope', '$q', 'GistofitService', 'embedlyService', 
  function ($scope, $q, Gistofit, Embedly) {

    $scope.setGistExtract = function(gist) {
        var url = gist.url.key.raw.name;

        Gistofit.getExtract(url).then(function (response) {
            if (response.data == undefined || response.data == '') {
                Embedly.extract(url)
                .then(function(e){
                    Gistofit.setExtract(url, e.data);
                    gist.extract = e.data;
                },
                 function(error) {
                    console.log(error);
                 });
            } else {
                gist.extract = response.data;
            }
        });
    }

    $scope.loadRecentGists = function() {
        $scope.gists = null; 
        var promises = [];

        Gistofit.getRecent().then(function (response) {
            $scope.gists = response.data.gists;

            angular.forEach($scope.gists,function(gist){
                promises.push($scope.setGistExtract(gist));
            });

            $q.all(promises).then(function success(data){
                console.log($scope.gists); // Should all be here
            }, function failure(err){
                // Can handle this is we want
            });
            
            $scope.cursor = response.data.nextCursor; 
            $scope.userServiceInfo = response.data.userServiceInfo;
            $scope.last_seen = response.data.lastSeen;
          });
    }
   
   $scope.deleteGist = function(index, id) {
        Gistofit.deleteGist(id).then(function (response) {
            $scope.gists.splice(index, 1);
          });
    }
    
   $scope.likeGist = function(url, id) {
        Gistofit.likeGist(url, id).then(function (response) {
            console.log(response);
          });
    }

    $scope.load_extract_content = function (content) {
        $scope.extract_content = content;
    };

    $scope.myPagingFunction = function () {
        Gistofit.getRecent($scope.cursor).then(function (response) {
            var promises = [];
        
            angular.forEach(response.data.gists,function(gist){
                promises.push($scope.setGistExtract(gist));
            });

            $q.all(promises).then(function success(data){
                $scope.gists = $scope.gists.concat(response.data.gists); 
            }, function failure(err){
                // Can handle this is we want
            });
           
            if (response.data.nextCursor != "")
                $scope.cursor = response.data.nextCursor; 
            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }
   
   $scope.onReload = function() {
      console.log('reloading');
      var deferred = $q.defer();
      setTimeout(function() {
        Gistofit.getNewest($scope.last_seen).then(function (response) {
            $scope.setGistExtract(response.data.gists[0]);
            $scope.gists.unshift.apply($scope.gists, response.data.gists);
            $scope.last_seen = response.data.lastSeen;
          })
        deferred.resolve(true);
      }, 1000);
      return deferred.promise;
    };
    
    $scope.loadRecentGists();
    steroids.view.navigationBar.show("Current");

    $scope.showComments = function(gist) {
        var message = {
            recipient: "commentsView",
            id: gist.id,
            url: gist.url.key.raw.name
        }
        window.postMessage(message);
        
        var fastSlide = new steroids.Animation({  transition: "slideFromRight",  duration: .2});
        
        // Navigate to your view
        steroids.layers.push(
        {
            view: commentsView,
            animation: fastSlide 
        });
    }
    
    var commentsView = new steroids.views.WebView("views/Comments/comments.html");
    commentsView.preload(); // Prelaod for faster view transitions
   

}]);
