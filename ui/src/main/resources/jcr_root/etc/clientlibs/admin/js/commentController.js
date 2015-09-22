/**
 * Angular controller to display comments for the admin panel as well as edit,
 * delete, mark as spam, and mark as ham (valid).
 */
app.controller('CommentController', function($scope, $modal, CommentService) {

  $scope.comments = [];

  $scope.edit = function() {
    alert('TODO: edit');
  };

  $scope.akismet = function() {
    alert('TODO: akismet');
  };

  $scope.delete = function() {
    alert('TODO: delete');
  };

  CommentService.getComments().success(function(data){
    $scope.comments = data;
  });

});