angular.module('noteflowApp')
.controller('NotesController', ['$scope', '$timeout', 'ApiService',
function($scope, $timeout, ApiService) {
  var notes = this;
  notes.allNotes = [];
  notes.archivedNotes = [];
  notes.loading = true;
  notes.colors = ['#ffffff','#fef3c7','#d1fae5','#dbeafe','#fce7f3','#ede9fe','#ffedd5','#f1f5f9'];
  notes.modal = { show: false, id: null, title: '', content: '', color: '#ffffff', isPinned: false };

  // ── Helpers ──────────────────────────────────────────────
  function findIdx(arr, id) {
    for (var i = 0; i < arr.length; i++) {
      if (arr[i].id === id) return i;
    }
    return -1;
  }

  function load() {
    notes.loading = true;
    ApiService.get('/notes')
      .then(function(res) { notes.allNotes = res.data || []; })
      .catch(function() { $scope.app.showToast('Failed to load notes', 'error'); })
      .finally(function() { notes.loading = false; });
  }

  notes.loadArchived = function() {
    ApiService.get('/notes/archived')
      .then(function(res) { notes.archivedNotes = res.data || []; });
  };

  notes.pinnedNotes = function() {
    var q = ($scope.app.searchQuery || '').toLowerCase();
    return notes.allNotes.filter(function(n) {
      return n.isPinned && (!q || n.title.toLowerCase().indexOf(q) > -1 || (n.content || '').toLowerCase().indexOf(q) > -1);
    });
  };

  notes.otherNotes = function() {
    var q = ($scope.app.searchQuery || '').toLowerCase();
    return notes.allNotes.filter(function(n) {
      return !n.isPinned && (!q || n.title.toLowerCase().indexOf(q) > -1 || (n.content || '').toLowerCase().indexOf(q) > -1);
    });
  };

  // ── Modal ────────────────────────────────────────────────
  notes.openModal = function(note) {
    if (note) {
      notes.modal = {
        show: true, id: note.id,
        title: note.title, content: note.content || '',
        color: note.color || '#ffffff', isPinned: note.isPinned
      };
    } else {
      notes.modal = { show: true, id: null, title: '', content: '', color: '#ffffff', isPinned: false };
    }
    $timeout(function() {
      var el = document.querySelector('.modal-title-input');
      if (el) el.focus();
    }, 100);
  };

  notes.closeAndDiscard = function() {
    notes.modal.show = false;
  };

  // ── Save Note (Create or Update) ─────────────────────────
  notes.saveNote = function() {
    var title = (notes.modal.title || '').trim();
    var content = (notes.modal.content || '').trim();

    if (!title && !content) {
      notes.modal.show = false;
      return;
    }

    var payload = {
      title: title || 'Untitled',
      content: content,
      color: notes.modal.color || '#ffffff',
      type: 'NOTE'
    };

    if (notes.modal.id) {
      // UPDATE existing note
      ApiService.put('/notes/' + notes.modal.id, payload)
        .then(function(res) {
          var idx = findIdx(notes.allNotes, notes.modal.id);
          if (idx > -1) notes.allNotes[idx] = res.data;
          notes.modal.show = false;
          $scope.app.showToast('Note updated!', 'success');
        })
        .catch(function(err) {
          var msg = (err.data && err.data.error) || 'Failed to update note';
          $scope.app.showToast(msg, 'error');
        });
    } else {
      // CREATE new note
      ApiService.post('/notes', payload)
        .then(function(res) {
          notes.allNotes.unshift(res.data);
          notes.modal.show = false;
          $scope.app.showToast('Note saved!', 'success');
        })
        .catch(function(err) {
          var msg = (err.data && err.data.error) || 'Failed to save note';
          $scope.app.showToast(msg, 'error');
        });
    }
  };

  // ── Pin ──────────────────────────────────────────────────
  notes.togglePin = function(note) {
    var id = note.id;
    ApiService.patch('/notes/' + id + '/pin')
      .then(function(res) {
        var idx = findIdx(notes.allNotes, id);
        if (idx > -1) notes.allNotes[idx] = res.data;
        if (notes.modal.id === id) notes.modal.isPinned = res.data.isPinned;
        $scope.app.showToast(res.data.isPinned ? 'Note pinned!' : 'Note unpinned!', 'info');
      })
      .catch(function() { $scope.app.showToast('Failed to update note', 'error'); });
  };

  // ── Archive ──────────────────────────────────────────────
  notes.toggleArchive = function(note) {
    var id = note.id;
    ApiService.patch('/notes/' + id + '/archive')
      .then(function(res) {
        notes.allNotes = notes.allNotes.filter(function(n) { return n.id !== id; });
        notes.archivedNotes = notes.archivedNotes.filter(function(n) { return n.id !== id; });
        if (!res.data.isArchived) {
          notes.allNotes.unshift(res.data);
        } else {
          notes.archivedNotes.unshift(res.data);
        }
        notes.modal.show = false;
        $scope.app.showToast(res.data.isArchived ? 'Note archived!' : 'Note restored!', 'info');
      })
      .catch(function() { $scope.app.showToast('Failed to archive note', 'error'); });
  };

  // ── Delete ───────────────────────────────────────────────
  notes.deleteNote = function(note) {
    if (!confirm('Delete this note? This cannot be undone.')) return;
    var id = note.id;
    ApiService.delete('/notes/' + id)
      .then(function() {
        notes.allNotes = notes.allNotes.filter(function(n) { return n.id !== id; });
        notes.archivedNotes = notes.archivedNotes.filter(function(n) { return n.id !== id; });
        notes.modal.show = false;
        $scope.app.showToast('Note deleted.', 'error');
      })
      .catch(function() { $scope.app.showToast('Failed to delete note', 'error'); });
  };

  load();
}]);
