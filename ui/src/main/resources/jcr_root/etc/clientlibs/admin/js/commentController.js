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

  $scope.delete = function(index) {

    var modalInstance = $modal.open({
      templateUrl: 'confirm.html',
      controller: 'CommentModalController',
      resolve: {
        comment: function() {
          return $scope.comments[index];
        }
      }
    });

    modalInstance.result.then(function(data) {
      if (data.success) {
        $scope.comments.splice($scope.comments.indexOf(data.comment), 1);
      }
    });
  };

  CommentService.getComments().success(function(data){
    $scope.comments = data;
  });

});