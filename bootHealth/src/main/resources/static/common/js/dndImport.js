/**
 * インポート処理を行うビューで使用する共通のスクリプトファイル.
 * 
 * このスクリプトを使用するビューで、インポートするファイルをドロップする領域の id を 'dropArea' に設定する必要がある。
 * インポート処理を行うサーバーのリクエストURL を設定する必要がある。
 */

var dropArea; // ドロップ領域要素
var importUrl = "importVital"; // インポート処理URL

jQuery(function($) {
  // ナノバーの色を設定
  var options = {
    bg : '#2AF',
    target : document.getElementById('myDivId')
  };
  var nanobar = new Nanobar(options);
  var upFile;

  // Bootstrap プログレスバーに進捗％を設定する
  function setProgress(p) {
    nanobar.go(p);
  }

  // id が 'dropArea' の要素を取得
  dropArea = document.getElementById('dropArea');
  dropArea.addEventListener('dragenter', function(e) {
    e.currentTarget.classList.add('drop');
  });
  dropArea.addEventListener('dragleave', function(e) {
    e.currentTarget.classList.remove('drop');
    dropArea.style.backgroundColor = "#FFFFFF";
  });
  dropArea.addEventListener('dragover', function(e) {
    dropArea.style.backgroundColor = "#BBCCDD";
    e.preventDefault();
    e.stopPropagation();
    e.dataTransfer.dropEffect = 'copy';
  });
  dropArea.addEventListener('drop', function(e) {
    e.preventDefault();
    e.stopPropagation();
    upFile = e.dataTransfer.files[0];
    // ファイルタイプがエクセルの場合だけアップロードを行う
    if (upFile.type.slice(-19) == "spreadsheetml.sheet"
        || upFile.type.slice(-8) == "ms-excel") {
      uploadFile();
    } else {
      $('#info').html("ファイルが不正：エクセルファイルを選択してください");
      $('#info').css('color', '#ff0000')
      dropArea.style.backgroundColor = "#FFFFFF";
    }
  });

  // ####################################
  // Bootstrap プログレスバーに進捗％を設定する
  // ####################################
  function setProgress(p) {
    nanobar.go(p);
  }

  var nanobar = new Nanobar(options);

  // ####################################
  // ファイルをアップロードする
  // ####################################
  function uploadFile() {
    $('#errMsg').html('');
    var fd = new FormData();
    fd.append("file", upFile);
    $.ajax({
      dataType : 'json',
      url : importUrl,
      data : fd,
      type : "POST",
      enctype : 'multipart/form-data',
      processData : false,
      contentType : false,
      xhr : function() {
        var xhr = new window.XMLHttpRequest();
        // Upload progress
        xhr.upload.addEventListener("progress", function(evt) {
          if (evt.lengthComputable) {
            var p = evt.loaded / evt.total;
            setProgress(Math.round(p * 100));
          }
        }, false);
        return xhr;
      },
      success : function(response) {
        dropArea.style.backgroundColor = "#FFFFFF";
        $('#errMsg').html(response);
        // alert(response);
        location.reload(true);
      },
      error : function(response) {
        dropArea.style.backgroundColor = "#FFFFFF";
        //alert(response);
        $('#errMsg').html(response);
      }
    });
  }
});
