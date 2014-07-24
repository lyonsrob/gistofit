function openExternalDirective($window) {
  return {
    restrict:'E',
    scope: {
            url: "=",
            exit : "&",
            loadStart : "&",
            loadStop : "&",
            loadError: "&"
    },
    transclude: true,
    template:"<a ng-click='openUrl()'><span ng-transclude></span></a>",
    controller: function($scope){
        var inAppBrowser;
        $scope.openUrl = function(){
            inAppBrowser = $window.open($scope.url, '_blank', 'location=yes,toolbar=yes,toolbarposition=top,closebuttoncaption=X');
            console.log($scope.url);
            if($scope.exit instanceof Function){
               inAppBrowser.addEventListener('exit', $scope.exit);
            }
        };
    } 
  }
}

