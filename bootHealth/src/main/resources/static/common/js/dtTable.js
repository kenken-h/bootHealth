/**
 * dataTable を使用するビューが使用する共通のスクリプト・ファイル.
 * 
 * 各ビューはこのスクリプト・ファイルとそれぞれのビューのテーブルに固有の列定義と ページ取得URLを設定する xxxTable.js
 * をロードする必要がある。
 */

// グローバル変数
var zDtColumns; // テーブルの列定義
var zDtPageUrl; // テーブルのページ取得URL

jQuery(function($) {
  zDtRowSelect = false;

  // %%%%% データテーブルを定義 %%%%%
  zDtTable = $("#dtTable").dataTable({
    "processing" : true, // 処理中の表示
    "serverSide" : true, // サーバーサイド処理
    "lengthChange" : true, // ページ表示行数を変更
    "lengthMenu" : [ [ 10, 20, ], [ 10, 20 ] ], // ページ表示行数の項目設定
    "ajax" : {
      "url" : zDtPageUrl,
      "contentType" : "application/json",
      "data" : function(d) {
        var rtn = $.extend({}, d, {
          "extra_search" : "ABC"
        });
        rtn = JSON.stringify(rtn);
        // alert(rtn);
        return rtn;
      }
    },
    "columns" : zDtColumns,
    "language" : {
      "lengthMenu" : "表示 _MENU_",
      "emptyTable" : "データはありません！",
      "zeroRecords" : "データはありません！",
      "search" : "検索:",
      "paginate" : {
        "first" : "先頭 ",
        "last" : " 最後",
        "next" : " 次 ",
        "previous" : " 前"
      },
      "processing" : "処理中..",
      "info" : "_START_件 ～ _END_件 / _TOTAL_件"
    },
    "pagingType" : "full_numbers" // ページングボタンの表示タイプ
  }).api(); // dataTables

  zDtTable.on('search', function() {
    // alert(JSON.stringify(zDtTable));
    // alert('search');
  })

  // ########################
  // 行の選択/非選択をトグルする
  // ########################
  $('#dtTable tbody').on('click', 'tr', function() {
    if (zDtRowSelect == false) {
      $(this).toggleClass('selected');
    } else {
      zDtRowSelect = false;
    }
  });
});
