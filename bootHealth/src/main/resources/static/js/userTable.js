/**
 * ユーザー一覧ビューで使用するスクリプト.
 * 
 * このスクリプト単独では機能しない。 共通スクリプトの common/js/dtTable.js もロードする必要がある。 dtTable.js
 * で使用する列定義（zDtColumns）とページ取得URL（zDtPageUrl）を設定する。
 */

var zDtColumns; // テーブルの列定義
var zDtPageUrl; // テーブルのページ取得URL

jQuery(function($) {
  // ページ情報を取得する為のリクエストURLを設定
  zDtPageUrl = "userPage";
  // データテーブルの列定義を設定
  zDtColumns = [
      {
        "data" : "name",
        "width" : 100
      },
      {
        "data" : "simei",
        "width" : 80
      },
      {
        "data" : "yomi",
        "width" : 80
      },
      {
        "data" : "roles",
        "width" : 160
      },
      {
        "data" : "id",
        "class" : "center",
        "width" : 160,
        "orderable" : false,
        "createdCell" : function(td, cellData, rowData, row, col) {
          // %%%%%% 削除ボタン %%%%%%
          bd = $(fGlyphicon('trash'));
          bd.on('click', function() {
            zDtRowSelect = true;
            // ajax で カレント行のデータを削除して成功時はメッセージを表示
            $.getJSON("deleteUser/" + cellData, {}, function(msg) {
              $('#msg').html(msg);
              zDtTable.draw();
            });
          });
          // %%%%%% 編集ボタン %%%%%%
          be = $(fGlyphicon('pencil'));
          be.on('click', function() {
            zDtRowSelect = true;
            // ajax で get して成功時はダイアログを表示
            $.getJSON("editUser/" + cellData, {}, function(user) {
              showModal("選択したユーザーを修正してください", user.id, user.version, user.name,
                  user.simei, user.yomi, user.roles);
            });
          });
          $(td).empty();
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
  // 追加ボタンが押されたらモーダルを
  // 新規モードで表示
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $("#btn-create").click(function() {
    // oTable.search('next').draw();
    showModal("新規ユーザーを追加してください。", "", 0, "", "", "", "");
  });

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルを表示する
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function showModal(title, id, version, name, sei, mei, roles) {
    var $form = $('#modalForm');
    $form.find('span').remove();
    $("#myModalLabel").html(title);
    setUser(id, version, name, sei, mei, roles);
    $('#myModal').modal('show');
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダル内のフォームに渡された値を設定する
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function setUser(id, version, name, simei, yomi, roles) {
    $("#id").val(id);
    $("#version").val(version);
    $("#name").val(name);
    $("#simei").val(simei);
    $("#yomi").val(yomi);
    $("#ROLE_ADMIN").prop('checked', false);
    $("#ROLE_USER").prop('checked', false);
    roleArray = roles.split(",");
    $.each(roleArray, function(index, value) {
      $("#" + value).prop('checked', true);
    });
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルの保存ボタン押下時の処理
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $('#btnSave').click(function() {
    // 多重送信を防ぐため通信完了までボタンをdisableにする
    var button = $(this);
    button.attr("disabled", true);
    $('#modalForm').find('span').remove();
    var user = {
      "id" : $('#id').val(),
      "version" : $('#version').val(),
      "name" : $('#name').val(),
      "pass" : $('#pass').val(),
      "simei" : $('#simei').val(),
      "yomi" : $('#yomi').val(),
      "roles" : getRoles()
    };
    $.ajax({
      method : 'post',
      contentType : 'application/json;charset=utf-8',
      data : JSON.stringify(user),
      url : 'saveUser',
      dataType : 'json',
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
  // roles チェックボックスからロールのカンマ区切り文字列を取得
  function getRoles() {
    return $('#roles').find('input:checkbox:checked').map(function() {
      return $(this).val();
    }).get().join(",");
  }
});
