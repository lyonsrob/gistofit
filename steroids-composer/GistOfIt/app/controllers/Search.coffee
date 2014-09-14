'use strict'

angular.module('gistOfItApp')
  .controller 'SearchCtrl', ($scope, $timeout) ->
    # Bind controller data
    do ->
      $scope.text_field0 = {}

      $scope.text_field0.value = '' # default value


    # Attach listeners


    # Initialize controller
    do ->
      steroids.view.navigationBar.show ' '
