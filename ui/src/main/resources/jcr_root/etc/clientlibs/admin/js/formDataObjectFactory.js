/**
 * Required for Multipart/form-data file upload with AngularJS.
 */
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