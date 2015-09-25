/**
 * ユーザー情報入力ビューで使用するスクリプト.
 * 
 */

var gtransit;
var callPhone;

jQuery(function($) {
  $('#name').focus();
  $(oStat.pageTitle).addClass("active");
  $('a[href="' + oStat.pageTitle + '"]').tab('show');

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 保存ボタン：FORM編集データの保存
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $("#btnSave").click(function() {
    $("#user-form").submit();
  });

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 取消ボタン：ページをリフレッシュ
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $("#btnCancel").click(function() {
    // location.reload(true);
    //alert(JSON.stringify(oUser));
  })

  // ###############################
  // 電話をかける（050plusを使用する例）
  // ###############################
  callPhone = function(obj) {
    if ($('#' + obj.id).val() != '') {
      // window.location = "tel:" + $(id).val();
      window.location = "com050voip://keypad?" + $('input#' + obj.id).val();
    }
  }
  
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 検索ボタン押下時：グーグル検索
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $("a#btnSearch").click(function(e) {
    e.preventDefault();
    var $btn = $(this);
    var btnName = $btn.attr("name");
    var searchStr = $.trim($('input#' + btnName).val());
    if (searchStr == '')
      return;
    window.open("http://www.google.com:80/search?q=" + searchStr);
  });
  
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // ルートボタン押下時：グーグル乗り換え案内
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $('a#btnTransit').click(
      function(e) {
        e.preventDefault();
        var $btn = $(this);
        var url = $btn.attr("href");
        var btnName = $btn.attr("name");
        var fromAddr = oUser.toDoFuKen + oUser.siKuTyoSon + oUser.banTi;
        // alert("form=" + fromAddr);
        var toAddr = $.trim($('input#' + btnName).val());
        // alert("url=" + url + " name=" + btnName + " to=" + toAddr);
        if (fromAddr == '' || toAddr == '')
          return;
        // 多重送信を防ぐため通信完了までボタンをdisableにする
        $btn.attr("disabled", true);
        var user = {
          "byoin1Jusyo" : fromAddr,
          "byoin2Jusyo" : toAddr
        };
        // 通信実行
        $.ajax({
          async : false, // 別ウインドウで開くため
          type : "post",
          url : url,
          data : JSON.stringify(user), // JSONデータ本体
          contentType : 'application/json', // リクエストの Content-Type
          dataType : "json", // レスポンスをJSONとしてパースする
        }).done(function(cmd) { // 200 OK時
          window.open(cmd.message);
        }).fail(
            function(XMLHttpRequest, textStatus, errorThrown) {
              alert("XMLHttpRequest : " + XMLHttpRequest.status
                  + "\ntextStatus : " + textStatus + "\nerrorThrown : "
                  + errorThrown.message);
            }).always(function() { // 成功・失敗に関わらず通信が終了した際の処理
          $btn.attr("disabled", false); // ボタンを再び enableにする
        });
      });
  
  // ------------------------------
  // 生年月日入力欄でデートピッカーを使用
  // 日本語化、日付書式を設定
  // ------------------------------
  $('#seinenGappi').datepicker({
    format : 'yyyy/mm/dd',
    language : 'ja', // カレンダー日本語化のため
    autoclose : true
  });
  
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // タブ切り替えイベント
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    var tabs = ("" + e.target).split("#");
    var cmd = {
      "pageTitle" : "#" + tabs[1]
    };
    // 通信実行
    $.ajax({
      async : true,
      type : "post",
      url : "setTabName",
      data : JSON.stringify(cmd), // JSONデータ本体
      contentType : 'application/json', // リクエストの Content-Type
      dataType : "json", // レスポンスをJSONとしてパースする
    }).done(function(cmd) { // 200 OK時
    });
  });
});
