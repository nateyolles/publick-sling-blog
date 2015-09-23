/**
 * Angular controller for the Comment Controller Modals. The author can confirm
 * actions such as deletion, marking as spam, and marking as ham. The author can
 * also edit comments.
 */
app.controller('CommentModalController', function ($scope, $modalInstance, CommentService, comment) {

  $scope.comment = comment;

  $scope.ok = function () {
    CommentService.deleteComment($scope.comment).success(function(data){
      $modalInstance.close({success: true, comment: comment});
    });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});