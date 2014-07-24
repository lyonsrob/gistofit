function gistService($http){
  var origin = 'http://localhost:8080';
  var fetchUsers = function(query){
    return $http({
      method: 'JSONP',
      url : origin + '/search/users?callback=JSON_CALLBACK',
      params: {q: query}
    });
  };

  return {
    fetchUsers: fetchUsers
  };
}
