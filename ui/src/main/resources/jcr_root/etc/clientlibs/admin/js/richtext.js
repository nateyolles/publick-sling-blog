/**
 * Initialize the Summernote WYSIWYG HTML5/Bootstrap editor. This depends on the
 * custom slingasset summernote plugin.
 */
$(function(){
  $('.blog-edit-content').summernote({
    height: 300,
    toolbar : [
      ['group', ['undo', 'redo']],
      ['style', ['bold', 'italic', 'underline', 'clear']],
      ['font', ['strikethrough', 'superscript', 'subscript']],
      ['fontsize', ['fontsize']],
      ['color', ['color']],
      ['para', ['ul', 'ol', 'paragraph']],
      ['insert', ['slingasset', 'link', 'table', 'hr']],
      ['misc', ['fullscreen', 'codeview']],
      ['group', ['help']]
    ]
  });
});