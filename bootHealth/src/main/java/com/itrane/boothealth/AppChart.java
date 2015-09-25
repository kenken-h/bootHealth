package com.itrane.boothealth;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * チャートの設定。
 * <p>
 * ap_chart.properties の設定値で、チャートの種類や出力項目を設定する。
 * </p>
 */
@Component
@PropertySource("ap_chart.properties")
@ConfigurationProperties(prefix = "health")
public class AppChart {

    private Map<String, String> chartNameMap;

    private Map<String, String> chartHeaderMap;

    /**
     * チャート数の取得。
     * 
     * @return チャートの種類の数を返す
     */
    public int numberObChart() {
        return chartNameMap.size();
    }

    /**
     * 指定した種類のチャート名を返す。
     * 
     * @param type
     *            チャートの種類
     * @return
     */
    public String chartName(String type) {
        return chartNameMap.get(type);
    }

    /**
     * 指定したタイプのチャートのヘッダを返す。
     * 
     * @param type
     *            チャートの種類
     * @return ヘッダ項目の配列
     */
    public String[] charHeader(String type) {
        return chartHeaderMap.get(type).split(",");
    }

    public Map<String, String> getChartNameMap() {
        return chartNameMap;
    }

    public void setChartNameMap(Map<String, String> chartNameMap) {
        this.chartNameMap = chartNameMap;
    }

    public Map<String, String> getChartHeaderMap() {
        return chartHeaderMap;
    }

    public void setChartHeaderMap(Map<String, String> chartHeaderMap) {
        this.chartHeaderMap = chartHeaderMap;
    }
}
