'use strict'

angular.module('gistOfItApp').controller('GistCtrl', ['$scope', 'GistofitService', 
  function ($scope, Gistofit) {

    window.addEventListener("message", $scope.setAddData);
    steroids.view.navigationBar.show("Search");

}    
]);
