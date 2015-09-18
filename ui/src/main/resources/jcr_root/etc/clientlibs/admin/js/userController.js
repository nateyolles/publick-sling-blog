/**
 * Angular controller to populate Groups and Users on the
 * /libs/publick/components/admin/userList component. Depends on the UserService
 * to communicate with the Sling server.
 */
app.controller('UserController', function($scope, $http, $modal, UserService) {

  $scope.userList = {
    groups:[
      {displayName: "Admin/Default", name: null, canUpdate : false, users: []},
      {displayName: "Authors", name: 'authors', canUpdate : true, users: []},
      {displayName: "Testers", name: 'testers', canUpdate : true, users: []}
    ]
  };

  $scope.delete = function(groupIndex, userIndex) {
    //TODO: create a confirmation modal
    UserService.deleteUser($scope.userList.groups[groupIndex].users[userIndex].user)
      .success(function(data){
        $scope.userList.groups[groupIndex].users.splice(userIndex ,1);
      });
  };

  $scope.edit = function(action, groupIndex, userIndex) {

    var modalInstance = $modal.open({
      templateUrl: 'user.html',
      controller: 'UserModalController',
      resolve: {
        action: function() {
          return action;
        },
        user: function() {
          return $scope.userList.groups[groupIndex].users[userIndex];
        },
        group: function() {
          return $scope.userList.groups[groupIndex].name;
        }
      }
    });

    modalInstance.result.then(function(changes) {
      if (!changes.data) {
        alert('error');
      } else {
        if (changes.action === 'updateUser') {
          $scope.userList.groups[groupIndex].users[userIndex].displayName = changes.data;
        } else if (changes.action === 'updatePass') {
          alert('password changed');
        } else {
          $scope.userList.groups[groupIndex].users.push({user: changes.data.user, displayName: changes.data.displayName});
        }
      }
    });
  };

  UserService.getAllUsers().success(function(data){
    angular.forEach(data, function(value, key){
      if (typeof value['memberOf'] !== 'undefined') {
        if (value['memberOf'][0] === '/system/userManager/group/authors') {
          $scope.userList.groups[1].users.push({user: key, displayName: value['displayName']});
        } else if (value['memberOf'][0] === '/system/userManager/group/testers') {
          $scope.userList.groups[2].users.push({user: key, displayName: value['displayName']});
        } else {
          $scope.userList.groups[0].users.push({user: key, displayName: value['displayName']});
        }
      }
    });
  });

});