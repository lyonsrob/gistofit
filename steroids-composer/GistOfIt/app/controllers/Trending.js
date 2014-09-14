'use strict'

angular.module('gistOfItApp').controller('TrendingCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {

    $scope.loadTrendingGists = function() {
        Gistofit.getTrending().then(function (response) {
            $scope.gists = response.data.gists; 
            $scope.cursor = response.data.nextCursor; 

            $scope.userServiceInfo = response.data.userServiceInfo;
          });
    }
    
    steroids.logger.log('loading trending gists!');
    $scope.loadTrendingGists();
}    
]);
