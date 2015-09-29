/**
 * Angular controller for the Backup Controller Modals. The author can confirm
 * actions such as deletion, installation, file upload, and package creation.
 */
app.controller('BackupModalController', function ($scope, $modalInstance, BackupService, action, package) {

  $scope.installMode = action == 'install';
  $scope.deleteMode = action == 'delete';
  $scope.uploadMode = action == 'upload';
  $scope.createMode = action == 'create';
  $scope.package = package;

  $scope.ok = function () {
    if ($scope.createMode) {
      BackupService.createBackup($scope.name).success(function(data){
        $scope.package = JSON.parse(data.data);
        $modalInstance.close({success: true, package: $scope.package});
      });
    } else if ($scope.deleteMode) {
      // TODO
    } else if ($scope.uploadMode) {
      // TODO
    } else if ($scope.installMode) {
      // TODO
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});