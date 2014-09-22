'use strict'

function toArrayObj(array) {
 for (var i = 0; i < array.length; ++i)
     array[i] = {url: array[i], extract: {}};
 return array;
}

angular.module('gistOfItApp').controller('SearchCtrl', ['$scope', 'GistofitService', 'embedlyService', 
  function ($scope, Gistofit, Embedly) {

    $scope.setUrlExtract = function(result) {
        var url = result.url; 

        Gistofit.getExtract(url).then(function (response) {
            if (response.data == undefined || response.data == '') {
                Embedly.extract(url,536,536)
                .then(function(e){
                    Gistofit.setExtract(url, e.data);
                    result.extract = e.data;
                },
                 function(error) {
                    console.log(error);
                 });
            } else {
                result.extract = response.data;
            }
        });
    }
 
    $scope.$watch('search', function (value) {
        Gistofit.searchTopUrls(value).then(function (response) {
            $scope.results = toArrayObj(response.data);
            var promises = []; 

            angular.forEach($scope.results,function(result){
               $scope.setUrlExtract(result);
            });
           
            console.log(response.data);
        });
        
        Gistofit.searchTopUrls(value).then(function (response) {
            $scope.results = toArrayObj(response.data);
            var promises = []; 

            angular.forEach($scope.results,function(result){
               $scope.setUrlExtract(result);
            });
           
            console.log(response.data);
        });
    });

    window.addEventListener("message", $scope.setAddData);
    steroids.view.navigationBar.show("Search");

}    
]);
