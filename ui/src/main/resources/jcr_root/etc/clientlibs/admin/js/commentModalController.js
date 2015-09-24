/**
 * Angular controller for the Comment Controller Modals. The author can confirm
 * actions such as deletion, marking as spam, and marking as ham. The author can
 * also edit comments.
 */
app.controller('CommentModalController', function ($scope, $modalInstance, CommentService, action, comment) {

  $scope.comment = angular.copy(comment);
  $scope.editMode = action === 'edit';
  $scope.deleteMode = action === 'delete';
  $scope.spamMode = action === 'akismet' && !$scope.comment.spam;
  $scope.hamMode = action === 'akismet' && $scope.comment.spam;

  $scope.ok = function () {
    if ($scope.deleteMode) {
      CommentService.deleteComment($scope.comment).success(function(data){
        $modalInstance.close({success: true, comment: comment});
      });
    } else if ($scope.editMode) {
      CommentService.editComment($scope.comment).success(function(data){
        $scope.comment.edited = true;
        $modalInstance.close({success: true, comment: $scope.comment});
      });
    } else if ($scope.spamMode) {
      CommentService.submitSpam($scope.comment).success(function(data){
        $scope.comment.spam = true;
        $modalInstance.close({success: true, comment: $scope.comment});
      });
    } else if ($scope.hamMode) {
      CommentService.submitHam($scope.comment).success(function(data){
        $scope.comment.spam = false;
        $modalInstance.close({success: true, comment: $scope.comment});
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});