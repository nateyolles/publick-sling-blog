/**
 * Angular controller for adding and removing keywords/tags while editing blog
 * posts. Works with /libs/publick/components/admin/blogEdit component.
 */
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