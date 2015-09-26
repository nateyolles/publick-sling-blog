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
    alert('TODO: create');
  };

  /* Get all packages on load */
  BackupService.getPackages().success(function(data){
    $scope.packages = data;
  });
});