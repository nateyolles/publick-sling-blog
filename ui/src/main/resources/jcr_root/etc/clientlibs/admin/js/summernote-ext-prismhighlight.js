/**
 * PrismJS embed extension to the Summernote HTML5 WYSIWYG editor. Creates a
 * PRE & CODE tag with data-properties and CSS classes for the PrismJS script.
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
   * Create the pre/code HTML element
   *
   * @member plugin.prismembed
   * @private
   * @param {String} lang The language
   * @param {Boolean} lineNumbers Show line numbers
   * @param {String} highlight The line numbers to highlight
   * @param {String} content The code to syntax highlight
   * @return {Node}
   */
  var createCodeNode = function (lang, lineNumbers, highlight, content) {
    var $code,
        $pre;

    $pre = $('<pre></pre>');
    $code = $('<code></code>').addClass('prism language-' + lang)
                              .html(content);

    if (highlight) {
      $pre.attr('data-line', highlight);
    }

    if (lineNumbers) {
      $pre.addClass('line-numbers');
    }

    $pre.append($code);

    return $pre[0];
  };

  /**
   * @member plugin.prismembed
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
   * Show Prism embed dialog and set event handlers on dialog controls.
   *
   * @member plugin.prismembed
   * @private
   * @param {jQuery} $dialog
   * @param {jQuery} $dialog
   * @param {Object} text
   * @return {Promise}
   */
  var showPrismDialog = function ($editable, $dialog, text) {
    return $.Deferred(function (deferred) {
      var $prismDialog = $dialog.find('.note-prismembed-dialog');

      var $prismBtn = $prismDialog.find('.note-prismembed-btn'),
          $lang = $prismDialog.find('#prism-lang'),
          $lineNumbers = $prismDialog.find('#prism-show-line-numbers'),
          $highlight = $prismDialog.find('#prism-highlight-line'),
          $content = $prismDialog.find('#prism-content');

      $content.val('');
      $highlight.val('');
      $lineNumbers.prop('checked', true);

      $prismDialog.one('shown.bs.modal', function () {
        $prismBtn.click(function (event) {
          event.preventDefault();

          deferred.resolve($lang.val(), $lineNumbers.prop('checked'), $highlight.val(), $content.val());

          $prismDialog.modal('hide');
        });
      }).one('hidden.bs.modal', function () {
        $prismBtn.off('click');

        if (deferred.state() === 'pending') {
          deferred.reject();
        }
      }).modal('show');
    });
  };

  $.summernote.addPlugin({
    /** @property {String} name name of plugin */
    name: 'prismembed',
    /**
     * @property {Object} buttons
     * @property {function(object): string} buttons.prismembed
     */
    buttons: {
      prismembed: function (lang, options) {
        return tmpl.iconButton(options.iconPrefix + 'code', {
          event: 'showPrismDialog',
          title: lang.prism.prism,
          hide: true
        });
      }
    },

    /**
     * @property {Object} dialogs
     * @property {function(object, object): string} dialogs.prismembed
    */
    dialogs: {
      prismembed: function (lang) {
        var body = $('<form>' +
                        '<div class="form-group">' +
                          '<label for="prism-lang">Language</label>' +
                          '<select class="form-control" id="prism-lang">' +
                            '<option value="markup">HTML</option>' +
                            '<option value="css">CSS</option>' +
                            '<option value="javascript">JavaScript</option>' +
                            '<option value="apacheconf">Apache Configuration</option>' +
                            '<option value="bash">Bash</option>' +
                            '<option value="git">Git</option>' +
                            '<option value="groovy">Groovy</option>' +
                            '<option value="handlebars">Handlebars</option>' +
                            '<option value="http">HTTP</option>' +
                            '<option value="java">Java</option>' +
                            '<option value="less">Less</option>' +
                            '<option value="markup">Markdown</option>' +
                            '<option value="sass">Sass</option>' +
                            '<option value="scss">Scss</option>' +
                            '<option value="scala">Scala</option>' +
                            '<option value="yaml">YAML</option>' +
                          '</select>' +
                        '</div>' +
                        '<div class="checkbox">' +
                          '<label>' +
                            '<input type="checkbox" id="prism-show-line-numbers"> Show line numbers' +
                          '</label>' +
                        '</div>' +
                        '<div class="form-group">' +
                          '<label for="prism-highlight-line">Highlight lines</label>' +
                            '<input type="text" class="form-control" id="prism-highlight-line">' +
                          '<p class="help-block">Lines to highlight (ex. "2", "4-5", "2,4,6-9").</p>' +
                        '</div>' +
                        '<div class="form-group">' +
                          '<label for="prism-content">Content</label>' +
                          '<textarea class="form-control" id="prism-content" rows="5"></textarea>' +
                        '</div>' +
                      '</form>').html();

        var footer = '<button href="#" class="btn btn-primary note-prismembed-btn">' + lang.prism.prism + '</button>';
        return tmpl.dialog('note-prismembed-dialog', lang.prism.prism, body, footer);
      }
    },

    /**
     * @property {Object} events
     * @property {Function} events.showPrismDialog
     */
    events: {
      showPrismDialog: function (event, editor, layoutInfo) {
        var $dialog = layoutInfo.dialog(),
            $editable = layoutInfo.editable(),
            text = getTextOnRange($editable);

        // save current range
        editor.saveRange($editable);

        showPrismDialog($editable, $dialog, text).then(function (lang, lineNumbers, highlight, content) {
          // when ok button clicked

          // restore range
          editor.restoreRange($editable);

          // build node
          var $node = createCodeNode(lang, lineNumbers, highlight, content);

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
        prism: {
          prism: 'Prism',
          insert: 'Insert Prism Code Highlight'
        }
      }
    }
  });
}));