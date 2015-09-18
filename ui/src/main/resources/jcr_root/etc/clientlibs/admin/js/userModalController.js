/**
 * Angular controller for the User Controller Modals. The modals contain forms
 * to create and update users and groups. Depends on the UserService to
 * communicate with the Sling server.
 */
app.controller('UserModalController', function ($scope, $modalInstance, UserService, group, user, action) {

  $scope.group = group;
  $scope.isNew = action === 'add';
  $scope.isPass = action === 'pass';

  if (typeof user !== 'undefined') {
    $scope.user = user.user;
    $scope.displayName = user.displayName;
  }

  $scope.ok = function () {
    if ($scope.isNew) {
      UserService.createUser($scope.user, $scope.displayName, $scope.password, $scope.passwordConfirm).success(function(data){
        UserService.updateGroup($scope.group, $scope.user).success(function(data){
          $modalInstance.close({action: 'createUser', data: {user: $scope.user, displayName: $scope.displayName}});
        }).error(function(data){
          $modalInstance.close({action: 'createUser', data: false});
        });
      });
    } else if ($scope.isPass) {
      UserService.changePassword($scope.user, $scope.oldPassword, $scope.password, $scope.passwordConfirm).success(function(data){
        $modalInstance.close({action: 'updatePass', data: true});
      }).error(function(data){
        $modalInstance.close({action: 'updatePass', data: false});
      });
    } else {
      UserService.updateUser($scope.user, $scope.displayName).success(function(data){
        $modalInstance.close({action: 'updateUser', data: $scope.displayName});
      }).error(function(data){
        $modalInstance.close({action: 'updateUser', data: false});
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});