// The contents of individual model .js files will be concatenated into dist/models.js

/*
(function(){
// Protects views where AngularJS is not loaded from errors
if ( typeof angular == 'undefined' ) {
	return;
};
*/

angular.module("gistOfItApp").factory('GistofitService', ['$http', 'embedlyService', function ($http, Embedly) {
    var baseURL = 'http://127.0.0.1:8080/rest/v1';
    //var baseURL = 'https://erudite-flag-623.appspot.com/rest/v1';

    function buildURL (method) {
        return baseURL + method;
    }
    var Gistofit = {};

    Gistofit.getNewest = function (id) {
        var url = buildURL('/gist/newest');
        return $http({method: 'GET', url: url, params: {last_seen: id}});
    };
    Gistofit.getRecent = function (cursor) {
        var url = buildURL('/gist/recent');
        return $http({method: 'GET', url: url, params: {cursor: cursor}});
    };
    Gistofit.getTrending = function (cursor) {
        var url = buildURL('/gist/trending');
        return $http({method: 'GET', url: url});
    };
    Gistofit.getExtract = function (obj, url) {
        var escapedUrl = encodeURIComponent(url);
        var extractUrl = buildURL('/url/'+ escapedUrl + '/extract');

        $http({method: 'GET', url: extractUrl}).then(function (response) {
            obj.extract = response.data;
            if (obj.extract == undefined || obj.extract == '') {
                Embedly.extract(url).then(function(e){
                    Gistofit.setExtract(url, e.data);
                    obj.extract = e.data;
                },
                 function(error) {
                    console.log(error);
                 });
            } 
        });
    };
    Gistofit.setExtract = function (inputUrl, data) {
        var escapedUrl = encodeURIComponent(inputUrl);
        var url = buildURL('/url/'+ escapedUrl + '/extract'); 
        return $http({method: 'POST', url: url, data: data, headers: {'Content-Type': 'text/plain'}});
    };
    Gistofit.addGist = function (inputUrl, content) {
        url = buildURL ('/gist/url/' + encodeURIComponent(inputUrl));
        var data = {'content': content};
        return $http({method: 'POST', url: url, data: data});
        //return $http({method: 'POST', url: url, data: data, withCredentials: true});
    };
    Gistofit.deleteGist = function (id) {
        url = buildURL ('/gist/' + id);
        return $http({method: 'DELETE', url: url, data: {}, headers: {'Content-Type': 'application/json'}});
        //return $http({method: 'POST', url: url, data: data, withCredentials: true});
    };
    Gistofit.likeGist = function (id) {
        url = buildURL ('/gist/' + id  + '/likes');
        return $http.post(url, {userid: '1234' });
    };
    Gistofit.commentGist = function (id, content) {
        url = buildURL ('/gist/' + id + '/comments');
        return $http.post(url, {content: content});
    };
    Gistofit.getComments = function (id) {
        url = buildURL ('/gist/' + id + '/comments');
        return $http({method: 'GET', url: url});
    };
    Gistofit.searchTopUrls = function (query) {
        var url = buildURL('/search/top/urls/?keyword=' + query); 
        return $http({method: 'GET', url: url});
    };
    
    return Gistofit; 
}]);
