angular.module('noteflowApp')
.controller('AdminController', ['$scope', 'ApiService', function($scope, ApiService) {
  var admin = this;
  admin.tab = 'users';
  admin.loading = true;
  admin.users = [];
  admin.notes = [];
  admin.todos = [];

  function loadAll() {
    admin.loading = true;
    ApiService.get('/users/admin/all').then(function(res) { admin.users = res.data; });
    ApiService.get('/notes/admin/all').then(function(res) { admin.notes = res.data; });
    ApiService.get('/todos/admin/all').then(function(res) {
      admin.todos = res.data;
      admin.loading = false;
    });
  }

  admin.toggleUserStatus = function(user) {
    ApiService.patch('/users/admin/' + user.id + '/toggle-status').then(function(res) {
      var idx = admin.users.findIndex(function(u) { return u.id === user.id; });
      if (idx > -1) admin.users[idx].isActive = res.data.isActive;
      $scope.app.showToast('User status updated.', 'info');
    });
  };

  admin.deleteUser = function(user) {
    if (!confirm('Delete user "' + user.name + '"? This will delete all their data!')) return;
    ApiService.delete('/users/admin/' + user.id).then(function() {
      admin.users = admin.users.filter(function(u) { return u.id !== user.id; });
      $scope.app.showToast('User deleted.', 'error');
    });
  };

  admin.deleteNote = function(note) {
    if (!confirm('Delete this note?')) return;
    ApiService.delete('/notes/' + note.id).then(function() {
      admin.notes = admin.notes.filter(function(n) { return n.id !== note.id; });
      $scope.app.showToast('Note deleted.', 'error');
    });
  };

  admin.deleteTodo = function(todo) {
    if (!confirm('Delete this task?')) return;
    ApiService.delete('/todos/' + todo.id).then(function() {
      admin.todos = admin.todos.filter(function(t) { return t.id !== todo.id; });
      $scope.app.showToast('Task deleted.', 'error');
    });
  };

  loadAll();
}]);
