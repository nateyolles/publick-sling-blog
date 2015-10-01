/**
 * Angular controller to upload, create, install, delete and list backup
 * packages.
 */
app.controller('BackupController', function($scope, $modal, BackupService) {

  $scope.packages = [];

  $scope.install = function(index) {
    openModal('install', index, function(data) {
      if (data.success) {
        // TODO: Display success/failure alerts
        alert('success');
      }
    });
  };

  $scope.delete = function(index) {
    alert('TODO: delete');
  };

  $scope.$watch('files', function() {
    if ($scope.files && $scope.files.length) {
      BackupService.uploadPackage($scope.files[0]).success(function(data, status, headers, config){
        if (data && data.data) {
          $scope.packages.unshift(JSON.parse(data.data));
        }
      });
    }
  });

  $scope.create = function() {
    openModal('create', null, function(data) {
      if (data.success) {
        $scope.packages.unshift(data.package);
      }
    });
  };

  function openModal(action, index, callback) {
    var modalInstance = $modal.open({
      templateUrl: 'package.html',
      controller: 'BackupModalController',
      resolve: {
        action: function() {
          return action;
        },
        package: function() {
          if (index) {
            return $scope.packages[index];
          } else {
            return null;
          }
        }
      }
    });

    modalInstance.result.then(function(data){
      callback(data);
    });
  }

  /* Get all packages on load */
  BackupService.getPackages().success(function(data){
    $scope.packages = data;
  });
});