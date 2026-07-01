angular.module('noteflowApp')
.controller('DashboardController', ['$scope', 'ApiService', function($scope, ApiService) {
  var dash = this;
  dash.loading = true;
  dash.recentNotes = [];
  dash.recentTodos = [];
  dash.stats = { totalNotes: 0, completedTodos: 0, pendingTodos: 0, archivedNotes: 0 };

  var hours = new Date().getHours();
  dash.greeting = hours < 12 ? 'morning' : hours < 17 ? 'afternoon' : 'evening';
  dash.dateStr = new Date().toLocaleDateString('en-IN', { weekday:'long', year:'numeric', month:'long', day:'numeric' });

  function loadData() {
    dash.loading = true;
    ApiService.get('/notes').then(function(res) {
      dash.recentNotes = res.data;
      dash.stats.totalNotes = res.data.length;
    });
    ApiService.get('/notes/archived').then(function(res) {
      dash.stats.archivedNotes = res.data.length;
    });
    ApiService.get('/todos').then(function(res) {
      dash.recentTodos = res.data.filter(function(t) { return !t.isCompleted; });
      dash.stats.pendingTodos = dash.recentTodos.length;
      dash.stats.completedTodos = res.data.filter(function(t) { return t.isCompleted; }).length;
      dash.loading = false;
    });
  }

  loadData();
}]);
