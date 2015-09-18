/**
 * SlingAsset extension to browse and upload assets to the JCR, then to insert
 * selected images into the Summernote HTML5 rich text editor. This pulls in the
 * /libs/publick/components/assetList component.
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
   * createImageNode
   *  
   * @member plugin.slingasset
   * @private
   * @param {String} url
   * @return {Node}
   */
  var createImageNode = function (url) {
    var $img;

    if (url) {
      $img = $('<img>').attr('src', url);
      return $img[0];
    } else {
      return null;
    }
  };

  /**
   * @member plugin.slingasset
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
   * Show Sling Asset dialog and set event handlers on dialog controls.
   *
   * @member plugin.slingasset
   * @private
   * @param {jQuery} $dialog
   * @param {jQuery} $dialog
   * @param {Object} text
   * @return {Promise}
   */
  var showAssetDialog = function ($editable, $dialog, text) {
    return $.Deferred(function (deferred) {
      var $assetDialog = $dialog.find('.note-slingasset-dialog');

      var $assetUrl = $assetDialog.find('.selected-asset'),
          $assetBtn = $assetDialog.find('.note-slingasset-btn');

      $assetDialog.one('shown.bs.modal', function () {
        $assetBtn.click(function (event) {
          event.preventDefault();

          deferred.resolve($assetUrl.val());
          $assetDialog.modal('hide');
        });
      }).one('hidden.bs.modal', function () {
        $assetUrl.off('input');
        $assetBtn.off('click');

        if (deferred.state() === 'pending') {
          deferred.reject();
        }
      }).modal('show');
    });
  };

  /**
   * @class plugin.slingasset
   *
   * SlingAsset Plugin
   *
   * Sling Asset plugin allows the user to browse the JCR
   * and insert an image tag.
   *
   * ### load script
   *
   * ```
   * <script src="plugin/summernote-ext-slingasset.js"></script>
   * ```
   *
   * ### use a plugin in toolbar
   * ```
   *    $("#editor").summernote({
   *    ...
   *    toolbar : [
   *        ['group', [ 'slingasset' ]]
   *    ]
   *    ...    
   *    });
   * ```
   */
  $.summernote.addPlugin({
    /** @property {String} name name of plugin */
    name: 'slingasset',
    /**
     * @property {Object} buttons
     * @property {function(object): string} buttons.slingasset
     */
    buttons: {
      slingasset: function (lang, options) {
        return tmpl.iconButton(options.iconPrefix + 'picture-o', {
          event: 'showAssetDialog',
          title: lang.asset.asset,
          hide: true
        });
      }
    },

    /**
     * @property {Object} dialogs
     * @property {function(object, object): string} dialogs.slingasset
    */
    dialogs: {
      slingasset: function (lang) {
        var body = $.ajax({
                      type: 'GET',
                      url: '/bin/admin/getassetlist',
                      cache: false,
                      async: false
                    }).responseText;

        var footer = '<button href="#" class="btn btn-primary note-slingasset-btn">' + lang.asset.insert + '</button>';
        return tmpl.dialog('note-slingasset-dialog', lang.asset.insert, body, footer);
      }
    },

    /**
     * @property {Object} events
     * @property {Function} events.showAssetDialog
     */
    events: {
      showAssetDialog: function (event, editor, layoutInfo) {
        var $dialog = layoutInfo.dialog(),
            $editable = layoutInfo.editable(),
            text = getTextOnRange($editable),
            $body = $('.asset-controller');

        // save current range
        editor.saveRange($editable);

        showAssetDialog($editable, $dialog, text).then(function (url) {
          // when ok button clicked

          // restore range
          editor.restoreRange($editable);
          
          // build node
          var $node = createImageNode(url);
          
          if ($node) {
            // insert asset node
            editor.insertNode($editable, $node);
          }
        }).fail(function () {
          // when cancel button clicked
          editor.restoreRange($editable);
        });

        if (first) {
          first = false;
          angular.element(document.body).injector().invoke(function($compile) {
            var scope = angular.element($body).scope();
            $compile($body)(scope);
          });
        }
      }
    },

    // define language
    langs: {
      'en-US': {
        asset: {
          asset: 'Asset',
          insert: 'Insert Asset'
        }
      }
    }
  });
}));