/**
 * Googleチャートを使用してチャートを表示するビューで使用する共通のスクリプト.
 * 
 */

google.load("visualization", "1.1", {
  packages : [ "corechart" ]
});

$(document).ready(
    function() {
      $("#vodButton").click(function() {
        $('#btnVod').tab('show')
      });
      $("#bsButton").click(showBsChart);
      $("#bpButton").click(showBpChart);
      $("#btButton").click(function() {
        // showLineChart("showBtChart", "体温", 8, 37, 35.5, 38.5);
        showLineChart("showVitalChart?type=Bt", "体温", 8, 37, 35.5, 38.5);
      });
      $("#wtButton").click(showWtChart);

      $("#gotButton").click(
          function() {
            showLineChart("showBloodChart?type=GotGpt", "GOT(0-35)/GPT(0-50) ",
                20, 50, 0, 600);
          });
      $("#ggtpButton").click(
          function() {
            showLineChart("showBloodChart?type=GGtp", "γ-GTP(0-50) ", 25, 50,
                0, 2300);
          });
      $("#alpButton").click(
          function() {
            showLineChart("showBloodChart?type=Alp", "ALP(100-340) ", 20, 340,
                100, 700);
          });
      $("#ldhButton").click(
          function() {
            showLineChart("showBloodChart?type=Ldh", "LDH(120-240) ", 20, 240,
                120, 800);
          });
      $("#tbillButton").click(
          function() {
            showLineChart("showBloodChart?type=TBill", "Tbill(0.2-1.2) ", 25,
                1.2, 0.2, 10);
          });
      $("#albButton").click(
          function() {
            showLineChart("showBloodChart?type=Alb", "Alb(3.8-5.3) ", 15, 3.8,
                0, 6);
          });
      $("#creButton").click(
          function() {
            showLineChart("showBloodChart?type=Cre", "CRE(0.6-1.2) ", 15, 1.2,
                0.6, 2);
          });
    });

// #######################
// 血糖値 のチャートを表示
// #######################
function showBsChart() {
  $.post("showVitalChart?type=Bs", // URL
  {}, // User データ
  function(result) { // 成功時のコールバック
    $('#btnVChart').tab('show')
    for (var row = 1; row < result.length; row++) {
      result[row][0] = new Date(Date.parse(result[row][0]));
    }
    var data = google.visualization.arrayToDataTable(result);
    var options = {
      hAxis : {
        format : "yy/M/d",
        title : "日付"
      },
      seriesType : "bars",
      series : {
        4 : {
          type : "line"
        }
      },
      width : 1080,
      height : 450,
    };
    var chart = new google.visualization.ComboChart(document
        .getElementById('chart_div'));
    chart.draw(data, options);
  });
}

// ###################
// 血圧 のチャートを表示
// ###################
function showBpChart() {
  $.post("showVitalChart?type=Bp", // URL
  {}, // User データ
  function(result) { // 成功時のコールバック
    $('#btnVChart').tab('show')
    for (var row = 1; row < result.length; row++) {
      result[row][0] = new Date(Date.parse(result[row][0]));
    }
    var data = google.visualization.arrayToDataTable(result);
    var options = {
      hAxis : {
        format : "yy/M/d",
        title : "日付"
      },
      width : 1080,
      height : 450,
    };
    var chart = new google.visualization.ColumnChart(document
        .getElementById('chart_div'));
    chart.draw(data, options);
  });
}

// ###################
// 体重 のチャートを表示
// ###################
function showWtChart() {
  $.post("showVitalChart?type=Wt", // URL
  {}, // User データ
  function(result) { // 成功時のコールバック
    $('#btnVChart').tab('show')
    for (var row = 1; row < result.length; row++) {
      result[row][0] = new Date(Date.parse(result[row][0]));
    }
    var data = google.visualization.arrayToDataTable(result);
    var options = {
      vAxis : {
        gridlines : {
          color : '#ccf'
        },
      },
      baseline : 60,
      viewWindow : {
        min : 56,
        max : 66
      },
      hAxis : {
        format : "yy/M/d",
        title : "日付"
      },
      width : 1080,
      height : 450,
    };
    var chart = new google.visualization.ColumnChart(document
        .getElementById('chart_div'));
    chart.draw(data, options);
  });
}

// ###################
// ライン・チャート表示
// ###################
function showLineChart(cUrl, cTitle, cCount, cBase, cMin, cMax) {
  $.post(cUrl, // URL
  {}, // User データ
  function(result) { // 成功時のコールバック
    $('#btnVChart').tab('show')
    for (var row = 1; row < result.length; row++) {
      result[row][0] = new Date(Date.parse(result[row][0]));
    }
    var data = google.visualization.arrayToDataTable(result);
    var options = {
      vAxis : {
        title : cTitle,
        gridlines : {
          color : '#ccf',
          count : cCount
        },
        baseline : cBase,
        viewWindow : {
          min : cMin,
          max : cMax
        },
      },
      hAxis : {
        format : "yy/M/d",
        title : "日付"
      },
      width : 1080,
      height : 450,
    };
    // var chart = new
    // google.visualization.ColumnChart(document.getElementById('chart_div'));
    var chart = new google.visualization.LineChart(document
        .getElementById('chart_div'));
    chart.draw(data, options);
  });
}
