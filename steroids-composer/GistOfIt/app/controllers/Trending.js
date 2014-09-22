'use strict'

angular.module('gistOfItApp').controller('TrendingCtrl', ['$scope', '$q', 'GistofitService', 'embedlyService', 
  function ($scope, $q, Gistofit, Embedly) {
    $scope.setGistExtract = function(gist) {
        var url = gist.url.key.raw.name;

        Gistofit.getExtract(url).then(function (response) {
            if (response.data == undefined || response.data == '') {
                Embedly.extract(url, 760, 760)
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

    $scope.loadTrendingGists = function() {
        $scope.gists = null; 
        var promises = [];

        Gistofit.getTrending().then(function (response) {
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
    
    steroids.logger.log('loading trending gists!');
    $scope.loadTrendingGists();
    steroids.view.navigationBar.show("Trending");

}]);
