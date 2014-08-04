/**
 * Created by moran on 21/06/14.
 */

var gistofit = angular.module('gistofit', []);
//service
(function (module) {
    module.provider('gistofitService', function () {
        var key;
        this.setKey = function(userKey) {
            key = userKey;
            return key;
        }
        this.getKey = function() {
            return key;
        }

        function embedly($http) {
            this.embed = function(inputUrl) {
                var escapedUrl = encodeURI(inputUrl);
                var embedlyRequest = 'http://api.embed.ly/1/oembed?key=' + key + '&url=' +  escapedUrl;
                return $http({method: 'GET', url: embedlyRequest});
            };
            this.extract = function(inputUrl, width, height, retina) {
                var escapedUrl = encodeURIComponent(inputUrl);
                var embedlyRequest = 'http://localhost:8080/rest/gists/' + escapedUrl + '/extract';
                return $http({method: 'GET', url: embedlyRequest});
            };
        }


        this.$get = function($http) {
            return new embedly($http);
        };

    })
//controller
    module.controller('gistCtrl', function($scope) {
        $scope.embedCode = '';
    })
})(gistofit);
