/**
 * Handle comment submission. Currently the form is a full postback but will
 * become an AJAX call.
 */
$(function(){
  var $commentForm = $('#commentForm'),
      $newCommentContainer = $('#newCommentContainer'),
      $addCommentContainer = $('#addCommentContainer'),
      $commentListContainer = $('#commentListContainer'),
      $commentPathField = $('#commentPath');

  /**
   * Move the form back to its orginal location and reset the path field to
   * blank as it's no longer a reply post but a top level comment. Hide the add
   * comment link as the form is in the correct place.
   */
  $addCommentContainer.click(function(e){
    e.preventDefault();
    $newCommentContainer.append($commentForm);
    $addCommentContainer.hide();
    $commentPathField.val('');
  });

  /**
   * Move the form under under the comment that the user wants to create a reply
   * for. Show the Add Comment link at the bottom of the list. Populate the form
   * field for comment path with the parent comment as that is the parent node
   * to create the reply to.
   */
  $commentListContainer.on('click', '.comment-reply', function(e){
    var $this = $(this),
        $listItem = $this.parents('li'),
        commentPath = $listItem.data('comment-path');

    e.preventDefault();
    $addCommentContainer.show();
    $listItem.append($commentForm);
    $commentPathField.val(commentPath);
  });

  /**
   * Embed GitHub Gist pages.
   */
  $('.gist').gist();
});