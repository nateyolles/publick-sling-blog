/**
 * Angular service to communicate with the Backup Admin Servlet. This service
 * will get all packages, create packages, delete packages, install packages,
 * and upload packages.
 */
app.factory('BackupService', function($http, formDataObject) {
  var backupFactory = {},
      PATH = '/bin/admin/backup';

  /**
   * @private
   */
  function post(data) {
    return $http({
      method: 'POST',
      url: PATH,
      data: data,
      transformRequest: formDataObject
    });
  }

  backupFactory.getPackages = function() {
    return $http({
      method: 'GET',
      url: PATH
    });
  };

  return backupFactory;
});