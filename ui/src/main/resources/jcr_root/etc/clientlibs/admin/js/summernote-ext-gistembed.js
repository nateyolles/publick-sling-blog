/**
 * Gist embed extension to the Summernote HTML5 WYSIWYG editor. Creates a CODE
 * tag with data-gist-* properties for the gist-embed jQuery plugin to convert
 * to an embedded Gist. See https://github.com/blairvanderhoof/gist-embed. The
 * Summernote display uses CSS to make sure the code tag is displayed in the
 * editor using the CSS attr() method.
 */
(function (factory) {
  /* global define */
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals: jQuery
    factory(window.jQuery);
  }
}(function($) {
  // template
  var tmpl = $.summernote.renderer.getTemplate();

  // core functions: range, dom
  var range = $.summernote.core.range;
  var dom = $.summernote.core.dom;
  var first = true;

  /**
   * Create the code HTML element
   *
   * @member plugin.gistembed
   * @private
   * @param {String} gistId
   * @param {String} gistFile
   * @param {String} gistLine
   * @param {String} gistHighlightLine
   * @param {String} gistHideLineNumbers
   * @param {String} gistHideFooter
   * @param {String} gistShowLoading
   * @param {String} gistShowSpinner
   * @return {Node}
   */
  var createCodeNode = function (gistId, gistFile, gistLine, gistHighlightLine,
      gistHideLineNumbers, gistHideFooter, gistShowLoading, gistShowSpinner) {
    var $code;

    if (gistId) {
      $code = $('<code>').addClass('gist')
                        .attr('data-gist-id', gistId)
                        .attr('data-gist-file', gistFile)
                        .attr('data-gist-line', gistLine)
                        .attr('data-gist-highlight-line', gistHighlightLine)
                        .attr('data-gist-hide-line-numbers', gistHideLineNumbers)
                        .attr('data-gist-hide-footer', gistHideFooter)
                        .attr('data-gist-show-loading', gistShowLoading)
                        .attr('data-gist-show-spinner', gistShowSpinner);

      return $code[0];
    } else {
      return null;
    }
  };

  /**
   * @member plugin.gistembed
   * @private
   * @param {jQuery} $editable
   * @return {String}
   */
  var getTextOnRange = function ($editable) {
    $editable.focus();

    var rng = range.create();

    // if range on anchor, expand range with anchor
    if (rng.isOnAnchor()) {
      var anchor = dom.ancestor(rng.sc, dom.isAnchor);
      rng = range.createFromNode(anchor);
    }

    return rng.toString();
  };

  /**
   * Show Gist embed dialog and set event handlers on dialog controls.
   *
   * @member plugin.gistembed
   * @private
   * @param {jQuery} $dialog
   * @param {jQuery} $dialog
   * @param {Object} text
   * @return {Promise}
   */
  var showGistDialog = function ($editable, $dialog, text) {
    return $.Deferred(function (deferred) {
      var $gistDialog = $dialog.find('.note-gistembed-dialog');

      var $gistBtn = $gistDialog.find('.note-gistembed-btn'),
          $gistId = $gistDialog.find('#gist-id'),
          $gistFile = $gistDialog.find('#gist-file'),
          $gistLine = $gistDialog.find('#gist-line'),
          $gistHighlightLine = $gistDialog.find('#gist-highlight-line'),
          $gistHideLineNumbers = $gistDialog.find('#gist-hide-line-numbers'),
          $gistHideFooter = $gistDialog.find('#gist-hide-footer'),
          $gistShowLoading = $gistDialog.find('#gist-show-loading'),
          $gistShowSpinner = $gistDialog.find('#gist-show-spinner');

      $gistId.val('');
      $gistFile.val('');
      $gistLine.val('');
      $gistHighlightLine.val('');
      $gistHideLineNumbers.prop('checked', false);
      $gistHideFooter.prop('checked', false);
      $gistShowLoading.prop('checked', true);
      $gistShowSpinner.prop('checked', false);

      $gistDialog.one('shown.bs.modal', function () {
        $gistBtn.click(function (event) {
          event.preventDefault();

          deferred.resolve($gistId.val(), $gistFile.val(), $gistLine.val(), $gistHighlightLine.val(),
            $gistHideLineNumbers.prop('checked'), $gistHideFooter.prop('checked'),
            $gistShowLoading.prop('checked'), $gistShowSpinner.prop('checked'));

          $gistDialog.modal('hide');
        });
      }).one('hidden.bs.modal', function () {
        $gistBtn.off('click');

        if (deferred.state() === 'pending') {
          deferred.reject();
        }
      }).modal('show');
    });
  };

  $.summernote.addPlugin({
    /** @property {String} name name of plugin */
    name: 'gistembed',
    /**
     * @property {Object} buttons
     * @property {function(object): string} buttons.gistembed
     */
    buttons: {
      gistembed: function (lang, options) {
        return tmpl.iconButton(options.iconPrefix + 'github', {
          event: 'showGistDialog',
          title: lang.gist.gist,
          hide: true
        });
      }
    },

    /**
     * @property {Object} dialogs
     * @property {function(object, object): string} dialogs.gistembed
    */
    dialogs: {
      gistembed: function (lang) {
        var body = $('<form>' +
                        '<div class="form-group">' +
                          '<label for="gist-id">Gist ID</label>' +
                          '<input type="text" class="form-control" id="gist-id">' +
                        '</div>' +
                        '<div class="form-group">' +
                          '<label for="gist-file">Load a single file from a Gist</label>' +
                            '<input type="text" class="form-control" id="gist-file">' +
                          '<p class="help-block">(ex. "MyClass.java", "test.json").</p>' +
                        '</div>' +
                        '<div class="form-group">' +
                          '<label for="gist-line">Load single line</label>' +
                            '<input type="text" class="form-control" id="gist-line">' +
                          '<p class="help-block">Single line to load (ex. "2", "5").</p>' +
                        '</div>' +
                        '<div class="form-group">' +
                          '<label for="gist-highlight-line">Highlight lines</label>' +
                            '<input type="text" class="form-control" id="gist-highlight-line">' +
                          '<p class="help-block">Lines to highlight (ex. "2", "4-5", "2,4,6-9").</p>' +
                        '</div>' +
                        '<div class="checkbox">' +
                          '<label>' +
                            '<input type="checkbox" id="gist-hide-line-numbers"> Hide line numbers' +
                          '</label>' +
                        '</div>' +
                        '<div class="checkbox">' +
                          '<label>' +
                            '<input type="checkbox" id="gist-hide-footer"> Hide footer' +
                          '</label>' +
                        '</div>' +
                        '<div class="checkbox">' +
                          '<label>' +
                            '<input type="checkbox" id="gist-show-loading"> Show loading text' +
                          '</label>' +
                        '</div>' +
                        '<div class="checkbox">' +
                          '<label>' +
                            '<input type="checkbox" id="gist-show-spinner"> Show spinner' +
                          '</label>' +
                        '</div>' +
                      '</form>').html();

        var footer = '<button href="#" class="btn btn-primary note-gistembed-btn">' + lang.gist.gist + '</button>';
        return tmpl.dialog('note-gistembed-dialog', lang.gist.gist, body, footer);
      }
    },

    /**
     * @property {Object} events
     * @property {Function} events.showGistDialog
     */
    events: {
      showGistDialog: function (event, editor, layoutInfo) {
        var $dialog = layoutInfo.dialog(),
            $editable = layoutInfo.editable(),
            text = getTextOnRange($editable);

        // save current range
        editor.saveRange($editable);

        showGistDialog($editable, $dialog, text).then(function (gistId, gistFile, gistLine, gistHighlightLine,
            gistHideLineNumbers, gistHideFooter, gistShowLoading, gistShowSpinner) {
          // when ok button clicked

          // restore range
          editor.restoreRange($editable);

          // build node
          var $node = createCodeNode(gistId, gistFile, gistLine, gistHighlightLine, gistHideLineNumbers,
              gistHideFooter, gistShowLoading, gistShowSpinner);

          if ($node) {
            // insert the generated HTML
            editor.insertNode($editable, $node);
          }
        }).fail(function () {
          // when cancel button clicked
          editor.restoreRange($editable);
        });
      }
    },

    // define language
    langs: {
      'en-US': {
        gist: {
          gist: 'Gist',
          insert: 'Insert Gist'
        }
      }
    }
  });
}));