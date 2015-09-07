$(function() {
  $('.blog-edit-content').summernote({
    height: 300,
    toolbar : [
      ['group', ['undo', 'redo']],
      ['style', ['bold', 'italic', 'underline', 'clear']],
      ['font', ['strikethrough', 'superscript', 'subscript']],
      ['fontsize', ['fontsize']],
      ['color', ['color']],
      ['para', ['ul', 'ol', 'paragraph']],
      ['insert', ['slingasset', 'link', 'table', 'hr']],
      ['misc', ['fullscreen', 'codeview']],
      ['group', ['help']]
    ]
  });

  $('.logout').click(function(e){
    e.preventDefault();
    $.post('j_security_check', {
      j_username : '-',
      j_password : '-',
      j_validate : true
    }).always(function(data){
      if (data.status === 403) {
        window.location = '/admin/login.html'
      }
    });
  });
});

var app = angular.module('publick', ['ngFileUpload', 'ui.bootstrap']);

app.controller('KeywordsController', function($scope){
  $scope.addKeyword = function(event) {
    event.preventDefault();
    $scope.keywords.push(null);
  };

  $scope.removeKeyword = function(event, index) {
    event.preventDefault();
    $scope.keywords.splice(index,1);
  }
});

app.controller('AssetController', function($scope, $http, Upload) {

    $scope.breadcrumbs = ['assets'];
    $scope.currentPath = '/content/assets';
    $scope.folders = [];
    $scope.assets = [];

    $scope.getImagePath = function(image) {
      return $scope.currentPath + '/' + image;
    }

    $scope.navigate = function(folder, isRelative) {
      $scope.selectedAsset = null;

      if (isRelative) {
        if (folder === -1) {
          $scope.breadcrumbs.pop();
        } else {
          for (var x = folder - $scope.breadcrumbs.length + 1; x < 0; x++) {
            $scope.breadcrumbs.pop();
          }
        }
      } else {
        $scope.breadcrumbs.push(folder);
      }

      $scope.currentPath = '/content/' + $scope.breadcrumbs.join('/');
      update($scope.currentPath);
    };

    $scope.selectAsset = function(event) {
      $scope.selectedAsset = $(event.currentTarget).find('img').attr('src');
    };

    $scope.$watch('files', function() {
      $scope.upload($scope.files);
    });

    $scope.upload = function (files) {
      if (files && files.length) {
        for (var i = 0; i < files.length; i++) {
          var file = files[i];
          Upload.upload({
            url: '/bin/admin/uploadfile',
            file: file,
            fields: {'path' : $scope.currentPath},
            sendFieldsAs: 'form'
          }).progress(function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
          }).success(function (data, status, headers, config) {
            console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
            $scope.assets.push(file.name);
          });
        }
      }
    };

    function update(path) {
      $http.get(path + '.1.json')
        .success(function(data, status, headers, config) { 
          $scope.folders = [];
          $scope.assets = [];

          angular.forEach(data, function(value, key){
            if (!(new RegExp(/^(sling|jcr|rep):/).test(key))) {
              if (value['jcr:primaryType'] === 'nt:file') {
                $scope.assets.push(key);
              } else {
                $scope.folders.push(key);
              }
            }
          });
        })
        .error(function(data, status, headers, config) {
            // log error
        });
    }

    update('/content/assets');
});

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

app.factory('formDataObject', function() {
  return function(data, headersGetter) {
    var formData = new FormData();

    angular.forEach(data, function (value, key) {
      formData.append(key, value);
    });

    var headers = headersGetter();
    delete headers['Content-Type'];

    return formData;
  };
});

app.factory('UserService', function($http, formDataObject) {
  var userFactory          = {},
      PLACEHOLDER          = '{}',
      PATH_BASE            = '/system/userManager',
      PATH_USER_HOME       = PATH_BASE + '/user/{}',
      PATH_GET_ALL_USERS   = PATH_BASE + '/user.tidy.1.json',
      PATH_GET_USER        = PATH_BASE + '/user/{}.tidy.1.json',
      PATH_CREATE_USER     = PATH_BASE + '/user.create.json',
      PATH_UPDATE_USER     = PATH_BASE + '/user/{}.update.json',
      PATH_CHANGE_PASSWORD = PATH_BASE + '/user/{}.changePassword.json',
      PATH_DELETE_USER     = PATH_BASE + '/user/{}.delete.json',
      PATH_GET_ALL_GROUPS  = PATH_BASE + '/group.tidy.1.json',
      PATH_GET_GROUP       = PATH_BASE + '/group/{}.tidy.1.json',
      PATH_CREATE_GROUP    = PATH_BASE + '/group.create.json',
      PATH_UPDATE_GROUP    = PATH_BASE + '/group/{}.update.html', // Bug in Sling won't work with JSON
      PATH_DELETE_GROUP    = PATH_BASE + '/group/{}.delete.json';

  /**
   * @private
   */
  function post(path, data) {
    return $http({
      method: 'POST',
      url: path,
      data: data,
      transformRequest: formDataObject
    });
  }

  userFactory.getAllUsers = function() {
    return $http.get(PATH_GET_ALL_USERS);
  };

  userFactory.getUser = function(username) {
    return $http.get(PATH_GET_USER.replace(PLACEHOLDER, username));
  };

  userFactory.createUser = function(username, displayName, password, passwordConfirm) {
    return post(PATH_CREATE_USER, {
      ':name': username,
      pwd: password,
      pwdConfirm : passwordConfirm,
      displayName : displayName
    });
  };

  userFactory.updateUser = function(username, displayName) {
    return post(PATH_UPDATE_USER.replace(PLACEHOLDER, username), {
      displayName : displayName
    });
  };

  userFactory.changePassword = function(username, oldPassword, newPassword, newPasswordConfirm) {
    return post(PATH_CHANGE_PASSWORD.replace(PLACEHOLDER, username), {
      oldPwd : oldPassword,
      newPwd : newPassword,
      newPwdConfirm : newPasswordConfirm
    });
  };

  userFactory.deleteUser = function(username) {
    return post(PATH_DELETE_USER.replace(PLACEHOLDER, username), {
      go: 1
    });
  };

  userFactory.getAllGroups = function() {
    return $http.get(PATH_GET_ALL_GROUPS);
  };

  userFactory.getGroup = function(group) {
    return $http.get(PATH_GET_GROUP.replace(PLACEHOLDER, group));
  };

  userFactory.createGroup = function(group) {
    return post(PATH_CREATE_GROUP, {
      ':name': group
    });
  };

  userFactory.updateGroup = function(group, user) {
    return post(PATH_UPDATE_GROUP.replace(PLACEHOLDER, group), {
      ':member' : PATH_USER_HOME.replace(PLACEHOLDER, user)
    });
  };

  userFactory.deleteGroup = function(group) {
    return post(PATH_DELETE_GROUP.replace(PLACEHOLDER, group), {
      go: 1
    });
  };

  return userFactory;
});

app.factory('SettingsService', function($http, formDataObject) {
  var settingsFactory = {},
      PATH_BASE = '/bin/admin',
      PATHS = {
        system    : PATH_BASE + '/systemconfig',
        recaptcha : PATH_BASE + '/recaptchaconfig',
        email     : PATH_BASE + '/emailconfig'
      };

  /**
   * @private
   */
  function post(path, data) {
    return $http({
      method: 'POST',
      url: path,
      data: data,
      transformRequest: formDataObject
    });
  }

  settingsFactory.updateSettings = function(type, model) {
    return post(PATHS[type], model);
  };

  return settingsFactory;
});