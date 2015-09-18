/**
 * Angular service to communicate with the Sling User Manager Post Servlet. This
 * service will save, update, and delete Jackrabbit users and groups.
 */
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