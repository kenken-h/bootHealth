/**
 * バイタル/血液検査入力ビューで使用するスクリプト.
 * 
 */
var showid;
jQuery(function($) {
  $('#memo').focus();
  $('#btn-save').click(function() {
    var errs = 0;
    $.each($("form").find("input"), function(i, inp) {
      var inpId = '#' + inp.id;
      var val = inp.value;
      var errId = inpId.split('.');
      // alert("i=" + i + " inpId=" + inpId + " errId="+ errId[0] + " val=" +
      // val);
      if (val == '' || 0 > val) {
        $(errId[0]).html("0以上の数値を入力してください");
        errs++;
      }
    });
    if (errs == 0)
      $('#form-vod').submit();
  });
});
