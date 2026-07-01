angular.module('noteflowApp')
.controller('AuthController', ['$scope', '$http', '$timeout', '$interval',
function($scope, $http, $timeout, $interval) {
  var auth = this;
  var BASE = 'https://noteflow-j15m.onrender.com/api';

  auth.step = 1;          // 1=email, 2=otp, 3=name(new user)
  auth.email = '';
  auth.otp = '';
  auth.name = '';
  auth.loading = false;
  auth.error = '';
  auth.devOtp = '';
  auth.isNewUser = false;
  auth.resendTimer = 0;
  auth.emailEnabled = false;

  var EMAIL_REGEX = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  // ── Step 1: Send OTP ──────────────────────────────────────
  auth.sendOtp = function() {
    var email = (auth.email || '').trim().toLowerCase();
    if (!EMAIL_REGEX.test(email)) {
      auth.error = 'Please enter a valid email address';
      return;
    }
    auth.loading = true;
    auth.error = '';

    $http.post(BASE + '/auth/send-otp', { email: email })
      .then(function(res) {
        auth.step = 2;
        auth.otp = '';
        auth.emailEnabled = res.data.emailEnabled || false;
        auth.devOtp = res.data.otp || '';
        startResendTimer();
        $timeout(function() {
          var el = document.getElementById('otp-input');
          if (el) el.focus();
        }, 150);
      })
      .catch(function(err) {
        auth.error = (err.data && err.data.error) || 'Failed to send OTP. Check your connection.';
      })
      .finally(function() { auth.loading = false; });
  };

  // ── Step 2: Verify OTP ────────────────────────────────────
  auth.verifyOtp = function() {
    var otp = auth.otp.trim();
    if (otp.length !== 6) {
      auth.error = 'Please enter the complete 6-digit OTP';
      return;
    }
    auth.loading = true;
    auth.error = '';

    $http.post(BASE + '/auth/verify-otp', {
      email: auth.email.trim().toLowerCase(),
      otp: otp,
      name: auth.name || 'User'
    })
    .then(function(res) {
      var data = res.data;
      var isNew = data.isNewUser || data.newUser || false;

      if (isNew && (!auth.name || auth.name.trim() === '')) {
        auth.isNewUser = true;
        auth.step = 3;
        auth.loading = false;
        auth._pendingToken = data.token;
        auth._pendingUser = data.user;
        return;
      }

      $scope.app.onLogin(data.token, data.user);
    })
    .catch(function(err) {
      auth.error = (err.data && err.data.error) || 'Invalid OTP. Please try again.';
      auth.loading = false;
    });
  };

  // ── Step 3: Submit name (new users) ──────────────────────
  auth.submitName = function() {
    if (!auth.name || auth.name.trim().length < 2) {
      auth.error = 'Please enter your name (at least 2 characters)';
      return;
    }
    auth.loading = true;
    auth.error = '';

    $http.post(BASE + '/auth/verify-otp', {
      email: auth.email.trim().toLowerCase(),
      otp: auth.otp.trim(),
      name: auth.name.trim()
    })
    .then(function(res) {
      $scope.app.onLogin(res.data.token, res.data.user);
    })
    .catch(function(err) {
      if (auth._pendingToken && auth._pendingUser) {
        auth._pendingUser.name = auth.name.trim();
        $scope.app.onLogin(auth._pendingToken, auth._pendingUser);
      } else {
        auth.error = 'Session expired. Please start again.';
        auth.step = 1;
        auth.loading = false;
      }
    });
  };

  // ── OTP input: digits only ───────────────────────────────
  auth.onOtpInput = function() {
    auth.otp = auth.otp.replace(/\D/g, '').substring(0, 6);
  };

  // ── Resend timer ──────────────────────────────────────────
  var resendInterval = null;
  function startResendTimer() {
    auth.resendTimer = 30;
    if (resendInterval) $interval.cancel(resendInterval);
    resendInterval = $interval(function() {
      auth.resendTimer--;
      if (auth.resendTimer <= 0) $interval.cancel(resendInterval);
    }, 1000);
  }

  auth.resendOtp = function() {
    if (auth.resendTimer > 0) return;
    auth.step = 1;
    auth.otp = '';
    auth.devOtp = '';
  };

  auth.goBack = function() {
    auth.step = 1;
    auth.otp = '';
    auth.devOtp = '';
    auth.error = '';
    auth.name = '';
  };
}]);
