'use strict'

angular.module('gistOfItApp').controller('CurrentCtrl', ['$scope', '$q', 'GistofitService', 'embedlyService', 
  function ($scope, $q, Gistofit, Embedly) {

    $scope.setGistExtract = function(gist) {
        var url = gist.url.key.raw.name;

        Gistofit.getExtract(url).then(function (response) {
            gist.extract = response.data;
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
            $scope.gists = $scope.gists.concat(response.data.gists); 
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
}    
]);
