angular.module('noteflowApp')
.factory('ApiService', ['$http', function($http) {
  var BASE = 'http://localhost:8080/api';
  var token = null;

  function headers() {
    var h = { 'Content-Type': 'application/json' };
    if (token) h['Authorization'] = 'Bearer ' + token;
    return { headers: h };
  }

  return {
    setToken: function(t) { token = t; },
    get: function(url) { return $http.get(BASE + url, headers()); },
    post: function(url, data) { return $http.post(BASE + url, data, headers()); },
    put: function(url, data) { return $http.put(BASE + url, data, headers()); },
    patch: function(url, data) { return $http.patch(BASE + url, data || {}, headers()); },
    delete: function(url) { return $http.delete(BASE + url, headers()); },
    uploadFile: function(url, formData) {
      return $http.post(BASE + url, formData, {
        headers: { 'Authorization': 'Bearer ' + token, 'Content-Type': undefined },
        transformRequest: angular.identity
      });
    }
  };
}]);
