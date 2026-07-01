angular.module('noteflowApp')
.controller('SettingsController', ['$scope', 'ApiService', function($scope, ApiService) {
  var settings = this;
  settings.name = $scope.app.user.name || '';
  settings.saving = false;

  settings.saveProfile = function() {
    settings.saving = true;
    ApiService.put('/users/me', { name: settings.name }).then(function(res) {
      $scope.app.user.name = res.data.name;
      localStorage.setItem('nf_user', JSON.stringify($scope.app.user));
      settings.saving = false;
      $scope.app.showToast('Profile updated!', 'success');
    }).catch(function() {
      settings.saving = false;
      $scope.app.showToast('Update failed.', 'error');
    });
  };

  settings.uploadPhoto = function(file) {
    if (!file) return;
    var fd = new FormData();
    fd.append('file', file);
    ApiService.uploadFile('/users/me/photo', fd).then(function(res) {
      $scope.app.user.profilePhoto = res.data.profilePhoto;
      localStorage.setItem('nf_user', JSON.stringify($scope.app.user));
      $scope.app.showToast('Photo updated!', 'success');
    }).catch(function() {
      $scope.app.showToast('Upload failed.', 'error');
    });
  };
}]);
