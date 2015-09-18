/**
 * Angular controller to save settings through AJAX posts and display the proper
 * success or failure message. Works with all settings components including
 * system settings, email config, and reCAPTcha config.
 */
app.controller('SettingsController', function($scope, $attrs, SettingsService) {
  var ALERT_ERROR_CLASS = 'alert-danger',
      ALERT_SUCCESS_CLASS = 'alert-success';

  $scope.type = $attrs.settingsType;

  $scope.status = {
    show: false,
    type: null,
    header: null,
    message: null
  };

  $scope.$watchCollection('model', function() {
    $scope.hideAlert();
  });

  $scope.save = function($event) {
    $event.preventDefault();
    $scope.hideAlert();

    function show(type, header, message) {
      $scope.status.show = true;
      $scope.status.type = type;
      $scope.status.header = header;
      $scope.status.message = message;
    }

    SettingsService.updateSettings($scope.type, $scope.model)
      .then(function(result) {
          show(ALERT_SUCCESS_CLASS, result.data.header, result.data.message);
        }, function(result) {
          var header = 'Error',
              message = 'An error occured.';

          if (typeof result !== 'undefined' && result.data) {
            header = result.data.header;
            message = result.data.message;
          }

          show(ALERT_ERROR_CLASS, header, message);
      });
  };

  $scope.hideAlert = function() {
    $scope.status.show = false;
    $scope.status.type = null;
    $scope.status.header = null;
    $scope.status.message = null;
  };

  $scope.clear = function($event) {
    if ($event.target.type === 'password') {
      // TODO: find the proper way to access the model bound to the element that gained focus.
      $scope.model[angular.element($event.target).data('ngModel').replace(/model./gi, '')] = '';
    }
  };
});