/**
 * 薬マスター一覧ビューで使用するスクリプト.
 * 
 * このスクリプト単独では機能しない。 共通スクリプトの common/js/dtTable.js もロードする必要がある。 dtTable.js
 * で使用する列定義（zDtColumns）とページ取得URL（zDtPageUrl）を設定する。
 */

var zDtColumns; // テーブルの列定義
var zDtPageUrl; // テーブルのページ取得URL

jQuery(function($) {
  // ページ情報を取得する為のリクエストURLを設定
  zDtPageUrl = "kusuriMstPage";

  // データテーブルの列定義を設定
  zDtColumns = [
      {
        "data" : "name",
        "width" : 80
      },
      {
        "data" : "setumei",
        "width" : 200
      },
      {
        "data" : "generic",
        "width" : 80
      },
      {
        "data" : "id",
        "class" : "center",
        "width" : 120,
        "orderable" : false,
        "createdCell" : function(td, cellData, rowData, row, col) {
          // %%%%%% 情報表示ボタン %%%%%%
          bi = $(fGlyphicon('info-sign'));
          bi.on('click', function() {
            zDtRowSelect = true;
            if (zDtTable.cell(row, 2).data() != "") {
              window.location = "dict://" + zDtTable.cell(row, 2).data();
            } else {
              window.location = "dict://" + zDtTable.cell(row, 0).data();
            }
          });
          // %%%%%% 削除ボタン %%%%%%
          bd = $(fGlyphicon('trash'));
          bd.on('click', function() {
            $('#errMsg').html('');
            zDtRowSelect = true;
            // ajax で カレント行のデータを削除して成功時はメッセージを表示
            $.getJSON("deleteKM/" + cellData, {}, function(msg) {
              $('#errMsg').html(msg);
              zDtTable.draw();
            });
          });
          // %%%%%% 編集ボタン %%%%%%
          be = $(fGlyphicon('pencil'));
          be.on('click', function() {
            zDtRowSelect = true;
            // ajax で カレント行のデータを取得して成功時はダイアログを表示
            $.getJSON("editKM/" + cellData, {}, function(km) {
              showModal("薬マスターを修正してください", km.id, km.version, km.name,
                  km.setumei, km.generic);
            });
          });
          $(td).empty();
          $(td).prepend(bi);
          $(td).prepend(bd);
          $(td).prepend(be);
        }
      } ];

  // ##########################
  // Bootstrap Glyphicons を表示
  // ##########################
  function fGlyphicon(s) {
    return '<a href="#" class="btn btn-default btn-sm">'
        + '<span class="glyphicon glyphicon-' + s + '"></span></a>';
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 追加ボタンが押下時の処理
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $("#btn-create").click(function() {
    // zDtTable.search('next').draw();
    showModal("新規の薬を追加してください。", "", 0, "", "", "");
  });

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルを表示する
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function showModal(title, id, version, name, setumei, generic) {
    $('#errMsg').html('');
    var $form = $('#modalForm');
    $form.find('span').remove();
    $("#myModalLabel").html(title);
    setKM(id, version, name, setumei, generic);
    $('#myModal').modal('show');
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 渡されたマスターの各項目の値を
  // モーダルの入力フィールドに設定
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function setKM(id, version, name, setumei, generic) {
    $("#id").val(id);
    $("#version").val(version);
    $("#name").val(name);
    $("#setumei").val(setumei);
    $("#generic").val(generic);
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルの保存ボタン押下時の処理
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $('#btnSave').click(function() {
    // 多重送信を防ぐため通信完了までボタンをdisableにする
    var button = $(this);
    button.attr("disabled", true);
    $('#modalForm').find('span').remove();
    var km = {
      "id" : $('#id').val(),
      "version" : $('#version').val(),
      "name" : $('#name').val(),
      "setumei" : $('#setumei').val(),
      "generic" : $('#generic').val()
    };
    // 通信実行
    $.ajax({
      type : "post",
      url : "saveKM",
      data : JSON.stringify(km), // JSONデータ本体
      contentType : 'application/json', // リクエストの Content-Type
      dataType : "json", // レスポンスをJSONとしてパースする
    }).done(function(cmd) {
      if (cmd.status == 1) {
        $('#myModal').modal('hide');
        $('#msg').html(cmd.message);
        // zDtTable.page(cmd.page).draw(false);
        zDtTable.draw();
      } else {
        for ( var key in cmd.fldErrors) {
          $('#' + key).after('<span>' + cmd.fldErrors[key] + '</span>')
        }
      }
    }).fail(function() { // HTTPエラー時
      alert("失敗！");
    }).always(function() { // 成功・失敗に関わらず通信が終了した際の処理
      button.attr("disabled", false); // ボタンを再び enableにする
    });
  });
});
