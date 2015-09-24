/**
 * Angular service to communicate with the Comment Admin Servlet. This service
 * will get all comments, delete comments, mark comments as spam, and mark
 * comments as ham (a valid comment).
 */
app.factory('CommentService', function($http, formDataObject) {
  var commentFactory = {},
      PATH = '/bin/admin/comment',
      DELETE_COMMENT = 'delete_comment',
      EDIT_COMMENT = 'edit_comment',
      MARK_SPAM = 'mark_spam',
      MARK_HAM = 'mark_ham'

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

  commentFactory.getComments = function() {
    return $http({
      method: 'GET',
      url: PATH
    });
  };

  commentFactory.deleteComment = function(comment) {
    return post({
      action: DELETE_COMMENT,
      id: comment.id
    });
  };

  commentFactory.editComment = function(comment) {
    return post({
      action: EDIT_COMMENT,
      id: comment.id,
      text: comment.comment
    });
  };

  commentFactory.submitSpam = function(comment) {
    return post({
      action: MARK_SPAM,
      id: comment.id
    });
  };

  commentFactory.submitHam = function(comment) {
    return post({
      action: MARK_HAM,
      id: comment.id
    });
  };

  return commentFactory;
});