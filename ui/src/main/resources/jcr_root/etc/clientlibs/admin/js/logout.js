/**
 * Logout of the system
 */
$(function(){
  $('.logout').click(function(e){
    e.preventDefault();
    $.post('j_security_check', {
      j_username : '-',
      j_password : '-',
      j_validate : true
    }).always(function(data){
      if (data.status === 403) {
        window.location = '/admin/login.html'
      }
    });
  });
});