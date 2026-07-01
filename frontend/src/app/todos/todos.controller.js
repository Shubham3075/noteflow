angular.module('noteflowApp')
.controller('TodosController', ['$scope', 'ApiService',
function($scope, ApiService) {
  var todos = this;
  todos.allTodos = [];
  todos.loading = true;
  todos.filter = 'all';
  todos.newTodo = { title: '', description: '', priority: 'MEDIUM', category: '', dueDate: null };
  todos.editModal = { show: false };

  // ── Helper ───────────────────────────────────────────────
  function findIdx(id) {
    for (var i = 0; i < todos.allTodos.length; i++) {
      if (todos.allTodos[i].id === id) return i;
    }
    return -1;
  }

  function load() {
    todos.loading = true;
    ApiService.get('/todos')
      .then(function(res) { todos.allTodos = res.data || []; })
      .catch(function() { $scope.app.showToast('Failed to load tasks', 'error'); })
      .finally(function() { todos.loading = false; });
  }

  // ── Filters ──────────────────────────────────────────────
  todos.filteredTodos = function() {
    var q = ($scope.app.searchQuery || '').toLowerCase();
    var list = todos.allTodos.filter(function(t) {
      if (!q) return true;
      return t.title.toLowerCase().indexOf(q) > -1 ||
             (t.description || '').toLowerCase().indexOf(q) > -1 ||
             (t.category || '').toLowerCase().indexOf(q) > -1;
    });
    if (todos.filter === 'all') return list;
    if (todos.filter === 'pending') return list.filter(function(t) { return !t.isCompleted; });
    if (todos.filter === 'completed') return list.filter(function(t) { return t.isCompleted; });
    return list.filter(function(t) { return t.priority === todos.filter; });
  };

  todos.pendingCount = function() { return todos.allTodos.filter(function(t) { return !t.isCompleted; }).length; };
  todos.completedCount = function() { return todos.allTodos.filter(function(t) { return t.isCompleted; }).length; };

  // ── Add Todo ─────────────────────────────────────────────
  todos.addTodo = function() {
    if (!todos.newTodo.title || !todos.newTodo.title.trim()) {
      $scope.app.showToast('Please enter a task title', 'error');
      return;
    }

    var payload = {
      title: todos.newTodo.title.trim(),
      description: todos.newTodo.description || '',
      priority: todos.newTodo.priority || 'MEDIUM',
      category: todos.newTodo.category || '',
      dueDate: todos.newTodo.dueDate || null
    };

    ApiService.post('/todos', payload)
      .then(function(res) {
        todos.allTodos.unshift(res.data);
        todos.newTodo = { title: '', description: '', priority: 'MEDIUM', category: '', dueDate: null };
        $scope.app.showToast('Task added!', 'success');
      })
      .catch(function(err) {
        var msg = (err.data && err.data.error) || 'Failed to add task';
        $scope.app.showToast(msg, 'error');
      });
  };

  // ── Toggle Complete ───────────────────────────────────────
  todos.toggleComplete = function(todo) {
    var id = todo.id;
    ApiService.patch('/todos/' + id + '/toggle')
      .then(function(res) {
        var idx = findIdx(id);
        if (idx > -1) todos.allTodos[idx] = res.data;
        $scope.app.showToast(res.data.isCompleted ? '✓ Task done!' : 'Marked pending', 'info');
      })
      .catch(function() { $scope.app.showToast('Failed to update task', 'error'); });
  };

  // ── Edit Todo ─────────────────────────────────────────────
  todos.openEdit = function(todo) {
    todos.editModal = {
      show: true,
      id: todo.id,
      title: todo.title,
      description: todo.description || '',
      priority: todo.priority || 'MEDIUM',
      category: todo.category || '',
      dueDate: todo.dueDate || null
    };
  };

  todos.saveEdit = function() {
    if (!todos.editModal.title || !todos.editModal.title.trim()) {
      $scope.app.showToast('Title cannot be empty', 'error');
      return;
    }
    var id = todos.editModal.id;
    var payload = {
      title: todos.editModal.title.trim(),
      description: todos.editModal.description || '',
      priority: todos.editModal.priority || 'MEDIUM',
      category: todos.editModal.category || '',
      dueDate: todos.editModal.dueDate || null
    };

    ApiService.put('/todos/' + id, payload)
      .then(function(res) {
        var idx = findIdx(id);
        if (idx > -1) todos.allTodos[idx] = res.data;
        todos.editModal.show = false;
        $scope.app.showToast('Task updated!', 'success');
      })
      .catch(function(err) {
        var msg = (err.data && err.data.error) || 'Failed to update task';
        $scope.app.showToast(msg, 'error');
      });
  };

  // ── Delete Todo ───────────────────────────────────────────
  todos.deleteTodo = function(todo) {
    if (!confirm('Delete "' + todo.title + '"?')) return;
    var id = todo.id;
    ApiService.delete('/todos/' + id)
      .then(function() {
        todos.allTodos = todos.allTodos.filter(function(t) { return t.id !== id; });
        $scope.app.showToast('Task deleted.', 'error');
      })
      .catch(function() { $scope.app.showToast('Failed to delete task', 'error'); });
  };

  load();
}]);
