/**
 * Angular controller to navigate and upload images and assets. The controller
 * works with the /libs/publick/components/assetList component.
 */
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