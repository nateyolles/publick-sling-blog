/**
 * Angular controller to display comments for the admin panel as well as edit,
 * delete, mark as spam, and mark as ham (valid).
 */
app.controller('CommentController', function($scope, $modal, CommentService) {

  function openModal(action, index, callback) {
    var modalInstance = $modal.open({
      templateUrl: 'comment.html',
      controller: 'CommentModalController',
      resolve: {
        action: function() {
          return action;
        },
        comment: function() {
          return $scope.comments[index];
        }
      }
    });

    modalInstance.result.then(function(data){
      callback(data);
    });
  }

  $scope.comments = [];

  $scope.edit = function(index) {
    openModal('edit', index, function(data) {
      if (data.success) {
        $scope.comments[index] = data.comment;
      }
    });
  };

  $scope.akismet = function(index) {
    openModal('akismet', index, function(data) {
      if (data.success) {
        if (data.comment.spam) {
          $scope.comments.splice($scope.comments.indexOf(data.comment), 1);
        } else {
          $scope.comments[index] = data.comment;
        }
      }
    });
  };

  $scope.delete = function(index) {
    openModal('delete', index, function(data) {
      if (data.success) {
        $scope.comments.splice($scope.comments.indexOf(data.comment), 1);
      }
    });
  };

  /* Get all comments on load */
  CommentService.getComments().success(function(data){
    $scope.comments = data;
  });
});