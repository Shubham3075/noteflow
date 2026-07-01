angular.module('noteflowApp')
.controller('AppController', ['$scope', '$http', '$timeout', 'ApiService',
function($scope, $http, $timeout, ApiService) {
  var app = this;

  app.apiBase = 'http://localhost:8080/api';
  app.isLoggedIn = false;
  app.isAdmin = false;
  app.darkMode = false;
  app.sidebarCollapsed = false;
  app.currentPage = 'dashboard';
  app.pageTitle = 'Dashboard';
  app.searchQuery = '';
  app.user = {};
  app.toast = { show: false, message: '', type: 'info' };

  var pageTitles = {
    dashboard: 'Dashboard', notes: 'My Notes', todos: 'To-Do List',
    archive: 'Archived Notes', settings: 'Settings', admin: 'Admin Panel'
  };

  // ── Init: restore session ─────────────────────────────────
  var token = localStorage.getItem('nf_token');
  var savedUser = localStorage.getItem('nf_user');
  if (token && savedUser) {
    try {
      app.user = JSON.parse(savedUser);
      app.isLoggedIn = true;
      app.isAdmin = app.user.role === 'ADMIN';
      ApiService.setToken(token);
      // Refresh user profile in background
      ApiService.get('/users/me').then(function(res) {
        app.user = res.data;
        app.isAdmin = app.user.role === 'ADMIN';
        localStorage.setItem('nf_user', JSON.stringify(app.user));
      }).catch(function() {
        // Token expired
        app.logout();
      });
    } catch(e) {
      app.logout();
    }
  }

  // ── Restore dark mode ─────────────────────────────────────
  app.darkMode = localStorage.getItem('nf_dark') === 'true';

  // ── Login callback ────────────────────────────────────────
  app.onLogin = function(token, user) {
    app.isLoggedIn = true;
    app.user = user;
    app.isAdmin = user.role === 'ADMIN';
    ApiService.setToken(token);
    localStorage.setItem('nf_token', token);
    localStorage.setItem('nf_user', JSON.stringify(user));
    app.currentPage = 'dashboard';
    app.pageTitle = 'Dashboard';
    app.showToast('Welcome, ' + user.name + '!', 'success');
  };

  // ── Logout ────────────────────────────────────────────────
  app.logout = function() {
    app.isLoggedIn = false;
    app.user = {};
    app.isAdmin = false;
    app.currentPage = 'dashboard';
    ApiService.setToken(null);
    localStorage.removeItem('nf_token');
    localStorage.removeItem('nf_user');
  };

  // ── Navigation ────────────────────────────────────────────
  app.navigate = function(page) {
    app.currentPage = page;
    app.pageTitle = pageTitles[page] || page;
    app.searchQuery = '';
    // Close mobile sidebar
    if (window.innerWidth < 900) {
      app.sidebarCollapsed = true;
      var sidebar = document.querySelector('.sidebar');
      if (sidebar) sidebar.classList.remove('mobile-open');
    }
  };

  app.toggleSidebar = function() {
    if (window.innerWidth < 900) {
      var sidebar = document.querySelector('.sidebar');
      if (sidebar) sidebar.classList.toggle('mobile-open');
    } else {
      app.sidebarCollapsed = !app.sidebarCollapsed;
    }
  };

  app.toggleDarkMode = function() {
    app.darkMode = !app.darkMode;
    localStorage.setItem('nf_dark', app.darkMode);
  };

  // ── Toast ─────────────────────────────────────────────────
  var toastTimer = null;
  app.showToast = function(message, type) {
    app.toast = { show: true, message: message, type: type || 'info' };
    if (toastTimer) $timeout.cancel(toastTimer);
    toastTimer = $timeout(function() { app.toast.show = false; }, 3500);
  };

  $scope.app = app;
}]);
