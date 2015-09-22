/**
 * Angular service to communicate with the Comment Admin Servlet. This service
 * will get all comments, delete comments, mark comments as spam, and mark
 * comments as ham (a valid comment).
 */
app.factory('CommentService', function($http, formDataObject) {
  var commentFactory = {},
      PATH = '/bin/admin/comment',
      DELETE_COMMENT = 'delete_comment',
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

  return commentFactory;
});