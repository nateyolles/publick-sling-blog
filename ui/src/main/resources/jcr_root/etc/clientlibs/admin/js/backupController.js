/**
 * Angular controller to upload, create, install, delete and list backup
 * packages.
 */
app.controller('BackupController', function($scope, $modal, BackupService) {

  $scope.packages = [];

  $scope.install = function(index) {
    alert('TODO: install');
  };

  $scope.delete = function(index) {
    alert('TODO: delete');
  };

  $scope.upload = function() {
    alert('TODO: upload');
  };

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