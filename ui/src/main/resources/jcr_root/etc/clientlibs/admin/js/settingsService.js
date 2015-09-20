/**
 * Angular service to communicate with the server-side for posting config
 * settings and returning success or failure messages.
 */
app.factory('SettingsService', function($http, formDataObject) {
  var settingsFactory = {},
      PATH_BASE = '/bin/admin',
      PATHS = {
        system    : PATH_BASE + '/systemconfig',
        email     : PATH_BASE + '/emailconfig',
        recaptcha : PATH_BASE + '/recaptchaconfig',
        akismet   : PATH_BASE + '/akismetconfig'
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