/**
 * バイタル/血液検査マスター一覧ビューで使用するスクリプト.
 * 
 * このスクリプト単独では機能しない。 共通スクリプトの common/js/dtTable.js もロードする必要がある。 dtTable.js
 * で使用する列定義（zDtColumns）とページ取得URL（zDtPageUrl）を設定する。
 */

var zDtColumns; // テーブルの列定義
var zDtPageUrl; // テーブルのページ取得URL

jQuery(function($) {
  // ページ情報を取得する為のリクエストURLを設定する
  zDtPageUrl = oStat.pageUrl;
  // データテーブルの列定義を設定する
  zDtColumns = [
      {
        "data" : "junjo",
        "width" : 40,
        "class" : "alignRight"
      },
      {
        "data" : "name",
        "width" : 140,
        "orderable" : false
      },
      {
        "data" : "type",
        "width" : 60,
        "class" : "alignCenter"
      },
      {
        "data" : "jikan",
        "width" : 60,
        "class" : "alignCenter"
      },
      {
        "data" : "kijunMin",
        "width" : 60,
        "class" : "alignRight",
        "orderable" : false,
        "render" : function(data, type, full, meta) {
          return Number(data).toFixed(2);
        }
      },
      {
        "data" : "kijunMax",
        "width" : 60,
        "class" : "alignRight",
        "orderable" : false,
        "render" : function(data, type, full, meta) {
          return Number(data).toFixed(2);
        }
      },
      {
        "data" : "id",
        "class" : "center",
        "width" : 100,
        "orderable" : false,
        "createdCell" : function(td, cellData, rowData, row, col) {
          // %%%%%% 削除ボタン %%%%%%
          bd = $(fGlyphicon('trash'));
          bd.on('click', function() {
            $('#errMsg').html('');
            zDtRowSelect = true;
            // ajax で カレント行のデータを削除して成功時はメッセージを表示
            $.getJSON("deleteVM/" + cellData, {}, function(msg) {
              $('#errMsg').html(msg);
              zDtTable.draw();
            });
          });
          // %%%%%% 編集ボタン %%%%%%
          be = $(fGlyphicon('pencil'));
          be.on('click', function() {
            zDtRowSelect = true;
            // ajax で get して成功時はダイアログを表示
            $.getJSON("editVM/" + cellData + ".html", {}, function(vm) {
              showModal("バイタルマスターを修正してください", vm.id, vm.version, vm.name,
                  vm.type, vm.junjo, vm.jikan, vm.kijunMin, vm.kijunMax);
            });
          });
          $(td).empty();
          $(td).prepend(bd);
          $(td).prepend(be);
        }
      } ]; // dataTables

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
    //alert(JSON.stringify(oStat))
    // oTable.search('next').draw();
    showModal("新規測定バイタルを追加してください。", "", 0, "", "", "", "", "", "");
  });

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルを表示する
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function showModal(title, id, version, name, type, junjo, jikan, kijunMin,
      kijunMax) {
    $('#errMsg').html('');
    var $form = $('#modalForm');
    $form.find('span').remove();
    $("#myModalLabel").html(title);
    setVitalMst(id, version, name, type, junjo, jikan, kijunMin, kijunMax);
    $('#myModal').modal('show');
  }

  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // 渡されたマスターの各項目の値を
  // モーダルの入力フィールドに設定
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  function setVitalMst(id, version, name, type, junjo, jikan, kijunMin,
      kijunMax) {
    $("#id").val(id);
    $("#version").val(version);
    $("#name").val(name);
    $("#type").val(type);
    $("#junjo").val(junjo);
    $("#jikan").val(jikan);
    $("#kijunMin").val(kijunMin);
    $("#kijunMax").val(kijunMax);
  }
  
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  // モーダルの保存ボタン押下時の処理
  // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&
  $('#btnSave').click(function() {
    $('#modalForm').find('span').remove();
    // 多重送信を防ぐため通信完了までボタンをdisableにする
    var vJunjo = $('#junjo').val();
    var vKijunMin = $('#kijunMin').val();
    var vKijunMax = $('#kijunMax').val();
    var vErrs = [];
    var eMsg = "";
    if (vJunjo == '' || vJunjo < 0)
      vErrs.push('junjo');
    if (vKijunMin == '' || vKijunMin < 0)
      vErrs.push('kijunMin');
    if (vKijunMax == '' || vKijunMax < 0)
      vErrs.push('kijunMax');
    if (vKijunMax < vKijunMin)
      eMsg = "基準値最大は基準値最小より大きくしてください";
    if (vErrs.length == 0 && eMsg == "") {
      var button = $(this);
      button.attr("disabled", true);
      var vm = {
        "id" : $('#id').val(),
        "version" : $('#version').val(),
        "name" : $('#name').val(),
        "type" : $('#type').val(),
        "junjo" : vJunjo,
        "jikan" : $('#jikan').val(),
        "kijunMin" : vKijunMin,
        "kijunMax" : vKijunMax,
      }
      // 通信実行
      $.ajax({
        type : "post",
        url : "saveVM",
        data : JSON.stringify(vm), // JSONデータ本体
        contentType : 'application/json', // リクエストの Content-Type
        dataType : "json", // レスポンスをJSONとしてパースする
      }).done(function(cmd) { // 200 OK時
        $('#myModal').modal('hide');
        if (cmd.status == 1) {
          $('#myModal').modal('hide');
          $('#errMsg').html(cmd.message);
          // zDtTable.page(cmd.page).draw(false);
          zDtTable.draw();
        } else {
          for ( var key in cmd.fldErrors) {
            $('#' + key).after('<span>' + cmd.fldErrors[key] + '</span>')
          }
        }
      }).fail(function() { // HTTPエラー時
        // alert("失敗！");
      }).always(function() { // 成功・失敗に関わらず通信が終了した際の処理
        button.attr("disabled", false); // ボタンを再び enableにする
      });

    } else {
      for (var i = 0; i < vErrs.length; i++) {
        $('#' + vErrs[i]).after('<span>0以上の数値を入力してください</span>');
      }
      $('#errMsg').html(eMsg);
    }

  });
});
