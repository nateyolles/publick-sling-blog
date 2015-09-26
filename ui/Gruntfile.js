/*global module:false*/
module.exports = function(grunt) {
  'use strict';
  require('time-grunt')(grunt);
  require('load-grunt-tasks')(grunt);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
      '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
      '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
      '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
      ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
    conf: {
      name: 'publick-sling-blog',
      dist: 'src/main/resources/jcr_root/etc/clientlibs/admin',
      src: 'src/main/resources/jcr_root/etc/clientlibs/admin'
    },
    concat: {
      'js': {
        src: [
          '<%=conf.dist%>/js/summernote-ext-slingasset.js',
          '<%=conf.dist%>/js/logout.js',
          '<%=conf.dist%>/js/richtext.js',
          '<%=conf.dist%>/js/app.js',
          '<%=conf.dist%>/js/keywordsController.js',
          '<%=conf.dist%>/js/commentController.js',
          '<%=conf.dist%>/js/assetsController.js',
          '<%=conf.dist%>/js/settingsController.js',
          '<%=conf.dist%>/js/userController.js',
          '<%=conf.dist%>/js/userModalController.js',
          '<%=conf.dist%>/js/formDataObjectFactory.js',
          '<%=conf.dist%>/js/userService.js',
          '<%=conf.dist%>/js/settingsService.js',
          '<%=conf.dist%>/js/commentService.js',
          '<%=conf.dist%>/js/commentModalController.js',
          '<%=conf.dist%>/js/backupController.js',
          '<%=conf.dist%>/js/backupService.js'
        ],
        dest: '<%=conf.src%>/js/admin.js'
      }
    }
  });

  grunt.registerTask('build', [
    'concat:js'
  ]);
};